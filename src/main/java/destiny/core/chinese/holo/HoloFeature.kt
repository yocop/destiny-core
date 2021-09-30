/**
 * Created by smallufo on 2021-09-30.
 */
package destiny.core.chinese.holo

import destiny.core.BirthDataNamePlace
import destiny.core.Gender
import destiny.core.astrology.IZodiacSign
import destiny.core.astrology.Planet
import destiny.core.calendar.*
import destiny.core.calendar.chinese.Yuan
import destiny.core.calendar.eightwords.EightWordsConfig
import destiny.core.calendar.eightwords.EightWordsFeature
import destiny.core.calendar.eightwords.IEightWords
import destiny.core.chinese.*
import destiny.core.chinese.Branch.*
import destiny.core.chinese.holo.IHoloContext.Companion.threeKingHexagrams
import destiny.core.fengshui.sanyuan.ISanYuan
import destiny.core.iching.*
import destiny.core.iching.Symbol.*
import destiny.core.iching.contentProviders.IHexNameFull
import destiny.core.iching.divine.DivineTraditionalConfig
import destiny.core.iching.divine.ISettingsOfStemBranch
import destiny.core.iching.divine.SettingsOfStemBranch
import destiny.tools.AbstractCachedPersonFeature
import destiny.tools.Builder
import destiny.tools.DestinyMarker
import kotlinx.serialization.Serializable
import mu.KotlinLogging
import java.time.chrono.ChronoLocalDateTime
import javax.inject.Named

@Serializable
data class HoloConfig(val divineTraditionalConfig: DivineTraditionalConfig = DivineTraditionalConfig(),
                      val eightWordsConfig: EightWordsConfig = EightWordsConfig(),
                      val seasonalSymbolConfig: SeasonalSymbolConfig = SeasonalSymbolConfig(SeasonalSymbolConfig.Impl.Holo()),
                      val threeKings: ThreeKingsAlgo? = ThreeKingsAlgo.HALF_YEAR,
                      val hexChange: HexChange = HexChange.DST,
                      val monthlyHexagram: MonthlyHexagram = MonthlyHexagram.Sign): java.io.Serializable

@DestinyMarker
class HoloConfigBuilder : Builder<HoloConfig> {

  var divineTraditionalConfig: DivineTraditionalConfig = DivineTraditionalConfig()

  var eightWordsConfig: EightWordsConfig = EightWordsConfig()

  var seasonalSymbolConfig: SeasonalSymbolConfig = SeasonalSymbolConfig(SeasonalSymbolConfig.Impl.Holo())

  var threeKings: ThreeKingsAlgo? = ThreeKingsAlgo.HALF_YEAR

  var hexChange: HexChange = HexChange.DST

  var monthlyHexagram: MonthlyHexagram = MonthlyHexagram.Sign

  override fun build(): HoloConfig {
    return HoloConfig(divineTraditionalConfig, eightWordsConfig, seasonalSymbolConfig, threeKings, hexChange, monthlyHexagram)
  }

  companion object {
    fun holoConfig(block : HoloConfigBuilder.() -> Unit = {}) : HoloConfig {
      return HoloConfigBuilder().apply(block).build()
    }
  }
}


interface IHoloFeature {

  /** 天數 */
  fun getHeavenNumber(ew: IEightWords): Int

  /** 天數 -> 卦 */
  fun getHeavenSymbol(ew: IEightWords, gender: Gender, yuan: Yuan): Symbol

  /** 地數 */
  fun getEarthNumber(ew: IEightWords): Int

  /** 地數 -> 卦 */
  fun getEarthSymbol(ew: IEightWords, gender: Gender, yuan: Yuan): Symbol

  /**
   * 本命卦 (先天卦 without 元堂)
   * */
  fun getHexagram(ew: IEightWords, gender: Gender, yuan: Yuan): Hexagram {
    val heavenSymbol = getHeavenSymbol(ew, gender, yuan)
    val earthSymbol = getEarthSymbol(ew, gender, yuan)

    return if ((gender == Gender.男 && ew.year.stem.booleanValue) || (gender == Gender.女 && !ew.year.stem.booleanValue)) {
      Hexagram.of(heavenSymbol, earthSymbol)
    } else {
      Hexagram.of(earthSymbol, heavenSymbol)
    }
  }

  /**
   * 元堂 , return 1(incl.) to 6(incl.)
   * @param yearHalfYinYang 切割年份的陰陽， 常例為 前半年(冬至 to 夏至) 為 陽 , 後半年(夏至 to 冬至) 為 陰
   * */
  fun getYuanTang(hexagram: IHexagram, hour: Branch, gender: Gender, yearHalfYinYang: IYinYang): Int {
    // 幾個陽爻
    val yangCount = hexagram.booleans.count { it }
    // 幾個陰爻
    val yinCount = hexagram.booleans.count { !it }

    val yangSeq: Sequence<Int> = hexagram.booleans.zip(1..6)
      .filter { (yinYang, _) -> yinYang }
      .map { pair -> pair.second }
      .let { list -> generateSequence { list }.flatten() }

    val yinSeq = hexagram.booleans.zip(1..6)
      .filter { (yinYang, _) -> !yinYang }
      .map { pair -> pair.second }
      .let { list -> generateSequence { list }.flatten() }

    return if (hour.index <= 5) {
      // 子~巳 , 前六時 陽

      when (yangCount) {
        0 -> {
          // 六陰爻
          when (gender) {
            Gender.男 -> {
              if (yearHalfYinYang.booleanValue) {
                // 前半年
                listOf(1, 2, 3, 1, 2, 3)[hour.index]
              } else {
                // 後半年
                listOf(6, 5, 4, 6, 5, 4)[hour.index]
              }
            }
            Gender.女 -> {
              listOf(1, 2, 3, 1, 2, 3)[hour.index]
            }
          }
        }
        1 -> when (hour) {
          in 子..丑 -> yangSeq.take(hour.index + 1).last()
          else    -> yinSeq.take(hour.index - 1).last()
        }
        2, 4 -> when (hour) {
          in 子..卯 -> yangSeq.take(hour.index + 1).last()
          else    -> yinSeq.take(hour.index + 1).last()
        }
        3 -> when (hour) {
          in 子..巳 -> yangSeq.take(hour.index + 1).last()
          else    -> throw RuntimeException("error")
        }
        5 -> when (hour) {
          in 子..辰 -> yangSeq.take(hour.index + 1).last()
          else    -> yinSeq.first()
        }
        else -> {
          // 六陽爻
          when (gender) {
            Gender.男 -> {
              listOf(1, 2, 3, 1, 2, 3)[hour.index]
            }
            Gender.女 -> {
              if (yearHalfYinYang.booleanValue) {
                // 前半年
                listOf(6, 5, 4, 6, 5, 4)[hour.index]
              } else {
                // 後半年
                listOf(1, 2, 3, 1, 2, 3)[hour.index]
              }
            }
          }
        }
      }
    } else {
      // 午~亥 , 後六時 陰
      when (yinCount) {
        0 -> {
          // 六陽爻
          when (gender) {
            Gender.男 -> {
              listOf(4, 5, 6, 4, 5, 6)[hour.index - 6]
            }
            Gender.女 -> {
              if (yearHalfYinYang.booleanValue) {
                // 前半年
                listOf(3, 2, 1, 3, 2, 1)[hour.index - 6]
              } else {
                // 後半年
                listOf(4, 5, 6, 4, 5, 6)[hour.index - 6]
              }
            }
          }
        }
        1 -> when (hour) {
          in 午..未 -> yinSeq.first()
          else    -> yangSeq.take(hour.index - 7).last()
        }
        2, 4 -> when (hour) {
          in 午..酉 -> yinSeq.take(hour.index - 5).last()
          else    -> yangSeq.take(hour.index - 9).last()
        }
        3 -> when (hour) {
          in 午..亥 -> yinSeq.take(hour.index - 5).last()
          else    -> throw RuntimeException("error")
        }
        5 -> when (hour) {
          in 午..戌 -> yinSeq.take(hour.index - 5).last()
          else    -> yangSeq.first()
        }
        else -> {
          // 六陰爻
          when (gender) {
            Gender.男 -> {
              if (yearHalfYinYang.booleanValue) {
                // 前半年
                listOf(4, 5, 6, 4, 5, 6)[hour.index - 6]
              } else {
                // 後半年
                listOf(3, 2, 1, 3, 2, 1)[hour.index - 6]
              }
            }
            Gender.女 -> {
              listOf(4, 5, 6, 4, 5, 6)[hour.index - 6]
            }
          }
        }
      }
    }


  }

  /** 先天卦 (with 元堂 : 1~6) */
  fun getHexagramCongenital(ew: IEightWords, gender: Gender, yuan: Yuan, yearHalfYinYang: IYinYang): Pair<Hexagram, Int> {
    if (yearHalfYinYang.booleanValue) {
      require(!(未..亥).contains(ew.month.branch)) {
        "前半年，月支 ${ew.month.branch} 不可以出現在 未~亥 當中"
      }
    } else {
      require(!(丑..巳).contains(ew.month.branch)) {
        "後半年，月支 ${ew.month.branch} 不可以出現在 丑~巳 當中"
      }
      // 冬至、夏至 平分 子月、午月 ，無法從八字中判斷
    }
    val hex = getHexagram(ew, gender, yuan)
    val yuanTang = getYuanTang(hex, ew.hour.branch, gender, yearHalfYinYang)
    return hex to yuanTang
  }


  /**
   * 後天卦 (with 元堂)
   * @param yinYang 在「三至尊卦」 [threeKingHexagrams] 的情形下，陰陽如何判別？ 是依據切割年份的陰陽 ( [ThreeKingsAlgo.HALF_YEAR] ) , 或是依據 月份地支 判斷的陰陽 ( [ThreeKingsAlgo.MONTH_BRANCH] )
   *  */
  fun getHexagramAcquired(hexagramCongenital: IHexagram, yuanTang: Int, yinYang: IYinYang, threeKings: ThreeKingsAlgo?): Pair<Hexagram, Int> {

    val newYuanTang = ((yuanTang + 3) % 6).let {
      if (it == 0)
        6
      else
        it
    }

    return hexagramCongenital.booleans.mapIndexed { index, booleanValue ->
      if (index == yuanTang - 1)
        !booleanValue
      else
        booleanValue
    }.let { list ->
      Hexagram.of(list).let { hex ->
        val upperSymbol = hex.upperSymbol
        val lowerSymbol = hex.lowerSymbol

        if (threeKings != null && threeKingHexagrams.contains(hexagramCongenital) && (yuanTang == 5 || yuanTang == 6)) {
          /** 三至尊卦 : [Hexagram.坎] [Hexagram.屯] [Hexagram.蹇] , 且 , 元堂 為 5 or 6 */
          if (yuanTang == 5) {
            if (yinYang.booleanValue) {
              Hexagram.of(lowerSymbol, upperSymbol) to newYuanTang
            } else {
              // 爻變，上下不動
              Hexagram.of(upperSymbol, lowerSymbol) to yuanTang
            }
          } else {
            // 6
            if (yinYang.booleanValue) {
              // 爻變，上下不動
              Hexagram.of(upperSymbol, lowerSymbol) to yuanTang
            } else {
              Hexagram.of(lowerSymbol, upperSymbol) to newYuanTang
            }
          }

        } else {
          // 正常狀況 上下交換
          Hexagram.of(lowerSymbol, upperSymbol) to newYuanTang
        }
      }
    }
  }

  /**
   * @param hex 先天卦 or 後天卦
   * @param yuanTangIndexFrom1 元堂 (1~6)
   * @return Holo卦象 以及 出生之後每個立春的GMT時刻(亦即：原歲數截止時刻 & 新歲數開始時刻 ）
   */
  fun getHoloHexagramAndAgeList(hex: Hexagram, yuanTangIndexFrom1: Int, initGmtJulDay: GmtJulDay, initStemBranch: IStemBranch, settings: SettingsOfStemBranch, hexChange: HexChange): List<HoloLine>

  /**
   * 取得流年卦 , 陽爻 取 9 年， 陰爻 取 6 年
   * @param hex 先天卦
   * @param lineIndex 元堂 (1~6)
   * @param stemBranch 第一年的干支
   */
  fun getYearlyHexagrams(hex: Hexagram, lineIndex: Int, stemBranch: IStemBranch, hexChange: HexChange): Sequence<Triple<Hexagram, Int, IStemBranch>>

}

@Named
class HoloFeature(private val solarTermsImpl: ISolarTerms,
                  private val settingsMap : Map<SettingsOfStemBranch, ISettingsOfStemBranch>,
                  private val sanYuanImpl: ISanYuan,
                  private val eightWordsFeature: EightWordsFeature,
                  private val numberize: INumberize,
                  private val yuanGenderImpl: IYuanGender,
                  private val zodiacSignImpl: IZodiacSign,
                  private val yearSplitterImpl: IYearSplitterBySign,
                  private val dailyHexagramService: IDailyHexagramService,
                  private val hexSolidImpl: IHexSolid,
                  private val seasonalSymbolFeature: SeasonalSymbolFeature,
                  private val monthlyHexagramMap: Map<MonthlyHexagram, IMonthlyHexagram>,
                  private val goldenKeyProvider: IGoldenKeyProvider,
                  private val nameFullImpl: IHexNameFull,
                  private val julDayResolver: JulDayResolver) : AbstractCachedPersonFeature<HoloConfig, IHolo>() , IHoloFeature {

  override val key: String = "holoFeature"

  override val defaultConfig: HoloConfig = HoloConfig()

  private fun Int.isOdd(): Boolean {
    return this % 2 == 1
  }

  private fun Int.isEven(): Boolean {
    return this % 2 == 0
  }

  /** 將 爻 限制在 1~6 之間 */
  private fun confine(line: Int): Int {
    return (line % 6).let { r -> if (r == 0) 6 else r }
  }

  /** @param line 第幾爻變換 (1~6) */
  private fun switch(hex: IHexagram, line: Int): Pair<Hexagram, Int> {
    val confinedLine = confine(line)
    return Hexagram.of(hex.getTargetYinYangs(confinedLine)) to confinedLine
  }

  /** 天數 */
  override fun getHeavenNumber(ew: IEightWords): Int {
    return ew.stemBranches.sumOf { sb ->
      (numberize.getNumber(sb.stem).takeIf { it.isOdd() } ?: 0) +
        numberize.getNumber(sb.branch).filter { it.isOdd() }.sum()
    }
  }

  /** 天數 -> 卦 */
  override fun getHeavenSymbol(ew: IEightWords, gender: Gender, yuan: Yuan): Symbol {
    fun shrink(value: Int): Int {
      return when {
        value > 25 -> shrink(value % 25)
        value == 25 -> 5
        else -> (value % 10).let { reminder ->
          if (reminder != 0)
            reminder
          else
            value / 10
        }
        //else -> (value % 10)
      }
    }

    val value = shrink(getHeavenNumber(ew))

    return SymbolAcquired.getSymbol(value) ?: yuanGenderImpl.getSymbol(gender, yuan, ew.year.stem)
  }

  /** 地數 */
  override fun getEarthNumber(ew: IEightWords): Int {
    return ew.stemBranches.sumOf { sb ->
      (numberize.getNumber(sb.stem).takeIf { it.isEven() } ?: 0) +
        numberize.getNumber(sb.branch).filter { it.isEven() }.sum()
    }
  }

  /** 地數 -> 卦 */
  override fun getEarthSymbol(ew: IEightWords, gender: Gender, yuan: Yuan): Symbol {
    fun shrink(value: Int): Int {
      return if (value > 30) {
        shrink(value % 30)
      } else {
        (value % 10).let { reminder ->
          if (reminder != 0)
            reminder
          else
            value / 10
        }
      }
    }

    val value = shrink(getEarthNumber(ew))

    return SymbolAcquired.getSymbol(value) ?: yuanGenderImpl.getSymbol(gender, yuan, ew.year.stem)
  }

  /**
   * @param hex 先天卦 or 後天卦
   * @param yuanTangIndexFrom1 元堂 (1~6)
   * @return Holo卦象 以及 出生之後每個立春的GMT時刻(亦即：原歲數截止時刻 & 新歲數開始時刻 ）
   */
  override fun getHoloHexagramAndAgeList(hex: Hexagram, yuanTangIndexFrom1: Int, initGmtJulDay: GmtJulDay, initStemBranch: IStemBranch, settings: SettingsOfStemBranch, hexChange: HexChange): List<HoloLine> {
    return generateSequence { hex.yinYangs }
      .flatten()
      .drop(yuanTangIndexFrom1 - 1) // 從「元堂」爻開始
      .withIndex()
      .map {
        // 把原本的 zero-based list index 改為 1~6
        // index : 第幾爻 (1~6)
        val index = ((it.index + yuanTangIndexFrom1) % 6).let { r -> if (r == 0) 6 else r }
        index to it.value
      }
      .take(6)
      .foldIndexed(mutableListOf<Pair<Int, HoloLine>>()) { indexFrom0to5, lineIndex_holoLine, lineIndex_boolean ->
        val yinYang = lineIndex_boolean.second
        val yuanTang: Boolean = (indexFrom0to5 == 0)
        val startFortuneGmtJulDay = if (lineIndex_holoLine.isEmpty()) initGmtJulDay else lineIndex_holoLine.last().second.endExclusive

        val stemBranchOf1stYear: IStemBranch = lineIndex_holoLine.lastOrNull()?.second?.hexagrams?.last()?.stemBranch?.next
          ?: initStemBranch

        // 每年 卦象 、元堂 以及 該年的干支
        val yearlySeq: Sequence<Triple<Hexagram, Int, IStemBranch>> = getYearlyHexagrams(hex, yuanTangIndexFrom1 + indexFrom0to5, stemBranchOf1stYear, hexChange)

        // 6 or 9 條流年資訊
        val yearly: List<HoloHexagramWithStemBranch> = generateSequence(startFortuneGmtJulDay) {
          solarTermsImpl.getSolarTermsTime(SolarTerms.立春, it + 0.1, true)
        }
          .take(if (lineIndex_boolean.second.booleanValue) 9 + 1 else 6 + 1)
          .windowed(2, 1, false)
          .map { it[0] to it[1] }
          .zip(yearlySeq)
          .map { (from_to, hex_lineIndex_stemBranch) ->
            val fromGmt = from_to.first
            val toGmt = from_to.second
            val yearlyHex: Hexagram = hex_lineIndex_stemBranch.first
            val lineIndex = hex_lineIndex_stemBranch.second
            val stemBranch = hex_lineIndex_stemBranch.third
            val settingsImpl = settingsMap[settings]!!
            val stemBranches = (1..6).map { settingsImpl.getStemBranch(yearlyHex, it) }.toList()
            val holoHexagram: IHoloHexagram = HoloHexagram(IHoloHexagram.Scale.YEAR, yearlyHex, lineIndex, stemBranches, fromGmt, toGmt)
            HoloHexagramWithStemBranch(holoHexagram, stemBranch)
          }.toList()

        val line = HoloLine(yinYang, yuanTang, yearly)
        lineIndex_holoLine.add(lineIndex_boolean.first to line)
        lineIndex_holoLine
      }
      .sortedBy { (lineIndex, _) -> lineIndex }
      .map { (_, holoLine) -> holoLine }
  }


  /**
   * 取得流年卦 , 陽爻 取 9 年， 陰爻 取 6 年
   * @param hex 先天卦
   * @param lineIndex 元堂 (1~6)
   * @param stemBranch 第一年的干支
   */
  override fun getYearlyHexagrams(hex: Hexagram, lineIndex: Int, stemBranch: IStemBranch, hexChange: HexChange): Sequence<Triple<Hexagram, Int, IStemBranch>> {

    logger.debug("getYearlyHexagrams() , hex = {} , lineIndex = {} , 年 = {}", Hexagram.of(hex), confine(lineIndex), stemBranch)

    val confinedLine = confine(lineIndex)

    return if (hex.getBoolean(confinedLine)) {
      // 陽爻
      val firstYear: Triple<Hexagram, Int, Int> = if (!stemBranch.stem.booleanValue) {
        logger.debug("陽爻，元堂，流年逢陰年 必變")
        if (hexChange == HexChange.SRC) {
          Triple(switch(hex, confinedLine).first, confine(confinedLine + 3), 1)
        } else {
          switch(hex, confinedLine).let { Triple(it.first, it.second, 1) }
        }
      } else {
        logger.debug("陽爻，元堂，流年仍陽年 ") // 第一年不用變
        Triple(Hexagram.of(hex), confinedLine, 1)
      }


      /**  [HexChange.SRC] 設定 */
      val srcSeq = generateSequence(firstYear) { triple ->
        when (triple.third) {
          1 -> {
            if (!stemBranch.stem.booleanValue) // 陰年
              Triple(switch(triple.first, triple.second).first, confine(triple.second + 3), triple.third + 1)
            else
              Triple(triple.first, confine(triple.second + 3), triple.third + 1) // 陽年
          }
          2 -> Triple(switch(triple.first, triple.second).first, confine(triple.second + 3), triple.third + 1)
          else -> Triple(switch(triple.first, triple.second).first, confine(triple.second + 1), triple.third + 1)
        }
      }

      /**  [HexChange.DST] 設定 */


      val dstSeq = generateSequence(firstYear) { triple ->
        if (triple.third < 9) {
          val toAddLine = when (triple.third) {
            1 -> 3
            2 -> 3
            else -> 1
          }
          val (a, b) = switch(triple.first, triple.second + toAddLine)
          Triple(a, b, triple.third + 1)
        } else {
          null
        }
      }

      val seq = if (hexChange == HexChange.SRC) srcSeq else dstSeq
      seq.take(9)
        .map { triple: Triple<Hexagram, Int, Int> ->
          Triple(triple.first, triple.second, stemBranch.next(triple.third - 1))
        }
    } else {
      // 陰爻
      generateSequence(switch(hex, confinedLine)) { pair ->
        switch(pair.first, pair.second + 1)
      }
        .take(6)
        .mapIndexed { indexFrom0, hexAndLine ->
          Triple(hexAndLine.first, hexAndLine.second, stemBranch.next(indexFrom0))
        }.map { triple: Triple<Hexagram, Int, IStemBranch> ->
          if (hexChange == HexChange.SRC) {
            val yearHex = switch(triple.first, triple.second).first
            Triple(yearHex, triple.second, triple.third)
          } else {
            triple
          }
        }
    }
  }

  override fun calculate(gmtJulDay: GmtJulDay, loc: ILocation, gender: Gender, name: String?, place: String?, config: HoloConfig): IHolo {
    val lmt = TimeTools.getLmtFromGmt(gmtJulDay, loc, julDayResolver)
    return getPersonModel(lmt, loc, gender, name, place, config)
  }

  override fun calculate(lmt: ChronoLocalDateTime<*>, loc: ILocation, gender: Gender, name: String?, place: String?, config: HoloConfig): IHolo {
    val yuan = sanYuanImpl.getYuan(lmt, loc)
    val gmtJulDay = TimeTools.getGmtJulDay(lmt, loc)

    val ew: IEightWords = eightWordsFeature.getModel(lmt, loc, config.eightWordsConfig)

    // 天數
    val heavenNumber = getHeavenNumber(ew)
    val heavenSymbol = getHeavenSymbol(ew, gender, yuan)

    // 地數
    val earthNumber = getEarthNumber(ew)
    val earthSymbol = getEarthSymbol(ew, gender, yuan)

    val sign = zodiacSignImpl.getSign(Planet.SUN, gmtJulDay)
    val yearHalfYinYang: IYinYang = yearSplitterImpl.getYinYang(sign)

    // 當年立春時刻
    val startOfYear = solarTermsImpl.getSolarTermsTime(SolarTerms.立春, gmtJulDay, false)

    val settingsImpl = settingsMap[config.divineTraditionalConfig.settings]!!
    // 先天命卦 , 以及六爻大運、流年 等資訊
    val hexagramCongenital: ILifeHoloHexagram = getHexagramCongenital(ew, gender, yuan, yearHalfYinYang).let { (hex, yuanTang) ->
      val lines: List<HoloLine> = getHoloHexagramAndAgeList(hex, yuanTang, gmtJulDay, ew.year, config.divineTraditionalConfig.settings, config.hexChange)
      val stemBranches = (1..6).map { settingsImpl.getStemBranch(hex, it) }.toList()
      val dutyDaysMap = dailyHexagramService.getDutyDays(hex, startOfYear - 0.01, true)
      val hexSolid = hexSolidImpl.getHexagram(hex)
      LifeHoloHexagram(lines, stemBranches, dutyDaysMap, hexSolid)
    }


    val yinYang: IYinYang = config.threeKings?.let { algo ->
      when (algo) {
        ThreeKingsAlgo.HALF_YEAR    -> yearHalfYinYang
        ThreeKingsAlgo.MONTH_BRANCH -> SimpleBranch[ew.month.branch]
      }
    } ?: yearHalfYinYang

    // 後天命卦 , 以及六爻大運、流年 等資訊
    val hexagramAcquired: ILifeHoloHexagram = getHexagramAcquired(hexagramCongenital, hexagramCongenital.yuanTang, yinYang, config.threeKings).let { (hex, yuanTang) ->
      // 先天卦的最後一爻
      val congMaxLine: HoloLine = hexagramCongenital.lines.maxByOrNull { it.endExclusive }!!

      val lines: List<HoloLine> = getHoloHexagramAndAgeList(hex, yuanTang, congMaxLine.endExclusive, congMaxLine.hexagrams.last().stemBranch.next, config.divineTraditionalConfig.settings, config.hexChange)
      val stemBranches = (1..6).map { settingsImpl.getStemBranch(hex, it) }.toList()
      val dutyDaysMap = dailyHexagramService.getDutyDays(hex, startOfYear - 0.01, true)
      val hexSolid = hexSolidImpl.getHexagram(hex)
      LifeHoloHexagram(lines, stemBranches, dutyDaysMap, hexSolid)
    }

    // 天元氣
    val vigorousSymbolFromStem = SymbolAcquired.getSymbol(numberize.getNumber(ew.year.stem))!!
    // 地元氣
    val vigorousSymbolFromBranch = ew.year.branch.let { branch ->
      when (branch) {
        子    -> 坎
        丑, 寅 -> 艮
        卯    -> 震
        辰, 巳 -> 巽
        午    -> 離
        未, 申 -> 坤
        酉    -> 兌
        戌, 亥 -> 乾
      }
    }
    // 天元氣相反
    val vigorlessSymbolFromStem = vigorousSymbolFromStem.let { symbol -> SymbolCongenital.getOppositeSymbol(symbol) }
    // 地元氣相反
    val vigorlessSymbolFromBranch = vigorousSymbolFromBranch.let { symbol -> SymbolCongenital.getOppositeSymbol(symbol) }

    // 化工 : 得到季節力量者 , 這裡的季節，與中國的季節不同（遲了一個半月），反而與西洋的季節定義相同 : 二分二至 為 春夏秋冬的起點
    // 另外考慮了季月 艮坤 旺 18日

    val seasonalSymbols: Set<Symbol> = seasonalSymbolFeature.getModel(lmt, loc, config.seasonalSymbolConfig)

    // 化工反例
    val seasonlessSymbols = seasonalSymbols.map { SymbolCongenital.getOppositeSymbol(it) }.toSet()

    // 位於哪兩個「節或氣」之間
    val solarTermsPos: SolarTermsTimePos = solarTermsImpl.getSolarTermsPosition(gmtJulDay)


    val birthData = BirthDataNamePlace(gender, lmt, loc, name, place)

    // 四節氣卦
    val seasonalHexagram: Pair<Hexagram, Int> = seasonHexMap.getValue(solarTermsPos.solarTerms).let { pair ->
      Hexagram.of(pair.first, pair.first) to pair.second
    }

    // 12消息卦
    val monthlyHexagramImpl = monthlyHexagramMap[config.monthlyHexagram]!!
    val monthlyHexagram: Hexagram = monthlyHexagramImpl.getHexagram(solarTermsPos.solarTerms).first

    // 當下值日卦列表
    val dailyHexagramMap: Map<IDailyHexagram, Hexagram> = dailyHexagramService.getHexagramMap(gmtJulDay).map { (k, v) ->
      k to v.first
    }.toMap()

    // 金鎖銀匙歌 參評歌訣
    val goldenKey = goldenKeyProvider.getBaseGoldenKey(NaYin.getFiveElement(ew.year), ew.day.branch, ew.hour.branch, gender)

    val summaries: List<String> = run {
      val line1 = StringBuilder().apply {
        append("天數")
        append(heavenNumber)
        append("，地數")
        append(earthNumber)
      }.toString()

      val line2 = StringBuilder().apply {
        append("先天卦：")
        append(nameFullImpl.getHexagram(Hexagram.of(hexagramCongenital)))
        append("，後天卦：")
        append(nameFullImpl.getHexagram(Hexagram.of(hexagramAcquired)))
      }.toString()

      listOf(line1, line2)
    }

    return Holo(birthData, ew, gender, yuan, solarTermsPos,
                heavenNumber, heavenSymbol,
                earthNumber, earthSymbol,
                hexagramCongenital, hexagramAcquired,
                vigorousSymbolFromStem, vigorousSymbolFromBranch,
                vigorlessSymbolFromStem, vigorlessSymbolFromBranch,
                seasonalSymbols, seasonlessSymbols,
                seasonalHexagram, monthlyHexagram,
                dailyHexagramMap,
                goldenKey , summaries)
  }

  companion object {
    private val logger = KotlinLogging.logger { }
  }
}
