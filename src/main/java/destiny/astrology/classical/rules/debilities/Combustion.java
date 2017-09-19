/**
 * @author smallufo
 * Created on 2007/12/31 at 上午 3:22:25
 */
package destiny.astrology.classical.rules.debilities;

import destiny.astrology.HoroscopeContextIF;
import destiny.astrology.Planet;
import org.jetbrains.annotations.NotNull;
import org.jooq.lambda.tuple.Tuple;
import org.jooq.lambda.tuple.Tuple2;

import java.util.Optional;

/** Combust the Sun (between 17' and 8.5 from Sol). */
public final class Combustion extends Rule {

  public Combustion() {
  }

  @Override
  protected Optional<Tuple2<String, Object[]>> getResult(Planet planet, @NotNull HoroscopeContextIF horoscopeContext) {
    if (planet != Planet.SUN) {
      if (horoscopeContext.getHoroscope().getAngle(planet, Planet.SUN) > 17.0 / 60 && horoscopeContext.getHoroscope().getAngle(planet, Planet.SUN) <= 8.5) {
        //addComment(Locale.TAIWAN , planet + " 被太陽焦傷 (Combustion)");
        return Optional.of(Tuple.tuple("comment", new Object[]{planet}));
      }
    }
    return Optional.empty();
  }

}
