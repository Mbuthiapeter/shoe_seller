package com.business.peter.shoeseller;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by Victor on 06/07/2016.
 */
public class Register extends Activity {
    /**
     *  JSON Response node names.
     **/


    private static String KEY_SUCCESS = "success";
    private static String KEY_UID = "uid";
    private static String KEY_USERNAME = "uname";
    private static String KEY_PHONE = "phone";
    private static String KEY_CREATED_AT = "created_at";
    private static String KEY_ERROR = "error";

    /**
     * Defining layout items.
     **/

    EditText inputUsername,phone_number, inputPassword;
    Button btnRegister,btnCancel;
    TextView registerErrorMsg;


    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register);

        /**
         * Defining all layout items
         **/
        inputUsername = (EditText) findViewById(R.id.username);
        phone_number = (EditText) findViewById(R.id.phone_number);
        inputPassword = (EditText) findViewById(R.id.pword);
        btnRegister = (Button) findViewById(R.id.btnRegister);
        btnCancel = (Button) findViewById(R.id.btnCancel);
        registerErrorMsg = (TextView) findViewById(R.id.registerError);
/**
         * Register Button click event.
         * A Toast is set to alert when the fields are empty.
         * Another toast is set to alert Username must be 5 characters.
         **/


        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (  ( !inputUsername.getText().toString().equals("")) && ( !inputPassword.getText().toString().equals("")) && ( !phone_number.getText().toString().equals("")) )
                {

                        NetAsync(view);
                   // new ProcessRegister().execute();

                }
                else
                {
                    Toast.makeText(getApplicationContext(),
                            "One or more fields are empty", Toast.LENGTH_LONG).show();
                }
            }
        });
    }
    /**
     * Async Task to check whether internet connection is working
     **/

    private class NetCheck extends AsyncTask<String,String,Boolean>
    {
        private ProgressDialog nDialog;

        @Override
        protected void onPreExecute(){
            super.onPreExecute();
            nDialog = new ProgressDialog(Register.this);
            nDialog.setMessage("Loading..");
            nDialog.setTitle("Network check");
            nDialog.setIndeterminate(false);
            nDialog.setCancelable(true);
            nDialog.show();
        }

        @Override
        protected Boolean doInBackground(String... args){


/**
 * Gets current device state and checks for working internet connection by trying Google.
 **/
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
                new ProcessRegister().execute();
            }
            else{
                nDialog.dismiss();
                registerErrorMsg.setText("Error in Network Connection");
            }
        }
    }





    private class ProcessRegister extends AsyncTask<String, String, JSONObject> {

        /**
         * Defining Process dialog
         **/
        private ProgressDialog pDialog;

        String password,phone,uname;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            inputUsername = (EditText) findViewById(R.id.username);
            inputPassword = (EditText) findViewById(R.id.pword);
            phone_number = (EditText) findViewById(R.id.phone_number);

            phone = phone_number.getText().toString();
            uname= inputUsername.getText().toString();
            password = inputPassword.getText().toString();

            pDialog = new ProgressDialog(Register.this);
            pDialog.setTitle("Contacting Servers");
            pDialog.setMessage("Registering ...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
        }

        @Override
        protected JSONObject doInBackground(String... args) {


            UserFunctions userFunction = new UserFunctions();
            JSONObject json = userFunction.registerUser(phone, uname, password);

            return json;


        }
        @Override
        protected void onPostExecute(JSONObject json) {
            /**
             * Checks for success message.
             **/
            try {
                if (json.getString(KEY_SUCCESS) != null) {
                    registerErrorMsg.setText("");
                    String res = json.getString(KEY_SUCCESS);

                    String red = json.getString(KEY_ERROR);

                    if(Integer.parseInt(res) == 1){
                        pDialog.setTitle("Getting Data");
                        pDialog.setMessage("Loading Info");

                        registerErrorMsg.setText("Successfully Registered");


                        DatabaseHandler db = new DatabaseHandler(getApplicationContext());
                        JSONObject json_user = json.getJSONObject("user");

                        /**
                         * Removes all the previous data in the SQlite database
                         **/

                        UserFunctions logout = new UserFunctions();
                        logout.logoutUser(getApplicationContext());
                        db.addUser(json_user.getString(KEY_PHONE),json_user.getString(KEY_USERNAME),json_user.getString(KEY_UID),password,json_user.getString(KEY_CREATED_AT));


                        /**
                         * Stores registered data in SQlite Database
                         * Launch Registered screen
                         **/

                        Intent registered = new Intent(getApplicationContext(), Registered.class);

                        /**
                         * Close all views before launching Registered screen
                         **/
                        registered.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        pDialog.dismiss();
                        startActivity(registered);


                        finish();
                    }

                    else if (Integer.parseInt(red) ==2){
                        pDialog.dismiss();
                        registerErrorMsg.setText("User already exists");
                    }
                    else if (Integer.parseInt(red) ==3){
                        pDialog.dismiss();
                        registerErrorMsg.setText("Invalid Email id");
                    }

                }


                else{
                    pDialog.dismiss();

                    registerErrorMsg.setText("Error occured in registration");
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
