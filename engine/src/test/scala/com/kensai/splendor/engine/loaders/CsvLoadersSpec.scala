package com.kensai.splendor.engine.loaders

import com.kensai.splendor.engine.model.Model.Noble
import org.scalatest.Inside.inside
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

class CsvLoadersSpec extends AnyFlatSpec with Matchers {

  "Noble loader" should "retrieve all nobles" in {
    val nobles = CsvLoaders.loadNobles()
    nobles should have size 10
    nobles.foreach { noble =>
      inside(noble) { case Noble(_, costs, winningPoints) =>
        winningPoints shouldBe 3
        costs.values.foreach(cost => cost should be <= 4)
        costs.values.foreach(cost => cost should be >= 3)
      }
    }
  }
}
