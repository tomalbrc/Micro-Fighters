package de.tomalbrc.microfighters.item;

import de.tomalbrc.microfighters.entity.Fighter;
import eu.pb4.polymer.core.api.item.PolymerItem;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import xyz.nucleoid.packettweaker.PacketContext;

import java.util.List;
import java.util.Objects;

public class DisintegratorItem extends Item implements PolymerItem {
    public DisintegratorItem(Item.Properties properties) {
        super(properties);
    }

    @Override
    public Item getPolymerItem(ItemStack itemStack, PacketContext context) {
        return Items.ENDER_EYE;
    }

    @Nullable
    public ResourceLocation getPolymerItemModel(ItemStack stack, PacketContext context) {
        return getPolymerItem(stack, context).getDefaultInstance().get(DataComponents.ITEM_MODEL);
    }

    @Override
    @NotNull
    public InteractionResult use(Level level, Player player, InteractionHand interactionHand) {
        if (!(level instanceof ServerLevel serverLevel))
            return InteractionResult.PASS;

        var entities = serverLevel.getNearbyEntities(Fighter.class, TargetingConditions.DEFAULT, player, player.getBoundingBox().inflate(16));
        for (Fighter entity : entities) {
            entity.kill(serverLevel);
        }
        return entities.isEmpty() ? InteractionResult.PASS : InteractionResult.SUCCESS_SERVER;
    }

    @Override
    @NotNull
    public InteractionResult useOn(UseOnContext useOnContext) {
        return this.use(useOnContext.getLevel(), Objects.requireNonNull(useOnContext.getPlayer()), useOnContext.getHand());
    }

    @Override
    @NotNull
    public Component getName(ItemStack itemStack) {
        return Component.literal("Micro Fighter Disintegrator");
    }

    public void appendHoverText(ItemStack itemStack, TooltipContext tooltipContext, List<Component> list, TooltipFlag tooltipFlag) {
        list.add(Component.literal("Disintegrates all Micro Fighters"));
        list.add(Component.literal("in a 16 block radius"));
    }
}