package com.kensai.splendor.engine

import com.kensai.splendor.engine.PlayerSamples._
import org.scalatest.matchers.should.Matchers
import com.kensai.splendor.model.protobuf.model._
import com.kensai.splendor.model.protobuf.model.Gem._
import org.scalatest.funspec.AnyFunSpec


class BoardInitSpec extends AnyFunSpec with Matchers {

  describe("init") {
    it("should be valid") {
      val defaultBoard = BoardInit.board(Seq(Geralt, Ciri, Yennefer, Triss))

      defaultBoard.players should have size 4
      defaultBoard.players shouldBe Seq(Geralt, Ciri, Yennefer, Triss)

      defaultBoard.coins should have size 6
      defaultBoard.coins should contain (GemCount(Gold, 5))
      defaultBoard.coins should contain (GemCount(Diamond, 7))
      defaultBoard.coins should contain (GemCount(Emerald, 7))
      defaultBoard.coins should contain (GemCount(Ruby, 7))
      defaultBoard.coins should contain (GemCount(Sapphire, 7))
      defaultBoard.coins should contain (GemCount(Onyx, 7))

      defaultBoard.displayedCards1 should have size 4
      defaultBoard.hiddenCards1 should have size 36

      defaultBoard.displayedCards2 should have size 4
      defaultBoard.hiddenCards2 should have size 26

      defaultBoard.displayedCards3 should have size 4
      defaultBoard.hiddenCards3 should have size 16

      defaultBoard.nobles should have size 4
    }
  }
}
