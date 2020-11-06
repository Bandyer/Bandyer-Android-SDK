/*
 * Copyright (C) 2020 Bandyer S.r.l. All Rights Reserved.
 * See LICENSE.txt for licensing information
 */
package com.bandyer.app_configuration.external_configuration.model

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