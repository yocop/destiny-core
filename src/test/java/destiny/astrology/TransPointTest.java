/**
 * @author smallufo 
 * Created on 2007/6/22 at 上午 4:34:22
 */ 
package destiny.astrology;

import org.junit.Test;

import java.util.Locale;

import static org.junit.Assert.assertEquals;

public class TransPointTest
{
  @Test
  public void testTransPoint()
  {
    Locale locale;
    assertEquals("東昇" , TransPoint.RISING.toString());
    
    locale = Locale.getDefault();
    assertEquals("東昇" , TransPoint.RISING.toString());
    assertEquals("西落" , TransPoint.SETTING.toString());
    assertEquals("天頂" , TransPoint.MERIDIAN.toString());
    assertEquals("天底" , TransPoint.NADIR.toString());
    
    locale = Locale.ENGLISH;
    assertEquals("Rising" , TransPoint.RISING.toString(locale));
  }
}
