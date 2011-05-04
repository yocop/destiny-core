/**
 * @author smallufo 
 * Created on 2007/12/29 at 下午 11:42:03
 */ 
package destiny.astrology.classical.rules.accidentalDignities;

import destiny.astrology.Aspect;
import destiny.astrology.Horoscope;
import destiny.astrology.HoroscopeContext;
import destiny.astrology.Planet;
import destiny.utils.Tuple;

/** Partile conjunction with Jupiter or Venus. 
 * 和金星或木星合相，交角 1 度內 */
public final class Partile_Conj_Jupiter_Venus extends Rule
{
  public Partile_Conj_Jupiter_Venus()
  {
  }

  @Override
  protected Tuple<String, Object[]> getResult(Planet planet, HoroscopeContext horoscopeContext)
  {
    double planetDegree = horoscopeContext.getPosition(planet).getLongitude();
    double jupiterDeg = horoscopeContext.getPosition(Planet.JUPITER).getLongitude();
    double venusDeg   = horoscopeContext.getPosition(Planet.VENUS).getLongitude();
    
    if (planet != Planet.JUPITER && Horoscope.getAngle(planetDegree , jupiterDeg) <= 1) 
    {
      //addComment(Locale.TAIWAN , planet + " 與 " + Planet.JUPITER + " 形成 " + Aspect.CONJUNCTION);
      return new Tuple<String , Object[]>("comment" , new Object[] {planet , Planet.JUPITER , Aspect.CONJUNCTION});
    }
    else if (planet != Planet.VENUS && Horoscope.getAngle(planetDegree , venusDeg) <= 1)
    {
      //addComment(Locale.TAIWAN , planet + " 與 " + Planet.VENUS + " 形成 " + Aspect.CONJUNCTION);
      return new Tuple<String , Object[]>("comment" , new Object[] {planet , Planet.VENUS , Aspect.CONJUNCTION});
    }
    return null;
  }

}
