package de.tomalbrc.microfighters.item;

import de.tomalbrc.microfighters.entity.Fighter;
import eu.pb4.polymer.core.api.item.PolymerItem;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import xyz.nucleoid.packettweaker.PacketContext;

import java.util.List;
import java.util.Objects;

public class DisintegratorItem extends Item implements PolymerItem {
    public DisintegratorItem(Properties properties) {
        super(properties.stacksTo(1).rarity(Rarity.RARE));
    }

    @Override
    public Item getPolymerItem(ItemStack itemStack, PacketContext context) {
        return Items.ENDER_EYE;
    }

    @Override
    @NotNull
    public InteractionResult use(Level level, Player player, InteractionHand interactionHand) {
        var entities = ((ServerLevel)level).getNearbyEntities(Fighter.class, TargetingConditions.DEFAULT, player, player.getBoundingBox().inflate(16));
        for (Fighter entity : entities) {
            entity.discard();
        }
        return entities.isEmpty() ? InteractionResult.PASS : InteractionResult.SUCCESS;
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