/**
 * Created by smallufo on 2017-10-24.
 */
package destiny.core

import kotlin.test.Test
import java.util.*
import kotlin.test.assertEquals

class IntAgeNoteWestYearImplTest {

  @Test
  fun getTitle() {
    val impl = IntAgeNoteWestYearImpl()
    assertEquals("西元", impl.toString(Locale.TAIWAN))
    assertEquals("西元", impl.toString(Locale.CHINA))
    assertEquals("Year", impl.toString(Locale.ENGLISH))
  }

}
