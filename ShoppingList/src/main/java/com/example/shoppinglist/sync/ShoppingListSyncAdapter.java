package com.example.shoppinglist.sync;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.Context;
import android.content.Intent;
import android.content.SyncResult;
import android.os.Bundle;
import android.util.Log;

import com.example.shoppinglist.MainActivity;
import com.example.shoppinglist.auth.Constants;
import com.example.shoppinglist.model.ShoppingItem;
import com.example.shoppinglist.model.ShoppingListSQLiteHelper;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by eandreevici on 01/08/13.
 */
public class ShoppingListSyncAdapter extends AbstractThreadedSyncAdapter {

    private final AccountManager accountManager;
    private final ShoppingListSQLiteHelper dbHelper;

    public ShoppingListSyncAdapter(Context context, boolean autoInitialize) {
        super(context, autoInitialize);
        this.accountManager = AccountManager.get(context);
        this.dbHelper = new ShoppingListSQLiteHelper(context);
    }

    @Override
    public void onPerformSync(Account account, Bundle extras, String authority, ContentProviderClient provider, SyncResult syncResult) {
        try {
            List<ShoppingItem> localItems = dbHelper.getShoppingItemsDao().queryForAll();
            List<ShoppingItem> remoteItems = new ArrayList<ShoppingItem>();
            ParseQuery<ParseObject> query = ParseQuery.getQuery("ShoppingItem");
            List<ParseObject> objects = query.find();
            for (ParseObject object : objects) {
                Log.d(Constants.DEBUG_TAG, "parseObject: " + object.toString());
                Log.d(Constants.DEBUG_TAG, "shoppingItem: " + ShoppingItem.fromParseObject(object).toString());
                remoteItems.add(ShoppingItem.fromParseObject(object));
            }
            for (ShoppingItem localItem : localItems) {
                if (!remoteItems.contains(localItem)) {
                    localItem.toParseObject().save();
                }
            }
            for (ShoppingItem remoteItem : remoteItems) {
                if (!localItems.contains(remoteItem)) {
                    dbHelper.getShoppingItemsDao().create(remoteItem);
                }
            }
        } catch (ParseException e) {
            Log.e(Constants.DEBUG_TAG, e.getLocalizedMessage(), e);
        }
        Intent intent = new Intent(MainActivity.SyncFinishedReceiver.ACTION_SYNC_FINISHED);
        getContext().sendBroadcast(intent);
    }
}
