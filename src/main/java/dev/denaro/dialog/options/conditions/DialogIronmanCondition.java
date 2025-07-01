package dev.denaro.dialog.options.conditions;

import net.runelite.api.Client;
import net.runelite.api.Varbits;
import net.runelite.api.annotations.Component;

@Component
public class DialogIronmanCondition extends DialogCondition
{
    @Override
    public boolean _is(Client client)
    {
        return client.getVarbitValue(Varbits.ACCOUNT_TYPE) > 0;
    }
}
