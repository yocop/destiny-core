/**
 * @author smallufo 
 * Created on 2007/12/29 at 上午 5:22:44
 */ 
package destiny.astrology.classical.rules.accidentalDignities;

import java.util.Locale;

import destiny.astrology.Horoscope;
import destiny.astrology.HoroscopeContext;
import destiny.astrology.Planet;

/** Moon increasing in light (月增光/上弦月) , or occidental of the Sun. */
public final class Moon_Increase_Light extends Rule
{
  public Moon_Increase_Light()
  {
    super("Moon_Increase_Light");
  }

  @Override
  public boolean isApplicable(Planet planet, HoroscopeContext horoscopeContext)
  {
    double planetDegree = horoscopeContext.getPosition(planet).getLongitude();
    double sunDegree    = horoscopeContext.getPosition(Planet.SUN).getLongitude();
    
    if (planet == Planet.MOON)
    {
      if ( Horoscope.isOccidental(planetDegree , sunDegree))
      {
        addComment(Locale.TAIWAN , planet + " 在太陽西邊（月增光/上弦月）");
        return true;
      }
    }
    return false;
  }

}
