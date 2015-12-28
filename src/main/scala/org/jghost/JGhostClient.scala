package org.jghost

import _root_.javafx.application.{Application, Platform}
import _root_.javafx.stage.Stage
import java.nio.file.{Files, Paths}

import io.netty.util.AttributeKey
import org.jghost.javafx.JGhostConnectWindow
import org.jghost.net.Client
import org.jghost.util.JGhostExceptionHandler

import scala.collection.mutable
import scala.reflect.io.File

object JGhostClient {
  val Version = "0.9.0"
  val Title = s"jGhost v$Version"
  val ResourcePath = System.getProperty("user.home") + "\\jghost_resources\\"
  val Properties: mutable.HashMap[String, String] = mutable.HashMap.empty
  val ExceptionHandler = new JGhostExceptionHandler
  val ClientKey: AttributeKey[Client] = AttributeKey.valueOf("client.KEY")

  def main(args: Array[String]) = {
    mkDirs
    readProperties

    Application.launch(classOf[JGhostClient], args: _*)
  }

  private def init = {
    // TODO: Locking, exit if lock already active
  }

  private def start(primaryStage: Stage) = {
    Thread.currentThread.setUncaughtExceptionHandler(ExceptionHandler)
    Platform.setImplicitExit(true)

    val connectWindow = new JGhostConnectWindow
    connectWindow.displayWindow
  }

  private def stop = {
    // TODO: Unlocking, remove lock before exiting. A shutdown hook might be needed as well (for properties too).
    writeProperties
    System.exit(0)
  }

  private def readProperties: Unit = {
    val file = File(ResourcePath + "jghost_properties.txt")

    if (!file.exists || file.slurp.isEmpty) {
      return
    }

    val fileContents = for (it <- file.slurp.split("\n")) yield it.split(":")

    for (it <- fileContents) {
      Properties(it(0)) = it(1)
    }
  }

  private def writeProperties = {
    val file = File(ResourcePath + "jghost_properties.txt")
    val tokens = (for ((k, v) <- JGhostClient.Properties) yield s"$k:$v\n").mkString

    if (!file.exists) {
      file.createFile()
    }

    file.writeAll(tokens)
  }

  private def mkDirs = {
    val toPath = Paths.get(ResourcePath)

    if (!Files.exists(toPath)) {
      Files.createDirectory(toPath)
    }
  }
}

class JGhostClient extends Application {
  override def init = JGhostClient.init
  override def start(primaryStage: Stage) = JGhostClient.start(primaryStage)
  override def stop = JGhostClient.stop
}