package dev.denaro.dialog.options.requirements;

import dev.denaro.dialog.options.conditions.DialogCondition;
import dev.denaro.yaml.types.YamlObject;
import net.runelite.api.Client;
import net.runelite.api.annotations.Component;
import net.runelite.api.gameval.DBTableID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;

@Component
public class DialogMusicRequirement extends DialogRequirement
{
    private static final Logger logger = LoggerFactory.getLogger(DialogMusicRequirement.class);
    String track;
    private Boolean isMet = null;

    static
    {
        DialogRequirement.RegisterCreateCall("music", DialogMusicRequirement::create);
    }
    public static DialogMusicRequirement create(YamlObject requirementMap) {
        DialogMusicRequirement req = new DialogMusicRequirement();
        req.track = "MUSIC_" + (requirementMap.getSimpleValue("track").getString()).replaceAll(" ", "_").toUpperCase();

        req.setup(requirementMap);
        return req;
    }

    @Override
    public boolean _isMet(Client client)
    {
        if (isMet != null)
        {
            return isMet;
        }

        try
        {
            Field field = DBTableID.Music.Row.class.getDeclaredField(track);
            int musicDbrowId = (int)field.get(null);
            client.runScript(252, musicDbrowId);
            boolean unlocked = client.getIntStack()[0] == 1;

            this.isMet = unlocked;

            return unlocked;
        }
        catch (Exception exception)
        {
            logger.error(("failed to check music requirement"), exception);
        }
        return false;
    }
}
