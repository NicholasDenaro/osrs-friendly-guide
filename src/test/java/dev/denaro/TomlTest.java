package dev.denaro;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import dev.denaro.dialog.Dialog;
import dev.denaro.dialog.options.conditions.DialogCondition;
import dev.denaro.dialog.options.requirements.DialogRequirement;
import org.slf4j.LoggerFactory;

public class TomlTest
{
    public static void main(String[] args)
    {
        DialogCondition.registerAllConditions();
        DialogRequirement.registerAllRequirements();
        ch.qos.logback.classic.Logger logger = (ch.qos.logback.classic.Logger) LoggerFactory.getLogger(Logger.ROOT_LOGGER_NAME);
        logger.setLevel(Level.ALL);
        String tomlStr = "type = \"Item\"\n" +
                "itemType = \"Weapon\"\n" +
                "messages = [\n" +
                "  \"There's a sword shop in Varrock.\",\n" +
                "  \"I would recommend a shortsword. They attack quickly and deal moderate damage.\"\n" +
                "]\n" +
                "\n" +
                "[[requirements]]\n" +
                "type = \"Skill\"\n" +
                "name = \"Attack\"\n" +
                "level = 1\n" +
                "levelMax = 30\n";

        Dialog.loadDynamicToml(tomlStr);
    }
}
