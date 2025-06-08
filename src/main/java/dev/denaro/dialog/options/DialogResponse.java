package dev.denaro.dialog.options;

import dev.denaro.dialog.Dialog;
import dev.denaro.dialog.DialogMessage;
import dev.denaro.dialog.options.conditions.DialogCondition;
import dev.denaro.dialog.options.requirements.DialogRequirement;
import net.runelite.api.Client;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class DialogResponse
{
    public List<DialogRequirement> requirements;
    private List<Object> messages;

    public DialogResponse(List<Object> messages, List<DialogRequirement> requirements) {
        this.messages = messages;
        this.requirements = requirements;
    }

    public Dialog createDialog(Client client)
    {
        List<Object> messageList = new ArrayList<>(this.messages);
        Dialog root = null;
        Dialog current = null;
        while (!messageList.isEmpty())
        {
            String message = null;
            if (messageList.get(0) instanceof String)
            {
                message = (String)messageList.get(0);
            }
            else
            {
                Map<String, Object> obj = (Map<String, Object>)messageList.get(0);
                if (obj.get("if") != null)
                {
                    String condition = ((String)obj.get("if")).toLowerCase();

                    if (DialogCondition.is(condition, client))
                    {
                        message = (String)obj.get("text");
                    }
                }
                else
                {
                    message = (String)obj.get("text");
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
