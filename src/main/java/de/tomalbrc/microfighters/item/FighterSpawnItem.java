package de.tomalbrc.microfighters.item;

import de.tomalbrc.microfighters.entity.Fighter;
import de.tomalbrc.microfighters.registry.MobRegistry;
import eu.pb4.polymer.core.api.item.PolymerItem;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.dispenser.BlockSource;
import net.minecraft.core.dispenser.DispenseItemBehavior;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.DispenserBlock;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import xyz.nucleoid.packettweaker.PacketContext;

import java.util.Objects;
import java.util.function.Consumer;

public class FighterSpawnItem extends Item implements PolymerItem {
    private final DyeColor color;

     public static final DispenseItemBehavior DISPENSE_BEHAVIOUR = new DispenseItemBehavior() {
        @Override
        @NotNull
        public ItemStack dispense(BlockSource blockSource, ItemStack itemStack) {
            if (itemStack.getItem() instanceof FighterSpawnItem fighterSpawnItem) {
                var pos = blockSource.pos().relative(blockSource.state().getValue(DispenserBlock.FACING));
                fighterSpawnItem.spawn(blockSource.level(), pos.getBottomCenter(), itemStack.getItem());
                blockSource.level().gameEvent(GameEvent.ENTITY_PLACE, pos, GameEvent.Context.of(blockSource.state()));
                itemStack.shrink(1);
            }
            return itemStack;
        }
    };

    public FighterSpawnItem(Properties properties, DyeColor color) {
        super(properties.stacksTo(16).rarity(Rarity.UNCOMMON));
        this.color = color;
    }

    @Override
    public void appendHoverText(ItemStack itemStack, TooltipContext tooltipContext, TooltipDisplay tooltipDisplay, Consumer<Component> consumer, TooltipFlag tooltipFlag) {
        consumer.accept(Component.literal("Micro Fighter"));
    }

    @Override
    @NotNull
    public Component getName(ItemStack itemStack) {
        return Component.literal(capitalize(this.color.name().toLowerCase()) + " Fighter");
    }

    private String capitalize(final String line) {
        return Character.toUpperCase(line.charAt(0)) + line.substring(1);
    }

    @Override
    public Item getPolymerItem(ItemStack itemStack, PacketContext context) {
        return Fighter.particleItem(this.color);
    }

    @Override
    public @Nullable ResourceLocation getPolymerItemModel(ItemStack stack, PacketContext context) {
        return this.getPolymerItem(stack, context).components().get(DataComponents.ITEM_MODEL);
    }

    @Override
    @NotNull
    public InteractionResult use(Level level, Player player, InteractionHand interactionHand) {
        ItemStack itemStack = player.getItemInHand(interactionHand);
        BlockHitResult blockHitResult = getPlayerPOVHitResult(level, player, ClipContext.Fluid.SOURCE_ONLY);

        if (blockHitResult.getType() != HitResult.Type.BLOCK) {
            return InteractionResult.PASS;
        } else if (!(level instanceof ServerLevel)) {
            return InteractionResult.PASS;
        } else {
            BlockPos blockPos = blockHitResult.getBlockPos().above();
            int count = itemStack.getCount();
            for (int i = 0; i < count; i++) {

                Fighter mob = spawn(level, blockPos, itemStack.getItem());

                itemStack.consume(1, player);
                level.gameEvent(player, GameEvent.ENTITY_PLACE, mob.position());
            }

            player.awardStat(Stats.ITEM_USED.get(this));
            return InteractionResult.CONSUME;
        }
    }

    public Fighter spawn(Level level, BlockPos blockPos, Item item) {
        return spawn(level, blockPos.getBottomCenter(), item);
    }

    public Fighter spawn(Level level, Vec3 blockPos, Item item) {
        Fighter mob = MobRegistry.FIGHTER.create(level, EntitySpawnReason.SPAWN_ITEM_USE);
        if (mob != null) {
            mob.setColor(this.color);
            mob.setItem(item);
            mob.yHeadRot = mob.getYRot();
            mob.yBodyRot = mob.getYRot();
            mob.finalizeSpawn((ServerLevel) level, level.getCurrentDifficultyAt(mob.blockPosition()), EntitySpawnReason.SPAWN_ITEM_USE, null);
            mob.setPos(blockPos);
            ((ServerLevel) level).addFreshEntityWithPassengers(mob);
        }

        return mob;
    }

    @Override
    @NotNull
    public InteractionResult useOn(UseOnContext useOnContext) {
        return this.use(useOnContext.getLevel(), Objects.requireNonNull(useOnContext.getPlayer()), useOnContext.getHand());
    }
}