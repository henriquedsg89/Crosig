package com.gh.crosig.services;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.Context;
import android.util.Log;

/**
 * Created by henrique on 12/05/15.
 */
public class ServiceUtils {

    private static final String TAG = "SERVICE_UTILS";
    private static final String ACCOUNT_TYPE = "com.test.accounttype";
    private static final String ACCOUNT = "Account";
    private static final String ACCOUNT_SERVICE = "AccountService";

    public static Account CreateSyncAccount(Context context) {
        // Create the account type and default account
        Account newAccount = new Account(
                ACCOUNT, ACCOUNT_TYPE);
        // Get an instance of the Android account manager
        AccountManager accountManager =
                (AccountManager) context.getSystemService(
                        ACCOUNT_SERVICE);
        /*
         * Add the account and account type, no password or user data
         * If successful, return the Account object, otherwise report an error.
         */

        if (accountManager.addAccountExplicitly(newAccount, null, null)) {
            /*
             * If you don't set android:syncable="true" in
             * in your <provider> element in the manifest,
             * then call context.setIsSyncable(account, AUTHORITY, 1)
             * here.
             */
            Log.d(TAG, "Account added successful!");
        } else {
            /*
             * The account exists or some other error occurred. Log this, report it,
             * or handle it internally.
             */
            Log.e(TAG, "Account add ERROR!");
        }

        return newAccount;
    }
}
