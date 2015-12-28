package org.jghost.javafx

import javafx.scene.control.TextField

import org.jghost.JGhostClient

final class JGhostConnectWindow extends JGhostWindow("connect.fxml", "Authentication") {

  override def onDisplay = {
    if (JGhostClient.Properties("address") != null) {
      scene.lookup("#ip_address").asInstanceOf[TextField].setText(JGhostClient.Properties("address"))
    } else if (JGhostClient.Properties("port") != null) {
      scene.lookup("#port").asInstanceOf[TextField].setText(JGhostClient.Properties("port"))
    }
  }
}
