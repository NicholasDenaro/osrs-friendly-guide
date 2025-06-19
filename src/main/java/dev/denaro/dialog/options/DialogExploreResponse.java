package dev.denaro.dialog.options;

import dev.denaro.dialog.options.requirements.DialogRequirement;
import org.tomlj.TomlArray;

import java.util.List;

public class DialogExploreResponse extends DialogResponse
{
    public DialogExploreResponse(TomlArray messages, List<DialogRequirement> requirements) {
        super(messages, requirements);
    }
}
