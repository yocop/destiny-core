/**
 * @author smallufo 
 * Created on 2007/12/30 at 上午 2:45:26
 */ 
package destiny.astrology.classical.rules.accidentalDignities;

import destiny.astrology.classical.rules.AbstractRule;

public abstract class Rule extends AbstractRule implements Applicable
{
  private final static String resource = "destiny.astrology.classical.rules.accidentalDignities.AccidentalDignities";
  
  public Rule(String nameKey)
  {
    super(nameKey, resource);
  }

}
