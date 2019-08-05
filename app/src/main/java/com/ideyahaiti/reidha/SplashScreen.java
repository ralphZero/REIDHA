package com.ideyahaiti.reidha;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

public class SplashScreen extends AppCompatActivity {

    SQLiteController controller = new SQLiteController(SplashScreen.this);
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        checkIfLoggedIn();
    }

    private void checkIfLoggedIn() {
        Cursor cursor = controller.getLoginData();
        if(cursor.getCount()==0){
            //todo: first signin or no logged account
            Intent intent = new Intent(SplashScreen.this,LoginActivity.class);
            startActivity(intent);
            finish();
        }else{
            //todo: get email from last connection and put extra to intent
            String email = null;
            cursor.moveToFirst();
            if (cursor.moveToFirst()){
                email = cursor.getString(cursor.getColumnIndex("mail"));
            }

            System.out.println(email);
            Intent intent = new Intent(SplashScreen.this,MainActivity.class);
            //ici on met dans intent but on devrais aller chercher l userId associe puis mettre dans intent extra
            //intent.putExtra("Email",email);

            //save email to preferences
            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
            SharedPreferences.Editor editor = preferences.edit();
            editor.putString("Email",email);
            editor.apply();

            //update database cookie with last connected date
            controller.updateLoginData(email,1);
            startActivity(intent);
            finish();
        }
    }
}
