package com.ideyahaiti.reidha;

import android.content.Context;
import android.content.Intent;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

public class LoginActivity extends AppCompatActivity {

    TextInputEditText email,passsword;
    Button button;
    ImageView imageView;
    SQLiteController controller = new SQLiteController(this);
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        email = findViewById(R.id.tb_login_email);
        passsword = findViewById(R.id.tb_login_pass);

        imageView = findViewById(R.id.logo_login);
        imageView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                //todo : After long click show dialog for admin credentials
                adminAlert();
                return false;
            }
        });

        button = findViewById(R.id.btnLogin);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(email.getText().length()==0){
                    email.setError("Empty field!");
                }else if(passsword.getText().length()==0){
                    passsword.setError("Empty field!");
                }else{
                    //todo : email and password fields are NOT empty.
                    loginProcedures();
                }
            }
        });
    }

    private void adminAlert() {
        AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
        AlertDialog alertDialog = builder.create();
        LayoutInflater inflater = (LayoutInflater) getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.admin_layout,null);
        final TextView mail = view.findViewById(R.id.admEmail);
        final TextView pass = view.findViewById(R.id.admPassword);
        Button admBtn = view.findViewById(R.id.btnAdm);
        admBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String url = "http://"+getText(R.string.currentIPAddress).toString()+":8080/IHSI/index.php?checkadmin";
                StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        switch (response){
                            case "Ok":
                                Intent intent = new Intent(LoginActivity.this,AddAgentActivity.class);
                                startActivity(intent);
                                break;
                            case "WP":
                                passsword.setError("You entered a wrong password.");
                                Toast.makeText(LoginActivity.this,"You entered a wrong password.",Toast.LENGTH_SHORT).show();
                                break;
                            case "EI":
                                email.setError("You entered an invalid email.");
                                Toast.makeText(LoginActivity.this,"You entered an invalid email.",Toast.LENGTH_SHORT).show();
                                break;
                            default:
                                Toast.makeText(LoginActivity.this,"Error "+ response+" : Try again",Toast.LENGTH_SHORT).show();
                                break;
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(LoginActivity.this,"Error adm: "+error.toString(),Toast.LENGTH_LONG).show();
                    }
                }){
                    @Override
                    protected Map<String, String> getParams() throws AuthFailureError {
                        HashMap<String,String> map = new HashMap<>();
                        map.put("email",mail.getText().toString());
                        map.put("password",pass.getText().toString());
                        return map;
                    }
                };
                SingletonClass.getInstance(LoginActivity.this).addToRequestQueue(stringRequest);
            }
        });
        alertDialog.setView(view);
        alertDialog.show();
    }

    private void loginProcedures() {
        String url = "http://"+getText(R.string.currentIPAddress).toString()+":8080/IHSI/index.php?login";
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                System.out.println("reponse"+response);
                switch (response){
                    case "Ok":
                        Intent intent = new Intent(LoginActivity.this,MainActivity.class);
                        saveCookie();
                        intent.putExtra("Email",email.getText().toString());
                        startActivity(intent);
                        finish();
                        break;
                    case "WP":
                        passsword.setError("You entered a wrong password.");
                        Toast.makeText(LoginActivity.this,"You entered a wrong password.",Toast.LENGTH_SHORT).show();
                        break;
                    case "EI":
                        email.setError("You entered an invalid email.");
                        Toast.makeText(LoginActivity.this,"You entered an invalid email.",Toast.LENGTH_SHORT).show();
                        break;
                    default:
                        Toast.makeText(LoginActivity.this,"Error "+ response+" : Try again",Toast.LENGTH_SHORT).show();
                        break;
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(LoginActivity.this,"Error : "+error.toString(),Toast.LENGTH_LONG).show();
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String,String> map = new HashMap<>();
                map.put("email",email.getText().toString());
                map.put("password",passsword.getText().toString());
                return map;
            }
        };
        SingletonClass.getInstance(this).addToRequestQueue(stringRequest);
    }

    private void saveCookie(){
        if(controller.checkEmail(email.getText().toString())){
            //todo:Update cookie data
            controller.updateLoginData(email.getText().toString(),1);
        }else{
            //todo:insert new cookie data
            controller.saveLoginDatas(email.getText().toString());
        }
    }
}
