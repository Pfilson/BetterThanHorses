package net.minecraft.src;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.server.MinecraftServer;

public class BTHBetterThanHorses extends FCAddOn 
{
	//A note for those working on the project: When decompiling BTW version 4.84 you will likely get a few errors
	//related to the EntityAnimal class. To fix these, replace the variable this.breeding with
	//this.entityLivingToAttack wherever it is shown to cause an error.
	
	//Modifies NetServerHandler.class
	//--Insert a newline after line 1174
	//--Copy and paste the following line
	//else BTHBetterThanHorses.ServerCustomPacketReceived(this.mcServer, this.playerEntity, par1Packet250CustomPayload);
	//
	//We may need to request a BTW hook for this depending on whether BTW already modifies this class (I'm not 100% sure)
	
	/**The better than horses addon instance*/
	//public static BTHBetterThanHorses instance = new BTHBetterThanHorses();
	
	/**Used to keep client-only code away from the server*/
	//The following line is modified by the server code generator
	public static BTHProxy proxy = new BTHProxyClient();
	
	//Feel free to change this to something less likely to be overriden by Mojang in the future
	//Or set it as a config option, whatever satisfies you
	private static int cHorseEntityID = 30;
	
	public static String cControlPacketID = "BTH|CP";
	
	//We still need a standardized version naming scheme...
	/**The version number of this version of Better Than Horses*/
	public static final String cVersion = "0.1";
	
	@Override
	public void Initialize() 
	{
		//Add the horse to the entity list and add a spawning egg for them
		EntityList.addMapping(BTHEntityHorse.class, "bthHorse", cHorseEntityID);
		EntityList.entityEggs.put(Integer.valueOf(cHorseEntityID), new EntityEggInfo(cHorseEntityID, 0x7F462C, 0x000000));
		
		//Add the renderer for the horse entity
		proxy.addEntityRenderers();
		
		//Add horses to the list of spawnable animals
		this.addSpawn(BTHEntityHorse.class, 10, 4, 4, EnumCreatureType.creature, createBiomeArrayForHorses());
		
		FCAddOnHandler.LogMessage("Better Than Horses " + cVersion + " has been loaded.");
	}
	
	@Override
	public void OnLanguageLoaded(StringTranslate aStringTranslator) 
	{
		//Add the name to the horse (so it doesn't appear in game as 'bthHorse')
		aStringTranslator.GetTranslateTable().put("entity.bthHorse.name", "Horse");
	}
	
	/**This function is called whenever the server receives a custom packet that isn't one of its own. Use this to send packets from client to server.
	 * 
	 * @param aMCS - the minecraft server instance
	 * @param aPlayer - the server player
	 * @param aCustomPacket - the custom packet
	 */
	public static void ServerCustomPacketReceived(MinecraftServer aMCS, EntityPlayerMP aPlayer, Packet250CustomPayload aCustomPacket)
	{
		if (cControlPacketID.equals(aCustomPacket.channel))
		{
			try
			{
				DataInputStream lCustomPacketRead = new DataInputStream(new ByteArrayInputStream(aCustomPacket.data));
				
				boolean lIsLeftPressed = lCustomPacketRead.readBoolean();
				boolean lIsRightPressed = lCustomPacketRead.readBoolean();
				boolean lIsJumpPressed = lCustomPacketRead.readBoolean();
				boolean lIsBackPressed = lCustomPacketRead.readBoolean();
				
				if (aPlayer.ridingEntity != null && aPlayer.ridingEntity instanceof BTHEntityHorse)
				{
					((BTHEntityHorse)aPlayer.ridingEntity).recieveControlPacket(lIsLeftPressed, lIsRightPressed, lIsJumpPressed, lIsBackPressed);
				}
			}
			catch (IOException e)
			{
				e.printStackTrace();
			}
		}
	}
	
	private BiomeGenBase[] createBiomeArrayForHorses()
	{
		List lBiomeList = new ArrayList();
		
		//The default list of biomes which animals spawn in. Feel free to change (like say, remove jungle, or whatnot)
		lBiomeList.add(BiomeGenBase.plains);
		lBiomeList.add(BiomeGenBase.extremeHills);
		lBiomeList.add(BiomeGenBase.forest);
		lBiomeList.add(BiomeGenBase.taiga);
		lBiomeList.add(BiomeGenBase.swampland);
		lBiomeList.add(BiomeGenBase.icePlains);
		lBiomeList.add(BiomeGenBase.iceMountains);
		lBiomeList.add(BiomeGenBase.forestHills);
		lBiomeList.add(BiomeGenBase.taigaHills);
		lBiomeList.add(BiomeGenBase.extremeHillsEdge);
		lBiomeList.add(BiomeGenBase.jungle);
		lBiomeList.add(BiomeGenBase.jungleHills);
		
		return (BiomeGenBase[])((BiomeGenBase[])lBiomeList.toArray(new BiomeGenBase[0]));
	}
	
	/**The add spawn method, copied from Risugami's Modloader (http://www.minecraftforum.net/topic/75440-v152-risugamis-mods-updated/)
	 * and modified slightly. This adds a entity (based on class) to the spawn list for an array of biomes.
	 * 
	 * @param aEntityClass - The class of the entity to add
	 * @param aSpawnWeight - The weight of this mob spawning. Most mobs have a weight of 10
	 * @param aMinGroupSize - The smallest size a group of the entity can spawn in
	 * @param aMaxGroupSize - The largest size a group of the entity can spawn in
	 * @param aCreatureType - The type of creature this is, which determines its spawning conditions. Options include
	 * monster (hostile mobs), creature (animals), ambient (bats), and waterCreature (squid)
	 * @param aBiomeArray - The array of biomes for which to make the entity spawn in. See BiomeGenBase for the full list
	 */
	public static void addSpawn(Class aEntityClass, int aSpawnWeight, int aMinGroupSize, int aMaxGroupSize, EnumCreatureType aCreatureType, BiomeGenBase[] aBiomeArray)
	{
		if (aEntityClass == null)
		{
			throw new IllegalArgumentException("entityClass cannot be null");
		}
		else if (aCreatureType == null)
		{
			throw new IllegalArgumentException("spawnList cannot be null");
		}
		else if (aBiomeArray == null)
		{
			throw new IllegalArgumentException("biomeArray cannot be null");
		}
		else
		{
			for (int iBiome = 0; iBiome < aBiomeArray.length; ++iBiome)
			{
				List lSpawnList = aBiomeArray[iBiome].getSpawnableList(aCreatureType);
				
				if (lSpawnList != null)
				{
					boolean lIsSpawnListed = false;
					Iterator lSpawnIterator = lSpawnList.iterator();
					
					while (lSpawnIterator.hasNext())
					{
						SpawnListEntry lSpawnEntry = (SpawnListEntry)lSpawnIterator.next();
						
						if (lSpawnEntry.entityClass == aEntityClass)
						{
							lSpawnEntry.itemWeight = aSpawnWeight;
							lSpawnEntry.minGroupCount = aMinGroupSize;
							lSpawnEntry.maxGroupCount = aMaxGroupSize;
							lIsSpawnListed = true;
							break;
						}
					}
					
					if (!lIsSpawnListed)
					{
						lSpawnList.add(new SpawnListEntry(aEntityClass, aSpawnWeight, aMinGroupSize, aMaxGroupSize));
					}
				}
			}
		}
	}
}
