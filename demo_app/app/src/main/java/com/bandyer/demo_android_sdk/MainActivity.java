/*
 * Copyright (C) 2018 Bandyer S.r.l. All Rights Reserved.
 * See LICENSE.txt for licensing information
 */

package com.bandyer.demo_android_sdk;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.design.widget.FloatingActionButton;
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

import com.bandyer.android_sdk.call.CallModule;
import com.bandyer.android_sdk.call.receiver.CallStatusEventObserver;
import com.bandyer.android_sdk.call.receiver.CallStatusListener;
import com.bandyer.android_sdk.chat.ChatModule;
import com.bandyer.android_sdk.client.BandyerSDKClient;
import com.bandyer.android_sdk.client.BandyerSDKClientObserver;
import com.bandyer.android_sdk.client.BandyerSDKClientOptions;
import com.bandyer.android_sdk.client.BandyerSDKClientState;
import com.bandyer.android_sdk.intent.BandyerIntent;
import com.bandyer.android_sdk.intent.call.CallIntentBuilder;
import com.bandyer.android_sdk.intent.call.CallIntentOptions;
import com.bandyer.android_sdk.module.BandyerModule;
import com.bandyer.android_sdk.module.BandyerModuleObserver;
import com.bandyer.android_sdk.module.BandyerModuleStatus;
import com.bandyer.demo_android_sdk.adapter_items.UserSelectionItem;
import com.bandyer.demo_android_sdk.utils.LoginManager;
import com.bandyer.demo_android_sdk.utils.networking.MockedNetwork;
import com.mikepenz.fastadapter.IAdapter;
import com.mikepenz.fastadapter.commons.adapters.FastItemAdapter;
import com.mikepenz.fastadapter.listeners.OnClickListener;
import com.mikepenz.fastadapter.select.SelectExtension;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * This Activity will be called after the user has logged or if an external url was opened with this app.
 * It's main job is to redirect to the dialing(outgoing) or ringing(ringing) call activities.
 *
 * @author kristiyan
 */
public class MainActivity extends BaseActivity implements BandyerSDKClientObserver, BandyerModuleObserver {

    private final int START_CHAT_CODE = 123;
    private final int START_CALL_CODE = 124;

    private FastItemAdapter<UserSelectionItem> fastAdapter;
    private List<String> calleeSelected;

    private AlertDialog chooseCallDialog;

    @BindView(R.id.contactsList)
    RecyclerView listContacts;

    @BindView(R.id.chat)
    FloatingActionButton chatButton;

    @BindView(R.id.call)
    FloatingActionButton callButton;

    // the external url to provide to the call client in case we want to setup a call coming from an url.
    // The url may be provided to join an existing call, or to create a new one.
    private String joinUrl;

    public static void show(Activity context) {
        Intent intent = new Intent(context, MainActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (!LoginManager.isUserLogged(this)) return;

        setContentView(R.layout.activity_main);

        // get the user that is currently logged in the sample app
        String userAlias = LoginManager.getLoggedUser(this);

        // set a title greeting the logged user
        TextView userGreeting = findViewById(R.id.userGreeting);
        userGreeting.setText(String.format(getResources().getString(R.string.pick_users), userAlias));

        final TextView ongoingCallLabel = findViewById(R.id.ongoing_call_label);

        // in case the MainActivity has been shown by opening an external link, handle it
        handleExternalUrl(getIntent());

        ongoingCallLabel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BandyerSDKClient.getInstance().resumeCallActivity();
            }
        });

        CallStatusEventObserver.getInstance().observeCallStatus(this, new CallStatusListener() {
            @Override
            public void onCallStarted() {
                ongoingCallLabel.getLayoutParams().height = RelativeLayout.LayoutParams.WRAP_CONTENT;
                ongoingCallLabel.requestLayout();
            }

            @Override
            public void onCallEnded() {
                ongoingCallLabel.getLayoutParams().height = 0;
                ongoingCallLabel.requestLayout();
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();

        BandyerSDKClient.getInstance().removeObserver(this);
        BandyerSDKClient.getInstance().removeModuleObserver(this);

        if (chooseCallDialog != null) chooseCallDialog.dismiss();
        chooseCallDialog = null;
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!LoginManager.isUserLogged(this)) {
            LoginActivity.show(this);
            return;
        }
        // If the user is already logged, setup the activity.
        setUpRecyclerView();

        BandyerSDKClient.getInstance().addObserver(this);
        BandyerSDKClient.getInstance().addModuleObserver(this);

        startBandyerSdk(LoginManager.getLoggedUser(this));
    }

    private void startBandyerSdk(String userAlias) {
        Log.d("MainActivity", "startBandyerSDK");

        if (BandyerSDKClient.getInstance().getState() != BandyerSDKClientState.UNINITIALIZED)
            return;

        if (chatButton != null) chatButton.setEnabled(false);
        if (callButton != null) callButton.setEnabled(false);

        BandyerSDKClientOptions options = new BandyerSDKClientOptions.Builder()
                .keepListeningForEventsInBackground(false)
                .build();
        BandyerSDKClient.getInstance().init(userAlias, options);

        // Start listening for events
        BandyerSDKClient.getInstance().startListening();
    }

    /**
     * Handle an external url by calling join method
     * <p>
     * WARNING!!!
     * Be sure to have the call client connected before joining a call with the url provided.
     * Otherwise you will receive an error.
     */
    private void handleExternalUrl(Intent intent) {
        // do not handle the url if we do not have a valid user
        if (!LoginManager.isUserLogged(this)) return;

        String userAlias = LoginManager.getLoggedUser(this);

        if (Intent.ACTION_VIEW.equals(intent.getAction())) {
            Uri uri = intent.getData();
            if (uri != null) {
                joinUrl = uri.toString();
                // if client is not running, then I need to initialize it
                if (BandyerSDKClient.getInstance().getState() == BandyerSDKClientState.UNINITIALIZED) {
                    startBandyerSdk(userAlias);
                } else if (BandyerSDKClient.getInstance().getState() == BandyerSDKClientState.RUNNING) {
                    BandyerIntent bandyerIntent = new BandyerIntent.Builder()
                            .startFromJoinCallUrl(this, joinUrl)
                            .withChatCapability()
                            .withWhiteboardCapability()
                            .build();

                    startActivityForResult(bandyerIntent, START_CALL_CODE);
                    joinUrl = null;
                }
            }
        }
    }

    @Override
    public void onDestroy() {
        BandyerSDKClient.getInstance().dispose();
        super.onDestroy();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        handleExternalUrl(intent);
    }

    @Override
    public void onBackPressed() {
        logout();
        super.onBackPressed();
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
        BandyerSDKClient.getInstance().clearUserCache();
        BandyerSDKClient.getInstance().dispose();
        LoginActivity.show(this);
    }

    private void setUpRecyclerView() {
        if (fastAdapter != null && fastAdapter.getItemCount() > 0) return;

        calleeSelected = new ArrayList<>();

        fastAdapter = new FastItemAdapter<>();
        fastAdapter.withSelectable(true);


        // Fetch the sample users you can use to login with.
        MockedNetwork.getSampleUsers(this, new MockedNetwork.GetBandyerUsersCallback() {
            @Override
            public void onUsers(List<String> users) {
                // Add each user(except the logged one) to the recyclerView adapter to be displayed in the list.
                for (String user : users)
                    if (!user.equals(LoginManager.getLoggedUser(MainActivity.this)))
                        fastAdapter.add(new UserSelectionItem(user));
            }

            @Override
            public void onError(String error) {
                showErrorDialog(error);
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
        }

        if (calleeSelected.size() > 1) {
            showErrorDialog(getResources().getString(R.string.oto_chat_error_group_selected));
            return;
        }

        Intent chatIntent = new BandyerIntent.Builder()
                .startWithChat(this)
                .with(calleeSelected.get(0))
                .withAudioCallCapability(false, false)
                .withAudioVideoCallCapability(false)
                .withWhiteboardInCallCapability()
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
        if (chooseCallDialog != null) chooseCallDialog.dismiss();
        final CharSequence[] callModes = {"Audio only call", "Audio upgradable to video call", "Audio video call"};
        AlertDialog.Builder mBuilder = new AlertDialog.Builder(MainActivity.this);
        mBuilder.setTitle("Call " + TextUtils.join(" ", calleeSelected));
        final CheckBox recording = new CheckBox(this);
        LinearLayout ll = new LinearLayout(this);
        LinearLayout.LayoutParams llp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        int margin = dp2px(16f);
        llp.leftMargin = margin;
        llp.topMargin = margin / 2;
        llp.bottomMargin = margin;
        ll.addView(recording, llp);
        recording.setText("Record call");
        mBuilder.setView(ll);
        mBuilder.setSingleChoiceItems(callModes, -1, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                CallIntentBuilder callIntentBuilder = null;

                switch (i) {
                    case 0:
                        callIntentBuilder = new BandyerIntent.Builder().startWithAudioCall(MainActivity.this, recording.isChecked(), false);
                        break;
                    case 1:
                        callIntentBuilder = new BandyerIntent.Builder().startWithAudioCall(MainActivity.this, recording.isChecked(), true);
                        break;
                    case 2:
                        callIntentBuilder = new BandyerIntent.Builder().startWithAudioVideoCall(MainActivity.this, recording.isChecked());
                        break;
                }

                BandyerIntent bandyerIntent = callIntentBuilder
                        .with(new ArrayList<>(calleeSelected))
                        .withChatCapability()
                        .withWhiteboardCapability()
                        .build();

                dialogInterface.dismiss();
                startActivityForResult(bandyerIntent, START_CALL_CODE);
            }
        });

        chooseCallDialog = mBuilder.create();
        chooseCallDialog.show();
    }


    /**
     * Result received after closing a chat or a call.
     *
     * @param requestCode that started a chat or a call.
     * @param resultCode  received from chat or call.
     * @param data        not used.
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_CANCELED) {

            if (data == null)
                return;

            String error = data.getExtras() != null ? data.getExtras().getString("error", "error") : "error";

            switch (requestCode) {
                case START_CALL_CODE:
                    Log.d("MainActivity", "Call ended: " + error);
                    break;

                case START_CHAT_CODE:
                    Log.d("MainActivity", "Chat closed: " + error);
                    break;

            }
        }
    }

    ///////////////////////////////////////////////// BANDYER SDK CLIENT MODULE OBSERVER /////////////////////////////////////////////////

    @Override
    public void onModuleReady(@NonNull BandyerModule module) {
        Log.d("MainActivity", "onModuleReady " + module.getName());

        if (module instanceof ChatModule && chatButton != null) {
            chatButton.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(this, R.color.colorModuleChatOffline)));
            chatButton.setEnabled(true);
        } else if (module instanceof CallModule) {
            if (callButton != null) {
                callButton.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(this, R.color.colorModuleConnected)));
                callButton.setEnabled(true);
            }
            if (joinUrl != null) {
                CallIntentOptions optionsBuilder = new BandyerIntent.Builder()
                        .startFromJoinCallUrl(this, joinUrl)
                        .withWhiteboardCapability()
                        .withChatCapability();

                startActivityForResult(optionsBuilder.build(), START_CALL_CODE);
                joinUrl = null; // reset boolean to avoid reopening external url twice on resume
            }
        }
    }

    @Override
    public void onModulePaused(@NonNull BandyerModule module) {
        Log.d("MainActivity", "onModulePaused " + module.getName());
        if (module instanceof ChatModule && chatButton != null) {
            chatButton.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(this, R.color.colorModuleNotActive)));
            chatButton.setEnabled(false);
        } else if (module instanceof CallModule && callButton != null) {
            callButton.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(this, R.color.colorModuleNotActive)));
            callButton.setEnabled(false);
        }
    }

    @Override
    public void onModuleFailed(@NonNull final BandyerModule module, @NonNull Throwable throwable) {
        Log.e("MainActivity", "onModuleFailed " + module.getName() + " error " + throwable.getLocalizedMessage());
        if (module instanceof ChatModule && chatButton != null) {
            chatButton.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(this, R.color.colorModuleError)));
            chatButton.setEnabled(false);
        } else if (module instanceof CallModule && callButton != null) {
            callButton.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(this, R.color.colorModuleError)));
            callButton.setEnabled(false);
        }
    }

    @Override
    public void onModuleStatusChanged(@NonNull BandyerModule module, @NonNull BandyerModuleStatus moduleStatus) {
        Log.d("MainActivity", "onModuleStatusChanged " + module.getName() + " status " + moduleStatus);

        // the chat module is offline first, which means that you can interact with it even when you are not connected to internet
        // here we color the button in black until the module gets online
        if (module instanceof ChatModule && chatButton != null) {
            if (moduleStatus == BandyerModuleStatus.CONNECTED) {
                chatButton.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(this, R.color.colorModuleConnected)));
                chatButton.setEnabled(true);
            } else if (moduleStatus == BandyerModuleStatus.DISCONNECTED || moduleStatus == BandyerModuleStatus.RECONNECTING) {
                chatButton.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(this, R.color.colorModuleChatOffline)));
                chatButton.setEnabled(true);
            }
        } else if (module instanceof CallModule && callButton != null) {
            if (moduleStatus == BandyerModuleStatus.CONNECTED) {
                callButton.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(this, R.color.colorModuleConnected)));
                callButton.setEnabled(true);
            } else if (moduleStatus == BandyerModuleStatus.DISCONNECTED || moduleStatus == BandyerModuleStatus.RECONNECTING) {
                callButton.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(this, R.color.colorModuleNotActive)));
                callButton.setEnabled(false);
            }
        }
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////


    ///////////////////////////////////////////////// BANDYER SDK CLIENT OBSERVER /////////////////////////////////////////////////

    @Override
    public void onClientStatusChange(@NonNull BandyerSDKClientState state) {
        Log.d("MainActivity", "onClientStatusChange " + state);
    }

    @Override
    public void onClientError(@NonNull Throwable throwable) {
        Log.e("MainActivity", "onClientError " + throwable.getLocalizedMessage());
    }

    @Override
    public void onClientReady() {
        Log.d("MainActivity", "onClientReady");
    }

    @Override
    public void onClientStopped() {
        Log.d("MainActivity", "onClientStopped");
    }


    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    int dp2px(float dp) {
        DisplayMetrics metrics = getResources().getDisplayMetrics();
        return (int) (dp * (metrics.densityDpi / DisplayMetrics.DENSITY_DEFAULT));
    }
}
