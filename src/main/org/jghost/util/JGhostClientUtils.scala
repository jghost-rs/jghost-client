package org.jghost.util

import java.util.concurrent.{ScheduledExecutorService, TimeUnit}

import com.google.common.util.concurrent.Uninterruptibles

import scala.concurrent.forkjoin.ThreadLocalRandom

object JGhostClientUtils {
  def sleep = Uninterruptibles.sleepUninterruptibly(ThreadLocalRandom.current.nextInt(500, 1200), TimeUnit.MILLISECONDS)
  def schedule(t: Runnable, service: ScheduledExecutorService) = service.schedule(t, ThreadLocalRandom.current().nextInt(500, 1200), TimeUnit.MILLISECONDS)
}
