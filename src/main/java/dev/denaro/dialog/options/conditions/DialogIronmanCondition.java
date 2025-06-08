package dev.denaro.dialog.options.conditions;

import net.runelite.api.Client;
import net.runelite.api.Varbits;
import net.runelite.api.annotations.Component;

@Component
public class DialogIronmanCondition extends DialogCondition
{
    static
    {
        DialogCondition.register("ironman", new DialogIronmanCondition());
    }

    @Override
    public boolean _is(Client client)
    {
        return client.getVarbitValue(Varbits.ACCOUNT_TYPE) > 0;
    }
}
