package net.minecraft.src;

import java.lang.reflect.Field;

public class BTHEntityAIWatchClosest extends EntityAIWatchClosest
{
	/**The offset of the main entity field in the superclass. See net.minecraft.src.EntityAIWatchClosest*/
	private static int cEntityFieldOffset = 0;
	
	/**The entity executing this task*/
	private EntityLiving theEntity;
	
	public BTHEntityAIWatchClosest(EntityLiving par1EntityLiving, Class par2Class, float par3)
	{
		this(par1EntityLiving, par2Class, par3, 0.2F);
	}
	
	public BTHEntityAIWatchClosest(EntityLiving par1EntityLiving, Class par2Class, float par3, float par4)
	{
		super(par1EntityLiving, par2Class, par3, par4);
		
		try 
		{
			//Grab the private entity object
			Field lTheEntityField = this.getClass().getSuperclass().getDeclaredFields()[cEntityFieldOffset];
			lTheEntityField.setAccessible(true);
			theEntity = (EntityLiving)lTheEntityField.get(this);
		}
		catch (IllegalArgumentException e)
		{
			e.printStackTrace();
		}
		catch (IllegalAccessException e)
		{
			e.printStackTrace();
		}
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
