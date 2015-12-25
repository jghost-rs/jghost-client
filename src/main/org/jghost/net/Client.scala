package org.jghost.net

import java.util.concurrent.ThreadLocalRandom
import javafx.fxml.FXMLLoader
import javafx.scene.Scene
import javafx.scene.control.{Label, ProgressBar}
import javafx.scene.layout.AnchorPane
import javafx.stage.Stage

import com.google.common.base.Throwables
import com.google.common.util.concurrent.ThreadFactoryBuilder
import io.netty.bootstrap.Bootstrap
import io.netty.channel.nio.NioEventLoopGroup
import io.netty.channel.socket.nio.NioSocketChannel
import io.netty.util.ResourceLeakDetector.Level
import io.netty.util.{AttributeKey, ResourceLeakDetector}
import org.jghost.JGhostClient

final case class Client(address: String, port: Int, password: String) {

  val bootstrap = new Bootstrap
  val loopGroup = new NioEventLoopGroup(1, new ThreadFactoryBuilder().setNameFormat("JGhostClientThread")
    .build)

  var progressValue: Double = _
  var progress: ProgressBar = _
  var label: Label = _

  def connect = {
    val scene = createProgressWindow

    label = scene.lookup("#progress_status").asInstanceOf[Label]
    label.setWrapText(true)

    progress = scene.lookup("#progress_bar").asInstanceOf[ProgressBar]

    ResourceLeakDetector.setLevel(Level.DISABLED)

    bootstrap.group(loopGroup)
    bootstrap.channel(classOf[NioSocketChannel])
    bootstrap.handler(new ClientChannelInitializer(this))

    increment(ThreadLocalRandom.current.nextDouble(0.05, 0.10))
    label.setText("Establishing connection with jGhost server...")

    bootstrap.connect(address, port).syncUninterruptibly
  }

  private def createProgressWindow: Scene = {
    val stage = new Stage

    try {
      val root: AnchorPane = FXMLLoader.load(getClass.getClassLoader.getResource("ConnectProgressWindow.fxml"))

      stage.setTitle(s"${JGhostClient.title} - Connecting")
      stage.setResizable(false)
      stage.setScene(new Scene(root))

      stage.show
    } catch {
      case ex: Exception => throw Throwables.propagate(ex)
    }
    return stage.getScene
  }

  def increment(amount: Double) {
    progressValue += amount
    progress.setProgress(progressValue)
  }
}

object Client {
  val ClientKey: AttributeKey[Client] = AttributeKey.valueOf("client.KEY")
}
