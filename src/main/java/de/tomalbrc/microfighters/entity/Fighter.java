package de.tomalbrc.microfighters.entity;

import com.google.common.collect.ImmutableList;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import de.tomalbrc.microfighters.MicroFighters;
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
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket;
import net.minecraft.network.protocol.game.ClientboundPlayerInfoRemovePacket;
import net.minecraft.network.protocol.game.ClientboundPlayerInfoUpdatePacket;
import net.minecraft.network.protocol.game.ClientboundUpdateAttributesPacket;
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
import net.minecraft.world.entity.monster.RangedAttackMob;
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

public class Fighter extends PathfinderMob implements PolymerEntity, RangedAttackMob {
    public static final ResourceLocation ID = ResourceLocation.fromNamespaceAndPath(MicroFighters.MOD_ID, "fighter");

    private DyeColor color = DyeColor.GRAY;

    private Item item;

    @NotNull
    public static AttributeSupplier.Builder createAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 8.0)
                .add(Attributes.FOLLOW_RANGE, 10.0)
                .add(Attributes.MOVEMENT_SPEED, 0.24)
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
        var gameprofile = new GameProfile(this.getUUID(), "Fighter");
        switch (this.color) {
            case DyeColor.GRAY: {
                gameprofile.getProperties().put("textures", new Property("textures",
                        "ewogICJ0aW1lc3RhbXAiIDogMTcyOTAxODkxMjE5OCwKICAicHJvZmlsZUlkIiA6ICJjZjc4YzFkZjE3ZTI0Y2Q5YTIxYmU4NWQ0NDk5ZWE4ZiIsCiAgInByb2ZpbGVOYW1lIiA6ICJNYXR0c0FybW9yU3RhbmRzIiwKICAic2lnbmF0dXJlUmVxdWlyZWQiIDogdHJ1ZSwKICAidGV4dHVyZXMiIDogewogICAgIlNLSU4iIDogewogICAgICAidXJsIiA6ICJodHRwOi8vdGV4dHVyZXMubWluZWNyYWZ0Lm5ldC90ZXh0dXJlL2ZhOTgyZTA4ZTM3Nzc5YWJiZDQ3NmUxZjQ5YmU3MzAzMmUxOWNlMTZkMWY5ZDI0NTVlODdhODk3MGM0MjVlMTkiLAogICAgICAibWV0YWRhdGEiIDogewogICAgICAgICJtb2RlbCIgOiAic2xpbSIKICAgICAgfQogICAgfQogIH0KfQ==",
                        "p+60MsZcNUA85wJu2S5sf/9ua0mEnFnFjrgJbpEHkX++rEvIUs3Q3PEwWUFRoL1sBZYaF28YJQJd6X8yZEXjhCCM8xc5gMtLaEgZD/nHqAjr7ySAOdwZ9ZYoLjTiOIoi+uLdC6pOvbE2Her8E9SEQX9Z6uthd1yH7WgQAL0NLQmrzsacxjSkYfi5M57Ky21nCxyC2uD66hBjqLyf3zLhFBsYHFqNuMy7v7/wYYvsV2foh8nUz4jBSwNkQQVIARt4aOsW267PqjNRPXbFLAgqzqCPtOgsHci2yg/tyehIw498ZgJHh+ApFwwt/r/JCgQZ3n1ZJJXI8K0PSEqgXCl9bna0NP97npSU+Q8N13a5B6xoLza89cm4AtuRdH6Cp7Mph8OuqMyDiHGgiXGy2Eal5r5f4ZE+iMiCKpl4jp/P0TuUQ4Wjj3eKnNUGvJ5CAPOqzq1nf4fhw5iPyeaMsUlNOT8yZzPc9NoUo4lOraPVAFTmiJrtstmxa2reTdVgJM/Dz4LEhiN5/6+wF5BTZdJcdOmgGdJ9lDEf41eqzWDjp7W6wWiNplOaBy5WtEq0YV1RehP3iSAsMZOxsCdZWFs3sCQze2u57vGXgyhgc+3y1PDERMmJBsMjZQdDCC4t5Lb1NzjY/0xIO/ot6EaC+Rg/fssXWCCwLSYM27sSaNhNAS8="
                ));
            }
            case DyeColor.BLACK: {
                gameprofile.getProperties().put("textures", new Property("textures",
                        "ewogICJ0aW1lc3RhbXAiIDogMTcyOTAxODkxMjE5OCwKICAicHJvZmlsZUlkIiA6ICJjZjc4YzFkZjE3ZTI0Y2Q5YTIxYmU4NWQ0NDk5ZWE4ZiIsCiAgInByb2ZpbGVOYW1lIiA6ICJNYXR0c0FybW9yU3RhbmRzIiwKICAic2lnbmF0dXJlUmVxdWlyZWQiIDogdHJ1ZSwKICAidGV4dHVyZXMiIDogewogICAgIlNLSU4iIDogewogICAgICAidXJsIiA6ICJodHRwOi8vdGV4dHVyZXMubWluZWNyYWZ0Lm5ldC90ZXh0dXJlL2ZhOTgyZTA4ZTM3Nzc5YWJiZDQ3NmUxZjQ5YmU3MzAzMmUxOWNlMTZkMWY5ZDI0NTVlODdhODk3MGM0MjVlMTkiLAogICAgICAibWV0YWRhdGEiIDogewogICAgICAgICJtb2RlbCIgOiAic2xpbSIKICAgICAgfQogICAgfQogIH0KfQ==",
                        "p+60MsZcNUA85wJu2S5sf/9ua0mEnFnFjrgJbpEHkX++rEvIUs3Q3PEwWUFRoL1sBZYaF28YJQJd6X8yZEXjhCCM8xc5gMtLaEgZD/nHqAjr7ySAOdwZ9ZYoLjTiOIoi+uLdC6pOvbE2Her8E9SEQX9Z6uthd1yH7WgQAL0NLQmrzsacxjSkYfi5M57Ky21nCxyC2uD66hBjqLyf3zLhFBsYHFqNuMy7v7/wYYvsV2foh8nUz4jBSwNkQQVIARt4aOsW267PqjNRPXbFLAgqzqCPtOgsHci2yg/tyehIw498ZgJHh+ApFwwt/r/JCgQZ3n1ZJJXI8K0PSEqgXCl9bna0NP97npSU+Q8N13a5B6xoLza89cm4AtuRdH6Cp7Mph8OuqMyDiHGgiXGy2Eal5r5f4ZE+iMiCKpl4jp/P0TuUQ4Wjj3eKnNUGvJ5CAPOqzq1nf4fhw5iPyeaMsUlNOT8yZzPc9NoUo4lOraPVAFTmiJrtstmxa2reTdVgJM/Dz4LEhiN5/6+wF5BTZdJcdOmgGdJ9lDEf41eqzWDjp7W6wWiNplOaBy5WtEq0YV1RehP3iSAsMZOxsCdZWFs3sCQze2u57vGXgyhgc+3y1PDERMmJBsMjZQdDCC4t5Lb1NzjY/0xIO/ot6EaC+Rg/fssXWCCwLSYM27sSaNhNAS8="
                ));
            }
            case DyeColor.BLUE: { // OK
                gameprofile.getProperties().put("textures", new Property("textures",
                        "ewogICJ0aW1lc3RhbXAiIDogMTcyOTA0MTE2NDU3MywKICAicHJvZmlsZUlkIiA6ICI2MTZiODhkNDMwNzM0ZTM3OWM3NDc1ODdlZTJkNzlmZCIsCiAgInByb2ZpbGVOYW1lIiA6ICJKZWxseUZpbiIsCiAgInNpZ25hdHVyZVJlcXVpcmVkIiA6IHRydWUsCiAgInRleHR1cmVzIiA6IHsKICAgICJTS0lOIiA6IHsKICAgICAgInVybCIgOiAiaHR0cDovL3RleHR1cmVzLm1pbmVjcmFmdC5uZXQvdGV4dHVyZS9mZTUzYjE5MzI5NjRhMzY2MmIxYzkwMGI3Yjc3YjBmYjI3NWYyOTRiMGJiNjlkYzRlNzViMGEyZThhYTNjODcwIgogICAgfQogIH0KfQ==",
                        "GmPqlDTt+O2mbOzZunNGGKkbuhNpow2ILOsYVcn0kDn0AMyzSDUJThpJcd7JTrjdNCF3fNDVUoKcyDZUK4lSnjB+vkUsFljZSpNfd+jVjdInPV/uBOvqKbCkV9HcBea2Vv1mJMFVf2hFDBIXAlJprjOFNqM4RMjmebLl1yCnwSvTjlzNuAew++9v0zst3hwHlKfAdM8oZ1MM7fo5oi8RPJc0r1H+pazR0xX/e3o1o3mlNGlI61AfbAFidvRC9IG4DK9u+BvJjiDXipP74DHMjQFWDYlpRtrCGIYxVJp4HyPUMJKEhFoXxuauEi6HRaBbeX1N1vkWAZfhKlYEDpJ93rGFrmyvzhBk0tkYm7q/cTlZ0ofj5kEzoU0nTjMVZpKkZE+Ts3rTUTE+dy5toL75M03HnLhwBeoVKBzOwvxQP4EAoku3o8XK1LV3T8djMXNDTu6h7acM7qOeClJ5kE5r1/C6ht5+3bOSc0gZnLoQF72YaZ019a7gXNA6Q3NpMV56GPRadPl6in5TzGzXKoJnYBhR0U+N8wb4fcsq4YkUtcAu14sOGE7AinI71bwx374erhwtGE+8Dze8lD6KubKS86xzVgAeE5xXTz+PWyWrGBSUyoS628QWca316MGnnpZC/GstndyVd1/35tq0RJwKqkJOFJ4gV9iuVxzsyFYCBys="
                ));
            }
            case DyeColor.LIGHT_BLUE: {
                gameprofile.getProperties().put("textures", new Property("textures",
                        "ewogICJ0aW1lc3RhbXAiIDogMTcyOTAxODkxMjE5OCwKICAicHJvZmlsZUlkIiA6ICJjZjc4YzFkZjE3ZTI0Y2Q5YTIxYmU4NWQ0NDk5ZWE4ZiIsCiAgInByb2ZpbGVOYW1lIiA6ICJNYXR0c0FybW9yU3RhbmRzIiwKICAic2lnbmF0dXJlUmVxdWlyZWQiIDogdHJ1ZSwKICAidGV4dHVyZXMiIDogewogICAgIlNLSU4iIDogewogICAgICAidXJsIiA6ICJodHRwOi8vdGV4dHVyZXMubWluZWNyYWZ0Lm5ldC90ZXh0dXJlL2ZhOTgyZTA4ZTM3Nzc5YWJiZDQ3NmUxZjQ5YmU3MzAzMmUxOWNlMTZkMWY5ZDI0NTVlODdhODk3MGM0MjVlMTkiLAogICAgICAibWV0YWRhdGEiIDogewogICAgICAgICJtb2RlbCIgOiAic2xpbSIKICAgICAgfQogICAgfQogIH0KfQ==",
                        "p+60MsZcNUA85wJu2S5sf/9ua0mEnFnFjrgJbpEHkX++rEvIUs3Q3PEwWUFRoL1sBZYaF28YJQJd6X8yZEXjhCCM8xc5gMtLaEgZD/nHqAjr7ySAOdwZ9ZYoLjTiOIoi+uLdC6pOvbE2Her8E9SEQX9Z6uthd1yH7WgQAL0NLQmrzsacxjSkYfi5M57Ky21nCxyC2uD66hBjqLyf3zLhFBsYHFqNuMy7v7/wYYvsV2foh8nUz4jBSwNkQQVIARt4aOsW267PqjNRPXbFLAgqzqCPtOgsHci2yg/tyehIw498ZgJHh+ApFwwt/r/JCgQZ3n1ZJJXI8K0PSEqgXCl9bna0NP97npSU+Q8N13a5B6xoLza89cm4AtuRdH6Cp7Mph8OuqMyDiHGgiXGy2Eal5r5f4ZE+iMiCKpl4jp/P0TuUQ4Wjj3eKnNUGvJ5CAPOqzq1nf4fhw5iPyeaMsUlNOT8yZzPc9NoUo4lOraPVAFTmiJrtstmxa2reTdVgJM/Dz4LEhiN5/6+wF5BTZdJcdOmgGdJ9lDEf41eqzWDjp7W6wWiNplOaBy5WtEq0YV1RehP3iSAsMZOxsCdZWFs3sCQze2u57vGXgyhgc+3y1PDERMmJBsMjZQdDCC4t5Lb1NzjY/0xIO/ot6EaC+Rg/fssXWCCwLSYM27sSaNhNAS8="
                ));
            }
            case DyeColor.GREEN: {
                gameprofile.getProperties().put("textures", new Property("textures",
                        "ewogICJ0aW1lc3RhbXAiIDogMTY4OTY5NjcwMDI0MiwKICAicHJvZmlsZUlkIiA6ICJkYjJmMTJhMmU5MTI0ZThiODZjZmQyNzU2MDk1N2JhNCIsCiAgInByb2ZpbGVOYW1lIiA6ICJDYWxpYnJlcyIsCiAgInNpZ25hdHVyZVJlcXVpcmVkIiA6IHRydWUsCiAgInRleHR1cmVzIiA6IHsKICAgICJTS0lOIiA6IHsKICAgICAgInVybCIgOiAiaHR0cDovL3RleHR1cmVzLm1pbmVjcmFmdC5uZXQvdGV4dHVyZS8yNTFmYzNlZDg4YzljYTdjYTBlMDFlOThmMjI3ZGM5NDRhNTc5YzAyZjc5ZjllYzM4MGI5ZDZlNWFhNzE5YTQ3IgogICAgfQogIH0KfQ==",
                        "Gh9sT3vtbnD+4uPD9ngQGmD8aSR3DvZ038OnXDx3fBqqB6SjcqPYrKnsxE1CQIlFyILNcUfu2HoBrihIMc6bbDN3tYMzLtGx4w0gCMhiB7kUh+O33v5Me9CgDCi86JCx/Pm6wlgS0W6KTBlhecNnMh2JIJqYPoC1IIXOTRZ6//THEUBNoPUpVL/uPVdlEAb3usjIJLRZrrVU7J8LJkLQyokQy9hX5wCeOGNVDj7x0r4Y0JyAEIMfOQogooOZ9hUEmXAJQP4YhymR0A3YHH2hGPVW7BgU/COCmwDn0lgSuFgW0Q+AbK0PYX5IsoFfgTIAWep38mJImmVenHi6ULmsePjV7plHoH0edcXJg1M0OzwsvrLiioyz3TNM2fCSSKBnlaWZ5b7S4sodtfGuRvL/sHEFsYv1h9EJadA6AeGFknohq0yrYOXZJXYngLIbojlphAQHaUdoWNAsCIF/BSDREZ+NLmnDjo6uzrKp297TB25TeoxBfMYGA0V4zWGBjyXF+ynNzlAORrHngslD01gnQ6J5DoCKYm4TdlMbz1xhrGjtG2ebkRgmt+Oj5OyJhNSkoT8TqpV6CL0WdljIeEBWPaVl0TC7ZqjMwnE6dhF7ox6i/eKPi9mXF93HylppORC701xODV2xR1njs0TjlkuEUTGBPLg+sB3ciOLPjgFI390="
                ));
            }
            case DyeColor.LIME: {
                gameprofile.getProperties().put("textures", new Property("textures",
                        "ewogICJ0aW1lc3RhbXAiIDogMTY4OTY5NzAzNzQ3NiwKICAicHJvZmlsZUlkIiA6ICIyYzEwNjRmY2Q5MTc0MjgyODRlM2JmN2ZhYTdlM2UxYSIsCiAgInByb2ZpbGVOYW1lIiA6ICJOYWVtZSIsCiAgInNpZ25hdHVyZVJlcXVpcmVkIiA6IHRydWUsCiAgInRleHR1cmVzIiA6IHsKICAgICJTS0lOIiA6IHsKICAgICAgInVybCIgOiAiaHR0cDovL3RleHR1cmVzLm1pbmVjcmFmdC5uZXQvdGV4dHVyZS9mNjAwNTcxZGUzYmQyNDhjNTE5ZGUzNDJkNDNkYTBlNzgyMjI3YTQ3MDEyMDBmODdiNDdlYWI3N2I0NjM0ODI4IgogICAgfQogIH0KfQ==",
                        "UmIt0xzCXi0nCfcwY/nfI3NKd7foo1hFIjQ9AJiJZ37VP3LddRR71+etlBicBaEPjMtXvhElNuNTjg0D9A7WVgjK2hDmLNiK+Gwt6jKUxymzDK505uHBU3Lz3bXj4I7g4TueDJj0AhCXQA90gKbbSTQISR5SNXzDo1LBtKBsGjriNJSZnuFkn0HtVG/GGpUn56FKR5xlfmKIrFInaTSxJmz86WCTuWPldJkRPqIt0VfUsln66K9Kcbwm0BznFCihYwZa9ELM8BF7cnBvGl7pr6F1baFc/KBzJctdZ0QsiVwouzv0wE5h7k3UH7ZmO/0dLoyVWR9CtgSn1IqfL3U4YX8ye5cGnIyFpmO9VZOh909EaBQ48wxo7FXytfaIA8KAZS5SNrAqO2G37NP+qbAEduUu4yERRPVULN5Mavoj5z9EIEYasfEVkWGbvvHC90P0zrIcwIurULRGIqpNTtuUQYlC77fLPkfoP9ZZ+2O40BB/CHpKdlE65EOTIbARq+1H5uNlyEL9J1LgcBg6KIDvZQzH+ldDkPAFzyhikVm2Ckb/LiIXY9k/vQIf3mCWyWtU42DnrZESQZjWQpZxDkZnVMGgRDCEI1+EcWF1qCQv9Ie/zQfKzikXISZOZe68DS4+hq23Yd2wRJyixI1xzvuhY4UD0hV6IhXXRgvAU991qdw="
                ));
            }
            case DyeColor.BROWN: { // OK
                gameprofile.getProperties().put("textures", new Property("textures",
                        "ewogICJ0aW1lc3RhbXAiIDogMTcyOTA0MTIzNjUyMiwKICAicHJvZmlsZUlkIiA6ICI4NDIyMDRlNjY4ODA0ODdiYWU1YTA1OTUzOTRmOTk0OCIsCiAgInByb2ZpbGVOYW1lIiA6ICJuZWE4OW8iLAogICJzaWduYXR1cmVSZXF1aXJlZCIgOiB0cnVlLAogICJ0ZXh0dXJlcyIgOiB7CiAgICAiU0tJTiIgOiB7CiAgICAgICJ1cmwiIDogImh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvOGVjMmFjM2UxMWYyMTk0MmVkMTE5OTY1ZDM3YTUzNGRhMmI4Y2Q0MGQ0ZWM1N2FlZDRjMjBlOWU0NTJjNjZiIgogICAgfQogIH0KfQ==",
                        "DmNIEKZH1SbnBmeBROE35+YsnZ3CVEMNQcmmXtlH7tDLmrxLGpV5hetTYWp4+xjJ/mgDieS12MILrxic/xmHjm7wDUwMpxt8qIApQtKr+WTuHkOO3CVkrgBwtyEWtvuQAmQo7o1iPf4iYPOa9pwLsMgT29zHOfbn9nJ0WldajXx6vLoSRIheCVQ4x3pX5RIFoLRlXmD3n2dgsZnIuQeulvdhUH3egFV4eWkHBnrW/1H0wog3GKhJb7CJN8tZOLcayEiZMDYA3C8t/MAwBRyXsOFFwXQ46z9yFRnVuNO4yv/uX73nnvXxpqyQlegdcmuDdnHr9pwbTzexhj6y2/uUtM4HSLeyCvQDpzf46eDHNRRuT3Rm4TOgb/q7sQ4gOsVMslIVFESoThNyOH8L5ISsJGJP0nMIova19WxSKwohThUFYjmvndMSBj5YBhLtg1uQ6ZcmiuGNF52psoWmphqzJODX7W6g7klQAK6m2/18dHsLXOWY0dvL+4O0bbD9dnd8JERAGvPQ4orn5UbG7tXaa4GoxGSLWrtHjtw3hZJ9FWHPgCqFh8A9YmZaTVZk1XK6yBC6mFNfn5IkcTniteVy3T/TvFlkHxgJJKkyl0I/e6XFZxYsDv9b+PrkVcpAjy7mEt8blviffff3a0+BPOg3qtChAuHP9htECHnM975olPc="
                ));
            }
            case DyeColor.CYAN: { // OK
                gameprofile.getProperties().put("textures", new Property("textures",
                        "ewogICJ0aW1lc3RhbXAiIDogMTcyOTA0MTA3MzI0MSwKICAicHJvZmlsZUlkIiA6ICIyNGFjYWZhMmVlNDA0ZGMwYmRjMjViNWVjNjgxZmRjZiIsCiAgInByb2ZpbGVOYW1lIiA6ICJXaWxsZXJtaW5hRDM0dGgiLAogICJzaWduYXR1cmVSZXF1aXJlZCIgOiB0cnVlLAogICJ0ZXh0dXJlcyIgOiB7CiAgICAiU0tJTiIgOiB7CiAgICAgICJ1cmwiIDogImh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMWExZGY5NDVkNmI4MmRlOTI1NWQ2NTYyNDgzZmRjMDM3ZTM2NjYxZDllZGYzMTJmOTY5MTk3ZDhiNjI5MjAzMyIKICAgIH0KICB9Cn0=",
                        "RJdqR+gKteBK08zDauhOwlNta2ivJMAn58Z5RublhoP8wGrMQMKY0GQSKVlc1XFLTLG5xwd45VYkazTGcADDvNItV/93lENr8lEhJZUs53/F4TfUYbfoBQZksoimIvFZIJ4Ma0Wz+WJUlo8jwLLiqZEFPeErYSCI1GPl50voCmWGezKKyFdBYCTM6NL1vSm3qfG1ar3WirVmRjxIst+W8T5pXUOniPG7XuTrZ0LtTLfog9F6RgkYlqDmwWKuK1Pqmfn3VJ1Q8QZ0i9n6/TzvuFX/Nk4YhTytc7r271Qajh4Bmv/JrFT3wpxl+wzMOD/Yiun3Uij5xF1f5YqzTj6bIVCjD+S3PnZkZ/j5XHduGVlYFWZu5mLDfjBlQFGy2zdWAwibz9UdUxDdTR20oW8Eifnh+ZqzaOGjOhsIoN2IZNNC52u3FkADpzpoCIbuPuHnxDMzTi2kOyi5KnyqgcO9R6qnc8BzbYhyx6xJVN4KqiRLi/W74oZBdiXNWu3AbSRnx540ryQpyOGb/fX+i92pbVvpljyzQ0qXUXgRBQkLt7g8I/DciLEctaXIL0dAVPvmunVYQQ0Vf3E3Q6L64GNo7cUY2HIvQgliKsCdyczblFIYskcdx/uwwLiCGYag7fVOFYKSg6mcfr12TkLn4xOqJof0fku5i14m4sfRc6MF0oQ="
                ));
            }
            case DyeColor.ORANGE: {
                gameprofile.getProperties().put("textures", new Property("textures",
                        "ewogICJ0aW1lc3RhbXAiIDogMTY4OTcwMDgyNzA4MywKICAicHJvZmlsZUlkIiA6ICIyYTExYzU4Njg1ZmU0ZGM3YjY0Nzc4OGYyMzZkN2VkMCIsCiAgInByb2ZpbGVOYW1lIiA6ICJNeWthbFRlY2giLAogICJzaWduYXR1cmVSZXF1aXJlZCIgOiB0cnVlLAogICJ0ZXh0dXJlcyIgOiB7CiAgICAiU0tJTiIgOiB7CiAgICAgICJ1cmwiIDogImh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMTEyZTI5Y2ZmZjlhN2I2YzUxNjk5MGExYzZhMWE5MzQ2ZGY2NzZkODBkNDEzNWNkNmI4NGNmNmU5N2UwZTRmNSIKICAgIH0KICB9Cn0=",
                        "vJMWHc4B8bGdZ6lHA8It7tSDNXJXAGnCZ/iprkc6k4j/Kdya27DuEItr5Lc7V22htkPXnq/wI0WGFxmM2VIegxhR2tfpemk9bJy8ZZ5PPWNC92R4WBEmTPjSJ+0HSC8RhKdEgKfDQY5Fx9ru0PtzWRp8xyy7CcL1j7yiGcsTALxHaNTFse8rerSz8Vygfn3hldcmIDxpNK+gg3sSQ+DFGLCOCwmQ/BLyJqKNI6PTfdaFejhTr5qmrw233MsyynQpm7uUdA6VZ1TRZUN1NY2XVSZXu+bJU7JLoOQ7kHKBAKz6FXpcTKbl0/kt58ZnD8OZu7IzK7WC7eEuUT52405dgFYV9aR2UtTQpcRuJ+W5tURj6mmw8nlznSpSw2b0iBKtZdmZyPrmZEg418UeXgDY5yse7CB1ngTKO80WK4bFLCwlMv7VqmPRYpJIRaIbxcTcquakId8PGqM39RR6oAEWH10Gmow5bPUpDJBzqoDEfx5eBrw9vByeYSfnHMeEwxnI+f0Q+oOdENjDtu2XWMV5jeR9H/hyTFMjEfBHIaqGk5Mb/n0lsboi0fgMeX3Wyx7fSeb+0Zgcwh+1ZvTX1R4dWOpc2jyNd3hbQL7rCG3nDPyeIV10d+HPrBPyEwkuKnqYBo3hwBNWUAzA3krGk45XWXNYbuxmX1wKSews5Ub/0xA="
                ));
            }
            case DyeColor.YELLOW: {
                gameprofile.getProperties().put("textures", new Property("textures",
                        "ewogICJ0aW1lc3RhbXAiIDogMTY4OTY5Njk0NTg1NywKICAicHJvZmlsZUlkIiA6ICI2NDZhMmVhYzg2OWM0N2ZmYmYzZjRiMTc3Y2FhNDM0MiIsCiAgInByb2ZpbGVOYW1lIiA6ICJTZXJwZW50U2FnZXMiLAogICJzaWduYXR1cmVSZXF1aXJlZCIgOiB0cnVlLAogICJ0ZXh0dXJlcyIgOiB7CiAgICAiU0tJTiIgOiB7CiAgICAgICJ1cmwiIDogImh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMTEyN2JkZWY0NWI5MTY3ZjY3OTllNGY3NGUxOWQxYmRiNGYxNWI5MWNiNmFhMWJlYTY1N2IyOGEwMDQ5Y2E2ZSIKICAgIH0KICB9Cn0=",
                        "k+5f2bXjPqofLquicOVOt7ygg9PzD0M+9qi2Q2fze3NpziWBB3F/+pvswrKHVngATwtvnhEGX6Ti5PBknGKjj9erXAPI/8VdkTl0ODN0KmrHZX6aW1gLMu9KduyjyFdcgJoKSWedaGdtZQbovE3iUPBcOumLJ+araN/kVOiKAhegOlxAQlVprZdwApimOWav9JjU30GqbbnV444fGaOhFvKjxk5XLReEvPBP7cvWQ4/z334fuxPRcPldtk1mEFwASpn+Oo1Z4entCa4HKtpLZklwodYixUfVJpVQBtAbKg7x4cheoKGOD0p2L5akpCE2U9fqEnQzdtGLDT3b3s8wRGJCJwCbxBCVK920xxwjjCGkE13lIWmUN3ZH7KFd4vsiAzN4Va8o2A5pXBV8kANfplgT1Fi+VZgXB0sOB7gmFE+X86sPQ64ruFT9qFrfB9ZKPv3t44RGnsbNBThrw1akW0CyQ5Pceep6qbPcYQh5CQxNZNVA7bD4XQrYUIz9xbjIynRa1QfrDbL1w2SityGfNcs9f0s1fn8yHXXXTj8eGXgnL/mrd2ZFaBrLVkd6aLSenAMBxtf8vqRaoOzDqmu9M3Mi9bWZZ5IkuPTCguQ3gUGlGwGzLzuUG2JZBQCbnNSmE6kIKqHkVF8aA6G5Os+95S2zv02Rw3iBqIzPfjTEEIk="
                ));
            }
            case DyeColor.LIGHT_GRAY: {
                gameprofile.getProperties().put("textures", new Property("textures",
                        "ewogICJ0aW1lc3RhbXAiIDogMTcyOTAxODkxMjE5OCwKICAicHJvZmlsZUlkIiA6ICJjZjc4YzFkZjE3ZTI0Y2Q5YTIxYmU4NWQ0NDk5ZWE4ZiIsCiAgInByb2ZpbGVOYW1lIiA6ICJNYXR0c0FybW9yU3RhbmRzIiwKICAic2lnbmF0dXJlUmVxdWlyZWQiIDogdHJ1ZSwKICAidGV4dHVyZXMiIDogewogICAgIlNLSU4iIDogewogICAgICAidXJsIiA6ICJodHRwOi8vdGV4dHVyZXMubWluZWNyYWZ0Lm5ldC90ZXh0dXJlL2ZhOTgyZTA4ZTM3Nzc5YWJiZDQ3NmUxZjQ5YmU3MzAzMmUxOWNlMTZkMWY5ZDI0NTVlODdhODk3MGM0MjVlMTkiLAogICAgICAibWV0YWRhdGEiIDogewogICAgICAgICJtb2RlbCIgOiAic2xpbSIKICAgICAgfQogICAgfQogIH0KfQ==",
                        "p+60MsZcNUA85wJu2S5sf/9ua0mEnFnFjrgJbpEHkX++rEvIUs3Q3PEwWUFRoL1sBZYaF28YJQJd6X8yZEXjhCCM8xc5gMtLaEgZD/nHqAjr7ySAOdwZ9ZYoLjTiOIoi+uLdC6pOvbE2Her8E9SEQX9Z6uthd1yH7WgQAL0NLQmrzsacxjSkYfi5M57Ky21nCxyC2uD66hBjqLyf3zLhFBsYHFqNuMy7v7/wYYvsV2foh8nUz4jBSwNkQQVIARt4aOsW267PqjNRPXbFLAgqzqCPtOgsHci2yg/tyehIw498ZgJHh+ApFwwt/r/JCgQZ3n1ZJJXI8K0PSEqgXCl9bna0NP97npSU+Q8N13a5B6xoLza89cm4AtuRdH6Cp7Mph8OuqMyDiHGgiXGy2Eal5r5f4ZE+iMiCKpl4jp/P0TuUQ4Wjj3eKnNUGvJ5CAPOqzq1nf4fhw5iPyeaMsUlNOT8yZzPc9NoUo4lOraPVAFTmiJrtstmxa2reTdVgJM/Dz4LEhiN5/6+wF5BTZdJcdOmgGdJ9lDEf41eqzWDjp7W6wWiNplOaBy5WtEq0YV1RehP3iSAsMZOxsCdZWFs3sCQze2u57vGXgyhgc+3y1PDERMmJBsMjZQdDCC4t5Lb1NzjY/0xIO/ot6EaC+Rg/fssXWCCwLSYM27sSaNhNAS8="
                ));
            }
            case DyeColor.WHITE: {
                gameprofile.getProperties().put("textures", new Property("textures",
                        "ewogICJ0aW1lc3RhbXAiIDogMTcyOTAxODkxMjE5OCwKICAicHJvZmlsZUlkIiA6ICJjZjc4YzFkZjE3ZTI0Y2Q5YTIxYmU4NWQ0NDk5ZWE4ZiIsCiAgInByb2ZpbGVOYW1lIiA6ICJNYXR0c0FybW9yU3RhbmRzIiwKICAic2lnbmF0dXJlUmVxdWlyZWQiIDogdHJ1ZSwKICAidGV4dHVyZXMiIDogewogICAgIlNLSU4iIDogewogICAgICAidXJsIiA6ICJodHRwOi8vdGV4dHVyZXMubWluZWNyYWZ0Lm5ldC90ZXh0dXJlL2ZhOTgyZTA4ZTM3Nzc5YWJiZDQ3NmUxZjQ5YmU3MzAzMmUxOWNlMTZkMWY5ZDI0NTVlODdhODk3MGM0MjVlMTkiLAogICAgICAibWV0YWRhdGEiIDogewogICAgICAgICJtb2RlbCIgOiAic2xpbSIKICAgICAgfQogICAgfQogIH0KfQ==",
                        "p+60MsZcNUA85wJu2S5sf/9ua0mEnFnFjrgJbpEHkX++rEvIUs3Q3PEwWUFRoL1sBZYaF28YJQJd6X8yZEXjhCCM8xc5gMtLaEgZD/nHqAjr7ySAOdwZ9ZYoLjTiOIoi+uLdC6pOvbE2Her8E9SEQX9Z6uthd1yH7WgQAL0NLQmrzsacxjSkYfi5M57Ky21nCxyC2uD66hBjqLyf3zLhFBsYHFqNuMy7v7/wYYvsV2foh8nUz4jBSwNkQQVIARt4aOsW267PqjNRPXbFLAgqzqCPtOgsHci2yg/tyehIw498ZgJHh+ApFwwt/r/JCgQZ3n1ZJJXI8K0PSEqgXCl9bna0NP97npSU+Q8N13a5B6xoLza89cm4AtuRdH6Cp7Mph8OuqMyDiHGgiXGy2Eal5r5f4ZE+iMiCKpl4jp/P0TuUQ4Wjj3eKnNUGvJ5CAPOqzq1nf4fhw5iPyeaMsUlNOT8yZzPc9NoUo4lOraPVAFTmiJrtstmxa2reTdVgJM/Dz4LEhiN5/6+wF5BTZdJcdOmgGdJ9lDEf41eqzWDjp7W6wWiNplOaBy5WtEq0YV1RehP3iSAsMZOxsCdZWFs3sCQze2u57vGXgyhgc+3y1PDERMmJBsMjZQdDCC4t5Lb1NzjY/0xIO/ot6EaC+Rg/fssXWCCwLSYM27sSaNhNAS8="
                ));
            }
            case DyeColor.MAGENTA: {
                gameprofile.getProperties().put("textures", new Property("textures",
                        "ewogICJ0aW1lc3RhbXAiIDogMTcyOTAxODkxMjE5OCwKICAicHJvZmlsZUlkIiA6ICJjZjc4YzFkZjE3ZTI0Y2Q5YTIxYmU4NWQ0NDk5ZWE4ZiIsCiAgInByb2ZpbGVOYW1lIiA6ICJNYXR0c0FybW9yU3RhbmRzIiwKICAic2lnbmF0dXJlUmVxdWlyZWQiIDogdHJ1ZSwKICAidGV4dHVyZXMiIDogewogICAgIlNLSU4iIDogewogICAgICAidXJsIiA6ICJodHRwOi8vdGV4dHVyZXMubWluZWNyYWZ0Lm5ldC90ZXh0dXJlL2ZhOTgyZTA4ZTM3Nzc5YWJiZDQ3NmUxZjQ5YmU3MzAzMmUxOWNlMTZkMWY5ZDI0NTVlODdhODk3MGM0MjVlMTkiLAogICAgICAibWV0YWRhdGEiIDogewogICAgICAgICJtb2RlbCIgOiAic2xpbSIKICAgICAgfQogICAgfQogIH0KfQ==",
                        "p+60MsZcNUA85wJu2S5sf/9ua0mEnFnFjrgJbpEHkX++rEvIUs3Q3PEwWUFRoL1sBZYaF28YJQJd6X8yZEXjhCCM8xc5gMtLaEgZD/nHqAjr7ySAOdwZ9ZYoLjTiOIoi+uLdC6pOvbE2Her8E9SEQX9Z6uthd1yH7WgQAL0NLQmrzsacxjSkYfi5M57Ky21nCxyC2uD66hBjqLyf3zLhFBsYHFqNuMy7v7/wYYvsV2foh8nUz4jBSwNkQQVIARt4aOsW267PqjNRPXbFLAgqzqCPtOgsHci2yg/tyehIw498ZgJHh+ApFwwt/r/JCgQZ3n1ZJJXI8K0PSEqgXCl9bna0NP97npSU+Q8N13a5B6xoLza89cm4AtuRdH6Cp7Mph8OuqMyDiHGgiXGy2Eal5r5f4ZE+iMiCKpl4jp/P0TuUQ4Wjj3eKnNUGvJ5CAPOqzq1nf4fhw5iPyeaMsUlNOT8yZzPc9NoUo4lOraPVAFTmiJrtstmxa2reTdVgJM/Dz4LEhiN5/6+wF5BTZdJcdOmgGdJ9lDEf41eqzWDjp7W6wWiNplOaBy5WtEq0YV1RehP3iSAsMZOxsCdZWFs3sCQze2u57vGXgyhgc+3y1PDERMmJBsMjZQdDCC4t5Lb1NzjY/0xIO/ot6EaC+Rg/fssXWCCwLSYM27sSaNhNAS8="
                ));
            }
            case DyeColor.PINK: { // OK
                gameprofile.getProperties().put("textures", new Property("textures",
                        "ewogICJ0aW1lc3RhbXAiIDogMTcyOTA0MDk5ODE0MywKICAicHJvZmlsZUlkIiA6ICJjYmNkNDQzZGE1NTI0OGU3ODM3NWNmZjYwMmQzZWI0NyIsCiAgInByb2ZpbGVOYW1lIiA6ICJPX1JlaSIsCiAgInNpZ25hdHVyZVJlcXVpcmVkIiA6IHRydWUsCiAgInRleHR1cmVzIiA6IHsKICAgICJTS0lOIiA6IHsKICAgICAgInVybCIgOiAiaHR0cDovL3RleHR1cmVzLm1pbmVjcmFmdC5uZXQvdGV4dHVyZS81MWZlNDM4ODM0NDQ3YmFkOTY0M2Y3ZGQyNDBjYzc0Nzg5MzZlZjk5MGVjMGI2ZGVlNGVkM2QwMzEyZGVmOGFmIgogICAgfQogIH0KfQ==",
                        "Z9lD7khC/RrSbw+gMXPyY/uhEWHDERxc1aLavlTTI25LWbG4VFb3DbECCVI+W5lPG81c3BrTkfpWm1O2apjaMoAn0uH80D77rU8kfcyjnuoaHC3uYvdlUqMd+nYyxWRrrJ79tGmPGAF3BL5SGqRgOM6bKgUcf1PAnv8zKVPYXxm/t7k1c+L8B/e6Wi8yHSUCTEH/CYCi5yjtvtMcYv97lgoD0GIAsutSg3BdwREtdgJxxXiH2lzPzpMVaB+mhuCJ/vxlB0g/Pj7nJr3TXtC9nu3BIe/PSdPxUCxrFR9qHpVtMrwE6Z4Y0UShWjyIcHVD8uWjkgS7avyd9hmMSPVMv3M8T7FN2+xQLR//1qeIMs6uL3rD8JOyFItwO0mOj1oj5FdjywPH5FQlsu5E9k52Bmg7T5wG2kJMWdMwXNj6bmPiS2AQ0axNl6JrLlrA5ghUmmNMjgVpUI1ndHT7Puyx0JnKIdX72fAPlOZA0HNhdOkaxtn2cm760Cc3pLJcjhTjwiuEXUxqjRsRAarbL7Rt/o3BNxc+z4uaqhOO2wMg2Sh2+SAENXxwiZZbHoaCOaAC9k9KHQOb2uOV6PDg259XyDHIG+W7umgRcuv2BjHfOyAkHJGgvjAcOqt2sYz4MUcxR7/ZThVUgQSgb1fu2TPJwNIdtCQJIenjE+rsbZLBRQ0="
                ));
            }
            case DyeColor.RED: {
                gameprofile.getProperties().put("textures", new Property("textures",
                        "ewogICJ0aW1lc3RhbXAiIDogMTY4OTcwMDkwOTQ5NCwKICAicHJvZmlsZUlkIiA6ICJhODJkZTliNjZhYjE0OGZhOTQ3OGY0OWJiZjg5OTk1YyIsCiAgInByb2ZpbGVOYW1lIiA6ICJmdWhyM3JibDRiIiwKICAic2lnbmF0dXJlUmVxdWlyZWQiIDogdHJ1ZSwKICAidGV4dHVyZXMiIDogewogICAgIlNLSU4iIDogewogICAgICAidXJsIiA6ICJodHRwOi8vdGV4dHVyZXMubWluZWNyYWZ0Lm5ldC90ZXh0dXJlLzJjOGI4OGQ0NmZlMjM0MzQ0NjM4M2E1OTFhN2IwZWU3MWVmM2JlYWVlNmY3ZDcxNDczZWQxNDE1OTM0YzIxZjgiCiAgICB9CiAgfQp9",
                        "FpUqJOL2Oo5lXb3auEOXUCuqo4qIvXN6lb7OsbVJYPUQtoIpYkzSr+h/bNFIdX0F0XeqvSzKtZMKjneJPe4t6YgOxtK3LoRiWdwTN5KhgcQpdQuc/3Pk2TsC/+53UoJr1sq6YmehqSgaWm8VB+bNt9t01GbqVy9/IhqY56fbQX8/UnaRpQbbBc60C9s7lV1Ak3ic2U/LWV1XWp+ow25LiWCBKSZZ5fwJcWqCRORIqdyF/c/UgFJu7RJ6MqidatW5Ki+ueQPTWtKAUFX4hoLffNBctBoJzshLpmx7vQh+lGNRVDWujb9DHG3PG9LwCQ3hcNu9rkLm/tbLlUucAIZu1M+i1fbaisTTOCKrkrcHEocg0Z4ljd4QIrZ/th92Fn0o265/8vACOFjnnd+JhzPwfj7utyROXe5lKQqhNaGedtbK4PdLly3svLbSWeDi1/o41ZfsJNEseKqZMksj/pM09f0zdk4bmaLdxkEWgWRmZa9Gqwk6d8qe1tynuYqDMCD5xF8MIIdaj/RU5oNQhkwoYzj+4sqvY8+QVeLDqeqVQcmybMFM2DOOnC9EsZAJBK5mEBaQthjSbwALT76stf4ATd2MIpkYO47y/NB0x9V+LB7srkER9YxATcDiNwYkK3vTG2BE++9htCxAYJTATV+xk+Plud842TgEDiGsY1trSZc="
                ));
            }
            case DyeColor.PURPLE: {
                gameprofile.getProperties().put("textures", new Property("textures",
                        "ewogICJ0aW1lc3RhbXAiIDogMTY4OTY5NTg3NTkwOCwKICAicHJvZmlsZUlkIiA6ICJiMGQ0YjI4YmMxZDc0ODg5YWYwZTg2NjFjZWU5NmFhYiIsCiAgInByb2ZpbGVOYW1lIiA6ICJNaW5lU2tpbl9vcmciLAogICJzaWduYXR1cmVSZXF1aXJlZCIgOiB0cnVlLAogICJ0ZXh0dXJlcyIgOiB7CiAgICAiU0tJTiIgOiB7CiAgICAgICJ1cmwiIDogImh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYzVlMjFhY2NiYzk3OTM3ZTk1MTUxNWE2MGNkMGJiYzJmYTNmYjUzOWQ5ZTRjODQxNzgxMzY4ODgyMDBkZWZmNiIKICAgIH0KICB9Cn0=",
                        "mjZTnHveIxLA0YI6EjR9umIinWUs76SK6XGO4Kcw3KJNd5raZiiuU934oKbMRaQ6+0+KdhiSUBNJqukxXKSv9Z2JGY1l/Hp+YPGxnSMKob1sOl0bc1A9/BgXBEEdDKraKMwArrWp1wplufO+f4NTGn4qMPc4+ORUAUw5C8dbnqPnVZvdJWhrRmcmSANnclHsX6JdYfULu2sXp5aEkqYpRAOAEAnNZmE65kxLPoSzL1/FFLUsdmwGLwZM+NDKYKk86o1UpX46FrN78sxuhdrM1HlvmwfTnkNpHO29SzM/ADpA0K50n0brIZYT1v8c2A1OqufAPHh7e7bzX5FV9JqHoUa22cKjCwaKlRefM4pvydarQxqSAllmG6e8mCsYYBzFw2XNV3O1I06fM9AXlDY+2euMYHsO0XoRMmtuoVae4LT+NDLZRK3HYmC3kzrKjnqff+jiBtZh1UdHIS+7cEXugXCdH80YxrZ8Y2aFabTWLxB/X3MSTNo8Y3taBvXFXfGiQOoqpn/HTaW2HO4XOpX2LtzYBXamhWk005OAEqRLaskstvk7R/4sJgsK2VSN4PaZcpzrbPdUpen4burCojSJOEpsSUJbrL1QxDQFFv+qgk3OvmseArj03hPp0RnXfw00jg5AnwmMC04Mv1JhW8/tOdVy5cBnKtd8Cx2EABp4oWY="
                ));
            }
            default: {
                gameprofile.getProperties().put("textures", new Property("textures",
                        "ewogICJ0aW1lc3RhbXAiIDogMTYxNDk0NDg4ODg4OSwKICAicHJvZmlsZUlkIiA6ICI1N2IzZGZiNWY4YTY0OWUyOGI1NDRlNGZmYzYzMjU2ZiIsCiAgInByb2ZpbGVOYW1lIiA6ICJYaWthcm8iLAogICJzaWduYXR1cmVSZXF1aXJlZCIgOiB0cnVlLAogICJ0ZXh0dXJlcyIgOiB7CiAgICAiU0tJTiIgOiB7CiAgICAgICJ1cmwiIDogImh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNzdmYzc1ZTBlYzAwNDAyMjMyOTZhYTRkMDhiZDI2YmU0ZDE3MmU4ZGUwNzE4NTU4ODgyMmZhZTM2M2QyMjMxOSIKICAgIH0KICB9Cn0=",
                        "R/dm6ic4CYbsr66Iz859K5r1MVd7y08FUvOmJgKTE5KRcPdDNe71Vv61jzh0jQ9QeZJXsHe4+58RY2LiXn7LdPKWpNd+ljK2K4n00Yjp/MM9s6ppNOAQj32LY5UuwcXPUkTSQfr2GROM9zvY93lAuILr6xodvUoIrPcbBDHgxuN6FDiE1jKfFF5z2yZIHOVZXqJPJ+0ri1sw3mjMhbO3dPdpzTW24olgR3wqbXgfEwIeiMk1En+wBtce6ZnNHNXIaMj4fFDAsMmFKqvFcPY8SjfjW/jWBDYNFUCMpxTS2XduQGhSoSlNXG+OrI93Ya/iObGeqAp9WCqFvkV8azyG1VTFfegZCFrUwKV+819B8Q3H3JzJOzES9zvhX5CDKYaE4QvWAqGzTOVw7h0NxtOh9alFkbRR2lWFiBhUMT8EqRjkb+OyBVe9vGRJOU448aLQFyuEWLICje9FAmOHRH0JFpMDEKCLvAAZKZAOx9jceQKrcrcAS0f9nnqjWLLrWMK8lWh0CNcPN1P51rQsxMUlWddNEig+RyjOLHIz/fsv3EQ7yycWkeFfkxq0NAVZGajp4T3NhtWG+WlYywafy5Gtys0Mmv4CXu6xzoUdeLhtMjwgmqfdatQlAJGiZCuSMc1KwWis2inI1YDg5jIy8BTViFBGn76mks21iUEpL4JP8FU="
                ));
            }
        }
        packet.entries().add(new ClientboundPlayerInfoUpdatePacket.Entry(this.getUUID(), gameprofile, false, 0, GameType.ADVENTURE, Component.empty(), null));
        packetConsumer.accept(packet);


    }

    @Override
    public void startSeenByPlayer(ServerPlayer player) {
        super.startSeenByPlayer(player);

        var attributesPacket = new ClientboundUpdateAttributesPacket(this.getId(), List.of());
        var attributeSnapshots = List.of(new ClientboundUpdateAttributesPacket.AttributeSnapshot(Attributes.SCALE, MicroFighters.SCALE, ImmutableList.of()));
        ((EntityAttributesS2CPacketAccessor)attributesPacket).getEntries().addAll(attributeSnapshots);
        player.connection.send(attributesPacket);

        var id = VirtualEntityUtils.requestEntityId();
        var entityPacket = new ClientboundAddEntityPacket(id, UUID.randomUUID(), player.getX(), player.getY(), player.getZ(), 0, 0, EntityType.BLOCK_DISPLAY, 0, Vec3.ZERO, 0.0);
        player.connection.send(entityPacket);
        player.connection.send(VirtualEntityUtils.createRidePacket(this.getId(), IntList.of(id)));
    }

    @Override
    public void stopSeenByPlayer(ServerPlayer player) {
        player.connection.send(new ClientboundPlayerInfoRemovePacket(List.of(this.getUUID())));
        super.stopSeenByPlayer(player);
    }

    @Override
    public void performRangedAttack(LivingEntity livingEntity, float f) {

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
        compoundTag.putString("drop", BuiltInRegistries.ITEM.getKey(this.item).toString());
        compoundTag.putInt("color", this.color.getId());
    }

    @Override
    public void readAdditionalSaveData(CompoundTag compoundTag) {
        super.readAdditionalSaveData(compoundTag);
        if (compoundTag.contains("drop"))
            this.item = BuiltInRegistries.ITEM.get(ResourceLocation.parse(Objects.requireNonNull(compoundTag.get("drop")).getAsString()));
        if (compoundTag.contains("color"))
            this.color = DyeColor.byId(compoundTag.getInt("color"));
    }
}