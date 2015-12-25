package org.jghost.net

import io.netty.channel.ChannelInitializer
import io.netty.channel.socket.nio.NioSocketChannel
import org.jghost.net.codec.AuthenticationDecoder

final class ClientChannelInitializer(client: Client) extends ChannelInitializer[NioSocketChannel] {

  val clientHandler = new ClientHandler

  @throws(classOf[Exception])
  override def initChannel(ch: NioSocketChannel) = {
    ch.pipeline.addLast("authentication-decoder", new AuthenticationDecoder)
    ch.pipeline.addLast("client-handler", clientHandler)

    ch.attr(Client.ClientKey).set(client)
  }
}
