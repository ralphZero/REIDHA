package com.ideyahaiti.reidha;

import android.Manifest;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class AddAgentActivity extends AppCompatActivity {

    static final int CAMERA_REQUEST_CODE = 111;
    static final int GALLERY_REQUEST_CODE = 222;

    EditText lastname,firstname,cinornif,email,password,conf_pass;
    ImageView imageView;
    Button saveAgent;
    FloatingActionButton fab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_agent);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        lastname = findViewById(R.id.reg_lastname);
        firstname = findViewById(R.id.reg_firstname);
        cinornif = findViewById(R.id.reg_cin_nif);
        email = findViewById(R.id.reg_email);
        password = findViewById(R.id.reg_pass);
        conf_pass = findViewById(R.id.reg_conf_pass);
        saveAgent = findViewById(R.id.btnRegister);
        fab = findViewById(R.id.fab_add_agent);
        imageView = findViewById(R.id.photo);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //todo: take photo or select from gallery
                takePhoto();
            }
        });

        saveAgent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(email.getText().length()!=0 && password.getText().toString().equals(conf_pass.getText().toString())){
                    registerProcedures();
                }else{
                    Toast.makeText(AddAgentActivity.this,"Check inserted datas.",Toast.LENGTH_LONG).show();
                }

            }
        });

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setTitle("Add New Agent");

    }
    private void registerProcedures() {
        String url = "http://"+getText(R.string.currentIPAddress).toString()+":8080/IHSI/index.php?add_agent";
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                System.out.println("Reposne "+response);
                switch (response){
                    case "Ok":
                        System.out.println("Ok");
                        Intent intent = new Intent(AddAgentActivity.this,LoginActivity.class);
                        startActivity(intent);
                        finish();
                        break;
                    default:
                        break;

                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(AddAgentActivity.this,"Error :"+error.toString(),Toast.LENGTH_LONG).show();
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String,String> map = new HashMap<>();
                map.put("lastname",lastname.getText().toString());
                map.put("firstname",firstname.getText().toString());
                if(cinornif.getText().length() > 10){
                    //CIN
                    map.put("cin",cinornif.getText().toString());
                    map.put("nif","0");
                }else{
                    //NIF
                    map.put("nif",cinornif.getText().toString());
                    map.put("cin","0");
                }
                System.out.println(cinornif.getText().toString());
                map.put("email",email.getText().toString());
                map.put("password",password.getText().toString());
                Bitmap bitmap = ((BitmapDrawable)imageView.getDrawable()).getBitmap();
                map.put("imgpath",saveToInternalStorage(bitmap));
                return map;
            }
        };
        SingletonClass.getInstance(this).addToRequestQueue(stringRequest);
    }

    //Methodes pour image
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try{
            switch (requestCode){
                case CAMERA_REQUEST_CODE:
                    if(resultCode == RESULT_OK){
                        try {
                            Bitmap photo = (Bitmap) data.getExtras().get("data");
                            imageView.setImageBitmap(photo);
                        } catch (Exception e) {
                            Toast.makeText(this, "Couldn't load photo", Toast.LENGTH_LONG).show();
                            Log.i("ImageLog",e.getMessage());
                        }
                    }
                    break;
                case GALLERY_REQUEST_CODE:
                    if(resultCode == RESULT_OK){
                        try {
                            Bitmap photo = (Bitmap) data.getExtras().get("data");
                            imageView.setImageBitmap(photo);
                        } catch (Exception e) {
                            Toast.makeText(this, "Couldn't load photo", Toast.LENGTH_LONG).show();
                            Log.i("ImageLog",e.getMessage());
                        }
                    }
                    break;
            }
        }catch (Exception e){
            System.out.println("Error: "+e.toString());
            Toast.makeText(this, "Error: "+e.toString(), Toast.LENGTH_SHORT).show();
        }
    }
    private void takePhoto(){
        final AlertDialog.Builder dialog = new AlertDialog.Builder(AddAgentActivity.this);
        dialog.setTitle("Choose..");
        String[] choix = {"Take photo","Choose photo"};
        dialog.setItems(choix, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch(which){
                    case 0:
                        //Take photo pressed
                        try{
                            if(checkCallingOrSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED)
                            {
                                ActivityCompat.requestPermissions(AddAgentActivity.this,new String[]{Manifest.permission.CAMERA},CAMERA_REQUEST_CODE);
                                Intent intent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                                dialog.dismiss();
                                startActivityForResult(intent,CAMERA_REQUEST_CODE);
                            }else{
                                Intent intent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                                dialog.dismiss();
                                startActivityForResult(intent,CAMERA_REQUEST_CODE);
                            }
                        }catch(Exception e){
                            Toast.makeText(getApplicationContext(), "Couldn't load camera app", Toast.LENGTH_LONG).show();
                            Log.i("Cameara log",e.getMessage());
                        }

                        break;
                    case 1:
                        //Choose photo pressed
                        try{
                            Intent intentGallery = new Intent(Intent.ACTION_PICK,android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                            if(intentGallery.resolveActivity(getPackageManager())!= null)
                            {
                                startActivityForResult(intentGallery,GALLERY_REQUEST_CODE);
                                dialog.dismiss();
                            }
                        }catch (Exception e){
                            Toast.makeText(getApplicationContext(), "Couldn't load gallery app", Toast.LENGTH_LONG).show();
                        }
                        break;
                }
            }
        });
        dialog.show();
    }
    private String saveToInternalStorage(Bitmap bitmapImage){
        ContextWrapper cw = new ContextWrapper(getApplicationContext());
        // path to /data/data/yourapp/app_data/imageDir
        File directory = cw.getDir("imageDir."+firstname.getText().toString()+lastname.getText().toString(), Context.MODE_PRIVATE);
        // Create imageDir
        File mypath=new File(directory,"profile.jpg");

        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(mypath);
            // Use the compress method on the BitMap object to write image to the OutputStream
            bitmapImage.compress(Bitmap.CompressFormat.PNG, 100, fos);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                fos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return directory.getAbsolutePath();
    }
}
