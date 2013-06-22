package net.minecraft.src;

import java.util.Iterator;
import java.util.List;

public class BTHEntityHorse extends EntityAnimal
{
	/**The list of items and the amount of hunger restored by them*/
	public static int cHungerRestoredItem[] = new int[32000];
	
	//TODO: change these to balanced numbers. Keep in mind that for comparison, the player entity has 20 health, 20 hunger, and 20 fat (not sure on that last one though...)
	/**The maximum hunger the for the horse*/
	public static final int cMaxHunger = 20;
	
	/**The maximum fat storage for the horse*/
	public static final int cMaxFat = 20;
	
	/**The maximum stamina (energy, whatever you want to call it) for the horse*/
	public static final int cMaxStamina = 20;
	
	/**The maximum health for the horse (in half hearts)*/
	public static final int cMaxHealth = 20;
	
	/**The health at which the horse is considered to be lamed (in half hearts)*/
	private static final int cLamedHealth = 0;
	
	/**The health at which the horse is considered to be crippled (in half hearts)*/
	private static final int cCrippledHealth = 0;
	
	/**The amount of fat needed to lame the horse*/
	private static final int cLamedFat = 20;
	
	/**The amount of fat needed to cripple the horse*/
	private static final int cCrippledFat = 20;
	
	//These may be breeding variables, in which case cMaxCrippledWeight and cMaxLamedWeight will be floored frations of
	//cMaxNormalWeight. Represented by an overall strength/endurance variable if it is heritable.
	private static final int cMaxCrippledWeight = 100;
	private static final int cMaxLamedWeight = 100;
	private static final int cMaxNormalWeightDamage = 100;//Damage the horse and throw the rider at this weight
	private static final int cMaxNormalWeightThrow = 100;//Throw the rider at this weight
	
	//TODO: set this to the proper level. Right now it is -16 so test horses don't run away from the player
	/**The default range at which the horse will tolerate players, in 1/16 meter increments*/
	private static final byte cAversionPlayerDefault = -16;
	
	/**The default range at which the horse will tolerate undead, in 1/16 meter increments*/
	private static final byte cAversionUndeadDefault = 80;
	
	/**The default range at which the horse will tolerate fire, in 1/16 meter increments*/
	private static final byte cAversionFireDefault = 80;
	
	/**The amount of hunger restored by eating grass*/
	private static final int cHungerRestoredGrass = 2;
	
	/**The delay between healing one half heart, measured in ticks (20 ticks = 1 second)*/
	private static final int cHealingDelay = 20;
	
	/**The distance (in blocks/meters) that a horse will search for grass and items to eat*/
	private static final double cFoodSearchRange = 5.0D;
	
	/**The range in which a horse will eat food off of the ground, squared*/
	private static final double cFoodEatRangeSq = 1.0D;
	
	/**The range in which a male horse will attempt to fight other male horses*/
	private static final double cMaleFightingRange = 5.0D;
	
	//The datawatcher constants. Note that the max id a datawatcher can have is 31
	private static final int cDWHunger = 25;
	private static final int cDWFat = 26;
	private static final int cDWStamina = 27;
	private static final int cDWAversionPlayer = 29;
	private static final int cDWAversionUndead = 30;
	private static final int cDWAversionFire = 31;
	
	//Available flag numbers are 6 and 7
	private static final int cGenderFlag = 7;
	
	public BTHEntityHorse(World par1World)
	{
		super(par1World);
		this.texture = "/bthmodtex/horse.png"; //Feel free to change where the horse texture is stored
		this.setSize(1.5F, 2.0F); //Feel free to adjust these, they were just a rough estimate
		this.getNavigator().setAvoidsWater(true);
		this.stepHeight = 1.0F;
		
		//The new AI
		this.tasks.addTask(0, new EntityAISwimming(this));
		this.tasks.addTask(1, new FCEntityAIAnimalFlee(this, 0.38F));
		this.tasks.addTask(2, new EntityAIMate(this, 0.2F));
		//Not quite what is described in the design doc, as this makes a child follow any adult
		//Might not be too hard to modify, but will require saving the actual parent to the nbt
		this.tasks.addTask(4, new EntityAIFollowParent(this, 0.25F));
		this.tasks.addTask(5, new EntityAIWander(this, 0.2F));
		//TODO:May need to scrap or modify so that it doesn't execute while the player is riding
		//the horse, cause that looks really weird.
		this.tasks.addTask(6, new EntityAIWatchClosest(this, EntityPlayer.class, 6.0F));
		this.tasks.addTask(7, new EntityAILookIdle(this));
	}
	
	@Override
	public boolean isAIEnabled()
	{
		return true;
	}
	
	@Override
	public int getMaxHealth()
	{
		return this.cMaxHealth;
	}
	
	@Override
	protected void entityInit()
	{
		super.entityInit();
		
		this.dataWatcher.addObject(cDWHunger, Byte.valueOf((byte)cMaxHunger));
		this.dataWatcher.addObject(cDWFat, Byte.valueOf((byte)0));
		this.dataWatcher.addObject(cDWStamina, Byte.valueOf((byte)cMaxStamina));
		
		//Make half the horses male when spawning
		if (rand.nextBoolean()) setIsMale(true);
		
		this.dataWatcher.addObject(cDWAversionPlayer, Byte.valueOf((byte)cAversionPlayerDefault));
		this.dataWatcher.addObject(cDWAversionUndead, Byte.valueOf((byte)cAversionUndeadDefault));
		this.dataWatcher.addObject(cDWAversionFire, Byte.valueOf((byte)cAversionFireDefault));
	}
	
	/**
	 * (abstract) Protected helper method to write subclass entity data to NBT.
	 */
	@Override
	public void writeEntityToNBT(NBTTagCompound par1NBTTagCompound)
	{
		super.writeEntityToNBT(par1NBTTagCompound);
		par1NBTTagCompound.setInteger("Hunger", this.getHunger());
		par1NBTTagCompound.setInteger("Fat", this.getFat());
		par1NBTTagCompound.setInteger("Stamina", this.getStamina());
		par1NBTTagCompound.setInteger("AversionPlayer", this.getStamina());
		par1NBTTagCompound.setInteger("AversionUndead", this.getStamina());
		par1NBTTagCompound.setInteger("AversionFire", this.getStamina());
	}
	
	/**
	 * (abstract) Protected helper method to read subclass entity data from NBT.
	 */
	@Override
	public void readEntityFromNBT(NBTTagCompound par1NBTTagCompound)
	{
		super.readEntityFromNBT(par1NBTTagCompound);
		this.setHunger(par1NBTTagCompound.getInteger("Hunger"));
		this.setHunger(par1NBTTagCompound.getInteger("Fat"));
		this.setHunger(par1NBTTagCompound.getInteger("Stamina"));
		this.setHunger(par1NBTTagCompound.getInteger("AversionPlayer"));
		this.setHunger(par1NBTTagCompound.getInteger("AversionUndead"));
		this.setHunger(par1NBTTagCompound.getInteger("AversionFire"));
	}
	
	//TODO: Need to add custom sounds
	@Override
	protected String getLivingSound()
	{
		return null;
	}
	
	@Override
	protected String getHurtSound()
	{
		return null;
	}
	
	@Override
	protected String getDeathSound()
	{
		return null;
	}
	
	@Override
	protected void playStepSound(int par1, int par2, int par3, int par4) {}
	
	//TODO: Saddling the horse, feeding the horse items, etc.
	@Override
	public boolean interact(EntityPlayer aPlayer)
	{
		if (super.interact(aPlayer))
		{
			return true;
		}
		else if (!this.worldObj.isRemote && (this.riddenByEntity == null || this.riddenByEntity == aPlayer))
		{
			aPlayer.mountEntity(this);
			return true;
		}
		else
		{
			return false;
		}
	}
	
	//TODO: Change to horse drops
	@Override
	protected int getDropItemId()
	{
		return 0;
	}
	
	//TODO: Change to horse drops
	@Override
	protected void dropFewItems(boolean par1, int par2) {}
	
	/**
	 * Called when the mob is falling. Calculates and applies fall damage.
	 */
	//I'm willing to work with this method. At the moment it just lowers the threshold for falling damage while
	//galloping so that falling at least 2 meters/blocks will cause damage.
	@Override
	protected void fall(float aDistanceFallen)
	{
		if (isGalloping()) aDistanceFallen += 1.5F; //If galloping, take damage after 1.5 meters
		else aDistanceFallen += 0.5F; //Otherwise, take damage after 2.5 meters
		
		//I'm considering making fall damage exponential after this point, to make horses even more susceptible to fall damage
		
		super.fall(aDistanceFallen);
	}
	
	/**
	 * This function is used when two same-species animals in 'love mode' breed to generate the new baby animal.
	 */
	public BTHEntityHorse spawnBabyAnimal(EntityAgeable par1EntityAgeable)
	{
		return new BTHEntityHorse(this.worldObj);
	}
	
	/**
	 * Checks if the parameter is an item which this animal can be fed to breed it (wheat, carrots or seeds depending on
	 * the animal type)
	 */
	//TODO: add horse breeding items, if applicable
	@Override
	public boolean isBreedingItem(ItemStack aItemStack) 
	{
		return false;
	}
	
	public EntityAgeable createChild(EntityAgeable par1EntityAgeable)
	{
		return this.spawnBabyAnimal(par1EntityAgeable);
	}
	
	/**A method which returns true if the horse is male, using the other flags object
	 * 
	 * @return true if male, false if female
	 */
	public boolean getIsMale()
	{
		return this.getFlag(cGenderFlag);
	}
	
	/**A method to set the isMale flag.
	 * 
	 * @param aIsMale - whether to set the horse as a male (true) or as a female (false)
	 */
	public void setIsMale(boolean aIsMale)
	{
		this.setFlag(cGenderFlag, aIsMale);
	}
	
	/**A method to determine the current health status of the horse. Returns a 0 if normal, a 1 if lamed, and a 2
	 * if crippled.
	 * 
	 * @return the health status code for the horse
	 */
	public int getHealthStatus()
	{
		if (this.getHealth() <= cCrippledHealth || this.getFat() >= cCrippledFat) return 2;
		else if (this.getHealth() <= cLamedHealth || this.getFat() >= cLamedFat) return 1;
		else return 0;
	}
	
	public void setHunger(int aHunger)
	{
		this.dataWatcher.updateObject(cDWHunger, Byte.valueOf((byte)aHunger));
	}
	
	public int getHunger()
	{
		return this.dataWatcher.getWatchableObjectByte(cDWHunger);
	}
	
	/**A method used to adjust the hunger level.
	 * 
	 * @param aAmountAdjusted - Can be either positive, to increase the amount of hunger, or negative, to decrease
	 * the amount of hunger.
	 */
	public void adjustHunger(int aAmountAdjusted)
	{
		int lOldHunger = this.getHunger();
		int lNewHunger = lOldHunger + aAmountAdjusted;
		
		if (lNewHunger > this.cMaxHunger)
		{
			this.adjustFat(lNewHunger - cMaxHunger);
			if (lOldHunger != this.cMaxHunger) this.setHunger(this.cMaxHunger);
		}
		else if (lNewHunger < 0)
		{
			//TODO: this.starvingCode
			if (lOldHunger != 0) this.setHunger(0);
		}
		else this.setHunger(lNewHunger);
	}
	
	public void setFat(int aFat)
	{
		this.dataWatcher.updateObject(cDWFat, Byte.valueOf((byte)aFat));
	}
	
	public int getFat()
	{
		return this.dataWatcher.getWatchableObjectByte(cDWFat);
	}
	
	/**A method used to adjust the fat level.
	 * 
	 * @param aAmountAdjusted - Can be either positive, to increase the amount of fat, or negative, to decrease
	 * the amount of fat.
	 */
	public void adjustFat(int aAmountAdjusted)
	{
		int lOldFat = this.getFat();
		int lNewFat = lOldFat + aAmountAdjusted;
		
		if (lNewFat > this.cMaxFat)
		{
			//TODO: this.overfeedingCode
			if (lOldFat != this.cMaxFat) this.setFat(this.cMaxFat);
		}
		else if (lNewFat < 0)
			{
				this.adjustHunger(lNewFat);
				if (lOldFat != 0) this.setFat(0);
			}
		else this.setFat(lNewFat);
	}
	
	public void setStamina(int aStamina)
	{
		this.dataWatcher.updateObject(cDWStamina, Byte.valueOf((byte)aStamina));
	}
	
	public int getStamina()
	{
		return this.dataWatcher.getWatchableObjectByte(cDWStamina);
	}
	
	/**Sets the horse's aversion level towards the player, in meters.
	 * 
	 * @param aAversion - the aversion level to assign to the horse, in meters. Max value of 7.9375, 
	 * min value of -8.0.
	 */
	public void setAversionPlayer(float aAversion)
	{
		dataWatcher.updateObject(cDWAversionPlayer, Byte.valueOf((byte)MathHelper.floor_float(aAversion * 16)));
	}
	
	public float getAversionPlayer()
	{
		return dataWatcher.getWatchableObjectByte(cDWAversionPlayer) / 16;
	}
	
	/**Sets the horse's aversion level towards the undead, in meters.
	 * 
	 * @param aAversion - the aversion level to assign to the horse, in meters. Max value of 7.9375, 
	 * min value of -8.0.
	 */
	public void setAversionUndead(float aAversion)
	{
		dataWatcher.updateObject(cDWAversionUndead, Byte.valueOf((byte)MathHelper.floor_float(aAversion * 16)));
	}
	
	public float getAversionUndead()
	{
		return dataWatcher.getWatchableObjectByte(cDWAversionUndead) / 16;
	}
	
	/**Sets the horse's aversion level towards fire, in meters.
	 * 
	 * @param aAversion - the aversion level to assign to the horse, in meters. Max value of 7.9375, 
	 * min value of -8.0.
	 */
	public void setAversionFire(float aAversion)
	{
		dataWatcher.updateObject(cDWAversionFire, Byte.valueOf((byte)MathHelper.floor_float(aAversion * 16)));
	}
	
	public float getAversionFire()
	{
		return dataWatcher.getWatchableObjectByte(cDWAversionFire) / 16;
	}
	
	/**This method gets the total weight being applied to the horse, only taking into account player armor.
	 * 
	 * @param aRider - the player riding the horse, null if there isn't one
	 */
	//TODO: may add player and horse fat levels as considerations in this method
	public int getTotalHorseLoad(EntityPlayer aRider)
	{	
		if (aRider == null) return 0;
		else return MathHelper.floor_float((aRider.GetArmorExhaustionModifier() - 1) * 44.0F);
	}
	
	//TODO: Possibly change this, maybe not.
	public boolean isGalloping()
	{
		return this.isSprinting();
	}
	
	@Override
	public float getSpeedModifier()
	{
		float lSpeedModifier = 1.0F;
		
		if (this.isPotionActive(Potion.moveSlowdown))
		{
			lSpeedModifier *= 1.0F - 0.15F * (float)(this.getActivePotionEffect(Potion.moveSlowdown).getAmplifier() + 1);
		}
		
		if (this.onGround && this.IsAffectedByMovementModifiers())
		{
			int lX = MathHelper.floor_double(this.posX);
			int lY = MathHelper.floor_double(this.posY - 0.03D - (double)this.yOffset);
			int lZ = MathHelper.floor_double(this.posZ);
			
			int lBlockID = this.worldObj.getBlockId(lX, lY, lZ);
			int lBlockIDAbove = this.worldObj.getBlockId(lX, lY + 1, lZ);
			Block lBlock = Block.blocksList[lBlockID];
			
			//Speed penalty for snow and webs
			if (lBlockIDAbove == Block.snow.blockID || lBlockIDAbove == Block.web.blockID)
			{
				lSpeedModifier *= 0.8F;
			}
			//Ignore sand and gravel
			else if (lBlock != null && lBlockID != Block.sand.blockID && lBlockID != Block.gravel.blockID)
			{
				lSpeedModifier *= lBlock.GetMovementModifier(this.worldObj, lX, lY, lZ);
			}
			
			lSpeedModifier *= this.GetHealthAndExhaustionModifier();
		}
		
		if (lSpeedModifier < 0.0F) lSpeedModifier = 0.0F;
		
		return lSpeedModifier;
	}
	
	@Override
	public void setInWeb() {} //Makes it so that horses don't get stuck in webs
	
	@Override
	protected void doBlockCollisions()
	{
		int lMinX = MathHelper.floor_double(this.boundingBox.minX + 0.001D);
		int lMinY = MathHelper.floor_double(this.boundingBox.minY + 0.001D);
		int lMinZ = MathHelper.floor_double(this.boundingBox.minZ + 0.001D);
		int lMaxX = MathHelper.floor_double(this.boundingBox.maxX - 0.001D);
		int lMaxY = MathHelper.floor_double(this.boundingBox.maxY - 0.001D);
		int lMaxZ = MathHelper.floor_double(this.boundingBox.maxZ - 0.001D);
		
		if (this.worldObj.checkChunksExist(lMinX, lMinY, lMinZ, lMaxX, lMaxY, lMaxZ))
		{
			for (int iX = lMinX; iX <= lMaxX; ++iX)
			{
				for (int iY = lMinY; iY <= lMaxY; ++iY)
				{
					for (int iZ = lMinZ; iZ <= lMaxZ; ++iZ)
					{
						int lBlockID = this.worldObj.getBlockId(iX, iY, iZ);
						
						if (lBlockID > 0)
						{
							//This makes the horse ignore flowers and tall grass as being speed penalties
							if (!(Block.blocksList[lBlockID] instanceof BlockFlower))
							{
								Block.blocksList[lBlockID].onEntityCollidedWithBlock(this.worldObj, iX, iY, iZ, this);
							}
						}
					}
				}
			}
		}
	}
	
	//Used to make it look like the player is riding the horse rather than floating over it
	@Override
	public double getMountedYOffset()
	{
		return (double)this.height * 0.6D;//Feel free to tweak a bit
	}
	
	@Override
	public void onLivingUpdate()
	{
		boolean lIsTasking = false;
		int lHealthStatus = this.getHealthStatus();
		
		EntityPlayer lRider = null;
		if (riddenByEntity != null && riddenByEntity instanceof EntityPlayer) lRider = (EntityPlayer)riddenByEntity;
		
		//Used to prevent crippled horses from moving
		if (lHealthStatus == 2) lIsTasking = true;
		
		//The 'avoid threats' section
		//TODO: Still need to add condition so that horses will attack undead if the aversion is negative
		//TODO: Need to make aversion slowly decrease when the horse is exposed to its source
		if (!lIsTasking)
		{
			double lClosestThreatX = 0;
			double lClosestThreatY = 0;
			double lClosestThreatZ = 0;
			boolean lIsAfraidOfThreat = true;
			Entity lEntityToAttack = null;
			float lClosestThreatDistanceSq = Float.POSITIVE_INFINITY;
			
			//Note, will run away even if it can't see the entity, possibly a problem, but low priority
			if (getAversionUndead() != 0)
			{
				List var1 = this.worldObj.getEntitiesWithinAABB(EntityZombie.class, this.boundingBox.expand(Math.abs(getAversionUndead()), 3.0D, Math.abs(getAversionUndead())));
				if (var1 != null && var1.size() != 0)
				{
					Entity lEntity = (Entity)var1.get(0);
					
					//Not neccesary, as this is the first 'threat' checked. Only here for demonstration/consistency
					//if (this.getDistanceSqToEntity(lEntity) < lClosestThreatDistanceSq)
					{
						lClosestThreatDistanceSq = (float)this.getDistanceSqToEntity(lEntity);
						lClosestThreatX = lEntity.posX;
						lClosestThreatY = lEntity.posY;
						lClosestThreatZ = lEntity.posZ;
						
						if (getAversionUndead() < 0)
						{
							lIsAfraidOfThreat = false;
							lEntityToAttack = lEntity;
						}
					}
				}
				List var2 = this.worldObj.getEntitiesWithinAABB(EntitySkeleton.class, this.boundingBox.expand(Math.abs(getAversionUndead()), 3.0D, Math.abs(getAversionUndead())));
				if (var2 != null && var2.size() != 0)
				{
					Entity lEntity = (Entity)var2.get(0);
					
					if (this.getDistanceSqToEntity(lEntity) < lClosestThreatDistanceSq)
					{
						lClosestThreatDistanceSq = (float)this.getDistanceSqToEntity(lEntity);
						lClosestThreatX = lEntity.posX;
						lClosestThreatY = lEntity.posY;
						lClosestThreatZ = lEntity.posZ;
						
						if (getAversionUndead() < 0)
						{
							lIsAfraidOfThreat = false;
							lEntityToAttack = lEntity;
						}
					}
				}
			}
			
			if (getAversionPlayer() > 0)
			{
				List var3 = this.worldObj.getEntitiesWithinAABB(EntityPlayer.class, this.boundingBox.expand(getAversionPlayer(), 3.0D, getAversionPlayer()));
				
				if (var3 != null && var3.size() != 0)
				{
					Entity lEntity = (Entity)var3.get(0);
					if (this.getDistanceSqToEntity(lEntity) < lClosestThreatDistanceSq)
					{
						lClosestThreatDistanceSq = (float)this.getDistanceSqToEntity(lEntity);
						lClosestThreatX = lEntity.posX;
						lClosestThreatY = lEntity.posY;
						lClosestThreatZ = lEntity.posZ;
						lIsAfraidOfThreat = true;
					}
				}
			}
			
			if (getAversionFire() > 0)
			{
				float lAversionFire = getAversionFire();
				float lBestDistanceFireSq = Float.POSITIVE_INFINITY;
				float lCurrentDistanceFireSq;
				ChunkCoordinates lClosestFire = new ChunkCoordinates();
				
				for (int iX = MathHelper.floor_double(this.posX - lAversionFire); iX <= MathHelper.ceiling_double_int(this.posX + lAversionFire); iX++)
				{
					for (int iY = MathHelper.floor_double(this.posY - lAversionFire); iY <= MathHelper.ceiling_double_int(this.posX + lAversionFire); iY++)
					{
						for (int iZ = MathHelper.floor_double(this.posZ - lAversionFire); iZ <= MathHelper.ceiling_double_int(this.posX + lAversionFire); iZ++)
						{
							if (this.worldObj.getBlockMaterial(iX, iY, iZ) == Material.fire)
							{
								lCurrentDistanceFireSq = (((float)this.posX - iX)*((float)this.posX - iX) + ((float)this.posY - iY)*((float)this.posY - iY) + ((float)this.posZ - iZ)*((float)this.posZ - iZ));
								
								if (lCurrentDistanceFireSq < lBestDistanceFireSq)
								{
									lBestDistanceFireSq = lCurrentDistanceFireSq;
									lClosestFire.posX = iX;
									lClosestFire.posY = iY;
									lClosestFire.posZ = iZ;
								}
							}
						}
					}
				}
				
				//This is like the null check  in the above sections, but it's not actually neccesary
				//if (lBestDistanceSquared < Float.POSITIVE_INFINITY)
				{
					if (lBestDistanceFireSq < lClosestThreatDistanceSq)
					{
						lClosestThreatDistanceSq = lBestDistanceFireSq;
						lClosestThreatX = lClosestFire.posX;
						lClosestThreatY = lClosestFire.posY;
						lClosestThreatZ = lClosestFire.posZ;
						lIsAfraidOfThreat = true;
					}
				}
			}
			
			//Now that we've found the closest threat, lets avoid it
			if (lClosestThreatDistanceSq < Float.POSITIVE_INFINITY)
			{
				Vec3 lDestination;
				
				if (lIsAfraidOfThreat) //Run away from threat
				{
					lDestination = RandomPositionGenerator.findRandomTargetBlockAwayFrom(this, 16, 7, this.worldObj.getWorldVec3Pool().getVecFromPool(lClosestThreatX, lClosestThreatY, lClosestThreatZ));
					
					//Check to make sure the destination isn't null and that it's farther away from the threat compared to before, if the entity is afraid of the 'threat'
					if (lDestination != null && this.getDistanceSq(lDestination.xCoord, lDestination.yCoord, lDestination.zCoord) > lClosestThreatDistanceSq)
					{
						this.getNavigator().setPath(this.getNavigator().getPathToXYZ(lDestination.xCoord, lDestination.yCoord, lDestination.zCoord), 0.25F);//Note: 0.25F is the speed
					}
				}
				else //Attack 'threat'
				{
					//TODO: add code to make the horse attack the 'threat'
					//if (lEntityToAttack != null) this.setTarget(lEntityToAttack);
				}
				
				lIsTasking = true;
			}	
		}
		//End of 'avoid threats' section
		
		//'Being ridden' section
		if (lRider != null)
		{
			double lMovementFactor = 0.1;
			float lRotationFactor = 5.0F;
			Vec3 lLookDirection = this.getLookVec();
			
			//Causes the horse to move in the direction it is facing while being ridden.
			//An alternitive is to set the path to the look vector, ala this.getNavigator().setPath(this.getNavigator().getPathToXYZ(lLookDirection.xCoord, lLookDirection.yCoord, lLookDirection.zCoord), 0.25F);
			this.moveEntity(lLookDirection.xCoord * lMovementFactor, lLookDirection.yCoord * lMovementFactor, lLookDirection.zCoord * lMovementFactor);
			
			//TODO: need to add the other controls for moving the horse, such as left/right for strafing and jump for gallop.
			//Most likely this will require the client sending a custom packet to the server describing which of these keys are being pressed.
		}
		//End of 'being ridden' section
		
		//The 'eat items' section
		if (!lIsTasking)
		{
			List lItemEntityList = this.worldObj.getEntitiesWithinAABB(EntityItem.class, (this.boundingBox.expand(this.cFoodSearchRange, 3.0D, this.cFoodSearchRange)));
			Iterator lItemEntityIterator = lItemEntityList.iterator();
			
			while (lItemEntityIterator.hasNext())
			{
				EntityItem lCurrentItemEntity = (EntityItem)lItemEntityIterator.next();
				ItemStack lCurrentItem = lCurrentItemEntity.getEntityItem();
				if (cHungerRestoredItem[lCurrentItem.itemID] > 0)
				{
					//If the food is within eating range, go ahead and eat the whole stack
					if (this.getDistanceSqToEntity(lCurrentItemEntity) < this.cFoodEatRangeSq)
					{
						this.adjustHunger(cHungerRestoredItem[lCurrentItem.itemID] * lCurrentItem.stackSize);
						lCurrentItemEntity.setDead();
						//TODO: Add eating sound
					}
					//Otherwise, path towards it so that you can eat it soon after
					else
					{
						this.getNavigator().setPath(this.getNavigator().getPathToXYZ(lCurrentItemEntity.posX, lCurrentItemEntity.posY, lCurrentItemEntity.posZ), 0.25F);//Note: 0.25F is the speed
						lIsTasking = true;
					}
				}
			}
		}
		//End of 'eat items' section
		
		//The 'eat grass' section
		//I seem to be having problems with the horses pathing to grass, they get there but are not aligned exactly with the block so they don't eat the grass
		if (!lIsTasking)
		{
			if (this.getHunger() < this.cMaxHunger)
			{
				int lPosX = MathHelper.floor_double(this.posX);
				int lPosY = MathHelper.floor_double(this.posY);
				int lPosZ = MathHelper.floor_double(this.posZ);
				
				//Quick check to see if the block right below the horse is grass. If so, it goes ahead and eats it
				if (worldObj.getBlockId(lPosX, lPosY - 1, lPosZ) == Block.grass.blockID)
				{
					worldObj.setBlock(lPosX, lPosY - 1, lPosZ, Block.dirt.blockID);
					this.adjustHunger(this.cHungerRestoredGrass);
				}
				//Otherwise, check within the food search radius for grass
				else
				{
					int lFoodSearchRange = MathHelper.ceiling_double_int(cFoodSearchRange);
					float lBestDistanceGrassSq = Float.POSITIVE_INFINITY;
					float lCurrentDistanceGrassSq;
					ChunkCoordinates lClosestGrass = new ChunkCoordinates();
					
					for (int iX = lPosX - lFoodSearchRange; iX < lPosX + lFoodSearchRange; iX++)
					{
						for (int iY = lPosY - lFoodSearchRange; iY < lPosY + lFoodSearchRange; iY++)
						{
							for (int iZ = lPosZ - lFoodSearchRange; iZ < lPosZ + lFoodSearchRange; iZ++)
							{
								if(worldObj.getBlockId(iX, iY, iZ) == Block.grass.blockID)
								{
									lCurrentDistanceGrassSq = (lPosX - iX)*(lPosX - iX) + (lPosY - iY)*(lPosY - iY) + (lPosZ - iZ)*(lPosZ - iZ);
									
									if (lCurrentDistanceGrassSq < lBestDistanceGrassSq)
									{
										lBestDistanceGrassSq = lCurrentDistanceGrassSq;
										lClosestGrass.posX = iX;
										lClosestGrass.posY = iY;
										lClosestGrass.posZ = iZ;
									}
								}
							}
						}
					}
					
					if (lBestDistanceGrassSq < Float.POSITIVE_INFINITY)
					{
						this.getNavigator().setPath(this.getNavigator().getPathToXYZ(lClosestGrass.posX + 0.5, lClosestGrass.posY + 1, lClosestGrass.posZ + 0.5), 0.25F);//Note: 0.25F is the speed
						lIsTasking = true;
					}
				}
			}
		}
		//End of 'eat grass' section
		
		//'Male dominance' section
		//TODO: add male dominace code
		//End of 'male dominance' section
		
		//'Breeding' section
		//TODO: add breeding code. It's worth noting that the breeding status will most likely be stored in the otherFlags object, or possibly in growingAge
		//End of 'breeding' section
		
		//'Update health/hunger/etc' section
		int lWeight = this.getTotalHorseLoad(lRider);
		long lTime = worldObj.getWorldTime();
		
		if (lHealthStatus == 2) //The horse is crippled
		{
			//TODO: make the horse "scream distressingly"
			if (lWeight > cMaxCrippledWeight)
			{
				if (lTime % 20 == 0) this.damageEntity(DamageSource.fall, 1);
			}
		}
		else if (lHealthStatus == 1) //The horse is lamed
		{
			if (lWeight > cMaxLamedWeight)
			{
				if (lTime % 20 == 0) this.damageEntity(DamageSource.fall, 1);
				//Buck the player
			}
			if (lTime % (cHealingDelay * 2) == 0) this.heal(1); //The horse heals twice as slowly when lamed
		}
		else if (lHealthStatus == 0) //The horse is normal
		{
			if (lWeight > cMaxNormalWeightThrow)
			{
				//Buck the player
				if (lWeight > cMaxNormalWeightDamage && lTime % 20 == 0) this.damageEntity(DamageSource.fall, 1);
			}
			if (lTime % cHealingDelay == 0) this.heal(1);
		}
		//End of 'update health/hunger/etc' section
		
		super.onLivingUpdate();
	}
	
	static
	{
		//TODO: add the actual list of items which the horse eats
		cHungerRestoredItem[Item.appleRed.itemID] = 2;
	}
}
