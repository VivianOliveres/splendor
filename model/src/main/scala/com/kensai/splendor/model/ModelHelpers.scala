package com.kensai.splendor.model

import com.kensai.splendor.model.protobuf.model._
import cats.implicits._

import scala.util.Random

object ModelHelpers {

  implicit class BoardOps(board: Board) {
    def computeWinners: Seq[Int] =
      if (board.turnNumber % board.players.size != 0)
        Seq()
      else {
        val winnersByScore = board.players
          .groupBy(_.cards.map(_.winningPoints).sum)
          .filter(_._1 >= 15)
          .maxByOption(_._1)
          .map(_._2)
          .getOrElse(Seq())
        winnersByScore
          .groupBy(_.cards.size)
          .minByOption(_._1)
          .map(_._2.map(_.playerNumber))
          .getOrElse(Seq())
      }

    def incTurn: Board = {
      val newPlayersToPlay = (board.playersToPlay + 1) % board.players.size
      board.copy(
        playersToPlay = newPlayersToPlay,
        turnNumber = board.turnNumber + 1
      )
    }

    def containCard(card: Card): Boolean =
      board.displayedCards1.contains(card) ||
        board.displayedCards2.contains(card) ||
        board.displayedCards3.contains(card)

    def contains(gems: Seq[Gem]): Boolean =
      contains(gems.map((_, 1)).toMap)

    def contains(gemCounts: Map[Gem, Int]): Boolean = {
      val boardGems = board.coins.toEnumMap
      val toRemove = gemCounts.view.mapValues(-_).toMap
      val result = boardGems |+| toRemove
      result.forall(_._2 >= 0)
    }

    def addCoins(gemCounts: Map[Gem, Int]): Board =
      board.copy(coins = board.coins.add(gemCounts))

    def removeCoins(gemCounts: Map[Gem, Int]): Board =
      board.copy(coins = board.coins.remove(gemCounts))

    def removeCoins(gems: Seq[Gem]): Board =
      removeCoins(gems.groupBy(identity).view.mapValues(_.size).toMap)

    def currentPlayer: Player =
      board.players(board.playersToPlay)

    def update(player: Player, newPlayer: Player): Board =
      board.copy(
        players = board.players.map(p => if (p == player) newPlayer else p)
      )

    def removeDisplayedCard(card: Card): Either[InvalidAction, Board] = {
      card.tierList match {
        case 1 =>
          removeAndAddCard(card, board.hiddenCards1, board.displayedCards1)
            .map { case (avCards, displCards) =>
              board.copy(hiddenCards1 = avCards, displayedCards1 = displCards)
            }
        case 2 =>
          removeAndAddCard(card, board.hiddenCards2, board.displayedCards2)
            .map { case (avCards, displCards) =>
              board.copy(hiddenCards2 = avCards, displayedCards2 = displCards)
            }
        case 3 =>
          removeAndAddCard(card, board.hiddenCards3, board.displayedCards3)
            .map { case (avCards, displCards) =>
              board.copy(hiddenCards3 = avCards, displayedCards3 = displCards)
            }
        case _ =>
          Left(InvalidAction(board.playersToPlay, s"Invalid card: $card"))
      }
    }

    private def removeAndAddCard(
        cardToRemove: Card,
        hiddenCards: Seq[Card],
        displayedCards: Seq[Card]
    ): Either[InvalidAction, (Seq[Card], Seq[Card])] = {
      if (!displayedCards.contains(cardToRemove))
        Left(
          InvalidAction(
            board.playersToPlay,
            s"Card does not exist [$cardToRemove]"
          )
        )
      else if (hiddenCards.isEmpty) {
        val newDisplayedCards = displayedCards.filterNot(_ == cardToRemove)
        Right((hiddenCards, newDisplayedCards))
      } else {
        val index = Random.nextInt(hiddenCards.size)
        val newCard = hiddenCards(index)
        val newHiddenCards = hiddenCards.filterNot(_ == newCard)
        val newDisplayedCards =
          displayedCards.filterNot(_ == cardToRemove) :+ newCard
        Right((newHiddenCards, newDisplayedCards))
      }
    }
  }

  implicit class MapGemCountsOps(map: Map[Gem, Int]) {
    def toGemCounts: Seq[GemCount] =
      map.map(i => GemCount(i._1, i._2)).toSeq
  }

  implicit class GemCounts(gemCounts: Seq[GemCount]) {

    def add(toAdd: Map[Gem, Int]): Seq[GemCount] = {
      val result = gemCounts.toEnumMap |+| toAdd
      result
        .filterNot(_._2 == 0)
        .toGemCounts
    }

    def remove(toAdd: Map[Gem, Int]): Seq[GemCount] =
      add(toAdd.view.mapValues(-_).toMap)

    def containsAll(other: Seq[GemCount]): Boolean =
      remove(other.toEnumMap).forall(_.count >= 0)

    def toEnumMap: Map[Gem, Int] =
      gemCounts
        .map(gc => (gc.gem, gc.count))
        .groupBy(identity)
        .map { case (gc, values) => gc._1 -> values.map(_._2).sum }
  }

  implicit class PlayerOps(player: Player) {

    def addCoins(gems: Seq[Gem]): Player =
      addCoins(gems.map(GemCount(_, 1)).toEnumMap)

    def addCoins(gems: Map[Gem, Int]): Player =
      player.copy(coins = player.coins.add(gems))

    def removeCoins(gems: Map[Gem, Int]): Player =
      player.copy(coins = player.coins.remove(gems))

    def addCard(card: Card): Player =
      player.addCards(card)
  }

}
