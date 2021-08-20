/**
 * Created by smallufo on 2021-08-16.
 */
package destiny.core.astrology

import destiny.core.calendar.GmtJulDay
import destiny.core.calendar.ILocation
import destiny.tools.Builder
import destiny.tools.DestinyMarker
import destiny.tools.Feature
import kotlinx.serialization.Serializable

@Serializable
data class AzimuthConfig(@Serializable(with = PointSerializer::class)
                         val star: Star = Planet.SUN,
                         val coordinate: Coordinate = Coordinate.ECLIPTIC,
                         val geoAlt: Double = 0.0,
                         val temperature: Double = 0.0,
                         val pressure: Double = 1013.25)

@DestinyMarker
class AzimuthConfigBuilder : Builder<AzimuthConfig> {

  var star: Star = Planet.SUN
  var coordinate: Coordinate = Coordinate.ECLIPTIC
  var geoAlt: Double = 0.0
  var temperature: Double = 0.0
  var pressure: Double = 1013.25

  override fun build(): AzimuthConfig {
    return AzimuthConfig(star, coordinate, geoAlt, temperature, pressure)
  }

  companion object {
    fun azimuth(block :AzimuthConfigBuilder.() -> Unit = {} ) : AzimuthConfig {
      return AzimuthConfigBuilder().apply(block).build()
    }
  }
}


class AzimuthFeature(val starPositionImpl: IStarPosition<IStarPos>,
                     val azimuthImpl: IAzimuthCalculator) : Feature<AzimuthConfig, StarPosWithAzimuth> {
  override val key: String = "azimuth"

  override val defaultConfig: AzimuthConfig = AzimuthConfig()

  override fun getModel(gmtJulDay: GmtJulDay, loc: ILocation, config: AzimuthConfig): StarPosWithAzimuth {
    val pos: IStarPos = starPositionImpl.getPosition(config.star, gmtJulDay, loc, Centric.GEO, config.coordinate, config.temperature, config.pressure)
    val azimuth = with(azimuthImpl) {
      pos.getAzimuth(config.coordinate, gmtJulDay, loc, config.temperature, config.pressure)
    }
    return StarPosWithAzimuth(pos, azimuth)
  }
}