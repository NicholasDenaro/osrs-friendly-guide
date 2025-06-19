package dev.denaro.dialog.options;

import dev.denaro.dialog.options.requirements.DialogRequirement;
import org.tomlj.TomlArray;

import java.util.List;

public class DialogCombatResponse extends DialogResponse
{
    public DialogCombatResponse(TomlArray messages, List<DialogRequirement> requirements) {
        super(messages, requirements);
    }
}
