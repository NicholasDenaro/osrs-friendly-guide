package dev.denaro.dialog.options;

import dev.denaro.dialog.options.requirements.DialogRequirement;
import net.runelite.api.Client;
import net.runelite.api.Quest;
import net.runelite.api.QuestState;

import java.util.List;

public class DialogQuestResponse extends DialogResponse
{
    public String quest;
    public DialogQuestResponse(List<Object> messages, List<DialogRequirement> requirements, String quest) {
        super(messages, requirements);
        this.quest = quest;
    }

    public boolean isQuestUnstarted(Client client)
    {
        String enumName = this.quest.toUpperCase().replaceAll(" ", "_");
        try
        {
            QuestState state = Quest.valueOf(enumName).getState(client);

            return state == QuestState.NOT_STARTED;
        }
        catch (Exception ex)
        {

        }

        return false;
    }
}
