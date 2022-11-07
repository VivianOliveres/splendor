package com.kensai.splendor.engine.loaders

import com.kensai.splendor.engine.model.Model._
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
        ).flatten.toMap
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

  private def parseGem(value: String, gem: Gem): Option[(Gem, Int)] =
    value.toIntOption.filterNot(_ == 0).map(value => (gem, value))

  def loadCards(path: Path =
                os.pwd / "engine" / "src" / "main" / "resources" / "cards.csv"): Seq[Card] = {
    readTailFile(path)
      .map { line =>
        try {
          val split = line.split(";")
          val tier = split.head.toInt
          val victoryPoints = split(1).toInt
          val gem = Gem(split(2))
          val costs = split(3).grouped(2).map { subValue => (Gem(subValue.tail.head) -> subValue.head.toString.toInt) }.toMap
          Card(
            tierList = tier,
            winningPoints = victoryPoints,
            cost = costs,
            valueType = gem
          )
        } catch {
          case e: RuntimeException => throw new RuntimeException(s"Error when parsing line [$line]", e)
        }
      }
  }

}
