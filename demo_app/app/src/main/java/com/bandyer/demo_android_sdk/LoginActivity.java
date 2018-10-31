/*
 * Copyright (C) 2018 Bandyer S.r.l. All Rights Reserved.
 * See LICENSE.txt for licensing information
 */

package com.bandyer.demo_android_sdk;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;

import com.bandyer.android_common.fetcher.UserDisplayInfo;
import com.bandyer.android_common.fetcher.UserDisplayInfoFormatter;
import com.bandyer.android_sdk.client.BandyerSDKClient;
import com.bandyer.android_sdk.client.BandyerSDKClientOptions;
import com.bandyer.android_sdk.client.BandyerSDKClientState;
import com.bandyer.android_sdk.notification.BandyerSDKNotificationConfig;
import com.bandyer.demo_android_sdk.adapter_items.UserItem;
import com.bandyer.demo_android_sdk.dummy.DummyUserFetcher;
import com.bandyer.demo_android_sdk.utils.LoginManager;
import com.bandyer.demo_android_sdk.utils.networking.BandyerUsers;
import com.bandyer.demo_android_sdk.utils.networking.MockedNetwork;
import com.mikepenz.fastadapter.FastAdapter;
import com.mikepenz.fastadapter.IAdapter;
import com.mikepenz.fastadapter.adapters.ItemAdapter;
import com.mikepenz.fastadapter.listeners.OnClickListener;

import javax.annotation.Nullable;

import butterknife.BindView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * This activity will allow you to choose a user from your company to use to interact with other users.
 * <p>
 * The list of users you can choose from will be displayed using the FastAdapter library to populate the  RecyclerView
 * <p>
 * For more information about how it works FastAdapter:
 * https://github.com/mikepenz/FastAdapter
 */
public class LoginActivity extends BaseActivity implements OnClickListener<UserItem> {

    @BindView(R.id.list_users)
    RecyclerView listUsers;

    private ItemAdapter<UserItem> itemAdapter = new ItemAdapter<>();
    private FastAdapter<UserItem> fastAdapter = FastAdapter.with(itemAdapter);

    // the userAlias is the identifier of the created user via Bandyer-server restCall see https://docs.bandyer.com/Bandyer-RESTAPI/#create-user
    private String userAlias = "";

    public static void show(Activity context) {
        Intent intent = new Intent(context, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // set the recyclerView
        listUsers.setAdapter(fastAdapter);
        listUsers.setLayoutManager(new LinearLayoutManager(this));
        fastAdapter.withOnClickListener(this);
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

        itemAdapter.clear();

        // Fetch the sample users you can use to login with.
        MockedNetwork.getSampleUsers(this, new Callback<BandyerUsers>() {
            @Override
            public void onResponse(@NonNull Call<BandyerUsers> call, @NonNull Response<BandyerUsers> response) {
                if (response.body() == null || response.body().user_id_list == null) {
                    showErrorDialog("Please check if you have provided the correct keys in the configuration.xml");
                    return;
                }
                // Add each user to the recyclerView adapter to be displayed in the list.

                for (String user : response.body().user_id_list)
                    itemAdapter.add(new UserItem(user));
            }

            @Override
            public void onFailure(@NonNull Call<BandyerUsers> call, @NonNull Throwable t) {
                // If contacts could not be fetched show error dialog
                showErrorDialog(t.getMessage());
            }

        });
    }

    /**
     * On click on a user from the list init the call client for that user
     * save the userAlias to be used for login after the call client has been initialized
     */
    @Override
    public boolean onClick(@Nullable View v, @NonNull IAdapter<UserItem> adapter,
                           @NonNull final UserItem item, int position) {
        userAlias = item.userAlias;
        if (!LoginManager.isUserLogged(this))
            LoginManager.login(this, userAlias);
        MainActivity.show(LoginActivity.this);
        return false;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
