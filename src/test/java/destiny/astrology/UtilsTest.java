/**
 * @author smallufo 
 * Created on 2008/6/1 at 上午 6:53:18
 */ 
package destiny.astrology;

import org.junit.Test;

import static org.junit.Assert.assertTrue;

public class UtilsTest
{
  @Test
  public void testGetNormalizeDegree()
  {
    //測試大於零的度數
    assertTrue(Utils.INSTANCE.getNormalizeDegree(0)==0);
    assertTrue(Utils.INSTANCE.getNormalizeDegree(359)==359);
    assertTrue(Utils.INSTANCE.getNormalizeDegree(360)==0);
    assertTrue(Utils.INSTANCE.getNormalizeDegree(361)==1);
    assertTrue(Utils.INSTANCE.getNormalizeDegree(720)==0);
    assertTrue(Utils.INSTANCE.getNormalizeDegree(721)==1);
    
    //測試小於零的度數
    assertTrue(Utils.INSTANCE.getNormalizeDegree(-1)==359);
    assertTrue(Utils.INSTANCE.getNormalizeDegree(-359)==1);
    assertTrue(Utils.INSTANCE.getNormalizeDegree(-360)==0);
    assertTrue(Utils.INSTANCE.getNormalizeDegree(-361)==359);
    assertTrue(Utils.INSTANCE.getNormalizeDegree(-719)==1);
    assertTrue(Utils.INSTANCE.getNormalizeDegree(-720)==0);
    assertTrue(Utils.INSTANCE.getNormalizeDegree(-721)==359);
  }
}
