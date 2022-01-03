package com.emedicoz.app.utilso;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.emedicoz.app.modelo.VideoViewCount;

public class DBHandler extends SQLiteOpenHelper {

    //Database version.
    //Note: Increase the database version every-time you make changes to your table structure.
    private static final int DATABASE_VERSION = 1;

    //Database Name
    private static final String DATABASE_NAME = "viewCountDetails";

    //You will declare all your table names here.
    private static final String TABLE_VIEWCOUNT = "ViewCountTable";

    // Students Table Columns names
    private static final String KEY_USER_ID = "userId";
    private static final String KEY_VIDEO_ID = "videoId";
    private static final String KEY_VIEW_COUNT = "viewCount";

    //Here context passed will be of application and not activity.
    public DBHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    //This method will be called every-time the file is called.
    @Override
    public void onCreate(SQLiteDatabase db) {
        //Query to create table
        String CREATE_VIEWCOUNT_TABLE = "CREATE TABLE IF NOT EXISTS "
                + TABLE_VIEWCOUNT + "("
                + KEY_USER_ID + " TEXT, "
                + KEY_VIDEO_ID + " TEXT PRIMARY KEY, "
                + KEY_VIEW_COUNT + " INTEGER (10)" + ")";

        //Create table query executed in sqlite
        db.execSQL(CREATE_VIEWCOUNT_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        //This method will be called only if there is change in DATABASE_VERSION.

        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_VIEWCOUNT);

        // Create tables again
        onCreate(db);
    }

    /**
     * All CRUD(Create, Read, Update, Delete) Operations
     */

    // add new data
    public void addData(VideoViewCount viewCount) {
        SQLiteDatabase db = this.getWritableDatabase();

        //Content values use KEY-VALUE pair concept
        ContentValues values = new ContentValues();
        values.put(KEY_USER_ID, viewCount.getUser_id());
        values.put(KEY_VIDEO_ID, viewCount.getVideo_id());
        values.put(KEY_VIEW_COUNT, viewCount.getViewCount());

        db.insert(TABLE_VIEWCOUNT, null, values);
        db.close();
    }

    // Getting single data through ID
    public VideoViewCount getData(String userId, String videoId) {

        SQLiteDatabase db = this.getReadableDatabase();


        //You can browse to the query method to know more about the arguments.
       /* Cursor cursor = db.query(TABLE_VIEWCOUNT,
                new String[] { KEY_USER_ID, KEY_VIDEO_ID, KEY_VIEW_COUNT },
                KEY_USER_ID + "=?" ,
                new String[] { String.valueOf(userId) },
                null,
                null,
                null,
                null);*/

        Cursor cursor = db.rawQuery("SELECT * FROM ViewCountTable WHERE userId=? AND videoId=?",
                new String[]{userId, videoId});

        if (cursor != null)
            cursor.moveToFirst();

        if (cursor.moveToFirst()) {
            VideoViewCount viewCount = new VideoViewCount(
                    cursor.getString(0),
                    cursor.getString(1),
                    Integer.parseInt(cursor.getString(2)));
            return viewCount;
        } else {
            return null;
        }
    }

/*    // Getting All Students
    public List<VideoViewCount> getAllStudents() {
        List<VideoViewCount> studentList = new ArrayList<VideoViewCount>();

        // Select All Query
        String selectQuery = "SELECT  * FROM " + TABLE_VIEWCOUNT;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // Looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                VideoViewCount student = new VideoViewCount(
                        cursor.getString(0),
                        cursor.getString(1),
                        cursor.getInt(2));

                studentList.add(student);
            } while (cursor.moveToNext());
        }

        // return student list
        return studentList;
    }*/

    // Updating single data
    public String updateData(VideoViewCount viewCount) {
        SQLiteDatabase db = this.getWritableDatabase();

/*        ContentValues values = new ContentValues();
        values.put(KEY_STUDENT_NAME, student.getStudentName());
        values.put(KEY_STUDENT_EMAIL, student.getStudentEmail());

        // updating student row
        return db.update(TABLE_STUDENTS,
                values,
                KEY_STUDENT_ID + " = ?",
                new String[] { String.valueOf(student.getStudentID())});*/
        Cursor cursor = db.rawQuery("UPDATE ViewCountTable SET viewCount=? WHERE userId=? AND videoId=?",
                new String[]{String.valueOf(viewCount.getViewCount()), viewCount.getUser_id(), viewCount.getVideo_id()});

        if (cursor != null) {
            cursor.moveToFirst();
            return "Data Updated";
        } else {
            return "data not updated";
        }

    }

/*    // Deleting single student
    public void deleteStudent(VideoViewCount viewCount) {

        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_VIEWCOUNT, KEY_STUDENT_ID + " = ?",
                new String[] { String.valueOf(student.getStudentID()) });
        db.close();
    }*/


/*    // Getting students count
    public int getStudentsCount() {
        String countQuery = "SELECT  * FROM " + TABLE_VIEWCOUNT;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        cursor.close();

        // return count
        return cursor.getCount();
    }*/
}
