/*
 * Copyright (C) 2019 Bandyer S.r.l. All Rights Reserved.
 * See LICENSE.txt for licensing information
 */

package com.bandyer.demo_android_sdk;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;

import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;

import com.bandyer.demo_android_sdk.adapter_items.UserItem;
import com.bandyer.demo_android_sdk.settings.ConfigurationActivity;
import com.bandyer.demo_android_sdk.utils.activities.CollapsingToolbarActivity;
import com.bandyer.demo_android_sdk.utils.storage.LoginManager;
import com.bandyer.demo_android_sdk.utils.networking.MockedNetwork;
import com.mikepenz.fastadapter.FastAdapter;
import com.mikepenz.fastadapter.IAdapter;
import com.mikepenz.fastadapter.adapters.ItemAdapter;
import com.mikepenz.fastadapter.listeners.ItemFilterListener;
import com.mikepenz.fastadapter.listeners.OnClickListener;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nullable;

import butterknife.BindView;

/**
 * This activity will allow you to choose a user from your company to use to interact with other users.
 * <p>
 * The list of users you can choose from will be displayed using the FastAdapter library to populate the  RecyclerView
 * <p>
 * For more information about how it works FastAdapter:
 * https://github.com/mikepenz/FastAdapter
 */
public class LoginActivity extends CollapsingToolbarActivity implements OnClickListener<UserItem>, SearchView.OnQueryTextListener, SwipeRefreshLayout.OnRefreshListener {

    @BindView(R.id.list_users)
    RecyclerView listUsers;

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @BindView(R.id.no_results)
    View noFilterResults;

    private SearchView searchView;

    private ItemAdapter<UserItem> itemAdapter = ItemAdapter.items();
    private FastAdapter<UserItem> fastAdapter = FastAdapter.with(itemAdapter);

    // the userAlias is the identifier of the created user via Bandyer-server restCall see https://docs.bandyer.com/Bandyer-RESTAPI/#create-user
    private String userAlias = "";

    private ArrayList<UserItem> usersList = new ArrayList<UserItem>();

    public static void show(Activity context) {
        Intent intent = new Intent(context, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        context.getApplication().startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_login);

        // customize toolbar
        setCollapsingToolbarTitle(getResources().getString(R.string.login_title));

        // set the recyclerView
        listUsers.setAdapter(fastAdapter);
        listUsers.setItemAnimator(null);
        listUsers.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        listUsers.setLayoutManager(new LinearLayoutManager(this));
        fastAdapter.withSelectable(true);
        fastAdapter.withOnPreClickListener(this);

        itemAdapter.getItemFilter().withFilterPredicate((userSelectionItem, constraint) -> userSelectionItem.userAlias.toLowerCase().contains(constraint.toString().toLowerCase()));

        itemAdapter.getItemFilter().withItemFilterListener(new ItemFilterListener<UserItem>() {
            @Override
            public void itemsFiltered(@Nullable CharSequence constraint, @Nullable List<UserItem> results) {
                if (results.size() > 0)
                    noFilterResults.setVisibility(View.GONE);
                else
                    noFilterResults.setVisibility(View.VISIBLE);
            }

            @Override
            public void onReset() {
                noFilterResults.setVisibility(View.GONE);
            }
        });

        refreshUsers.setOnRefreshListener(this);
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

        onRefresh();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.login, menu);
        searchView = (SearchView) menu.findItem(R.id.searchLogin).getActionView();
        searchView.setOnSearchClickListener(v -> appBarLayout.setExpanded(false, true));
        searchView.setQueryHint(getString(R.string.search));
        searchView.setOnQueryTextListener(this);
        menu.findItem(R.id.action_settings).setOnMenuItemClickListener(item -> {
            ConfigurationActivity.show(this);
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
        loading.setVisibility(View.VISIBLE);
        // Fetch the sample users you can use to login with.
        MockedNetwork.getSampleUsers(this, new MockedNetwork.GetBandyerUsersCallback() {
            @Override
            public void onUsers(List<String> users) {
                loading.setVisibility(View.GONE);
                usersList.clear();
                // Add each user(except the logged one) to the recyclerView adapter to be displayed in the list.
                for (String user : users) usersList.add(new UserItem(user));
                refreshUsers.setRefreshing(false);
                itemAdapter.set(usersList);
                if (searchView != null) itemAdapter.filter(searchView.getQuery());
            }

            @Override
            public void onError(String error) {
                loading.setVisibility(View.GONE);
                showErrorDialog(error);
                refreshUsers.setRefreshing(false);
                itemAdapter.clear();
            }
        });
    }

    /**
     * On click on a user from the list init the call client for that user
     * save the userAlias to be used for login after the call client has been initialized
     */
    @Override
    public boolean onClick(@Nullable View v, @NonNull IAdapter<UserItem> adapter, @NonNull final UserItem item, int position) {
        userAlias = item.userAlias;
        if (!LoginManager.isUserLogged(this))
            LoginManager.login(this, userAlias);
        MainActivity.show(LoginActivity.this);
        return false;
    }
}
