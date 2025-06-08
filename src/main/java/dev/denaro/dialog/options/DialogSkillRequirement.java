package dev.denaro.dialog.options;

import net.runelite.api.Client;
import net.runelite.api.Player;
import net.runelite.api.Skill;
import net.runelite.api.Varbits;

public class DialogSkillRequirement extends DialogRequirement
{
    String skill;
    String condition;
    int levelMin;
    int levelMax;

    public DialogSkillRequirement(String skill, String condition, int levelMin, int levelMax)
    {
        this.skill = skill;
        this.condition = condition;
        this.levelMin = levelMin;
        this.levelMax = levelMax;
    }

    @Override
    public boolean isMet(Client client) {
        if ("ironman".equals(this.condition) && client.getVarbitValue(Varbits.ACCOUNT_TYPE) == 0)
        {
            // Skip ("pass") since account type 0 is not ironman
            return true;
        }

        Player player = client.getLocalPlayer();
        if (this.skill.equals("Combat"))
        {
            return player.getCombatLevel() >= this.levelMin && player.getCombatLevel() <= this.levelMax;
        }

        int skillLevel = client.getRealSkillLevel(Skill.valueOf(this.skill.toUpperCase()));

        return skillLevel >= this.levelMin && skillLevel <= this.levelMax;
    }
}
