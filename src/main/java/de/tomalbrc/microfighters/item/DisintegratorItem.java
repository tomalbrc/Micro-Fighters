package de.tomalbrc.microfighters.item;

import de.tomalbrc.microfighters.entity.Fighter;
import eu.pb4.polymer.core.api.item.PolymerItem;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Objects;

public class DisintegratorItem extends Item implements PolymerItem {
    public DisintegratorItem() {
        super(new Properties().stacksTo(1).rarity(Rarity.RARE));
    }

    @Override
    public Item getPolymerItem(ItemStack itemStack, @Nullable ServerPlayer serverPlayer) {
        return Items.ENDER_EYE;
    }

    @Override
    @NotNull
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand interactionHand) {
        ItemStack item = player.getItemInHand(interactionHand);
        var entities = level.getNearbyEntities(Fighter.class, TargetingConditions.DEFAULT, player, player.getBoundingBox().inflate(16));
        for (Fighter entity : entities) {
            entity.kill();
        }
        return entities.isEmpty() ? InteractionResultHolder.pass(item) : InteractionResultHolder.success(item);
    }

    @Override
    @NotNull
    public InteractionResult useOn(UseOnContext useOnContext) {
        return this.use(useOnContext.getLevel(), Objects.requireNonNull(useOnContext.getPlayer()), useOnContext.getHand()).getResult();
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