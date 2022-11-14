package com.kensai.splendor.model

import com.kensai.splendor.model.DataSamples._
import com.kensai.splendor.model.protobuf.model.Gem._
import com.kensai.splendor.model.protobuf.model._
import com.kensai.splendor.model.ModelHelpers._
import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.should.Matchers


class BoardOpsSpec extends AnyFunSpec with Matchers {

  describe("computeWinners") {
    it("should computeWinners returns nothing when there is a winner but turn is not finished") {
      val winningYennefer = Yennefer.copy(cards = AllCards3.filter(_.winningPoints == 5).take(5))
      val updatedBoard = DefaultBoard.update(Yennefer, winningYennefer).copy(turnNumber = 1)

      val result = updatedBoard.computeWinners

      result shouldBe empty
    }

    it("should computeWinners returns winner when there is a winner and turn is finished") {
      val winningYennefer = Yennefer.copy(cards = AllCards3.filter(_.winningPoints == 5).take(5))
      val updatedBoard = DefaultBoard.update(Yennefer, winningYennefer).copy(turnNumber = 4)

      val result = updatedBoard.computeWinners

      result shouldBe Seq(winningYennefer.playerNumber)
    }

    it("should computeWinners returns nothing when there is no winner and turn is finished") {
      val updatedBoard = DefaultBoard.copy(turnNumber = 4)

      val result = updatedBoard.computeWinners
      println(updatedBoard.turnNumber % updatedBoard.players.size)

      result shouldBe empty
    }

    it("should computeWinners returns nothing when there is no winner and turn is not finished") {
      val updatedBoard = DefaultBoard.copy(turnNumber = 1)
      val result = updatedBoard.computeWinners
      result shouldBe empty
    }

    it("should computeWinners returns best winner when there are two of them") {
      val winningYennefer = Yennefer.copy(cards = AllCards.filter(_.winningPoints == 3).take(5))
      val winningTriss = Triss.copy(cards = AllCards3.filter(_.winningPoints == 5).take(3))
      val updatedBoard = DefaultBoard
        .update(Yennefer, winningYennefer)
        .update(Triss, winningTriss)
        .copy(turnNumber = 4)

      val result = updatedBoard.computeWinners

      result shouldBe Seq(winningTriss.playerNumber)
    }

    it("should computeWinners returns multiple winners when they are ex-aequo") {
      // Same winning configuration for both player: 15 points and 3 cards
      val winningYennefer = Yennefer.copy(cards = AllCards.filter(_.winningPoints == 3).take(5))
      val winningTriss = Triss.copy(cards = AllCards.filter(_.winningPoints == 3).take(5))
      val updatedBoard = DefaultBoard
        .update(Yennefer, winningYennefer)
        .update(Triss, winningTriss)
        .copy(turnNumber = 4)

      val result = updatedBoard.computeWinners

      result shouldBe Seq(winningYennefer.playerNumber, winningTriss.playerNumber)
    }
  }

  describe("incTurn") {
    it("should increment turn number and the playerToPlay") {
      val result = DefaultBoard.incTurn

      result.turnNumber shouldBe 1
      result.playersToPlay shouldBe 1
    }
    it("should increment turn number and set playerToPlay to 0") {
      val result = DefaultBoard.copy(playersToPlay = 3).incTurn

      result.turnNumber shouldBe 1
      result.playersToPlay shouldBe 0
    }
  }

  describe("contains") {
    it("should be true when board contains gems") {
      val gems: Map[Gem, Int] = Map(Gold -> 5, Sapphire -> 7, Diamond -> 3)
      val result = DefaultBoard.contains(gems)
      result shouldBe true
    }
    it("should be false when board does not contains gems") {
      val gems: Map[Gem, Int] = Map(Gold -> 5, Sapphire -> 8, Diamond -> 3)
      val result = DefaultBoard.contains(gems)
      result shouldBe false
    }
  }

  describe("addCoins") {
    it("should add coins") {
      val emptyBoard = DefaultBoard.copy(coins = Seq())

      val gems: Map[Gem, Int] = Map(Gold -> 5, Sapphire -> 7, Diamond -> 3)
      val boardResult = emptyBoard.addCoins(gems)

      boardResult.coins shouldBe Seq(GemCount(Gold, 5), GemCount(Sapphire, 7), GemCount(Diamond, 3))
    }
  }

  describe("removeCoins") {
    it("should remove coins with Map") {
      val gems: Map[Gem, Int] = Map(Gold -> 5, Sapphire -> 7, Diamond -> 3)

      val boardResult = DefaultBoard.removeCoins(gems)

      boardResult.coins should have size 4
      boardResult.coins should contain (GemCount(Diamond, 4))
      boardResult.coins should contain (GemCount(Emerald, 7))
      boardResult.coins should contain (GemCount(Ruby, 7))
      boardResult.coins should contain (GemCount(Onyx, 7))
    }

    it("should remove coins with Seq") {
      val gems: Seq[Gem] = Seq(Gold, Diamond)

      val boardResult = DefaultBoard.removeCoins(gems)

      boardResult.coins should have size 6
      boardResult.coins should contain (GemCount(Gold, 4))
      boardResult.coins should contain (GemCount(Diamond, 6))
      boardResult.coins should contain (GemCount(Emerald, 7))
      boardResult.coins should contain (GemCount(Sapphire, 7))
      boardResult.coins should contain (GemCount(Ruby, 7))
      boardResult.coins should contain (GemCount(Onyx, 7))
    }
  }

  describe("currentPlayer") {
    it("should return currentPlayer") {
      val player = DefaultBoard.currentPlayer
      player shouldBe Geralt
    }

    it("should return currentPlayer after 2 turn") {
      val player = DefaultBoard.incTurn.incTurn.currentPlayer
      player shouldBe Yennefer
    }

    it("should return currentPlayer after 5 turn") {
      val player = DefaultBoard.incTurn.incTurn.incTurn.incTurn.incTurn.currentPlayer
      player shouldBe Ciri
    }
  }

  describe("update") {
    it("should update the given player") {
      val updatedPlayer = Geralt.copy(coins = Seq(GemCount(Sapphire, 1), GemCount(Onyx, 1), GemCount(Diamond, 1)))

      val boardResult = DefaultBoard.update(Geralt, updatedPlayer)

      boardResult.players should have size 4
      boardResult.players should contain (Yennefer)
      boardResult.players should contain (Triss)
      boardResult.players should contain (Ciri)
      boardResult.players should contain (updatedPlayer)
      boardResult.players shouldNot contain (Geralt)
      boardResult.players shouldBe Seq(updatedPlayer, Ciri, Yennefer, Triss) // Check ordering
    }
  }

  describe("removeDisplayedCard") {
    it("should update tier1 cards") {
      val cardToRemove = DefaultBoard.displayedCards1.head

      val result = DefaultBoard.removeDisplayedCard(cardToRemove)

      result.toOption.get.displayedCards1 should have size 4
      result.toOption.get.hiddenCards1 should not contain cardToRemove
    }

    it("should update tier2 cards") {
      val cardToRemove = DefaultBoard.displayedCards2.head

      val result = DefaultBoard.removeDisplayedCard(cardToRemove)

      result.toOption.get.displayedCards2 should have size 4
      result.toOption.get.hiddenCards2 should not contain cardToRemove
    }

    it("should update tier3 cards") {
      val cardToRemove = DefaultBoard.displayedCards2.head

      val result = DefaultBoard.removeDisplayedCard(cardToRemove)

      result.toOption.get.displayedCards3 should have size 4
      result.toOption.get.hiddenCards3 should not contain cardToRemove
    }

    it("should update even when there are no more cards to pickup") {
      val updatedBoard = DefaultBoard.copy(hiddenCards1 = Seq())
      val cardToRemove = updatedBoard.displayedCards1.head

      val result = updatedBoard.removeDisplayedCard(cardToRemove)

      result.toOption.get.displayedCards1 should have size 3
      result.toOption.get.hiddenCards1 should have size 0
    }

  }

}
