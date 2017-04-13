package com.business.peter.shoeseller;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
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
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;

/**
 * Created by Victor on 05/07/2016.
 */
public class SellShoe extends Activity implements View.OnClickListener, AdapterView.OnItemSelectedListener {
    private ProgressDialog pDialog;

    JSONParser jsonParser = new JSONParser();

    EditText et_selling_price, etselldate, etcomments;
    ImageView imageDownloaded;
    Spinner sp2;

    static  String TAG_SHOE_ID = "sid";
    static  String TAG_IMAGE_URL = "url";
    DatePickerDialog dpd;

    private static final int RESULT_LOAD_IMAGE = 1;

    private static String url_sell = "http://www.kinyafridah.com/shoe_seller/shoe_inventory/sell_shoe.php";
        // JSON Node names
    private static final String TAG_SUCCESS = "success";
    HashMap<String,String> user = new HashMap<String, String>();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sell_shoe);

        imageDownloaded = (ImageView) findViewById(R.id.imageDownloaded);

        DatabaseHandler db = new DatabaseHandler(getApplicationContext());
        user = db.getUserDetails();

        Intent i = getIntent();
        final String url = i.getStringExtra(TAG_IMAGE_URL);
        final String sid = i.getStringExtra(TAG_SHOE_ID);

        String uid = user.get("plain_id").toString();

        ArrayList<String> listSizes = db.getSizes(uid,sid);
        sp2 = (Spinner)findViewById(R.id.sizes_spinner);
        ArrayAdapter<String> adapter2 = new ArrayAdapter<String>(this, R.layout.spinner_layout,R.id.txt,listSizes);
        sp2.setAdapter(adapter2);

               // Edit Text
        et_selling_price = (EditText) findViewById(R.id.et_selling_price);
        etcomments = (EditText) findViewById(R.id.etcomments);
        etselldate = (EditText) findViewById(R.id.etselldate);
        etselldate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // calender class's instance and get current date , month and year from calender
                final Calendar c = Calendar.getInstance();
                int mYear = c.get(Calendar.YEAR); // current year
                int mMonth = c.get(Calendar.MONTH); // current month
                int mDay = c.get(Calendar.DAY_OF_MONTH); // current day
                // date picker dialog
                dpd = new DatePickerDialog(SellShoe.this,
                        new DatePickerDialog.OnDateSetListener() {

                            @Override
                            public void onDateSet(DatePicker view, int year,
                                                  int monthOfYear, int dayOfMonth) {
                                // set day of month , month and year value in the edit text
                                etselldate.setText(year+ "-" +(monthOfYear + 1)+ "-" +dayOfMonth);
                            }
                        }, mYear, mMonth, mDay);
                dpd.show();
            }
        });

        new DownloadImage(url).execute();

        // onClickListener for create button
        Button btnSave = (Button) findViewById(R.id.btnsave);
        btnSave.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                String userId = user.get("plain_id").toString();
                String size = sp2.getSelectedItem().toString();
                String sale_date = etselldate.getText().toString();
                String selling_price = et_selling_price.getText().toString();
                String comments = etcomments.getText().toString();
                new SellIt(sid,userId,size,sale_date,selling_price,comments).execute();
            }
        });

        // onClickListener for category button
        Button btnAddCat = (Button) findViewById(R.id.btnAddCat);
        btnAddCat.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                Intent i = new Intent(getApplicationContext(), AddType.class);
                startActivity(i);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode==RESULT_LOAD_IMAGE && resultCode== RESULT_OK && data !=null){
            Uri selectedImage = data.getData();
            //imageDownloaded.setImageURI(selectedImage);
        }

    }
    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        // On selecting a spinner item
        switch (view.getId()){
            case R.id.sizes_spinner:
                sp2.setSelection(position);
        }
    }
    /**
     * Background Async Task to Create new product
     * */
    class SellIt extends AsyncTask<String, String, String> {

        String userId;
        String sid;
        String size;
        String sale_date;
        String selling_price;
        String comments;

        public SellIt(String sid,String userId,String size, String sale_date,String selling_price, String comments){

            this.userId= userId;
            this.sid= sid;
            this.size= size;
            this.sale_date= sale_date;
            this.selling_price= selling_price;
            this.comments= comments;

        }

        /**
         * Before starting background thread Show Progress Dialog
         * */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(SellShoe.this);
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
            dataToSend.add(new BasicNameValuePair("sid", sid));
            dataToSend.add(new BasicNameValuePair("size", size));
            dataToSend.add(new BasicNameValuePair("sale_date", sale_date));
            dataToSend.add(new BasicNameValuePair("selling_price", selling_price));
            dataToSend.add(new BasicNameValuePair("comments", comments));

            // getting JSON Object
            // Note that create product url accepts POST method
            JSONObject json = jsonParser.makeHttpRequest(url_sell,"POST", dataToSend);

            // check log cat for response
            Log.d("Create Response", json.toString());

            // check for success tag
            try {
                int success = json.getInt(TAG_SUCCESS);

                if (success == 1) {
                    DatabaseHandler db = new DatabaseHandler(getApplicationContext());
                    String id = db.get_size_id(size,sid);
                    db.delete_size(id);
                    // successfully sold
                    Intent i = new Intent(getApplicationContext(),AllShoes.class);
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
    public void onNothingSelected(AdapterView<?> arg0) {
        // TODO Auto-generated method stub

    }

    /**
     * Background Async Task to Create new product
     * */
    class DownloadImage extends AsyncTask<Void, Void, Bitmap> {

        String url;

        public DownloadImage(String url){

            this.url= url;

        }

        /**
         * Before starting background thread Show Progress Dialog
         * */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(SellShoe.this);
            pDialog.setMessage("Saving..");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
        }

        /**
         * Creating animal
         * */
        protected Bitmap doInBackground(Void... args) {


            try {
                URLConnection connection = new URL(url).openConnection();
                connection.setConnectTimeout(1000*30);
                connection.setReadTimeout(1000*30);

                return BitmapFactory.decodeStream((InputStream) connection.getContent(),null,null);

            } catch (Exception e) {
                e.printStackTrace();
            }

            return null;
        }

        /**
         * After completing background task Dismiss the progress dialog
         * **/
        protected void onPostExecute(Bitmap bitmap) {
            // dismiss the dialog once done
            pDialog.dismiss();
            if(bitmap !=null){
                imageDownloaded.setImageBitmap(bitmap);
            }
        }

    }
}
