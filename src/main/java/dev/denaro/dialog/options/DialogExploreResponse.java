package dev.denaro.dialog.options;

import dev.denaro.dialog.options.requirements.DialogRequirement;
import dev.denaro.yaml.types.YamlArray;

import java.util.List;

public class DialogExploreResponse extends DialogResponse
{
    public DialogExploreResponse(YamlArray messages, List<DialogRequirement> requirements) {
        super(messages, requirements);
    }
}
