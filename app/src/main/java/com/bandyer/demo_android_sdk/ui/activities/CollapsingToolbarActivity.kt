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
package com.bandyer.demo_android_sdk.ui.activities

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
import com.bandyer.demo_android_sdk.R
import com.bandyer.demo_android_sdk.databinding.ActivityCollapsingToolbarBinding
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.appbar.AppBarLayout.OnOffsetChangedListener
import com.google.android.material.appbar.AppBarLayout.ScrollingViewBehavior
import com.kaleyra.app_utilities.MultiDexApplication
import com.kaleyra.app_utilities.activities.BaseActivity
import com.kaleyra.app_utilities.storage.ConfigurationPrefsManager
import com.kaleyra.app_utilities.utils.DPadNavigationHelper
import kotlin.math.abs

abstract class CollapsingToolbarActivity : BaseActivity(), OnRefreshListener {

    private var appTitle: String? = null
    private var collapsedTitle: String? = null

    private val textSizeH1 by lazy { resources.getDimensionPixelSize(R.dimen.text_h1) }
    private val textSizeH3 by lazy { resources.getDimensionPixelSize(R.dimen.text_h3) }
    private val textSizeH4 by lazy { resources.getDimensionPixelSize(R.dimen.text_h4) }
    private var titleSpan: SpannableString? = null

    protected val restApi by lazy { MultiDexApplication.restApi }

    private lateinit var binding: ActivityCollapsingToolbarBinding

    private val version: String by lazy {
        val pInfo = packageManager.getPackageInfo(packageName, 0)
        pInfo.versionName
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        titleSpan = SpannableString(String.format(resources.getString(R.string.app_name_with_version), "v$version"))
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
        binding = ActivityCollapsingToolbarBinding.bind(container)
        customizeSwipeRefreshLayout()
        customizeAppBarLayout()

        binding.refreshUsersView.setOnRefreshListener(this)
        binding.appbarToolbar.setExpanded(isPortrait())
    }

    protected fun setRefreshing(refresh: Boolean) {
        binding.refreshUsersView.isRefreshing = refresh
    }

    override fun onResume() {
        super.onResume()
        window.setBackgroundDrawable(ColorDrawable(Color.WHITE))
    }

    fun setCollapsingToolbarTitle(portraitTitle: String, landscapeTitle: String) {
        val environment = ConfigurationPrefsManager.getConfiguration(this).environment
        val region = ConfigurationPrefsManager.getConfiguration(this).region
        appTitle = String.format(resources.getString(R.string.app_name_with_version), "v$version")
        val envTextView = SpannableString("\n@${environment}-${region}\n")
        envTextView.setSpan(AbsoluteSizeSpan(textSizeH4), 0, envTextView.length, Spanned.SPAN_INCLUSIVE_INCLUSIVE)
        val infoSpan = SpannableString("\n$portraitTitle")
        infoSpan.setSpan(AbsoluteSizeSpan(textSizeH3), 0, infoSpan.length, Spanned.SPAN_INCLUSIVE_INCLUSIVE)
        val collapsingToolbarTitle = TextUtils.concat(titleSpan, envTextView, infoSpan)
        binding.info.text = collapsingToolbarTitle
        this@CollapsingToolbarActivity.collapsedTitle = "$appTitle  @$environment | $landscapeTitle"
        binding.collapsingToolbar.title = if (isPortrait()) appTitle else collapsedTitle
    }

    private fun customizeSwipeRefreshLayout() {
        binding.refreshUsersView.setColorSchemeColors(
                ContextCompat.getColor(this, R.color.colorPrimaryDark),
                ContextCompat.getColor(this, R.color.colorPrimary),
                ContextCompat.getColor(this, R.color.colorPrimaryLight)
        )
    }

    private fun customizeAppBarLayout() {
        setSupportActionBar(binding.toolbar)
        supportActionBar!!.setHomeButtonEnabled(false)
        supportActionBar!!.setDisplayHomeAsUpEnabled(false)
        supportActionBar!!.setDisplayShowHomeEnabled(false)
        binding.appbarToolbar.addOnOffsetChangedListener(OnOffsetChangedListener { appBarLayout: AppBarLayout, verticalOffset: Int ->
            if (binding.refreshUsersView.isRefreshing && verticalOffset == 0) binding.refreshUsersView.isRefreshing = false
            binding.refreshUsersView.isEnabled = verticalOffset == 0
            if (abs(verticalOffset) - appBarLayout.totalScrollRange == 0) {
                binding.collapsingToolbar.title = if (isPortrait()) appTitle else collapsedTitle
                binding.toolbar.background = ColorDrawable(ContextCompat.getColor(this, R.color.colorPrimary))
            } else {
                binding.collapsingToolbar.title = ""
                binding.toolbar.background = ColorDrawable(Color.TRANSPARENT)
            }
            val offsetAlpha = 1 - appBarLayout.y / appBarLayout.totalScrollRange * -1
            binding.fader.alpha = 1 - offsetAlpha
            binding.toolbar.background.alpha = (255 * offsetAlpha).toInt()
        })
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent): Boolean {
        if (DPadNavigationHelper.isDpad(keyCode)) binding.appbarToolbar.setExpanded(false)
        return super.onKeyDown(keyCode, event)
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        binding.appbarToolbar.setExpanded(isPortrait())
    }

    private fun isPortrait() = resources.configuration.orientation != Configuration.ORIENTATION_LANDSCAPE
}