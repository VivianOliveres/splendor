package com.kensai.splendor.engine

import com.kensai.splendor.model.protobuf.model.Player

import java.util.UUID

object PlayerSamples {

  lazy val Geralt = Player(id = UUID.randomUUID().toString, name = "Geralt of Rivia", playerNumber = 0)
  lazy val Ciri = Player(id = UUID.randomUUID().toString, name = "Ciri of Cintra", playerNumber = 1)
  lazy val Yennefer = Player(id = UUID.randomUUID().toString, name = "Yennefer of Vengerberg", playerNumber = 2)
  lazy val Triss = Player(id = UUID.randomUUID().toString, name = "Triss Merigold", playerNumber = 4)

}
