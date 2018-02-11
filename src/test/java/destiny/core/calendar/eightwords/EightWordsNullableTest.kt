package destiny.core.calendar.eightwords

import destiny.core.chinese.Branch.*
import destiny.core.chinese.Stem.*
import destiny.core.chinese.StemBranchOptional
import destiny.tools.Hashids
import org.junit.Assert.assertEquals
import org.slf4j.LoggerFactory
import kotlin.test.Test

class EightWordsNullableTest {

  private fun EightWords.getIntList() : List<Int> {
    return listOf(
      year.stem.indexFromOne,
      year.branch.indexFromOne,
      month.stem.indexFromOne,
      month.branch.indexFromOne,
      day.stem.indexFromOne,
      day.branch.indexFromOne,
      hour.stem.indexFromOne,
      hour.branch.indexFromOne
                 )
  }

  private fun EightWordsNullable.getIntList() : List<Int> {
    return listOf(
      year.stem?.indexFromOne ?: 0,
      year.branch?.indexFromOne ?: 0,
      month.stem?.indexFromOne ?: 0,
      month.branch?.indexFromOne ?: 0,
      day.stem?.indexFromOne ?: 0,
      day.branch?.indexFromOne ?: 0,
      hour.stem?.indexFromOne ?: 0,
      hour.branch?.indexFromOne ?: 0)
  }

  private val logger = LoggerFactory.getLogger(javaClass)

  @Test
  fun testGetList() {
    var ewn1 = EightWordsNullable(StemBranchOptional["甲子"], StemBranchOptional["乙丑"], StemBranchOptional["丙寅"],
                                  StemBranchOptional["丁卯"])
    var ewn2 = EightWordsNullable.getFromIntList(ewn1.getIntList())
    assertEquals(ewn1, ewn2)

    ewn1 = EightWordsNullable(
      StemBranchOptional[甲, null],
      StemBranchOptional[null, 丑],
      StemBranchOptional[丙, null],
      StemBranchOptional[null, 卯])
    ewn2 = EightWordsNullable.getFromIntList(ewn1.getIntList())
    assertEquals(ewn1, ewn2)


    val empty = EightWordsNullable(StemBranchOptional.empty(), StemBranchOptional.empty(), StemBranchOptional.empty(),
                                   StemBranchOptional.empty())
    val empty2 = EightWordsNullable.getFromIntList(empty.getIntList())
    assertEquals(empty, empty2)

    val hashids = Hashids("ewn")
    val encoded = hashids.encode(*empty.getIntList().stream().mapToLong { i -> i.toLong() }.toArray())
    logger.info("encoded = {}", encoded)

    val ew1 = EightWords(甲, 子, 乙, 丑, 丙, 寅, 丁, 卯)
    logger.info("ints = {}", ew1.getIntList())
    val ew2 = EightWordsNullable.getFromIntList(ew1.getIntList())
    logger.info("ew2 = {}", ew2)

  }

  @Test
  fun testEquals() {
    var ew1 = EightWordsNullable()
    var ew2 = EightWordsNullable()
    assertEquals(ew1, ew2) //兩個都是 null 八字,應該 equals

    ew1 = EightWordsNullable(StemBranchOptional["甲子"], StemBranchOptional["乙丑"], StemBranchOptional["丙寅"],
                             StemBranchOptional["丁卯"])
    ew2 = EightWordsNullable(StemBranchOptional["甲子"], StemBranchOptional["乙丑"], StemBranchOptional["丙寅"],
                             StemBranchOptional["丁卯"])
    assertEquals(ew1, ew2)

    ew1 = EightWordsNullable(StemBranchOptional["甲子"], StemBranchOptional["乙丑"], StemBranchOptional["丙寅"],
                             StemBranchOptional["丁卯"])
    ew2 = EightWordsNullable(
      StemBranchOptional[甲, 子],
      StemBranchOptional[乙, 丑],
      StemBranchOptional[丙, 寅],
      StemBranchOptional[丁, 卯])
    assertEquals(ew1, ew2)

    ew1 = EightWordsNullable(StemBranchOptional["甲子"], StemBranchOptional.empty(), StemBranchOptional["丙寅"],
                             StemBranchOptional.empty())
    ew2 = EightWordsNullable(
      StemBranchOptional[甲, 子],
      StemBranchOptional.empty(),
      StemBranchOptional[丙, 寅],
      StemBranchOptional.empty())
    assertEquals(ew1, ew2)


    ew1 = EightWordsNullable(StemBranchOptional["甲子"],
                             StemBranchOptional.empty(),
                             StemBranchOptional[丙, null],
                             StemBranchOptional[null, 卯]
                            )
    ew2 = EightWordsNullable(
      StemBranchOptional[甲, 子],
      StemBranchOptional.empty(),
      StemBranchOptional[丙, null],
      StemBranchOptional[null, 卯])
    assertEquals(ew1, ew2)

  }

}