/**
 * @author smallufo
 * Created on 2008/5/29 at 上午 3:15:13
 */
package destiny.core.astrology.prediction

import destiny.core.astrology.*
import destiny.core.astrology.classical.VoidCourseConfig
import destiny.core.calendar.GmtJulDay
import destiny.core.calendar.ILocation
import destiny.core.calendar.JulDayResolver
import destiny.core.calendar.TimeTools
import destiny.tools.Feature
import mu.KotlinLogging
import java.io.Serializable
import java.time.chrono.ChronoLocalDateTime
import kotlin.math.absoluteValue

interface IReturnContext : Conversable, IDiscrete {

  val planet: Planet

  /** 交角 , 通常是 0 , 代表回歸到原始度數  */
  val orb: Double

  /** 是否消除歲差， false = 不計算歲差  */
  val precession: Boolean

  /** 對外主要的 method , 取得 return 盤  */
  fun getReturnHoroscope(natalGmtJulDay: GmtJulDay, natalLoc: ILocation, nowGmtJulDay: GmtJulDay, nowLoc: ILocation): IHoroscopeModel

  fun getReturnHoroscope(natalLmt: ChronoLocalDateTime<*>, natalLoc: ILocation, nowLmt: ChronoLocalDateTime<*>, nowLoc: ILocation): IHoroscopeModel {
    val natalGmtJulDay = TimeTools.getGmtJulDay(natalLmt, natalLoc)
    val nowGmtJulDay = TimeTools.getGmtJulDay(nowLmt, nowLoc)
    return getReturnHoroscope(natalGmtJulDay, natalLoc, nowGmtJulDay, nowLoc)
  }
}


/**
 * 返照法演算法 , 可以計算 Planet 的返照
 */
class ReturnContext(
  /** 返照法所採用的行星 , 太陽/太陰 , 或是其他  */
  override val planet: Planet = Planet.SUN,
  /** 是否逆推 , true 代表「是」，逆推！ */
  override val converse: Boolean = false,
  /** 交角 , 通常是 0 , 代表回歸到原始度數  */
  override val orb: Double = 0.0,
  /** 是否消除歲差，內定是不計算歲差  */
  override val precession: Boolean = false,

  /** 計算星體的介面  */
  private val starPositionWithAzimuthImpl: IStarPositionWithAzimuthCalculator,
  /** 計算星體到黃道幾度的時刻，的介面  */
  private var starTransitImpl: IStarTransit,
  private val horoscopeFeature: Feature<HoroscopeConfig, IHoroscopeModel>,
  private val julDayResolver: JulDayResolver
) : IReturnContext, Serializable {


  override fun getReturnHoroscope(natalGmtJulDay: GmtJulDay, natalLoc: ILocation, nowGmtJulDay: GmtJulDay, nowLoc: ILocation): IHoroscopeModel {
    val convergentGmtJulDay = getConvergentTime(natalGmtJulDay, nowGmtJulDay)
    val convergentGmt = julDayResolver.getLocalDateTime(convergentGmtJulDay)


    val convergentLmt = TimeTools.getLmtFromGmt(convergentGmt, nowLoc)


    val config = HoroscopeConfig(
      setOf(*Planet.values, *Axis.array, LunarNode.NORTH_MEAN, LunarNode.SOUTH_MEAN),
      HouseSystem.PLACIDUS,
      Coordinate.ECLIPTIC,
      Centric.GEO,
      0.0,
      1013.25,
      VoidCourseConfig.VoidCourseImpl.Medieval
    )

    return horoscopeFeature.getModel(convergentLmt, nowLoc, config)
  }


  override fun getConvergentTime(natalGmtJulDay: GmtJulDay, nowGmtJulDay: GmtJulDay): GmtJulDay {
    val coordinate = if (precession) Coordinate.SIDEREAL else Coordinate.ECLIPTIC
    //先計算出生盤中，該星體的黃道位置
    val natalPlanetDegree: ZodiacDegree = starPositionWithAzimuthImpl.getPosition(planet, natalGmtJulDay, Centric.GEO, coordinate).lngDeg

    //再從現在的時刻，往前(prior , before) 推 , 取得 planet 與 natal planet 呈現 orb 的時刻
    return if (!converse) {
      //順推
      starTransitImpl.getNextTransitGmt(planet, (natalPlanetDegree + orb), nowGmtJulDay, false, coordinate) //false 代表逆推，往before算
    } else {
      // converse == true , 逆推
      //從出生時間往前(before)推
      val d = (natalGmtJulDay - nowGmtJulDay).absoluteValue
      val beforeNatalGmtJulDay = natalGmtJulDay - d // TimeTools.getGmtJulDay(natalTime.minus(d))
      //要確認最後一個參數，到底是要用 true , 還是 false , 要找相關定義 , 我覺得這裡應該是順推
      starTransitImpl.getNextTransitGmt(planet, (natalPlanetDegree + orb), beforeNatalGmtJulDay, true, coordinate) //true 代表順推 , 往 after 算
    }
  }


  companion object {
    val logger = KotlinLogging.logger { }
  }

}
