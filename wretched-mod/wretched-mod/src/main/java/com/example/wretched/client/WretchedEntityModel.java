package com.example.wretched.client;

import com.example.wretched.WretchedMod;
import com.example.wretched.entity.WretchedEntity;
import net.minecraft.client.model.ModelData;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.model.ModelPartBuilder;
import net.minecraft.client.model.ModelPartData;
import net.minecraft.client.model.ModelTransform;
import net.minecraft.client.model.TexturedModelData;
import net.minecraft.client.render.entity.model.EntityModelLayer;
import net.minecraft.client.render.entity.model.SinglePartEntityModel;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;

/**
 * Blocky centipede: head -> 4 body segments -> 3-step drill tail, each segment parented to the one
 * before it so the whole body can ripple like a snake. Model space: +Z is the tail, -Z is the face.
 */
public class WretchedEntityModel extends SinglePartEntityModel<WretchedEntity> {
	public static final EntityModelLayer LAYER =
			new EntityModelLayer(Identifier.of(WretchedMod.MOD_ID, "wretched"), "main");

	private final ModelPart root;
	private final ModelPart jawR;
	private final ModelPart jawL;
	private final ModelPart[] legged; // parts that carry a leg pair: head, seg1..seg4
	private final ModelPart[] chain;  // everything that ripples: head..drill2

	public WretchedEntityModel(ModelPart root) {
		this.root = root;
		ModelPart head = root.getChild("head");
		this.jawR = head.getChild("jaw_r");
		this.jawL = head.getChild("jaw_l");
		ModelPart seg1 = head.getChild("seg1");
		ModelPart seg2 = seg1.getChild("seg2");
		ModelPart seg3 = seg2.getChild("seg3");
		ModelPart seg4 = seg3.getChild("seg4");
		ModelPart drill1 = seg4.getChild("drill1");
		ModelPart drill2 = drill1.getChild("drill2");
		this.legged = new ModelPart[] { head, seg1, seg2, seg3, seg4 };
		this.chain = new ModelPart[] { head, seg1, seg2, seg3, seg4, drill1, drill2 };
	}

	public static TexturedModelData getTexturedModelData() {
		ModelData data = new ModelData();
		ModelPartData root = data.getRoot();

		// Head: pivot at its back-bottom edge (y=20 is 4px above the ground at y=24; legs cover the gap).
		ModelPartData head = root.addChild("head",
				ModelPartBuilder.create().uv(0, 0).cuboid(-4f, -8f, -8f, 8f, 8f, 8f),
				ModelTransform.pivot(0f, 20f, -12f));
		ModelPartBuilder jaw = ModelPartBuilder.create().uv(48, 16).cuboid(-1.5f, -1.5f, -2f, 3f, 3f, 2f);
		head.addChild("jaw_r", jaw, ModelTransform.pivot(-2f, -1.5f, -8f));
		head.addChild("jaw_l", jaw, ModelTransform.pivot(2f, -1.5f, -8f));
		addLegs(head, 4f, -4f);

		// Each child pivots at the joint (front edge) of its own segment, i.e. the back edge of its parent.
		ModelPartData seg1 = head.addChild("seg1",
				ModelPartBuilder.create().uv(32, 0).cuboid(-3.5f, -7f, 0f, 7f, 7f, 7f),
				ModelTransform.pivot(0f, 0f, 0f));
		addLegs(seg1, 3.5f, 3.5f);

		ModelPartData seg2 = seg1.addChild("seg2",
				ModelPartBuilder.create().uv(0, 16).cuboid(-3f, -6f, 0f, 6f, 6f, 6f),
				ModelTransform.pivot(0f, 0f, 7f));
		addLegs(seg2, 3f, 3f);

		ModelPartData seg3 = seg2.addChild("seg3",
				ModelPartBuilder.create().uv(24, 16).cuboid(-3f, -6f, 0f, 6f, 6f, 6f),
				ModelTransform.pivot(0f, 0f, 6f));
		addLegs(seg3, 3f, 3f);

		ModelPartData seg4 = seg3.addChild("seg4",
				ModelPartBuilder.create().uv(0, 32).cuboid(-2.5f, -5f, 0f, 5f, 5f, 5f),
				ModelTransform.pivot(0f, 0f, 6f));
		addLegs(seg4, 2.5f, 2.5f);

		// Stepped drill tail, vertically centred on the last segment.
		ModelPartData drill1 = seg4.addChild("drill1",
				ModelPartBuilder.create().uv(20, 32).cuboid(-2f, -4.5f, 0f, 4f, 4f, 3f),
				ModelTransform.pivot(0f, 0f, 5f));
		ModelPartData drill2 = drill1.addChild("drill2",
				ModelPartBuilder.create().uv(34, 32).cuboid(-1.5f, -4f, 0f, 3f, 3f, 3f),
				ModelTransform.pivot(0f, 0f, 3f));
		drill2.addChild("drill3",
				ModelPartBuilder.create().uv(46, 32).cuboid(-0.5f, -3f, 0f, 1f, 1f, 2f),
				ModelTransform.pivot(0f, 0f, 3f));

		return TexturedModelData.of(data, 64, 64);
	}

	/** One splayed leg on each side of a segment. All legs share one 2x6x2 texture patch. */
	private static void addLegs(ModelPartData parent, float halfWidth, float z) {
		ModelPartBuilder leg = ModelPartBuilder.create().uv(52, 32).cuboid(-1f, 0f, -1f, 2f, 6f, 2f);
		parent.addChild("leg_r", leg, ModelTransform.of(-halfWidth, -1f, z, 0f, 0f, 0.6f));
		parent.addChild("leg_l", leg, ModelTransform.of(halfWidth, -1f, z, 0f, 0f, -0.6f));
	}

	@Override
	public void setAngles(WretchedEntity entity, float limbAngle, float limbDistance,
						  float animationProgress, float headYaw, float headPitch) {
		float idle = animationProgress * 0.12f;
		float walk = limbAngle * 0.8f;
		float move = Math.min(limbDistance, 1.0f);

		// Body ripple: a faint idle sway plus a bigger travelling wave while moving.
		for (int i = 0; i < chain.length; i++) {
			float lag = i * 0.8f;
			chain[i].yaw = MathHelper.sin(idle - lag) * 0.05f + MathHelper.sin(walk - lag) * 0.28f * move;
		}

		// Legs scuttle in alternating waves down the body.
		for (int i = 0; i < legged.length; i++) {
			float phase = i * 0.9f;
			legged[i].getChild("leg_r").pitch = MathHelper.cos(walk * 2.0f + phase) * 0.9f * move;
			legged[i].getChild("leg_l").pitch = MathHelper.cos(walk * 2.0f + phase + MathHelper.PI) * 0.9f * move;
		}

		// Mandibles twitch at rest and snap when it has a target.
		float chomp = entity.isAttacking()
				? 0.35f + MathHelper.sin(animationProgress * 0.9f) * 0.25f
				: 0.12f + MathHelper.sin(animationProgress * 0.12f) * 0.05f;
		jawR.yaw = chomp;
		jawL.yaw = -chomp;
	}

	@Override
	public ModelPart getPart() {
		return root;
	}
}
