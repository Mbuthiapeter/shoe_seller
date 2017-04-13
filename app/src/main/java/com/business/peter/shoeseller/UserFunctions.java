package com.business.peter.shoeseller;

import android.content.Context;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Victor on 06/07/2016.
 */
public class UserFunctions {
    private JSONParser jsonParser;

    //URL of the PHP API
    private static String loginURL = "http://www.kinyafridah.com/shoe_seller/shoe_inventory/login/";

    private static String login_tag = "login";
    private static String register_tag = "register";
    private static String forpass_tag = "forpass";
    private static String chgpass_tag = "chgpass";


    // constructor
    public UserFunctions(){
        jsonParser = new JSONParser();
    }

    /**
     * Function to Login
     **/

    public JSONObject loginUser(String username, String password){
        // Building Parameters
        ArrayList<NameValuePair> dataToSend = new ArrayList<NameValuePair>();
        dataToSend.add(new BasicNameValuePair("tag", login_tag));
        dataToSend.add(new BasicNameValuePair("username", username));
        dataToSend.add(new BasicNameValuePair("password", password));
        JSONObject json = jsonParser.makeHttpRequest(loginURL,"POST", dataToSend);
        return json;
    }

    /**
     * Function to change password
     **/

    public JSONObject chgPass(String newpas, String email){
        ArrayList<NameValuePair> dataToSend = new ArrayList<NameValuePair>();
        dataToSend.add(new BasicNameValuePair("tag", chgpass_tag));
        dataToSend.add(new BasicNameValuePair("newpas", newpas));
        dataToSend.add(new BasicNameValuePair("email", email));
        JSONObject json = jsonParser.makeHttpRequest(loginURL,"POST", dataToSend);
        return json;
    }





    /**
     * Function to reset the password
     **/

    public JSONObject forPass(String forgotpassword){
        ArrayList<NameValuePair> dataToSend = new ArrayList<NameValuePair>();
        dataToSend.add(new BasicNameValuePair("tag", forpass_tag));
        dataToSend.add(new BasicNameValuePair("forgotpassword", forgotpassword));
        JSONObject json = jsonParser.makeHttpRequest(loginURL,"POST", dataToSend);
        return json;
    }






    /**
     * Function to  Register
     **/
    public JSONObject registerUser(String phone, String uname, String password){
        // Building Parameters
        ArrayList<NameValuePair> dataToSend = new ArrayList<NameValuePair>();
        dataToSend.add(new BasicNameValuePair("tag", register_tag));
        dataToSend.add(new BasicNameValuePair("phone", phone));
        dataToSend.add(new BasicNameValuePair("uname", uname));
        dataToSend.add(new BasicNameValuePair("password", password));
        JSONObject json = jsonParser.makeHttpRequest(loginURL,"POST", dataToSend);
        return json;
    }


    /**
     * Function to logout user
     * Resets the temporary data stored in SQLite Database
     * */
    public boolean logoutUser(Context context){
        DatabaseHandler db = new DatabaseHandler(context);
        db.resetTables();
        return true;
    }
}
