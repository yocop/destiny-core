/**
 * Created by smallufo on 2021-08-07.
 */
package destiny.core.calendar.eightwords

import destiny.core.News
import destiny.core.astrology.*
import destiny.core.calendar.*
import destiny.core.chinese.*
import destiny.core.chinese.Branch.*
import mu.KotlinLogging
import java.time.chrono.ChronoLocalDateTime
import java.time.temporal.ChronoField
import java.time.temporal.ChronoUnit

val logger = KotlinLogging.logger { }


fun getYear(
  gmtJulDay: GmtJulDay,
  loc: ILocation,
  changeYearDegree: Double,
  julDayResolver: JulDayResolver,
  starPositionImpl: IStarPosition<*>
): StemBranch {
  val lmt = TimeTools.getLmtFromGmt(gmtJulDay, loc, julDayResolver)

  val resultStemBranch: StemBranch
  //西元 1984 年為 甲子年
  val index = if (lmt.get(ChronoField.YEAR) > 0)
    (lmt.get(ChronoField.YEAR) - 1984) % 60
  else
    (1 - lmt.get(ChronoField.YEAR) - 1984) % 60

  val gmtSecondsOffset = TimeTools.getDstSecondOffset(lmt, loc).second.toDouble()

  val gmtSecondsOffsetInt = gmtSecondsOffset.toInt()
  val gmtNanoOffset = ((gmtSecondsOffset - gmtSecondsOffsetInt) * 1000000000).toInt()

  val gmt =
    lmt.minus(gmtSecondsOffsetInt.toLong(), ChronoUnit.SECONDS).minus(gmtNanoOffset.toLong(), ChronoUnit.NANOS)


  val solarLngDeg = starPositionImpl.getPosition(Planet.SUN, gmt, Centric.GEO, Coordinate.ECLIPTIC).lng
  if (solarLngDeg < 180)
  //立春(0)過後，到秋分之間(180)，確定不會換年
    resultStemBranch = StemBranch[index]
  else {
    // 360 > solarLongitude >= 180

    //取得 lmt 當年 1/1 凌晨零分的度數
    val startOfYear = lmt
      .with(ChronoField.DAY_OF_YEAR, 1)
      .with(ChronoField.HOUR_OF_DAY, 0)
      .with(ChronoField.MINUTE_OF_HOUR, 0)
      .minus(gmtSecondsOffsetInt.toLong(), ChronoUnit.SECONDS)

    val degreeOfStartOfYear =
      starPositionImpl.getPosition(Planet.SUN, startOfYear, Centric.GEO, Coordinate.ECLIPTIC).lng

    if (changeYearDegree >= degreeOfStartOfYear) {
      resultStemBranch = if (solarLngDeg >= changeYearDegree)
        StemBranch[index]
      else if (changeYearDegree > solarLngDeg && solarLngDeg >= degreeOfStartOfYear) {
        val tempTime = gmt.minus((180 * 24 * 60 * 60).toLong(), ChronoUnit.SECONDS)
        if (TimeTools.isBefore(tempTime, startOfYear))
          StemBranch[index - 1]
        else
          StemBranch[index]
      } else
        StemBranch[index]
    } else {
      // degreeOfStartOfYear > changeYearDegree >= 秋分 (180)
      resultStemBranch = if (solarLngDeg >= degreeOfStartOfYear) {
        val tempTime = gmt.minus((180 * 24 * 60 * 60).toLong(), ChronoUnit.SECONDS)
        if (TimeTools.isBefore(tempTime, startOfYear))
          StemBranch[index]
        else
          StemBranch[index + 1]
      } else {
        if (solarLngDeg >= changeYearDegree)
          StemBranch[index + 1]
        else
          StemBranch[index]
      }
    }
  }
  return resultStemBranch
}


fun getMonth(
  gmtJulDay: GmtJulDay,
  location: ILocation,
  solarTermsImpl: ISolarTerms,
  starPositionImpl: IStarPosition<*>,
  southernHemisphereOpposition: Boolean,
  hemisphereBy: HemisphereBy,
  changeYearDegree: Double,
  julDayResolver: JulDayResolver
): IStemBranch {

  val resultMonthBranch: Branch
  //先算出太陽在黃經上的度數

  // 目前的節氣
  val solarTerms = solarTermsImpl.getSolarTermsFromGMT(gmtJulDay)

  val monthIndex = (SolarTerms.getIndex(solarTerms) / 2 + 2).let {
    if (it >= 12)
      it - 12
    else
      it
  }

  // 月支
  val monthBranch = Branch[monthIndex]

  if (southernHemisphereOpposition) {
    /**
     * 解決南半球月支正沖的問題
     */
    if (hemisphereBy == HemisphereBy.EQUATOR) {
      //如果是依據赤道來區分南北半球
      resultMonthBranch = if (location.northSouth == News.NorthSouth.SOUTH)
        Branch[monthIndex + 6]
      else
        monthBranch
    } else {
      /**
       * 如果 hemisphereBy == DECLINATION (赤緯) , 就必須計算 太陽在「赤緯」的度數
       */
      val solarEquatorialDegree = starPositionImpl.getPosition(Planet.SUN, gmtJulDay, Centric.GEO, Coordinate.EQUATORIAL).lat

      if (solarEquatorialDegree >= 0) {
        //如果太陽在赤北緯
        resultMonthBranch = if (location.northSouth == News.NorthSouth.NORTH) {
          //地點在北半球
          if (location.lat >= solarEquatorialDegree)
            monthBranch
          else
            Branch[monthIndex + 6] //所在地緯度低於 太陽赤緯，取對沖月份
        } else {
          //地點在南半球 , 取正沖
          Branch[monthIndex + 6]
        }
      } else {
        //太陽在赤南緯
        resultMonthBranch = if (location.northSouth == News.NorthSouth.SOUTH) {
          //地點在南半球
          if (location.lat <= solarEquatorialDegree)
            Branch[monthIndex + 6] //所在地緯度高於 太陽赤南緯，真正的南半球
          else
            monthBranch //雖在南半球，但緯度低於太陽赤南緯，視為北半球
        } else {
          //地點在北半球，月支不變
          monthBranch
        }
      }
    }
  } else
    resultMonthBranch = monthBranch

  // 年干
  val yearStem = getYear(gmtJulDay, location, changeYearDegree, julDayResolver, starPositionImpl).stem
  return StemBranch[getMonthStem(gmtJulDay, yearStem, resultMonthBranch, changeYearDegree, starPositionImpl), resultMonthBranch]
}

/**
 * 五虎遁月 取得月干
 *
 * 甲己之年丙作首
 * 乙庚之歲戊為頭
 * 丙辛之歲由庚上
 * 丁壬壬位順行流
 * 若言戊癸何方發
 * 甲寅之上好追求。
 *
 */
private fun getMonthStem(
  gmtJulDay: GmtJulDay,
  yearStem: Stem,
  monthBranch: Branch,
  changeYearDegree: Double,
  starPositionImpl: IStarPosition<*>
): Stem {

  // 月干
  var monthStem: Stem = StemBranchUtils.getMonthStem(yearStem, monthBranch)

  if (changeYearDegree != 315.0) {

    val sunDegree = starPositionImpl.getPosition(Planet.SUN, gmtJulDay, Centric.GEO, Coordinate.ECLIPTIC).lng

    if (changeYearDegree < 315) {
      logger.debug("換年點在立春前 , changeYearDegree < 315 , value = {}", changeYearDegree)
      if (sunDegree > changeYearDegree && 315 > sunDegree) {
        // t <---立春---- LMT -----換年點
        monthStem = Stem[monthStem.index - 2]
      }
    } else if (changeYearDegree > 315) {
      //換年點在立春後 , 還沒測試
      if (sunDegree > 315 && changeYearDegree > sunDegree)
        monthStem = Stem[monthStem.index + 2]
    }
  }
  return monthStem
}

fun getHourImpl(hourImpl : DayHourConfig.HourImpl, riseTransImpl : IRiseTrans, julDayResolver: JulDayResolver) : IHour {
  return when(hourImpl) {
    DayHourConfig.HourImpl.TST -> {
      HourSolarTransImpl(riseTransImpl)
    }
    DayHourConfig.HourImpl.LMT -> {
      HourLmtImpl(julDayResolver)
    }
  }
}

fun getDay(
  lmt: ChronoLocalDateTime<*>,
  location: ILocation,
  hourImpl: IHour,
  // 下個子初時刻
  nextZiStart: ChronoLocalDateTime<*>,
  // 下個子正時刻
  nextMidnightLmt: ChronoLocalDateTime<*>,
  changeDayAfterZi: Boolean,
  julDayResolver: JulDayResolver
): StemBranch {

  // 這是很特別的作法，將 lmt 當作 GMT 取 JulDay
  val lmtJulDay = (TimeTools.getGmtJulDay(lmt).value + 0.5).toInt()
  var index = (lmtJulDay - 11) % 60



  if (nextMidnightLmt.get(ChronoField.HOUR_OF_DAY) >= 12) {
    //子正，在 LMT 零時之前
    index = getIndex(index, nextMidnightLmt, lmt, hourImpl, location, changeDayAfterZi, nextZiStart, julDayResolver)
  } else {
    //子正，在 LMT 零時之後（含）
    if (nextMidnightLmt.get(ChronoField.DAY_OF_MONTH) == lmt.get(ChronoField.DAY_OF_MONTH)) {
      // lmt 落於當地 零時 到 子正的這段期間
      if (TimeTools.isBefore(nextZiStart, nextMidnightLmt)) {
        // lmt 落於零時到子初之間 (這代表當地地點「極西」) , 此時一定還沒換日
        index--
      } else {
        // lmt 落於子初到子正之間
        if (!changeDayAfterZi)
        //如果子正才換日
          index--
      }
    } else {
      // lmt 落於前一個子正之後，到當天24時為止 (範圍最大的一塊「餅」)
      if (changeDayAfterZi
        && lmt.get(ChronoField.DAY_OF_MONTH) != nextZiStart.get(ChronoField.DAY_OF_MONTH)
        && nextZiStart.get(ChronoField.HOUR_OF_DAY) >= 12
      )
      // lmt 落於 子初之後 , 零時之前 , 而子初又是在零時之前（hour >=12 , 過濾掉極西的狀況)
        index++
    }
  }
  return StemBranch[index]
}

private fun getIndex(
  index: Int, nextMidnightLmt: ChronoLocalDateTime<*>,
  lmt: ChronoLocalDateTime<*>,
  hourImpl: IHour,
  location: ILocation,
  changeDayAfterZi: Boolean,
  nextZi: ChronoLocalDateTime<*>,
  julDayResolver: JulDayResolver
): Int {

  var result = index
  //子正，在 LMT 零時之前
  if (nextMidnightLmt.get(ChronoField.DAY_OF_MONTH) == lmt.get(ChronoField.DAY_OF_MONTH)) {
    // lmt 落於 當日零時之後，子正之前（餅最大的那一塊）
    val midnightNextZi = hourImpl.getLmtNextStartOf(nextMidnightLmt, location, 子, julDayResolver)

    if (changeDayAfterZi && nextZi.get(ChronoField.DAY_OF_MONTH) == midnightNextZi.get(ChronoField.DAY_OF_MONTH)) {
      result++
    }
  } else {
    // lmt 落於 子正之後，到 24 時之間 (其 nextMidnight 其實是明日的子正) , 則不論是否早子時換日，都一定換日
    result++
  }
  return result
}



/** 真太陽時 */
object Tst {

  fun getHourBranch(gmtJulDay: GmtJulDay, location: ILocation, riseTransImpl: IRiseTrans,
                    atmosphericPressure: Double = 1013.25,
                    atmosphericTemperature: Double = 0.0,
                    discCenter: Boolean = true,
                    refraction: Boolean = true): Branch {

    val nextMeridian =
      riseTransImpl.getGmtTransJulDay(
        gmtJulDay, Planet.SUN, TransPoint.MERIDIAN, location, discCenter, refraction,
        atmosphericTemperature, atmosphericPressure
      )!!
    val nextNadir =
      riseTransImpl.getGmtTransJulDay(
        gmtJulDay, Planet.SUN, TransPoint.NADIR, location, discCenter, refraction,
        atmosphericTemperature, atmosphericPressure
      )!!

    return if (nextNadir > nextMeridian) {
      //子正到午正（上半天）
      val thirteenHoursAgo = gmtJulDay - 13 / 24.0
      val previousNadirGmt =
        riseTransImpl.getGmtTransJulDay(
          thirteenHoursAgo, Planet.SUN, TransPoint.NADIR, location, discCenter,
          refraction, atmosphericTemperature, atmosphericPressure
        )!!

      logger.debug("gmtJulDay = {}", gmtJulDay)

      val diffDays = nextMeridian - previousNadirGmt // 從子正到午正，總共幾秒
      val oneUnitDays = diffDays / 12.0
      logger.debug("diffDays = {} , oneUnitDays = {}", diffDays, oneUnitDays)
      when {
        gmtJulDay < previousNadirGmt + oneUnitDays -> 子
        gmtJulDay < previousNadirGmt + oneUnitDays * 3 -> 丑
        gmtJulDay < previousNadirGmt + oneUnitDays * 5 -> 寅
        gmtJulDay < previousNadirGmt + oneUnitDays * 7 -> 卯
        gmtJulDay < previousNadirGmt + oneUnitDays * 9 -> 辰
        gmtJulDay < previousNadirGmt + oneUnitDays * 11 -> 巳
        else -> 午
      }
    } else {
      //午正到子正（下半天）
      val thirteenHoursAgo = gmtJulDay - 13 / 24.0
      val previousMeridian =
        riseTransImpl.getGmtTransJulDay(
          thirteenHoursAgo, Planet.SUN, TransPoint.MERIDIAN, location, discCenter,
          refraction, atmosphericTemperature, atmosphericPressure
        )!!

      val diffDays = nextNadir - previousMeridian
      val oneUnitDays = diffDays / 12.0

      when {
        gmtJulDay < previousMeridian + oneUnitDays -> 午
        gmtJulDay < previousMeridian + oneUnitDays * 3 -> 未
        gmtJulDay < previousMeridian + oneUnitDays * 5 -> 申
        gmtJulDay < previousMeridian + oneUnitDays * 7 -> 酉
        gmtJulDay < previousMeridian + oneUnitDays * 9 -> 戌
        gmtJulDay < previousMeridian + oneUnitDays * 11 -> 亥
        else -> 子
      }
    }
  }
}


/** 平均太陽時 */
object Lmt {
  fun getHourBranch(gmtJulDay: GmtJulDay, location: ILocation, julDayResolver: JulDayResolver): Branch {
    val gmt = julDayResolver.getLocalDateTime(gmtJulDay)

    val lmt = TimeTools.getLmtFromGmt(gmt, location)
    return getHourBranch(lmt)
  }

  fun getHourBranch(lmt: ChronoLocalDateTime<*>) : Branch {
    return when(val lmtHour = lmt.get(ChronoField.HOUR_OF_DAY)) {
      23, 0  -> 子
      1, 2   -> 丑
      3, 4   -> 寅
      5, 6   -> 卯
      7, 8   -> 辰
      9, 10  -> 巳
      11, 12 -> 午
      13, 14 -> 未
      15, 16 -> 申
      17, 18 -> 酉
      19, 20 -> 戌
      21, 22 -> 亥
      else   -> throw IllegalArgumentException("Cannot find EarthlyBranches for this LMT : $lmtHour")
    }
  }
}



fun getHourStemByGmt(gmtJulDay: GmtJulDay,
                     loc: ILocation,
                     day : StemBranch,
                     cdaz : Boolean,
                     hourBranch : Branch,
                     dayHourImpl : IDayHour,
                     julDayResolver: JulDayResolver , hourImpl : IHour) : Stem {

  logger.trace("[GMT] get HourStem by GMT ...")
  val lmt = TimeTools.getLmtFromGmt(gmtJulDay, loc, julDayResolver)
  val nextZi = hourImpl.getLmtNextStartOf(lmt, loc, 子, julDayResolver)

  // 臨時日干
  val tempDayStem = day.stem.let {
    // 如果「子正」才換日
    if (!cdaz) {
      /**
       * <pre>
       * 而且 LMT 的八字日柱 不同於 下一個子初的八字日柱 發生情況有兩種：
       * 第一： LMT 零時 > 子正 > LMT > 子初 ,（即下圖之 LMT1)
       * 第二： 子正 > LMT > LMT 零時 (> 子初) , （即下圖之 LMT3)
       *
       * 子末(通常1)  LMT4    子正      LMT3       0|24     LMT2        子正    LMT1    子初（通常23)
       * |------------------|--------------------|--------------------|------------------|
      </pre> *
       */
      if (day !== dayHourImpl.getDay(nextZi, loc))
        it.next
      else
        it
    }
    else
      it
  }
  // 時干
  return StemBranchUtils.getHourStem(tempDayStem, hourBranch)
}
