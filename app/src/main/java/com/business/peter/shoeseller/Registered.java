package com.business.peter.shoeseller;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.HashMap;

/**
 * Created by Victor on 09/07/2016.
 */
public class Registered extends Activity {
    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.registered);


        DatabaseHandler db = new DatabaseHandler(getApplicationContext());

        HashMap<String,String> user = new HashMap<String, String>();
        user = db.getUserDetails();

        /**
         * Displays the registration details in Text view
         **/

        final TextView phone = (TextView)findViewById(R.id.phone);
        final TextView uname = (TextView)findViewById(R.id.uname);
        final TextView created_at = (TextView)findViewById(R.id.regat);

        uname.setText(user.get("uname"));
        phone.setText(user.get("phone"));
        created_at.setText(user.get("created_at"));

        Button login = (Button) findViewById(R.id.login);
        login.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Intent myIntent = new Intent(view.getContext(), MainActivity.class);
                startActivityForResult(myIntent, 0);
                finish();
            }

        });


    }
}
