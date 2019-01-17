/*
 * Copyright (C) 2018 Bandyer S.r.l. All Rights Reserved.
 * See LICENSE.txt for licensing information
 */

package com.bandyer.demo_android_sdk.utils.networking;

import java.util.List;

/**
 * @author kristiyan
 */

public class DemoAppUsers {

    public List<DemoAppUser> results;

    public class DemoAppUser {
        public Name name;

        public String email;

        public Picture picture;


        public class Name {
            public String first;
            public String last;
        }

        public class Picture {
            public String large;
        }
    }

}
