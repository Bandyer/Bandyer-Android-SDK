/*
 * Copyright (C) 2018 Bandyer S.r.l. All Rights Reserved.
 * See LICENSE.txt for licensing information
 */

package com.bandyer.demo_android_sdk.dummy;

import android.os.Handler;
import android.os.Looper;
import com.bandyer.android_common.fetcher.OnUserImageFetcherObserver;
import com.bandyer.android_common.fetcher.OnUserInformationFetcherObserver;
import com.bandyer.android_common.fetcher.UserDisplayInfo;
import com.bandyer.android_common.fetcher.UserImageDisplayInfo;
import com.bandyer.android_common.fetcher.UserInformationFetcher;

import org.jetbrains.annotations.NotNull;
import java.util.Random;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class DummyUserFetcher extends UserInformationFetcher {

    private static Executor background = Executors.newSingleThreadExecutor();
    private static android.os.Handler handler = new Handler(Looper.getMainLooper());

    private static String[] Beginning = { "Kr", "Ca", "Ra", "Mrok", "Cru",
            "Ray", "Bre", "Zed", "Drak", "Mor", "Jag", "Mer", "Jar", "Mjol",
            "Zork", "Mad", "Cry", "Zur", "Creo", "Azak", "Azur", "Rei", "Cro",
            "Mar", "Luk" };
    private static String[] Middle = { "air", "ir", "mi", "sor", "mee", "clo",
            "red", "cra", "ark", "arc", "miri", "lori", "cres", "mur", "zer",
            "marac", "zoir", "slamar", "salmar", "urak" };
    private static String[] End = { "d", "ed", "ark", "arc", "es", "er", "der",
            "tron", "med", "ure", "zur", "cred", "mur" };

    private static String[] Mails = { "gmail.com", "hotmail.com", "ymail.com" };

    private static Random rand = new Random();

    @Override
    public void fetchUser(@NotNull final String userAlias, @NotNull final OnUserInformationFetcherObserver onUserInformationFetcherObserver) {
        onUserInformationFetcherObserver.onUserFetched(generateUserDisplayInfo(userAlias));
    }

    @Override
    public void fetchUserImage(final @NotNull String userAlias, final @NotNull OnUserImageFetcherObserver onUserImageFetcherObserver) {
        onUserImageFetcherObserver.onUserImagesFetched(new UserImageDisplayInfo.Builder(userAlias).withImageUrl("https://cdn8.bigcommerce.com/s-29i296wym3/images/stencil/1024x1024/products/3118/6551/HEISENBERG__89676.1525428438.jpg?c=2"));
    }

    private UserDisplayInfo generateUserDisplayInfo(String userAlias) {
        String nickName = generateNickName();

        return new UserDisplayInfo.Builder(userAlias)
                .withFirstName(generateFirstName())
                .withLastName(generateLastName())
                .withNickName(nickName)
                .withEmail(generateMail(nickName))
                .build();
    }

    private String generateFirstName() {
        return Beginning[rand.nextInt(Beginning.length)] + Middle[rand.nextInt(Middle.length)];
    }

    private String generateNickName() {
        return Middle[rand.nextInt(Middle.length)];
    }

    private String generateLastName() {
        return Beginning[rand.nextInt(Beginning.length)] + End[rand.nextInt(End.length)];
    }

    private  String generateMail(String name) {
        return name+"@"+Mails[rand.nextInt(Mails.length)];
    }
}