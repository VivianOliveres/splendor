package com.kensai.splendor.model

import com.kensai.splendor.model.protobuf.model.Gem._
import com.kensai.splendor.model.protobuf.model._

import java.io._

object DataSamples {

  lazy val Geralt: Player = Player(
    id = "c63d81b3-5456-4713-b1bd-65f372ebb1d7",
    name = "Geralt of Rivia",
    playerNumber = 0
  )
  lazy val Ciri: Player = Player(
    id = "427ab270-0071-44bd-a23e-2f73d550ea90",
    name = "Ciri of Cintra",
    playerNumber = 1
  )
  lazy val Yennefer: Player = Player(
    id = "310c4fa2-192e-470b-b88a-81b4a84cf1d9",
    name = "Yennefer of Vengerberg",
    playerNumber = 2
  )
  lazy val Triss: Player = Player(
    id = "2230d890-bd3f-4d97-a324-a01a6204a56e",
    name = "Triss Merigold",
    playerNumber = 4
  )

  lazy val Players: Seq[Player] = Seq(Geralt, Ciri, Yennefer, Triss)

  lazy val DefaultCoins: Seq[GemCount] = Seq(GemCount(Ruby, 7), GemCount(Gold, 5), GemCount(Emerald, 7), GemCount(Sapphire, 7), GemCount(Onyx, 7), GemCount(Diamond, 7))

  lazy val DefaultNobles: Seq[Noble] = Seq(
    Noble("Henry VIII",List(GemCount(Ruby,4), GemCount(Onyx,4)),3), 
    Noble("Suleiman the Magnificent",List(GemCount(Sapphire,4), GemCount(Emerald,4)),3),
    Noble("Catherine of Medici",List(GemCount(Ruby,3), GemCount(Sapphire,3), GemCount(Emerald,3)),3),
    Noble("Charles V of Holy Roman Empire",List(GemCount(Ruby,3), GemCount(Onyx,3), GemCount(Diamond, 3)),3)
  )

  private lazy val boardFile = new File(getClass.getResource("/board.proto.bin").getPath)
  lazy val DefaultBoard: Board = Board.parseFrom(new BufferedInputStream(new FileInputStream(boardFile)))

  lazy val AllCards1: Seq[Card] = DefaultBoard.displayedCards1 ++ DefaultBoard.hiddenCards1
  lazy val AllCards2: Seq[Card] = DefaultBoard.displayedCards2 ++ DefaultBoard.hiddenCards2
  lazy val AllCards3: Seq[Card] = DefaultBoard.displayedCards3 ++ DefaultBoard.hiddenCards3
  lazy val AllCards: Seq[Card] = AllCards1 ++ AllCards2 ++ AllCards3
}
