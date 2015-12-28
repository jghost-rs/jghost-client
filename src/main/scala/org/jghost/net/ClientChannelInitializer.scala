package org.jghost.net

import io.netty.channel.ChannelInitializer
import io.netty.channel.socket.nio.NioSocketChannel
import org.jghost.JGhostClient
import org.jghost.net.codec.AuthenticationDecoder

final class ClientChannelInitializer(client: Client) extends ChannelInitializer[NioSocketChannel] {

  private val clientHandler = new ClientHandler
  private val authenticationDecoder = new AuthenticationDecoder

  override def initChannel(ch: NioSocketChannel) = {
    ch.pipeline.addLast("authentication-decoder", authenticationDecoder)
    ch.pipeline.addLast("client-handler", clientHandler)

    ch.attr(JGhostClient.ClientKey).set(client)
  }
}
