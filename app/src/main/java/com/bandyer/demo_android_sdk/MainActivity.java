/*
 * Copyright (C) 2020 Bandyer S.r.l. All Rights Reserved.
 * See LICENSE.txt for licensing information
 */

package com.bandyer.demo_android_sdk;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.bandyer.android_sdk.call.CallException;
import com.bandyer.android_sdk.call.CallModule;
import com.bandyer.android_sdk.call.CallObserver;
import com.bandyer.android_sdk.call.CallRecordingObserver;
import com.bandyer.android_sdk.call.CallUIObserver;
import com.bandyer.android_sdk.chat.ChatException;
import com.bandyer.android_sdk.chat.ChatModule;
import com.bandyer.android_sdk.chat.ChatObserver;
import com.bandyer.android_sdk.chat.ChatUIObserver;
import com.bandyer.android_sdk.client.AccessTokenProvider;
import com.bandyer.android_sdk.client.BandyerSDK;
import com.bandyer.android_sdk.client.Session;
import com.bandyer.android_sdk.client.SessionObserver;
import com.bandyer.android_sdk.intent.BandyerIntent;
import com.bandyer.android_sdk.intent.call.Call;
import com.bandyer.android_sdk.intent.call.CallDisplayMode;
import com.bandyer.android_sdk.intent.call.CallIntentBuilder;
import com.bandyer.android_sdk.intent.call.CallRecordingState;
import com.bandyer.android_sdk.intent.chat.Chat;
import com.bandyer.android_sdk.intent.chat.ChatIntentBuilder;
import com.bandyer.android_sdk.module.AuthenticationException;
import com.bandyer.android_sdk.module.BandyerModule;
import com.bandyer.android_sdk.module.BandyerModuleObserver;
import com.bandyer.android_sdk.module.BandyerModuleStatus;
import com.bandyer.android_sdk.tool_configuration.call.CustomCallConfiguration;
import com.bandyer.demo_android_sdk.databinding.ActivityMainBinding;
import com.bandyer.demo_android_sdk.notification.MissedNotificationPayloadWorker;
import com.bandyer.demo_android_sdk.storage.DefaultConfigurationManager;
import com.bandyer.demo_android_sdk.ui.activities.CollapsingToolbarActivity;
import com.bandyer.demo_android_sdk.ui.adapter_items.NoUserSelectedItem;
import com.bandyer.demo_android_sdk.ui.adapter_items.SelectedUserItem;
import com.bandyer.demo_android_sdk.ui.adapter_items.UserSelectionItem;
import com.bandyer.demo_android_sdk.ui.custom_views.CustomConfigurationDialog;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.snackbar.Snackbar;
import com.kaleyra.app_configuration.activities.BaseConfigurationActivity;
import com.kaleyra.app_configuration.activities.ConfigurationActivity;
import com.kaleyra.app_utilities.notification.NotificationProxy;
import com.kaleyra.app_utilities.notification.NotificationUtilsKt;
import com.kaleyra.app_utilities.storage.ConfigurationPrefsManager;
import com.kaleyra.app_utilities.storage.LoginManager;
import com.kaleyra.collaboration_suite_phone_ui.recording.KaleyraRecordingSnackbar;
import com.kaleyra.collaboration_suite_phone_ui.snackbar.KaleyraSnackbar;
import com.mikepenz.fastadapter.FastAdapter;
import com.mikepenz.fastadapter.IAdapter;
import com.mikepenz.fastadapter.IItem;
import com.mikepenz.fastadapter.adapters.ItemAdapter;
import com.mikepenz.fastadapter.extensions.ExtensionsFactories;
import com.mikepenz.fastadapter.listeners.ItemFilterListener;
import com.mikepenz.fastadapter.select.SelectExtension;
import com.mikepenz.fastadapter.select.SelectExtensionFactory;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import kotlin.jvm.functions.Function4;

/**
 * This Activity will be called after the user has logged or if an external url was opened with this app.
 * It's main job is to redirect to the dialing(outgoing) or ringing(ringing) call activities.
 *
 * @author kristiyan
 */
public class MainActivity extends CollapsingToolbarActivity implements BandyerModuleObserver, SearchView.OnQueryTextListener, SwipeRefreshLayout.OnRefreshListener {

    private final String TAG = "MainActivity";

    private ItemAdapter<UserSelectionItem> itemAdapter;
    private FastAdapter<UserSelectionItem> fastAdapter;
    private ArrayList<String> calleeSelected = new ArrayList<>();

    private final ItemAdapter<IItem<?>> selectedUsersItemAdapter = new ItemAdapter<>();

    private com.kaleyra.app_configuration.model.Configuration configuration = null;

    private ActivityMainBinding binding;

    SearchView searchView;

    private final ArrayList<UserSelectionItem> usersList = new ArrayList<>();

    private KaleyraSnackbar recordingSnackbar;

    abstract static class MyCallObserver implements CallUIObserver, CallObserver, CallRecordingObserver {
    }

    abstract static class MyChatObserver implements ChatUIObserver, ChatObserver {
    }

    private final SessionObserver sessionObserver = new SessionObserver() {
        @Override
        public void onSessionAuthenticating(@NonNull Session session) {
            Log.d(TAG, "onSessionAuthenticating for user " + session.getUserId());
        }

        @Override
        public void onSessionAuthenticated(@NonNull Session session) {
            Log.d(TAG, "onSessionAuthenticated for user " + session.getUserId());
        }

        @Override
        public void onSessionRefreshing(@NonNull Session session) {
            Log.d(TAG, "onSessionRefreshing for user " + session.getUserId());
        }

        @Override
        public void onSessionRefreshed(@NonNull Session session) {
            Log.d(TAG, "onSessionRefreshed for user " + session.getUserId());
        }

        @Override
        public void onSessionError(@NonNull Session session, @NonNull Error error) {
            Log.e(TAG, "onSessionError for user " + session.getUserId() + " with error: " + error.getMessage());
        }
    };

    private final MyCallObserver callObserver = new MyCallObserver() {

        @Override
        public void onActivityError(@NonNull Call ongoingCall, @NonNull WeakReference<AppCompatActivity> callActivity, @NonNull CallException error) {
            Log.e(TAG, "onCallActivityError " + error.getMessage());
            showErrorDialog(error.getMessage());
            Call callModuleOngoingCall = BandyerSDK.getInstance().getCallModule().getOngoingCall();
            if (callModuleOngoingCall != null && ongoingCall != callModuleOngoingCall) return;
            hideOngoingCallLabel();
        }

        @Override
        public void onActivityDestroyed(@NonNull Call ongoingCall, @NonNull WeakReference<AppCompatActivity> callActivity) {
            Log.d(TAG, "onCallActivityDestroyed");
            Call callModuleOngoingCall = BandyerSDK.getInstance().getCallModule().getOngoingCall();
            if (callModuleOngoingCall != null && ongoingCall != callModuleOngoingCall) return;
            hideOngoingCallLabel();
        }

        @Override
        public void onActivityStarted(@NonNull Call ongoingCall, @NonNull WeakReference<AppCompatActivity> callActivity) {
            Log.d(TAG, "onCallActivityStarted");
        }

        @Override
        public void onCallStarted(@NonNull Call ongoingCall) {
            Log.d(TAG, "onCallStarted");
            ongoingCall.addCallRecordingObserver(this);
        }

        @Override
        public void onCallCreated(@NonNull Call ongoingCall) {
            Log.d(TAG, "onCallCreated");
            showOngoingCallLabel();
        }

        @Override
        public void onCallEnded(@NonNull Call ongoingCall) {
            Log.d(TAG, "onCallEnded");
            Call callModuleOngoingCall = BandyerSDK.getInstance().getCallModule().getOngoingCall();
            if (callModuleOngoingCall != null && ongoingCall != callModuleOngoingCall) return;
            hideOngoingCallLabel();
            ongoingCall.removeCallRecordingObserver(this);
        }

        @Override
        public void onCallEndedWithError(@NonNull Call ongoingCall, @NonNull CallException callException) {
            Log.d(TAG, "onCallEnded with error: " + callException.getMessage());
            Call callModuleOngoingCall = BandyerSDK.getInstance().getCallModule().getOngoingCall();
            if (callModuleOngoingCall != null && ongoingCall != callModuleOngoingCall) return;
            hideOngoingCallLabel();
            showErrorDialog(callException.getMessage());
            ongoingCall.removeCallRecordingObserver(this);
        }

        @Override
        public void onCallRecordingStateChanged(@NonNull Call call, @NonNull CallRecordingState callRecordingState) {
            switch (callRecordingState) {
                case STOPPED:
                    recordingSnackbar = KaleyraRecordingSnackbar.make(findViewById(R.id.main_view), KaleyraRecordingSnackbar.Type.TYPE_ENDED, Snackbar.LENGTH_LONG);
                    break;
                case STARTED:
                    recordingSnackbar = KaleyraRecordingSnackbar.make(findViewById(R.id.main_view), KaleyraRecordingSnackbar.Type.TYPE_STARTED, Snackbar.LENGTH_LONG);
                    break;
            }
            recordingSnackbar.show();
        }

        @Override
        public void onCallRecordingFailed(@NonNull Call call, @NonNull String reason) {
            recordingSnackbar = KaleyraRecordingSnackbar.make(findViewById(R.id.main_view), KaleyraRecordingSnackbar.Type.TYPE_ERROR, Snackbar.LENGTH_LONG);
            recordingSnackbar.show();
        }
    };

    private final MyChatObserver chatObserver = new MyChatObserver() {

        @Override
        public void onActivityError(@NonNull Chat chat, @NonNull WeakReference<AppCompatActivity> activity, @NonNull ChatException error) {
            Log.e(TAG, "onChatActivityError " + error.getMessage());
            showErrorDialog(error.getMessage());
        }

        @Override
        public void onActivityDestroyed(@NonNull Chat chat, @NonNull WeakReference<AppCompatActivity> activity) {
            Log.d(TAG, "onChatActivityDestroyed");
        }

        @Override
        public void onActivityStarted(@NonNull Chat chat, @NonNull WeakReference<AppCompatActivity> activity) {
            Log.d(TAG, "onChatActivityStarted");
        }
    };

    private boolean areObserversAdded = false;

    public static void show(Activity context) {
        Intent intent = new Intent(context, MainActivity.class);
        context.startActivity(intent);
    }

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        configuration = ConfigurationPrefsManager.INSTANCE.getConfiguration(this);

        NotificationUtilsKt.requestPushNotificationPermissionApi33(this);

        if (!LoginManager.isUserLogged(this)) return;
        // If FCM is not being used as the default notification service.
        // We need to launch the other notification services in the main launcher activity.
        NotificationProxy.listen(this);

        // inflate main layout and keep a reference to it in case of use with dpad navigation
        setContentView(R.layout.activity_main);
        binding = ActivityMainBinding.bind(getWindow().getDecorView());

        // If the user is already logged, setup the activity.
        setUpRecyclerView();

        ImageView header = findViewById(R.id.headerView);
        if (!BuildConfig.DEBUG) header.setImageResource(R.drawable.landing_image);

        // get the user that is currently logged in the sample app
        String userAlias = LoginManager.getLoggedUser(this);

        // customize toolbar
        setCollapsingToolbarTitle(String.format(getResources().getString(R.string.pick_users), userAlias), userAlias);

        // in case the MainActivity has been shown by opening an external link, handle it
        handleExternalUrl(getIntent());

        // in case the MainActivity has been shown by an action of missed call notification
        handleMissedCall(getIntent());

        binding.ongoingCallLabel.setOnClickListener(v -> {
            CallModule callModule = BandyerSDK.getInstance().getCallModule();
            if (callModule == null) return;

            Call ongoingCall = callModule.getOngoingCall();
            if (ongoingCall == null) return;

            callModule.setDisplayMode(ongoingCall, CallDisplayMode.FOREGROUND);
        });

        FastAdapter<IItem<?>> selectedUsersAdapter = FastAdapter.with(selectedUsersItemAdapter);
        SelectExtension<IItem<?>> selectExtension = selectedUsersAdapter.getOrCreateExtension(SelectExtension.class);
        selectExtension.setSelectable(true);
        selectedUsersAdapter.setOnClickListener((view, iItemIAdapter, iItem, integer) -> {
            if (iItem instanceof com.bandyer.demo_android_sdk.ui.adapter_items.SelectedUserItem) {
                deselectUser(((com.bandyer.demo_android_sdk.ui.adapter_items.SelectedUserItem) iItem).userAlias, ((com.bandyer.demo_android_sdk.ui.adapter_items.SelectedUserItem) iItem).position);
            }
            return true;
        });

        binding.selectedUsersChipgroup.setFocusable(false);
        binding.selectedUsersChipgroup.setLayoutManager(new LinearLayoutManager(MainActivity.this, RecyclerView.HORIZONTAL, false));
        binding.selectedUsersChipgroup.setAdapter(selectedUsersAdapter);
        selectedUsersItemAdapter.add(new NoUserSelectedItem());

        addObservers();

        startBandyerSDK();
    }

    @Override
    protected void onPause() {
        super.onPause();
        hideKeyboard(true);
    }

    @Override
    protected void onStop() {
        super.onStop();
        resetButtonsState();
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (configuration.isMockConfiguration()) {
            ConfigurationActivity.Companion.showNew(this, configuration, true);
            return;
        }

        if (!LoginManager.isUserLogged(this)) {
            LoginActivity.show(this);
            finish();
            return;
        }

        if (usersList.isEmpty()) loadUsersList();

        // Update the button colors based on their current module status to avoid interaction before the modules are ready.
        for (BandyerModule module : BandyerSDK.getInstance().getModules()) {
            setModuleButtonsColors(module, module.getStatus());
        }
    }

    private void startBandyerSDK() {
        String userId = LoginManager.getLoggedUser(getApplicationContext());

        AccessTokenProvider accessTokenProvider = (userId1, completion) -> getRestApi().getAccessToken(userId, accessToken -> {
            completion.success(accessToken);
            return null;
        }, exception -> {
            completion.error(exception);
            return null;
        });

        Session session = new Session(
                userId,
                accessTokenProvider,
                sessionObserver);

        BandyerSDK.getInstance().connect(
                session,
                errorReason -> Log.e(TAG, "Unable to connect BandyerSDK with error: " + errorReason)
        );
    }

    /**
     * Adds sdk client observer, chat and call modules observers.
     * The observers will be notified when a chat or a call UI will be started, closed or closed with errors.
     */
    private void addObservers() {
        if (areObserversAdded) return;
        areObserversAdded = true;

        // Add an observer for the chat and call modules
        BandyerSDK.getInstance().addModuleObserver(this);

        // set an observer for the call to show ongoing call label
        CallModule callModule = BandyerSDK.getInstance().getCallModule();
        callModule.addCallObserver(this, callObserver);
        callModule.addCallUIObserver(this, callObserver);
        if (callModule.isInCall()) showOngoingCallLabel();
        else hideOngoingCallLabel();

        // set an observer for the ongoing chat
        ChatModule chatModule = BandyerSDK.getInstance().getChatModule();
        chatModule.addChatObserver(this, chatObserver);
        chatModule.addChatUIObserver(this, chatObserver);
    }

    private void removeObservers() {
        areObserversAdded = false;

        BandyerSDK.getInstance().removeModuleObserver(this);

        CallModule callModule = BandyerSDK.getInstance().getCallModule();
        if (callModule != null) {
            callModule.removeCallObserver(callObserver);
            callModule.removeCallUIObserver(callObserver);
            Call ongoingCall = callModule.getOngoingCall();
            if (ongoingCall != null) callModule.getOngoingCall().removeCallRecordingObserver(callObserver);
        }

        ChatModule chatModule = BandyerSDK.getInstance().getChatModule();
        if (chatModule != null) {
            chatModule.removeChatObserver(chatObserver);
            chatModule.removeChatUIObserver(chatObserver);
        }
    }

    /**
     * Handle an external url by calling join method
     * WARNING!!!
     * Be sure to have the call client connected before joining a call with the url provided.
     * Otherwise you will receive an error.
     */
    @SuppressLint("NewApi")
    private void handleExternalUrl(Intent intent) {
        if (!Intent.ACTION_VIEW.equals(intent.getAction())) return;
        Uri uri = intent.getData();
        if (uri == null) return;

        // do not handle the url if we do not have a valid user
        if (!LoginManager.isUserLogged(this)) return;

        startBandyerSDK();

        BandyerIntent bandyerIntent = new BandyerIntent.Builder()
                .startFromJoinCallUrl(this, uri.toString())
                .build();

        startActivity(bandyerIntent);
    }

    private void handleMissedCall(Intent intent) {
        int notificationId = intent.getIntExtra(MissedNotificationPayloadWorker.notificationId, 0);
        if (notificationId == 0) return;
        MissedNotificationPayloadWorker.cancelNotification(this, notificationId);
        if (!LoginManager.isUserLogged(this)) return;
        startBandyerSDK();
        ArrayList<String> callUsers = intent.getStringArrayListExtra(MissedNotificationPayloadWorker.startCall);
        if (callUsers != null) {
            BandyerIntent.Builder bandyerIntentBuilder = new BandyerIntent.Builder();
            CallIntentBuilder callIntentBuilder = bandyerIntentBuilder.startWithAudioUpgradableCall(getApplicationContext());
            BandyerIntent bandyerIntent = callIntentBuilder.with(callUsers).build();
            startActivity(bandyerIntent);
            return;
        }
        String user = intent.getStringExtra(MissedNotificationPayloadWorker.startChat);
        if (user != null) {
            BandyerIntent.Builder bandyerIntentBuilder = new BandyerIntent.Builder();
            ChatIntentBuilder chatIntentBuilder = bandyerIntentBuilder.startWithChat(getApplicationContext());
            BandyerIntent bandyerIntent = chatIntentBuilder.with(user).build();
            startActivity(bandyerIntent);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        removeObservers();
        BandyerSDK.getInstance().disconnect();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        handleExternalUrl(intent);
        handleMissedCall(intent);
    }

    @Override
    public void onBackPressed() {
        showConfirmDialog(R.string.logout, R.string.logout_confirmation, (dialogInterface, i) -> {
            logout();
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main, menu);
        searchView = (SearchView) menu.findItem(R.id.searchMain).getActionView();
        searchView.setOnSearchClickListener(v -> ((AppBarLayout) findViewById(R.id.appbar_toolbar)).setExpanded(false, true));
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
        int itemId = item.getItemId();

        if (itemId == R.id.call_configuration) {
            showCallConfigurationDialog();
        } else if (itemId == R.id.chat_configuration) {
            showChatConfigurationDialog();
        } else if (itemId == R.id.logout) logout();

        return super.onOptionsItemSelected(item);
    }

    private void showCallConfigurationDialog() {
        com.kaleyra.app_configuration.model.Configuration configuration
                = ConfigurationPrefsManager.INSTANCE.getConfiguration(this);

        CustomConfigurationDialog.showCallConfigurationDialog(this, configuration.getDefaultCallType());

        getSupportFragmentManager().setFragmentResultListener("customize_configuration", this, (requestKey, result) -> {
            CustomCallConfiguration callConfiguration = result.getParcelable("call_configuration");
            if (callConfiguration == null) return;
            DefaultConfigurationManager.INSTANCE.saveDefaultCallConfiguration(callConfiguration);
            saveAppConfiguration((com.kaleyra.app_configuration.model.Configuration) result.getSerializable("app_configuration"));
        });
    }

    private void showChatConfigurationDialog() {
        CustomConfigurationDialog.showChatConfigurationDialog(this);
        getSupportFragmentManager().setFragmentResultListener("customize_configuration", this, (requestKey, result) -> {
            com.bandyer.android_sdk.tool_configuration.chat.ChatConfiguration chatConfiguration = result.getParcelable("chat_configuration");
            if (chatConfiguration == null) return;
            DefaultConfigurationManager.INSTANCE.saveDefaultChatConfiguration(chatConfiguration);
            saveAppConfiguration((com.kaleyra.app_configuration.model.Configuration) result.getSerializable("app_configuration"));
        });
    }

    private void logout() {
        LoginManager.logout(this);
        BandyerSDK.getInstance().disconnect(true);
        DefaultConfigurationManager.INSTANCE.clearAll();
        binding.ongoingCallLabel.setVisibility(View.GONE);
        LoginActivity.show(this);
        finish();
    }

    private void setUpRecyclerView() {
        if (fastAdapter != null && usersList.size() > 0) return;

        itemAdapter = ItemAdapter.items();
        fastAdapter = FastAdapter.with(itemAdapter);

        ExtensionsFactories.INSTANCE.register(new SelectExtensionFactory());

        SelectExtension<UserSelectionItem> selectExtension = fastAdapter.getOrCreateExtension(SelectExtension.class);
        selectExtension.setSelectable(true);

        // on user selection put it in a list to be called on click on call button.
        fastAdapter.setOnPreClickListener(new Function4<View, IAdapter<UserSelectionItem>, UserSelectionItem, Integer, Boolean>() {
            @Override
            public Boolean invoke(View view, IAdapter<UserSelectionItem> userSelectionItemIAdapter, UserSelectionItem userSelectionItem, Integer position) {
                if (!userSelectionItem.isSelected())
                    selectUser(userSelectionItem.name, position);
                else
                    deselectUser(userSelectionItem.name, position);
                return true;
            }
        });

        binding.contactsList.setItemAnimator(null);
        binding.contactsList.setLayoutManager(new LinearLayoutManager(this));
        binding.contactsList.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        binding.contactsList.setAdapter(fastAdapter);

        itemAdapter.getItemFilter().setFilterPredicate((userSelectionItem, constraint) -> userSelectionItem.name.toLowerCase().contains(constraint.toString().toLowerCase()));
        itemAdapter.getItemFilter().setItemFilterListener(new ItemFilterListener<UserSelectionItem>() {
            @Override
            public void itemsFiltered(@org.jetbrains.annotations.Nullable CharSequence charSequence, @org.jetbrains.annotations.Nullable List<? extends UserSelectionItem> results) {
                binding.noResults.post(() -> {
                    if (results.size() > 0) binding.noResults.setVisibility(View.GONE);
                    else binding.noResults.setVisibility(View.VISIBLE);
                });
            }

            @Override
            public void onReset() {
                binding.noResults.post(() -> binding.noResults.setVisibility(View.GONE));
            }
        });
    }

    private void loadUsersList() {
        onRefresh();
    }

    @Override
    public void onRefresh() {
        usersList.clear();
        itemAdapter.clear();
        binding.contactsList.invalidateItemDecorations();
        binding.loading.setVisibility(View.VISIBLE);

        // Fetch the sample users you can use to login with.
        com.kaleyra.app_utilities.MultiDexApplication.Companion.getRestApi().listUsers((com.bandyer.demo_android_sdk.mock.Users) users -> {
            binding.loading.setVisibility(android.view.View.GONE);
            // Add each user(except the logged one) to the recyclerView adapter to be displayed in the list.
            for (String user : users)
                if (!user.equals(com.kaleyra.app_utilities.storage.LoginManager.getLoggedUser(com.bandyer.demo_android_sdk.MainActivity.this)))
                    usersList.add(new com.bandyer.demo_android_sdk.ui.adapter_items.UserSelectionItem(user));

            for (String selectedItem : calleeSelected)
                if (!users.contains(selectedItem)) {
                    calleeSelected = new java.util.ArrayList<>();
                    selectedUsersItemAdapter.clear();
                    selectedUsersItemAdapter.add(new com.bandyer.demo_android_sdk.ui.adapter_items.NoUserSelectedItem());
                }

            itemAdapter.set(usersList);

            for (String userSelected : calleeSelected)
                selectUser(userSelected, usersList.indexOf(new com.bandyer.demo_android_sdk.ui.adapter_items.UserSelectionItem(userSelected)));

            if (searchView != null) itemAdapter.filter(searchView.getQuery());

            setRefreshing(false);
        });
    }

    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE)
            ((AppBarLayout) findViewById(R.id.appbar_toolbar)).setExpanded(false);
        else if (searchView != null && searchView.isIconified())
            ((AppBarLayout) findViewById(R.id.appbar_toolbar)).setExpanded(true);
    }

    private void selectUser(String userAlias, int position) {
        SelectExtension<UserSelectionItem> selectExtension = fastAdapter.getExtension(SelectExtension.class);
        selectExtension.select(position);
        if (calleeSelected.contains(userAlias)) return;
        calleeSelected.add(userAlias);
        SelectedUserItem selectedUserItem = new SelectedUserItem(userAlias, position);
        selectedUsersItemAdapter.add(0, selectedUserItem);
        binding.selectedUsersChipgroup.smoothScrollToPosition(0);
        selectedUsersItemAdapter.removeByIdentifier(NoUserSelectedItem.NO_USER_SELECTED_ITEM_IDENTIFIER);
    }

    private void deselectUser(String userAlias, int position) {
        SelectExtension<UserSelectionItem> selectExtension = fastAdapter.getExtension(SelectExtension.class);
        selectExtension.deselect(position);
        calleeSelected.remove(userAlias);
        selectedUsersItemAdapter.removeByIdentifier(userAlias.hashCode());
        if (selectedUsersItemAdapter.getAdapterItemCount() == 0)
            selectedUsersItemAdapter.add(new NoUserSelectedItem());
    }

    private void showOngoingCallLabel() {
        if (binding.ongoingCallLabel == null) return;
        binding.ongoingCallLabel.setVisibility(View.VISIBLE);
    }

    private void hideOngoingCallLabel() {
        if (binding.ongoingCallLabel == null) return;
        binding.ongoingCallLabel.setVisibility(View.GONE);
    }

    /**
     * This is how a chat is started. You must provide one users alias identifying the user your user wants to communicate with.
     * Starting a chat is an asynchronous process, failure or success is reported in the callback provided.
     * <p>
     * WARNING!!!
     * Be aware that all the observers in this SDK, MUST NOT be defined as anonymous class because the call client will have a weak reference to them to avoid leaks and other scenarios.
     * If you do implement the observer anonymously the methods may not be called.
     */
    public void onChatClicked(View view) {
        if (calleeSelected.size() == 0) {
            showErrorDialog(getResources().getString(R.string.oto_chat_error_no_selected_user));
            return;
        }
        if (calleeSelected.size() > 1) {
            showErrorDialog(getResources().getString(R.string.oto_chat_error_group_selected));
            return;
        }

        hideKeyboard(this);

        ChatIntentBuilder.ChatConfigurationBuilder chatConfigurationBuilder = new BandyerIntent.Builder()
                .startWithChat(MainActivity.this)
                .with(calleeSelected.get(0));
        BandyerIntent chatIntent = chatConfigurationBuilder.build();
        startActivity(chatIntent);
    }

    /**
     * This is how a call is started. You must provide one users alias identifying the user your user wants to communicate with.
     * Starting a chat is an asynchronous process, failure or success is reported in the callback provided.
     * <p>
     * WARNING!!!
     * Be aware that all the observers in this SDK, MUST NOT be defined as anonymous class because the call client will have a weak reference to them to avoid leaks and other scenarios.
     * If you do implement the observer anonymously the methods may not be called.
     */
    public void onCallClicked(View v) {
        if (calleeSelected.size() == 0) {
            showErrorDialog(getResources().getString(R.string.oto_call_error_no_selected_user));
            return;
        }

        hideKeyboard(this);

        com.kaleyra.app_configuration.model.Configuration configuration = ConfigurationPrefsManager.INSTANCE.getConfiguration(this);

        BandyerIntent.Builder bandyerIntentBuilder = new BandyerIntent.Builder();
        CallIntentBuilder callIntentBuilder = null;
        switch (configuration.getDefaultCallType()) {
            case AUDIO_ONLY:
                callIntentBuilder = bandyerIntentBuilder.startWithAudioCall(MainActivity.this);
                break;
            case AUDIO_UPGRADABLE:
                callIntentBuilder = bandyerIntentBuilder.startWithAudioUpgradableCall(MainActivity.this);
                break;
            case AUDIO_VIDEO:
                callIntentBuilder = bandyerIntentBuilder.startWithAudioVideoCall(MainActivity.this);
                break;
        }

        BandyerIntent bandyerIntent = callIntentBuilder
                .with(calleeSelected)
                .build();
        startActivity(bandyerIntent);
    }

    ///////////////////////////////////////////////// BANDYER SDK CLIENT MODULE OBSERVER /////////////////////////////////////////////////

    @Override
    @SuppressLint("NewApi")
    public void onModuleReady(@NonNull BandyerModule module) {
        Log.d(TAG, "onModuleReady " + module.getName());
    }

    @Override
    public void onModulePaused(@NonNull BandyerModule module) {
        Log.d(TAG, "onModulePaused " + module.getName());
    }

    @Override
    public void onModuleFailed(@NonNull final BandyerModule module, @NonNull Throwable throwable) {
        Log.e(TAG, "onModuleFailed " + module.getName() + " error " + throwable.getLocalizedMessage());
        if (throwable instanceof AuthenticationException) {
            showErrorDialog("The credentials provided are not valid!", (dialog, which) -> {
                com.kaleyra.app_configuration.model.Configuration configuration = ConfigurationPrefsManager.INSTANCE.getConfiguration(MainActivity.this);
                ConfigurationActivity.Companion.showNew(this, configuration, configuration.isMockConfiguration());
            });
        } else showErrorDialog(throwable.getMessage());
    }

    @Override
    public void onModuleStatusChanged(@NonNull BandyerModule module, @NonNull BandyerModuleStatus moduleStatus) {
        Log.d(TAG, "onModuleStatusChanged " + module.getName() + " status " + moduleStatus);
        setModuleButtonsColors(module, moduleStatus);
        if (areAllModulesConnected()) dismissErrorDialog();
    }

    private Boolean areAllModulesConnected() {
        return BandyerSDK.getInstance().getCallModule() != null
                && BandyerSDK.getInstance().getCallModule().getStatus() == BandyerModuleStatus.CONNECTED
                && BandyerSDK.getInstance().getChatModule() != null
                && BandyerSDK.getInstance().getChatModule().getStatus() == BandyerModuleStatus.CONNECTED;
    }

    private void setModuleButtonsColors(@NonNull BandyerModule module, @NonNull BandyerModuleStatus moduleStatus) {
        if (module instanceof ChatModule && binding.chat != null)
            setChatButtonColor(moduleStatus);
        else if (module instanceof CallModule && binding.call != null)
            setCallButtonColor(moduleStatus);
    }

    private void setChatButtonColor(@NonNull BandyerModuleStatus moduleStatus) {
        // the chat module is offline first, which means that you can interact with it even when you are not connected to internet
        // here we color the button in black until the module gets online
        switch (moduleStatus) {
            case CONNECTING:
            case READY:
                binding.chat.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(this, R.color.colorModuleChatOffline)));
                break;
            case CONNECTED:
                binding.chat.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(this, R.color.colorModuleConnected)));
                break;
            case DISCONNECTED:
                binding.chat.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(this, R.color.colorModuleNotActive)));
                break;
            case FAILED:
                binding.chat.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(this, R.color.colorModuleError)));
                break;
        }
    }

    private void setCallButtonColor(@NonNull BandyerModuleStatus moduleStatus) {
        switch (moduleStatus) {
            case CONNECTING:
                binding.call.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(this, R.color.colorModuleConnecting)));
                break;
            case CONNECTED:
            case READY:
                binding.call.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(this, R.color.colorModuleConnected)));
                break;
            case FAILED:
                binding.call.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(this, R.color.colorModuleError)));
                break;
            case DISCONNECTED:
                binding.call.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(this, R.color.colorModuleNotActive)));
                break;
        }
    }

    private void resetButtonsState() {
        if (binding == null || binding.call == null || binding.chat == null) return;
        binding.call.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(this, R.color.colorModuleNotActive)));
        binding.chat.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(this, R.color.colorModuleNotActive)));
    }

    ////////////////////////////////////////////////////// UTILS /////////////////////////////////////////////////////////////

    public void hideKeyboard(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        if (imm == null) return;
        View view = activity.getCurrentFocus();
        if (view == null) view = new View(activity);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    private void saveAppConfiguration(com.kaleyra.app_configuration.model.Configuration configuration) {
        Intent resultIntent = new Intent();
        resultIntent.setPackage(MainActivity.this.getPackageName());
        resultIntent.putExtra(ConfigurationActivity.CONFIGURATION_RESULT, configuration.toJson());
        resultIntent.setAction(BaseConfigurationActivity.CONFIGURATION_CALL_SETTINGS_ACTION_UPDATE);
        sendBroadcast(resultIntent);
    }
}


