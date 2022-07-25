package destiny.core.astrology.prediction

import destiny.core.calendar.GmtJulDay
import destiny.core.calendar.JulDayResolver
import destiny.core.calendar.JulDayResolver1582CutoverImpl
import destiny.core.calendar.TimeTools
import java.time.chrono.ChronoLocalDateTime

/**
 * 繼承自 [Mappable] , 只是此推運法是「線性收斂」的 : 只要時間不同，所收斂到的時間，也必定不相同
 * 可以反推(從近推到遠)：從推運日期逆推到真實日期 。
 * [ProgressionSecondary] , [ProgressionTertiary] , [ProgressionMinor] 皆屬此類 , Transits 更不用提，收斂係數等於 1:1
 * 但 SolarReturn , LunarReturn 不屬於此類
 *
 */
internal interface ILinear : Mappable {
  /**
   * 取得反推（發散 divergent）的時間
   * @param natalGmtJulDay 起始時間，通常是出生時間
   * @param nowTime   某時刻
   * @return nowTime 相對於 natalTime 輻射放大出去， 是此人的何時
   */

  fun getDivergentTime(natalGmtJulDay: GmtJulDay, nowTime: GmtJulDay): GmtJulDay


  fun getDivergentTime(natalTime: ChronoLocalDateTime<*>, nowTime: ChronoLocalDateTime<*>,
                       julDayResolver: JulDayResolver = JulDayResolver1582CutoverImpl()): ChronoLocalDateTime<*> {
    val natalGmtJulDay = TimeTools.getGmtJulDay(natalTime)
    val nowGmtJulDay = TimeTools.getGmtJulDay(nowTime)
    return julDayResolver.getLocalDateTime(getDivergentTime(natalGmtJulDay, nowGmtJulDay))
  }

}
