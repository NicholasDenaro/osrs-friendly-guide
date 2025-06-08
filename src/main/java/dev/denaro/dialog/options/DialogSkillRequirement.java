package dev.denaro.dialog.options;

import net.runelite.api.Client;
import net.runelite.api.Player;
import net.runelite.api.Skill;

public class DialogSkillRequirement extends DialogRequirement
{
    String skill;
    int levelMin;
    int levelMax;

    public DialogSkillRequirement(String skill, int levelMin, int levelMax)
    {
        this.skill = skill;
        this.levelMin = levelMin;
        this.levelMax = levelMax;
    }

    @Override
    public boolean isMet(Client client) {

        Player player = client.getLocalPlayer();
        if (this.skill.equals("Combat")) {
            return player.getCombatLevel() >= this.levelMin && player.getCombatLevel() <= this.levelMax;
        }

        int skillLevel = client.getRealSkillLevel(Skill.valueOf(this.skill.toUpperCase()));

        return skillLevel >= this.levelMin && skillLevel <= this.levelMax;
    }
}
