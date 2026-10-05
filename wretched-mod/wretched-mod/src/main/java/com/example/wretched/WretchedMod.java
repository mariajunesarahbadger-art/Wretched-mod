package com.example.wretched;

import net.fabricmc.api.ModInitializer;

public class WretchedMod implements ModInitializer {
	public static final String MOD_ID = "wretched";

	@Override
	public void onInitialize() {
		ModEntities.register();
		ModItems.register();
	}
}
