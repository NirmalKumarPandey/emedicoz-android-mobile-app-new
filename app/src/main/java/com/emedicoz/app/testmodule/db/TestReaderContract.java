package com.emedicoz.app.testmodule.db;

import android.provider.BaseColumns;

public final class TestReaderContract {
    // To prevent someone from accidentally instantiating the contract class,
    // make the constructor private.
    private TestReaderContract() {
    }

    /* Inner class that defines the table contents */
    public static class TestDataTable implements BaseColumns {
        public static final String TABLE_NAME = "TestData";
        public static final String TEST_STATE = "id";
        public static final String USER_ID = "user_id";
        public static final String TEST_DATA = "response";
    }
}

