package com.kensai.splendor.engine

import _root_.com.kensai.splendor.model._
import _root_.com.kensai.splendor.model.ModelHelpers._
import _root_.com.kensai.splendor.model.DataSamples._
import com.kensai.splendor.model.protobuf.model.Gem._
import com.kensai.splendor.model.protobuf.model._
import org.scalatest.Inside.inside
import org.scalatest.funspec.AnyFunSpec

class GameEngineSpec extends AnyFunSpec with ModelAsserts {

  private def createActions(playerNumber: Int, card: Card): Seq[PlayerAction] =
    Seq(
      PlayerAction(playerNumber = playerNumber, doNothing = Some(DoNothing())),
      PlayerAction(playerNumber = playerNumber, take3Gems = Some(Take3Gems(Gem.Onyx))),
      PlayerAction(playerNumber = playerNumber, take2Gems = Some(Take2Gems(Gem.Onyx))),
      PlayerAction(playerNumber = playerNumber, reserveCard = Some(ReserveCard(card))),
      PlayerAction(playerNumber = playerNumber, buyCard = Some(BuyCard(card)))
    )

  describe("play for unexpected player") {
    it("should return InvalidAction if another player send any action") {
      // Every player except player 0
      DefaultBoard.players.tail.foreach{ player =>
        // Every possible actions
        createActions(player.playerNumber, DefaultBoard.displayedCards1.head).foreach{ action =>
          val response = GameEngine.play(action, DefaultBoard)
          inside (response) { case Left(InvalidAction(number, _, _)) =>
            number shouldBe player.playerNumber
          }
        }
      }
    }
  }

  describe("play for DoNothing") {
    it("should return same board with playerNumber increased") {
      val action = PlayerAction(playerNumber = Geralt.playerNumber, doNothing = Some(DoNothing()))
      val maybeResponse = GameEngine.play(action, DefaultBoard)
      inside (maybeResponse) { case Right(GameResponse(Some(newTurn), None, _)) =>
        val expectedBoard = DefaultBoard.copy(playersToPlay = 1, turnNumber = 1)
        newTurn.board.shouldBeEq(expectedBoard)
      }
    }
  }

  describe("play for Take3Gems") {

    it("should return InvalidAction if player wants gold") {
      val action = PlayerAction(playerNumber = Geralt.playerNumber, take3Gems = Some(Take3Gems(Gold)))
      val maybeResponse = GameEngine.play(action, DefaultBoard)
      inside (maybeResponse) { case Left(InvalidAction(number, _, _)) =>
        number shouldBe Geralt.playerNumber
      }
    }

    it("should return InvalidAction if player wants at least 2 Gem of same color") {
      val action = PlayerAction(playerNumber = Geralt.playerNumber, take3Gems = Some(Take3Gems(Onyx, Some(Onyx), Some(Ruby))))
      val maybeResponse = GameEngine.play(action, DefaultBoard)
      inside (maybeResponse) { case Left(InvalidAction(number, _, _)) =>
        number shouldBe Geralt.playerNumber
      }
    }

    it("should return InvalidAction if there are not enough of this color in board") {
      val boardWithNotEnoughResources = DefaultBoard.copy(coins = Seq())
      val action = PlayerAction(playerNumber = Geralt.playerNumber, take3Gems = Some(Take3Gems(Onyx, Some(Diamond), Some(Ruby))))
      val maybeResponse = GameEngine.play(action, boardWithNotEnoughResources)
      inside (maybeResponse) { case Left(InvalidAction(number, _, _)) =>
        number shouldBe Geralt.playerNumber
      }
    }

    it("should work for 1 gem") {
      val action = PlayerAction(playerNumber = Geralt.playerNumber, take3Gems = Some(Take3Gems(Onyx)))
      val maybeResponse = GameEngine.play(action, DefaultBoard)

      val gems: Map[Gem, Int] = Map(Onyx -> 1)
      val expectEdCoins = DefaultBoard.coins.remove(gems)
      val expectedGeralt = Geralt.copy(coins = Geralt.coins.add(gems))
      val expectedPlayers = Seq(expectedGeralt, Ciri, Yennefer, Triss)
      val expectedBoard = DefaultBoard.copy(playersToPlay = 1, turnNumber = 1, coins = expectEdCoins, players = expectedPlayers)

      inside (maybeResponse) { case Right(GameResponse(Some(newTurn), None, _)) =>
        newTurn.board.shouldBeEq(expectedBoard)
      }
    }

    it("should work for 2 gem") {
      val action = PlayerAction(playerNumber = Geralt.playerNumber, take3Gems = Some(Take3Gems(Onyx, Some(Ruby))))
      val maybeResponse = GameEngine.play(action, DefaultBoard)

      val gems: Map[Gem, Int] = Map(Onyx -> 1, Ruby -> 1)
      val expectEdCoins = DefaultBoard.coins.remove(gems)
      val expectedGeralt = Geralt.copy(coins = Geralt.coins.add(gems))
      val expectedPlayers = Seq(expectedGeralt, Ciri, Yennefer, Triss)
      val expectedBoard = DefaultBoard.copy(playersToPlay = 1, turnNumber = 1, coins = expectEdCoins, players = expectedPlayers)

      inside (maybeResponse) { case Right(GameResponse(Some(newTurn), None, _)) =>
        newTurn.board.shouldBeEq(expectedBoard)
      }
    }

    it("should work for 3 gem") {
      val action = PlayerAction(playerNumber = Geralt.playerNumber, take3Gems = Some(Take3Gems(Onyx, Some(Ruby), Some(Diamond))))
      val maybeResponse = GameEngine.play(action, DefaultBoard)

      val gems: Map[Gem, Int] = Map(Onyx -> 1, Ruby -> 1, Diamond -> 1)
      val expectEdCoins = DefaultBoard.coins.remove(gems)
      val expectedGeralt = Geralt.copy(coins = Geralt.coins.add(gems))
      val expectedPlayers = Seq(expectedGeralt, Ciri, Yennefer, Triss)
      val expectedBoard = DefaultBoard.copy(playersToPlay = 1, turnNumber = 1, coins = expectEdCoins, players = expectedPlayers)

      inside (maybeResponse) { case Right(GameResponse(Some(newTurn), None, _)) =>
        newTurn.board.shouldBeEq(expectedBoard)
      }
    }
  }

  describe("play for Take2Gems") {

    it("should return InvalidAction if player wants gold") {
      val action = PlayerAction(playerNumber = Geralt.playerNumber, take2Gems = Some(Take2Gems(Gold)))
      val maybeResponse = GameEngine.play(action, DefaultBoard)
      inside (maybeResponse) { case Left(InvalidAction(number, _, _)) =>
        number shouldBe Geralt.playerNumber
      }
    }

    it("should return InvalidAction if there are not enough of this color in board") {
      val action = PlayerAction(playerNumber = Geralt.playerNumber, take2Gems = Some(Take2Gems(Sapphire)))
      val boardWithNotEnoughResources = DefaultBoard.copy(coins = Seq(GemCount(Sapphire, 5)))
      val maybeResponse = GameEngine.play(action, boardWithNotEnoughResources)
      inside (maybeResponse) { case Left(InvalidAction(number, _, _)) =>
        number shouldBe Geralt.playerNumber
      }
    }

    it("should work for every GemType (except Gold)") {
      Seq(Ruby,Sapphire,Emerald,Onyx,Diamond).foreach{ gem =>
        val action = PlayerAction(playerNumber = Geralt.playerNumber, take2Gems = Some(Take2Gems(gem)))
        val maybeResponse = GameEngine.play(action, DefaultBoard)

        val gems: Map[Gem, Int] = Map(gem -> 2)
        val expectEdCoins = DefaultBoard.coins.remove(gems)
        val expectedGeralt = Geralt.copy(coins = Geralt.coins.add(gems))
        val expectedPlayers = Seq(expectedGeralt, Ciri, Yennefer, Triss)
        val expectedBoard = DefaultBoard.copy(playersToPlay = 1, turnNumber = 1, coins = expectEdCoins, players = expectedPlayers)

        inside (maybeResponse) { case Right(GameResponse(Some(newTurn), None, _)) =>
          newTurn.board.shouldBeEq(expectedBoard)
        }
      }
    }
  }

  describe("play for ReserveCard") {

    it("should return InvalidAction if player has already 3 reserved cards") {
      val reservedCards = DefaultBoard.displayedCards3.take(3)
      val updatedPlayer = Geralt.copy(reservedCards = reservedCards)
      val updatedBoard = reservedCards.foldLeft(DefaultBoard){case (board, card) => board.removeDisplayedCard(card).toOption.get}
        .copy(players = Seq(updatedPlayer, Ciri, Yennefer, Triss))

      val action = PlayerAction(playerNumber = Geralt.playerNumber, reserveCard = Some(ReserveCard(updatedBoard.displayedCards1.head)))
      val maybeResponse = GameEngine.play(action, updatedBoard)

      inside (maybeResponse) { case Left(InvalidAction(number, _, _)) =>
        number shouldBe Geralt.playerNumber
      }
    }

    it("should work for cards lvl 1") {
      val cardToReserve = DefaultBoard.displayedCards1.head
      val action = PlayerAction(playerNumber = Geralt.playerNumber, reserveCard = Some(ReserveCard(cardToReserve)))

      val maybeResponse = GameEngine.play(action, DefaultBoard)

      inside (maybeResponse) { case Right(GameResponse(Some(newTurn), None, _)) =>
        val board = newTurn.board

        // Generic updates
        board.playersToPlay shouldBe 1
        board.turnNumber shouldBe 1

        // Coins have changed
        val gems: Map[Gem, Int] = Map(Gold -> 1)
        board.coins shouldBe DefaultBoard.coins.remove(gems)

        // Players updated
        val expectedGeralt = Geralt.copy(coins = Geralt.coins.add(gems), reservedCards = Seq(cardToReserve))
        board.players.head shouldBe expectedGeralt
        board.players(1) shouldBe Ciri
        board.players(2) shouldBe Yennefer
        board.players(3) shouldBe Triss

        // Displayed and hidden cards updated for level 1
        board.displayedCards1 should contain allElementsOf DefaultBoard.displayedCards1.tail
        board.displayedCards1 should have size 4
        board.hiddenCards1 should have size (DefaultBoard.hiddenCards1.size - 1L)

        // Other cards unchanged
        board.displayedCards2 shouldBe DefaultBoard.displayedCards2
        board.hiddenCards2 shouldBe DefaultBoard.hiddenCards2
        board.displayedCards3 shouldBe DefaultBoard.displayedCards3
        board.hiddenCards3 shouldBe DefaultBoard.hiddenCards3

        // Nobles unchanged
        board.nobles shouldBe DefaultBoard.nobles
      }
    }

    it("should work for cards lvl 2") {
      val cardToReserve = DefaultBoard.displayedCards2.head
      val action = PlayerAction(playerNumber = Geralt.playerNumber, reserveCard = Some(ReserveCard(cardToReserve)))

      val maybeResponse = GameEngine.play(action, DefaultBoard)

      inside (maybeResponse) { case Right(GameResponse(Some(newTurn), None, _)) =>
        val board = newTurn.board

        // Generic updates
        board.playersToPlay shouldBe 1
        board.turnNumber shouldBe 1

        // Coins have changed
        val gems: Map[Gem, Int] = Map(Gold -> 1)
        board.coins shouldBe DefaultBoard.coins.remove(gems)

        // Players updated
        val expectedGeralt = Geralt.copy(coins = Geralt.coins.add(gems), reservedCards = Seq(cardToReserve))
        board.players.head shouldBe expectedGeralt
        board.players(1) shouldBe Ciri
        board.players(2) shouldBe Yennefer
        board.players(3) shouldBe Triss

        // Displayed and hidden cards updated for level 2
        board.displayedCards2 should contain allElementsOf DefaultBoard.displayedCards2.tail
        board.displayedCards2 should have size 4
        board.hiddenCards2 should have size (DefaultBoard.hiddenCards2.size - 1L)

        // Other cards unchanged
        board.displayedCards1 shouldBe DefaultBoard.displayedCards1
        board.hiddenCards1 shouldBe DefaultBoard.hiddenCards1
        board.displayedCards3 shouldBe DefaultBoard.displayedCards3
        board.hiddenCards3 shouldBe DefaultBoard.hiddenCards3

        // Nobles unchanged
        board.nobles shouldBe DefaultBoard.nobles
      }
    }

    it("should work for cards lvl 3") {
      val cardToReserve = DefaultBoard.displayedCards3.head
      val action = PlayerAction(playerNumber = Geralt.playerNumber, reserveCard = Some(ReserveCard(cardToReserve)))

      val maybeResponse = GameEngine.play(action, DefaultBoard)

      inside (maybeResponse) { case Right(GameResponse(Some(newTurn), None, _)) =>
        val board = newTurn.board

        // Generic updates
        board.playersToPlay shouldBe 1
        board.turnNumber shouldBe 1

        // Coins have changed
        val gems: Map[Gem, Int] = Map(Gold -> 1)
        board.coins shouldBe DefaultBoard.coins.remove(gems)

        // Players updated
        val expectedGeralt = Geralt.copy(coins = Geralt.coins.add(gems), reservedCards = Seq(cardToReserve))
        board.players.head shouldBe expectedGeralt
        board.players(1) shouldBe Ciri
        board.players(2) shouldBe Yennefer
        board.players(3) shouldBe Triss

        // Displayed and hidden cards updated for level 2
        board.displayedCards3 should contain allElementsOf DefaultBoard.displayedCards3.tail
        board.displayedCards3 should have size 4
        board.hiddenCards3 should have size (DefaultBoard.hiddenCards3.size - 1L)

        // Other cards unchanged
        board.displayedCards1 shouldBe DefaultBoard.displayedCards1
        board.hiddenCards1 shouldBe DefaultBoard.hiddenCards1
        board.displayedCards2 shouldBe DefaultBoard.displayedCards2
        board.hiddenCards2 shouldBe DefaultBoard.hiddenCards2

        // Nobles unchanged
        board.nobles shouldBe DefaultBoard.nobles
      }
    }

    it("should work when there are not enough gold on board") {
      // No coins on board
      val updatedBoard = DefaultBoard.copy(coins = Seq())

      val cardToReserve = DefaultBoard.displayedCards2.head
      val action = PlayerAction(playerNumber = Geralt.playerNumber, reserveCard = Some(ReserveCard(cardToReserve)))

      val maybeResponse = GameEngine.play(action, updatedBoard)
      inside (maybeResponse) { case Right(GameResponse(Some(newTurn), None, _)) =>
        val board = newTurn.board

        // Generic updates
        board.playersToPlay shouldBe 1
        board.turnNumber shouldBe 1

        // Coins have not changed
        board.coins shouldBe updatedBoard.coins
        board.coins shouldBe empty

        // Players updated with no coins
        val expectedGeralt = Geralt.copy(reservedCards = Seq(cardToReserve), coins = Seq())
        board.players.head shouldBe expectedGeralt
      }
    }

    it("should work when player has already 10 coins") {
      // player has 10 coins
      val playerCoins = Seq(GemCount(Onyx, 3), GemCount(Diamond, 3), GemCount(Ruby, 3), GemCount(Sapphire, 1))
      val updatedPlayer = Geralt.copy(coins = playerCoins)
      val updatedBoard = DefaultBoard.copy(players = Seq(updatedPlayer, Ciri, Yennefer, Triss))

      val cardToReserve = DefaultBoard.displayedCards1.head
      val action = PlayerAction(playerNumber = Geralt.playerNumber, reserveCard = Some(ReserveCard(cardToReserve)))

      val maybeResponse = GameEngine.play(action, updatedBoard)
      inside (maybeResponse) { case Right(GameResponse(Some(newTurn), None, _)) =>
        val board = newTurn.board

        // Generic updates
        board.playersToPlay shouldBe 1
        board.turnNumber shouldBe 1

        // Coins have not changed
        board.coins shouldBe updatedBoard.coins

        // Players updated with no new coins
        val expectedGeralt = updatedPlayer.copy(reservedCards = Seq(cardToReserve), coins = playerCoins)
        board.players.head shouldBe expectedGeralt
      }
    }
  }

  describe("play for BuyCard") {

    it("should fail if card is not displayed (ie does not exist)") {
      // invalid card to reserve
      val cardToBuy = DefaultBoard.hiddenCards1.head

      val action = PlayerAction(playerNumber = Geralt.playerNumber, buyCard = Some(BuyCard(cardToBuy)))
      val maybeResponse = GameEngine.play(action, DefaultBoard)

      inside (maybeResponse) { case Left(InvalidAction(number, _, _)) =>
        number shouldBe Geralt.playerNumber
      }
    }

    it("should fail if player has not enough coins") {
      val cardToBuy = DefaultBoard.displayedCards1.head

      val action = PlayerAction(playerNumber = Geralt.playerNumber, buyCard = Some(BuyCard(cardToBuy)))
      val maybeResponse = GameEngine.play(action, DefaultBoard)

      inside (maybeResponse) { case Left(InvalidAction(number, _, _)) =>
        number shouldBe Geralt.playerNumber
      }
    }

    it("should work for card level 1") {
      val cardToBuy = DefaultBoard.displayedCards1.head
      val updatedPlayer = Geralt.copy(coins = cardToBuy.costs)
      val updatedBoard = DefaultBoard
        .removeCoins(cardToBuy.costs.toEnumMap)
        .copy(players = Seq(updatedPlayer, Ciri, Yennefer, Triss))

      val action = PlayerAction(playerNumber = Geralt.playerNumber, buyCard = Some(BuyCard(cardToBuy)))
      val maybeResponse = GameEngine.play(action, updatedBoard)

      inside (maybeResponse) { case Right(GameResponse(Some(newTurn), None, _)) =>
        val board = newTurn.board

        // Generic updates
        board.playersToPlay shouldBe 1
        board.turnNumber shouldBe 1

        // Coins have increased
        board.coins should contain theSameElementsAs DefaultBoard.coins

        // Players updated with no more coins
        val expectedGeralt = updatedPlayer.copy(cards = Seq(cardToBuy), coins = Geralt.coins)
        board.players.head shouldBe expectedGeralt
      }
    }

    it("should work for card level 2") {
      val cardToBuy = DefaultBoard.displayedCards2.head
      val updatedPlayer = Geralt.copy(coins = cardToBuy.costs)
      val updatedBoard = DefaultBoard
        .removeCoins(cardToBuy.costs.toEnumMap)
        .copy(players = Seq(updatedPlayer, Ciri, Yennefer, Triss))

      val action = PlayerAction(playerNumber = Geralt.playerNumber, buyCard = Some(BuyCard(cardToBuy)))
      val maybeResponse = GameEngine.play(action, updatedBoard)

      inside (maybeResponse) { case Right(GameResponse(Some(newTurn), None, _)) =>
        val board = newTurn.board

        // Generic updates
        board.playersToPlay shouldBe 1
        board.turnNumber shouldBe 1

        // Coins have increased
        board.coins should contain theSameElementsAs DefaultBoard.coins

        // Players updated with no more coins
        val expectedGeralt = updatedPlayer.copy(cards = Seq(cardToBuy), coins = Geralt.coins)
        board.players.head shouldBe expectedGeralt
      }
    }

    it("should work for card level 3") {
      val cardToBuy = DefaultBoard.displayedCards3.head
      val updatedPlayer = Geralt.copy(coins = cardToBuy.costs)
      val updatedBoard = DefaultBoard
        .removeCoins(cardToBuy.costs.toEnumMap)
        .copy(players = Seq(updatedPlayer, Ciri, Yennefer, Triss))

      val action = PlayerAction(playerNumber = Geralt.playerNumber, buyCard = Some(BuyCard(cardToBuy)))
      val maybeResponse = GameEngine.play(action, updatedBoard)

      inside (maybeResponse) { case Right(GameResponse(Some(newTurn), None, _)) =>
        val board = newTurn.board

        // Generic updates
        board.playersToPlay shouldBe 1
        board.turnNumber shouldBe 1

        // Coins have increased
        board.coins should contain theSameElementsAs DefaultBoard.coins

        // Players updated with no more coins
        val expectedGeralt = updatedPlayer.copy(cards = Seq(cardToBuy), coins = Geralt.coins)
        board.players.head shouldBe expectedGeralt
      }
    }

    it("should work for reserved card") {
      val (cardReserved, hiddenCards) = (DefaultBoard.hiddenCards1.head, DefaultBoard.hiddenCards1.tail)
      val updatedPlayer = Geralt.copy(coins = cardReserved.costs, reservedCards = Seq(cardReserved))
      val updatedBoard = DefaultBoard
        .removeCoins(cardReserved.costs.toEnumMap)
        .copy(players = Seq(updatedPlayer, Ciri, Yennefer, Triss), hiddenCards1 = hiddenCards)

      val action = PlayerAction(playerNumber = Geralt.playerNumber, buyCard = Some(BuyCard(cardReserved)))
      val maybeResponse = GameEngine.play(action, updatedBoard)

      inside (maybeResponse) { case Right(GameResponse(Some(newTurn), None, _)) =>
        val board = newTurn.board

        // Generic updates
        board.playersToPlay shouldBe 1
        board.turnNumber shouldBe 1

        // Coins have increased
        board.coins should contain theSameElementsAs DefaultBoard.coins

        // Players updated with no more coins
        val expectedGeralt = Geralt.copy(cards = Seq(cardReserved))
        board.players.head.shouldBeEq(expectedGeralt)
      }
    }
  }
}
