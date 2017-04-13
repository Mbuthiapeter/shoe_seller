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
import com.business.peter.shoeseller.model.ListData;
import com.business.peter.shoeseller.model.ListSales;
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
 * Created by Victor on 30/11/2016.
 */

public class SalesHistory extends ListActivity implements View.OnClickListener, AdapterView.OnItemSelectedListener {

    ListView listView;
    ListView lv;
    List<ListData> dataList = new ArrayList<>();
    private ProgressDialog pDialog;
    JSONParser jParser = new JSONParser();
    HashMap<String,String> user = new HashMap<String, String>();

    private static String url_history = "http://www.kinyafridah.com/shoe_seller/shoe_inventory/";

    // JSON Node names
    private static final String TAG_SUCCESS = "success";
    private static final String TAG_SHOES = "sales";
    private static final String TAG_COUNT = "shoe_count";
    private static final String TAG_SELLING_PRICE = "selling_price";
    private static final String TAG_PROFIT = "profit";
    private static final String TAG_SIZE = "size";
    private static final String TAG_TOTAL_PROFIT = "total_profit";
    private static final String TAG_DATE_SOLD = "date_sold";
    private static final String TAG_TOTAL_SALE = "total_sale";
    static  String TAG_SHOE_ID = "sid";
    static  String TAG_NAME = "name";
    static  String TAG_BUYING_PRICE = "buying_price";


    String cumulativeProfit;
    String totalSale;
    int shoeCount;

    public TextView tvTotal_sales,tvTotal_profit,tvCount;

    // products JSONArray
    JSONArray sales_hist = null;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sales_history);

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
                String sid = ((TextView) view.findViewById(R.id.sid)).getText().toString();
                String name = ((TextView) view.findViewById(R.id.tvName)).getText().toString();
                // Starting new intent
                Intent in = new Intent(getApplicationContext(),ViewShoe.class);
                in.putExtra(TAG_SHOE_ID, sid);
                in.putExtra(TAG_NAME, name);
                startActivityForResult(in, 1);
            }
        });

        new LoadSalesHistory(userId,theMonth,theYear).execute();
    }

    private void initViews()
    {
        //listView = (ListView) findViewById(R.id.list_view);
        tvTotal_sales = (TextView)findViewById(R.id.tvTotalSale);
        tvTotal_profit = (TextView)findViewById(R.id.tvTotalProfit);
        tvCount = (TextView)findViewById(R.id.tvShoeCount);
    }

    /**
     * Background Async Task to Load all product by making HTTP Request
     * */
    class LoadSalesHistory extends AsyncTask<String, Void, Boolean> {
        String userId;
        int month;
        int year;
        String prev_date ="";
        public LoadSalesHistory(String userId,int month,int year){
            this.userId= userId;
            this.month = month;
            this.year = year;
        }
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(SalesHistory.this);
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
            final JSONObject json = jParser.makeHttpRequest(url_history +"sales_history.php", "GET", dataToSend);

            Log.d("Values", json.toString());
            try {
                int success = json.getInt(TAG_SUCCESS);
                if (success == 1) {
                    sales_hist = json.getJSONArray(TAG_SHOES);

                    for (int i = 0; i < sales_hist.length(); i++) {
                        JSONObject c = sales_hist.getJSONObject(i);
                        final String id = c.getString(TAG_SHOE_ID);
                        final String name = c.getString(TAG_NAME);
                        final String buying_price = c.getString(TAG_BUYING_PRICE);
                        final String selling_price = c.getString(TAG_SELLING_PRICE);
                        final String profit = c.getString(TAG_PROFIT);
                        final String size = c.getString(TAG_SIZE);
                        final String date_sold = c.getString(TAG_DATE_SOLD);

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                //Log.d("Previous Date",prev_date);
                                if (!prev_date.equals(date_sold)) {
                                    ListSection listSection = new ListSection();
                                    listSection.setTitle(date_sold);
                                    dataList.add(listSection);
                                }else{
                                    Log.d("Previous Date",prev_date);
                                }
                                ListSales listSale = new ListSales();
                                listSale.setName("Name: "+ name);
                                listSale.setSize("Size: "+ size);
                                listSale.setBuying_price("Buying price: "+ buying_price);
                                listSale.setSelling_price("Selling price: "+ selling_price);
                                listSale.setProfit("Profit: "+ profit);
                                listSale.setSid(id);
                                dataList.add(listSale);

                                prev_date = date_sold;
                            }
                        });
                    }

                    shoeCount = json.getInt(TAG_COUNT);
                    totalSale = json.getString(TAG_TOTAL_SALE);
                    cumulativeProfit = json.getString(TAG_TOTAL_PROFIT);

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            AdaptBaseAdapter adapter = new AdaptBaseAdapter(SalesHistory.this, dataList);
                            lv.setAdapter(adapter);
                            tvTotal_sales.setText("Cumulative Profit = " + cumulativeProfit);
                            tvTotal_profit.setText("Cumulative sale = " + totalSale);
                            tvCount.setText("Count = " + shoeCount);
                        }


                    });

                } else {
                    Intent i = new Intent(getApplicationContext(),
                            AllShoes.class);
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
