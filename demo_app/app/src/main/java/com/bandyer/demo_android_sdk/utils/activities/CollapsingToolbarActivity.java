package com.bandyer.demo_android_sdk.utils.activities;

import android.annotation.SuppressLint;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.AbsoluteSizeSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.content.ContextCompat;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.bandyer.demo_android_sdk.R;
import com.bandyer.demo_android_sdk.utils.storage.ConfigurationPrefsManager;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.squareup.picasso.Picasso;

import butterknife.BindView;

import static android.text.Spanned.SPAN_INCLUSIVE_INCLUSIVE;

public abstract class CollapsingToolbarActivity extends BaseActivity {

    @BindView(R.id.refreshUsers)
    public SwipeRefreshLayout refreshUsers;

    @BindView(R.id.info)
    public TextView info;

    @BindView(R.id.loading)
    public ProgressBar loading;

    @BindView(R.id.appbar_toolbar)
    public AppBarLayout appBarLayout;

    @BindView(R.id.collapsing_toolbar)
    CollapsingToolbarLayout collapsingToolbarLayout;

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @BindView(R.id.fader)
    View fader;

    @BindView(R.id.header)
    ImageView header;

    protected SearchView searchView;

    protected String appTitle;

    protected int textSizeH1;
    protected int textSizeH3;
    protected int textSizeH4;

    protected SpannableString titleSpan;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        textSizeH1 = getResources().getDimensionPixelSize(R.dimen.text_h1);
        textSizeH3 = getResources().getDimensionPixelSize(R.dimen.text_h3);
        textSizeH4 = getResources().getDimensionPixelSize(R.dimen.text_h4);
        titleSpan = new SpannableString(String.format(getResources().getString(R.string.app_name_with_version), "v" + com.bandyer.android_sdk.BuildConfig.VERSION_NAME));
        titleSpan.setSpan(new AbsoluteSizeSpan(textSizeH1), 0, titleSpan.length(), SPAN_INCLUSIVE_INCLUSIVE);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void setContentView(int layoutResID) {
        appTitle = String.format(getResources().getString(R.string.app_name_with_version), "v" + com.bandyer.android_sdk.BuildConfig.VERSION_NAME);
        LayoutInflater inflater = LayoutInflater.from(this);
        View container = inflater.inflate(R.layout.activity_collapsing_toolbar, null);
        CoordinatorLayout coordinatorLayout = container.findViewById(R.id.main_view);
        inflater.inflate(layoutResID, coordinatorLayout);
        super.setContentView(container);
        Picasso.get().load(R.drawable.landing_collaboration).into(header);
        CoordinatorLayout.LayoutParams layoutParams = new CoordinatorLayout.LayoutParams(CoordinatorLayout.LayoutParams.MATCH_PARENT, CoordinatorLayout.LayoutParams.MATCH_PARENT);
        layoutParams.setBehavior(new AppBarLayout.ScrollingViewBehavior(this, null));
        findViewById(R.id.content).setLayoutParams(layoutParams);
        customizeSwipeRefreshLayout();
        customizeAppBarLayout();
    }

    @Override
    protected void onResume() {
        super.onResume();
        getWindow().setBackgroundDrawable(new ColorDrawable(Color.WHITE));
    }

    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE)
            appBarLayout.setExpanded(false);
        else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT)
            if (searchView != null && searchView.isIconified() == true)
                appBarLayout.setExpanded(true);
    }

    public void setCollapsingToolbarTitle(String title) {
        SpannableString envTextView = new SpannableString("\n@" + ConfigurationPrefsManager.getEnvironmentName(this));
        envTextView.setSpan(new AbsoluteSizeSpan(textSizeH4), 0, envTextView.length(), SPAN_INCLUSIVE_INCLUSIVE);
        SpannableString infoSpan = new SpannableString("\n\n" + title);
        infoSpan.setSpan(new AbsoluteSizeSpan(textSizeH3), 0, infoSpan.length(), SPAN_INCLUSIVE_INCLUSIVE);
        CharSequence collapsingToolbarTitle = TextUtils.concat(titleSpan, envTextView, infoSpan);
        info.setText(collapsingToolbarTitle);
    }

    private void customizeSwipeRefreshLayout() {
        refreshUsers.setColorSchemeColors(
                ContextCompat.getColor(this, R.color.colorPrimaryDark),
                ContextCompat.getColor(this, R.color.colorPrimary),
                ContextCompat.getColor(this, R.color.colorPrimaryLight)
        );
    }

    private void customizeAppBarLayout() {
        setSupportActionBar(toolbar);
        toolbar.setTitle(appTitle);
        getSupportActionBar().setHomeButtonEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        getSupportActionBar().setDisplayShowHomeEnabled(false);

        appBarLayout.addOnOffsetChangedListener((appBarLayout, verticalOffset) -> {
            if (refreshUsers.isRefreshing() && verticalOffset == 0)
                refreshUsers.setRefreshing(false);
            refreshUsers.setEnabled(verticalOffset == 0);
            if (Math.abs(verticalOffset) - appBarLayout.getTotalScrollRange() == 0) {
                collapsingToolbarLayout.setTitle(appTitle);
                toolbar.setBackground(new ColorDrawable(ContextCompat.getColor(this, R.color.colorPrimary)));
            } else {
                collapsingToolbarLayout.setTitle("");
                toolbar.setBackground(ContextCompat.getDrawable(this, R.drawable.dark_gradient));
            }
            float offsetAlpha = 1 - (appBarLayout.getY() / appBarLayout.getTotalScrollRange()) * -1;
            fader.setAlpha(1 - offsetAlpha);
            toolbar.getBackground().setAlpha((int) (255 * offsetAlpha));
        });
    }
}