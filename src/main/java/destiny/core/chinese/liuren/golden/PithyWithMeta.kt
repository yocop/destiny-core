/**
 * Created by smallufo on 2015-05-31.
 */
package destiny.core.chinese.liuren.golden

import destiny.astrology.DayNight
import destiny.astrology.DayNightDifferentiator
import destiny.core.Gender
import destiny.core.calendar.LocationPlace
import destiny.core.chinese.IClockwise
import destiny.core.chinese.IMonthMaster
import destiny.core.chinese.ITianyi
import destiny.core.chinese.liuren.General
import destiny.core.chinese.liuren.IGeneralSeq
import destiny.core.chinese.liuren.IGeneralStemBranch
import java.io.Serializable
import java.util.*

class PithyWithMeta(val pithy: Pithy, private val method: Method?, val gender: Gender, val question: String, val locationPlace: LocationPlace,
                    /** 月將  */
                    private val monthMasterImpl: IMonthMaster,
                    /** 晝夜區分  */
                    val dayNightImpl: DayNightDifferentiator,
                    /** 天乙貴人  */
                    val tianyiImpl: ITianyi,
                    /** 貴神順逆  */
                    private val clockwiseImpl: IClockwise,
                    /** 12天將順序  */
                    private val seqImpl: IGeneralSeq,
                    /** 12天將干支  */
                    private val generalStemBranchImpl: IGeneralStemBranch) : Serializable {

  /** 起課方式  */
  enum class Method {
    RANDOM, MANUAL
  }


  override fun toString(): String {
    val sb = StringBuilder()
    val ew = pithy.eightWords
    sb.append("　日").append("\n")
    sb.append(ew.hour.stem).append(ew.day.stem).append(ew.month.stem).append(ew.year.stem).append("\n")
    sb.append(ew.hour.branch).append(ew.day.branch).append(ew.month.branch).append(ew.year.branch).append("\n")
    sb.append("\n")
    sb.append("月將：").append(pithy.monthSign).append("（").append(monthMasterImpl.getTitle(Locale.TAIWAN)).append("）").append("\n")
    sb.append("晝夜：").append(if (pithy.dayNight == DayNight.DAY) "日" else "夜").append("\n")
    sb.append("年空：").append(ew.year.empties.joinToString("、") { it.toString() }).append("\n")
    //sb.append("年空：").append(ew.year.empties.stream().map<String>(Function<Branch, String> { it.toString() }).collect<String, *>(Collectors.joining("、"))).append("\n")
    sb.append("日空：").append(ew.day.empties.joinToString("、") { it.toString() }).append("\n")
    //sb.append("日空：").append(ew.day.empties.stream().map<String>(Function<Branch, String> { it.toString() }).collect<String, *>(Collectors.joining("、"))).append("\n")
    sb.append("\n")
    sb.append("人元：").append(pithy.human).append("\n")
    val 貴神 = pithy.benefactor
    sb.append("貴神：").append(貴神).append("（").append(General.get(貴神.branch, generalStemBranchImpl)).append("）").append("\n")
    val 將神 = pithy.johnson
    sb.append("將神：").append(將神).append("（").append(IMonthMaster.getName(將神.branch)).append("）").append("\n")
    sb.append("地分：").append(pithy.direction)
    sb.append("\n\n")

    sb.append("性別：").append(gender).append("\n")
    sb.append("問題：").append(question).append("\n")
    sb.append("地點：").append(locationPlace.place).append("\n")

    if (method != null)
      sb.append("起課方式：").append(if (method == Method.RANDOM) "電腦起課" else "手動起課").append("\n")
    sb.append("晝夜設定：").append(dayNightImpl.getTitle(Locale.TAIWAN)).append("\n")
    sb.append("天乙貴人：").append(tianyiImpl.getTitle(Locale.TAIWAN)).append("\n")
    sb.append("順逆設定：").append(clockwiseImpl.getTitle(Locale.TAIWAN)).append("\n")
    sb.append("天將順序：").append(seqImpl.getTitle(Locale.TAIWAN)).append("\n")
    sb.append("天將干支：").append(generalStemBranchImpl.getTitle(Locale.TAIWAN)).append("\n")

    return sb.toString()
  }


}
