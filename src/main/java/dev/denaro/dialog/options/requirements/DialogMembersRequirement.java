package dev.denaro.dialog.options.requirements;

import net.runelite.api.Client;
import net.runelite.api.WorldType;
import net.runelite.api.annotations.Component;
import org.tomlj.TomlTable;

@Component
public class DialogMembersRequirement extends DialogRequirement
{
    public static DialogMembersRequirement create(TomlTable requirement)
    {
        return new DialogMembersRequirement();
    }

    @Override
    public boolean _isMet(Client client) {
        return client.getWorldType().contains(WorldType.MEMBERS);
    }
}
