package com.kensai.splendor.model

import com.kensai.splendor.model.protobuf.model.{Board, Player}
import org.scalatest.matchers.should.Matchers

trait ModelAsserts extends Matchers {

  implicit class BoardAssert(board: Board) {
    def shouldBeEq(otherBoard: Board): Board = {
      board.gameId shouldBe otherBoard.gameId
      board.turnNumber shouldBe otherBoard.turnNumber
      board.playersToPlay shouldBe otherBoard.playersToPlay

      board.coins should contain theSameElementsAs otherBoard.coins

      board.displayedCards1 should contain theSameElementsAs otherBoard.displayedCards1
      board.displayedCards2 should contain theSameElementsAs otherBoard.displayedCards2
      board.displayedCards3 should contain theSameElementsAs otherBoard.displayedCards3

      board.hiddenCards1 should contain theSameElementsAs otherBoard.hiddenCards1
      board.hiddenCards2 should contain theSameElementsAs otherBoard.hiddenCards2
      board.hiddenCards3 should contain theSameElementsAs otherBoard.hiddenCards3

      board.nobles should contain theSameElementsAs otherBoard.nobles

      board.players.zip(otherBoard.players).foreach {
        case (player, otherPlayer) =>
          player.shouldBeEq(otherPlayer)
      }

      board
    }
  }

  implicit class PlayerAssert(player: Player) {

    def shouldBeEq(otherPlayer: Player): Player = {
      player.id shouldBe otherPlayer.id
      player.playerNumber shouldBe otherPlayer.playerNumber
      player.name shouldBe otherPlayer.name
      player.nobles should contain theSameElementsAs otherPlayer.nobles
      player.cards should contain theSameElementsAs otherPlayer.cards
      player.reservedCards should contain theSameElementsAs otherPlayer.reservedCards
      player
    }
  }

}
