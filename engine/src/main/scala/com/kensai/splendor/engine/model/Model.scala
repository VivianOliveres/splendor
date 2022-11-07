package com.kensai.splendor.engine.model


object Model {

//  sealed trait Gem
//  object Gem {
//    def apply(value: String): Gem = value match {
//      case "ruby"     => Ruby
//      case "sapphire" => Sapphire
//      case "emerald"  => Emerald
//      case "onyx"     => Onyx
//      case "diamond"  => Diamond
//      case "gold"     => Gold
//      case _          => throw new IllegalArgumentException(s"Invalid gem name [$value]")
//    }
//    def apply(c: Char): Gem = c match {
//      case 'r' => Ruby
//      case 's' => Sapphire
//      case 'e' => Emerald
//      case 'o' => Onyx
//      case 'd' => Diamond
//      case 'g' => Gold
//      case _   => throw new IllegalArgumentException(s"Invalid gem char [$c]")
//    }
//  }
//  case object Ruby extends Gem
//  case object Sapphire extends Gem
//  case object Emerald extends Gem
//  case object Onyx extends Gem
//  case object Diamond extends Gem
//  case object Gold extends Gem
//
//  case class Noble(name: String, costs: Map[Gem, Int], winningPoints: Int)
//
//  case class Card(
//      tierList: Int,
//      winningPoints: Int,
//      costs: Map[Gem, Int],
//      valueType: Gem
//  )
//
//  case class Player(
//      id: String,
//      name: String,
//      playerNumber: Int,
//      score: Int = 0,
//      nobles: Seq[Noble] = Seq(),
//      cards: Seq[Card] = Seq(),
//      reservedCards: Seq[Card] = Seq(),
//      coins: Map[Gem, Int] = Map()
//  )
//
//  case class Board(
//      gameId: Long,
//      playersToPlay: Int = 1,
//      availableCoins: Map[Gem, Int],
//      displayedCards1: Seq[Card],
//      availableCards1: Seq[Card],
//      displayedCards2: Seq[Card],
//      availableCards2: Seq[Card],
//      displayedCards3: Seq[Card],
//      availableCards3: Seq[Card],
//      availableNobles: Seq[Noble],
//      players: Seq[Player]
//  )
}
