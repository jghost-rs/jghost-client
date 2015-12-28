package org.jghost.net

import java.util.concurrent.TimeUnit
import javafx.application.Platform

import io.netty.buffer.Unpooled
import io.netty.channel.{ChannelHandlerContext, SimpleChannelInboundHandler}
import org.jghost.JGhostClient
import org.jghost.util.JGhostExceptionHandler

import scala.concurrent.forkjoin.ThreadLocalRandom

final class ClientHandler extends SimpleChannelInboundHandler[Object] {

  override def channelRegistered(ctx: ChannelHandlerContext) = {
    val channel = ctx.channel
    val client = channel.attr(JGhostClient.ClientKey).get

    client.progress.updateProgress(0.05, "Sending initial request to jGhost server...")

    channel.eventLoop.schedule(new Runnable {
      override def run = {
        val initialRequest = Unpooled.buffer(1).writeByte(10)

        ctx.writeAndFlush(initialRequest, ctx.voidPromise)
        client.progress.updateProgress(0.10, "Waiting on initial response from jGhost server...")
      }
    }, ThreadLocalRandom.current.nextLong(1000, 3000), TimeUnit.MILLISECONDS)
  }

  override def exceptionCaught(ctx: ChannelHandlerContext, e: Throwable) = {
    e.printStackTrace
    JGhostClient.ExceptionHandler.displayUncaughtException(e)
  }

  override def channelRead0(ctx: ChannelHandlerContext, msg: Object) = {
    //TODO: Recieve messages
  }
}