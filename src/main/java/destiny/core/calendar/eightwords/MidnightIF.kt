/*
 * @author smallufo
 * @date 2004/12/6
 * @time 上午 11:23:37
 */
package destiny.core.calendar.eightwords

import destiny.core.Descriptive
import destiny.core.calendar.Location
import destiny.core.calendar.TimeTools

import java.time.chrono.ChronoLocalDateTime
import java.util.function.Function

/** 定義「子正」的介面，是要以當地手錶 0時 為子正，亦或是太陽過當地天底 ... 或是其他實作  */
interface MidnightIF : Descriptive {

  /** 取得下一個「子正」的 GMT 時刻  */
  fun getNextMidnight(gmtJulDay: Double, loc: Location): Double

  /** 取得下一個「子正」的 LMT 時刻  */
  open fun getNextMidnight(lmt: ChronoLocalDateTime<*>, loc: Location, revJulDayFunc: Function<Double, ChronoLocalDateTime<*>>): ChronoLocalDateTime<*> {
    val gmtJulDay = TimeTools.getGmtJulDay(lmt, loc)
    val gmtResultJulDay = getNextMidnight(gmtJulDay, loc)
    val gmtResult = revJulDayFunc.apply(gmtResultJulDay)
    return TimeTools.getLmtFromGmt(gmtResult, loc)
  }
}
