package org.jghost.javafx

import javafx.fxml.FXML
import javafx.scene.control.Alert.AlertType
import javafx.scene.control._
import javafx.scene.input.{KeyCode, KeyEvent}
import javafx.stage.Stage

import com.google.common.net.InetAddresses
import org.jghost.JGhostClient
import org.jghost.net.Client

final class JGhostEventHandler {

  @FXML
  private var connect: Button = _

  @FXML
  def handleConnectButton: Unit = {
    val scene = connect.getScene

    val address = scene.lookup("#ip_address").asInstanceOf[TextField].getText
    val port = scene.lookup("#port").asInstanceOf[TextField].getText
    val password = scene.lookup("#password").asInstanceOf[PasswordField].getText

    if (address.isEmpty || port.isEmpty || password.isEmpty) {
      val alert = new JGhostAlert(AlertType.WARNING, "Warning")

      alert.setHeaderText("jGhost has encountered an issue!")

      if (address.isEmpty) {
        alert.setContentText("No connection address was specified.")
      } else if (port.isEmpty) {
        alert.setContentText("No connection port was specified.")
      } else if (password.isEmpty) {
        alert.setContentText("No authenticating password was specified.")
      }
      alert.show
      return
    }

    if (!InetAddresses.isInetAddress(address)) {
      val alert = new JGhostAlert(AlertType.WARNING, "Warning")

      alert.setHeaderText("jGhost has encountered an issue!")
      alert.setContentText("An invalid IP address was specified.")
      alert.show
      return
    }

    scene.getWindow.hide

    val client = new Client(address, Integer.parseInt(port), password)
    client.connect
  }

  @FXML
  def handleConnectKey(evt: KeyEvent) = {
    if (evt.getCode() == KeyCode.ENTER) {
      handleConnectButton
    }
  }
}
