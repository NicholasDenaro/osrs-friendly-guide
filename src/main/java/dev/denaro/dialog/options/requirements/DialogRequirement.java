package dev.denaro.dialog.options.requirements;

import dev.denaro.dialog.Dialog;
import net.runelite.api.Client;
import net.runelite.api.Varbits;

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

    private static Map<String, Function<Map<String, Object>, DialogRequirement>> CreateCalls;
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
        if (this.condition != null)
        {
            System.out.println(this + " has the condition: " + this.condition);
        }
    }

    public abstract boolean isMet(Client client);

    public boolean isRequirementRequired(Client client)
    {
        if ("ironman".equals(this.condition) && client.getVarbitValue(Varbits.ACCOUNT_TYPE) == 0)
        {
            // Skip ("pass") since account type 0 is not ironman
            return false;
        }

        return true;
    }
}
