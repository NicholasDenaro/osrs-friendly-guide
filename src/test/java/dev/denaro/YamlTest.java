package dev.denaro;

import dev.denaro.yaml.Yaml;
import dev.denaro.yaml.YamlValue;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.text.ParseException;

public class YamlTest
{
    public static void main(String[] args) throws IOException, ParseException {
        String yamlStr = "type: Combat # comment\n" +
                "requirements:\n" +
                "  - type: Skill\n" +
                "    name: Combat\n" +
                "    level: 40\n" +
                "    levelMax: 128\n" +
                "messages:\n" +
                "  - Vannaka can give you a slayer task.\n" +
                "  - He's in the Edgeville Dungeon, west of Varrock.\n" +
                "  - If you find a brass key, you can enter the dungeon from the locked house west of the cooking guild.";
        InputStream is = new ByteArrayInputStream(yamlStr.getBytes(StandardCharsets.UTF_8));
        YamlValue value = new Yaml().load(is);

        System.out.println(value);
    }
}
