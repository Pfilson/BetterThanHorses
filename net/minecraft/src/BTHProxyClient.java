package net.minecraft.src;

/**
 * Implements client-only code in a place where it cannot affect server code.
 */
public class BTHProxyClient extends BTHProxy
{
	@Override
	public void addEntityRenderers()
	{
		RenderManager.AddEntityRenderer(BTHEntityHorse.class, new BTHRenderHorse(new BTHModelHorse()));
	}
}
