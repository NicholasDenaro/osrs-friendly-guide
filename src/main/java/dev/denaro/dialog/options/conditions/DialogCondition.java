package dev.denaro.dialog.options.conditions;

import net.runelite.api.Client;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

public abstract class DialogCondition
{
    private static final Logger logger = LoggerFactory.getLogger(DialogCondition.class);
    private static Map<String, DialogCondition> map;

    public static void registerAllConditions()
    {
        logger.debug("Loading dialog conditions");
        map = new HashMap<>();
        DialogCondition.register("ironman", new DialogIronmanCondition());
        DialogCondition.register("member", new DialogMembersCondition());
        DialogCondition.register("members", new DialogMembersCondition());
    }

    private static void register(String key, DialogCondition condition)
    {
        map.put(key, condition);
        logger.debug("Registered " + key + " " + condition);
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
            logger.info(condition);
            return map.get(condition)._is(client);
        }
    }

    public abstract boolean _is(Client client);
}
