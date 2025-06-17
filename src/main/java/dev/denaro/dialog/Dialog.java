package dev.denaro.dialog;

import dev.denaro.FriendlyGuidePlugin;
import dev.denaro.dialog.options.*;
import dev.denaro.dialog.options.requirements.*;
import dev.denaro.yaml.*;
import dev.denaro.yaml.types.YamlArray;
import dev.denaro.yaml.types.YamlObject;
import dev.denaro.yaml.types.YamlValue;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.ParseException;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public abstract class Dialog
{
    public static Map<DialogType, List<DialogResponse>> dialogResponses = new HashMap();

    protected Dialog()
    {

    }

    private Runnable runnable;
    protected Dialog(Runnable runnable)
    {
        this.runnable = runnable;
    }

    private Dialog next;
    public Dialog setNext(Dialog next)
    {
        this.next = next;
        return this.next;
    }

    public Dialog next()
    {
        if (this.runnable != null)
        {
            this.runnable.run();
        }
        return this.next;
    }

    public static Dialog createDialogTree(FriendlyGuidePlugin plugin)
    {
        Dialog intro = new DialogMessage(DialogMessage.DialogSpeaker.Player, "Hello. Who are you?");


        Dialog endIntro = intro.setNext(new DialogMessage(DialogMessage.DialogSpeaker.Guide, "I'm the Friendly Guide."))
            .setNext(new DialogMessage(DialogMessage.DialogSpeaker.Player, "What can you do for me?"))
            .setNext(new DialogMessage(DialogMessage.DialogSpeaker.Guide, "I can give you ideas for what to do next."))
            .setNext(new DialogMessage(DialogMessage.DialogSpeaker.Player, "That sounds nice.", plugin::setIntroduced));



        Dialog options = new DialogOption(
                new DialogOption.Option[]{
                        new DialogOption.Option("Combat", () -> Dialog.buildOption(plugin, getCombatResponses())),
                        new DialogOption.Option("Quest", () -> Dialog.buildOption(plugin, getQuestResponses(), dr -> dr.isQuestUnstarted(plugin.getClient()))),
                        new DialogOption.Option("Money", () -> Dialog.buildOption(plugin, getMoneyResponses())),
                        new DialogOption.Option("Skill", () -> new DialogOption(new DialogOption.Option[]{
                                new DialogOption.Option("Gather", () -> Dialog.buildOption(plugin, getSkillResponses(), dr -> dr.isSkillGroup("Gather"))),
                                new DialogOption.Option("Refine", () -> Dialog.buildOption(plugin, getSkillResponses(), dr -> dr.isSkillGroup("Refine"))),
                                new DialogOption.Option("Combat", () -> Dialog.buildOption(plugin, getSkillResponses(), dr -> dr.isSkillGroup("Combat"))),
                                new DialogOption.Option("Other", () -> Dialog.buildOption(plugin, getSkillResponses(), dr -> dr.isSkillGroup("Other"))),
                        })),
                        new DialogOption.Option("Item", () -> new DialogOption(new DialogOption.Option[]{
                                new DialogOption.Option("Weapon", () -> Dialog.buildOption(plugin, getItemResponses(), dr -> dr.isItemType("Weapon"))),
                                new DialogOption.Option("Armor", () -> Dialog.buildOption(plugin, getItemResponses(), dr -> dr.isItemType("Armor"))),
                                new DialogOption.Option("Potion", () -> Dialog.buildOption(plugin, getItemResponses(), dr -> dr.isItemType("Potion"))),
                                new DialogOption.Option("Teleport", () -> Dialog.buildOption(plugin, getItemResponses(), dr -> dr.isItemType("Teleport"))),
                                new DialogOption.Option("Food", () -> Dialog.buildOption(plugin, getItemResponses(), dr -> dr.isItemType("Food"))),
                        })),
                        new DialogOption.Option("Explore", () -> Dialog.buildOption(plugin, getExploreResponses())),
                });

        endIntro.setNext(options);

        return plugin.getConfig().showIntroduction() ? intro : options;
    }

    private static Stream<DialogCombatResponse> getCombatResponses()
    {
        return getResponses(DialogType.Combat, DialogCombatResponse.class);
    }

    private static Stream<DialogQuestResponse> getQuestResponses()
    {
        return getResponses(DialogType.Quest, DialogQuestResponse.class);
    }

    private static Stream<DialogMoneyResponse> getMoneyResponses()
    {
        return getResponses(DialogType.Money, DialogMoneyResponse.class);
    }

    private static Stream<DialogExploreResponse> getExploreResponses()
    {
        return getResponses(DialogType.Explore, DialogExploreResponse.class);
    }

    private static Stream<DialogItemResponse> getItemResponses()
    {
        return getResponses(DialogType.Item, DialogItemResponse.class);
    }

    private static Stream<DialogSkillResponse> getSkillResponses()
    {
        return getResponses(DialogType.Skill, DialogSkillResponse.class);
    }

    private static <T extends DialogResponse> Stream<T> getResponses(DialogType type, Class<T> cl)
    {
        return Dialog.dialogResponses.get(type).stream().filter(cl::isInstance).map(response -> (T) response);
    }

    private static <T extends DialogResponse> Dialog buildOption(FriendlyGuidePlugin plugin, Stream<T> dialogs)
    {
        try
        {
            return buildOption(plugin, dialogs, t -> true);
        }
        catch (Exception ex)
        {
            return new DialogOption(new DialogOption.Option[]{new DialogOption.Option("Error", new DialogMessage(DialogMessage.DialogSpeaker.Guide, "Error"))});
        }
    }

    private static <T extends DialogResponse> Dialog buildOption(FriendlyGuidePlugin plugin, Stream<T> dialogs, Predicate<T> predicate) {
        List<T> list = dialogs.peek(System.out::println).filter(predicate).filter(response ->
                response.requirements.stream().allMatch(requirement -> requirement.isMet(plugin.getClient()))
        ).collect(Collectors.toList());

        System.out.println("Filtered dialogs:" + list);

        if (list.isEmpty())
        {
            System.out.println("No dialogs found");
            return new DialogMessage(DialogMessage.DialogSpeaker.Guide, "I don't have anything for you about this right now.");
        }

        int index = (int) (Math.random() * list.size());
        try
        {
            return list.get(index).createDialog(plugin.getClient());
        }
        catch (Exception e)
        {
            return new DialogOption(new DialogOption.Option[]{new DialogOption.Option("Error", new DialogMessage(DialogMessage.DialogSpeaker.Guide, "Error"))});
        }
    }

    public static void loadDynamicYaml(String yamlString)
    {
        System.out.println("Parsing dynamic yaml data");
        AtomicInteger counter = new AtomicInteger(0);
        try
        {
            Stream<YamlValue> stream = new Yaml().loadAll(yamlString).stream();
            stream.forEach(doc -> {
                System.out.println("Loading doc: " + doc);

                if (doc instanceof YamlObject)
                {
                    try {
                        loadDocument((YamlObject)doc);
                    } catch (Exception e) {
                        System.out.println("Doc not loaded. error");
                        System.err.println("Doc not loaded. error");
                        e.printStackTrace();
                    }
                    System.out.println("doc loaded");
                    counter.incrementAndGet();
                }
                else
                {
                    System.out.println("doc NOT loaded, incorrect type");
                }
            });
            System.out.println("loaded " + counter.get() + " docs");
        }
        catch (IOException | ParseException ioException)
        {
            ioException.printStackTrace();
        }
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
                    YamlObject document = (YamlObject) new Yaml().load(dialogFileStream);

                    loadDocument(document);
                }
            }

            System.out.println("Finished loading dialogs");
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

    private static void loadDocument(YamlObject document) throws Exception {
        String type = (String)(document.getSimpleValue("type").getString());

        YamlArray requirements = document.getArrayOrDefault("requirements", new YamlArray());
        YamlArray messages = document.getArray("messages");

        List<DialogRequirement> requirementList = new ArrayList<>();
        for (YamlValue requirementVal : requirements)
        {
            if (requirementVal instanceof YamlObject)
            {
                YamlObject requirementMap = (YamlObject) requirementVal;

                String requirementType = requirementMap.getSimpleValue("type").getString().toLowerCase();
                DialogRequirement req = DialogRequirement.New(requirementType, requirementMap);
                if (req != null) {
                    requirementList.add(req);
                }
            }

        }

        switch (type.toLowerCase())
        {
            case "combat":
                Dialog.dialogResponses.get(DialogType.Combat).add(new DialogCombatResponse(messages, requirementList));
                break;
            case "item":
                String itemType = document.getSimpleValue("itemType").getString();
                Dialog.dialogResponses.get(DialogType.Item).add(new DialogItemResponse(messages, requirementList, itemType));
                break;
            case "money":
                Dialog.dialogResponses.get(DialogType.Money).add(new DialogMoneyResponse(messages, requirementList));
                break;
            case "quest":
                String quest = document.getSimpleValue("quest").getString();
                Dialog.dialogResponses.get(DialogType.Quest).add(new DialogQuestResponse(messages, requirementList, quest));
                break;
            case "explore":
                Dialog.dialogResponses.get(DialogType.Explore).add(new DialogExploreResponse(messages, requirementList));
                break;
            case "skill":
                String skillGroup = document.getSimpleValue("skillGroup").getString();
                Dialog.dialogResponses.get(DialogType.Skill).add(new DialogSkillResponse(messages, requirementList, skillGroup));
                break;
        }
    }
}
