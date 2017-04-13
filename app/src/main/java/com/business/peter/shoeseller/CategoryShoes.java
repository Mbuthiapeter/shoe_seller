package com.business.peter.shoeseller;

import android.app.Activity;
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
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Victor on 21/09/2016.
 */
public class CategoryShoes extends ListActivity implements View.OnClickListener, AdapterView.OnItemSelectedListener{
    // Progress Dialog
    private ProgressDialog pDialog;

    // Creating JSON Parser object
    JSONParser jParser = new JSONParser();
    //Hashmap of items

    HashMap<String,String> user = new HashMap<String, String>();
    ArrayList<Shoe> shoesList;

    // url to get all products list
    private static String url_all_shoes = "http://www.kinyafridah.com/shoe_seller/shoe_inventory/";

    TextView txtViewAll, txtAllAnimals;
    // JSON Node names
    private static final String TAG_SUCCESS = "success";
    private static final String TAG_SHOES = "shoes";
    private static final String TAG_TYPE = "type";
    private static final String TAG_COLOR = "color";
    private static final String TAG_DATE_BOUGHT = "date_bought";
    private static final String TAG_DESCRIPTION = "description";
    private static final String TAG_COUNT = "shoe_count";
    private static final String TAG_SELLING_PRICE = "selling_price";
    private static final String TAG_DATE_SOLD = "date_sold";

    static  String TAG_ID = "id";
    static  String TAG_SHOE_ID = "sid";
    static  String TAG_NAME = "name";
    static  String TAG_BUYING_PRICE = "buying_price";
    static  String TAG_IMAGE_URL = "url";
    static  String TAG_SHOE_TYPE = "shoe_type";

    String shoe_type;

    Spinner sp;
    ListView list_v;
    ShoeAdapter adapter;



    // products JSONArray
    JSONArray shoes = null;
    JSONArray catAnimals = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.all_shoes);
        shoesList = new ArrayList<Shoe>();

        DatabaseHandler db = new DatabaseHandler(getApplicationContext());
        user = db.getUserDetails();

        ArrayList<String> listPro = db.getAllTypes();
        sp = (Spinner)findViewById(R.id.category);
        ArrayAdapter<String> typeAdapter = new ArrayAdapter<String>(this, R.layout.spinner_layout,R.id.txt,listPro);
        sp.setAdapter(typeAdapter);


        String userId = user.get("uid").toString();
        // Loading shoes in Background Thread
        Intent i = getIntent();
        shoe_type = i.getStringExtra(TAG_TYPE);

        new LoadAllProducts(userId,shoe_type).execute();
        ListView lv = getListView();
        //ListView listview = (ListView)findViewById(R.id.list);
        adapter = new ShoeAdapter(getApplicationContext(), R.layout.list_item, shoesList);

        lv.setAdapter(adapter);
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
        // On selecting a spinner item
        //String label = parent.getItemAtPosition(position).toString();
        //sp.setSelection(position);
        /* String category = parent.getItemAtPosition(position).toString();
        String userId = user.get("uid").toString();

       new LoadCategoryShoes(userId,category).execute();
        ListView listview = (ListView)findViewById(R.id.list);
        adapter = new ShoeAdapter(getApplicationContext(), R.layout.list_item, shoesList);

        listview.setAdapter(adapter);*/
    }

    /**
     * Background Async Task to Load all product by making HTTP Request
     * */
    class LoadAllProducts extends AsyncTask<String, Void, Boolean> {
        String userId;
        String shoe_type;

        public LoadAllProducts(String userId, String type){
            this.userId= userId;
            this.shoe_type= type;
        }
        /**
         * Before starting background thread Show Progress Dialog
         * */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(CategoryShoes.this);
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
            dataToSend.add(new BasicNameValuePair("uid", userId));
            dataToSend.add(new BasicNameValuePair("type", shoe_type));
            final JSONObject json = jParser.makeHttpRequest(url_all_shoes +"get_all_shoes.php", "GET", dataToSend);
            // Check your log cat for JSON reponse
            Log.d("All shoes: ", json.toString());

            try {
                // Checking for SUCCESS TAG
                int success = json.getInt(TAG_SUCCESS);

                if (success == 1) {
                    // Getting Array of shoes
                    shoes = json.getJSONArray(TAG_SHOES);
                    // looping through All Shoe
                    for (int i = 0; i < shoes.length(); i++) {
                        JSONObject c = shoes.getJSONObject(i);

                        // Storing each json item in variable
                        String id = c.getString(TAG_SHOE_ID);
                        String name = c.getString(TAG_NAME);
                        String type = c.getString(TAG_TYPE);
                        String date_bought = c.getString(TAG_DATE_BOUGHT);
                        String color = c.getString(TAG_COLOR);
                        String buying_price = c.getString(TAG_BUYING_PRICE);
                        String description = c.getString(TAG_DESCRIPTION);

                        String url = url_all_shoes + "Pictures/" + id + name + ".JPG";

                        Shoe shoe = new Shoe();

                        shoe.setSid(id);
                        shoe.setBuying_price(buying_price);
                        shoe.setColor(color);
                        shoe.setType(type);
                        shoe.setName(name);
                        shoe.setDate_bought(date_bought);
                        shoe.setDescription(description);
                        shoe.setImage(url);

                        shoesList.add(shoe);


                    }

                } else {
                    // no shoe found
                    // Launch Add New product Activity
                    Intent i = new Intent(getApplicationContext(),
                            AddShoe.class);
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
