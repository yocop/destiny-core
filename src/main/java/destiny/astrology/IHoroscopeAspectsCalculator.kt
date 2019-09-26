/**
 * @author smallufo
 * Created on 2008/6/19 at 上午 1:48:49
 */
package destiny.astrology

import destiny.core.Descriptive
import mu.KotlinLogging

/**
 * 計算一張命盤 ( Horoscope ) 中，的交角列表
 * 分為古典 [HoroscopeAspectsCalculatorClassical]
 * 以及西洋 [HoroscopeAspectsCalculatorModern] 實作
 *
 *  */
interface IHoroscopeAspectsCalculator : Descriptive {

  /**
   * 取得與 [Point] 形成交角的星體，以及其交角是哪種，以及交角緊密度 (0~1)
   * 如果沒形成任何交角，傳回 empty
   * */
  fun getPointAspectAndScore(point: Point,
                             positionMap: Map<Point, IPos>,
                             points: Collection<Point> = positionMap.keys,
                             aspects: Collection<Aspect> = Aspect.getAngles(Aspect.Importance.HIGH)
  ): Map<Point, Pair<Aspect, Double>>

  /**
   * 取得與 [Point] 形成交角的星體，以及其交角是哪種 ，如果沒形成任何交角，傳回 empty
   * */
  fun getPointAspect(point: Point,
                     positionMap: Map<Point, IPos>,
                     points: Collection<Point> = positionMap.keys,
                     aspects: Collection<Aspect> = Aspect.getAngles(Aspect.Importance.HIGH)
  ): Map<Point, Aspect> {
    return getPointAspectAndScore(point, positionMap, points, aspects)
      .map { (point, pair) ->
        point to pair.first
      }.toMap()
  }


  fun getPointAspect(point: Point, horoscope: IHoroscopeModel,
                     points: Collection<Point>,
                     aspects: Collection<Aspect> = Aspect.getAngles(Aspect.Importance.HIGH)): Map<Point, Aspect> {
    return getPointAspect(point, horoscope.positionMap, points, aspects)
  }


  /**
   * @param positionMap : 計算 此 map 星體之間所形成的交角
   * @param points : 欲計算交角的 points , 因為 positionMap 可能包含許多小星體、沒必要計算的星體
   *
   * 如果沒有形成任何交角（不太可能 , 除非 points 很少 ），則傳回 size = 0 之 Set
   */
  fun getAspectDataSet(positionMap: Map<Point, IPos>,
                       points: Collection<Point> = positionMap.keys,
                       aspects: Collection<Aspect> = Aspect.getAngles(Aspect.Importance.HIGH)): Set<HoroscopeAspectData> {

    return points.asSequence().map { point ->
      val map: Map<Point, Pair<Aspect, Double>> = getPointAspectAndScore(point, positionMap, points, aspects)
      logger.trace("與 {} 形成所有交角的 pointAspect Map = {}", point, map)

      map.filter { (_, aspectAndScore) -> aspects.contains(aspectAndScore.first) }
        .map { (key, aspectAndScore) ->
          HoroscopeAspectData(point, key, aspectAndScore.first,
            IHoroscopeModel.getAspectError(positionMap, point, key, aspectAndScore.first) ?: 0.0, aspectAndScore.second)
        }.toSet()

    }.flatten()
      .toSet()
  }

  companion object {
    private val logger = KotlinLogging.logger { }
  }

}
