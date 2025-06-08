package dev.denaro.dialog.options;

import dev.denaro.dialog.options.requirements.DialogRequirement;
import net.runelite.api.Client;
import net.runelite.api.Quest;
import net.runelite.api.QuestState;

import java.util.List;

public class DialogExploreResponse extends DialogResponse
{
    public DialogExploreResponse(List<String> messages, List<DialogRequirement> requirements) {
        super(messages, requirements);
    }
}
