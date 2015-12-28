package org.jghost.javafx

import javafx.scene.control.Alert
import javafx.scene.control.Alert.AlertType
import javafx.scene.image.Image
import javafx.stage.Stage

import org.jghost.JGhostClient

final class JGhostAlert(alertType: AlertType, title: String) extends Alert(alertType) {

  getStage.setTitle(s"${JGhostClient.Title} - $title")
  getDialogPane.getStylesheets.add("dark_theme.css")
  getStage.getIcons.add(new Image(getClass.getClassLoader.getResourceAsStream("jghost_icon.png")))

  def getStage = getDialogPane.getScene.getWindow.asInstanceOf[Stage]
}
