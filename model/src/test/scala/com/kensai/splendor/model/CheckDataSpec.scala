package com.kensai.splendor.model

import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.should.Matchers

class CheckDataSpec extends AnyFunSpec with Matchers {

  describe("Test data") {
    it("should load valid data") {
      DataSamples.DefaultBoard.players shouldBe DataSamples.Players
      DataSamples.DefaultBoard.nobles shouldBe DataSamples.DefaultNobles
      DataSamples.DefaultBoard.coins shouldBe DataSamples.DefaultCoins
    }
  }
}
