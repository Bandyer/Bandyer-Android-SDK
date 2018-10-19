/*
 * Copyright (C) 2018 Bandyer S.r.l. All Rights Reserved.
 * See LICENSE.txt for licensing information
 */

package com.bandyer.demo_android_sdk;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bandyer.android_common.extensions.ContextExtensionsKt;
import com.bandyer.android_common.fetcher.UserDisplayInfo;
import com.bandyer.android_common.fetcher.UserDisplayInfoFormatter;
import com.bandyer.android_sdk.client.BandyerSDKClient;
import com.bandyer.android_sdk.client.BandyerSDKClientObserver;
import com.bandyer.android_sdk.client.BandyerSDKClientOptions;
import com.bandyer.android_sdk.client.BandyerSDKClientState;
import com.bandyer.android_sdk.intent.BandyerIntent;
import com.bandyer.android_sdk.notification.BandyerSDKNotificationConfig;
import com.bandyer.demo_android_sdk.adapter_items.UserSelectionItem;
import com.bandyer.demo_android_sdk.dummy.DummyUserFetcher;
import com.bandyer.demo_android_sdk.utils.LoginManager;
import com.bandyer.demo_android_sdk.utils.networking.BandyerUsers;
import com.bandyer.demo_android_sdk.utils.networking.MockedNetwork;
import com.mikepenz.fastadapter.IAdapter;
import com.mikepenz.fastadapter.commons.adapters.FastItemAdapter;
import com.mikepenz.fastadapter.listeners.OnClickListener;
import com.mikepenz.fastadapter.select.SelectExtension;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * This Activity will be called after the user has logged or if an external url was opened with this app.
 * It's main job is to redirect to the dialing(outgoing) or ringing(ringing) call activities.
 *
 * @author kristiyan
 */
public class MainActivity extends BaseActivity implements BandyerSDKClientObserver {

    private final int START_CHAT_CODE = 123;
    private final int START_CALL_CODE = 124;

    private FastItemAdapter<UserSelectionItem> fastAdapter;
    private List<String> calleeSelected;

    @BindView(R.id.contactsList)
    RecyclerView listContacts;

    @BindView(R.id.chat)
    FloatingActionButton chatButton;

    @BindView(R.id.call)
    FloatingActionButton callButton;

    private ProgressDialog dialog;

    private boolean shouldOpenExternalUrl = false;

    // the external url to provide to the call client in case we want to setup a call coming from an url.
    // The url may be provided to join an existing call, or to create a new one.
    private String joinUrl;

    private Snackbar snackbar;

    public static void show(Activity context) {
        Intent intent = new Intent(context, MainActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        snackbar = Snackbar.make(findViewById(R.id.main_view), "Initializing Bandyer SDK modules ...", Snackbar.LENGTH_INDEFINITE);
        snackbar.show();

        Drawable chatDrawable = ContextCompat.getDrawable(this, R.drawable.ic_bandyer_chat).mutate();
        chatDrawable.setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_IN);
        chatButton.setImageDrawable(chatDrawable);


        Drawable callDrawable = ContextCompat.getDrawable(this, R.drawable.ic_bandyer_video_call).mutate();
        callDrawable.setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_IN);
        callButton.setImageDrawable(callDrawable);

        // if no valid user exists, delete all the preferences and show the LoginActivity
        // else setup the recycler view
        if (!LoginManager.isUserLogged(this)) logout();
        else setUpRecyclerView();

        // get the user that is currently logged in the sample app
        String userAlias = LoginManager.getLoggedUser(this);

        // set a title greeting the logged user
        TextView userGreeting = findViewById(R.id.userGreeting);
        userGreeting.setText(String.format(getResources().getString(R.string.pick_users), userAlias));

        startBandyerSdk(userAlias);
    }

    private void startBandyerSdk(String userAlias) {
        if (userAlias == null || userAlias.trim().isEmpty()) return;

        if (!LoginManager.isUserLogged(this))
            LoginManager.login(this, userAlias);

        // If the user is already logged, initialize the SDK client and show the MainActivity.
        // Bandyer SDK optional components builder user to retrieve and display users' info
        BandyerSDKClientOptions options = new BandyerSDKClientOptions.Builder()
                .withUserInformationFetcher(new DummyUserFetcher())
                .withNotificationDisplayFormatter(
                        new UserDisplayInfoFormatter() {
                            @NotNull
                            @Override
                            public String format(@NotNull UserDisplayInfo userDisplayInfo) {
                                return userDisplayInfo.getNickName() + " " + userDisplayInfo.getEmail();
                            }
                        }
                )
                .withNotificationConfig(
                        new BandyerSDKNotificationConfig.Builder()
                                .setNotificationSmallIcon(R.drawable.ic_bandyer_notification)
                                .setNotificationColor(R.drawable.bandyer_selected_item_color)
                                .setIncomingCallSmallIcon(R.drawable.ic_bandyer_audio_call)
                                .build()
                )
                .keepListeningforIncomingCallsInBackground(false)
                .build();

        // If the user is already logged, initialize the SDK client and show the MainActivity.
        BandyerSDKClient.getInstance().init(this.getApplicationContext(), userAlias, MainActivity.this, options);

        // Start listening for events
        BandyerSDKClient.getInstance().startListening();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        BandyerSDKClient.getInstance().removeObserver(this);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
    }

    @Override
    public void onBackPressed() {
        logout();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main, menu);
        // return true so that the menu pop up is opened
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.logout:
                logout();
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void logout() {
        LoginManager.logout(this);
        BandyerSDKClient.getInstance().dispose();
        LoginActivity.show(this);
    }

    private void setUpRecyclerView() {
        calleeSelected = new ArrayList<>();

        fastAdapter = new FastItemAdapter<>();
        fastAdapter.withSelectable(true);

        fastAdapter.clear();

        // Fetch the sample users you can use to login with.
        MockedNetwork.getSampleUsers(this, new Callback<BandyerUsers>() {

            @Override
            public void onResponse(@NonNull retrofit2.Call<BandyerUsers> call, @NonNull Response<BandyerUsers> response) {
                // Add each user(except the logged one) to the recyclerView adapter to be displayed in the list.
                if (response.body() == null || response.body().user_id_list == null)
                    return;

                for (String user : response.body().user_id_list)
                    if (!user.equals(LoginManager.getLoggedUser(MainActivity.this)))
                        fastAdapter.add(new UserSelectionItem(user));
            }

            @Override
            public void onFailure(@NonNull retrofit2.Call<BandyerUsers> call, @NonNull Throwable t) {
                // If contacts could not be fetched show error dialog.
                showErrorDialog(t.getMessage());
            }

        });

        // on user selection put in a list to be called on click on call button.
        fastAdapter.withOnPreClickListener(new OnClickListener<UserSelectionItem>() {
            @Override
            public boolean onClick(@Nullable View v, @NonNull IAdapter<UserSelectionItem> adapter, @NonNull UserSelectionItem item, int position) {
                SelectExtension<UserSelectionItem> selectExtension = fastAdapter.getExtension(SelectExtension.class);
                if (selectExtension != null) {
                    selectExtension.toggleSelection(position);
                }

                if (!item.isSelected())
                    calleeSelected.remove(item.name);
                else
                    calleeSelected.add(item.name);
                return true;
            }
        });

        listContacts.setLayoutManager(new LinearLayoutManager(this));
        listContacts.setAdapter(fastAdapter);
    }

    /**
     * This is how a chat is started. You must provide one users alias identifying the user your user wants to communicate with.
     * Starting a chat is an asynchronous process, failure or success is reported in the callback provided.
     * <p>
     * WARNING!!!
     * Be aware that all the observers in this SDK, MUST NOT be defined as anonymous class because the call client will have a weak reference to them to avoid leaks and other scenarios.
     * If you do implement the observer anonymously the methods may not be called.
     */
    @OnClick(R.id.chat)
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    void chat() {
        if (calleeSelected.size() == 0) {
            showErrorDialog(getResources().getString(R.string.oto_chat_error_no_selected_user));
            return;
        } else if (calleeSelected.size() > 1) {
            showErrorDialog(getResources().getString(R.string.oto_chat_error));
            return;
        }

        Intent chatIntent = new BandyerIntent.Builder(MainActivity.this)
                .startWithChat()
                .withAudioCallCapability(false)
                .withAudioVideoCallCapability()
                .withUserAlias(calleeSelected.get(0))
                .withUserDisplayFormatter(new UserDisplayInfoFormatter() {
                    @NotNull
                    @Override
                    public String format(@NotNull UserDisplayInfo userDisplayInfo) {
                        return userDisplayInfo.getNickName() + " " + userDisplayInfo.getEmail();
                    }
                })
                .build();

        startActivityForResult(chatIntent, START_CHAT_CODE);
    }

    /**
     * This is how a call is started. You must provide one users alias identifying the user your user wants to communicate with.
     * Starting a chat is an asynchronous process, failure or success is reported in the callback provided.
     * <p>
     * WARNING!!!
     * Be aware that all the observers in this SDK, MUST NOT be defined as anonymous class because the call client will have a weak reference to them to avoid leaks and other scenarios.
     * If you do implement the observer anonymously the methods may not be called.
     */
    @OnClick(R.id.call)
    void call() {

        final CharSequence[] callModes = {"Audio only call", "Audio upgradable to video call", "Audio video call"};
        AlertDialog.Builder mBuilder = new AlertDialog.Builder(MainActivity.this);
        mBuilder.setTitle("Call " + TextUtils.join(" ", calleeSelected));
        final CheckBox recording = new CheckBox(this);
        LinearLayout ll = new LinearLayout(this);
        LinearLayout.LayoutParams llp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        int margin =  dp2px(16f);
        llp.leftMargin = margin;
        llp.topMargin = margin / 2;
        llp.bottomMargin = margin;
        ll.addView(recording, llp);
        recording.setText("Record call");
        mBuilder.setView(ll);
        mBuilder.setSingleChoiceItems(callModes, -1, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                BandyerIntent.Builder.CallIntentBuilder builder = new BandyerIntent.Builder(MainActivity.this)
                        .startWithCall()
                        .withUserAliases(new ArrayList<>(calleeSelected))
                        .withUserDisplayFormatter(new UserDisplayInfoFormatter() {
                            @NotNull
                            @Override
                            public String format(@NotNull UserDisplayInfo userDisplayInfo) {
                                return userDisplayInfo.getNickName() + " " + userDisplayInfo.getEmail();
                            }
                        });

                if (recording.isChecked())
                    builder.recordCall();

                switch (i) {
                    case 0:
                        builder.audioCall();
                        break;
                    case 1:
                        builder.audioUpgradableCall();
                        break;
                    case 2:
                        builder.audioVideoCall();
                        break;
                }
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                    builder.withChatCapability();
                }
                Intent callIntent = builder.build();
                dialogInterface.dismiss();
                startActivityForResult(callIntent, START_CALL_CODE);
            }
        });
        AlertDialog mDialog = mBuilder.create();
        mDialog.show();

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_CANCELED) {

            if (data == null)
                return;

            String error = data.getExtras() != null ? data.getExtras().getString("error", "error") : "error";

            switch (requestCode) {

                case START_CALL_CODE:

                    Toast.makeText(this, "Call ennded: " + error, Toast.LENGTH_LONG).show();
                    break;

                case START_CHAT_CODE:
                    Toast.makeText(this, "Chat closed: " + error, Toast.LENGTH_LONG).show();
                    break;

            }
        }
    }

    ///////////////////////////////////////////////// BANDYER SDK CLIENT OBSERVER /////////////////////////////////////////////////

    @Override
    public void onStatusChange(@NotNull BandyerSDKClientState state) {
    }

    @Override
    public void onChatModuleReady() {
        if (chatButton.getVisibility() == View.INVISIBLE) {
            Toast.makeText(MainActivity.this, "Bandyer SDK Chat Module initialized.", Toast.LENGTH_SHORT).show();
            chatButton.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onCallModuleReady() {
        if (callButton.getVisibility() == View.INVISIBLE) {
            Toast.makeText(MainActivity.this, "Bandyer SDK Call Module initialized.", Toast.LENGTH_SHORT).show();
            callButton.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onAllModulesReady() {
        if (snackbar.isShown()) {
            snackbar.dismiss();
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT)
                chatButton.setVisibility(View.VISIBLE);
            callButton.setVisibility(View.VISIBLE);

            RelativeLayout.LayoutParams callButtonLp = (RelativeLayout.LayoutParams) callButton.getLayoutParams();
            RelativeLayout.LayoutParams chatButtonLp = (RelativeLayout.LayoutParams) chatButton.getLayoutParams();
            callButtonLp.bottomMargin = dp2px(16f);
            chatButtonLp.bottomMargin = dp2px(16f);
            callButton.setLayoutParams(callButtonLp);
            chatButton.setLayoutParams(chatButtonLp);
        }
    }

    int dp2px(float dp) {
        DisplayMetrics metrics = getResources().getDisplayMetrics();
        return (int) (dp * (metrics.densityDpi / DisplayMetrics.DENSITY_DEFAULT));
    }

    @Override
    public void onCallModuleFailed(Throwable throwable) {
        Toast.makeText(MainActivity.this, throwable.getMessage(), Toast.LENGTH_SHORT).show();
        callButton.setVisibility(View.INVISIBLE);
    }

    @Override
    public void onChatModuleFailed(@NotNull Throwable throwable) {
        Toast.makeText(MainActivity.this, throwable.getMessage(), Toast.LENGTH_SHORT).show();
        chatButton.setVisibility(View.INVISIBLE);
    }

}
