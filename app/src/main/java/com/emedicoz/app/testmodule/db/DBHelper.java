package com.emedicoz.app.testmodule.db;

public class DBHelper {
    public static final int DATABASE_VERSION = 2;
    public static final String DATABASE_NAME = "TestSeries.db";

    public static final String SQL_CREATE_ENTRIES =
            "CREATE TABLE " + TestReaderContract.TestDataTable.TABLE_NAME + " (" +
                    TestReaderContract.TestDataTable._ID + " INTEGER," +
                    TestReaderContract.TestDataTable.TEST_STATE + " TEXT," +
                    TestReaderContract.TestDataTable.USER_ID + " TEXT," +
                    TestReaderContract.TestDataTable.TEST_DATA + " TEXT)";

    public static final String SQL_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS " + TestReaderContract.TestDataTable.TABLE_NAME;

}
