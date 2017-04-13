package com.business.peter.shoeseller;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Victor on 05/07/2016.
 */
public class AddShoe extends Activity implements View.OnClickListener, AdapterView.OnItemSelectedListener {
    private ProgressDialog pDialog;

    JSONParser jsonParser = new JSONParser();

    EditText etname, etprice, etdescription, etcolor,etbuydate;
    ImageView imageToUpload;
    Spinner sp;
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
        setContentView(R.layout.add_product);

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
                dpd = new DatePickerDialog(AddShoe.this,
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

                Bitmap image = ((BitmapDrawable) imageToUpload.getDrawable()).getBitmap();

                String userId = user.get("plain_id").toString();
                String name = etname.getText().toString();
                String type = sp.getSelectedItem().toString();
                String price = etprice.getText().toString();
                String description = etdescription.getText().toString();
                String date_bought = etbuydate.getText().toString();
                String color = etcolor.getText().toString();
                new CreateNewProduct(userId,name,type,price,description,date_bought,color,image).execute();
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
        if (requestCode==RESULT_LOAD_IMAGE && resultCode== RESULT_OK && data !=null){
            Uri selectedImage = data.getData();
            imageToUpload.setImageURI(selectedImage);
        }

    }
    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        // On selecting a spinner item
        //String label = parent.getItemAtPosition(position).toString();
        sp.setSelection(position);

    }

    /**
     * Background Async Task to Create new product
     * */
    class CreateNewProduct extends AsyncTask<String, String, String> {

        Bitmap image;
        String userId;
        String name;
        String type;
        String price;
        String description;
        String date_bought;
        String color;

        public CreateNewProduct(String userId,String name,String type,String price, String description,String date_bought,String color, Bitmap image){

            this.image= image;
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
            pDialog = new ProgressDialog(AddShoe.this);
            pDialog.setMessage("Saving..");
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
            JSONObject json = jsonParser.makeHttpRequest(url_create + "create_shoe.php","POST", dataToSend);

            // check log cat for response
            Log.d("Create Response", json.toString());

            // check for success tag
            try {
                int success = json.getInt(TAG_SUCCESS);

                if (success == 1) {
                    // successfully created animal
                    String sid = json.getString(TAG_SID);
                    Intent i = new Intent(getApplicationContext(),AddSizes.class);
                    i.putExtra(TAG_SID, sid);
                    startActivityForResult(i, 2);
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
}
