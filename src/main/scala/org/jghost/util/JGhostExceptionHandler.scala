package org.jghost.util

import java.lang.Thread.UncaughtExceptionHandler
import java.util.concurrent.atomic.AtomicInteger
import javafx.application.Platform
import javafx.event.EventHandler
import javafx.fxml.FXMLLoader
import javafx.scene.Scene
import javafx.scene.control.Alert.AlertType
import javafx.scene.control.{ButtonType, Alert, TextArea}
import javafx.scene.layout.AnchorPane
import javafx.stage.{Modality, Stage, WindowEvent}

import com.google.common.base.Throwables
import org.jghost.JGhostClient
import org.jghost.javafx.JGhostAlert

class JGhostExceptionHandler extends UncaughtExceptionHandler {

  override def uncaughtException(t: Thread, e: Throwable) {
    e.printStackTrace
    displayUncaughtException(e)
  }

  def displayUncaughtException(t: Throwable) = {
    if (Platform.isFxApplicationThread) {
      drawAlertWindow(t)
    } else {
      Platform.runLater(new Runnable {
        override def run = drawAlertWindow(t)
      })
    }
  }

  private def drawAlertWindow(t: Throwable) = {
    val alert = new JGhostAlert(AlertType.ERROR, "Exception")

    alert.setHeaderText("An unexpected exception has been thrown!")
    alert.setContentText(Throwables.getStackTraceAsString(t))
    alert.show
  }
}
