/**
 * @author smallufo 
 * Created on 2007/12/29 at 上午 4:39:42
 */ 
package destiny.astrology.classical.rules.accidentalDignities;

import destiny.astrology.HoroscopeContext;
import destiny.astrology.Planet;
import destiny.utils.Tuple;

/** In the 10th or 1st house. */
public final class House_1_10 extends Rule
{
  public House_1_10()
  {
  }

  @Override
  protected Tuple<String, Object[]> getResult(Planet planet, HoroscopeContext horoscopeContext)
  {
    int planetHouse = horoscopeContext.getHouse(planet); 
    if ( planetHouse == 1 || planetHouse == 10)
    {
      return new Tuple<String , Object[]>("comment" , new Object[] {planet , planetHouse});
    }
    return null;
  }


}
