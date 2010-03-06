/**
 * @author smallufo 
 * Created on 2007/12/31 at 上午 3:33:43
 */ 
package destiny.astrology.classical.rules.debilities;

import java.util.Locale;

import destiny.astrology.HoroscopeContext;
import destiny.astrology.Planet;
import destiny.astrology.RelativeTransitIF;
import destiny.astrology.beans.BesiegedBean;
import destiny.core.calendar.Time;

/** 
 * Besieged between Mars and Saturn. 
 * 被火土夾制，只有日月水金，這四星有可能發生
 * 前一個角度與火土之一形成 0/90/180 , 後一個角度又與火土另一顆形成 0/90/180
 * 中間不能與其他行星形成角度
 */
public final class Besieged_Mars_Saturn extends Rule
{
  /** 計算兩星交角的介面 */
  private RelativeTransitIF relativeTransitImpl;
  
  /** 計算兩星夾角的工具箱 */
  BesiegedBean besiegedBean;
  
  public Besieged_Mars_Saturn(RelativeTransitIF relativeTransitImpl)
  {
    super("Besieged_Mars_Saturn");
    this.relativeTransitImpl = relativeTransitImpl;
  }

  @Override
  public boolean isApplicable(Planet planet, HoroscopeContext horoscopeContext)
  {
    if (planet == Planet.SUN || planet == Planet.MOON || planet == Planet.MERCURY || planet == Planet.VENUS)
    {
      besiegedBean = new BesiegedBean(relativeTransitImpl);
      //火土夾制，只考量「硬」角度 , 所以最後一個參數設成 true
      if (besiegedBean.isBesieged(planet, Planet.MARS , Planet.SATURN , Time.getGMTfromLMT(horoscopeContext.getLmt() , horoscopeContext.getLocation())  , true , true))
      {
        addComment(Locale.TAIWAN , planet + " 被 " + Planet.MARS + " 以及 " + Planet.SATURN +" 夾制 (Besieged)");
        return true;
      }
    }
    return false;
  }

  public void setRelativeTransitImpl(RelativeTransitIF relativeTransitImpl)
  {
    this.relativeTransitImpl = relativeTransitImpl;
  }

}
