package net.minecraft.src;

public class BTHEntityAIWatchClosest extends EntityAIWatchClosest
{
	/**The entity executing this task*/
	private EntityLiving theEntity;
	
	public BTHEntityAIWatchClosest(EntityLiving aEntity, Class aTargetClass, float aLookRange)
	{
		this(aEntity, aTargetClass, aLookRange, 0.02F);
	}
	
	public BTHEntityAIWatchClosest(EntityLiving aEntity, Class aTargetClass, float aLookRange, float aFrequency)
	{
		super(aEntity, aTargetClass, aLookRange, aFrequency);
		
		theEntity = aEntity;
	}
	
	public boolean shouldExecute()
	{
		//Don't look if we are riding or being ridden by the thing we are trying to look at
		if (theEntity.riddenByEntity == closestEntity || theEntity.ridingEntity == closestEntity)
		{
			return false;
		}
		else return super.shouldExecute();
	}
}
