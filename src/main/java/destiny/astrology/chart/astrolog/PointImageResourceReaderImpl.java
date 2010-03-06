/**
 * @author smallufo 
 * Created on 2008/12/15 at 下午 11:57:23
 */ 
package destiny.astrology.chart.astrolog;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.Locale;

import javax.imageio.ImageIO;

import destiny.astrology.Asteroid;
import destiny.astrology.LunarNode;
import destiny.astrology.Planet;
import destiny.astrology.Point;
import destiny.astrology.LunarNode.NorthSouth;
import destiny.astrology.chart.PointImageResourceReader;

public class PointImageResourceReaderImpl implements PointImageResourceReader
{

  @Override
  public BufferedImage getBufferedImage(Point point)
  {
    InputStream is = null;
    if (point instanceof Planet)
    {
      is = getClass().getResourceAsStream("Planet." + point.toString(Locale.ENGLISH) + ".gif");
    }
    else if (point instanceof LunarNode)
    {
      LunarNode node = (LunarNode) point;
      if (node.getNorthSouth() == NorthSouth.NORTH)
        is = getClass().getResourceAsStream("Node.North.gif");
      else
        is = getClass().getResourceAsStream("Node.South.gif");
    }
    else if (point instanceof Asteroid)
    {
      is = getClass().getResourceAsStream("Asteroid." + point.toString(Locale.ENGLISH) + ".gif");
    }
    try
    {
      BufferedImage img = ImageIO.read(is);
      return img;
    }
    catch (IOException e)
    {
      e.printStackTrace();
    }
    return null;
  }

}
