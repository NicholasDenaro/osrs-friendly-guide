package dev.denaro;

import net.runelite.api.coords.WorldPoint;

public class GuidePosition
{
    WorldPoint point;
    int orientation;

    public GuidePosition(WorldPoint point, int orientation)
    {
        this.point = point;
        this.orientation = orientation;
    }
}
