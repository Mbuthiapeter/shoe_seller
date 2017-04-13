package com.business.peter.shoeseller;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

/**
 * Created by Victor on 30/12/2016.
 */
public class AddExpense extends Activity implements View.OnClickListener, AdapterView.OnItemSelectedListener{
    private ProgressDialog pDialog;

    JSONParser jsonParser = new JSONParser();

    EditText etexpense, etamount, etdate;
    DatePickerDialog dpd;

    private static final int RESULT_LOAD_IMAGE = 1;

    private static String url_create = "http://www.kinyafridah.com/shoe_seller/shoe_inventory/";
    // JSON Node names
    private static final String TAG_SUCCESS = "success";
    private static final String TAG_SID = "sid";

    HashMap<String,String> user = new HashMap<String, String>();
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_expense);

        DatabaseHandler db = new DatabaseHandler(getApplicationContext());
        user = db.getUserDetails();

        etexpense = (EditText) findViewById(R.id.etexpense);
        etamount = (EditText) findViewById(R.id.etamount);
        etdate = (EditText) findViewById(R.id.etdate);

        etdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // calender class's instance and get current date , month and year from calender
                final Calendar c = Calendar.getInstance();
                int mYear = c.get(Calendar.YEAR); // current year
                int mMonth = c.get(Calendar.MONTH); // current month
                int mDay = c.get(Calendar.DAY_OF_MONTH); // current day
                // date picker dialog
                dpd = new DatePickerDialog(AddExpense.this,
                        new DatePickerDialog.OnDateSetListener() {

                            @Override
                            public void onDateSet(DatePicker view, int year,
                                                  int monthOfYear, int dayOfMonth) {
                                // set day of month , month and year value in the edit text
                                etdate.setText(year+ "-" +(monthOfYear + 1)+ "-" +dayOfMonth);
                            }
                        }, mYear, mMonth, mDay);
                dpd.show();
            }
        });

        Button btnSave = (Button) findViewById(R.id.btnsave);
        btnSave.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                String userId = user.get("plain_id").toString();
                String expense = etexpense.getText().toString();
                String amount = etamount.getText().toString();
                String date = etdate.getText().toString();
                new CreateNewExpense(userId,expense,amount,date).execute();
            }
        });
    }
    /**
     * Background Async Task to Create new product
     * */
    class CreateNewExpense extends AsyncTask<String, String, String> {

        String userId;
        String expense;
        String amount;
        String date;

        public CreateNewExpense(String userId,String expense,String amount,String date){

            this.expense= expense;
            this.userId= userId;
            this.amount= amount;
            this.date= date;

        }

        /**
         * Before starting background thread Show Progress Dialog
         * */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(AddExpense.this);
            pDialog.setMessage("Saving..");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
        }

        /**
         * Creating animal
         * */
        protected String doInBackground(String... args) {
            // Building Parameters
            ArrayList<NameValuePair> dataToSend = new ArrayList<NameValuePair>();
            dataToSend.add(new BasicNameValuePair("userId", userId));
            dataToSend.add(new BasicNameValuePair("expense", expense));
            dataToSend.add(new BasicNameValuePair("amount", amount));
            dataToSend.add(new BasicNameValuePair("date", date));

            // getting JSON Object
            // Note that create product url accepts POST method
            JSONObject json = jsonParser.makeHttpRequest(url_create + "create_expense.php","POST", dataToSend);

            // check log cat for response
            Log.d("Create Response", json.toString());

            // check for success tag
            try {
                int success = json.getInt(TAG_SUCCESS);

                if (success == 1) {
                    // successfully created animal
                    Intent i = new Intent(getApplicationContext(),AddExpense.class);
                    startActivity(i);
                    // closing this screen
                    finish();
                } else {
                    // failed to create product
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return null;
        }

        /**
         * After completing background task Dismiss the progress dialog
         * **/
        protected void onPostExecute(String file_url) {
            // dismiss the dialog once done
            pDialog.dismiss();
        }

    }
    @Override
    public void onClick(View v) {

    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}
