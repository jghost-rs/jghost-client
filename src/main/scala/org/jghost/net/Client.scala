package org.jghost.net

import java.util.concurrent.{ThreadLocalRandom, TimeUnit}
import javafx.application.Platform
import javafx.scene.control.Alert.AlertType
import javafx.scene.control.ButtonType

import com.google.common.util.concurrent.ThreadFactoryBuilder
import io.netty.bootstrap.Bootstrap
import io.netty.channel.Channel
import io.netty.channel.nio.NioEventLoopGroup
import io.netty.channel.socket.nio.NioSocketChannel
import io.netty.util.ResourceLeakDetector
import io.netty.util.ResourceLeakDetector.Level
import org.jghost.JGhostClient
import org.jghost.javafx.{JGhostControlPanel, JGhostConnectWindow, JGhostAlert, JGhostConnectProgressWindow}

final case class Client(address: String, port: Int, password: String) {

  val progress = new JGhostConnectProgressWindow

  private var channel: Channel = _
  private val loopGroup = new NioEventLoopGroup(1, new ThreadFactoryBuilder().setNameFormat("JGhostClientThread")
    .build)

  def connect: Unit = {
    progress.displayWindow

    ResourceLeakDetector.setLevel(Level.DISABLED)

    val bootstrap = new Bootstrap

    bootstrap.group(loopGroup)
    bootstrap.channel(classOf[NioSocketChannel])
    bootstrap.handler(new ClientChannelInitializer(this))

    progress.updateProgress(0.05, "Establishing connection with jGhost server...")

    val channelFuture = bootstrap.connect(address, port)
    channel = channelFuture.channel

    channelFuture.addListener(new ClientBootstrapFuture(this))
  }

  def close: Unit = {
    channel.close.syncUninterruptibly
    loopGroup.shutdownGracefully(0, 0, TimeUnit.MILLISECONDS)
  }

  def displayPasswordAlert = {
    Platform.runLater(new Runnable {
      override def run = {
        val alert = new JGhostAlert(AlertType.ERROR, "Error")
        val newWindow = new JGhostConnectWindow

        alert.setHeaderText("An error has occurred with jGhost!")
        alert.setContentText("Authentication failed! Please specify the correct password.")

        val alertResult = alert.showAndWait

        if (alertResult.isPresent && alertResult.get == ButtonType.OK) {
          newWindow.displayWindow
          progress.hideWindow
        }
      }
    })
  }

  def displayControlPanel = {
    Platform.runLater(new Runnable {
      override def run = {
        val controlPanel = new JGhostControlPanel
        controlPanel.displayWindow

        progress.hideWindow

        JGhostClient.Properties("address") = address
        JGhostClient.Properties("port") = Integer.toString(port)
      }
    })
  }
}