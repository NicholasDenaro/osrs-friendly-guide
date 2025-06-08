package dev.denaro.dialog.options;

import net.runelite.api.Client;

public abstract class DialogRequirement
{
    public abstract boolean isMet(Client client);
}
