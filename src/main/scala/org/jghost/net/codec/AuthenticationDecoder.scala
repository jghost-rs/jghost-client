package org.jghost.net.codec

import java.util.List
import java.util.concurrent.TimeUnit
import javafx.application.Platform
import javafx.scene.Scene
import javafx.scene.control.{TextField, ButtonType, Alert}
import javafx.scene.control.Alert.AlertType
import javafx.scene.layout.AnchorPane
import javafx.stage.Stage

import io.netty.buffer.{Unpooled, ByteBuf}
import io.netty.channel.{Channel, ChannelHandlerContext}
import io.netty.handler.codec.ReplayingDecoder
import org.jghost.JGhostClient
import org.jghost.javafx.{JGhostControlPanel, JGhostConnectWindow, JGhostAlert}
import org.jghost.net.Client

import scala.concurrent.forkjoin.ThreadLocalRandom

final class AuthenticationDecoder extends ReplayingDecoder[AuthenticationState](InitialResponse) {

  override def decode(ctx: ChannelHandlerContext, in: ByteBuf, out: List[Object]) {
    val client = ctx.channel.attr(JGhostClient.ClientKey).get

    state match {
      case InitialResponse => {
        displayInitialResponse(ctx, in, client)
        checkpoint(ValidationResponse)
      }
      case ValidationResponse => {
        displayValidationResponse(ctx.channel, in, client)
      }
      case _ => throw new IllegalStateException("should never reach here")
    }
  }

  private def displayInitialResponse(ctx: ChannelHandlerContext, in: ByteBuf, client: Client) {
    val initialResponse = in.readByte
    if (initialResponse != 26) {
      throw new IllegalStateException("unexpected response")
    }

    client.progress.updateProgress(0.10, "Sending password length to jGhost server...")

    val passwordLength = Unpooled.buffer(1).writeByte(client.password.length)
    ctx.writeAndFlush(passwordLength, ctx.voidPromise)

    client.progress.updateProgress(0.10, "Requesting password validation from jGhost server...")

    val passwordValidation = Unpooled.buffer
    passwordValidation.writeBytes(client.password.getBytes)

    ctx.writeAndFlush(passwordValidation, ctx.voidPromise)
    client.progress.updateProgress(0.10, "Waiting on password validation from the jGhost server...")
  }

  private def displayValidationResponse(channel: Channel, in: ByteBuf, client: Client) {
    val validationResp = in.readBoolean
    if (!validationResp) {
      client.displayPasswordAlert

      channel.close
      client.close
      return
    }

    client.progress.updateProgress(1, "Connection and authentication to jGhost server established!")
    client.displayControlPanel

    channel.pipeline.remove(this)
  }
}
