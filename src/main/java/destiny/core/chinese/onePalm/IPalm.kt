/**
 * Created by smallufo on 2015-05-17.
 */
package destiny.core.chinese.onePalm

import com.google.common.collect.HashBiMap
import destiny.core.Gender
import destiny.core.calendar.chinese.IChineseDateHourModel
import destiny.core.calendar.chinese.IFinalMonthNumber
import destiny.core.calendar.chinese.IFinalMonthNumber.MonthAlgo
import destiny.core.chinese.Branch
import org.slf4j.LoggerFactory


@Deprecated("")
interface IPalm {

  /**
   * [A] 本命盤，已經預先計算命宮
   * @param clockwiseHouse 宮位飛佈，順時針(true) or 逆時針(false)
   */
  fun getPalm(gender: Gender,
              yearBranch: Branch,
              leap: Boolean,
              monthNum: Int,
              dayNum: Int,
              hourBranch: Branch,
              positiveImpl: IPositive,
              rising: Branch,
              monthBranch: Branch,
              monthAlgo: MonthAlgo,
              clockwiseHouse: Boolean): Palm {
    val positive = if (positiveImpl.isPositive(gender, yearBranch)) 1 else -1

    val finalMonthNum = IFinalMonthNumber.getFinalMonthNumber(monthNum, leap, monthBranch, dayNum, monthAlgo)

    // 年上起月
    val month = yearBranch.next((finalMonthNum - 1) * positive)

    // 月上起日
    val day = month.next((dayNum - 1) * positive)

    // 日上起時
    val hour = day.next(hourBranch.index * positive)

    val houseMap = HashBiMap.create<Branch, Palm.House>(12)
    for (i in 0..11) {
      houseMap[if (clockwiseHouse) rising.next(i) else rising.prev(i)] = Palm.House.values()[i]
    }
    return Palm(gender, yearBranch, month, day, hour, houseMap)
  }

  /**
   * [B] 本命盤 , 沒有預先計算命宮
   * @param clockwiseHouse 宮位飛佈，順時針(true) or 逆時針(false)
   */
  fun getPalm(gender: Gender,
              yearBranch: Branch,
              leap: Boolean,
              monthNum: Int,
              dayNum: Int,
              hourBranch: Branch,
              positiveImpl: IPositive,
              monthBranch: Branch,
              monthAlgo: MonthAlgo,
              clockwiseHouse: Boolean): Palm {
    val positive = if (positiveImpl.isPositive(gender, yearBranch)) 1 else -1

    logger.trace("positive = {}", positive)

    val finalMonthNum = IFinalMonthNumber.getFinalMonthNumber(monthNum, leap, monthBranch, dayNum, monthAlgo)

    logger.trace("yearBranch = {}", yearBranch)

    // 年上起月
    val month = yearBranch.next((finalMonthNum - 1) * positive)

    // 月上起日
    val day = month.next((dayNum - 1) * positive)

    // 日上起時
    val hour = day.next(hourBranch.index * positive)

    // 命宮
    val steps = Branch.卯.getAheadOf(hourBranch)
    val rising = hour.next(steps * positive)
    val houseMap = HashBiMap.create<Branch, Palm.House>(12)
    for (i in 0..11) {
      houseMap[if (clockwiseHouse) rising.next(i) else rising.prev(i)] = Palm.House.values()[i]
    }
    return Palm(gender, yearBranch, month, day, hour, houseMap)
  }

  /** [C] 沒帶入節氣資料 , 內定把月份計算採用 [MonthAlgo.MONTH_LEAP_SPLIT15] 的演算法  */
  fun getPalm(gender: Gender,
              yearBranch: Branch,
              leap: Boolean,
              monthNum: Int,
              dayNum: Int,
              hourBranch: Branch,
              positiveImpl: IPositive,
              clockwiseHouse: Boolean): Palm {
    val positive = if (positiveImpl.isPositive(gender, yearBranch)) 1 else -1

    logger.trace("positive = {}", positive)

    var finalMonthNum = monthNum
    if (leap && dayNum > 15)
    // 若為閏月，15日以後算下個月
      finalMonthNum++

    logger.trace("yearBranch = {}", yearBranch)

    // 年上起月
    val month = yearBranch.next((finalMonthNum - 1) * positive)

    // 月上起日
    val day = month.next((dayNum - 1) * positive)

    // 日上起時
    val hour = day.next(hourBranch.index * positive)

    // 命宮
    val steps = Branch.卯.getAheadOf(hourBranch)
    val rising = hour.next(steps * positive)
    val houseMap = HashBiMap.create<Branch, Palm.House>(12)
    for (i in 0..11) {
      houseMap[if (clockwiseHouse) rising.next(i) else rising.prev(i)] = Palm.House.values()[i]
    }
    return Palm(gender, yearBranch, month, day, hour, houseMap)
  }

  /**
   * [D] 本命盤
   *
   * @param trueRising  是否已經預先計算好了真實的上升星座
   * @param monthBranch 「節氣」的月支
   */
  fun getPalm(gender: Gender,
              chineseDateHour: IChineseDateHourModel,
              positiveImpl: IPositive,
              trueRising: Branch?,
              monthBranch: Branch,
              monthAlgo: MonthAlgo,
              clockwiseHouse: Boolean): Palm {

    return trueRising?.let { rising ->
      getPalm(gender, chineseDateHour.year.branch, chineseDateHour.isLeapMonth, chineseDateHour.month,
              chineseDateHour.day, chineseDateHour.hourBranch, positiveImpl, rising     , monthBranch, monthAlgo,
              clockwiseHouse)
    }?: getPalm(gender, chineseDateHour.year.branch, chineseDateHour.isLeapMonth, chineseDateHour.month,
              chineseDateHour.day, chineseDateHour.hourBranch, positiveImpl, monthBranch, monthAlgo, clockwiseHouse)
  }



  /**
   * 大運從掌中年上起月，男順、女逆，輪數至本生月起運。本生月所在宮為一運，下一宮為二運，而一運管10年。
   *
   *
   * 女則逆行，也從子（天貴宮）上起正月，三月則落在戌（天藝宮）上，戌為初運（1－10歲），酉（天刃宮）為二運（11－20歲）也，餘此類推。
   *
   * 大運盤，每運10年，從 1歲起. 1~10 , 11~20 , 21~30 ...
   * Map 的 key 為「運的開始」: 1 , 11 , 21 , 31 ...
   * @param count : 要算多少組大運
   * 演算法與 [Palm.getMajorFortunes] 一致
   */
  fun getMajorFortunes(gender: Gender, yearBranch: Branch, month: Int, count: Int): Map<Int, Branch> {
    val positive = if (gender === Gender.男) 1 else -1

    // 年上起月
    val monthBranch = yearBranch.next((month - 1) * positive)

    return (1..count).map { (it -1) * 10 + 1 to monthBranch.next((it-1)*positive) }
      .sortedBy { it.first }
      .toMap()
  }

  companion object {

    val logger = LoggerFactory.getLogger(IPalm::class.java)
  }

}
