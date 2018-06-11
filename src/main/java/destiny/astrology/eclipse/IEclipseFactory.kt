/**
 * Created by smallufo on 2018-05-05.
 */
package destiny.astrology.eclipse

import destiny.core.calendar.ILocation
import destiny.core.calendar.TimeTools
import java.time.chrono.ChronoLocalDateTime


interface IEclipseFactory {

  // ================================== 日食 ==================================

  /** 從此時之後，全球各地的「一場」日食資料 (型態、開始、最大、結束...）  */
  fun getNextSolarEclipse(fromGmtJulDay: Double, forward: Boolean, types: Collection<ISolarEclipse.SolarType>):
    AbstractSolarEclipse

  /** 承上 , 不指定 日食類型 [ISolarEclipse.SolarType]]  */
  fun getNextSolarEclipse(fromGmtJulDay: Double, forward: Boolean): AbstractSolarEclipse {
    return getNextSolarEclipse(fromGmtJulDay, forward, listOf(*ISolarEclipse.SolarType.values()))
  }

  /** 全球，某時間範圍內的日食記錄  */
  fun getRangeSolarEclipses(fromGmt: Double,
                            toGmt: Double,
                            types: Collection<ISolarEclipse.SolarType> = listOf(
                              *ISolarEclipse.SolarType.values())): List<AbstractSolarEclipse> {
    require( fromGmt < toGmt) { "fromGmt : $fromGmt must less than toGmt : $toGmt" }

    return generateSequence (getNextSolarEclipse(fromGmt , true , types)) {
      getNextSolarEclipse(it.end , true , types)
    }.takeWhile { it.end < toGmt }
      .toList()
  }

  /** 承上 , [ChronoLocalDateTime] 版本 , 搜尋 全部 種類的日食 */
  fun getRangeSolarEclipses(fromGmt: ChronoLocalDateTime<*>,
                            toGmt: ChronoLocalDateTime<*>): List<AbstractSolarEclipse> {
    return getRangeSolarEclipses(TimeTools.getGmtJulDay(fromGmt), TimeTools.getGmtJulDay(toGmt))
  }

  /** 承上 , [ChronoLocalDateTime] 版本 , 搜尋 單一種類的日食 */
  fun getRangeSolarEclipses(fromGmt: ChronoLocalDateTime<*>,
                            toGmt: ChronoLocalDateTime<*>,
                            type: ISolarEclipse.SolarType): List<AbstractSolarEclipse> {
    return getRangeSolarEclipses(TimeTools.getGmtJulDay(fromGmt), TimeTools.getGmtJulDay(toGmt), listOf(type))
  }

  // ================================== 月食 ==================================

  /** 從此時之後，全球各地的「一場」月食資料 (型態、開始、最大、結束...）  */
  fun getNextLunarEclipse(fromGmtJulDay: Double, forward: Boolean) : AbstractLunarEclipse

  /** 全球，某時間範圍內的月食記錄 */
  fun getRangeLunarEclipses(fromGmt: Double,
                            toGmt: Double,
                            types: Collection<ILunarEclipse.LunarType> = listOf(
                              *ILunarEclipse.LunarType.values())): List<AbstractLunarEclipse> {
    require( fromGmt < toGmt) { "fromGmt : $fromGmt must less than toGmt : $toGmt" }

    return generateSequence( getNextLunarEclipse(fromGmt , true)) {
      getNextLunarEclipse(it.end , true)
    }.takeWhile { it.end < toGmt }
      .toList()
  }

  /** 承上 , [ChronoLocalDateTime] 版本 , 搜尋 全部 種類的月食  */
  fun getRangeLunarEclipses(fromGmt: ChronoLocalDateTime<*>,
                            toGmt: ChronoLocalDateTime<*>): List<AbstractLunarEclipse> {
    return getRangeLunarEclipses(TimeTools.getGmtJulDay(fromGmt), TimeTools.getGmtJulDay(toGmt))
  }

  // ================================== 日食觀測 ==================================

  /** 從此之後 , 此地點下次發生日食的資訊為何 (tuple.v1) , 以及， 日食最大化的時間，該地的觀測資訊為何 (tuple.v2)  */
  fun getNextSolarEclipseAtLoc(fromGmtJulDay: Double,
                               lng: Double,
                               lat: Double,
                               alt: Double? = 0.0,
                               forward: Boolean): Pair<EclipseSpan, ISolarEclipseObservation>

  /** 承上 [ILocation] 版本 */
  fun getNextSolarEclipseAtLoc(fromGmtJulDay: Double,
                               loc: ILocation,
                               forward: Boolean): Pair<EclipseSpan, ISolarEclipseObservation> {
    return getNextSolarEclipseAtLoc(fromGmtJulDay, loc.lng, loc.lat, loc.altitudeMeter, forward)
  }

  /**
   * 若當下 gmtJulDay 有日食，傳出此地點觀測此日食的相關資料
   * */
  fun getSolarEclipseObservationAtLoc(gmtJulDay: Double,
                                      lng: Double,
                                      lat: Double,
                                      alt: Double): ISolarEclipseObservation?

  /** 承上 , [ChronoLocalDateTime] 版本 */
  fun getSolarEclipseObservationAtLoc(gmt: ChronoLocalDateTime<*>,
                                      lng: Double,
                                      lat: Double,
                                      alt: Double): ISolarEclipseObservation? {
    return getSolarEclipseObservationAtLoc(TimeTools.getGmtJulDay(gmt), lng, lat, alt)
  }


  /** 此時此刻，哪裡有發生日食，其「中線」的地點為何 , 以及其相關日食觀測結果 . 此 method 專門計算「中線在哪裡 , 其太陽觀測為何」 (t.v1) , 是否出現中線了 (t.v2)  */
  fun getEclipseCenterInfo(gmtJulDay: Double) : Pair<ISolarEclipseObservation , Boolean>?

  // ================================== 月食觀測 ==================================
  /** 若當下 gmtJulDay 有月食，傳出此地點觀測此月食的相關資料  */
  fun getLunarEclipseObservationAtLoc(gmtJulDay: Double,
                                      lng: Double,
                                      lat: Double,
                                      alt: Double): AbstractLunarEclipseObservation?

  /** 從此之後 , 此地點下次發生月食的資訊為何 (tuple.v1) , 以及， 該地能否見到 半影、偏食、全蝕、的起訖 (tuple.v2)  */
  fun getNextLunarEclipseAtLoc(fromGmtJulDay: Double,
                               lng: Double,
                               lat: Double,
                               alt: Double,
                               forward: Boolean): AbstractLunarEclipseObservation
}