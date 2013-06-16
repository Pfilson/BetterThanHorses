package net.minecraft.src;

import org.lwjgl.opengl.GL11;

public class BTHModelHorse extends ModelBase {
	
	public ModelRenderer head;
	public ModelRenderer neck;
	public ModelRenderer body;
	public ModelRenderer tail;
	public ModelRenderer legFrontLeft;
	public ModelRenderer legFrontRight;
	public ModelRenderer legRearLeft;
	public ModelRenderer legRearRight;
	
	// static final float headPitchDefault = 0.52F;
	// static final float tailPitchDefault = 0.79F;
	
	/** Forward tilt of the horse's head at full energy, in radians. */
	static final float headPitchMin = 0.20F;
	/** Forward tilt of the horse's head at 0 energy, in radians. */
	static final float headPitchMax = 1.70F;
	
	/** Upwards tilt of the tail for a horse in critical condition, in radians. */
	static final float tailPitchMin = 0.08F;
	/** Upwards tilt of the tail for a horse in optimal condition, in radians. */
	static final float tailPitchMax = 1.30F;
	
	/** Amount that horse heads turn sideways to look at the player, as a fraction of how far most mobs' heads will turn. */
	static final float lookYawRange = 0.5F;
	/** Amount that horse heads tilt up and down to look at the player, as a fraction of how far most mobs' heads will turn. */
	static final float lookPitchRange = 0.5F;
	
	/** Factor deciding how far horse heads can bob back and forth along the Z-axis. */
	static final float headBobRange = 0.1F;
	/** Factor deciding how far legs swing.  1.4F for most things in Minecraft. */
	static final float legSwingRange = 1.4F;
	/** Factor deciding how quickly limbs swing.  0.6662F for most things in Minecraft. */
	static final float swingRate = 0.6662F;
	
	public BTHModelHorse() {
		super();
		
		// Texture sheet parameters
		this.textureWidth = 128;
		this.textureHeight = 64;
		
		final int coordHeadU = 0, coordHeadV = 0;
		final int dimHeadX = 4, dimHeadY = 5, dimHeadZ = 12;
		
		final int coordEarLeftU  = 0, coordEarLeftV  = 0;
		final int coordEarRightU = 4, coordEarRightV = 0;
		final int dimEarX = 1, dimEarY = 2, dimEarZ = 1;
		
		final int coordNeckU = 32, coordNeckV = 0;
		final int dimNeckX = 4, dimNeckY = 11, dimNeckZ = 7;
		
		final int coordManeU = 54, coordManeV = 0;
		final int dimManeX = 2, dimManeY = 18, dimManeZ = 3;
		
		final int coordBodyU = 0, coordBodyV = 18;
		final int dimBodyX = 12, dimBodyY = 10, dimBodyZ = 22;
		
		final int coordTailU = 0, coordTailV = 18;
		final int dimTailX = 2, dimTailY = 11, dimTailZ = 3;
		
		final int coordLegU = 46, coordLegV = 22;
		final int dimLegX = 4, dimLegY = 12, dimLegZ = 4;
		
		this.head = new ModelRenderer(this, 0, 0);
		this.head.setRotationPoint(0.0F, 6.0F, -8.0F);
		this.head.setTextureOffset(coordHeadU, coordHeadV);
		this.head.addBox(-2.0F, -15.5F, -9.0F, dimHeadX, dimHeadY, dimHeadZ, 0);
		this.head.setTextureOffset(coordEarLeftU, coordEarLeftV);
		this.head.addBox(1.0F, -17.5F, 2.0F, dimEarX, dimEarY, dimEarZ, 0);
		this.head.setTextureOffset(coordEarRightU, coordEarRightV);
		this.head.addBox(-2.0F, -17.5F, 2.0F, dimEarX, dimEarY, dimEarZ, 0);
		
		this.neck = new ModelRenderer(this, 0, 0);
		this.neck.setRotationPoint(0.0F, 6.0F, -8.0F);
		this.neck.setTextureOffset(coordNeckU, coordNeckV);
		this.neck.addBox(-2.0F, -10.5F, -4.0F, dimNeckX, dimNeckY, dimNeckZ, 0);
		this.neck.setTextureOffset(coordManeU, coordManeV);
		this.neck.addBox(-1.0F, -16.5F, 2.0F, dimManeX, dimManeY, dimManeZ, 0);
		
		this.body = new ModelRenderer(this, coordBodyU, coordBodyV);
		this.body.setRotationPoint(0.0F, 8.0F, 0.0F);
		this.body.addBox(-6.0F, -5.0F, -11.0F, dimBodyX, dimBodyY, dimBodyZ, 0);
		
		this.tail = new ModelRenderer(this, coordTailU, coordTailV);
		this.tail.setRotationPoint(0.0F, 3.0F, 11.0F);
		this.tail.addBox(-1.0F, 0.0F, -3.0F, dimTailX, dimTailY, dimTailZ, 0);
		
		this.legFrontLeft = new ModelRenderer(this, coordLegU, coordLegV);
		this.legFrontLeft.setRotationPoint(4.0F, 12.0F, -9.0F);
		this.legFrontLeft.addBox(-2.0F, 0.0F, -2.0F, dimLegX, dimLegY, dimLegZ, 0);
		
		this.legFrontRight = new ModelRenderer(this, coordLegU, coordLegV);
		this.legFrontRight.setRotationPoint(-4.0F, 12.0F, -9.0F);
		this.legFrontRight.addBox(-2.0F, 0.0F, -2.0F, dimLegX, dimLegY, dimLegZ, 0);
		
		this.legRearLeft = new ModelRenderer(this, coordLegU, coordLegV);
		this.legRearLeft.setRotationPoint(4.0F, 12.0F, 10.0F);
		this.legRearLeft.addBox(-2.0F, 0.0F, -2.0F, dimLegX, dimLegY, dimLegZ, 0);
		
		this.legRearRight = new ModelRenderer(this, coordLegU, coordLegV);
		this.legRearRight.setRotationPoint(-4.0F, 12.0F, 10.0F);
		this.legRearRight.addBox(-2.0F, 0.0F, -2.0F, dimLegX, dimLegY, dimLegZ, 0);
	}
	
	
	
	@Override // ModelBase
	public void render(Entity entityHorse, float travelDistance, float travelSpeed, float f2, float headYaw, float headPitch, float scale) {
		this.setRotationAngles(travelDistance, travelSpeed, f2, headYaw, headPitch, scale, entityHorse);
		
		if (this.isChild) {
			// Babies render with big heads and smaller bodies.
			float babyBodyScale = 0.5F;
			
			// The baby's head location must be adjusted to the proper location for its new, smaller body.
			float babyHeadOffsetY = 9.0F;
			float babyHeadOffsetZ = 5.5F;
			
			// Big head
			GL11.glPushMatrix();
			GL11.glTranslatef(0.0F, babyHeadOffsetY * scale, babyHeadOffsetZ * scale);
			this.head.render(scale);
			this.neck.render(scale);
			GL11.glPopMatrix();
			
			// Little body
			GL11.glPushMatrix();
			GL11.glScalef(babyBodyScale, babyBodyScale, babyBodyScale);
			GL11.glTranslatef(0.0F, 24.0F * scale, 0.0F);
			this.body.render(scale);
			this.tail.render(scale);
			this.legFrontLeft.render(scale);
			this.legFrontRight.render(scale);
			this.legRearLeft.render(scale);
			this.legRearRight.render(scale);
			GL11.glPopMatrix();
		} // end if child
		else {
			// Make head bob
			float headOffsetZ = headBobRange * travelSpeed * MathHelper.cos(travelDistance * swingRate + 1.3333F * (float)Math.PI); 
			
			GL11.glPushMatrix();
			GL11.glTranslatef(0.0F, 0.0F, headOffsetZ);
			this.head.render(scale);
			this.neck.render(scale);
			GL11.glPopMatrix();
			
			this.body.render(scale);
			this.tail.render(scale);
			this.legFrontLeft.render(scale);
			this.legFrontRight.render(scale);
			this.legRearLeft.render(scale);
			this.legRearRight.render(scale);
		}
	}
	
	@Override // ModelBase
	public void setRotationAngles(float travelDistance, float travelSpeed, float par3, float headYaw, float headPitch, float scale, Entity entity) {
		final float radiansPerDegree = 0.017453292F;
		
		BTHEntityHorse horse = (BTHEntityHorse)entity;
		boolean isGalloping  = horse.isGalloping();
		float energyFraction = (float)horse.getStamina() / horse.cMaxStamina;
		float foodFraction   = (float)horse.getHunger() / horse.cMaxHunger;
		
		// Walking:   Legs swing as all mobs' legs do.
		// Galloping: Leg movement more closely resembles a galloping horse (1-2-3-4-...-1-2-3-4-...)
		float swingPhase = travelDistance * swingRate;
		float legSwingExtent = travelSpeed * legSwingRange;
		if (isGalloping) {
			this.legFrontLeft.rotateAngleX  = legSwingExtent * MathHelper.cos(swingPhase + (float)Math.PI * 0.3333F);
			this.legFrontRight.rotateAngleX = legSwingExtent * MathHelper.cos(swingPhase);
			this.legRearLeft.rotateAngleX   = legSwingExtent * MathHelper.cos(swingPhase + (float)Math.PI);
			this.legRearRight.rotateAngleX  = legSwingExtent * MathHelper.cos(swingPhase + (float)Math.PI * 0.6667F);
		}
		else {
			this.legFrontLeft.rotateAngleX  = legSwingExtent * MathHelper.cos(swingPhase);
			this.legFrontRight.rotateAngleX = legSwingExtent * MathHelper.cos(swingPhase + (float)Math.PI);
			this.legRearLeft.rotateAngleX   = legSwingExtent * MathHelper.cos(swingPhase + (float)Math.PI);
			this.legRearRight.rotateAngleX  = legSwingExtent * MathHelper.cos(swingPhase);
		}
		
		// The movement range of the head is restricted a bit.
		float reducedHeadYaw   = headYaw * radiansPerDegree * lookYawRange;
		float reducedHeadPitch = headPitch * radiansPerDegree * lookPitchRange;
		// The forward tilt of the head also reflects the horse's energy.
		reducedHeadPitch += headPitchMax + energyFraction * (headPitchMin - headPitchMax);
		
		// The neck and head must rotate together.  
		this.head.rotateAngleX = reducedHeadPitch;
		this.neck.rotateAngleX = reducedHeadPitch;
		this.head.rotateAngleY = reducedHeadYaw;
		this.neck.rotateAngleY = reducedHeadYaw;
		
		// Tail angle depends on hunger
		this.tail.rotateAngleX = tailPitchMin + foodFraction * (tailPitchMax - tailPitchMin); 
	}
}
