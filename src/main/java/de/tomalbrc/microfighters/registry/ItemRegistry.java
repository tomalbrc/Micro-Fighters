package de.tomalbrc.microfighters.registry;

import de.tomalbrc.microfighters.MicroFighters;
import de.tomalbrc.microfighters.item.DisintegratorItem;
import de.tomalbrc.microfighters.item.FighterSpawnItem;
import eu.pb4.polymer.core.api.item.PolymerItemGroupUtils;
import it.unimi.dsi.fastutil.objects.Object2ObjectLinkedOpenHashMap;
import net.minecraft.ChatFormatting;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.DispenserBlock;

public class ItemRegistry {

    public static final Object2ObjectLinkedOpenHashMap<ResourceLocation, Item> CUSTOM_ITEMS;

    public static final CreativeModeTab ITEM_GROUP;

    static {
        CUSTOM_ITEMS = new Object2ObjectLinkedOpenHashMap<>();

        for (DyeColor color : DyeColor.values()) {
            register(color);
        }

        registerDisintegrator();

        ITEM_GROUP = new CreativeModeTab.Builder(null, -1)
                .title(Component.literal("Micro Fighters").withStyle(ChatFormatting.DARK_GRAY))
                .icon(Items.TERRACOTTA::getDefaultInstance)
                .displayItems((parameters, output) -> CUSTOM_ITEMS.forEach((key, value) -> output.accept(value)))
                .build();

        PolymerItemGroupUtils.registerPolymerItemGroup(ResourceLocation.fromNamespaceAndPath(MicroFighters.MOD_ID, "items"), ITEM_GROUP);
    }

    public static void register() {}

    static public void register(DyeColor color) {
        Item item = new FighterSpawnItem(color);
        ResourceLocation identifier = ResourceLocation.fromNamespaceAndPath(MicroFighters.MOD_ID, color.getName() + "_fighter");
        Registry.register(BuiltInRegistries.ITEM, identifier, item);
        CUSTOM_ITEMS.putIfAbsent(identifier, item);

        DispenserBlock.registerBehavior(item, FighterSpawnItem.DISPENSE_BEHAVIOUR);
    }

    static public void registerDisintegrator() {
        Item item = new DisintegratorItem();
        ResourceLocation identifier = ResourceLocation.fromNamespaceAndPath(MicroFighters.MOD_ID, "disintegrator");
        Registry.register(BuiltInRegistries.ITEM, identifier, item);
        CUSTOM_ITEMS.putIfAbsent(identifier, item);
    }
}