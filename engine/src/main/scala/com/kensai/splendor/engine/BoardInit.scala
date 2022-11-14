package com.kensai.splendor.engine

import com.kensai.splendor.model.protobuf.model._

import scala.annotation.tailrec
import scala.util.Random

object BoardInit {

  lazy val Nobles = CsvLoaders.loadNobles()
  lazy val Coins = CsvLoaders.loadCoins()
  lazy val Cards = CsvLoaders.loadCards()

  def board(players: Seq[Player]): Board =
    board(System.currentTimeMillis(), players)

  def board(gameId: Long, players: Seq[Player]): Board =
    board(
      gameId = gameId,
      nobles = Nobles,
      coins = Coins,
      cards = Cards,
      players = players
    )

  def board(
      gameId: Long,
      nobles: Seq[Noble],
      coins: Map[Gem, Int],
      cards: Seq[Card],
      players: Seq[Player]
  ): Board = {
    val cardsByTier = cards.groupBy(_.tierList)
    val (restCards1, displayedCards1) = extractRandom(4, cardsByTier(1))
    val (restCards2, displayedCards2) = extractRandom(4, cardsByTier(2))
    val (restCards3, displayedCards3) = extractRandom(4, cardsByTier(3))
    val (_, pickedUpNobles) = extractRandom(4, nobles)
    Board(
      gameId = gameId,
      coins = coins.map{case (key, value) => GemCount(key, value)}.toSeq,
      displayedCards1 = displayedCards1,
      hiddenCards1 = restCards1,
      displayedCards2 = displayedCards2,
      hiddenCards2 = restCards2,
      displayedCards3 = displayedCards3,
      hiddenCards3 = restCards3,
      nobles = pickedUpNobles,
      players = players
    )
  }

  @tailrec
  private def extractRandom[T](count: Int, available: Seq[T], extracted: Seq[T] = Seq()): (Seq[T], Seq[T]) =
    if (extracted.size == count)
      (available, extracted)
    else {
      val index = Random.nextInt(available.size)
      val item = available(index)
      extractRandom(
        count,
        available.filterNot(_ == item),
        extracted :+ item
      )
    }

}
