package net.minecraft.src;

public class BTHRenderHorse extends RenderLiving
{
	//This is just a barebones render class. If anything else needs to be rendered onto the horse (say...fat rolls) it should probably be put in here
	
	public BTHRenderHorse(ModelBase aModel)
	{
		super(aModel, 0.7F); //0.7F is the shadow size. Feel free to adjust it if it feels off
	}
}
