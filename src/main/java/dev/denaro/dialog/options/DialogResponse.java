package dev.denaro.dialog.options;

import dev.denaro.dialog.Dialog;
import dev.denaro.dialog.DialogMessage;
import dev.denaro.dialog.options.requirements.DialogRequirement;

import java.util.ArrayList;
import java.util.List;

public class DialogResponse
{
    public List<DialogRequirement> requirements;
    public List<String> messages;

    public DialogResponse(List<String> messages, List<DialogRequirement> requirements) {
        this.messages = messages;
        this.requirements = requirements;
    }

    public Dialog createDialog()
    {
        List<String> messageList = new ArrayList<>(this.messages);
        Dialog root = new DialogMessage(DialogMessage.DialogSpeaker.Guide, messageList.get(0));
        Dialog current = root;
        messageList.remove(0);
        while (!messageList.isEmpty())
        {
            current = current.setNext(new DialogMessage(DialogMessage.DialogSpeaker.Guide, messageList.get(0)));
            messageList.remove(0);
        }

        return root;
    }
}
