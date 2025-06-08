package dev.denaro;

import net.runelite.api.*;
import net.runelite.api.coords.LocalPoint;

import java.util.ArrayList;
import java.util.Arrays;

public class Guide
{
    private RuneLiteObject object;
    private FriendlyGuidePlugin plugin;
    public Guide(FriendlyGuidePlugin plugin)
    {
        this.plugin = plugin;

        Player player = this.plugin.getClient().getLocalPlayer();

        System.out.println("Using model ids: " + this.plugin.getConfig().modelIds());

        int[] ids = Arrays.stream(this.plugin.getConfig().modelIds().split(", ")).mapToInt(Integer::parseInt).toArray();

        ArrayList<ModelData> data = new ArrayList<>();
        for (int id : ids) {
            data.add(plugin.getClient().loadModelData(id));
        }

        this.object = plugin.getClient().createRuneLiteObject();
        ModelData mergedModel = plugin.getClient().mergeModels(data.toArray(new ModelData[data.size()]), data.size());
        this.object.setModel(mergedModel.light(64, 850, -30, -50, -30));

        this.object.setLocation(LocalPoint.fromWorld(player.getWorldView(), player.getWorldLocation()), player.getWorldLocation().getPlane());
        this.object.setOrientation(player.getCurrentOrientation());
        this.object.setAnimationController(new AnimationController(plugin.getClient(), -1));
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

    public void show()
    {
        this.object.setActive(true);
        System.out.println("Show guide");
    }
}
