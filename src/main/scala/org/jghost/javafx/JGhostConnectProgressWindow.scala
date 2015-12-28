package org.jghost.javafx

import java.util.concurrent.ThreadLocalRandom
import javafx.application.Platform
import javafx.scene.control.{Label, ProgressBar}
import javafx.stage.StageStyle

final class JGhostConnectProgressWindow extends JGhostWindow("connect_progress.fxml", "Connecting") {
  stage.initStyle(StageStyle.UNDECORATED)

  private var progress: ProgressBar = _
  private var label: Label = _

  override def onDisplay = {
    label = scene.lookup("#progress_status").asInstanceOf[Label]
    label.setWrapText(true)

    progress = scene.lookup("#progress_bar").asInstanceOf[ProgressBar]
    progress.setProgress(0.0)
  }

  def updateProgress(baseAmount: Double, newText: String) = {
    val updateTask = computeUpdateTask(baseAmount, newText)

    if (Platform.isFxApplicationThread) {
      updateTask.run
    } else {
      Platform.runLater(updateTask)
    }
  }

  private def computeUpdateTask(baseAmount: Double, newText: String) = {
    new Runnable {
      override def run = {
        progress.setProgress(progress.getProgress + ThreadLocalRandom.current.nextDouble(baseAmount, baseAmount * 2))
        label.setText(newText)
      }
    }
  }
}
