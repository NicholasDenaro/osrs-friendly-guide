package dev.denaro.dialog.options.requirements;

import net.runelite.api.Client;
import net.runelite.api.WorldType;
import net.runelite.api.annotations.Component;

import java.util.Map;

@Component
public class DialogMembersRequirement extends DialogRequirement
{
    static
    {
        DialogRequirement.RegisterCreateCall("members", DialogMembersRequirement::create);
    }
    public static DialogMembersRequirement create(Map<String, Object> requirementMap)
    {
        return new DialogMembersRequirement();
    }

    @Override
    public boolean _isMet(Client client) {
        return client.getWorldType().contains(WorldType.MEMBERS);
    }
}
