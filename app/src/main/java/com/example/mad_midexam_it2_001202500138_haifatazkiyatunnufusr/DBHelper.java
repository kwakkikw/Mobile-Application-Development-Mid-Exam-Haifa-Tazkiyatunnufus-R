package com.example.mad_midexam_it2_001202500138_haifatazkiyatunnufusr;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteConstraintException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

public class DBHelper extends SQLiteOpenHelper {
    private static final int DB_VERSION = 1;
    private static final String DB_NAME = "student_db";
    private static final String TB_NAME = "student";
    private static final String Col_ID = "id";
    private static final String Col_Name = "name";
    private static final String Col_StudentID = "student_id";
    private static final String Col_Enrolled = "enrolled";
    private SQLiteDatabase mySQLite;

    private static final String CREATE_TABLE = "CREATE TABLE " + TB_NAME + "("
            + Col_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + Col_Name + " TEXT NOT NULL, "
            + Col_StudentID + " TEXT NOT NULL UNIQUE, "
            + Col_Enrolled + " INTEGER NOT NULL DEFAULT 0);";

    public DBHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TB_NAME);
        onCreate(db);
    }

    public boolean isStudentIdExists(String studentId, int excludeId) {
        mySQLite = this.getReadableDatabase();
        Cursor cursor = mySQLite.rawQuery(
                "SELECT " + Col_ID + " FROM " + TB_NAME
                        + " WHERE " + Col_StudentID + " = ? AND " + Col_ID + " != ?",
                new String[]{studentId, String.valueOf(excludeId)});
        boolean exists = cursor.getCount() > 0;
        cursor.close();
        return exists;
    }

    public boolean addStudent(StudentModel studentModel) {
        if (isStudentIdExists(studentModel.getStudentId(), -1)) {
            return false;
        }
        ContentValues contentValues = new ContentValues();
        contentValues.put(Col_Name, studentModel.getName());
        contentValues.put(Col_StudentID, studentModel.getStudentId());
        contentValues.put(Col_Enrolled, studentModel.isEnrolled() ? 1 : 0);
        mySQLite = this.getWritableDatabase();
        try {
            long result = mySQLite.insert(TB_NAME, null, contentValues);
            return result != -1;
        } catch (SQLiteConstraintException e) {
            return false;
        }
    }

    public boolean updateStudent(StudentModel studentModel) {
        if (isStudentIdExists(studentModel.getStudentId(), studentModel.getId())) {
            return false;
        }
        ContentValues contentValues = new ContentValues();
        contentValues.put(Col_Name, studentModel.getName());
        contentValues.put(Col_StudentID, studentModel.getStudentId());
        contentValues.put(Col_Enrolled, studentModel.isEnrolled() ? 1 : 0);
        mySQLite = this.getWritableDatabase();
        try {
            mySQLite.update(TB_NAME, contentValues, Col_ID + " = ?",
                    new String[]{String.valueOf(studentModel.getId())});
            return true;
        } catch (SQLiteConstraintException e) {
            return false;
        }
    }

    public void deleteStudent(int id) {
        mySQLite = this.getWritableDatabase();
        mySQLite.delete(TB_NAME, Col_ID + " = ?", new String[]{String.valueOf(id)});
    }

    public List<StudentModel> getStudentList() {
        String viewData = "SELECT id, name, student_id, enrolled FROM " + TB_NAME;
        mySQLite = this.getReadableDatabase();
        List<StudentModel> storeStudent = new ArrayList<>();
        Cursor cursor = mySQLite.rawQuery(viewData, null);
        if (cursor.moveToFirst()) {
            do {
                int id = cursor.getInt(0);
                String name = cursor.getString(1);
                String studentId = cursor.getString(2);
                boolean enrolled = cursor.getInt(3) == 1;

                storeStudent.add(new StudentModel(id, name, studentId, enrolled));
            } while (cursor.moveToNext());
        }
        cursor.close();
        return storeStudent;
    }
}