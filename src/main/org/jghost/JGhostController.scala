package org.jghost

import javafx.fxml.FXML
import javafx.scene.control.{Button, PasswordField, TextField}
import javafx.scene.input.{KeyCode, KeyEvent}

import org.jghost.net.Client

final class JGhostController {

  @FXML
  private var connect: Button = _

  @FXML
  def handleConnectButton = {
    val scene = connect.getScene

    scene.getWindow.hide

    val address = scene.lookup("#ip_address").asInstanceOf[TextField].getText
    val port = Integer.parseInt(scene.lookup("#port").asInstanceOf[TextField].getText)
    val password = scene.lookup("#password").asInstanceOf[PasswordField].getText

    val client = new Client(address, port, password)
    client.connect
  }

  @FXML
  def handleConnectKey(evt: KeyEvent) = {
    if (evt.getCode() == KeyCode.ENTER) {
      handleConnectButton
    }
  }
}
