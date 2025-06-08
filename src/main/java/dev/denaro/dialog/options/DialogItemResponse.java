package dev.denaro.dialog.options;

import dev.denaro.dialog.options.requirements.DialogRequirement;

import java.util.List;

public class DialogItemResponse extends DialogResponse
{
    public String itemType;
    public DialogItemResponse(List<Object> messages, List<DialogRequirement> requirements, String itemType) {
        super(messages, requirements);
        this.itemType = itemType;
    }

    public boolean isItemType(String type)
    {
        return type.equalsIgnoreCase(this.itemType);
    }

    @Override
    public String toString()
    {
        return "DialogItemResponse " + itemType + " " + this.requirements;
    }
}
