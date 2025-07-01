package dev.denaro.dialog.options.requirements;

import net.runelite.api.Client;
import net.runelite.api.Player;
import net.runelite.api.Skill;
import net.runelite.api.annotations.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tomlj.TomlTable;

@Component
public class DialogSkillRequirement extends DialogRequirement
{
    private static final Logger logger = LoggerFactory.getLogger(DialogSkillRequirement.class);
    String skill;
    int levelMin;
    int levelMax;

    public static DialogSkillRequirement create(TomlTable requirement)
    {
        DialogSkillRequirement req = new DialogSkillRequirement();

        req.skill = requirement.getString("name");
        req.levelMin = Math.toIntExact(requirement.getLong("level"));
        if (requirement.contains("levelMax"))
        {
            req.levelMax = Math.toIntExact(requirement.getLong("levelMax"));
        }
        else
        {
            req.levelMax = 999;
        }

        req.setup(requirement);

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

        logger.debug("Checking " + this.levelMin + " <= " + this.skill + " " + skillLevel + " <= " + this.levelMax);

        return this.levelMin <= skillLevel && skillLevel <= this.levelMax;
    }

    @Override
    public String toString()
    {
        return "DialogSkillRequirement: " + this.skill + " lvl " + this.levelMin + " max " + this.levelMax;
    }
}
