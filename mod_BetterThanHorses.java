// Devon 'DNoved1' Cooper (dnoved1@aim.com) (Totally not copied from anyone :P)
// For Better Than Horses
// 2013-06-14 (v0.1)
//
//A note on my variable names:
//--variables starting with a c are Constants
//--variables starting with an a are Arguments
//--variables starting with an l are Local variables

package net.minecraft.src;

import java.util.Map;

import net.minecraft.client.Minecraft;

public class mod_BetterThanHorses extends BaseMod 
{
    //Feel free to change this to something less likely to be overriden by Mojang in the future
	//Or set it as a config option, whatever satisfies you
	private static int cHorseEntityID = 30;
	
	@Override
	public String getVersion() 
	{
		return "0.1"; //We need a standardized version naming scheme...
	}

	@Override
	public void load() 
	{
		//Registers the horse entity and adds a spawn egg with brown and black(feel free to change, if you so wish)
		ModLoader.registerEntityID(BTHEntityHorse.class, "bthHorse", cHorseEntityID, 0x7F462C, 0x000000);
		ModLoader.addLocalization("entity.bthHorse.name", "Horse");
	}
	
	@Override
	public void addRenderer(Map aEntityRenderMap) 
	{
		aEntityRenderMap.put(BTHEntityHorse.class, new BTHRenderHorse(new ModelBetterHorse()));
	}
}
