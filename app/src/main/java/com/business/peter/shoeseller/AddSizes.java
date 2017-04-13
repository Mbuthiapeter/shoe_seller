package com.business.peter.shoeseller;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.HashMap;

/**
 * Created by Victor on 23/09/2016.
 */
public class AddSizes extends Activity {
    EditText tsize;
    Button btnSave, btnBack, btnAllShoes;

    HashMap<String,String> user = new HashMap<String, String>();
    private static final String TAG_SID = "sid";


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_size);

        tsize = (EditText)findViewById(R.id.tsize);
        btnSave = (Button)findViewById(R.id.btnsave);
        btnSave.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                DatabaseHandler databaseHandler = new DatabaseHandler(getApplicationContext());
                user = databaseHandler.getUserDetails();


                String size = tsize.getText().toString();
                if(size !=""){
                    Intent in = getIntent();
                    String sid = in.getStringExtra(TAG_SID);
                    String uid = user.get("plain_id").toString();

                    databaseHandler.addSize(size,sid,uid);
                    Toast.makeText(getBaseContext(), "Add successful ", Toast.LENGTH_LONG).show();

                    Intent i = new Intent(getApplicationContext(),AddSizes.class);
                    i.putExtra(TAG_SID, sid);
                    startActivityForResult(i, 3);
                }
                else {
                    Toast.makeText(getBaseContext(), "Size is empty", Toast.LENGTH_LONG).show();
                }
            }
        });

        btnBack = (Button)findViewById(R.id.btnBack);
        btnBack.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(), AddShoe.class);
                startActivity(i);
            }
        });

        btnAllShoes = (Button)findViewById(R.id.btnAllShoes);
        btnAllShoes.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                Intent it = new Intent(getApplicationContext(), AllShoes.class);
                startActivity(it);
            }
        });

    }
}
