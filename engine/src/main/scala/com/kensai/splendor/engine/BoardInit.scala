package com.kensai.splendor.engine

import com.kensai.splendor.model.protobuf.model._

import scala.annotation.tailrec
import scala.util.Random

object BoardInit {

  def board(
      gameId: Long,
      nobles: Seq[Noble],
      coins: Map[Gem, Int],
      cards: Seq[Card],
      players: Seq[Player]
  ): Board = {
    val cardsByTier = cards.groupBy(_.tierList)
    val (availableCards1, displayedCards1) = extractRandomCards(cardsByTier(1))
    val (availableCards2, displayedCards2) = extractRandomCards(cardsByTier(2))
    val (availableCards3, displayedCards3) = extractRandomCards(cardsByTier(3))
    Board(
      gameId = gameId,
      availableCoins = coins.map{case (key, value) => GemCount(key, value)}.toSeq,
      displayedCards1 = availableCards1,
      availableCards1 = displayedCards1,
      displayedCards2 = availableCards2,
      availableCards2 = displayedCards2,
      displayedCards3 = availableCards3,
      availableCards3 = displayedCards3,
      availableNobles = nobles,
      players = players
    )
  }

  @tailrec
  private def extractRandomCards(
      availableCards: Seq[Card],
      extractedCards: Seq[Card] = Seq()
  ): (Seq[Card], Seq[Card]) =
    if (extractedCards.size == 4)
      (availableCards, extractedCards)
    else {
      val index = Random.nextInt(availableCards.size)
      val card = extractedCards(index)
      extractRandomCards(
        availableCards.filterNot(_ == card),
        extractedCards :+ card
      )
    }

}
