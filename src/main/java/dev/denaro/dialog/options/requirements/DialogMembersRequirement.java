package dev.denaro.dialog.options.requirements;

import dev.denaro.yaml.YamlObject;
import net.runelite.api.Client;
import net.runelite.api.WorldType;
import net.runelite.api.annotations.Component;

@Component
public class DialogMembersRequirement extends DialogRequirement
{
    static
    {
        DialogRequirement.RegisterCreateCall("members", DialogMembersRequirement::create);
    }
    public static DialogMembersRequirement create(YamlObject requirementMap)
    {
        return new DialogMembersRequirement();
    }

    @Override
    public boolean _isMet(Client client) {
        return client.getWorldType().contains(WorldType.MEMBERS);
    }
}
