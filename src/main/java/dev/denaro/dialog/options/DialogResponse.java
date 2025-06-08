package dev.denaro.dialog.options;

import dev.denaro.dialog.Dialog;
import dev.denaro.dialog.DialogMessage;

import java.util.ArrayList;
import java.util.List;

public abstract class DialogResponse
{
    public List<String> messages;

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
