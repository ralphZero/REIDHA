package com.ideyahaiti.reidha;

import android.app.DownloadManager;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ShowActivity extends AppCompatActivity {

    ArrayList<Census> arrayList;
    int agent_id;
    int id;
    TextView lastname,firstname,gender,address,phone,state,birthdate,cinounif;
    FloatingActionButton fab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        final Intent intent = getIntent();

        agent_id = intent.getIntExtra("agent_id",999);
        id = intent.getIntExtra("Id",999);

        lastname = findViewById(R.id.show_lastname);
        firstname = findViewById(R.id.show_firstname);
        gender = findViewById(R.id.show_sexe);
        address = findViewById(R.id.show_address);
        phone = findViewById(R.id.show_phone);
        state = findViewById(R.id.show_state);
        birthdate = findViewById(R.id.show_birthdate);
        cinounif = findViewById(R.id.show_cinornif);
        fab = findViewById(R.id.fab_show);

        String url = "http://"+getText(R.string.currentIPAddress).toString()+":8080/IHSI/index.php?showcensusbyid";
        JSONObject object = new JSONObject();
        try {
            object.put("agent_id",agent_id);
            object.put("rec_id",id);
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
                    phone.setText(array.getJSONObject(0).getString("phone"));
                    birthdate.setText(array.getJSONObject(0).getString("birthdate"));

                    if(array.getJSONObject(0).getString("nif").contentEquals("0")){
                        cinounif.setText(array.getJSONObject(0).getString("cin")+"  (CIN)");
                    }else{
                        cinounif.setText(array.getJSONObject(0).getString("nif")+"  (NIF)");
                    }

                    switch (array.getJSONObject(0).getInt("gender_id")){
                        case 1:
                            gender.setText("Homme");
                            break;
                        case 2:
                            gender.setText("Femme");
                            break;
                    }

                    switch (array.getJSONObject(0).getInt("status_id")){
                        case 1:
                            state.setText("Célibataire");
                            break;
                        case 2:
                            state.setText("Marié(e)");
                            break;
                        case 3:
                            state.setText("Veuf(ve)");
                            break;
                        case 4:
                            state.setText("Divorcé(e)");
                            break;
                        case 5:
                            state.setText("Autre");
                            break;
                    }

                    fab.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            //todo: put extra and startActivity
                            Intent intent1 = new Intent(ShowActivity.this, EditActivity.class);
                            try {
                                intent1.putExtra("agent_id",array.getJSONObject(0).getInt("agent_id"));
                                intent1.putExtra("rec_id",array.getJSONObject(0).getInt("rec_id"));
                                startActivity(intent1);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            startActivity(intent1);
                        }
                    });

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                System.out.println(error.toString());
                Toast.makeText(ShowActivity.this,"Error :"+error.toString(),Toast.LENGTH_LONG).show();
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
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

}
