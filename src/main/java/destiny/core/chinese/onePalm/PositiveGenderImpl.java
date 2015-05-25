/**
 * Created by smallufo on 2015-05-18.
 */
package destiny.core.chinese.onePalm;

import destiny.core.Gender;
import destiny.core.chinese.Branch;

import java.io.Serializable;
import java.util.Locale;

public class PositiveGenderImpl implements PositiveIF , Serializable {

  @Override
  public boolean isPositive(Gender gender, Branch yearBranch) {
    return (gender == Gender.男);
  }

  @Override
  public String getTitle(Locale locale) {
    return "男順女逆";
  }

  @Override
  public String getDescription(Locale locale) {
    return "固定男順女逆";
  }
}
