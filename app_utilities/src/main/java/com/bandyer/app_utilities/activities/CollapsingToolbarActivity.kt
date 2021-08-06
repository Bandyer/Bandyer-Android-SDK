/*
 * Copyright (C) 2020 Bandyer S.r.l. All Rights Reserved.
 * See LICENSE.txt for licensing information
 */
package com.bandyer.app_utilities.activities

import android.annotation.SuppressLint
import android.content.res.Configuration
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.text.SpannableString
import android.text.Spanned
import android.text.TextUtils
import android.text.style.AbsoluteSizeSpan
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.content.ContextCompat
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout.OnRefreshListener
import com.bandyer.android_sdk.BuildConfig
import com.bandyer.app_utilities.R
import com.bandyer.app_utilities.storage.ConfigurationPrefsManager
import com.bandyer.app_utilities.utils.DPadNavigationHelper
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.appbar.AppBarLayout.OnOffsetChangedListener
import com.google.android.material.appbar.AppBarLayout.ScrollingViewBehavior
import kotlinx.android.synthetic.main.activity_collapsing_toolbar.*
import kotlin.math.abs

abstract class CollapsingToolbarActivity : BaseActivity(), OnRefreshListener {

    private var appTitle: String? = null
    private var collapsedTitle: String? = null

    private val textSizeH1 by lazy { resources.getDimensionPixelSize(R.dimen.text_h1) }
    private val textSizeH3 by lazy { resources.getDimensionPixelSize(R.dimen.text_h3) }
    private val textSizeH4 by lazy { resources.getDimensionPixelSize(R.dimen.text_h4) }
    private var titleSpan: SpannableString? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        titleSpan = SpannableString(String.format(resources.getString(R.string.app_name_with_version), "v" + BuildConfig.VERSION_NAME))
        titleSpan!!.setSpan(AbsoluteSizeSpan(textSizeH1), 0, titleSpan!!.length, Spanned.SPAN_INCLUSIVE_INCLUSIVE)
    }

    @SuppressLint("SetTextI18n")
    override fun setContentView(layoutResID: Int) {
        val inflater = LayoutInflater.from(this)
        val container = inflater.inflate(R.layout.activity_collapsing_toolbar, null)
        val coordinatorLayout: CoordinatorLayout = container.findViewById(R.id.main_view)
        inflater.inflate(layoutResID, coordinatorLayout)
        super.setContentView(container)
        val layoutParams = CoordinatorLayout.LayoutParams(CoordinatorLayout.LayoutParams.MATCH_PARENT, CoordinatorLayout.LayoutParams.MATCH_PARENT)
        layoutParams.behavior = ScrollingViewBehavior(this, null)
        findViewById<View>(R.id.content).layoutParams = layoutParams
        customizeSwipeRefreshLayout()
        customizeAppBarLayout()
        refreshUsersView.setOnRefreshListener(this)
        appbar_toolbar?.setExpanded(isPortrait())
    }

    protected fun setRefreshing(refresh: Boolean) {
        refreshUsersView.isRefreshing = refresh
    }

    override fun onResume() {
        super.onResume()
        window.setBackgroundDrawable(ColorDrawable(Color.WHITE))
    }

    fun setCollapsingToolbarTitle(portraitTitle: String, landscapeTitle: String) {
        val environment = ConfigurationPrefsManager.getConfiguration(this).environment
        appTitle = String.format(resources.getString(R.string.app_name_with_version), "v" + BuildConfig.VERSION_NAME)
        val envTextView = SpannableString("\n@${environment}\n")
        envTextView.setSpan(AbsoluteSizeSpan(textSizeH4), 0, envTextView.length, Spanned.SPAN_INCLUSIVE_INCLUSIVE)
        val infoSpan = SpannableString("\n$portraitTitle")
        infoSpan.setSpan(AbsoluteSizeSpan(textSizeH3), 0, infoSpan.length, Spanned.SPAN_INCLUSIVE_INCLUSIVE)
        val collapsingToolbarTitle = TextUtils.concat(titleSpan, envTextView, infoSpan)
        info.text = collapsingToolbarTitle
        this@CollapsingToolbarActivity.collapsedTitle = "$appTitle  @$environment | $landscapeTitle"
        collapsing_toolbar.title = if (isPortrait()) appTitle else collapsedTitle
    }

    private fun customizeSwipeRefreshLayout() {
        refreshUsersView.setColorSchemeColors(
                ContextCompat.getColor(this, R.color.colorPrimaryDark),
                ContextCompat.getColor(this, R.color.colorPrimary),
                ContextCompat.getColor(this, R.color.colorPrimaryLight)
        )
    }

    private fun customizeAppBarLayout() {
        setSupportActionBar(toolbar)
        supportActionBar!!.setHomeButtonEnabled(false)
        supportActionBar!!.setDisplayHomeAsUpEnabled(false)
        supportActionBar!!.setDisplayShowHomeEnabled(false)
        appbar_toolbar.addOnOffsetChangedListener(OnOffsetChangedListener { appBarLayout: AppBarLayout, verticalOffset: Int ->
            if (refreshUsersView.isRefreshing && verticalOffset == 0) refreshUsersView.isRefreshing = false
            refreshUsersView.isEnabled = verticalOffset == 0
            if (abs(verticalOffset) - appBarLayout.totalScrollRange == 0) {
                collapsing_toolbar.title = if (isPortrait()) appTitle else collapsedTitle
                toolbar.background = ColorDrawable(ContextCompat.getColor(this, R.color.colorPrimary))
            } else {
                collapsing_toolbar.title = ""
                toolbar.background = ColorDrawable(Color.TRANSPARENT)
            }
            val offsetAlpha = 1 - appBarLayout.y / appBarLayout.totalScrollRange * -1
            fader.alpha = 1 - offsetAlpha
            toolbar.background.alpha = (255 * offsetAlpha).toInt()
        })
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent): Boolean {
        if (DPadNavigationHelper.isDpad(keyCode)) appbar_toolbar.setExpanded(false)
        return super.onKeyDown(keyCode, event)
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        appbar_toolbar?.setExpanded(isPortrait())
    }

    private fun isPortrait() = resources.configuration.orientation != Configuration.ORIENTATION_LANDSCAPE
}