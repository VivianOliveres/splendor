package com.kensai.splendor.bots

import com.kensai.splendor.model.protobuf.model._

trait Bot {

  def playerNumber: Int

  def init(board: Board): Unit = {}

  def gameEnded(state: PlayerWon): Unit = {}

  def newTurn(turn: NewTurn): Option[PlayerAction] = {
    if (playerNumber == turn.board.playersToPlay)
      Some(play(turn.board))
    else
      None
  }

  def play(board: Board): PlayerAction

}
