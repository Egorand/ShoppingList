package com.example.shoppinglist;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.shoppinglist.auth.AccountUtils;
import com.example.shoppinglist.auth.ShoppingListAccountInfo;
import com.example.shoppinglist.model.ORMLiteLoader;
import com.example.shoppinglist.model.ShoppingItem;
import com.example.shoppinglist.model.ShoppingListSQLiteHelper;
import com.google.inject.Inject;

import java.util.List;

import roboguice.activity.RoboFragmentActivity;
import roboguice.inject.ContentView;
import roboguice.inject.InjectView;

/**
 * Created by eandreevici on 31/07/13.
 */
@ContentView(R.layout.activity_main)
public class MainActivity extends RoboFragmentActivity implements LoaderManager.LoaderCallbacks<List<ShoppingItem>>,
        View.OnClickListener {

    public static final int REQUEST_LOGIN = 1;

    @Inject
    private AccountManager accountManager;
    private LoaderManager loaderManager;

    private ShoppingListSQLiteHelper dbHelper;

    private SyncFinishedReceiver syncFinishedReceiver;

    @InjectView(android.R.id.list)
    private ListView listView;
    @InjectView(R.id.addNewItemLayout)
    private RelativeLayout addNewItemLayout;
    @InjectView(R.id.newItemEt)
    private EditText addNewItemEt;
    @InjectView(R.id.addNewItemBtn)
    private ImageButton addNewItemBtn;
    @InjectView(R.id.emptyView)
    private TextView emptyView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.dbHelper = new ShoppingListSQLiteHelper(this);
        this.loaderManager = getSupportLoaderManager();
        listView.setEmptyView(emptyView);
        addNewItemBtn.setOnClickListener(this);
        if (checkRegistration()) {
            Toast.makeText(this, "Welcome!", Toast.LENGTH_LONG).show();
        }
        this.syncFinishedReceiver = new SyncFinishedReceiver();
    }


    @Override
    protected void onResume() {
        super.onResume();
        loaderManager.restartLoader(0, null, this);
        IntentFilter filter = new IntentFilter(SyncFinishedReceiver.ACTION_SYNC_FINISHED);
        registerReceiver(syncFinishedReceiver, filter);
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(syncFinishedReceiver);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.logout) {
            AccountUtils.invalidateToken(this, AccountUtils.getAccount(this, ShoppingListAccountInfo.ACCOUNT_TYPE),
                    ShoppingListAccountInfo.AUTHTOKEN_TYPE_STANDARD);
            finish();
            return true;
        } else if (item.getItemId() == R.id.add) {
            addNewItemLayout.setVisibility(View.VISIBLE);
            addNewItemEt.requestFocus();
        } else if (item.getItemId() == R.id.refresh) {
            Bundle bundle = new Bundle();
            bundle.putBoolean(ContentResolver.SYNC_EXTRAS_MANUAL, true);
            bundle.putBoolean(ContentResolver.SYNC_EXTRAS_EXPEDITED, true);
            ContentResolver.requestSync(AccountUtils.getAccount(this, ShoppingListAccountInfo.ACCOUNT_TYPE), ShoppingListAccountInfo.AUTHORITY, bundle);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == REQUEST_LOGIN) {
                if (checkRegistration()) {
                    Toast.makeText(this, "Welcome!", Toast.LENGTH_LONG).show();
                    loaderManager.initLoader(0, null, this);
                }
            }
        } else {
            finish();
        }
    }

    private boolean checkRegistration() {
        Account registeredAccount = AccountUtils.getAccount(this, ShoppingListAccountInfo.ACCOUNT_TYPE);
        if (registeredAccount == null || !AccountUtils.isTokenValid(this, registeredAccount, ShoppingListAccountInfo.AUTHTOKEN_TYPE_STANDARD)) {
            Intent loginIntent = new Intent(this, LoginActivity.class);
            if (registeredAccount != null) {
                loginIntent.putExtra(LoginActivity.ARG_DEFAULT_EMAIL, registeredAccount.name);
            }
            startActivityForResult(loginIntent, REQUEST_LOGIN);
            return false;
        }
        return true;
    }

    @Override
    public Loader<List<ShoppingItem>> onCreateLoader(int id, Bundle args) {
        return new ORMLiteLoader<ShoppingItem, String>(this, dbHelper.getShoppingItemsDao());
    }

    @Override
    public void onLoadFinished(Loader<List<ShoppingItem>> loader, List<ShoppingItem> data) {
        ArrayAdapter<ShoppingItem> adapter = new ArrayAdapter<ShoppingItem>(this, android.R.layout.simple_list_item_1, data);
        listView.setAdapter(adapter);
    }

    @Override
    public void onLoaderReset(Loader<List<ShoppingItem>> loader) {
        listView.setAdapter(null);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.addNewItemBtn) {
            if (!TextUtils.isEmpty(addNewItemEt.getText())) {
                ShoppingItem item = new ShoppingItem(addNewItemEt.getText().toString());
                dbHelper.getShoppingItemsDao().create(item);
                addNewItemLayout.setVisibility(View.GONE);
                loaderManager.restartLoader(0, null, this);
            }
        }
    }

    public class SyncFinishedReceiver extends BroadcastReceiver {

        public static final String ACTION_SYNC_FINISHED = "sync_finished";

        @Override
        public void onReceive(Context context, Intent intent) {
            loaderManager.restartLoader(0, null, MainActivity.this);
        }
    }
}
