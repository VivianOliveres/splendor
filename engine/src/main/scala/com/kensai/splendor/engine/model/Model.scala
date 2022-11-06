package com.kensai.splendor.engine.model

object Model {

  sealed trait Gem
  case object Ruby extends Gem
  case object Sapphire extends Gem
  case object Emerald extends Gem
  case object Onyx extends Gem
  case object Diamond extends Gem

  case class Noble(name: String, cost: Map[Gem, Int], winningPoints: Int)
}
