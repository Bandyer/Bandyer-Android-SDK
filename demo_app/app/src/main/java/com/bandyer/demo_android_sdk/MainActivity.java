/*
 * Copyright (C) 2019 Bandyer S.r.l. All Rights Reserved.
 * See LICENSE.txt for licensing information
 */

package com.bandyer.demo_android_sdk;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.bandyer.android_sdk.call.CallException;
import com.bandyer.android_sdk.call.CallModule;
import com.bandyer.android_sdk.call.CallObserver;
import com.bandyer.android_sdk.call.CallUIObserver;
import com.bandyer.android_sdk.chat.ChatException;
import com.bandyer.android_sdk.chat.ChatModule;
import com.bandyer.android_sdk.chat.ChatObserver;
import com.bandyer.android_sdk.chat.ChatUIObserver;
import com.bandyer.android_sdk.client.BandyerSDKClient;
import com.bandyer.android_sdk.client.BandyerSDKClientObserver;
import com.bandyer.android_sdk.client.BandyerSDKClientOptions;
import com.bandyer.android_sdk.client.BandyerSDKClientState;
import com.bandyer.android_sdk.intent.BandyerIntent;
import com.bandyer.android_sdk.intent.call.Call;
import com.bandyer.android_sdk.intent.call.CallCapabilities;
import com.bandyer.android_sdk.intent.call.CallDisplayMode;
import com.bandyer.android_sdk.intent.call.CallIntentOptions;
import com.bandyer.android_sdk.intent.call.CallOptions;
import com.bandyer.android_sdk.intent.chat.Chat;
import com.bandyer.android_sdk.intent.chat.ChatIntentOptions;
import com.bandyer.android_sdk.module.AuthenticationException;
import com.bandyer.android_sdk.module.BandyerModule;
import com.bandyer.android_sdk.module.BandyerModuleObserver;
import com.bandyer.android_sdk.module.BandyerModuleStatus;
import com.bandyer.demo_android_sdk.adapter_items.NoUserSelectedItem;
import com.bandyer.demo_android_sdk.adapter_items.SelectedUserItem;
import com.bandyer.demo_android_sdk.adapter_items.UserSelectionItem;
import com.bandyer.demo_android_sdk.custom_views.CallOptionsDialog;
import com.bandyer.demo_android_sdk.notification.NotificationProxy;
import com.bandyer.demo_android_sdk.settings.ConfigurationActivity;
import com.bandyer.demo_android_sdk.settings.DefaultCallSettingsActivity;
import com.bandyer.demo_android_sdk.utils.activities.CollapsingToolbarActivity;
import com.bandyer.demo_android_sdk.utils.networking.MockedNetwork;
import com.bandyer.demo_android_sdk.utils.storage.ConfigurationPrefsManager;
import com.bandyer.demo_android_sdk.utils.storage.LoginManager;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.mikepenz.fastadapter.FastAdapter;
import com.mikepenz.fastadapter.IItem;
import com.mikepenz.fastadapter.adapters.ItemAdapter;
import com.mikepenz.fastadapter.listeners.ItemFilterListener;
import com.mikepenz.fastadapter.select.SelectExtension;

import java.lang.ref.WeakReference;
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
public class MainActivity extends CollapsingToolbarActivity implements BandyerSDKClientObserver, BandyerModuleObserver, SearchView.OnQueryTextListener, SwipeRefreshLayout.OnRefreshListener {

    private String TAG = "MainActivity";

    private ItemAdapter<UserSelectionItem> itemAdapter;
    private FastAdapter<UserSelectionItem> fastAdapter;
    private ArrayList<String> calleeSelected;

    private ItemAdapter<IItem> selectedUsersItemAdapter;

    @BindView(R.id.contactsList)
    RecyclerView listContacts;

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @BindView(R.id.call)
    FloatingActionButton callButton;

    @BindView(R.id.chat)
    FloatingActionButton chatButton;

    @BindView(R.id.chat_info_api_21)
    View chatInfo;

    @BindView(R.id.ongoing_call_label)
    TextView ongoingCallLabel;

    @BindView(R.id.no_results)
    View noFilterResults;

    @BindView(R.id.selected_users_chipgroup)
    RecyclerView selectedUsersList;

    @BindView((R.id.selected_users_textview))
    TextView selectedUsersTextView;

    // the external url to provide to the call client in case we want to setup a call coming from an url.
    // The url may be provided to join an existing call, or to create a new one.
    private String joinUrl;

    private ArrayList<UserSelectionItem> usersList = new ArrayList<>();

    abstract class MyCallObserver implements CallUIObserver, CallObserver {
    }

    abstract class MyChatObserver implements ChatUIObserver, ChatObserver {
    }

    private MyCallObserver callObserver = new MyCallObserver() {

        @Override
        public void onActivityError(@NonNull Call ongoingCall, @NonNull WeakReference<AppCompatActivity> callActivity, @NonNull CallException error) {
            Log.e(TAG, "onCallActivityError " + error.getMessage());
        }

        @Override
        public void onActivityDestroyed(@NonNull Call ongoingCall, @NonNull WeakReference<AppCompatActivity> callActivity) {
            Log.d(TAG, "onCallActivityDestroyed");
        }

        @Override
        public void onActivityStarted(@NonNull Call ongoingCall, @NonNull WeakReference<AppCompatActivity> callActivity) {
            Log.d(TAG, "onCallActivityStarted");
        }

        @Override
        public void onCallStarted(@NonNull Call ongoingCall) {
            Log.d(TAG, "onCallStarted");
        }

        @Override
        public void onCallCreated(@NonNull Call ongoingCall) {
            Log.d(TAG, "onCallCreated");
            showOngoingCallLabel();
        }

        @Override
        public void onCallEnded(@NonNull Call ongoingCall) {
            Log.d(TAG, "onCallEnded");
            hideOngoingCallLabel();
        }

        @Override
        public void onCallEndedWithError(@NonNull Call ongoingCall, @NonNull CallException callException) {
            Log.d(TAG, "onCallEnded with error: " + callException.getMessage());
            hideOngoingCallLabel();
            showErrorDialog(callException.getMessage());
        }
    };

    private MyChatObserver chatObserver = new MyChatObserver() {

        @Override
        public void onActivityError(@NonNull Chat chat, @NonNull WeakReference<AppCompatActivity> activity, @NonNull ChatException error) {
            Log.e(TAG, "onChatActivityError " + error.getMessage());
        }

        @Override
        public void onActivityDestroyed(@NonNull Chat chat, @NonNull WeakReference<AppCompatActivity> activity) {
            Log.d(TAG, "onChatActivityDestroyed");
        }

        @Override
        public void onActivityStarted(@NonNull Chat chat, @NonNull WeakReference<AppCompatActivity> activity) {
            Log.d(TAG, "onChatActivityStarted");
        }

        @Override
        public void onChatStarted() {
            Log.d(TAG, "onChatStarted");
        }

        @Override
        public void onChatEndedWithError(@NonNull ChatException chatException) {
            Log.d(TAG, "onChatEndedWithError: " + chatException.getMessage());
            showErrorDialog(chatException.getMessage());
        }

        @Override
        public void onChatEnded() {
            Log.d(TAG, "onChatEnded");
        }
    };

    public static void show(Activity context) {
        Intent intent = new Intent(context, MainActivity.class);
        context.startActivity(intent);
    }

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (!LoginManager.isUserLogged(this)) return;

        // If FCM is not being used as the default notification service.
        // We need to launch the other notification services in the main launcher activity.
        NotificationProxy.listen(this);

        // inflate main layout and keep a reference to it in case of use with dpad navigation
        setContentView(R.layout.activity_main);

        // get the user that is currently logged in the sample app
        String userAlias = LoginManager.getLoggedUser(this);

        // customize toolbar
        setCollapsingToolbarTitle(String.format(getResources().getString(R.string.pick_users), userAlias));

        // in case the MainActivity has been shown by opening an external link, handle it
        handleExternalUrl(getIntent());

        ongoingCallLabel.setOnClickListener(v -> {
            CallModule callModule = BandyerSDKClient.getInstance().getCallModule();
            if (callModule == null) return;

            Call ongoingCall = callModule.getOngoingCall();
            if (ongoingCall == null) return;

            callModule.setDisplayMode(ongoingCall, CallDisplayMode.FOREGROUND);
        });

        refreshUsers.setOnRefreshListener(this);

        selectedUsersItemAdapter = ItemAdapter.items();
        FastAdapter<IItem> selectedUsersAdapter = FastAdapter.with(selectedUsersItemAdapter);
        selectedUsersAdapter.withSelectable(true);
        selectedUsersAdapter.withOnClickListener((v, adapter, item, position) -> {
            if (item instanceof SelectedUserItem)
                deselectUser(((SelectedUserItem) item).userAlias, ((SelectedUserItem) item).position);
            return true;
        });
        selectedUsersList.setLayoutManager(new LinearLayoutManager(MainActivity.this, RecyclerView.HORIZONTAL, false));
        selectedUsersList.setAdapter(selectedUsersAdapter);
        selectedUsersItemAdapter.add(new NoUserSelectedItem());
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (ConfigurationPrefsManager.areCredentialsMockedOrEmpty(this)) return;

        BandyerSDKClient.getInstance().removeObserver(this);
        BandyerSDKClient.getInstance().removeModuleObserver(this);

        hideKeyboard(true);
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (ConfigurationPrefsManager.areCredentialsMockedOrEmpty(this)) {
            ConfigurationActivity.showNew(this);
            return;
        }

        if (!LoginManager.isUserLogged(this)) {
            LoginActivity.show(this);
            return;
        }

        // If the user is already logged, setup the activity.
        setUpRecyclerView();

        BandyerSDKClient.getInstance().addObserver(this);
        BandyerSDKClient.getInstance().addModuleObserver(this);

        startBandyerSdk(LoginManager.getLoggedUser(this));

        // BE AWARE that you don't get notified if the modules are already initialized/running as it is already in the past.
        // Update the button colors based on their current module status to avoid interaction before the modules are ready.
        for (BandyerModule module : BandyerSDKClient.getInstance().getModules()) {
            setModuleButtonsColors(module, module.getStatus());
        }
    }

    private void startBandyerSdk(String userAlias) {
        Log.d(TAG, "startBandyerSDK");

        if (chatButton != null) chatButton.setEnabled(false);
        if (callButton != null) callButton.setEnabled(false);

        if (BandyerSDKClient.getInstance().getState() == BandyerSDKClientState.UNINITIALIZED) {
            BandyerSDKClientOptions options = new BandyerSDKClientOptions.Builder()
                    .keepListeningForEventsInBackground(false)
                    .build();
            BandyerSDKClient.getInstance().init(userAlias, options);
        }

        // Start listening for events
        BandyerSDKClient.getInstance().startListening();

        addModulesObservers();
    }

    /**
     * Adds chat and call modules observers.
     * The observers will be notified when a chat or a call UI will be started, closed or closed with errors.
     */
    private void addModulesObservers() {
        // set an observer for the call to show ongoing call label
        CallModule callModule = BandyerSDKClient.getInstance().getCallModule();
        if (callModule != null) {
            callModule.addCallObserver(this, callObserver);
            callModule.addCallUIObserver(this, callObserver);
            if (callModule.isInCall()) showOngoingCallLabel();
            else hideOngoingCallLabel();
        }

        // set an observer for the chat
        ChatModule chatModule = BandyerSDKClient.getInstance().getChatModule();
        if (chatModule != null) {
            chatModule.addChatObserver(this, chatObserver);
            chatModule.addChatUIObserver(this, chatObserver);
        }
    }

    /**
     * Handle an external url by calling join method
     * <p>
     * WARNING!!!
     * Be sure to have the call client connected before joining a call with the url provided.
     * Otherwise you will receive an error.
     */
    @SuppressLint("NewApi")
    private void handleExternalUrl(Intent intent) {
        // do not handle the url if we do not have a valid user
        if (!LoginManager.isUserLogged(this)) return;

        String userAlias = LoginManager.getLoggedUser(this);

        if (!Intent.ACTION_VIEW.equals(intent.getAction())) return;

        Uri uri = intent.getData();
        if (uri != null) {
            joinUrl = uri.toString();
            // if client is not running, then I need to initialize it
            if (BandyerSDKClient.getInstance().getState() == BandyerSDKClientState.UNINITIALIZED) {
                startBandyerSdk(userAlias);
            } else if (BandyerSDKClient.getInstance().getCallModule().getStatus() == BandyerModuleStatus.CONNECTED) {

                BandyerIntent bandyerIntent = new BandyerIntent.Builder()
                        .startFromJoinCallUrl(this, joinUrl)
                        .withCapabilities(new CallCapabilities().withChat().withFileSharing().withScreenSharing().withWhiteboard())
                        .withOptions(new CallOptions())
                        .build();

                startActivity(bandyerIntent);
                joinUrl = null;
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (ConfigurationPrefsManager.areCredentialsMockedOrEmpty(this)) return;
        BandyerSDKClient.getInstance().dispose();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        handleExternalUrl(intent);
    }

    @Override
    public void onBackPressed() {
        showConfirmDialog(R.string.logout, R.string.logout_confirmation, (dialogInterface, i) -> {
            logout();
            finish();
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main, menu);
        searchView = (SearchView) menu.findItem(R.id.searchMain).getActionView();
        searchView.setOnSearchClickListener(v -> appBarLayout.setExpanded(false, true));
        searchView.setQueryHint(getString(R.string.search));
        searchView.setOnQueryTextListener(this);
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
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.logout:
                logout();
                break;
            case R.id.options:
                openCallOptionsActivity();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void logout() {
        LoginManager.logout(this);
        BandyerSDKClient.getInstance().clearUserCache();
        BandyerSDKClient.getInstance().dispose();
        ongoingCallLabel.setVisibility(View.GONE);
        LoginActivity.show(this);
    }

    private void openCallOptionsActivity() {
        DefaultCallSettingsActivity.show(this);
    }

    @Override
    public void onRefresh() {
        calleeSelected = new ArrayList<>();

        usersList.clear();
        itemAdapter.clear();
        selectedUsersItemAdapter.clear();
        selectedUsersItemAdapter.add(new NoUserSelectedItem());
        loading.setVisibility(View.VISIBLE);

        // Fetch the sample users you can use to login with.
        MockedNetwork.getSampleUsers(this, new MockedNetwork.GetBandyerUsersCallback() {
            @Override
            public void onUsers(List<String> users) {
                loading.setVisibility(View.GONE);
                // Add each user(except the logged one) to the recyclerView adapter to be displayed in the list.
                for (String user : users)
                    if (!user.equals(LoginManager.getLoggedUser(MainActivity.this)))
                        usersList.add(new UserSelectionItem(user));
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

    private void setUpRecyclerView() {
        if (fastAdapter != null && usersList.size() > 0) return;

        itemAdapter = ItemAdapter.items();
        fastAdapter = FastAdapter.with(itemAdapter);
        fastAdapter.withSelectable(true);

        // on user selection put it in a list to be called on click on call button.
        fastAdapter.withOnPreClickListener((itemView, adapter, item, position) -> {
            if (!item.isSelected())
                selectUser(item.name, position);
            else
                deselectUser(item.name, position);
            return true;
        });

        listContacts.setItemAnimator(null);
        listContacts.setLayoutManager(new LinearLayoutManager(this));
        listContacts.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        listContacts.setAdapter(fastAdapter);

        itemAdapter.getItemFilter().withFilterPredicate((userSelectionItem, constraint) -> userSelectionItem.name.toLowerCase().contains(constraint.toString().toLowerCase()));

        itemAdapter.getItemFilter().withItemFilterListener(new ItemFilterListener<UserSelectionItem>() {
            @Override
            public void itemsFiltered(@Nullable CharSequence constraint, @Nullable List<UserSelectionItem> results) {
                noFilterResults.post(() -> {
                    if (results.size() > 0) noFilterResults.setVisibility(View.GONE);
                    else noFilterResults.setVisibility(View.VISIBLE);
                });
            }

            @Override
            public void onReset() {
                noFilterResults.post(() -> noFilterResults.setVisibility(View.GONE));
            }
        });

        onRefresh();
    }

    private void selectUser(String userAlias, int position) {
        SelectExtension<UserSelectionItem> selectExtension = fastAdapter.getExtension(SelectExtension.class);
        selectExtension.select(position);
        calleeSelected.add(userAlias);
        SelectedUserItem selectedUserItem = new SelectedUserItem(userAlias, position);
        selectedUserItem.withIdentifier(position);
        selectedUsersItemAdapter.add(0, selectedUserItem);
        selectedUsersList.smoothScrollToPosition(0);
        selectedUsersItemAdapter.removeByIdentifier(NoUserSelectedItem.NO_USER_SELECTED_ITEM_IDENTIFIER);
    }

    private void deselectUser(String userAlias, int position) {
        SelectExtension<UserSelectionItem> selectExtension = fastAdapter.getExtension(SelectExtension.class);
        selectExtension.deselect(position);
        calleeSelected.remove(userAlias);
        selectedUsersItemAdapter.removeByIdentifier(position);
        if (selectedUsersItemAdapter.getAdapterItemCount() == 0)
            selectedUsersItemAdapter.add(new NoUserSelectedItem());
    }

    private void showOngoingCallLabel() {
        if (ongoingCallLabel == null) return;
        ongoingCallLabel.setVisibility(View.VISIBLE);
    }

    private void hideOngoingCallLabel() {
        if (ongoingCallLabel == null) return;
        ongoingCallLabel.setVisibility(View.GONE);
    }

    /**
     * This is how a chat is started. You must provide one users alias identifying the user your user wants to communicate with.
     * Starting a chat is an asynchronous process, failure or success is reported in the callback provided.
     * <p>
     * WARNING!!!
     * Be aware that all the observers in this SDK, MUST NOT be defined as anonymous class because the call client will have a weak reference to them to avoid leaks and other scenarios.
     * If you do implement the observer anonymously the methods may not be called.
     */
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @OnClick(R.id.chat)
    void chat() {
        if (calleeSelected.size() == 0) {
            showErrorDialog(getResources().getString(R.string.oto_chat_error_no_selected_user));
            return;
        }
        if (calleeSelected.size() > 1) {
            showErrorDialog(getResources().getString(R.string.oto_chat_error_group_selected));
            return;
        }

        hideKeyboard(this);

        ChatIntentOptions chatIntentOptions = new BandyerIntent.Builder()
                .startWithChat(MainActivity.this)
                .with(calleeSelected.get(0));

        CallOptionsDialog dialog = CallOptionsDialog.newInstance(calleeSelected, CallOptionsDialog.Configuration.CHAT);
        dialog.setOnCallOptionsUpdatedListener(new CallOptionsDialog.OnCallOptionsUpdatedListener() {
            @Override
            public void onCallOptionsUpdated(CallOptionsDialog.CallOptionsType callOptionsType, CallCapabilities callCapabilities, CallOptions callOptions) {
                switch (callOptionsType) {
                    case AUDIO_ONLY:
                        chatIntentOptions.withAudioCallCapability(callCapabilities, callOptions);
                        break;
                    case AUDIO_UPGRADABLE:
                        chatIntentOptions.withAudioUpgradableCallCapability(callCapabilities, callOptions);
                        break;
                    case AUDIO_VIDEO:
                        chatIntentOptions.withAudioVideoCallCapability(callCapabilities, callOptions);
                        break;
                }
            }

            @Override
            public void onOptionsConfirmed() {
                BandyerIntent chatIntent = chatIntentOptions.build();
                startActivity(chatIntent);
            }
        });
        dialog.show(getSupportFragmentManager(), "chat_options_dialog");
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
        if (calleeSelected.size() == 0) {
            showErrorDialog(getResources().getString(R.string.oto_call_error_no_selected_user));
            return;
        }

        hideKeyboard(this);

        CallOptionsDialog dialog = CallOptionsDialog.newInstance(calleeSelected, CallOptionsDialog.Configuration.CALL);
        dialog.setOnCallOptionsUpdatedListener(new CallOptionsDialog.OnCallOptionsUpdatedListener() {

            CallIntentOptions callIntentOptions = null;

            @Override
            public void onCallOptionsUpdated(CallOptionsDialog.CallOptionsType callOptionsType, CallCapabilities callCapabilities, CallOptions callOptions) {
                switch (callOptionsType) {
                    case AUDIO_ONLY:
                        callIntentOptions = new BandyerIntent.Builder()
                                .startWithAudioCall(MainActivity.this)
                                .with(calleeSelected);
                        break;
                    case AUDIO_UPGRADABLE:
                        callIntentOptions = new BandyerIntent.Builder()
                                .startWithAudioUpgradableCall(MainActivity.this)
                                .with(calleeSelected);
                        break;
                    case AUDIO_VIDEO:
                        callIntentOptions = new BandyerIntent.Builder()
                                .startWithAudioVideoCall(MainActivity.this)
                                .with(calleeSelected);
                        break;
                }
                callIntentOptions
                        .withCapabilities(callCapabilities)
                        .withOptions(callOptions);
            }

            @Override
            public void onOptionsConfirmed() {
                BandyerIntent callIntent = callIntentOptions.build();
                startActivity(callIntent);
            }
        });
        dialog.show(getSupportFragmentManager(), "call_options_dialog");
    }

    ///////////////////////////////////////////////// BANDYER SDK CLIENT MODULE OBSERVER /////////////////////////////////////////////////

    @Override
    @SuppressLint("NewApi")
    public void onModuleReady(@NonNull BandyerModule module) {
        Log.d(TAG, "onModuleReady " + module.getName());

        if (module instanceof ChatModule && chatButton != null) {
            chatButton.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(this, R.color.colorModuleChatOffline)));
            chatButton.setEnabled(true);
        } else if (module instanceof CallModule) {
            if (callButton != null) {
                callButton.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(this, R.color.colorModuleConnected)));
                callButton.setEnabled(true);
            }
            if (joinUrl != null) {

                BandyerIntent bandyerIntent = new BandyerIntent.Builder()
                        .startFromJoinCallUrl(this, joinUrl)
                        .withOptions(new CallOptions())
                        .withCapabilities(new CallCapabilities().withChat().withWhiteboard().withScreenSharing().withFileSharing())
                        .build();

                startActivity(bandyerIntent);
                joinUrl = null; // reset boolean to avoid reopening external url twice on resume
            }
        }
    }

    @Override
    public void onModulePaused(@NonNull BandyerModule module) {
        Log.d(TAG, "onModulePaused " + module.getName());
        setModuleButtonsColors(module, module.getStatus());
    }

    @Override
    public void onModuleFailed(@NonNull final BandyerModule module, @NonNull Throwable throwable) {
        Log.e(TAG, "onModuleFailed " + module.getName() + " error " + throwable.getLocalizedMessage());
        setModuleButtonsColors(module, module.getStatus());
        if (throwable instanceof AuthenticationException) {
            showErrorDialog("The credentials provided are not valid!", (dialog, which) -> {
                logout();
                ConfigurationActivity.show(this);
            });
        } else
            setModuleButtonsColors(module, BandyerModuleStatus.FAILED);
    }

    @Override
    public void onModuleStatusChanged(@NonNull BandyerModule module, @NonNull BandyerModuleStatus moduleStatus) {
        Log.d(TAG, "onModuleStatusChanged " + module.getName() + " status " + moduleStatus);
        setModuleButtonsColors(module, moduleStatus);
    }

    private void setModuleButtonsColors(@NonNull BandyerModule module, @NonNull BandyerModuleStatus moduleStatus) {
        if (module instanceof ChatModule && chatButton != null)
            setChatButtonColor(moduleStatus);
        else if (module instanceof CallModule && callButton != null)
            setCallButtonColor(moduleStatus);
    }

    private void setChatButtonColor(@NonNull BandyerModuleStatus moduleStatus) {
        // the chat module is offline first, which means that you can interact with it even when you are not connected to internet
        // here we color the button in black until the module gets online
        switch (moduleStatus) {
            case CONNECTED:
                chatButton.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(this, R.color.colorModuleConnected)));
                chatButton.setEnabled(true);
                break;
            case PAUSED:
                chatButton.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(this, R.color.colorModuleNotActive)));
                chatButton.setEnabled(false);
                break;
            case DESTROYED:
            case FAILED:
                chatButton.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(this, R.color.colorModuleError)));
                chatButton.setEnabled(false);
                break;
            case DISCONNECTED:
            case RECONNECTING:
            case READY:
                chatButton.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(this, R.color.colorModuleChatOffline)));
                chatButton.setEnabled(true);
                break;
        }
    }

    private void setCallButtonColor(@NonNull BandyerModuleStatus moduleStatus) {
        switch (moduleStatus) {
            case CONNECTED:
            case READY:
                callButton.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(this, R.color.colorModuleConnected)));
                callButton.setEnabled(true);
                break;
            case DESTROYED:
            case FAILED:
                callButton.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(this, R.color.colorModuleError)));
                callButton.setEnabled(false);
                break;
            default:
                callButton.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(this, R.color.colorModuleNotActive)));
                callButton.setEnabled(false);
                break;
        }
    }

    ///////////////////////////////////////////////// BANDYER SDK CLIENT OBSERVER /////////////////////////////////////////////////

    @Override
    public void onClientStatusChange(@NonNull BandyerSDKClientState state) {
        Log.d(TAG, "onClientStatusChange " + state);
    }

    @Override
    public void onClientError(@NonNull Throwable throwable) {
        Log.e(TAG, "onClientError " + throwable.getLocalizedMessage());
        setCallButtonColor(BandyerModuleStatus.FAILED);
        setChatButtonColor(BandyerModuleStatus.FAILED);
    }

    @Override
    public void onClientReady() {
        Log.d(TAG, "onClientReady");
    }

    @Override
    public void onClientStopped() {
        Log.d(TAG, "onClientStopped");
    }

    ////////////////////////////////////////////////////// UTILS /////////////////////////////////////////////////////////////

    public void hideKeyboard(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        if (imm == null) return;
        View view = activity.getCurrentFocus();
        if (view == null) view = new View(activity);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }
}


