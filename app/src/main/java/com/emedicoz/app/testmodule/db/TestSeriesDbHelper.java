package com.emedicoz.app.testmodule.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.emedicoz.app.utilso.SharedPreference;

import static com.emedicoz.app.testmodule.db.DBHelper.DATABASE_NAME;
import static com.emedicoz.app.testmodule.db.DBHelper.DATABASE_VERSION;
import static com.emedicoz.app.testmodule.db.DBHelper.SQL_CREATE_ENTRIES;
import static com.emedicoz.app.testmodule.db.DBHelper.SQL_DELETE_ENTRIES;

public class TestSeriesDbHelper extends SQLiteOpenHelper {
    // If you change the database schema, you must increment the database version.


    public TestSeriesDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_ENTRIES);
    }

    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // This database is only a cache for online data, so its upgrade policy is
        // to simply to discard the data and start over
        db.execSQL(SQL_DELETE_ENTRIES);
        onCreate(db);
    }

    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }

    public boolean insertTestSeries(Integer id, String status, String response) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(TestReaderContract.TestDataTable._ID, id);
        contentValues.put(TestReaderContract.TestDataTable.TEST_STATE, status);
        contentValues.put(TestReaderContract.TestDataTable.USER_ID, SharedPreference.getInstance().getLoggedInUser().getId());
        contentValues.put(TestReaderContract.TestDataTable.TEST_DATA, response);
        db.insert(TestReaderContract.TestDataTable.TABLE_NAME, null, contentValues);
        return true;
    }

    public Cursor getTestSeriesByid(int id) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res = db.rawQuery("select * from " + TestReaderContract.TestDataTable.TABLE_NAME + " where " +
                TestReaderContract.TestDataTable._ID + "=" + id + " and " +
                TestReaderContract.TestDataTable.USER_ID + "=" + SharedPreference.getInstance().getLoggedInUser().getId()
                + "", null);
        return res;
    }

    public boolean updateTestSeries(Integer id, String status, String response) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(TestReaderContract.TestDataTable._ID, id);
        contentValues.put(TestReaderContract.TestDataTable.TEST_STATE, status);
        contentValues.put(TestReaderContract.TestDataTable.TEST_DATA, response);
        db.update(TestReaderContract.TestDataTable.TABLE_NAME, contentValues, TestReaderContract.TestDataTable._ID + "= ? "
                + " and " + TestReaderContract.TestDataTable.USER_ID + " = ? ", new String[]{Integer.toString(id),
                SharedPreference.getInstance().getLoggedInUser().getId()});
        return true;
    }

    public Integer deleteTestSeries(String id) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(TestReaderContract.TestDataTable.TABLE_NAME,
                TestReaderContract.TestDataTable._ID + " = ? " + " and " + TestReaderContract.TestDataTable.USER_ID + " = ? ",
                new String[]{id, SharedPreference.getInstance().getLoggedInUser().getId()});
    }
}

