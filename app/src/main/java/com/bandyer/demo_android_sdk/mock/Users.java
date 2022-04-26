package com.bandyer.demo_android_sdk.mock;

public interface Users extends kotlin.coroutines.Continuation<java.util.List<? extends String>> {

    @androidx.annotation.NonNull
    @Override
    default kotlin.coroutines.CoroutineContext getContext() {
        return kotlin.coroutines.EmptyCoroutineContext.INSTANCE;
    }

    @Override
    @SuppressWarnings("unchecked")
    default void resumeWith(@androidx.annotation.NonNull Object o) {
        usersList((java.util.List<String>) o);
    }

    void usersList(java.util.List<String> users);
}

