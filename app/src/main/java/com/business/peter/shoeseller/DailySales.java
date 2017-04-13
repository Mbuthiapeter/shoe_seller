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
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemSelectedListener;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

/**
 * Created by Victor on 25/11/2016.
 */
public class DailySales extends ListActivity implements View.OnClickListener, OnItemSelectedListener {
    // Progress Dialog
    private ProgressDialog pDialog;
    DatePickerDialog dpd;

    // Creating JSON Parser object
    JSONParser jParser = new JSONParser();
    //Hashmap of items

    HashMap<String,String> user = new HashMap<String, String>();
    ArrayList<SoldShoe> shoesList;

    // url to get all products list
    private static String url_all_shoes = "http://www.kinyafridah.com/shoe_seller/shoe_inventory/";

    // JSON Node names
    private static final String TAG_SUCCESS = "success";
    private static final String TAG_SHOES = "sold_shoes";
    private static final String TAG_DATE_BOUGHT = "date_bought";
    private static final String TAG_COMMENTS = "comments";
    private static final String TAG_COUNT = "shoe_count";
    private static final String TAG_SELLING_PRICE = "selling_price";
    private static final String TAG_PROFIT = "profit";
    private static final String TAG_SIZE = "size";
    private static final String TAG_TOTAL_PROFIT = "total_profit";
    private static final String TAG_TOTAL_SALE = "total_sale";
    private static final String TAG_DESCRIPTION = "description";
    static  String TAG_SHOE_ID = "sid";
    static  String TAG_TYPE = "type";
    static  String TAG_BUYING_PRICE = "buying_price";
    static  String TAG_NAME = "name";
    static  String TAG_COLOR = "color";
    static  String TAG_IMAGE_URL = "url";
    static  int month;
    static  int year;

    String cumulative_profit;
    String total_sale;
    String shoe_count;
    public EditText from_date;
    public int theMonth;
    public  int theYear;

    Spinner sp;
    public TextView total_sales,total_profit,count,type,color;
    SoldShoeAdapter adapter;

    ImageView icon_add, fetch_icon;

    // products JSONArray
    JSONArray sold_shoes = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.daily_sale);

        shoesList = new ArrayList<SoldShoe>();

        DatabaseHandler db = new DatabaseHandler(getApplicationContext());
        user = db.getUserDetails();


        String userId = user.get("plain_id").toString();
        // Loading shoes in Background Thread
        new LoadDailyShoes(userId).execute();
        ListView lv = getListView();

        adapter = new SoldShoeAdapter(getApplicationContext(), R.layout.daily_sale_list, shoesList);
        lv.setAdapter(adapter);

        total_sales = (TextView)findViewById(R.id.tvTotalSale);
        total_profit = (TextView)findViewById(R.id.tvTotalProfit);
        count = (TextView)findViewById(R.id.tvShoeCount);
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
                dpd = new DatePickerDialog(DailySales.this,
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
                Intent in = new Intent(getApplicationContext(),SalesHistory.class);
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
                String sid = ((TextView) view.findViewById(R.id.sid)).getText().toString();
                String name = ((TextView) view.findViewById(R.id.tvName)).getText().toString();
                String buyingPrice = ((TextView) view.findViewById(R.id.tvBuyingPrice)).getText().toString();
                String dateBought = ((TextView) view.findViewById(R.id.tvDateBought)).getText().toString();
                String comments = ((TextView) view.findViewById(R.id.tvComments)).getText().toString();
                String type = ((TextView) view.findViewById(R.id.tvType)).getText().toString();
                String color = ((TextView) view.findViewById(R.id.tvColor)).getText().toString();
                String url = url_all_shoes + "Pictures/" + sid + name + ".JPG";

                // Starting new intent
                Intent in = new Intent(getApplicationContext(),ViewShoe.class);
                in.putExtra(TAG_SHOE_ID, sid);
                in.putExtra(TAG_NAME, name);
                in.putExtra(TAG_BUYING_PRICE, buyingPrice);
                in.putExtra(TAG_DATE_BOUGHT, dateBought);
                in.putExtra(TAG_DESCRIPTION, comments);
                in.putExtra(TAG_TYPE, type);
                in.putExtra(TAG_COLOR, color);
                in.putExtra(TAG_IMAGE_URL, url);
                startActivityForResult(in, 1);
            }
        });

    }

    // Response from Edit Product Activity
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // if result code 100
        if (resultCode == 100) {
            // if result code 100 is received
            // means user edited/deleted product
            // reload this screen again
            Intent intent = getIntent();
            finish();
            startActivity(intent);
        }

    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

        String category = parent.getItemAtPosition(position).toString();
        String userId = user.get("plain_id").toString();

        // Starting new intent
        Intent in = new Intent(getApplicationContext(), CategoryShoes.class);
        // sending pid and name to next activity
        //in.putExtra(TAG_ID, userId);
        // starting new activity and expecting some response back
        startActivityForResult(in, 100);
    }

    /**
     * Background Async Task to Load all product by making HTTP Request
     * */
    class LoadDailyShoes extends AsyncTask<String, Void, Boolean> {
        String userId;

        public LoadDailyShoes(String userId){
            this.userId= userId;
        }
        /**
         * Before starting background thread Show Progress Dialog
         * */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(DailySales.this);
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
            final JSONObject json = jParser.makeHttpRequest(url_all_shoes +"sold_shoes.php", "GET", dataToSend);
            // Check your log cat for JSON reponse
            Log.d("Sold shoes", json.toString());

            try {
                // Checking for SUCCESS TAG
                int success = json.getInt(TAG_SUCCESS);

                if (success == 1) {
                    // Getting Array of shoes
                    sold_shoes = json.getJSONArray(TAG_SHOES);
                    // looping through All Shoe
                    for (int i = 0; i < sold_shoes.length(); i++) {
                        JSONObject c = sold_shoes.getJSONObject(i);

                        // Storing each json item in variable
                        String id = c.getString(TAG_SHOE_ID);
                        String name = c.getString(TAG_NAME);
                        String date_bought = c.getString(TAG_DATE_BOUGHT);
                        String buying_price = c.getString(TAG_BUYING_PRICE);
                        String selling_price = c.getString(TAG_SELLING_PRICE);
                        String profit = c.getString(TAG_PROFIT);
                        String size = c.getString(TAG_SIZE);
                        String comments = c.getString(TAG_COMMENTS);
                        String type = c.getString(TAG_TYPE);
                        String color = c.getString(TAG_COLOR);

                        cumulative_profit = json.getString(TAG_TOTAL_PROFIT);
                        total_sale = json.getString(TAG_TOTAL_SALE);
                        shoe_count = json.getString(TAG_COUNT);

                        String url = url_all_shoes + "Pictures/" + id + name + ".JPG";

                        SoldShoe soldShoe = new SoldShoe();

                        soldShoe.setSid(id);
                        soldShoe.setBuying_price(buying_price);
                        soldShoe.setName(name);
                        soldShoe.setDate_bought(date_bought);
                        soldShoe.setComment(comments);
                        soldShoe.setSelling_price(selling_price);
                        soldShoe.setProfit(profit);
                        soldShoe.setSize(size);
                        soldShoe.setImage(url);
                        soldShoe.setType(type);
                        soldShoe.setColor(color);

                        shoesList.add(soldShoe);
                    }

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            total_sales.setText("Total sale:" +total_sale );
                            total_profit.setText("Total profit:" +cumulative_profit );
                            count.setText("Sales count:" +shoe_count );
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
    public void onNothingSelected(AdapterView<?> arg0) {
        // TODO Auto-generated method stub

    }
}
