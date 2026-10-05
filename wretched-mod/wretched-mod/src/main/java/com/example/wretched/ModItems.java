package com.example.wretched;

import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroups;
import net.minecraft.item.SpawnEggItem;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public final class ModItems {
	private ModItems() {}

	public static final Item WRETCHED_DRILL_TIP = register("wretched_drill_tip", new Item(new Item.Settings()));
	public static final Item WRETCHED_SPAWN_EGG = register("wretched_spawn_egg",
			new SpawnEggItem(ModEntities.WRETCHED, 0x3b3a3b, 0xe53332, new Item.Settings()));

	private static Item register(String name, Item item) {
		return Registry.register(Registries.ITEM, Identifier.of(WretchedMod.MOD_ID, name), item);
	}

	public static void register() {
		ItemGroupEvents.modifyEntriesEvent(ItemGroups.INGREDIENTS).register(entries -> entries.add(WRETCHED_DRILL_TIP));
		ItemGroupEvents.modifyEntriesEvent(ItemGroups.SPAWN_EGGS).register(entries -> entries.add(WRETCHED_SPAWN_EGG));
	}
}
