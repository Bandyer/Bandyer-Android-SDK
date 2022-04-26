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
package com.kaleyra.app_utilities

import com.facebook.flipper.plugins.leakcanary2.FlipperLeakListener
import leakcanary.LeakCanary
import leakcanary.ToastEventListener

/**
 * Enable or Disable leakCanary
 * @author kristiyan
 */
object LeakCanaryManager {
    fun enableLeakCanary(enabled: Boolean) {
        LeakCanary.config = LeakCanary.config.run {
            copy(
                dumpHeap = enabled,
                onHeapAnalyzedListener = FlipperLeakListener(),
                eventListeners = eventListeners.filter { it !is ToastEventListener })
        }
    }
}