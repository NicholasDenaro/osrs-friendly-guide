package dev.denaro.dialog.options.requirements;

import net.runelite.api.Client;
import net.runelite.api.Varbits;
import net.runelite.api.annotations.Component;
import org.tomlj.TomlTable;

@Component
public class DialogNotIronManRequirement extends DialogRequirement
{
    public static DialogNotIronManRequirement create(TomlTable requirement)
    {
        return new DialogNotIronManRequirement();
    }

    @Override
    public boolean _isMet(Client client) {
        return client.getVarbitValue(Varbits.ACCOUNT_TYPE) == 0;
    }
}
