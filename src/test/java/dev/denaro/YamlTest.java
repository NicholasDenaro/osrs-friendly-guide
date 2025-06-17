package dev.denaro;

import dev.denaro.dialog.Dialog;

public class YamlTest
{
    public static void main(String[] args) {
        String yamlStr = "type: Item\n" +
                "itemType: Weapon\n" +
                "requirements:\n" +
                "  - type: Skill\n" +
                "    name: Attack\n" +
                "    level: 1\n" +
                "    levelMax: 30\n" +
                "messages:\n" +
                "  - There's a sword shop in Varrock.\n" +
                "  - I would recommend a shortsword. They attack quickly and deal moderate damage.";

        Dialog.loadDynamicYaml(yamlStr);
    }
}
