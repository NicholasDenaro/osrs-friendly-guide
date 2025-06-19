package dev.denaro.dialog.options;

import dev.denaro.dialog.Dialog;
import dev.denaro.dialog.DialogMessage;
import dev.denaro.dialog.options.conditions.DialogCondition;
import dev.denaro.dialog.options.requirements.DialogRequirement;
import net.runelite.api.Client;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tomlj.TomlArray;
import org.tomlj.TomlInvalidTypeException;
import org.tomlj.TomlTable;

import java.util.List;

public class DialogResponse
{
    private static final Logger logger = LoggerFactory.getLogger(DialogResponse.class);
    public List<DialogRequirement> requirements;
    private final TomlArray messages;

    public DialogResponse(TomlArray messages, List<DialogRequirement> requirements) {
        this.messages = messages;
        this.requirements = requirements;
    }

    public Dialog createDialog(Client client) throws Exception {
        Dialog root = null;
        Dialog current = null;
        for (int i = 0; i < this.messages.size(); i++)
        {
            String message = null;
            try
            {
                message = this.messages.getString(i);
            }
            catch (TomlInvalidTypeException exceptionStr)
            {
                // not a string, so check table
                try
                {
                    TomlTable obj = this.messages.getTable(i);
                    if (obj.contains("if"))
                    {
                        String condition = obj.getString("if").toLowerCase();

                        if (DialogCondition.is(condition, client))
                        {
                            message = obj.getString("text");
                        }
                    }
                    else
                    {
                        message = obj.getString("text");
                    }
                }
                catch (TomlInvalidTypeException exceptionObj)
                {
                    logger.error("messages has incorrect format");
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
        }

        return root;
    }
}
