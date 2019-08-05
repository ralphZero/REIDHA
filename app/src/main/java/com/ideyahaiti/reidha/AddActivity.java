package com.ideyahaiti.reidha;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioGroup;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class AddActivity extends AppCompatActivity {

    EditText lastname,firstname,date,cinornif,address,phone;
    RadioGroup gender,state;
    String email = "";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Intent intent = getIntent();
        email = intent.getStringExtra("Email");

        lastname = findViewById(R.id.add_lastname);
        firstname = findViewById(R.id.add_firstname);
        date = findViewById(R.id.add_birthdate);
        cinornif = findViewById(R.id.add_cin_nif);
        address = findViewById(R.id.add_address);
        phone = findViewById(R.id.add_phone);
        gender = findViewById(R.id.add_sexe);
        state = findViewById(R.id.add_state);

        //date
        final Calendar myCalendar = Calendar.getInstance();
        final DatePickerDialog.OnDateSetListener datedialog = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                myCalendar.set(Calendar.YEAR,year);
                myCalendar.set(Calendar.MONTH,month);
                myCalendar.set(Calendar.DAY_OF_MONTH,dayOfMonth);
                String myFormat = "yy-MM-dd";
                SimpleDateFormat dateFormat = new SimpleDateFormat(myFormat, Locale.US);
                date.setText(dateFormat.format(myCalendar.getTime()));
            }
        };
        date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DatePickerDialog(AddActivity.this,datedialog,myCalendar.get(Calendar.YEAR),myCalendar.get(Calendar.MONTH),myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

        FloatingActionButton fab = findViewById(R.id.fab_add);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //todo; save data to db
                makeCensus(view);
            }
        });

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private void makeCensus(final View view) {
        String url = "http://"+getText(R.string.currentIPAddress).toString()+":8080/IHSI/index.php?makecensus";
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                System.out.println("Rep: "+response);
                switch (response){
                    case "Ok":
                        //clean form
                        lastname.setText("");
                        firstname.setText("");
                        address.setText("");
                        phone.setText("");
                        date.setText("");
                        cinornif.setText("");
                        gender.clearCheck();
                        state.clearCheck();
                        Snackbar.make(view,"Enregistrement reussi",Snackbar.LENGTH_LONG).setAction("Ok",null).show();
                        break;
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                System.out.println(error.toString());
                error.printStackTrace();
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String,String> map = new HashMap<>();
                map.put("lastname",lastname.getText().toString());
                map.put("firstname",firstname.getText().toString());
                map.put("date",date.getText().toString());
                map.put("address",address.getText().toString());
                map.put("phone",phone.getText().toString());
                System.out.println(cinornif.getText().toString());
                if(cinornif.getText().length() > 10){
                    //CIN
                    map.put("cin",cinornif.getText().toString());
                    map.put("nif","0");
                }else{
                    //NIF
                    map.put("nif",cinornif.getText().toString());
                    map.put("cin","0");
                }
                switch (gender.getCheckedRadioButtonId()){
                    case R.id.add_homme:
                        map.put("gender_id","1");
                        break;
                    case R.id.add_femme:
                        map.put("gender_id","2");
                        break;
                }
                switch (state.getCheckedRadioButtonId()){
                    case R.id.add_celibataire:
                        map.put("status_id","1");
                        break;
                    case R.id.add_marie:
                        map.put("status_id","2");
                        break;
                    case R.id.add_veuf:
                        map.put("status_id","3");
                        break;
                    case R.id.add_divorce:
                        map.put("status_id","4");
                        break;
                    case R.id.add_autre:
                        map.put("status_id","5");
                        break;
                }
                map.put("birthdate",date.getText().toString());
                map.put("email",email);
                return map;
            }
        };
        SingletonClass.getInstance(AddActivity.this).addToRequestQueue(stringRequest);
    }

    @Override
    protected void onResume() {
        super.onResume();
        Intent intent = getIntent();
        email = intent.getStringExtra("Email");
    }
}
