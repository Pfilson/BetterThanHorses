package net.minecraft.src;

/** Used to separate client code from server code in places where referencing a client class would
 *  make the server fail to compile.  This class is created on the server, and a BTHProxyClient
 *  (that actually does stuff) is created on the client. */
public class BTHProxy
{
	public void addEntityRenderers() {}
}
