package dev.denaro;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;

@ConfigGroup("example")
public interface FriendlyGuideConfig extends Config
{
	@ConfigItem(
		keyName = "greeting",
		name = "Welcome Greeting",
		description = "The message to show to the user when they login"
	)
	default String greeting()
	{
		return "Hello";
	}

	@ConfigItem(
			keyName = "model_ids",
			name = "Model Ids",
			description = "comma separated list of ids"
	)
	default String modelIds() { return "214, 250, 5674, 5668, 5672, 7123, 12799"; }
}
