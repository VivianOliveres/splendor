package com.kensai.splendor.engine.model

object Model {

  sealed trait Gem
  object Gem {
    def apply(value: String): Gem = value match {
      case "ruby" => Ruby
      case "sapphire" => Sapphire
      case "emerald" => Emerald
      case "onyx" => Onyx
      case "diamond" => Diamond
      case _ => throw new IllegalArgumentException(s"Invalid gem name [$value]")
    }
    def apply(c: Char): Gem = c match {
      case 'r' => Ruby
      case 's' => Sapphire
      case 'e' => Emerald
      case 'o' => Onyx
      case 'd' => Diamond
      case _ => throw new IllegalArgumentException(s"Invalid gem char [$c]")
    }
  }
  case object Ruby extends Gem
  case object Sapphire extends Gem
  case object Emerald extends Gem
  case object Onyx extends Gem
  case object Diamond extends Gem

  case class Noble(name: String, cost: Map[Gem, Int], winningPoints: Int)

  case class Card(tierList: Int, winningPoints: Int, cost: Map[Gem, Int], valueType: Gem)
}
