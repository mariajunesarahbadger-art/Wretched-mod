package com.example.wretched.client;

import com.example.wretched.ModEntities;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.EntityModelLayerRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;

public class WretchedModClient implements ClientModInitializer {
	@Override
	public void onInitializeClient() {
		EntityModelLayerRegistry.registerModelLayer(WretchedEntityModel.LAYER, WretchedEntityModel::getTexturedModelData);
		EntityRendererRegistry.register(ModEntities.WRETCHED, WretchedEntityRenderer::new);
	}
}
