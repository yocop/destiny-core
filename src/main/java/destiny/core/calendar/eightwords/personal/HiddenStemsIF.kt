/**
 * @author smallufo
 * @date 2005/4/7
 * @time 下午 02:04:42
 */
package destiny.core.calendar.eightwords.personal

import destiny.core.chinese.Branch
import destiny.core.chinese.Stem


/** 地支藏干  */
interface HiddenStemsIF {
  fun getHiddenStems(branch: Branch): List<Stem>
}
