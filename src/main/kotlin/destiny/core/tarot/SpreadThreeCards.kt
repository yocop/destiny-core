/**
 * Created by smallufo on 2023-04-02.
 */
package destiny.core.tarot

import destiny.tools.ILocaleString
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import java.util.*
import java.util.Locale.*

/**
 * maybe Major Arcana
 */
@Serializable
@SerialName("3cards")
data class SpreadThreeCards(@SerialName("c1") val card1: CardOrientation,
                            @SerialName("c2") val card2: CardOrientation,
                            @SerialName("c3") val card3: CardOrientation) : ISpread {

  override fun getLocalePosMap(): List<Pair<CardOrientation, Map<Locale, String>>> {
    return listOf(
      card1 to
        mapOf(
          TAIWAN to "左方",
          JAPANESE to "左",
          ENGLISH to "Left",
        ),
      card2 to mapOf(
        TAIWAN to "中央",
        JAPANESE to "中央",
        ENGLISH to "Middle",
      ),
      card3 to mapOf(
        TAIWAN to "右方",
        JAPANESE to "右方",
        ENGLISH to "Right",
      ),
    )
  }

  override fun getTitle(locale: Locale): String {
    return SpreadThreeCards.getTitle(locale)
  }

  companion object : ILocaleString {
    fun of(list: List<CardOrientation>): SpreadThreeCards {
      require(list.size == 3)
      return SpreadThreeCards(list[0], list[1], list[2])
    }

    override fun getTitle(locale: Locale): String {
      return when (locale.language) {
        "en" -> "Three Card Spread"
        "ja" -> "三枚カード"
        else -> "三牌法"
      }
    }
  }
}
