package de.tomalbrc.microfighters.registry;

import de.tomalbrc.microfighters.MicroFighters;
import de.tomalbrc.microfighters.entity.Fighter;
import eu.pb4.polymer.core.api.entity.PolymerEntityUtils;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityType;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.*;
import net.minecraft.world.level.levelgen.Heightmap;

public class MobRegistry {
    public static final EntityType<Fighter> FIGHTER = register(
            FabricEntityType.Builder.createMob(Fighter::new, MobCategory.CREATURE, x -> x
                    .defaultAttributes(Fighter::createAttributes)
                    .spawnRestriction(SpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, Mob::checkMobSpawnRules)
            ).sized(0.9f * MicroFighters.SCALE, 1.9f * MicroFighters.SCALE)
    );

    private static <T extends Entity> EntityType<T> register(EntityType.Builder<T> builder) {
        EntityType<T> type = builder.build(ResourceKey.create(Registries.ENTITY_TYPE, ResourceLocation.fromNamespaceAndPath("", "")));
        PolymerEntityUtils.registerType(type);
        return Registry.register(BuiltInRegistries.ENTITY_TYPE, Fighter.ID, type);
    }

    public static void register() {
    }
}
