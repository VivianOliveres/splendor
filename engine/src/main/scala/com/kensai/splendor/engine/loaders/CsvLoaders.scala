package com.kensai.splendor.engine.loaders

import com.kensai.splendor.engine.model.Model._
import os.Path

object CsvLoaders {

  def loadNobles(
      path: Path =
        os.pwd / "engine" / "src" / "main" / "resources" / "nobles.csv"
  ): Seq[Noble] = {
    val fileContent = os.read(path)
    fileContent
      .split("\n")
      .map(_.trim)
      .filterNot(_.isEmpty)
      .toSeq
      .tail // skip header
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

  def parseGem(value: String, gem: Gem): Option[(Gem, Int)] =
    value.toIntOption.filterNot(_ == 0).map(value => (gem, value))
}
