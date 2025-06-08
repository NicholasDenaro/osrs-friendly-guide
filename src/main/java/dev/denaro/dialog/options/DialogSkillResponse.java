package dev.denaro.dialog.options;

import java.util.List;

public class DialogSkillResponse extends DialogResponse
{
    public List<DialogSkillRequirement> requirements;

    public List<String> skills;

    public DialogSkillResponse(List<String> skills, List<String> messages, List<DialogSkillRequirement> requirements) {
        this.skills = skills;
        this.messages = messages;
        this.requirements = requirements;
    }
}
