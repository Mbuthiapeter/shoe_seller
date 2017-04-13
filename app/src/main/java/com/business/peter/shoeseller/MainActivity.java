package com.business.peter.shoeseller;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;

public class MainActivity extends Activity {

    Button btnLogin,btnRegister,btnPassreset;
    EditText inputUsername,inputPassword;
    private TextView loginErrorMsg;
    /**
     * Called when the activity is first created.
     */
    private static String KEY_SUCCESS = "success";
    private static String KEY_UID = "uid";
    private static String KEY_PLAIN_ID = "plain_id";
    private static String KEY_USERNAME = "uname";
    private static String KEY_PHONE = "phone";
    private static String KEY_CREATED_AT = "created_at";

    static  String TAG_SHOE_TYPE = "shoe_type";


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        inputUsername = (EditText) findViewById(R.id.username);
        inputPassword = (EditText) findViewById(R.id.pword);

        btnLogin = (Button) findViewById(R.id.btnLogin);
        btnRegister = (Button) findViewById(R.id.btnRegister);
        btnPassreset = (Button) findViewById(R.id.btnPassreset);

        loginErrorMsg = (TextView) findViewById(R.id.loginErrorMsg);

        btnPassreset.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Intent myIntent = new Intent(view.getContext(), PasswordReset.class);
                startActivityForResult(myIntent, 0);
                finish();
            }});


        btnRegister.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Intent myIntent = new Intent(view.getContext(), Register.class);
                startActivityForResult(myIntent, 0);
                finish();
            }});

        /**
 * Login button click event
 * A Toast is set to alert when the Email and Password field is empty
 **/
        btnLogin.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {

                if (  ( !inputUsername.getText().toString().equals("")) && ( !inputPassword.getText().toString().equals("")) )
                {
                    NetAsync(view);
                }
                else if ( ( !inputUsername.getText().toString().equals("")) )
                {
                    Toast.makeText(getApplicationContext(),
                            "Username field empty", Toast.LENGTH_SHORT).show();
                }
                else if ( ( !inputPassword.getText().toString().equals("")) )
                {
                    Toast.makeText(getApplicationContext(),
                            "Username field empty", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    Toast.makeText(getApplicationContext(),
                            "Username and Password field are empty", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }


    /**
     * Async Task to check whether internet connection is working.
     **/

    private class NetCheck extends AsyncTask<String,String,Boolean>
    {
        private ProgressDialog nDialog;

        @Override
        protected void onPreExecute(){
            super.onPreExecute();
            nDialog = new ProgressDialog(MainActivity.this);
            nDialog.setTitle("Network Check");
            nDialog.setMessage("Loading..");
            nDialog.setIndeterminate(false);
            nDialog.setCancelable(true);
            nDialog.show();
        }
        /**
         * Gets current device state and checks for working internet connection by trying Google.
         **/
        @Override
        protected Boolean doInBackground(String... args){



            ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo netInfo = cm.getActiveNetworkInfo();
            if (netInfo != null && netInfo.isConnected()) {
                try {
                    URL url = new URL("http://www.google.com");
                    HttpURLConnection urlc = (HttpURLConnection) url.openConnection();
                    urlc.setConnectTimeout(3000);
                    urlc.connect();
                    if (urlc.getResponseCode() == 200) {
                        return true;
                    }
                } catch (MalformedURLException e1) {
                    e1.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return false;

        }
        @Override
        protected void onPostExecute(Boolean th){

            if(th == true){
                nDialog.dismiss();
                new ProcessLogin().execute();
            }
            else{
                nDialog.dismiss();
                loginErrorMsg.setText("Error in Network Connection");
            }
        }
    }

    /**
     * Async Task to get and send data to My Sql database through JSON respone.
     **/
    private class ProcessLogin extends AsyncTask<String, String, JSONObject> {


        private ProgressDialog pDialog;

        String username,password;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            inputUsername = (EditText) findViewById(R.id.username);
            inputPassword = (EditText) findViewById(R.id.pword);
            username = inputUsername.getText().toString();
            password = inputPassword.getText().toString();
            pDialog = new ProgressDialog(MainActivity.this);
            pDialog.setTitle("Contacting Servers");
            pDialog.setMessage("Logging in ...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
        }

        @Override
        protected JSONObject doInBackground(String... args) {

            UserFunctions userFunction = new UserFunctions();
            JSONObject json = userFunction.loginUser(username, password);
            return json;
        }

        @Override
        protected void onPostExecute(JSONObject json) {
            try {
                if (json.getString(KEY_SUCCESS) != null) {

                    String res = json.getString(KEY_SUCCESS);

                    if(Integer.parseInt(res) == 1){
                        pDialog.setMessage("Loading User Space");
                        pDialog.setTitle("Getting Data");
                        DatabaseHandler db = new DatabaseHandler(getApplicationContext());
                        JSONObject json_user = json.getJSONObject("user");
                        /**
                         * Clear all previous data in SQlite database.
                         **/
                        UserFunctions logout = new UserFunctions();
                        logout.logoutUser(getApplicationContext());
                        db.addUser(json_user.getString(KEY_PHONE),json_user.getString(KEY_USERNAME),json_user.getString(KEY_UID),json_user.getString(KEY_PLAIN_ID),json_user.getString(KEY_CREATED_AT));
                        /**
                         *If JSON array details are stored in SQlite it launches the User Panel.
                         **/

                        String category = "All";
                        Intent upanel = new Intent(getApplicationContext(), Dashboard.class);
                        upanel.putExtra(TAG_SHOE_TYPE, category);
                        upanel.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        pDialog.dismiss();
                        startActivity(upanel);
                        /**
                         * Close Login Screen
                         **/
                        finish();
                    }else{

                        pDialog.dismiss();
                        loginErrorMsg.setText("Incorrect username/password");
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
    public void NetAsync(View view){
        new NetCheck().execute();
    }
}
