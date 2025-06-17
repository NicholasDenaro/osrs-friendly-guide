package dev.denaro.dialog.options;

import dev.denaro.dialog.Dialog;
import dev.denaro.dialog.DialogMessage;
import dev.denaro.dialog.options.conditions.DialogCondition;
import dev.denaro.dialog.options.requirements.DialogRequirement;
import dev.denaro.yaml.types.YamlArray;
import dev.denaro.yaml.types.YamlObject;
import dev.denaro.yaml.types.YamlSimpleValue;
import dev.denaro.yaml.types.YamlValue;
import net.runelite.api.Client;

import java.util.List;

public class DialogResponse
{
    public List<DialogRequirement> requirements;
    private final YamlArray messages;

    public DialogResponse(YamlArray messages, List<DialogRequirement> requirements) {
        this.messages = messages;
        this.requirements = requirements;
    }

    public Dialog createDialog(Client client) throws Exception {
        List<YamlValue> messageList = this.messages.getValues();
        Dialog root = null;
        Dialog current = null;
        while (!messageList.isEmpty())
        {
            String message = null;
            if (messageList.get(0) instanceof YamlSimpleValue)
            {
                message = ((YamlSimpleValue) messageList.get(0)).getString();
            }
            else
            {
                YamlObject obj = (YamlObject) messageList.get(0);
                if (obj.hasKey("if"))
                {
                    String condition = obj.getSimpleValue("if").getString().toLowerCase();

                    if (DialogCondition.is(condition, client))
                    {
                        message = obj.getSimpleValue("text").getString();
                    }
                }
                else
                {
                    message = obj.getSimpleValue("text").getString();
                }
            }
            if (message != null)
            {
                if (root == null) {

                    root = current = new DialogMessage(DialogMessage.DialogSpeaker.Guide, message);
                }
                else
                {
                    current = current.setNext(new DialogMessage(DialogMessage.DialogSpeaker.Guide, message));
                }
            }

            messageList.remove(0);
        }

        return root;
    }
}
