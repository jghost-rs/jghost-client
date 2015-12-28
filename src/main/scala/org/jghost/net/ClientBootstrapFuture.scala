package org.jghost.net

import java.io.File
import javafx.application.Platform
import javafx.scene.control.Alert.AlertType
import javafx.scene.control.ButtonType

import io.netty.channel.{ChannelFuture, ChannelFutureListener}
import org.jghost.javafx.{JGhostAlert, JGhostConnectWindow}

final class ClientBootstrapFuture(client: Client) extends ChannelFutureListener {

  private val failInformation =
    """
      |The Bootstrap has failed to connect to the user-specified address. This is almost always
      |caused by the jGhost client being unable to locate a running server. Please ensure that
      |a jGhost server is online.
    """.stripMargin.replaceAll(System.lineSeparator, " ").substring(1)

  override def operationComplete(future: ChannelFuture) = {
    if (!future.isSuccess) {
      Platform.runLater(new Runnable {
        override def run = {
          displayAlertWindow
        }
      })

      client.close
    }
  }

  private def displayAlertWindow = {
    val alert = new JGhostAlert(AlertType.ERROR, "Error")
    val window = new JGhostConnectWindow

    alert.setHeaderText("An error has occurred with jGhost!")
    alert.setContentText(failInformation)

    val alertResult = alert.showAndWait
    if (alertResult.isPresent && alertResult.get == ButtonType.OK) {
      window.displayWindow
      client.progress.hideWindow
    }
  }
}
