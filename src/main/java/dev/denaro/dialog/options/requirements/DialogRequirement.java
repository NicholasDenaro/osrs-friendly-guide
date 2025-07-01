package dev.denaro.dialog.options.requirements;

import dev.denaro.dialog.options.conditions.DialogCondition;
import net.runelite.api.Client;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tomlj.TomlTable;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public abstract class DialogRequirement
{
    private static final Logger logger = LoggerFactory.getLogger(DialogRequirement.class);

    public static void registerAllRequirements()
    {
        logger.debug("Loading dialog requirements");
        CreateCalls = new HashMap<>();
        DialogRequirement.RegisterCreateCall("members", DialogMembersRequirement::create);
        DialogRequirement.RegisterCreateCall("skill", DialogSkillRequirement::create);
        DialogRequirement.RegisterCreateCall("quest", DialogQuestRequirement::create);
        DialogRequirement.RegisterCreateCall("notironman", DialogNotIronManRequirement::create);
        DialogRequirement.RegisterCreateCall("music", DialogMusicRequirement::create);
    }

    private static Map<String, Function<TomlTable, DialogRequirement>> CreateCalls;
    public static void RegisterCreateCall(String key, Function<TomlTable, DialogRequirement> func)
    {
        CreateCalls.put(key, func);
        logger.debug("Registered " + key + " Requirement");
    }

    public static DialogRequirement New(String type, TomlTable requirement)
    {
        Function<TomlTable, DialogRequirement> func = CreateCalls.get(type);
        return func.apply(requirement);
    }

    private String condition;
    private boolean negate;

    protected void setup(TomlTable requirement)
    {
        this.condition = requirement.getString("if");
        this.negate = Boolean.TRUE.equals(requirement.getBoolean("negate"));
    }

    public abstract boolean _isMet(Client client);

    public boolean isMet(Client client)
    {
        if (isRequirementRequired(client))
        {
            return _isMet(client) == !this.negate;
        }

        return true;
    }

    private boolean isRequirementRequired(Client client)
    {
        if (this.condition != null && DialogCondition.is(this.condition, client))
        {
            // Skip ("pass")
            return false;
        }

        return true;
    }
}
