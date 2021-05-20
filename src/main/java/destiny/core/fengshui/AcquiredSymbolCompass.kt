/**
 * @author smallufo
 * @date 2002/9/23
 * @time 下午 07:30:40
 */
package destiny.core.fengshui

import destiny.core.iching.Symbol
import destiny.core.iching.Symbol.*
import destiny.tools.CircleTools
import destiny.tools.CircleTools.normalize
import java.io.Serializable

/**
 * 後天八卦於羅盤上的位置
 */
class AcquiredSymbolCompass : AbstractSymbolCompass(), Serializable {

  /**
   * 取得某個卦的起始度數
   */
  override fun getStartDegree(t: Symbol): Double {
    return (symbolList.indexOf(t) * stepDegree + initDegree).normalize()
  }


  /**
   * 取得某個卦的結束度數
   */
  override fun getEndDegree(t: Symbol): Double {
    return ((symbolList.indexOf(t) + 1) * stepDegree + initDegree).normalize()
  }


  /**
   * 取得目前這個度數位於哪個卦當中
   */
  override fun get(degree: Double): Symbol {
    val index = with(CircleTools) {
      (degree.aheadOf(initDegree) / stepDegree).toInt()
    }
    return symbolList[index]
  }

  companion object {
    private val symbolList = listOf(坎, 艮, 震, 巽, 離, 坤, 兌, 乾)
  }
}
