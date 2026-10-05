package com.example.wretched.client;

import com.example.wretched.WretchedMod;
import com.example.wretched.entity.WretchedEntity;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.entity.feature.EyesFeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.util.Identifier;

/** Draws the glowing red eyes fullbright, like the vanilla spider's. */
public class WretchedEyesFeatureRenderer extends EyesFeatureRenderer<WretchedEntity, WretchedEntityModel> {
	private static final RenderLayer EYES =
			RenderLayer.getEyes(Identifier.of(WretchedMod.MOD_ID, "textures/entity/wretched_eyes.png"));

	public WretchedEyesFeatureRenderer(FeatureRendererContext<WretchedEntity, WretchedEntityModel> context) {
		super(context);
	}

	@Override
	public RenderLayer getEyesTexture() {
		return EYES;
	}
}
