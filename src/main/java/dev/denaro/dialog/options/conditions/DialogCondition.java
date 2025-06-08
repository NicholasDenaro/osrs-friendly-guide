package dev.denaro.dialog.options.conditions;

import dev.denaro.dialog.Dialog;
import net.runelite.api.Client;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public abstract class DialogCondition
{
    static
    {
        map = new HashMap<>();
        System.out.println("Loading dialog conditions");

        InputStream conditionsFolder = Dialog.class.getResourceAsStream("/dev/denaro/dialog/options/conditions");
        BufferedReader reader = new BufferedReader(new InputStreamReader(conditionsFolder));
        ArrayList<String> files = new ArrayList<>();
        String resource;
        try
        {
            while((resource = reader.readLine()) != null) {
                files.add("/dev/denaro/dialog/options/conditions/" + resource);
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
    private static Map<String, DialogCondition> map;
    public static void register(String key, DialogCondition condition)
    {
        map.put(key, condition);
        System.out.println("Registered "+ key + " " + condition);
    }

    public static boolean is(String condition, Client client)
    {
        if (condition.startsWith("not "))
        {
            condition = condition.substring(4);
            return !map.get(condition)._is(client);
        }
        else
        {
            return map.get(condition)._is(client);
        }
    }

    public abstract boolean _is(Client client);
}
