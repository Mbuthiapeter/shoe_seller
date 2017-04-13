package com.business.peter.shoeseller;

import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.ParseException;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.business.peter.shoeseller.adapter.AdaptBaseAdapter;
import com.business.peter.shoeseller.adapter.Expense;
import com.business.peter.shoeseller.adapter.ExpensesBaseAdapter;
import com.business.peter.shoeseller.model.ListData;
import com.business.peter.shoeseller.model.ListSection;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Victor on 02/03/2017.
 */
public class ExpensesHistory extends ListActivity implements View.OnClickListener, AdapterView.OnItemSelectedListener {
    ListView listView;
    ListView lv;
    List<ListData> dataList = new ArrayList<>();
    private ProgressDialog pDialog;
    JSONParser jParser = new JSONParser();
    HashMap<String,String> user = new HashMap<String, String>();

    private static String url_history = "http://www.kinyafridah.com/shoe_seller/shoe_inventory/";

    // JSON Node names
    private static final String TAG_SUCCESS = "success";
    private static final String TAG_EXPENSES = "expenses";
    private static final String TAG_DATE_INCURRED = "date_incurred";
    private static final String TAG_EXPENSE = "expense";
    private static final String TAG_AMOUNT = "amount";
    private static final String TAG_TOTAL_AMOUNT = "total_expenses";
    private static final String TAG_EXPID = "expId";

    String cumulativeExpenses;

    public TextView tvTotal_expenses;

    // products JSONArray
    JSONArray expenses_hist = null;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.expenses_history);

        initViews();

        DatabaseHandler db = new DatabaseHandler(getApplicationContext());
        user = db.getUserDetails();
        String userId = user.get("plain_id").toString();

        Intent intent = getIntent();
        int theMonth = intent.getIntExtra("month",0);
        int theYear = intent.getIntExtra("year",0);

        lv = getListView();


        // on seleting single product
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                // getting values from selected ListItem
                //String sid = ((TextView) view.findViewById(R.id.eId)).getText().toString();
                Intent in = new Intent(getApplicationContext(),ViewShoe.class);
                //in.putExtra(TAG_EXPID, eId);
                startActivityForResult(in, 1);
            }
        });

        new LoadExpensesHistory(userId,theMonth,theYear).execute();
    }

    /**
     * Background Async Task to Load all product by making HTTP Request
     * */
    class LoadExpensesHistory extends AsyncTask<String, Void, Boolean> {
        String userId;
        int month;
        int year;
        String prev_date ="";
        public LoadExpensesHistory(String userId,int month,int year){
            this.userId= userId;
            this.month = month;
            this.year = year;
        }
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(ExpensesHistory.this);
            pDialog.setMessage("Loading...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();
        }
        protected Boolean doInBackground(String... urls) {
            ArrayList<NameValuePair> dataToSend = new ArrayList<NameValuePair>();
            dataToSend.add(new BasicNameValuePair("uid", userId));
            dataToSend.add(new BasicNameValuePair("month", Integer.toString(month)));
            dataToSend.add(new BasicNameValuePair("year", Integer.toString(year)));
            final JSONObject json = jParser.makeHttpRequest(url_history +"expenses_history.php", "GET", dataToSend);

            Log.d("Expenses History Values", json.toString());
            try {
                int success = json.getInt(TAG_SUCCESS);
                if (success == 1) {
                    expenses_hist = json.getJSONArray(TAG_EXPENSES);

                    for (int i = 0; i < expenses_hist.length(); i++) {
                        JSONObject c = expenses_hist.getJSONObject(i);
                        final String expId = c.getString(TAG_EXPID);
                        final String expense = c.getString(TAG_EXPENSE);
                        final String amount = c.getString(TAG_AMOUNT);
                        final String date_incurred = c.getString(TAG_DATE_INCURRED);

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                //Log.d("Previous Date",prev_date);
                                if (!prev_date.equals(date_incurred)) {
                                    ListSection listSection = new ListSection();
                                    listSection.setTitle(date_incurred);
                                    dataList.add(listSection);
                                }else{
                                    //Log.d("Previous Date",prev_date);
                                }
                                Expense exps = new Expense();
                                exps.setAmount("Amount: "+ amount);
                                exps.setExpense("Expense: "+ expense);
                                exps.setExpId(expId);
                                dataList.add(exps);

                                prev_date = date_incurred;
                            }
                        });
                    }

                    cumulativeExpenses = json.getString(TAG_TOTAL_AMOUNT);

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            ExpensesBaseAdapter adapter = new ExpensesBaseAdapter(ExpensesHistory.this, dataList);
                            lv.setAdapter(adapter);
                            tvTotal_expenses.setText("Cumulative Expenses = " + cumulativeExpenses);
                        }


                    });

                } else {
                    Intent i = new Intent(getApplicationContext(),
                            Expenses.class);
                    i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(i);
                }
                return true;
            } catch (ParseException e1) {
                e1.printStackTrace();
            }  catch (JSONException e) {
                e.printStackTrace();
            }
            return true;
        }
        protected void onPostExecute(Boolean result) {
            pDialog.dismiss();
            if(result == false)
                Toast.makeText(getApplicationContext(), "Unable to fetch data from server", Toast.LENGTH_LONG).show();

        }
    }

    private void initViews()
    {
        tvTotal_expenses = (TextView)findViewById(R.id.tvTotalExpenses);
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
