package com.example.wretched.client;

import com.example.wretched.WretchedMod;
import com.example.wretched.entity.WretchedEntity;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.MobEntityRenderer;
import net.minecraft.util.Identifier;

public class WretchedEntityRenderer extends MobEntityRenderer<WretchedEntity, WretchedEntityModel> {
	private static final Identifier TEXTURE = Identifier.of(WretchedMod.MOD_ID, "textures/entity/wretched.png");

	public WretchedEntityRenderer(EntityRendererFactory.Context context) {
		super(context, new WretchedEntityModel(context.getPart(WretchedEntityModel.LAYER)), 0.6f);
		this.addFeature(new WretchedEyesFeatureRenderer(this));
	}

	@Override
	public Identifier getTexture(WretchedEntity entity) {
		return TEXTURE;
	}
}
