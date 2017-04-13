package com.business.peter.shoeseller;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Victor on 21/09/2016.
 */
public class ViewShoe extends Activity implements View.OnClickListener{

    private static final String TAG_SUCCESS = "success";
    private static final String TAG_SHOES = "shoes";
    private static final String TAG_TYPE = "type";
    private static final String TAG_COLOR = "color";
    private static final String TAG_DATE_BOUGHT = "date_bought";
    private static final String TAG_DESCRIPTION = "description";
    static  String TAG_SHOE_ID = "sid";
    static  String TAG_NAME = "name";
    static  String TAG_BUYING_PRICE = "buying_price";
    static  String TAG_IMAGE_URL = "url";

    private ProgressDialog pDialog;
    final Context context = this;

    JSONParser jsonParser = new JSONParser();

    TextView tvName, sid, tvDescription, tvColor, tvType,tvBuyingPrice, tvbuydate, tvSizes;
    ImageView downloadImage;
    Spinner sp;

    private static final int RESULT_LOAD_IMAGE = 1;
    HashMap<String,String> user = new HashMap<String, String>();

    private static String shoe_url = "http://www.kinyafridah.com/shoe_seller/shoe_inventory/";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.view_shoe);

        DatabaseHandler db = new DatabaseHandler(getApplicationContext());
        user = db.getUserDetails();


        // Edit Text
        tvName = (TextView) findViewById(R.id.tvName);
        sid = (TextView) findViewById(R.id.sid);
        tvDescription = (TextView) findViewById(R.id.tvDescription);
        downloadImage= (ImageView) findViewById(R.id.downloadImage);
        tvColor = (TextView) findViewById(R.id.tvColor);
        tvbuydate= (TextView) findViewById(R.id.tvbuydate);
        tvType= (TextView) findViewById(R.id.tvType);
        tvBuyingPrice= (TextView) findViewById(R.id.tvBuyingPrice);
        tvSizes= (TextView) findViewById(R.id.tvSizes);

        Intent i = getIntent();
        String name = i.getStringExtra(TAG_NAME);
        String description = i.getStringExtra(TAG_DESCRIPTION);
        String color = i.getStringExtra(TAG_COLOR);
        String buyDate = i.getStringExtra(TAG_DATE_BOUGHT);
        String type = i.getStringExtra(TAG_TYPE);
        String buyingPrice = i.getStringExtra(TAG_BUYING_PRICE);
        final String url = i.getStringExtra(TAG_IMAGE_URL);
        final String sid = i.getStringExtra(TAG_SHOE_ID);
       String uid = user.get("plain_id").toString();

        ArrayList<String> listSizes = db.getSizes(uid,sid);

        tvName.setText(name);
        tvDescription.setText("Description: " + description);
        tvColor.setText(color);
        tvbuydate.setText(buyDate);
        tvType.setText(type);
        tvBuyingPrice.setText(buyingPrice);

        for(int j=0; j < listSizes.size(); j++){

            tvSizes.setText(tvSizes.getText() + " " + listSizes.get(j) + " | ");
        }
        //tvSizes.setText(Arrays.toString(listSizes).replaceAll("\\[|\\]", ""));

        new DownloadImage(url).execute();


        //
        Button btnSell = (Button) findViewById(R.id.btnSell);
        btnSell.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                Intent in = new Intent(getApplicationContext(),SellShoe.class);
                in.putExtra(TAG_SHOE_ID, sid);
                in.putExtra(TAG_IMAGE_URL, url);
                startActivityForResult(in, 1);
            }
        });

        //  onClickListener to add size
        Button btnAddSize = (Button) findViewById(R.id.btnAddSize);
        btnAddSize.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                Intent in = new Intent(getApplicationContext(),AddSizes.class);
                in.putExtra(TAG_SHOE_ID, sid);
                startActivityForResult(in, 1);
            }
        });

        // onClickListener for edit button
        Button btnEdit = (Button) findViewById(R.id.btnEdit);
        btnEdit.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                Intent in = new Intent(getApplicationContext(),EditShoe.class);
                in.putExtra(TAG_SHOE_ID, sid);
                in.putExtra(TAG_IMAGE_URL, url);
                startActivityForResult(in, 1);
            }
        });
        // onClickListener for edit button
        Button btnDelete = (Button) findViewById(R.id.btnDelete);
        btnDelete.setOnClickListener(new View.OnClickListener() {
            String uid = user.get("plain_id").toString();

            @Override
            public void onClick(View view) {
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                        context);
                alertDialogBuilder.setTitle("Confirm Delete");
                alertDialogBuilder
                        .setMessage("Delete the item?")
                        .setCancelable(false)
                        .setPositiveButton("Yes",new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,int id) {

                                // deleting product in background thread
                                new DeleteShoe(uid,sid).execute();
                            }

                        })
                        .setNegativeButton("No",new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,int id) {
                                // if this button is clicked, just close
                                // the dialog box and do nothing
                                dialog.cancel();
                            }
                        });
                // create alert dialog
                AlertDialog alertDialog = alertDialogBuilder.create();

                // show it
                alertDialog.show();

            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode==RESULT_LOAD_IMAGE && resultCode== RESULT_OK && data !=null){
            Uri selectedImage = data.getData();
            //imageToUpload.setImageURI(selectedImage);
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
            pDialog = new ProgressDialog(ViewShoe.this);
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
                downloadImage.setImageBitmap(bitmap);
            }
        }

    }

    @Override
    public void onClick(View v) {
    }

    /**
     * Background Async Task to Create new product
     * */
    class DeleteShoe extends AsyncTask<String, String, String> {

        String sid;
        String uid;

        public DeleteShoe(String uid,String sid){

            this.sid= sid;
            this.uid= uid;
        }

        /**
         * Before starting background thread Show Progress Dialog
         * */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(ViewShoe.this);
            pDialog.setMessage("Deleting..");
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
            dataToSend.add(new BasicNameValuePair("sid", sid));
            dataToSend.add(new BasicNameValuePair("uid", uid));
            JSONObject json = jsonParser.makeHttpRequest(shoe_url + "delete_shoe.php","POST", dataToSend);

            // check log cat for response
            Log.d("Create Response", json.toString());

            // check for success tag
            try {
                int success = json.getInt(TAG_SUCCESS);

                if (success == 1) {
                    DatabaseHandler db = new DatabaseHandler(getApplicationContext());
                    ArrayList<String> listSizes = db.getDeleteSizes(sid);
                    for(int j=0; j < listSizes.size(); j++){
                        db.delete_size(listSizes.get(j));
                    }

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


}
