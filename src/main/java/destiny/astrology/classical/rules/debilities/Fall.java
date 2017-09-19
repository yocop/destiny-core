/**
 * @author smallufo
 * Created on 2007/12/30 at 上午 4:51:31
 */
package destiny.astrology.classical.rules.debilities;

import destiny.astrology.HoroscopeContextIF;
import destiny.astrology.Planet;
import destiny.astrology.ZodiacSign;
import destiny.astrology.classical.Dignity;
import org.jetbrains.annotations.NotNull;
import org.jooq.lambda.tuple.Tuple;
import org.jooq.lambda.tuple.Tuple2;

import java.util.Optional;

/** In Fall. */
public final class Fall extends EssentialRule {

  public Fall() {
  }

  @Override
  protected Optional<Tuple2<String, Object[]>> getResult(Planet planet, @NotNull HoroscopeContextIF horoscopeContext) {
    //取得此 Planet 在什麼星座
    ZodiacSign sign = horoscopeContext.getZodiacSign(planet);

    if (planet == essentialImpl.getPoint(sign, Dignity.FALL)) {
      //addComment(Locale.TAIWAN , planet + " 位於 " + sign + " , 為其 Fall");
      return Optional.of(Tuple.tuple("comment", new Object[]{planet, sign}));
    }
    return Optional.empty();
  }

}
