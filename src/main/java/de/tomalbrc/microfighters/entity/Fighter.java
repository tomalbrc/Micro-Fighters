package de.tomalbrc.microfighters.entity;

import com.google.common.collect.ImmutableList;
import com.mojang.authlib.GameProfile;
import de.tomalbrc.microfighters.MicroFighters;
import de.tomalbrc.microfighters.Util;
import eu.pb4.polymer.core.api.entity.PolymerEntity;
import eu.pb4.polymer.core.api.entity.PolymerEntityUtils;
import eu.pb4.polymer.core.mixin.entity.EntityAttributesS2CPacketAccessor;
import eu.pb4.polymer.virtualentity.api.VirtualEntityUtils;
import it.unimi.dsi.fastutil.ints.IntList;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ItemParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import xyz.nucleoid.packettweaker.PacketContext;

import java.util.*;
import java.util.function.Consumer;

public class Fighter extends PathfinderMob implements PolymerEntity {
    public static final ResourceLocation ID = ResourceLocation.fromNamespaceAndPath(MicroFighters.MOD_ID, "fighter");
    public static final String DROP = "drop";
    public static final String COLOR = "color";

    private DyeColor color = DyeColor.GRAY;

    private Item item;

    private int nametagHidingPassengerId = -1;

    @NotNull
    public static AttributeSupplier.Builder createAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 8.0)
                .add(Attributes.FOLLOW_RANGE, 10.0)
                .add(Attributes.MOVEMENT_SPEED, 0.2)
                .add(Attributes.ATTACK_DAMAGE, 2.0)
                .add(Attributes.ARMOR, 1.0)
                .add(Attributes.KNOCKBACK_RESISTANCE, 0.2);
    }

    public Fighter(EntityType<? extends Fighter> entityEntityType, Level world) {
        super(entityEntityType, world);
        this.setPersistenceRequired();
        this.setCanPickUpLoot(true);
        this.setInvisible(true);
        this.setSilent(true);
        Arrays.fill(this.armorDropChances, 1);
        Arrays.fill(this.handDropChances, 1);
        this.bodyArmorDropChance = 1;
    }

    public void setItem(Item item) {
        this.item = item;
    }

    @Override
    protected void populateDefaultEquipmentSlots(RandomSource randomSource, DifficultyInstance difficultyInstance) {
    }

    @Override
    public boolean wantsToPickUp(ItemStack itemStack) {
        return this.canHoldItem(itemStack);
    }

    @Override
    protected SoundEvent getAmbientSound() {
        return SoundEvents.EMPTY;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.EMPTY;
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource damageSource) {
        return SoundEvents.EMPTY;
    }

    @Override
    public boolean hurt(DamageSource damageSource, float f) {
        this.level().playSound(null, this.getX(), this.getY(), this.getZ(), SoundEvents.GRAVEL_BREAK, this.getSoundSource(), .5f, 0.9f);

        if (damageSource.is(DamageTypeTags.IS_PLAYER_ATTACK)) {
            this.kill();
            return false;
        } else {
            return super.hurt(damageSource, f);
        }
    }

    @Override
    public void aiStep() {
        if (this.isDeadOrDying() && this.level() instanceof ServerLevel serverLevel) {
            if (this.item != null) this.spawnAtLocation(this.item);
            serverLevel.sendParticles(new ItemParticleOption(ParticleTypes.ITEM, particleItem(this.color).getDefaultInstance()), this.getX(), this.getY(), this.getZ(), 20, 0.125, 0.125, 0.125, 0.05);
            this.dropCustomDeathLoot(serverLevel, this.damageSources().genericKill(), true);
            this.discard();
            return;

        }
        super.aiStep();

    }

    @Override
    public void tick() {
        super.tick();

        if (this.isOnFire() && this.level() instanceof ServerLevel serverLevel) {
            serverLevel.sendParticles(ParticleTypes.FLAME, this.getX(), this.getY(), this.getZ(), 1, 0, 0, 0, 0);
        }
    }

    @Override
    protected float getEquipmentDropChance(EquipmentSlot equipmentSlot) {
        return 1;
    }

    public void setColor(DyeColor color) {
        this.color = color;
    }

    @Override
    public boolean canBeLeashed() {
        return false;
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(1, new FloatGoal(this));
        this.goalSelector.addGoal(4, new MeleeAttackGoal(this, 1.0, true));
        this.goalSelector.addGoal(5, new WaterAvoidingRandomStrollGoal(this, 1.0));
        this.goalSelector.addGoal(6, new RandomLookAroundGoal(this));

        this.targetSelector.addGoal(1, (new HurtByTargetGoal(this)).setAlertOthers(Fighter.class));
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Fighter.class, true));
    }

    @Override
    public boolean doHurtTarget(Entity entity) {
        boolean doHurt = super.doHurtTarget(entity);
        ItemStack weapon = this.getWeaponItem();
        if (weapon.is(Items.BLAZE_ROD)) {
            entity.setRemainingFireTicks(5*20);
        }
        return doHurt;
    }

    @Override
    public boolean isAlliedTo(Entity mob) {
        return mob instanceof Fighter fighter && fighter.color == this.color;
    }

    @Override
    public void push(double d, double e, double f) {
        super.push(2 * d, 2 * e, 2 * f);
    }

    @Override
    public boolean canHoldItem(ItemStack itemStack) {
        return itemStack.is(Items.STICK) ||
                itemStack.is(Items.BLAZE_ROD) ||
                itemStack.is(Items.SHIELD) ||
                itemStack.is(Items.LEATHER_BOOTS) ||
                itemStack.is(Items.LEATHER_CHESTPLATE) ||
                itemStack.is(Items.LEATHER_HELMET) ||
                itemStack.is(Items.LEATHER_LEGGINGS);
    }

    @Override
    public EntityType<?> getPolymerEntityType(ServerPlayer player) {
        return EntityType.PLAYER;
    }

    @Override
    public float getBlockExplosionResistance(Explosion explosion, BlockGetter world, BlockPos pos, BlockState blockState, FluidState fluidState, float max) {
        return blockState.isAir() || blockState.getBlock() instanceof LiquidBlock ? 0 : blockState.getBlock().getExplosionResistance();
    }

    @Override
    public void modifyRawEntityAttributeData(List<ClientboundUpdateAttributesPacket.AttributeSnapshot> data, PacketContext context, boolean initial) {
        data.add(new ClientboundUpdateAttributesPacket.AttributeSnapshot(Attributes.SCALE, MicroFighters.SCALE, ImmutableList.of()));
    }

    @Override
    public void onBeforeSpawnPacket(ServerPlayer player, Consumer<Packet<?>> packetConsumer) {
        var packet = PolymerEntityUtils.createMutablePlayerListPacket(EnumSet.of(ClientboundPlayerInfoUpdatePacket.Action.ADD_PLAYER, ClientboundPlayerInfoUpdatePacket.Action.UPDATE_LISTED));
        var gameProfile = new GameProfile(this.getUUID(), "Fighter");
        Util.modifyProfileForColor(this.color, gameProfile);
        packet.entries().add(new ClientboundPlayerInfoUpdatePacket.Entry(this.getUUID(), gameProfile, false, 0, GameType.ADVENTURE, Component.empty(), null));
        packetConsumer.accept(packet);
    }

    @Override
    public void startSeenByPlayer(ServerPlayer player) {
        super.startSeenByPlayer(player);

        var attributesPacket = new ClientboundUpdateAttributesPacket(this.getId(), List.of());
        var attributeSnapshots = List.of(new ClientboundUpdateAttributesPacket.AttributeSnapshot(Attributes.SCALE, MicroFighters.SCALE, ImmutableList.of()));
        ((EntityAttributesS2CPacketAccessor)attributesPacket).getEntries().addAll(attributeSnapshots);
        player.connection.send(attributesPacket);

        this.nametagHidingPassengerId = VirtualEntityUtils.requestEntityId();
        var entityPacket = new ClientboundAddEntityPacket(this.nametagHidingPassengerId, UUID.randomUUID(), player.getX(), player.getY(), player.getZ(), 0, 0, EntityType.BLOCK_DISPLAY, 0, Vec3.ZERO, 0.0);
        player.connection.send(entityPacket);
        player.connection.send(VirtualEntityUtils.createRidePacket(this.getId(), IntList.of(this.nametagHidingPassengerId)));
    }

    @Override
    public void stopSeenByPlayer(ServerPlayer player) {
        if (nametagHidingPassengerId != -1) player.connection.send(new ClientboundRemoveEntitiesPacket(this.nametagHidingPassengerId));
        player.connection.send(new ClientboundPlayerInfoRemovePacket(List.of(this.getUUID())));
        super.stopSeenByPlayer(player);
    }

    @NotNull
    public static Item particleItem(DyeColor color) {
        return switch (color) {
            case WHITE -> Items.WHITE_TERRACOTTA;
            case ORANGE -> Items.ORANGE_TERRACOTTA;
            case MAGENTA -> Items.MAGENTA_TERRACOTTA;
            case LIGHT_BLUE -> Items.LIGHT_BLUE_TERRACOTTA;
            case YELLOW -> Items.YELLOW_TERRACOTTA;
            case LIME -> Items.LIME_TERRACOTTA;
            case PINK -> Items.PINK_TERRACOTTA;
            case GRAY -> Items.GRAY_TERRACOTTA;
            case LIGHT_GRAY -> Items.LIGHT_GRAY_TERRACOTTA;
            case CYAN -> Items.CYAN_TERRACOTTA;
            case PURPLE -> Items.PURPLE_TERRACOTTA;
            case BLUE -> Items.BLUE_TERRACOTTA;
            case BROWN -> Items.BROWN_TERRACOTTA;
            case GREEN -> Items.GREEN_TERRACOTTA;
            case RED -> Items.RED_TERRACOTTA;
            case BLACK -> Items.BLACK_TERRACOTTA;
        };
    }

    @Override
    public void addAdditionalSaveData(CompoundTag compoundTag) {
        super.addAdditionalSaveData(compoundTag);
        compoundTag.putString(DROP, BuiltInRegistries.ITEM.getKey(this.item).toString());
        compoundTag.putInt(COLOR, this.color.getId());this.discard();
    }

    @Override
    public void readAdditionalSaveData(CompoundTag compoundTag) {
        super.readAdditionalSaveData(compoundTag);
        if (compoundTag.contains(DROP))
            this.item = BuiltInRegistries.ITEM.get(ResourceLocation.parse(Objects.requireNonNull(compoundTag.get(DROP)).getAsString()));
        if (compoundTag.contains(COLOR))
            this.color = DyeColor.byId(compoundTag.getInt(COLOR));
    }
}