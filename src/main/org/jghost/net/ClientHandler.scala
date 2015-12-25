package org.jghost.net

import java.util.concurrent.TimeUnit
import javafx.application.Platform

import io.netty.buffer.Unpooled
import io.netty.channel.{ChannelHandlerContext, SimpleChannelInboundHandler}
import org.jghost.util.{ConnectProgressTask, ExceptionHandler}

import scala.concurrent.forkjoin.ThreadLocalRandom

final class ClientHandler extends SimpleChannelInboundHandler[Object] {

  override def channelRegistered(ctx: ChannelHandlerContext) = {
    val channel = ctx.channel
    val client = channel.attr(Client.ClientKey).get

    Platform.runLater(new ConnectProgressTask(client, 0.05, "Sending inital request to jGhost server..."))

    channel.eventLoop.schedule(new Runnable {
      override def run = {
        val initialRequest = Unpooled.buffer(1).writeByte(10)

        ctx.writeAndFlush(initialRequest, ctx.voidPromise)
        Platform.runLater(new ConnectProgressTask(client, 0.10, "Waiting on initial response from jGhost server..."))
      }
    }, ThreadLocalRandom.current.nextLong(500, 1000), TimeUnit.MILLISECONDS)
  }

  override def exceptionCaught(ctx: ChannelHandlerContext, e: Throwable) = {
    ExceptionHandler.drawErrorWindow(e)
    ctx.channel.close
  }

  @throws(classOf[Exception])
  override def channelInactive(ctx: ChannelHandlerContext) = {
    throw new IllegalStateException("jGhost has disconnected")
  }

  @throws(classOf[Exception])
  override def channelRead0(ctx: ChannelHandlerContext, msg: Object) = {
    throw new IllegalStateException("unexpected data recieved")
  }
}