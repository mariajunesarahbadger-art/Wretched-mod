package com.example.wretched.entity;

import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.ai.goal.ActiveTargetGoal;
import net.minecraft.entity.ai.goal.LookAroundGoal;
import net.minecraft.entity.ai.goal.LookAtEntityGoal;
import net.minecraft.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.entity.ai.goal.RevengeGoal;
import net.minecraft.entity.ai.goal.SwimGoal;
import net.minecraft.entity.ai.goal.WanderAroundFarGoal;
import net.minecraft.entity.ai.pathing.EntityNavigation;
import net.minecraft.entity.ai.pathing.SpiderNavigation;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.ServerWorldAccess;
import net.minecraft.world.World;

/**
 * The Wretched: a segmented, wall-climbing centipede that lurks in dark caves.
 * Its bite saps your strength (Weakness).
 */
public class WretchedEntity extends HostileEntity {
	private static final TrackedData<Byte> CLIMBING_FLAGS =
			DataTracker.registerData(WretchedEntity.class, TrackedDataHandlerRegistry.BYTE);

	public WretchedEntity(EntityType<? extends WretchedEntity> type, World world) {
		super(type, world);
		this.experiencePoints = 8;
	}

	public static DefaultAttributeContainer.Builder createWretchedAttributes() {
		return HostileEntity.createHostileAttributes()
				.add(EntityAttributes.GENERIC_MAX_HEALTH, 22.0)
				.add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.27)
				.add(EntityAttributes.GENERIC_ATTACK_DAMAGE, 3.0)
				.add(EntityAttributes.GENERIC_ARMOR, 4.0)
				.add(EntityAttributes.GENERIC_FOLLOW_RANGE, 24.0);
	}

	/** Dark places only, and only below sea level (so: caves). Spawners ignore the depth rule. */
	public static boolean canSpawn(EntityType<WretchedEntity> type, ServerWorldAccess world,
								   SpawnReason reason, BlockPos pos, Random random) {
		return HostileEntity.canSpawnInDark(type, world, reason, pos, random)
				&& (reason == SpawnReason.SPAWNER || pos.getY() < world.getSeaLevel());
	}

	@Override
	protected void initGoals() {
		this.goalSelector.add(1, new SwimGoal(this));
		this.goalSelector.add(4, new MeleeAttackGoal(this, 1.1, true));
		this.goalSelector.add(5, new WanderAroundFarGoal(this, 0.8));
		this.goalSelector.add(6, new LookAtEntityGoal(this, PlayerEntity.class, 8.0f));
		this.goalSelector.add(6, new LookAroundGoal(this));
		this.targetSelector.add(1, new RevengeGoal(this));
		this.targetSelector.add(2, new ActiveTargetGoal<>(this, PlayerEntity.class, true));
	}

	// ---- wall climbing (same approach as the vanilla spider) ----

	@Override
	protected EntityNavigation createNavigation(World world) {
		return new SpiderNavigation(this, world);
	}

	@Override
	protected void initDataTracker(DataTracker.Builder builder) {
		super.initDataTracker(builder);
		builder.add(CLIMBING_FLAGS, (byte) 0);
	}

	@Override
	public void tick() {
		super.tick();
		if (!this.getWorld().isClient) {
			this.setClimbingWall(this.horizontalCollision);
		}
	}

	@Override
	public boolean isClimbing() {
		return this.isClimbingWall();
	}

	public boolean isClimbingWall() {
		return (this.dataTracker.get(CLIMBING_FLAGS) & 1) != 0;
	}

	public void setClimbingWall(boolean climbing) {
		byte flags = this.dataTracker.get(CLIMBING_FLAGS);
		flags = climbing ? (byte) (flags | 1) : (byte) (flags & ~1);
		this.dataTracker.set(CLIMBING_FLAGS, flags);
	}

	// ---- combat ----

	@Override
	public boolean tryAttack(Entity target) {
		if (!super.tryAttack(target)) {
			return false;
		}
		if (target instanceof LivingEntity living) {
			int seconds = switch (this.getWorld().getDifficulty()) {
				case EASY -> 3;
				case NORMAL -> 6;
				case HARD -> 10;
				default -> 0;
			};
			if (seconds > 0) {
				living.addStatusEffect(new StatusEffectInstance(StatusEffects.WEAKNESS, seconds * 20), this);
			}
		}
		return true;
	}

	// ---- sounds ----

	@Override
	protected SoundEvent getAmbientSound() {
		return SoundEvents.ENTITY_SILVERFISH_AMBIENT;
	}

	@Override
	protected SoundEvent getHurtSound(DamageSource source) {
		return SoundEvents.ENTITY_SILVERFISH_HURT;
	}

	@Override
	protected SoundEvent getDeathSound() {
		return SoundEvents.ENTITY_SILVERFISH_DEATH;
	}

	@Override
	protected void playStepSound(BlockPos pos, BlockState state) {
		this.playSound(SoundEvents.ENTITY_SPIDER_STEP, 0.15f, 1.0f);
	}
}
