package dev.denaro.dialog.options.requirements;

import dev.denaro.yaml.YamlObject;
import net.runelite.api.*;
import net.runelite.api.annotations.Component;

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
    public static DialogQuestRequirement create(YamlObject requirementMap)
    {
        DialogQuestRequirement req = new DialogQuestRequirement();
        req.quest =requirementMap.getSimpleValue("name").getString();
        if (requirementMap.hasKey("status"))
        {
            req.status = requirementMap.getSimpleValue("status").getString();
        }
        if (requirementMap.hasKey("minimum"))
        {
            req.atleast = requirementMap.getSimpleValue("minimum").getString();
        }

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
