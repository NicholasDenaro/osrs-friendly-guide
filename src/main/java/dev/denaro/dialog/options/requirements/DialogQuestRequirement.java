package dev.denaro.dialog.options.requirements;

import net.runelite.api.*;

import java.util.Map;

public class DialogQuestRequirement extends DialogRequirement
{
    String quest;
    String status;

    static
    {
        DialogRequirement.RegisterCreateCall("quest", DialogQuestRequirement::create);
    }
    public static DialogQuestRequirement create(Map<String, Object> requirementMap)
    {
        DialogQuestRequirement req = new DialogQuestRequirement();
        req.quest =(String)requirementMap.get("name");
        req.status = (String)requirementMap.get("status");

        req.setup(requirementMap);
        return req;
    }

    @Override
    public boolean isMet(Client client) {
        QuestState state = Quest.valueOf(this.quest.toUpperCase().replaceAll(" ", "_")).getState(client);

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
}
