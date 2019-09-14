/**
 * @author smallufo
 * Created on 2007/8/29 at 下午 3:01:26
 */
package destiny.astrology

import java.util.*
import kotlin.test.Test
import kotlin.test.assertEquals


class QualityTest {

  @Test
  fun testToString() {
    assertEquals("基本", Quality.CARDINAL.toString(Locale.TAIWAN))
    assertEquals("固定", Quality.FIXED.toString(Locale.TAIWAN))
    assertEquals("變動", Quality.MUTABLE.toString(Locale.TAIWAN))
  }

  @Test
  fun testToStringLocale() {
    assertEquals("基本", Quality.CARDINAL.toString(Locale.TAIWAN))
    assertEquals("固定", Quality.FIXED.toString(Locale.TRADITIONAL_CHINESE))
    assertEquals("變動", Quality.MUTABLE.toString(Locale.TAIWAN))

    assertEquals("Cardinal", Quality.CARDINAL.toString(Locale.ENGLISH))
    assertEquals("Fixed", Quality.FIXED.toString(Locale.US))
    assertEquals("Mutable", Quality.MUTABLE.toString(Locale.UK))
  }

}
