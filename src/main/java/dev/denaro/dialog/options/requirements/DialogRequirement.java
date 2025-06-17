package dev.denaro.dialog.options.requirements;

import dev.denaro.dialog.Dialog;
import dev.denaro.dialog.options.conditions.DialogCondition;
import dev.denaro.yaml.types.YamlObject;
import dev.denaro.yaml.types.YamlSimpleValue;
import net.runelite.api.Client;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public abstract class DialogRequirement
{
    static
    {
        CreateCalls = new HashMap<>();
        System.out.println("Loading dialog requirements");

        InputStream requirementsFolder = Dialog.class.getResourceAsStream("/dev/denaro/dialog/options/requirements");
        BufferedReader reader = new BufferedReader(new InputStreamReader(requirementsFolder));
        ArrayList<String> files = new ArrayList<>();
        String resource;
        try
        {
            while((resource = reader.readLine()) != null) {
                files.add("/dev/denaro/dialog/options/requirements/" + resource);
            }

            reader.close();

            System.out.println(files);

            for (String file : files)
            {
                String className = file.replaceAll("/", ".").replaceAll(".class", "").substring(1);

                System.out.println("Attempting to load file: " + file + " as: " + className);
                Class.forName(className);
            }

        }
        catch(Exception ex)
        {
            ex.printStackTrace();
        }
    }

    private static Map<String, Function<YamlObject, DialogRequirement>> CreateCalls;
    public static void RegisterCreateCall(String key, Function<YamlObject, DialogRequirement> func)
    {
        CreateCalls.put(key, func);
        System.out.println("Registered " + key + " Requirement");
    }
    public static DialogRequirement New(String type, YamlObject requirementMap)
    {
        Function<YamlObject, DialogRequirement> func = CreateCalls.get(type);
        return func.apply(requirementMap);
    }

    private String condition;
    private boolean negate;

    protected void setup(YamlObject requirementMap)
    {
        this.condition = requirementMap.getSimpleValueOrDefault("if", YamlSimpleValue.Null).getString();
        this.negate = requirementMap.getSimpleValueOrDefault("negate", YamlSimpleValue.fromBoolean(false)).getBoolean();
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
