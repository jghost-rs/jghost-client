package org.jghost.util

import java.util.concurrent.ThreadLocalRandom

import org.jghost.net.Client

case class ConnectProgressTask(client: Client, progress: Double, newMessage: String) extends Runnable {
  override def run = {
    client.increment(ThreadLocalRandom.current.nextDouble(progress, progress * 2))
    client.label.setText(newMessage);
  }
}
