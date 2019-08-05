package com.ideyahaiti.reidha;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Cache;
import com.android.volley.Network;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.BasicNetwork;
import com.android.volley.toolbox.DiskBasedCache;
import com.android.volley.toolbox.HurlStack;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    static String email;

    ListView listView;
    CustomAdapter adapter;
    ArrayList<Census> arrayList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        email = preferences.getString("Email", "");

        listView = findViewById(R.id.listview);
        System.out.println("Email"+email);
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent1 = new Intent(MainActivity.this,AddActivity.class);
                intent1.putExtra("Email",email);
                startActivity(intent1);
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        //set text to nav items
        setAgentDatasToNavigation();

        //main function to retreive data from server to listview
        mainFunction();


    }

    private void mainFunction() {
        String url = "http://"+getText(R.string.currentIPAddress).toString()+":8080/IHSI/index.php?showcensus";
        arrayList = new ArrayList<>();
        JSONObject object = new JSONObject();
        try {
            object.put("email",email);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, object, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    System.out.println("resp :"+response.toString());
                    JSONArray array = response.getJSONArray("result");
                    for(int i=0;i<array.length();i++){
                        Census census = new Census();
                        census.setId(array.getJSONObject(i).getInt("rec_id"));
                        census.setLastname(array.getJSONObject(i).getString("lastname"));
                        census.setFirstname(array.getJSONObject(i).getString("firstname"));
                        census.setBirthdate(array.getJSONObject(i).getString("birthdate"));
                        census.setAddress(array.getJSONObject(i).getString("address"));
                        census.setNif(array.getJSONObject(i).getString("nif"));
                        census.setCin(array.getJSONObject(i).getString("cin"));
                        census.setPhone(array.getJSONObject(i).getString("phone"));
                        census.setGender_id(array.getJSONObject(i).getInt("gender_id"));
                        census.setStatus_id(array.getJSONObject(i).getInt("status_id"));
                        census.setAgent_id(array.getJSONObject(i).getInt("agent_id"));
                        arrayList.add(census);
                    }
                    adapter = new CustomAdapter(MainActivity.this,arrayList);

                    listView.setAdapter(adapter);
                    listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            Intent intent = new Intent(MainActivity.this,ShowActivity.class);
                            intent.putExtra("agent_id",arrayList.get(position).getAgent_id());
                            intent.putExtra("Id",arrayList.get(position).getId());
                            startActivity(intent);
                        }
                    });
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(MainActivity.this,"Error listview: "+error.toString(),Toast.LENGTH_LONG).show();
                System.out.println("Error listview: "+error.toString());
                error.printStackTrace();
            }
        }){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<>();
                headers.put("Content-Type", "application/json; charset=utf-8");
                return headers;
            }
        };
        SingletonClass.getInstance(this).addToRequestQueue(jsonObjectRequest);

        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                final int pos = position;
                final AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                String[] item = {"Ouvrir","Modifier","Retirer"};
                builder.setItems(item, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which){
                            case 0:
                                Intent intent = new Intent(MainActivity.this,ShowActivity.class);
                                intent.putExtra("agent_id",arrayList.get(pos).getAgent_id());
                                intent.putExtra("Id",arrayList.get(pos).getId());
                                dialog.dismiss();
                                startActivity(intent);
                                break;
                            case 1:
                                Intent intent1 = new Intent(MainActivity.this,EditActivity.class);
                                intent1.putExtra("agent_id",arrayList.get(pos).getAgent_id());
                                intent1.putExtra("rec_id",arrayList.get(pos).getId());
                                dialog.dismiss();
                                startActivity(intent1);
                                break;
                            case 2:
                                deleteFunction(arrayList.get(pos).getId(),arrayList.get(pos).getAgent_id());
                                break;
                        }
                    }
                });
                builder.show();
                return true;
            }
        });
    }


    //fonction delete
    private void deleteFunction(final int rec_id, final int agent_id) {

        String url = "http://"+getText(R.string.currentIPAddress).toString()+":8080/IHSI/index.php?deletecensus&agent_id="+agent_id+"&rec_id="+rec_id;

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                if(response.contentEquals("Ok")){
                    System.out.println("Rep: "+response);
                    Intent intent = new Intent(MainActivity.this,MainActivity.class);
                    startActivity(intent);
                    finish();
                }else{
                    System.out.println("not deleted.");
                }
                System.out.println(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(MainActivity.this,"Error delete: "+error.toString(),Toast.LENGTH_LONG).show();
                System.out.println("Error delete: "+error.toString());
                error.printStackTrace();
            }
        });
        SingletonClass.getInstance(this).addToRequestQueue(stringRequest);
    }


    //other functions
    private void setAgentDatasToNavigation() {
        JSONObject object = new JSONObject();
        try {
            object.put("email",email);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        String url = "http://"+getText(R.string.currentIPAddress).toString()+":8080/IHSI/index.php?get_agent_data";

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url,object, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                System.out.println("Reponse: "+response);
                try {
                    JSONArray array = response.getJSONArray("result");

                    NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
                    View headerView = navigationView.getHeaderView(0);

                    TextView navname = headerView.findViewById(R.id.nav_name);
                    navname.setText(array.getJSONObject(0).getString("lastname")+" "+array.getJSONObject(0).getString("firstname"));

                    TextView navemail = headerView.findViewById(R.id.nav_email);
                    navemail.setText(email);

                    ImageView navPhoto = headerView.findViewById(R.id.nav_image);
                    loadImageFromStorage(array.getJSONObject(0).getString("img"),navPhoto);

                } catch (JSONException e) {
                    e.printStackTrace();
                }



            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(MainActivity.this,"Error navHeader: "+error.toString(),Toast.LENGTH_LONG).show();
                System.out.println("Error navHeader: "+error.toString() + error.getMessage());
                error.printStackTrace();
            }
        }){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<>();
                headers.put("Content-Type", "application/json; charset=utf-8");
                return headers;
            }
        };
        SingletonClass.getInstance(this).addToRequestQueue(jsonObjectRequest);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        MenuItem menuItem = (MenuItem) menu.findItem(R.id.app_bar_search);

        SearchView searchView = (SearchView) menuItem.getActionView();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                if (TextUtils.isEmpty(s)) {
                    adapter.filter("");
                    listView.clearTextFilter();
                } else {
                    adapter.filter(s);
                }
                return true;
            }
        });
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id){
            case R.id.btnLogout:
                //todo:Logout
                SQLiteController controller = new SQLiteController(MainActivity.this);
                controller.updateLoginData(email,0);
                Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
                break;
            case R.id.ic_refresh:
                    //todo: Refresh the listview
                mainFunction();
                break;
        }
        return true;
    }

    private void loadImageFromStorage(String path, ImageView img)
    {
        try {
            File f=new File(path, "profile.jpg");
            Bitmap b = BitmapFactory.decodeStream(new FileInputStream(f));
            img.setImageBitmap(b);
        }
        catch (FileNotFoundException e)
        {
            e.printStackTrace();
        }

    }

}
