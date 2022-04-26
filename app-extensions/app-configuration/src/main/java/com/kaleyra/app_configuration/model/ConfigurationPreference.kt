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
package com.kaleyra.app_configuration.model

import android.view.View
import android.widget.TextView

/**
 * Configuration preference that can be shown with title, subtitle and summary
 */
interface ConfigurationPreference {

    /**
     * The title textview
     */
    var titleTextView: TextView?

    /**
     * The subtitle textview
     */
    var subtitleTextView: TextView?

    /**
     * The summary textview
     */
    var summaryTextView: TextView?

    /**
     * The summary container
     */
    var summaryTextViewHolder: View?

    /**
     * Sets the preference title
     * @param title String? title text
     */
    fun setTitle(title: String?) {
        titleTextView?.visibility = if (title.isNullOrEmpty()) View.GONE else View.VISIBLE
        titleTextView?.text = title
    }

    /**
     * Sets the preference subtitle
     * @param subtitle String? subtitle text
     */
    fun setSubtitle(subtitle: String?) {
        subtitleTextView?.visibility = if (subtitle.isNullOrEmpty()) View.GONE else View.VISIBLE
        subtitleTextView?.text = subtitle
    }

    /**
     * Sets the preference summary
     * @param summary String? summary text
     */
    fun setSummary(summary: String?) {
        summaryTextViewHolder?.visibility = if (summary.isNullOrEmpty()) View.GONE else View.VISIBLE
        summaryTextView?.text = summary
    }
}