package dev.denaro.dialog.options.requirements;

import net.runelite.api.Client;
import net.runelite.api.Varbits;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;


public abstract class DialogRequirement
{
    static
    {
        // TODO: Load the requirement classes at runtime so they register their create calls.
    }

    private static Map<String, Function<Map<String, Object>, DialogRequirement>> CreateCalls = new HashMap<>();
    public static void RegisterCreateCall(String key, Function<Map<String, Object>, DialogRequirement> func)
    {
        CreateCalls.put(key, func);
        System.out.println("Registered " + key + " Requirement");
    }
    public static DialogRequirement New(String type, Map<String, Object> requirementMap)
    {
        Function<Map<String, Object>, DialogRequirement> func = CreateCalls.get(type);
        return func.apply(requirementMap);
    }

    private String condition;

    protected void setup(Map<String, Object> requirementMap)
    {
        this.condition = (String)requirementMap.get("if");
    }

    public abstract boolean isMet(Client client);

    public boolean isRequirementRequired(Client client)
    {
        if ("ironman".equals(this.condition) && client.getVarbitValue(Varbits.ACCOUNT_TYPE) == 0)
        {
            // Skip ("pass") since account type 0 is not ironman
            return true;
        }

        return false;
    }
}
