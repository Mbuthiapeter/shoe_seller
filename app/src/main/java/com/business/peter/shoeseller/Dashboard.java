package com.business.peter.shoeseller;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

/**
 * Created by Victor on 22/11/2016.
 */
public class Dashboard extends Activity implements View.OnClickListener{
    ImageView sales, shoes, expenses, profit;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dashboard);

        setviews();
        shoes.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                Intent in = new Intent(getApplicationContext(),AllShoes.class);
                startActivity(in);
            }
        });
        sales.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                Intent in = new Intent(getApplicationContext(),DailySales.class);
                startActivity(in);
            }
        });
        expenses.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                Intent in = new Intent(getApplicationContext(),Expenses.class);
                startActivity(in);
            }
        });
        profit.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                Intent in = new Intent(getApplicationContext(),Analysis.class);
                startActivity(in);
            }
        });
    }
    public void setviews(){
        sales = (ImageView)findViewById(R.id.icon_sales);
        shoes =(ImageView)findViewById(R.id.icon_shoes);
        expenses = (ImageView)findViewById(R.id.icon_expenses);
        profit =(ImageView)findViewById(R.id.icon_profit);
    }

    @Override
    public void onClick(View v) {

    }
}