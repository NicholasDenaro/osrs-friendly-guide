package dev.denaro.dialog.options.requirements;

import net.runelite.api.*;
import net.runelite.api.annotations.Component;

import java.util.Map;

@Component
public class DialogQuestRequirement extends DialogRequirement
{
    String quest;
    String status;
    String atleast;

    static
    {
        DialogRequirement.RegisterCreateCall("quest", DialogQuestRequirement::create);
    }
    public static DialogQuestRequirement create(Map<String, Object> requirementMap)
    {
        DialogQuestRequirement req = new DialogQuestRequirement();
        req.quest =(String)requirementMap.get("name");
        req.status = (String)requirementMap.get("status");
        req.atleast = (String)requirementMap.get("minimum");

        req.setup(requirementMap);
        return req;
    }

    @Override
    public boolean _isMet(Client client) {
        QuestState state = Quest.valueOf(this.quest.toUpperCase().replaceAll(" ", "_").replaceAll("'", "")).getState(client);

        if (this.status != null)
        {
            switch (this.status.toLowerCase())
            {
                case "not started":
                    return state == QuestState.NOT_STARTED;
                case "in progress":
                    return state == QuestState.IN_PROGRESS;
                case "complete":
                    return state == QuestState.FINISHED;
                default:
                    return false;
            }
        }
        else
        {
            switch (this.status.toLowerCase())
            {
                case "not started":
                    return true;
                case "in progress":
                    return state == QuestState.IN_PROGRESS || state == QuestState.FINISHED;
                case "complete":
                    return state == QuestState.FINISHED;
                default:
                    return false;
            }
        }
    }
}
