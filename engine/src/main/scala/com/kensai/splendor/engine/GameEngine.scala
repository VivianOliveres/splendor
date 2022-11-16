package com.kensai.splendor.engine

import com.kensai.splendor.model.protobuf.model.Gem.Gold
import com.kensai.splendor.model.protobuf.model._
import com.kensai.splendor.model.ModelHelpers._

object GameEngine {

  def play(msg: PlayerAction, board: Board): Either[InvalidAction, GameResponse] = {

    if (msg.playerNumber != board.playersToPlay) {
      Left(InvalidAction(msg.playerNumber, s"Invalid player to play [${msg.playerNumber}] - Waiting player [${board.playersToPlay}]"))
    } else {
      val maybeNewBoard = msg match {
        case PlayerAction(_, Some(_), _, _, _, _, _) => Right(board)
        case PlayerAction(_, _, Some(take3Gems), _, _, _, _) => doExecute(board, take3Gems)
        case PlayerAction(_, _, _, Some(take2Gems), _, _, _) => doExecute(board, take2Gems)
        case PlayerAction(_, _, _, _, Some(reserveCard), _, _) => doExecute(board, reserveCard)
        case PlayerAction(_, _, _, _, _, Some(buyCard), _) => doExecute(board, buyCard)
        case _ => Left(InvalidAction(msg.playerNumber, "No action was sent"))
      }

      maybeNewBoard
        .map(_.incTurn)
        .map{ newBoard =>
        val winners = newBoard.computeWinners
        if (winners.isEmpty)
          GameResponse(newTurn = Some(NewTurn(newBoard)))
        else
          GameResponse(playerWon = Some(PlayerWon(winners, newBoard)))
      }
    }
  }

  private def doExecute(board: Board, action: BuyCard): Either[InvalidAction, Board] = {
    val gemsToRemove = action.card.costs.toEnumMap
    val newPlayer = board.currentPlayer
      .removeCoins(gemsToRemove)
      .addCard(action.card)

    // TODO: use bought cards from the player in the count (and not only coins)
    // TODO: implement gold mechanic

    if (newPlayer.coins.exists(_.count < 0))
      Left(InvalidAction(board.playersToPlay, s"Player can not afford to buy this card"))
    else if (newPlayer.reservedCards.contains(action.card)) {
      val updatedPlayer = newPlayer.copy(reservedCards = newPlayer.reservedCards.filterNot(_ == action.card))
      Right(board.addCoins(gemsToRemove).update(board.currentPlayer, updatedPlayer))
    } else if (board.containCard(action.card)) {
      board.addCoins(gemsToRemove)
        .update(board.currentPlayer, newPlayer)
        .removeDisplayedCard(action.card)
    } else {
      Left(InvalidAction(board.playersToPlay, s"Card does not exist ${action.card}"))
    }
  }

  private def doExecute(board: Board, action: Take2Gems): Either[InvalidAction, Board] = {
    // Check predicates (not gold and min 6 coins in board)
    val maybeBoard = checkTake2GemsPredicate(board, action)

    val coins = Map(action.gem -> 2)
    val newPlayer = board.currentPlayer.addCoins(coins)

    maybeBoard
      .map(tmpBoard => tmpBoard.removeCoins(coins))
      .map(_.update(board.currentPlayer, newPlayer))
  }

  private def checkTake2GemsPredicate(board: Board, action: Take2Gems): Either[InvalidAction, Board] =
    if (action.gem == Gold)
      Left(InvalidAction(board.playersToPlay, s"Forbidden to take 2 gold"))
    else if (!board.contains(Map(action.gem -> 6)))
      Left(InvalidAction(board.playersToPlay, s"Board does not contains enough coins of type [${action.gem}]"))
    else
      Right(board)

  private def doExecute(board: Board, action: Take3Gems): Either[InvalidAction, Board] = {
    // Check predicates
    val maybeBoard = checkTake3GemsPredicates(board, action)

    // Gems to remove
    val gems = Seq(action.gem1) ++ action.gem2 ++ action.gem3

    // Inc gems for player
    val newPlayer = board.currentPlayer.addCoins(gems)

    // Update board
    maybeBoard
      .map(_.removeCoins(gems))
      .map(b => b.update(b.currentPlayer, newPlayer))
  }

  private def checkTake3GemsPredicates(board: Board, action: Take3Gems): Either[InvalidAction, Board] = {
    val gems: Seq[Gem] = Seq(action.gem1) ++ action.gem2 ++ action.gem3
    if (gems.size != gems.distinct.size)
      Left(InvalidAction(board.playersToPlay, s"Gem must be different: $action"))
    else if (gems.contains(Gold))
      Left(InvalidAction(board.playersToPlay, s"Gem must be Gold: $action"))
    else if (!board.contains(gems))
      Left(InvalidAction(board.playersToPlay, s"Not enough gems in board: $action"))
    else
      Right(board)
  }

  private def doExecute(board: Board, action: ReserveCard): Either[InvalidAction, Board] = {
    val player = board.players(board.playersToPlay)
    // Check: player does not have 3 reserved cards
    if (player.reservedCards.size >= 3)
      Left(InvalidAction(board.playersToPlay, s"Player[${board.playersToPlay}] cannot reserve a card because he has reserved too much cards"))
    else {
      // if gold in bank and player has less than 10 coins => get a gold coin
      val (newPlayerCoins, newBoardCoins) = if (player.coins.map(_.count).sum < 10 && board.coins.find(_.gem == Gold).map(_.count).getOrElse(0) > 0){
        val newPlayerCoins = player.coins.add(Map(Gold -> 1))
        val newBoardCoins = board.coins.remove(Map(Gold -> 1))
        (newPlayerCoins, newBoardCoins)
      } else
        (player.coins, board.coins) // Unchanged

      // remove card from board and put it into reserved cards of player
      val reservedCard = action.card
      val newPlayer = player.addReservedCards(reservedCard).copy(coins = newPlayerCoins)

      // return updated board
      board
        .removeDisplayedCard(reservedCard)
        .map(_.copy(coins = newBoardCoins))
        .map(_.update(player, newPlayer))
    }
  }

}
