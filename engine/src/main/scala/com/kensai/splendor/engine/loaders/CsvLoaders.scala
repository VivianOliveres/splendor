package com.kensai.splendor.engine.loaders

import com.kensai.splendor.model.protobuf.model.Gem._
import com.kensai.splendor.model.protobuf.model._
import os.Path

object CsvLoaders {

  def loadNobles(
      path: Path =
        os.pwd / "engine" / "src" / "main" / "resources" / "nobles.csv"
  ): Seq[Noble] = {
    readTailFile(path)
      .map { line =>
        val split = line.split(";")
        val costs = Seq(
          parseGem(split(1), Ruby),
          parseGem(split(2), Sapphire),
          parseGem(split(3), Emerald),
          parseGem(split(4), Onyx),
          parseGem(split(5), Diamond)
        ).flatten
        val victoryPoints = split(6).toInt
        Noble(
          name = split.head,
          costs,
          victoryPoints
        )
      }
  }

  private def readTailFile(path: Path): Seq[String] =
    os.read(path)
      .split("\n")
      .map(_.trim)
      .filterNot(_.isEmpty)
      .toSeq
      .tail // skip header

  private def parseGem(value: String, gem: Gem): Option[GemCount] =
    value.toIntOption.filterNot(_ == 0).map(value => GemCount(gem, value))

  private def toGem(value: String): Gem = value match {
    case "ruby"     => Ruby
    case "sapphire" => Sapphire
    case "emerald"  => Emerald
    case "onyx"     => Onyx
    case "diamond"  => Diamond
    case "gold"     => Gold
    case _          => throw new IllegalArgumentException(s"Invalid gem name [$value]")
  }
  private def toGem(c: Char): Gem = c match {
    case 'r' => Ruby
    case 's' => Sapphire
    case 'e' => Emerald
    case 'o' => Onyx
    case 'd' => Diamond
    case 'g' => Gold
    case _   => throw new IllegalArgumentException(s"Invalid gem char [$c]")
  }

  def loadCards(
      path: Path =
        os.pwd / "engine" / "src" / "main" / "resources" / "cards.csv"
  ): Seq[Card] = {
    readTailFile(path)
      .map { line =>
        try {
          val split = line.split(";")
          val tier = split.head.toInt
          val victoryPoints = split(1).toInt
          val gem = toGem(split(2))
          val costs = split(3)
            .grouped(2)
            .map { subValue =>
              GemCount(toGem(subValue.tail.head), subValue.head.toString.toInt)
            }.toSeq
          Card(
            tierList = tier,
            winningPoints = victoryPoints,
            costs = costs,
            valueType = gem
          )
        } catch {
          case e: RuntimeException =>
            throw new RuntimeException(s"Error when parsing line [$line]", e)
        }
      }
  }

  def loadCoins(
      path: Path =
        os.pwd / "engine" / "src" / "main" / "resources" / "coins.csv"
  ): Map[Gem, Int] = {
    readTailFile(path).map { line =>
      val split = line.split(";")
      (toGem(split.head), split(1).toInt)
    }.toMap
  }

}
