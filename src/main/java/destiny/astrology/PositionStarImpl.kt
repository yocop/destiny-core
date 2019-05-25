/**
 * Created by smallufo on 2017-07-02.
 */
package destiny.astrology

import destiny.core.calendar.ILocation
import mu.KotlinLogging

abstract class PositionStarImpl(val starPositionImpl: IStarPosition<*>, star: Star) : AbstractPositionImpl<Star>(star) {

  override fun getPosition(gmtJulDay: Double,
                           loc: ILocation,
                           centric: Centric,
                           coordinate: Coordinate,
                           temperature: Double,
                           pressure: Double): IPos {
    return starPositionImpl.getPosition(point, gmtJulDay, loc.lat, loc.lng, loc.altitudeMeter?:0.0, centric, coordinate , temperature, pressure)
  }

}
