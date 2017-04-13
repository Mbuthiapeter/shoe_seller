package com.business.peter.shoeseller;


import android.app.DatePickerDialog;
import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.ParseException;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.business.peter.shoeseller.adapter.Expense;
import com.business.peter.shoeseller.adapter.ExpenseAdapter;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

/**
 * Created by Victor on 03/01/2017.
 */
public class Expenses extends ListActivity implements View.OnClickListener, AdapterView.OnItemSelectedListener {
    private ProgressDialog pDialog;
    DatePickerDialog dpd;

    JSONParser jParser = new JSONParser();

    HashMap<String,String> user = new HashMap<String, String>();
    ArrayList<Expense> expenses;

    private static String url_expenses = "http://www.kinyafridah.com/shoe_seller/shoe_inventory/";

    // JSON Node names
    private static final String TAG_SUCCESS = "success";
    private static final String TAG_EXPENSES = "expenses";
    private static final String TAG_DATE_INCURRED = "date_incurred";
    private static final String TAG_EXPENSE = "expense";
    private static final String TAG_AMOUNT = "amount";
    private static final String TAG_TOTAL_AMOUNT = "total_expenses";
    private static final String TAG_EXPID = "expId";
    static  int month;
    static  int year;

    String total_expenses;
    public EditText from_date;
    public int theMonth;
    public  int theYear;

    Spinner sp;
    public TextView tvTotalExpenses;
    ExpenseAdapter adapter;

    ImageView icon_add, fetch_icon;

    // products JSONArray
    JSONArray daily_expenses = null;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.expenses);

        expenses = new ArrayList<Expense>();

        DatabaseHandler db = new DatabaseHandler(getApplicationContext());
        user = db.getUserDetails();


        String userId = user.get("plain_id").toString();
        // Loading shoes in Background Thread
        new LoadDailyExpenses(userId).execute();
        ListView lv = getListView();

        adapter = new ExpenseAdapter(getApplicationContext(), R.layout.daily_expense_list, expenses);
        lv.setAdapter(adapter);

        tvTotalExpenses = (TextView)findViewById(R.id.tvTotalExpenses);
        from_date = (EditText) findViewById(R.id.from_date);
        icon_add = (ImageView) findViewById(R.id.icon_add);
        fetch_icon = (ImageView) findViewById(R.id.icon_fetch);

        from_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // calender class's instance and get current date , month and year from calender
                final Calendar c = Calendar.getInstance();
                int mYear = c.get(Calendar.YEAR); // current year
                int mMonth = c.get(Calendar.MONTH); // current month
                int mDay = c.get(Calendar.DAY_OF_MONTH); // current day
                // date picker dialog
                dpd = new DatePickerDialog(Expenses.this,
                        new DatePickerDialog.OnDateSetListener() {

                            @Override
                            public void onDateSet(DatePicker view, int year,
                                                  int monthOfYear, int dayOfMonth) {
                                // set day of month , month and year value in the edit text
                                String sMonth;
                                switch(monthOfYear + 1){
                                    case 1: sMonth = "Jan";
                                        break;
                                    case 2: sMonth = "Feb";
                                        break;
                                    case 3: sMonth = "March";
                                        break;
                                    case 4: sMonth = "April";
                                        break;
                                    case 5: sMonth = "May";
                                        break;
                                    case 6: sMonth = "June";
                                        break;
                                    case 7: sMonth = "July";
                                        break;
                                    case 8: sMonth = "Aug";
                                        break;
                                    case 9: sMonth = "Sept";
                                        break;
                                    case 10: sMonth = "Oct";
                                        break;
                                    case 11: sMonth = "Nov";
                                        break;
                                    case 12: sMonth = "Dec";
                                        break;
                                    default: sMonth = "Invalid month";
                                        break;
                                }
                                from_date.setText(sMonth + "-" +year);
                                theYear = year;
                                theMonth = monthOfYear + 1;
                            }
                        }, mYear, mMonth, mDay);
                dpd.show();
            }
        });

        icon_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent= new Intent(getApplicationContext(),AddExpense.class);
                startActivity(intent);
            }
        });
        fetch_icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Starting new intent
                Intent in = new Intent(getApplicationContext(),ExpensesHistory.class);
                in.putExtra("month", theMonth);
                in.putExtra("year", theYear);
                startActivityForResult(in, 1);
            }
        });

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                // getting values from selected ListItem
                String expId = ((TextView) view.findViewById(R.id.tvExpId)).getText().toString();
                String expense = ((TextView) view.findViewById(R.id.tvExpense)).getText().toString();
                String amount = ((TextView) view.findViewById(R.id.tvAmount)).getText().toString();

                // Starting new intent
                Intent in = new Intent(getApplicationContext(),ViewShoe.class);
                in.putExtra(TAG_EXPID, expId);
                startActivityForResult(in, 1);
            }
        });
    }

    /**
     * Background Async Task to Load all product by making HTTP Request
     * */
    class LoadDailyExpenses extends AsyncTask<String, Void, Boolean> {
        String userId;

        public LoadDailyExpenses(String userId){
            this.userId= userId;
        }
        /**
         * Before starting background thread Show Progress Dialog
         * */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(Expenses.this);
            pDialog.setMessage("Loading shoes...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();
        }

        /**
         * getting All products from url
         * */
        protected Boolean doInBackground(String... urls) {
            ArrayList<NameValuePair> dataToSend = new ArrayList<NameValuePair>();

            Calendar cal = Calendar.getInstance();
            SimpleDateFormat tf = new SimpleDateFormat("yyyy-MM-dd");
            String date_today =tf.format(cal.getTime());

            dataToSend.add(new BasicNameValuePair("uid", userId));
            dataToSend.add(new BasicNameValuePair("date_today", date_today));
            final JSONObject json = jParser.makeHttpRequest(url_expenses +"fetch_daily_expenses.php", "GET", dataToSend);
            // Check your log cat for JSON reponse
            Log.d("Daily Expenses", json.toString());

            try {
                // Checking for SUCCESS TAG
                int success = json.getInt(TAG_SUCCESS);

                if (success == 1) {
                    // Getting Array of shoes
                    daily_expenses = json.getJSONArray(TAG_EXPENSES);
                    // looping through All Shoe
                    for (int i = 0; i < daily_expenses.length(); i++) {
                        JSONObject c = daily_expenses.getJSONObject(i);

                        // Storing each json item in variable
                        String eId = c.getString(TAG_EXPID);
                        String expense = c.getString(TAG_EXPENSE);
                        String date_incurred = c.getString(TAG_DATE_INCURRED);
                        String amount = c.getString(TAG_AMOUNT);

                        total_expenses = json.getString(TAG_TOTAL_AMOUNT);

                        Expense exp= new Expense();

                        exp.setExpId(eId);
                        exp.setExpense(expense);
                        exp.setDate_incurred(date_incurred);
                        exp.setAmount(amount);

                        expenses.add(exp);
                    }

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            tvTotalExpenses.setText("Total Expenses:" +total_expenses );
                        }
                    });

                } else {
                    // no shoe found
                    // Launch Add New product Activity
                    Intent i = new Intent(getApplicationContext(),
                            AllShoes.class);
                    // Closing all previous activities
                    i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(i);
                    // Toast.makeText(getApplicationContext(), "No shoe found. Enter new", Toast.LENGTH_LONG).show();
                }
                return true;
            } catch (ParseException e1) {
                e1.printStackTrace();
            }  catch (JSONException e) {
                e.printStackTrace();
            }
            return false;
        }

        /**
         * After completing background task Dismiss the progress dialog
         * **/
        protected void onPostExecute(Boolean result) {
            // dismiss the dialog after getting all products

            pDialog.dismiss();
            adapter.notifyDataSetChanged();
            if(result == false)
                Toast.makeText(getApplicationContext(), "Unable to fetch data from server", Toast.LENGTH_LONG).show();

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
