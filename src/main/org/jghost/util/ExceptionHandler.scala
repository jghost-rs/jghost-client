package org.jghost.util

import java.lang.Thread.UncaughtExceptionHandler
import java.util.concurrent.atomic.AtomicInteger
import javafx.application.Platform
import javafx.event.EventHandler
import javafx.fxml.FXMLLoader
import javafx.scene.Scene
import javafx.scene.control.TextArea
import javafx.scene.layout.AnchorPane
import javafx.stage.{Modality, Stage, WindowEvent}

import com.google.common.base.Throwables
import org.jghost.JGhostClient

object ExceptionHandler extends UncaughtExceptionHandler {

  val WindowCount = new AtomicInteger

  override def uncaughtException(t: Thread, e: Throwable) {
    e.printStackTrace
    drawErrorWindow(e)
  }

  def drawErrorWindow(t: Throwable) = {
    WindowCount.incrementAndGet

    if (Platform.isFxApplicationThread) {
      draw(t)
    } else {
      Platform.runLater(new Runnable {
        override def run = draw(t)
      })
    }
  }

  private def draw(t: Throwable) = {
    try {
      val stage = new Stage
      val root: AnchorPane = FXMLLoader.load(getClass.getClassLoader.getResource("ErrorMessageWindow.fxml"))

      stage.setTitle(s"${JGhostClient.title} - Exception")
      stage.initModality(Modality.APPLICATION_MODAL)
      stage.setResizable(false)
      stage.setScene(new Scene(root))
      stage.setOnCloseRequest(new EventHandler[WindowEvent] {
        override def handle(event: WindowEvent) = {
          if (WindowCount.getAndDecrement <= 0) {
            Platform.exit
          }
        }
      })

      val area = stage.getScene.lookup("#exception_stack").asInstanceOf[TextArea]
      area.setWrapText(true)
      area.setText(Throwables.getStackTraceAsString(t))

      stage.show
    } catch {
      case e: Exception => System.exit(0)
    }
  }
}
