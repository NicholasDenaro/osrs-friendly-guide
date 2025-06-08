package dev.denaro.dialog;

import dev.denaro.FriendlyGuidePlugin;
import dev.denaro.dialog.options.DialogResponse;
import dev.denaro.dialog.options.DialogSkillRequirement;
import dev.denaro.dialog.options.DialogSkillResponse;
import dev.denaro.dialog.options.DialogType;
import org.yaml.snakeyaml.Yaml;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.*;
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
                    new DialogOption.Option[]{new DialogOption.Option("Combat", () -> {
                        new DialogMessage(DialogMessage.DialogSpeaker.Guide, "You can try fighting goblins.");
                        List<DialogResponse> dialogs = (List<DialogResponse>) Dialog.dialogResponses.get(DialogType.Combat).stream().filter(response -> {
                            if (response instanceof DialogSkillResponse)
                            {
                                return ((DialogSkillResponse)response).requirements.stream().allMatch(requirement -> requirement.isMet(plugin.getClient()));
                            }

                            return false;
                        }).collect(Collectors.toList());

                        if (dialogs.isEmpty())
                        {
                            System.out.println("No dialogs found");
                        }

                        int index = (int) (Math.random() * dialogs.size());
                        return dialogs.get(index).createDialog();
                    })})
            );

        return dialog;
    }

    static {
        System.out.println("loading yml files");
        Dialog.dialogResponses.put(DialogType.Combat, new ArrayList<>());
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
                    List<Map<String, Object>> requirements = (List<Map<String, Object>>)document.get("requirements");
                    List<String> skills = (List<String>)document.get("skills");
                    List<String> messages = (List<String>)document.get("messages");

                    List<DialogSkillRequirement> requirementList = new ArrayList<>();
                    for (Map<String, Object> requirement : requirements)
                    {
                        if ("skill".equalsIgnoreCase((String)requirement.get("type")))
                        {
                            requirementList.add(new DialogSkillRequirement((String)requirement.get("name"), (int)requirement.get("level"), (int)requirement.get("levelMax")));
                        }
                    }

                    switch (type.toLowerCase())
                    {
                        case "combat":
                            Dialog.dialogResponses.get(DialogType.Combat).add(new DialogSkillResponse(skills, messages, requirementList));
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
