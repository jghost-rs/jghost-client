package org.jghost

import javafx.application.{Application, Platform}
import javafx.fxml.FXMLLoader
import javafx.scene.Scene
import javafx.scene.layout.AnchorPane
import javafx.stage.Stage

import org.jghost.util.{ExceptionHandler, JGhostClientLock}

final class JGhostClient extends Application {

  private val jGhostLock = new JGhostClientLock

  override def init = {
    //  if (!jGhostLock.lock) {
    //    System.exit(0)
    //  }
    Runtime.getRuntime.addShutdownHook(new Thread {
      override def run = {
        jGhostLock.unlock
      }
    })
  }

  override def start(primaryStage: Stage) = {
    Platform.setImplicitExit(true)
    Thread.currentThread.setUncaughtExceptionHandler(ExceptionHandler)

    val root: AnchorPane = FXMLLoader.load(getClass.getClassLoader.getResource("ConnectWindow.fxml"))

    primaryStage.setTitle(s"${JGhostClient.title} - Authentication")
    primaryStage.setResizable(false)
    primaryStage.setScene(new Scene(root))
    primaryStage.show
  }

  override def stop = {
    jGhostLock.unlock
    System.exit(0)
  }
}

object JGhostClient {
  val Version = "0.6.0"
  def title = s"jGhost v$Version"

  def main(args: Array[String]) = Application.launch(classOf[JGhostClient], args: _*)
}
