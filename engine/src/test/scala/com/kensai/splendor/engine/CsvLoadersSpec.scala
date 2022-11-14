package com.kensai.splendor.engine

import com.kensai.splendor.model.protobuf.model.Gem._
import com.kensai.splendor.model.protobuf.model._
import org.scalatest.Inside.inside
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

class CsvLoadersSpec extends AnyFlatSpec with Matchers {

  "Noble loader" should "retrieve all nobles" in {
    val nobles = CsvLoaders.loadNobles()
    nobles should have size 10
    nobles.foreach { noble =>
      inside(noble) { case Noble(_, costs, winningPoints, _) =>
        winningPoints shouldBe 3
        costs.foreach(cost => cost.count should be <= 4)
        costs.foreach(cost => cost.count should be >= 3)
      }
    }
  }

  "Card loader" should "retrieve all cards" in {
    val cards = CsvLoaders.loadCards()
    cards should have size 90
  }

  "Coins loader" should "retrieve all coins" in {
    val coins = CsvLoaders.loadCoins()
    coins should have size 6
    coins should contain (Gold -> 5)
  }
}
