package de.tomalbrc.microfighters.registry;

import com.mojang.datafixers.DataFixUtils;
import com.mojang.datafixers.types.Type;
import de.tomalbrc.microfighters.MicroFighters;
import de.tomalbrc.microfighters.entity.Fighter;
import eu.pb4.polymer.core.api.entity.PolymerEntityUtils;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityType;
import net.minecraft.SharedConstants;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.datafix.DataFixers;
import net.minecraft.util.datafix.fixes.References;
import net.minecraft.world.entity.*;
import net.minecraft.world.level.levelgen.Heightmap;

import java.util.Map;

public class MobRegistry {
    public static final EntityType<Fighter> FIGHTER = register(
            FabricEntityType.Builder.createMob(Fighter::new, MobCategory.CREATURE, x -> x
                    .defaultAttributes(Fighter::createAttributes)
                    .spawnRestriction(SpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, Mob::checkMobSpawnRules)
            ).sized(0.9f * MicroFighters.SCALE, 1.9f * MicroFighters.SCALE)
    );

    private static <T extends Entity> EntityType<T> register(EntityType.Builder<T> builder) {
        ResourceKey<EntityType<?>> key = ResourceKey.create(Registries.ENTITY_TYPE, Fighter.ID);
        EntityType<T> type = builder.build(key);
        PolymerEntityUtils.registerType(type);

        @SuppressWarnings("unchecked") Map<String, Type<?>> types = (Map<String, Type<?>>) DataFixers.getDataFixer().getSchema(DataFixUtils.makeKey(SharedConstants.getCurrentVersion().getDataVersion().getVersion())).findChoiceType(References.ENTITY).types();
        types.put(Fighter.ID.toString(), types.get(BuiltInRegistries.ENTITY_TYPE.getKey(EntityType.SKELETON).toString()));

        return Registry.register(BuiltInRegistries.ENTITY_TYPE, key, type);
    }

    public static void register() {
    }
}
