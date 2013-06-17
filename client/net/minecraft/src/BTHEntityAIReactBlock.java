package net.minecraft.src;

import java.util.List;

/**An AI which allows an entity to either avoid a block or run towards it.*/
public class BTHEntityAIReactBlock extends EntityAIBase
{
	/**The boolean which tells whether this AI is for avoiding blocks or seeking them out*/
	public boolean isAvoiding;
	/**The entity this AI belongs to*/
	private EntityCreature entity;
	/**The speed at which to path when farther than 49 meters away from the target*/
	private float farSpeed;
	/**The speed at which to path when closer than 49 meters away from the target*/
	private float nearSpeed;
	/**The range in which to look for the target*/
	private float rangeToAvoid;
	/**The path to set to get away from the target*/
	private PathEntity entityPath;
	/**The target id of the block*/
	private int targetBlockID;
	/**The target*/
	private ChunkCoordinates targetBlock = new ChunkCoordinates();
	
	public BTHEntityAIReactBlock(EntityCreature aEntity, int aBlockID, float aRange, float aFarSpeed, float aNearSpeed, boolean aAvoid)
	{
		this.entity = aEntity;
		this.targetBlockID = aBlockID;
		this.rangeToAvoid = aRange;
		this.farSpeed = aFarSpeed;
		this.nearSpeed = aNearSpeed;
		this.isAvoiding = aAvoid;
		//May change this at some point, as it prevents it from running while doing certain other things
		//such as avoiding entities
		this.setMutexBits(1);
	}
	
	/**
	 * Returns whether the EntityAIBase should begin execution.
	 */
	public boolean shouldExecute()
	{
		//Run a standard searching algorithm to find the closest block within the searching range
		double lBestDistanceSq = Double.POSITIVE_INFINITY;
		double lCurrentDistanceSq;
		
		for (int iX = MathHelper.floor_double(entity.posX - rangeToAvoid); iX <= MathHelper.ceiling_double_int(entity.posX + rangeToAvoid); iX++)
		{
			for (int iY = MathHelper.floor_double(entity.posY - rangeToAvoid); iY <= MathHelper.ceiling_double_int(entity.posY + rangeToAvoid); iY++)
			{
				for (int iZ = MathHelper.floor_double(entity.posZ - rangeToAvoid); iZ <= MathHelper.ceiling_double_int(entity.posZ + rangeToAvoid); iZ++)
				{
					if (entity.worldObj.getBlockId(iX, iY, iZ) == targetBlockID)
					{
						lCurrentDistanceSq = (entity.posX - iX)*(entity.posX - iX) + (entity.posY - iY)*(entity.posY - iY) + (entity.posZ - iZ)*(entity.posZ - iZ);
						
						if (lCurrentDistanceSq < lBestDistanceSq)
						{
							lBestDistanceSq = lCurrentDistanceSq;
							targetBlock.posX = iX;
							targetBlock.posY = iY;
							targetBlock.posZ = iZ;
						}
					}
				}
			}
		}
		
		//If a block is found
		if (lBestDistanceSq < Double.POSITIVE_INFINITY)
		{	
			Vec3 lTargetVector = entity.worldObj.getWorldVec3Pool().getVecFromPool(targetBlock.posX, targetBlock.posY, targetBlock.posZ);
			Vec3 lDestination;
			
			if (isAvoiding) //Set the destination to some random point away from the block
			{
				lDestination = RandomPositionGenerator.findRandomTargetBlockAwayFrom(entity, MathHelper.ceiling_double_int(rangeToAvoid * 2), MathHelper.ceiling_double_int(rangeToAvoid), lTargetVector);
			}
			//TODO:For some reason or another this doesn't seem to work. In other words, the entity won't path towards the block
			//If anyone can figure out why, it would be much appreciated
			else //Set the destination to the block
			{
				lDestination = lTargetVector;
			}
			
			//A check to make sure a) the destination isn't null, b)if avoiding, make sure you're going further away
			//than you were to begin with, and c)if you're not avoiding, go ahead (because you should be getting closer)
			//if (lDestination != null && ((isAvoiding && entity.getDistanceSq(lDestination.xCoord, lDestination.yCoord, lDestination.zCoord) > lBestDistanceSq) || !isAvoiding))
			if (lDestination != null)
			{
				entityPath = entity.getNavigator().getPathToXYZ(lDestination.xCoord, lDestination.yCoord, lDestination.zCoord);
				
				//Make sure the path is valid and not already set to the same place
				return this.entityPath == null ? false : this.entityPath.isDestinationSame(lDestination);
			}
		}
		return false;
	}
	
	/**
	 * Returns whether an in-progress EntityAIBase should continue executing
	 */
	public boolean continueExecuting()
	{
		return !entity.getNavigator().noPath();
	}
	
	/**
	 * Execute a one shot task or start executing a continuous task
	 */
	public void startExecuting()
	{
		entity.getNavigator().setPath(this.entityPath, this.farSpeed);
	}
	
	/**
	 * Resets the task
	 */
	public void resetTask()
	{
		//Clears the targetBlock
		targetBlock = new ChunkCoordinates();
	}
	
	/**
	 * Updates the task
	 */
	public void updateTask()
	{
		if (entity.getDistanceSq(targetBlock.posX, targetBlock.posY, targetBlock.posZ) < 49.0D)
		{
			this.entity.getNavigator().setSpeed(this.nearSpeed);
		}
		else
		{
			this.entity.getNavigator().setSpeed(this.farSpeed);
		}
	}
}
