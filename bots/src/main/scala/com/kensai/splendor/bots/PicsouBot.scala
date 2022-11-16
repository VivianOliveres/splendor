package com.kensai.splendor.bots

import cats.implicits._
import com.kensai.splendor.model.ModelHelpers._
import com.kensai.splendor.model.protobuf.model._

/**
 * Always buy the cheapest cards (but yet with the more points) card available.
 */
class PicsouBot(val playerNumber: Int) extends Bot {

  override def play(board: Board): PlayerAction = {
    val player = board.players(playerNumber)

    val gems = (player.cards.map(card => GemCount(card.valueType, 1)).toEnumMap |+| player.coins.toEnumMap).toGemCounts
    val displayedCards = board.displayedCards3 ++ board.displayedCards2 ++ board.displayedCards1

    val maybeCardToBuy = displayedCards.find(card => gems.containsAll(card.costs))
    if (maybeCardToBuy.isDefined)
      PlayerAction(buyCard = Some(BuyCard(maybeCardToBuy.get)))
    else {
//      val
      ???
    }


  }
}
