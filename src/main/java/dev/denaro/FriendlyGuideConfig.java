package dev.denaro;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;

@ConfigGroup("friendlyGuide")
public interface FriendlyGuideConfig extends Config
{
	@ConfigItem(
			keyName = "introduction",
			name = "Introduction",
			description = "Show an introduction to the player about the friendly guide"
	)
	default boolean showIntroduction() { return true; }

	@ConfigItem(
			keyName = "model_ids",
			name = "Model Ids",
			description = "comma separated list of ids",
			hidden = true
	)
	default String modelIds() { return "214, 250, 5674, 5668, 5672, 7123"; }

	@ConfigItem(
			keyName = "etag",
			name = "Etag",
			description = "Used to download new dialogs only when necessary"
	)
	default String etag() { return ""; }

	@ConfigItem(
			keyName = "data",
			name = "Data",
			description = "Data used for guide responses",
			hidden = true
	)
	default String data() { return ""; }
}
