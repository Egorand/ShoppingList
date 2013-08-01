package com.example.shoppinglist.sync;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

/**
 * Created by eandreevici on 01/08/13.
 */
public class ShoppingListSyncService extends Service {

    private static final Object syncAdapterLock = new Object();
    private static ShoppingListSyncAdapter syncAdapter = null;

    @Override
    public void onCreate() {
        synchronized (syncAdapterLock) {
            if (syncAdapter == null) {
                syncAdapter = new ShoppingListSyncAdapter(getApplicationContext(), true);
            }
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return syncAdapter.getSyncAdapterBinder();
    }
}
