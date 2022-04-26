/*
 * Copyright 2022 Kaleyra @ https://www.kaleyra.com
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.kaleyra.demo_collaboration_suite

import android.util.Log
import java.util.logging.Handler
import java.util.logging.Level
import java.util.logging.LogRecord
import java.util.logging.Logger

fun enableSocketIOLogger() {

    val handler = object : Handler() {

        override fun publish(record: LogRecord) {
            if (!super.isLoggable(record)) return
            val name = record.loggerName
            val maxLength = 30
            val tag = if (name.length > maxLength) name.substring(name.length - maxLength) else name
            kotlin.runCatching {
                val level = record.level.toAndroidLogLevel()
                Log.println(level, tag, record.message)
                if (record.thrown != null) Log.println(level, tag, Log.getStackTraceString(record.thrown))
            }
        }

        override fun flush() = Unit
        override fun close() = Unit
    }

    Logger.getLogger("io.socket.client.Socket").apply {
        addHandler(handler)
        level = Level.FINE
    }
    Logger.getLogger("io.socket.engineio.client.Socket").apply {
        addHandler(handler)
        level = Level.FINEST
    }
    Logger.getLogger("io.socket.client.Manager").apply {
        addHandler(handler)
        level = Level.FINEST
    }
}

private fun Level.toAndroidLogLevel(): Int {
    val value = intValue()
    return when {
        value >= 1000 -> Log.ERROR
        value >= 900  -> Log.WARN
        value >= 800  -> Log.INFO
        else          -> Log.DEBUG
    }
}