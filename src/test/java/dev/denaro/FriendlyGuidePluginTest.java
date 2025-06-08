package dev.denaro;

import net.runelite.client.RuneLite;
import net.runelite.client.externalplugins.ExternalPluginManager;

public class FriendlyGuidePluginTest
{
	public static void main(String[] args) throws Exception
	{
		ExternalPluginManager.loadBuiltin(FriendlyGuidePlugin.class);
		RuneLite.main(args);
	}
}