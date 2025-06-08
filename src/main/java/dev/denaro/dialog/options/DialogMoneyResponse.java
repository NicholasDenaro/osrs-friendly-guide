package dev.denaro.dialog.options;

import dev.denaro.dialog.options.requirements.DialogRequirement;

import java.util.List;

public class DialogMoneyResponse extends DialogResponse
{
    public DialogMoneyResponse(List<String> messages, List<DialogRequirement> requirements) {
        super(messages, requirements);
    }
}
