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
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

/**
 * Created by Victor on 22/12/2016.
 */
public class EditShoe extends Activity implements View.OnClickListener, AdapterView.OnItemSelectedListener {

    private ProgressDialog pDialog,pd;

    JSONParser jsonParser = new JSONParser();

    EditText etname, etprice, etdescription, etcolor,etbuydate;
    ImageView imageToUpload;
    Spinner sp;
    DatePickerDialog dpd;

    String name, color, type, buying_price, date_bought, description;

    private static final int RESULT_LOAD_IMAGE = 1;

    private static String url_edit = "http://www.kinyafridah.com/shoe_seller/shoe_inventory/";
    // JSON Node names
    private static final String TAG_SUCCESS = "success";
    private static final String TAG_SHOES = "shoes";
    private static final String TAG_DATE_BOUGHT = "date_bought";
    private static final String TAG_DESCRIPTION = "description";
    static  String TAG_SHOE_ID = "sid";
    static  String TAG_TYPE = "type";
    static  String TAG_BUYING_PRICE = "buying_price";
    static  String TAG_NAME = "name";
    static  String TAG_COLOR = "color";
    static  String TAG_IMAGE_URL = "url";

    JSONArray shoes = null;

    HashMap<String,String> user = new HashMap<String, String>();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_shoe);

        DatabaseHandler db = new DatabaseHandler(getApplicationContext());
        user = db.getUserDetails();

        ArrayList<String> listPro = db.getAllTypes();
        sp = (Spinner)findViewById(R.id.spinner);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.spinner_layout,R.id.txt,listPro);

        sp.setAdapter(adapter);

        // Edit Text
        etname = (EditText) findViewById(R.id.etname);
        etprice = (EditText) findViewById(R.id.etprice);
        etdescription = (EditText) findViewById(R.id.etdescription);
        imageToUpload= (ImageView) findViewById(R.id.imageToUpload);
        etcolor = (EditText) findViewById(R.id.etcolor);
        etbuydate= (EditText) findViewById(R.id.etbuydate);
        etbuydate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // calender class's instance and get current date , month and year from calender
                final Calendar c = Calendar.getInstance();
                int mYear = c.get(Calendar.YEAR); // current year
                int mMonth = c.get(Calendar.MONTH); // current month
                int mDay = c.get(Calendar.DAY_OF_MONTH); // current day
                // date picker dialog
                dpd = new DatePickerDialog(EditShoe.this,
                        new DatePickerDialog.OnDateSetListener() {

                            @Override
                            public void onDateSet(DatePicker view, int year,
                                                  int monthOfYear, int dayOfMonth) {
                                // set day of month , month and year value in the edit text
                                etbuydate.setText(year+ "-" +(monthOfYear + 1)+ "-" +dayOfMonth);
                            }
                        }, mYear, mMonth, mDay);
                dpd.show();
            }
        });

        Intent i = getIntent();
        final String url = i.getStringExtra(TAG_IMAGE_URL);
        final String sid = i.getStringExtra(TAG_SHOE_ID);

        String userId = user.get("plain_id").toString();

        new FetchShoe(userId,sid).execute();
        new DownloadImage(url).execute();
        imageToUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent galleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(galleryIntent, RESULT_LOAD_IMAGE);
            }
        });

        // onClickListener for create button
        Button btnSave = (Button) findViewById(R.id.btnsave);
        btnSave.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                Intent i = getIntent();

                Bitmap image = ((BitmapDrawable) imageToUpload.getDrawable()).getBitmap();

                String userId = user.get("plain_id").toString();
                final String sid = i.getStringExtra(TAG_SHOE_ID);
                String name = etname.getText().toString();
                String type = sp.getSelectedItem().toString();
                String price = etprice.getText().toString();
                String description = etdescription.getText().toString();
                String date_bought = etbuydate.getText().toString();
                String color = etcolor.getText().toString();
                new EditThisShoe(sid,userId,name,type,price,description,date_bought,color,image).execute();
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
        // onClickListener for cancel button
        Button btnCancel = (Button) findViewById(R.id.btnCancel);
        btnCancel.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                Intent it = new Intent(getApplicationContext(), AllShoes.class);
                startActivity(it);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RESULT_LOAD_IMAGE && resultCode == RESULT_OK && data != null) {
            Uri selectedImage = data.getData();
            imageToUpload.setImageURI(selectedImage);
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

    /**
     * Background Async Task to Create new product
     * */
    class EditThisShoe extends AsyncTask<String, String, String> {

        Bitmap image;
        String userId;
        String sid;
        String name;
        String type;
        String price;
        String description;
        String date_bought;
        String color;

        public EditThisShoe(String sid,String userId,String name,String type,String price, String description,String date_bought,String color, Bitmap image){

            this.image= image;
            this.sid= sid;
            this.userId= userId;
            this.name= name;
            this.type= type;
            this.price= price;
            this.description= description;
            this.date_bought= date_bought;
            this.color= color;

        }

        /**
         * Before starting background thread Show Progress Dialog
         * */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(EditShoe.this);
            pDialog.setMessage("Updating..");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
        }

        /**
         * Creating animal
         * */
        protected String doInBackground(String... args) {

            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            image.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
            String encodedImage = Base64.encodeToString(byteArrayOutputStream.toByteArray(), Base64.DEFAULT);

            // Building Parameters
            ArrayList<NameValuePair> dataToSend = new ArrayList<NameValuePair>();
            dataToSend.add(new BasicNameValuePair("sid", sid));
            dataToSend.add(new BasicNameValuePair("userId", userId));
            dataToSend.add(new BasicNameValuePair("name", name));
            dataToSend.add(new BasicNameValuePair("type", type));
            dataToSend.add(new BasicNameValuePair("price", price));
            dataToSend.add(new BasicNameValuePair("description", description));
            dataToSend.add(new BasicNameValuePair("color", color));
            dataToSend.add(new BasicNameValuePair("date_bought", date_bought));
            dataToSend.add(new BasicNameValuePair("image",encodedImage));

            // getting JSON Object
            // Note that create product url accepts POST method
            JSONObject json = jsonParser.makeHttpRequest(url_edit + "edit_shoe.php","POST", dataToSend);

            // check log cat for response
            Log.d("Create Response", json.toString());

            // check for success tag
            try {
                int success = json.getInt(TAG_SUCCESS);

                if (success == 1) {
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

    /**
     * Background Async Task to Create new product
     * */
    class FetchShoe extends AsyncTask<String, String, String> {

        String userId;
        String sid;

        public FetchShoe(String userId,String sid){

            this.sid= sid;
            this.userId= userId;

        }

        /**
         * Before starting background thread Show Progress Dialog
         * */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pd = new ProgressDialog(EditShoe.this);
            pd.setMessage("Fetching..");
            pd.setIndeterminate(false);
            pd.setCancelable(true);
            pd.show();
        }

        /**
         * Creating animal
         * */
        protected String doInBackground(String... args) {

            // Building Parameters
            ArrayList<NameValuePair> dataToSend = new ArrayList<NameValuePair>();
            dataToSend.add(new BasicNameValuePair("userId", userId));
            dataToSend.add(new BasicNameValuePair("sid", sid));

            // getting JSON Object
            // Note that create product url accepts POST method
            JSONObject json = jsonParser.makeHttpRequest(url_edit + "get_one_shoe.php","GET", dataToSend);

            // check log cat for response
            Log.d("Create Response", json.toString());

            // check for success tag
            try {
                int success = json.getInt(TAG_SUCCESS);

                if (success == 1) {

                    shoes = json.getJSONArray(TAG_SHOES);
                    for (int i = 0; i < shoes.length(); i++) {
                        JSONObject c = shoes.getJSONObject(i);

                        // Storing each json item in variable
                        String id = c.getString(TAG_SHOE_ID);
                        name = c.getString(TAG_NAME);
                        date_bought = c.getString(TAG_DATE_BOUGHT);
                        buying_price = c.getString(TAG_BUYING_PRICE);
                        description = c.getString(TAG_DESCRIPTION);
                        type = c.getString(TAG_TYPE);
                        color = c.getString(TAG_COLOR);


                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                etname.setText(name);
                                etprice.setText(buying_price);
                                etdescription.setText(description);
                                etbuydate.setText(date_bought);
                                etdescription.setText(description);
                                etcolor.setText(color);
                            }
                        });
                    }
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
            pd.dismiss();
        }

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
            pDialog = new ProgressDialog(EditShoe.this);
            pDialog.setMessage("Loading..");
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
                imageToUpload.setImageBitmap(bitmap);
            }
        }

    }
}
