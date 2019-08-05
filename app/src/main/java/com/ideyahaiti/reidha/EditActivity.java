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
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class EditActivity extends AppCompatActivity {
    int agent_id;
    int rec_id;
    EditText lastname,firstname,date,cinornif,address,phone;
    RadioGroup gender,state;
    FloatingActionButton fab;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Intent intent = getIntent();
        agent_id = intent.getIntExtra("agent_id",999);
        rec_id = intent.getIntExtra("rec_id",999);

        lastname = findViewById(R.id.add_lastname2);
        firstname = findViewById(R.id.add_firstname2);
        date = findViewById(R.id.add_birthdate2);
        cinornif = findViewById(R.id.add_cin_nif2);
        address = findViewById(R.id.add_address2);
        phone = findViewById(R.id.add_phone2);
        gender = findViewById(R.id.add_sexe2);
        state = findViewById(R.id.add_state2);
        fab = findViewById(R.id.fab_edit);

        dateFunction();
        mainFunction();

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    private void dateFunction() {
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
                new DatePickerDialog(EditActivity.this,datedialog,myCalendar.get(Calendar.YEAR),myCalendar.get(Calendar.MONTH),myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });
    }

    private void mainFunction() {
        final String url = "http://"+getText(R.string.currentIPAddress).toString()+":8080/IHSI/index.php?showcensusbyid";
        JSONObject object = new JSONObject();
        try {
            object.put("agent_id",agent_id);
            object.put("rec_id",rec_id);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, object, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    final JSONArray array = response.getJSONArray("result");

                    lastname.setText(array.getJSONObject(0).getString("lastname"));
                    firstname.setText(array.getJSONObject(0).getString("firstname"));
                    address.setText(array.getJSONObject(0).getString("address"));
                    date.setText(array.getJSONObject(0).getString("birthdate"));
                    phone.setText(array.getJSONObject(0).getString("phone"));
                    if(array.getJSONObject(0).getString("nif").contentEquals("0")){
                        cinornif.setText(array.getJSONObject(0).getString("cin"));
                    }else{
                        cinornif.setText(array.getJSONObject(0).getString("nif"));
                    }
                    switch (array.getJSONObject(0).getInt("gender_id")){
                        case 1:
                            gender.check(R.id.edit_homme);
                            break;
                        case 2:
                            gender.check(R.id.edit_femme);
                            break;
                    }

                    switch (array.getJSONObject(0).getInt("status_id")){
                        case 1:
                            state.check(R.id.edit_celibataire);
                            break;
                        case 2:
                            state.check(R.id.edit_marie);
                            break;
                        case 3:
                            state.check(R.id.edit_veuf);
                            break;
                        case 4:
                            state.check(R.id.edit_divorce);
                            break;
                        case 5:
                            state.check(R.id.edit_autre);
                            break;
                    }

                    //save
                    fab.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            //todo: save changes to db
                            String url1 = "http://"+getText(R.string.currentIPAddress).toString()+":8080/IHSI/index.php?updatecensus";
                            StringRequest stringRequest = new StringRequest(Request.Method.POST, url1, new Response.Listener<String>() {
                                @Override
                                public void onResponse(String response) {
                                    if(response.contentEquals("Ok")){
                                        Intent intent = new Intent(EditActivity.this,ShowActivity.class);
                                        try {
                                            intent.putExtra("agent_id",array.getJSONObject(0).getInt("agent_id"));
                                            intent.putExtra("Id",array.getJSONObject(0).getInt("rec_id"));
                                            startActivity(intent);
                                            finish();
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                }
                            }, new Response.ErrorListener() {
                                @Override
                                public void onErrorResponse(VolleyError error) {
                                    System.out.println("Error update: "+error.toString());
                                    Toast.makeText(EditActivity.this,"Error :"+error.toString(),Toast.LENGTH_LONG).show();
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
                                    map.put("agent_id",String.valueOf(agent_id));
                                    map.put("rec_id",String.valueOf(rec_id));
                                    return map;
                                }
                            };
                            SingletonClass.getInstance(EditActivity.this).addToRequestQueue(stringRequest);
                        }
                    });

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                System.out.println("Error fetch: "+error.toString());
                Toast.makeText(EditActivity.this,"Error :"+error.toString(),Toast.LENGTH_LONG).show();
                error.printStackTrace();
            }
        })
        {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<>();
                headers.put("Content-Type", "application/json; charset=utf-8");
                return headers;
            }
        };
        SingletonClass.getInstance(this).addToRequestQueue(jsonObjectRequest);
    }

}
