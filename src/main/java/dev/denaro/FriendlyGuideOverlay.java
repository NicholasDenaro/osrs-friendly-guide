package dev.denaro;

import net.runelite.api.Point;
import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.util.ImageUtil;

import java.awt.*;
import java.awt.image.BufferedImage;

public class FriendlyGuideOverlay extends Overlay
{
    private static final BufferedImage[] images = new BufferedImage[4];

    static
    {
        try
        {
            images[0] = ImageUtil.loadImageResource(FriendlyGuideOverlay.class, "/images/click/click1.png");
            images[1] = ImageUtil.loadImageResource(FriendlyGuideOverlay.class, "/images/click/click2.png");
            images[2] = ImageUtil.loadImageResource(FriendlyGuideOverlay.class, "/images/click/click3.png");
            images[3] = ImageUtil.loadImageResource(FriendlyGuideOverlay.class, "/images/click/click4.png");
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
    }

    private class ClickAnimation
    {
        private Point point;
        private int timer;

        public ClickAnimation(Point point)
        {
            this.point = point;
            timer = 16;
        }

        public void render(Graphics2D graphics)
        {
            BufferedImage image = images[(int)(timer-- * 1.0 / 16 * 3)];
            graphics.drawImage(image, this.point.getX() - image.getWidth() / 2, this.point.getY() - image.getHeight() / 2, null);
        }

        public boolean isFinished()
        {
            return timer <= 0;
        }
    }

    private ClickAnimation click;

    @Override
    public Dimension render(Graphics2D graphics)
    {
        if (click != null)
        {
            click.render(graphics);
            if (click.isFinished())
            {
                click = null;
            }
        }

        return null;
    }

    public void setClick(Point point)
    {
        Point p = new Point(point.getX() - this.getBounds().x, point.getY() - this.getBounds().y);
        click = new ClickAnimation(p);
    }
}
