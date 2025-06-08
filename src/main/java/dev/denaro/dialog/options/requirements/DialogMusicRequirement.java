package dev.denaro.dialog.options.requirements;

import net.runelite.api.Client;
import net.runelite.api.gameval.DBTableID;

import java.lang.reflect.Field;
import java.util.Map;

public class DialogMusicRequirement extends DialogRequirement
{
    String quest;
    String track;
    private Boolean isMet = null;

    static
    {
        DialogRequirement.RegisterCreateCall("music", DialogMusicRequirement::create);
    }
    public static DialogMusicRequirement create(Map<String, Object> requirementMap)
    {
        DialogMusicRequirement req = new DialogMusicRequirement();
        req.quest = (String)requirementMap.get("name");
        req.track = (String)requirementMap.get("track");

        req.setup(requirementMap);
        return req;
    }

    @Override
    public boolean isMet(Client client)
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
        } catch (Exception ignored)
        {

        }
        return false;
    }
}
