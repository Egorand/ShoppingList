package com.example.shoppinglist.model;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.example.shoppinglist.auth.Constants;
import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.dao.RuntimeExceptionDao;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;

import java.sql.SQLException;

/**
 * Created by eandreevici on 31/07/13.
 */
public class ShoppingListSQLiteHelper extends OrmLiteSqliteOpenHelper {

    private static final String DATABASE_NAME = "shopping_list.db";
    private static final int DATABASE_VERSION = 1;

    private RuntimeExceptionDao<ShoppingItem, String> shoppingItemsDao;

    public ShoppingListSQLiteHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase, ConnectionSource connectionSource) {
        try {
            TableUtils.createTable(connectionSource, ShoppingItem.class);
        } catch (SQLException e) {
            Log.e(Constants.DEBUG_TAG, e.getLocalizedMessage(), e);
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, ConnectionSource connectionSource, int i, int i2) {
        try {
            TableUtils.dropTable(connectionSource, ShoppingItem.class, true);
            onCreate(sqLiteDatabase, connectionSource);
        } catch (SQLException e) {
            Log.e(Constants.DEBUG_TAG, e.getLocalizedMessage(), e);
        }
    }

    public RuntimeExceptionDao<ShoppingItem, String> getShoppingItemsDao() {
        if (shoppingItemsDao == null) {
            try {
                shoppingItemsDao = RuntimeExceptionDao.createDao(getConnectionSource(), ShoppingItem.class);
            } catch (SQLException e) {
                Log.e(Constants.DEBUG_TAG, e.getLocalizedMessage(), e);
            }
        }
        return shoppingItemsDao;
    }

    @Override
    public void close() {
        super.close();
        shoppingItemsDao = null;
    }
}
