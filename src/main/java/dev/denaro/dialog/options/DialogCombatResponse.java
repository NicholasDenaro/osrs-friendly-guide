package dev.denaro.dialog.options;

import dev.denaro.dialog.options.requirements.DialogRequirement;

import java.util.List;

public class DialogCombatResponse extends DialogResponse
{
    public DialogCombatResponse(List<Object> messages, List<DialogRequirement> requirements) {
        super(messages, requirements);
    }
}
