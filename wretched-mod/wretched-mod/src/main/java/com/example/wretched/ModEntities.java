package com.example.wretched;

import com.example.wretched.entity.WretchedEntity;
import net.fabricmc.fabric.api.biome.v1.BiomeModifications;
import net.fabricmc.fabric.api.biome.v1.BiomeSelectors;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.entity.SpawnLocationTypes;
import net.minecraft.entity.SpawnRestriction;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import net.minecraft.world.Heightmap;

public final class ModEntities {
	private ModEntities() {}

	public static final EntityType<WretchedEntity> WRETCHED = Registry.register(
			Registries.ENTITY_TYPE,
			Identifier.of(WretchedMod.MOD_ID, "wretched"),
			EntityType.Builder.create(WretchedEntity::new, SpawnGroup.MONSTER)
					// Narrow enough (<1.0) to path through 1-block gaps.
					.dimensions(0.9f, 0.8f)
					.maxTrackingRange(8)
					.build("wretched")
	);

	public static void register() {
		FabricDefaultAttributeRegistry.register(WRETCHED, WretchedEntity.createWretchedAttributes());

		SpawnRestriction.register(
				WRETCHED,
				SpawnLocationTypes.ON_GROUND,
				Heightmap.Type.MOTION_BLOCKING_NO_LEAVES,
				WretchedEntity::canSpawn
		);

		// Weight 30, groups of 1-2, anywhere in the Overworld (the spawn rule keeps them underground).
		BiomeModifications.addSpawn(BiomeSelectors.foundInOverworld(), SpawnGroup.MONSTER, WRETCHED, 30, 1, 2);
	}
}
