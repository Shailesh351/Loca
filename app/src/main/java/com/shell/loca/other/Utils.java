package com.shell.loca.other;

import com.google.firebase.database.FirebaseDatabase;

/**
 * Created by shell on 13/11/16.
 */

public class Utils {
    private static FirebaseDatabase mDatabase;

    public static FirebaseDatabase getDatabase() {
        if (mDatabase == null) {
            mDatabase = FirebaseDatabase.getInstance();
            mDatabase.setPersistenceEnabled(true);
        }
        return mDatabase;
    }
}
