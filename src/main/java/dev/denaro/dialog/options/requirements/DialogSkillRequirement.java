package dev.denaro.dialog.options.requirements;

import dev.denaro.yaml.types.YamlObject;
import net.runelite.api.Client;
import net.runelite.api.Player;
import net.runelite.api.Skill;
import net.runelite.api.annotations.Component;

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
    public static DialogSkillRequirement create(YamlObject requirementMap)
    {
        DialogSkillRequirement req = new DialogSkillRequirement();

        req.skill = requirementMap.getSimpleValue("name").getString();
        req.levelMin = requirementMap.getSimpleValue("level").getInt();
        if (requirementMap.hasKey("levelMax"))
        {
            req.levelMax = requirementMap.getSimpleValue("levelMax").getInt();
        }
        else
        {
            req.levelMax = 999;
        }

        req.setup(requirementMap);

        return req;
    }

    @Override
    public boolean _isMet(Client client)
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
