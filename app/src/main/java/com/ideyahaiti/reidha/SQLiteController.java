package com.ideyahaiti.reidha;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.text.method.DateTimeKeyListener;
import android.util.Log;

import java.sql.Date;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Calendar;

public class SQLiteController extends SQLiteOpenHelper {

    static String name = "cookie.db";
    static int version = 1;
    String create = "CREATE TABLE IF NOT EXISTS login(id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, email varchar(50) NOT NULL, status INTEGER NOT NULL, connectionDate Date NOT NULL)";
    public SQLiteController(Context context) {
        super(context, name, null, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(create);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
    public long saveLoginDatas(String email){
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");
        String date = dateFormat.format(calendar.getTime());

        SQLiteDatabase database = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("email",email);
        values.put("status",1);
        values.put("connectionDate",date);
        long result = database.insert("login",null,values);
        return result;
    }

    public Cursor getLoginData(){
        SQLiteDatabase database = this.getReadableDatabase();
        String query = "SELECT email AS mail FROM login WHERE status = 1 ORDER BY id DESC LIMIT 0,1";
        Cursor cursor = database.rawQuery(query,null);
        return cursor;
    }

    public Boolean checkEmail(String email){
        SQLiteDatabase database = this.getReadableDatabase();
        String query = "SELECT COUNT(email) FROM login WHERE email = ?";
        Cursor cursor = database.rawQuery(query,new String[]{email});
        if(cursor.getCount()==0)
            return true;
        else
            return false;

    }

    public void removeData(int userId){
        SQLiteDatabase database = this.getWritableDatabase();
        database.delete("login","userid = ?",new String[]{String.valueOf(userId)});
    }

    public int updateLoginData(String email,int statut){
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");
        String date = dateFormat.format(calendar.getTime());

        SQLiteDatabase database = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("email",email);
        values.put("status",statut);
        values.put("connectionDate",date);
        return database.update("login",values,"email = ?",new String[]{email});
    }

}
