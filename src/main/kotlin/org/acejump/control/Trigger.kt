package org.acejump.control

import com.intellij.openapi.diagnostic.Logger
import org.acejump.search.runLater
import java.lang.System.currentTimeMillis
import kotlin.math.abs

/**
 * Timer for triggering events with a designated delay. May be invoked multiple
 * times inside the delay, but doing so will only prolong the event from firing.
 */

object Trigger: () -> Unit {
  private val logger = Logger.getInstance(Trigger::class.java)
  private var delay = 0L
  private var timer = currentTimeMillis()
  private var isRunning = false
  private var invokable: () -> Unit = {}

  override fun invoke() {
    timer = currentTimeMillis()
    if (isRunning) return
    synchronized(this) {
      isRunning = true

      while (currentTimeMillis() - timer <= delay)
        Thread.sleep(abs(delay - (currentTimeMillis() - timer)))

      try {
        invokable()
      } catch (e: Exception) {
        logger.error("Exception occurred while triggering event!", e)
      }

      isRunning = false
    }
  }

  operator fun invoke(withDelay: Long = 750, event: () -> Unit = {}) {
    delay = withDelay
    invokable = event
    runLater(this)
  }
}