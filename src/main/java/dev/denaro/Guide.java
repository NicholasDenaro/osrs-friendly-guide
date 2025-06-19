package dev.denaro;

import net.runelite.api.*;
import net.runelite.api.coords.LocalPoint;
import net.runelite.api.coords.WorldPoint;
import net.runelite.client.callback.ClientThread;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Arrays;

public class Guide
{
    private static final Logger logger = LoggerFactory.getLogger(Guide.class);

    private static final GuidePosition GEGuide = new GuidePosition(new WorldPoint(3157, 3490, 0), 0);
    private static final GuidePosition LumbridgeGuide = new GuidePosition(new WorldPoint(3217, 3223, 0), 1536);

    private static ArrayList<Guide> guides = new ArrayList<>();

    public static ArrayList<Guide> allGuides()
    {
        return guides;
    }

    public static void unload(ClientThread clientThread)
    {
        for (Guide guide : guides)
        {
            guide.hide(clientThread);
        }
    }

    public static void load(ClientThread clientThread)
    {
        for (Guide guide : guides)
        {
            guide.show(clientThread);
        }
    }

    public static void firstLoad(FriendlyGuidePlugin plugin, ClientThread clientThread)
    {
        clientThread.invokeLater(() ->
        {
            new Guide(plugin, Guide.GEGuide);
            new Guide(plugin, Guide.LumbridgeGuide);

            Guide.load(clientThread);
        });
    }

    private static ModelData model;

    private RuneLiteObject object;
    private FriendlyGuidePlugin plugin;
    private GuidePosition position;
    private boolean shown = false;
    public Guide(FriendlyGuidePlugin plugin, GuidePosition position)
    {
        this.plugin = plugin;
        this.position = position;
        guides.add(this);

        if (model == null)
        {
            logger.debug("Using model ids: " + this.plugin.getConfig().modelIds());

            int[] ids = Arrays.stream(this.plugin.getConfig().modelIds().split(", ")).mapToInt(Integer::parseInt).toArray();

            ArrayList<ModelData> data = new ArrayList<>();
            for (int id : ids) {
                data.add(this.plugin.getClient().loadModelData(id));
            }

            model = this.plugin.getClient().mergeModels(data.toArray(new ModelData[data.size()]), data.size());
        }

        Player player = this.plugin.getClient().getLocalPlayer();
        this.object = plugin.getClient().createRuneLiteObject();
        this.object.setModel(model.light(64, 850, -30, -50, -30));

        this.object.setAnimationController(new AnimationController(plugin.getClient(), -1));
    }

    public static void tickAll()
    {
        for (Guide guide : guides)
        {
            guide.tick();
        }
    }

    public void tick()
    {
        if (LocalPoint.fromWorld(this.plugin.getClient().getLocalPlayer().getWorldView(), position.point) != null)
        {
            this.show(null);
        }
        else
        {
            this.hide(null);
        }
    }

    public Model getModel()
    {
        return this.object.getModel();
    }

    public int getOrientation()
    {
        return this.object.getOrientation();
    }

    public int getX()
    {
        return this.object.getX();
    }

    public int getY()
    {
        return this.object.getY();
    }

    public int getZ()
    {
        return this.object.getZ();
    }

    public void show(ClientThread clientThread)
    {
        if (this.shown)
        {
            if (!plugin.getClient().isRuneLiteObjectRegistered(this.object))
            {
                plugin.getClient().registerRuneLiteObject(this.object);
            }
            return;
        }
        this.shown = true;

        Runnable r = () ->
        {
            Player player = this.plugin.getClient().getLocalPlayer();
            if (player == null)
            {
                this.shown = false;
                return;
            }

            LocalPoint local = LocalPoint.fromWorld(player.getWorldView(), position.point);
            if (local == null)
            {
                this.shown = false;
                return;
            }
            this.object.setLocation(local, position.point.getPlane());
            this.object.setOrientation(position.orientation);
            this.object.setActive(true);
            this.shown = true;
            logger.debug(String.valueOf(this.object.getLocation()));
        };

        if (this.plugin.getClient().isClientThread())
        {
            r.run();
        }
        else
        {
            clientThread.invokeLater(r);
        }
    }

    public void hide(ClientThread clientThread)
    {
        if (!this.shown)
        {
            return;
        }

        Runnable r = () ->
        {
            this.object.setActive(false);
            this.shown = false;
        };

        this.shown = false;

        if (clientThread != null)
        {
            clientThread.invokeLater(r);
        }
        else
        {
            r.run();
        }
    }
}
