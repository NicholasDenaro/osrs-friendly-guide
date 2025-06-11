package dev.denaro.dialog.options.requirements;

import net.runelite.api.Client;
import net.runelite.api.Varbits;
import net.runelite.api.annotations.Component;

import java.util.Map;

@Component
public class DialogNotIronManRequirement extends DialogRequirement
{
    static
    {
        DialogRequirement.RegisterCreateCall("notironman", DialogNotIronManRequirement::create);
    }
    public static DialogNotIronManRequirement create(Map<String, Object> requirementMap)
    {
        return new DialogNotIronManRequirement();
    }

    @Override
    public boolean _isMet(Client client) {
        return client.getVarbitValue(Varbits.ACCOUNT_TYPE) == 0;
    }
}
