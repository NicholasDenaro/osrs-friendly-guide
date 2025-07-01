package dev.denaro;

import ch.qos.logback.classic.Level;
import dev.denaro.dialog.Dialog;
import dev.denaro.dialog.DialogMessage;
import dev.denaro.dialog.DialogOption;
import dev.denaro.dialog.options.conditions.DialogCondition;
import dev.denaro.dialog.options.requirements.DialogRequirement;
import net.runelite.api.Client;
import net.runelite.api.Player;
import net.runelite.api.Skill;
import net.runelite.api.WorldType;
import net.runelite.client.config.ConfigManager;
import org.mockito.Mockito;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.http.HttpClient;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static net.runelite.api.gameval.DBTableID.Music.Row;
import static org.mockito.Mockito.*;

public class DialogTest
{
    private static final Logger logger = LoggerFactory.getLogger(DialogTest.class);

    public static void main(String[] args) throws InterruptedException
    {
        DialogCondition.registerAllConditions();
        DialogRequirement.registerAllRequirements();
        ch.qos.logback.classic.Logger logger = (ch.qos.logback.classic.Logger) LoggerFactory.getLogger(Logger.ROOT_LOGGER_NAME);
        logger.setLevel(Level.ALL);

        FriendlyGuideConfig config = mock(FriendlyGuideConfig.class);

        Mockito.when(config.etag()).thenReturn("a");
        Mockito.when(config.showIntroduction()).thenReturn(false);

        ConfigManager configManager = mock(ConfigManager.class);
        doAnswer((a) -> {
            String etag = a.getArgument(2);
            Mockito.when(config.etag()).thenReturn(etag);
            return null;
        }).when(configManager).setConfiguration(Mockito.eq("friendlyGuide"), Mockito.eq("etag"), Mockito.anyString());


        doAnswer((a) -> {
            String data = a.getArgument(2);
            Mockito.when(config.data()).thenReturn(data);
            return null;
        }).when(configManager).setConfiguration(Mockito.eq("friendlyGuide"), Mockito.eq("data"), Mockito.anyString());

        logger.info("Loading data");
        new DialogDataLoader(HttpClient.newHttpClient(), config, configManager).Load();
        logger.info("Finished loading data");

        Client clientMock = mock(Client.class);
        Mockito.when(clientMock.getWorldType()).thenReturn(EnumSet.of(WorldType.MEMBERS));
        int[] musicUnlocks = new int[]{Row.MUSIC_7TH_REALM};
        doAnswer((a) ->
        {
            when(clientMock.getIntStack()).thenReturn(new int[]{Arrays.stream(musicUnlocks).anyMatch(music -> music == (int)a.getArgument(1)) ? 1 : 0}); // just make it so all music is unlocked?
            return null;
        }).when(clientMock).runScript(Mockito.eq(252), Mockito.anyInt());
        Player playerMock = mock(Player.class);
        Mockito.when(clientMock.getLocalPlayer()).thenReturn(playerMock);

        Mockito.when(playerMock.getCombatLevel()).thenReturn(50);
        Mockito.when(clientMock.getRealSkillLevel(Skill.WOODCUTTING)).thenReturn(50);
        Mockito.when(clientMock.getRealSkillLevel(Skill.ATTACK)).thenReturn(20);
        Mockito.when(clientMock.getRealSkillLevel(Skill.CRAFTING)).thenReturn(10);

        FriendlyGuidePlugin plugin = mock(FriendlyGuidePlugin.class);
        Mockito.when(plugin.getClient()).thenReturn(clientMock);
        Mockito.when(plugin.getConfig()).thenReturn(config);

        Dialog treeOptions = Dialog.createDialogTree(plugin);

        ArrayList<Integer> subOptions = new ArrayList<>();

        if (treeOptions instanceof DialogOption)
        {
            for (int i = 0; i < ((DialogOption) treeOptions).options.length; i++)
            {
                logger.info("Running tree with dialog option: " + i + " " + ((DialogOption) treeOptions).options[i].text + "\n");
                Dialog dialog = treeOptions;

                if (subOptions.isEmpty() && ((DialogOption) treeOptions).options[i].next() instanceof DialogOption)
                {
                    int optionCount = ((DialogOption)((DialogOption) treeOptions).options[i].next()).options.length;
                    subOptions = new ArrayList<Integer>(IntStream.range(0, optionCount).boxed().collect(Collectors.toList()));
                    logger.info("Found sub-options: " + subOptions);
                }

                while (dialog != null)
                {
                    if (dialog instanceof DialogMessage)
                    {
                        DialogMessage dm = (DialogMessage) dialog;
                        logger.info(dm.speaker + ":" + dm.message);

                        dialog = dialog.next();
                    }
                    else if (dialog instanceof DialogOption)
                    {
                        DialogOption options = (DialogOption) dialog;
                        logger.info("Options:\n- " + Arrays.stream(options.options).map(opt -> opt.text).collect(Collectors.joining("\n- ")));

                        int index = i;
                        if (dialog != treeOptions && !subOptions.isEmpty())
                        {
                            index = subOptions.remove(0);
                        }

                        dialog = options.options[index].next();
                    }
                    else
                    {
                        logger.info("Unknown dialog type");
                    }
                }

                if (!subOptions.isEmpty())
                {
                    // Replay with another sub option
                    i--;
                }
            }
        }
    }
}
