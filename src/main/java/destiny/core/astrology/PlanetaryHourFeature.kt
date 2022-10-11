/**
 * Created by smallufo on 2022-10-11.
 */
package destiny.core.astrology

import destiny.core.calendar.GmtJulDay
import destiny.core.calendar.ILocation
import destiny.tools.AbstractCachedFeature
import javax.inject.Named


enum class PlanetaryHourType {
  ASTRO,
  CLOCK
}

data class PlanetaryHourConfig(val type: PlanetaryHourType = PlanetaryHourType.ASTRO,
                               val transConfig: TransConfig = TransConfig())

@Named
class PlanetaryHourFeature(private val astroHourImplMap : Map<PlanetaryHourType, IPlanetaryHour>) : AbstractCachedFeature<PlanetaryHourConfig, PlanetaryHour?>() {

  override val defaultConfig: PlanetaryHourConfig = PlanetaryHourConfig()

  override fun calculate(gmtJulDay: GmtJulDay, loc: ILocation, config: PlanetaryHourConfig): PlanetaryHour? {
    return astroHourImplMap[config.type]!!.getPlanetaryHour(gmtJulDay, loc, config.transConfig)
  }

}
