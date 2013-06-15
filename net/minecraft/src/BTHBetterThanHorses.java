package net.minecraft.src;

public class BTHBetterThanHorses extends FCAddOn 
{
	//A note for those working on the project: When decompiling BTW version 4.84 you will likely get a few errors
	//related to the EntityAnimal class. To fix these, replace the variable this.breeding with
	//this.entityLivingToAttack wherever it is shown to cause an error.
	
	/**The better than horses addon instance*/
	public static BTHBetterThanHorses instance = new BTHBetterThanHorses();
	
	//Feel free to change this to something less likely to be overriden by Mojang in the future
	//Or set it as a config option, whatever satisfies you
	private static int cHorseEntityID = 30;
	
	//We still need a standardized version naming scheme...
	/**The version number of this version of Better Than Horses*/
	public static final String cVersion = "0.1";
	
	@Override
	public void Initialize() 
	{
		//Add the horse to the entity list
		//TODO: possibly add a mob spawner egg to assist in testing
		EntityList.addMapping(BTHEntityHorse.class, "bthHorse", cHorseEntityID);
		
		//Add the renderer for the horse entity
		//Note: This needs to be disabled in the server version
		RenderManager.AddEntityRenderer(BTHEntityHorse.class, new BTHRenderHorse(new BTHModelHorse()));
		
		FCAddOnHandler.LogMessage("Better Than Horses " + cVersion + "has been loaded.");
	}
	
	public void OnLanguageLoaded(StringTranslate aStringTranslator) 
	{
		//Add the name to the horse (so it doesn't appear in game as 'bthHorse')
		aStringTranslator.GetTranslateTable().put("entity.bthHorse.name", "Horse");
	}
}
