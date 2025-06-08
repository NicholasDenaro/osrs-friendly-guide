package dev.denaro.dialog.options.requirements;

import net.runelite.api.Client;
import net.runelite.api.Player;
import net.runelite.api.Skill;
import net.runelite.api.annotations.Component;

import java.util.Map;

@Component
public class DialogSkillRequirement extends DialogRequirement
{
    String skill;
    int levelMin;
    int levelMax;

    static
    {
        DialogRequirement.RegisterCreateCall("skill", DialogSkillRequirement::create);
    }
    public static DialogSkillRequirement create(Map<String, Object> requirementMap)
    {
        DialogSkillRequirement req = new DialogSkillRequirement();

        req.skill = (String)requirementMap.get("name");
        req.levelMin = (int)requirementMap.get("level");
        if (requirementMap.get("levelMax") != null)
        {
            req.levelMax = (int)requirementMap.get("levelMax");
        }
        else
        {
            req.levelMax = 999;
        }

        req.setup(requirementMap);

        return req;
    }

    @Override
    public boolean isMet(Client client)
    {
        Player player = client.getLocalPlayer();
        if (this.skill.equals("Combat"))
        {
            return player.getCombatLevel() >= this.levelMin && player.getCombatLevel() <= this.levelMax;
        }

        int skillLevel = client.getRealSkillLevel(Skill.valueOf(this.skill.toUpperCase()));

        System.out.println("Checking " + this.levelMin + " <= " + this.skill + " " + skillLevel + " <= " + this.levelMax);

        return this.levelMin <= skillLevel && skillLevel <= this.levelMax;
    }

    @Override
    public String toString()
    {
        return "DialogSkillRequirement: " + this.skill + " lvl " + this.levelMin + " max " + this.levelMax;
    }
}
