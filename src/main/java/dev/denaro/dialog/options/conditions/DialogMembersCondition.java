package dev.denaro.dialog.options.conditions;

import net.runelite.api.Client;
import net.runelite.api.VarPlayer;
import net.runelite.api.WorldType;
import net.runelite.api.annotations.Component;

@Component
public class DialogMembersCondition extends DialogCondition
{
    static
    {
        DialogCondition.register("members", new DialogMembersCondition());
    }

    @Override
    public boolean _is(Client client)
    {
        return client.getWorldType().contains(WorldType.MEMBERS);
    }
}
