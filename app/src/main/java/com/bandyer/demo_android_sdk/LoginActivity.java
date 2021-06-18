/*
 * Copyright (C) 2020 Bandyer S.r.l. All Rights Reserved.
 * See LICENSE.txt for licensing information
 */

package com.bandyer.demo_android_sdk;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.ImageView;

import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.bandyer.app_configuration.external_configuration.activities.ConfigurationActivity;
import com.bandyer.app_configuration.external_configuration.model.Configuration;
import com.bandyer.app_utilities.BuildConfig;
import com.bandyer.app_utilities.activities.CollapsingToolbarActivity;
import com.bandyer.app_utilities.adapter_items.UserItem;
import com.bandyer.app_utilities.networking.MockedNetwork;
import com.bandyer.app_utilities.storage.ConfigurationPrefsManager;
import com.bandyer.app_utilities.storage.LoginManager;
import com.bandyer.demo_android_sdk.databinding.ActivityLoginBinding;
import com.google.android.material.appbar.AppBarLayout;
import com.mikepenz.fastadapter.FastAdapter;
import com.mikepenz.fastadapter.IAdapter;
import com.mikepenz.fastadapter.adapters.ItemAdapter;
import com.mikepenz.fastadapter.extensions.ExtensionsFactories;
import com.mikepenz.fastadapter.listeners.ItemFilterListener;
import com.mikepenz.fastadapter.select.SelectExtension;
import com.mikepenz.fastadapter.select.SelectExtensionFactory;

import java.util.ArrayList;
import java.util.List;

import kotlin.jvm.functions.Function4;

/**
 * This activity will allow you to choose a user from your company to use to interact with other users.
 * <p>
 * The list of users you can choose from will be displayed using the FastAdapter library to populate the  RecyclerView
 * <p>
 * For more information about how it works FastAdapter:
 * https://github.com/mikepenz/FastAdapter
 */
public class LoginActivity extends CollapsingToolbarActivity implements SearchView.OnQueryTextListener {

    private ActivityLoginBinding binding;

    SearchView searchView;

    private ItemAdapter<UserItem> itemAdapter = ItemAdapter.items();
    private FastAdapter<UserItem> fastAdapter = FastAdapter.with(itemAdapter);

    // the userAlias is the identifier of the created user via Bandyer-server restCall see https://docs.bandyer.com/Bandyer-RESTAPI/#create-user
    private String userAlias = "";

    private ArrayList<UserItem> usersList = new ArrayList<UserItem>();

    public static void show(Activity context) {
        Intent intent = new Intent(context, LoginActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_login);
        binding = com.bandyer.demo_android_sdk.databinding.ActivityLoginBinding.bind(getWindow().getDecorView());

        ImageView header = findViewById(R.id.headerView);
        if (!BuildConfig.DEBUG) header.setImageResource(R.drawable.landing_image);

        // customize toolbar
        setCollapsingToolbarTitle(getResources().getString(R.string.login_title));

        // set the recyclerView
        binding.listUsers.setAdapter(fastAdapter);
        binding.listUsers.setItemAnimator(null);
        binding.listUsers.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        binding.listUsers.setLayoutManager(new LinearLayoutManager(this));

        ExtensionsFactories.INSTANCE.register(new SelectExtensionFactory());
        SelectExtension<UserItem> selectExtension = fastAdapter.getOrCreateExtension(SelectExtension.class);
        selectExtension.setSelectable(true);
        fastAdapter.setOnPreClickListener(new Function4<View, IAdapter<UserItem>, UserItem, Integer, Boolean>() {
            @Override
            public Boolean invoke(View view, IAdapter<UserItem> userItemIAdapter, UserItem userItem, Integer integer) {
                userAlias = userItem.userAlias;
                if (!LoginManager.isUserLogged(LoginActivity.this))
                    LoginManager.login(LoginActivity.this, userAlias);
                MainActivity.show(LoginActivity.this);
                finish();
                return false;
            }
        });

        itemAdapter.getItemFilter().setFilterPredicate((userSelectionItem, constraint) -> userSelectionItem.userAlias.toLowerCase().contains(constraint.toString().toLowerCase()));
        itemAdapter.getItemFilter().setItemFilterListener(new ItemFilterListener<UserItem>() {
            @Override
            public void itemsFiltered(@org.jetbrains.annotations.Nullable CharSequence charSequence, @org.jetbrains.annotations.Nullable List<? extends UserItem> list) {
                if (list.size() > 0)
                    binding.noResults.setVisibility(View.GONE);
                else
                    binding.noResults.setVisibility(View.VISIBLE);
            }

            @Override
            public void onReset() {
                binding.noResults.setVisibility(View.GONE);

            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        // the userAlias is the identifier of the created user via Bandyer-server restCall see https://docs.bandyer.com/Bandyer-RESTAPI/#create-user
        userAlias = LoginManager.getLoggedUser(this);

        // If the user is already logged init the call client and do not fetch the sample users again.
        if (userAlias != null && !userAlias.trim().isEmpty()) {
            MainActivity.show(LoginActivity.this);
            return;
        }

        if (usersList.isEmpty()) onRefresh();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.login, menu);
        searchView = (SearchView) menu.findItem(R.id.searchLogin).getActionView();
        searchView.setOnSearchClickListener(v -> ((AppBarLayout) findViewById(R.id.appbar_toolbar)).setExpanded(false, true));
        searchView.setQueryHint(getString(R.string.search));
        searchView.setOnQueryTextListener(this);
        menu.findItem(R.id.action_settings).setOnMenuItemClickListener(item -> {
            Configuration configuration = ConfigurationPrefsManager.INSTANCE.getConfiguration(this);
            ConfigurationActivity.Companion.show(this, configuration, configuration.isMockConfiguration());
            return true;
        });
        return true;
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        itemAdapter.filter(newText);
        return true;
    }

    @Override
    public void onRefresh() {
        itemAdapter.clear();
        binding.loading.setVisibility(View.VISIBLE);
        // Fetch the sample users you can use to login with.
        MockedNetwork.getSampleUsers(this, new MockedNetwork.GetBandyerUsersCallback() {
            @Override
            public void onUsers(List<String> users) {
                binding.loading.setVisibility(View.GONE);
                usersList.clear();
                // Add each user(except the logged one) to the recyclerView adapter to be displayed in the list.
                for (String user : users) usersList.add(new UserItem(user));
                setRefreshing(false);
                itemAdapter.set(usersList);
                if (searchView != null) itemAdapter.filter(searchView.getQuery());
            }

            @Override
            public void onError(String error) {
                binding.loading.setVisibility(View.GONE);
                showErrorDialog(error);
                setRefreshing(false);
                itemAdapter.clear();
            }
        });
    }
}
