/**
 * @author smallufo
 * Created on 2008/1/3 at 上午 8:55:07
 */
package destiny.astrology.classical.rules.accidentalDignities

import destiny.astrology.Horoscope
import destiny.astrology.IBesieged
import destiny.astrology.Planet
import destiny.astrology.Planet.*
import destiny.core.calendar.TimeTools
import org.jooq.lambda.tuple.Tuple
import org.jooq.lambda.tuple.Tuple2
import java.util.*

/**
 * 夾輔 : 被金星木星包夾 , 是很幸運的情形<br></br>
 * 角度考量 0/60/90/120/180 <br></br>
 * 中間不能與其他行星形成角度
 */
class Besieged_Jupiter_Venus(

  /** 計算兩星夾角的實作  */
  private val besiegedImpl: IBesieged) : Rule() {

  override fun getResult(planet: Planet, h: Horoscope): Optional<Tuple2<String, Array<Any>>> {
    if (planet === SUN
      || planet === MOON
      || planet === MERCURY
      || planet === MARS
      || planet === SATURN) {

      val gmt = TimeTools.getGmtFromLmt(h.lmt, h.location)
      if (besiegedImpl.isBesieged(planet, VENUS, JUPITER, gmt, true, false)) {
        //planet + " 被 " + Planet.VENUS + " 以及 " + Planet.JUPITER + " 夾輔 (善意 Besieged)"
        return Optional.of(Tuple.tuple("comment", arrayOf<Any>(planet, VENUS, JUPITER)))
      }
    }
    return Optional.empty()
  }

  override fun getResult2(planet: Planet, h: Horoscope): Pair<String, Array<Any>>? {
    return planet.takeIf { arrayOf(SUN , MOON , MERCURY , MARS , SATURN).contains(it) }?.takeIf {
      val gmt = TimeTools.getGmtFromLmt(h.lmt, h.location)
      besiegedImpl.isBesieged(it , VENUS , JUPITER , gmt , true , false)
    }?.let { "comment" to arrayOf<Any>(planet , VENUS , JUPITER) }
  }
}
