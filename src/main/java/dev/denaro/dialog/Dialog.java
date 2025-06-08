package dev.denaro.dialog;

import dev.denaro.FriendlyGuidePlugin;
import dev.denaro.dialog.options.DialogResponse;
import dev.denaro.dialog.options.DialogSkillRequirement;
import dev.denaro.dialog.options.DialogType;
import net.runelite.api.Varbits;
import org.yaml.snakeyaml.Yaml;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public abstract class Dialog
{
    public static Map<DialogType, List<DialogResponse>> dialogResponses = new HashMap();

    private Dialog next;
    public Dialog setNext(Dialog next)
    {
        this.next = next;
        return this.next;
    }

    public Dialog next()
    {
        return this.next;
    }

    public static Dialog createDialogTree(FriendlyGuidePlugin plugin)
    {
        Dialog dialog = new DialogMessage(DialogMessage.DialogSpeaker.Player, "Hello. Who are you?");


        dialog.setNext(new DialogMessage(DialogMessage.DialogSpeaker.Guide, "I'm the Friendly Guide."))
            .setNext(new DialogMessage(DialogMessage.DialogSpeaker.Player, "What can you do for me?"))
            .setNext(new DialogMessage(DialogMessage.DialogSpeaker.Guide, "I can give you ideas for what to do next."))
            .setNext(new DialogMessage(DialogMessage.DialogSpeaker.Player, "That sounds nice."))
            .setNext(new DialogOption(
                    new DialogOption.Option[]{
                            new DialogOption.Option("Combat", () -> Dialog.buildOption(plugin, DialogType.Combat, dr -> true)),
                            new DialogOption.Option("Quest", () -> Dialog.buildOption(plugin, DialogType.Quest, dr -> true)),
                            new DialogOption.Option("Money", () -> Dialog.buildOption(plugin, DialogType.Money, dr -> true)),
                            new DialogOption.Option("Skill", () -> new DialogOption(new DialogOption.Option[]{
                                    new DialogOption.Option("Gather", () -> Dialog.buildOption(plugin, DialogType.Item, dr -> "Gather".equals(dr.subType))),
                                    new DialogOption.Option("Refine", () -> Dialog.buildOption(plugin, DialogType.Item, dr -> "Refine".equals(dr.subType))),
                                    new DialogOption.Option("Magic", () -> Dialog.buildOption(plugin, DialogType.Item, dr -> "Magic".equals(dr.subType))),
                                    new DialogOption.Option("Other", () -> Dialog.buildOption(plugin, DialogType.Item, dr -> "Other".equals(dr.subType))),
                            })),
                            new DialogOption.Option("Item", () -> new DialogOption(new DialogOption.Option[]{
                                    new DialogOption.Option("Weapon", () -> Dialog.buildOption(plugin, DialogType.Item, dr -> "Weapon".equals(dr.subType))),
                                    new DialogOption.Option("Armor", () -> Dialog.buildOption(plugin, DialogType.Item, dr -> "Armor".equals(dr.subType))),
                                    new DialogOption.Option("Potion", () -> Dialog.buildOption(plugin, DialogType.Item, dr -> "Potion".equals(dr.subType))),
                                    new DialogOption.Option("Teleport", () -> Dialog.buildOption(plugin, DialogType.Item, dr -> "Teleport".equals(dr.subType))),
                            })),
                            new DialogOption.Option("Explore", () -> Dialog.buildOption(plugin, DialogType.Explore, dr -> true)),
                    })
            );

        return dialog;
    }

    private static Dialog buildOption(FriendlyGuidePlugin plugin, DialogType type, Predicate<DialogResponse> predicate)
    {
        List<DialogResponse> dialogs = Dialog.dialogResponses.get(type).stream().filter(response -> !response.ironman || (plugin.getClient().getVarbitValue(Varbits.ACCOUNT_TYPE) == 0)).filter(predicate).filter(response ->
                response.requirements.stream().allMatch(requirement -> requirement.isMet(plugin.getClient()))
        ).collect(Collectors.toList());

        if (dialogs.isEmpty())
        {
            System.out.println("No dialogs found");
            return new DialogMessage(DialogMessage.DialogSpeaker.Guide, "I don't have anything for you about this right now.");
        }

        int index = (int) (Math.random() * dialogs.size());
        return dialogs.get(index).createDialog();
    }

    static {
        System.out.println("loading yml files");
        Dialog.dialogResponses.put(DialogType.Combat, new ArrayList<>());
        Dialog.dialogResponses.put(DialogType.Item, new ArrayList<>());
        Dialog.dialogResponses.put(DialogType.Money, new ArrayList<>());
        Dialog.dialogResponses.put(DialogType.Explore, new ArrayList<>());
        Dialog.dialogResponses.put(DialogType.Quest, new ArrayList<>());
        Dialog.dialogResponses.put(DialogType.Skill, new ArrayList<>());
        try
        {
            InputStream dialogFolder = Dialog.class.getResourceAsStream("/dialogs");
            BufferedReader reader = new BufferedReader(new InputStreamReader(dialogFolder));
            ArrayList<String> files = new ArrayList<>();
            String resource;
            while((resource = reader.readLine()) != null) {
                files.add("/dialogs/" + resource);
            }

            reader.close();

            System.out.println(files);

            for (String file : files)
            {
                InputStream dialogFileStream = Dialog.class.getResourceAsStream(file);
                if (dialogFileStream != null)
                {
                    System.out.println("Loading file: " + file);
                    Yaml yaml = new Yaml();
                    Map<String, Object> document = yaml.load(dialogFileStream);

                    String type = (String)document.get("type");
                    String subtype = (String)document.get("subType");

                    Boolean ironmanBool = (Boolean)document.get("ironman");
                    if (ironmanBool == null)
                    {
                        ironmanBool = false;
                    }
                    List<Map<String, Object>> requirements = (List<Map<String, Object>>)document.get("requirements");
                    List<String> messages = (List<String>)document.get("messages");

                    List<DialogSkillRequirement> requirementList = new ArrayList<>();
                    for (Map<String, Object> requirement : requirements)
                    {
                        if ("skill".equalsIgnoreCase((String)requirement.get("type")))
                        {
                            requirementList.add(new DialogSkillRequirement((String)requirement.get("name"), (String)requirement.get("if"), (int)requirement.get("level"), (int)requirement.get("levelMax")));
                        }
                    }

                    switch (type.toLowerCase())
                    {
                        case "combat":
                            Dialog.dialogResponses.get(DialogType.Combat).add(new DialogResponse(messages, requirementList, subtype, ironmanBool));
                            break;
                        case "item":
                            Dialog.dialogResponses.get(DialogType.Item).add(new DialogResponse(messages, requirementList, subtype, ironmanBool));
                            break;
                        case "money":
                            Dialog.dialogResponses.get(DialogType.Money).add(new DialogResponse(messages, requirementList, subtype, ironmanBool));
                            break;
                        case "quest":
                            Dialog.dialogResponses.get(DialogType.Quest).add(new DialogResponse(messages, requirementList, subtype, ironmanBool));
                            break;
                        case "explore":
                            Dialog.dialogResponses.get(DialogType.Explore).add(new DialogResponse(messages, requirementList, subtype, ironmanBool));
                            break;
                        case "skill":
                            Dialog.dialogResponses.get(DialogType.Skill).add(new DialogResponse(messages, requirementList, subtype, ironmanBool));
                            break;

                    }
                }
            }

            System.out.println("Finished loading dialogs");
//            System.out.println(Dialog.dialogResponses);
        }
        catch (IOException exception)
        {
            System.out.println("Failed to load files:");
            exception.printStackTrace();
        }
        catch (Exception exception)
        {
            System.out.println("Other Exception:");
            exception.printStackTrace();
        }

    }
}
