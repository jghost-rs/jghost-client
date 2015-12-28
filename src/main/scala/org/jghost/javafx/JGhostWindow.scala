package org.jghost.javafx

import javafx.fxml.FXMLLoader
import javafx.scene.Scene
import javafx.scene.image.Image
import javafx.scene.layout.AnchorPane
import javafx.stage.Stage

import org.jghost.JGhostClient

class JGhostWindow(resource: String, title: String) {

  var root: AnchorPane = _
  var scene: Scene = _
  var stage: Stage = new Stage

  def onDisplay = {}

  def displayWindow = {
    root = FXMLLoader.load(getClass.getClassLoader.getResource(resource))
    scene = new Scene(root)

    root.getStylesheets.add("dark_theme.css")

    stage.setTitle(s"${JGhostClient.Title} - $title")
    stage.setResizable(false)
    stage.setScene(scene)
    stage.getIcons.add(new Image(getClass.getClassLoader.getResourceAsStream("jghost_icon.png")))

    onDisplay

    stage.show
  }

  def hideWindow = {
    stage.hide
  }
}
