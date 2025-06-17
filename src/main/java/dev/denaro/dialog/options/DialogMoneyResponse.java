package dev.denaro.dialog.options;

import dev.denaro.dialog.options.requirements.DialogRequirement;
import dev.denaro.yaml.YamlArray;

import java.util.List;

public class DialogMoneyResponse extends DialogResponse
{
    public DialogMoneyResponse(YamlArray messages, List<DialogRequirement> requirements) {
        super(messages, requirements);
    }
}
