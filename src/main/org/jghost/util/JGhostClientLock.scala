package org.jghost.util

import java.io.IOException
import java.nio.file.{Files, Paths}

import scala.reflect.io.File

final class JGhostClientLock {

  private val lockPath = Paths.get(System.getProperty("user.home") + File.separator + "jGhostLock.lock")

  @throws(classOf[IOException])
  def lock: Boolean = {
    if (isLocked) {
      return false
    }
    Files.createFile(lockPath)
    true
  }

  @throws(classOf[IOException])
  def unlock: Boolean = {
    if (!isLocked) {
      return false
    }
    Files.delete(lockPath)
    true
  }

  def isLocked = Files.exists(lockPath)
}
