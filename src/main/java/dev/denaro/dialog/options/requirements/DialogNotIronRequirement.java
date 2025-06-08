package dev.denaro.dialog.options.requirements;

import net.runelite.api.Client;
import net.runelite.api.Player;
import net.runelite.api.Skill;
import net.runelite.api.Varbits;

import java.util.Map;

public class DialogNotIronRequirement extends DialogRequirement
{
    static
    {
        DialogRequirement.RegisterCreateCall("notiron", DialogNotIronRequirement::create);
    }
    public static DialogNotIronRequirement create(Map<String, Object> requirementMap)
    {
        return new DialogNotIronRequirement();
    }

    @Override
    public boolean isMet(Client client) {
        return client.getVarbitValue(Varbits.ACCOUNT_TYPE) == 0;
    }
}
