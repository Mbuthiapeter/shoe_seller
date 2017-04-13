package com.business.peter.shoeseller;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Victor on 11/07/2016.
 */
public class AddType extends Activity {
    EditText tname;
    Button btnSave, btnBack;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_type);

        tname = (EditText)findViewById(R.id.tname);
        btnSave = (Button)findViewById(R.id.btnsave);
        btnSave.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                DatabaseHandler databaseHandler = new DatabaseHandler(getApplicationContext());


                String name = tname.getText().toString();
                if(name !=""){
                    databaseHandler.addType(name);
                    Toast.makeText(getBaseContext(), "Add successful ", Toast.LENGTH_SHORT).show();

                    Intent i = new Intent(getApplicationContext(), AddType.class);
                    startActivity(i);
                }
                else {
                    Toast.makeText(getBaseContext(), "Type name is empty", Toast.LENGTH_SHORT).show();
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

        }
}
