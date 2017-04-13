package com.business.peter.shoeseller;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.InputStream;
import java.util.ArrayList;

/**
 * Created by Victor on 26/11/2016.
 */
public class AnalysisAdapter extends ArrayAdapter<SoldShoe> {
    ArrayList<SoldShoe> shoesList;
    LayoutInflater vi;
    int Resource;
    ViewHolder holder2;

    public AnalysisAdapter(Context context, int resource, ArrayList<SoldShoe> objects) {
        super(context, resource, objects);
        vi = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        Resource = resource;
        shoesList = objects;
    }
    public boolean isAnalysis(){
        SoldShoe soldShoe = new SoldShoe();
        if (soldShoe.getLayout()==1);
            return true;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // convert view = design
        View v = convertView;
        if (v == null) {
            holder2 = new ViewHolder();
            v = vi.inflate(Resource, null);
                holder2.imageview = (ImageView) v.findViewById(R.id.downloadImage);
                holder2.tvName = (TextView) v.findViewById(R.id.tvName);
                holder2.sid = (TextView) v.findViewById(R.id.sid);
                holder2.tvBuyingPrice = (TextView) v.findViewById(R.id.tvBuyingPrice);
                holder2.tvSellingPrice = (TextView) v.findViewById(R.id.tvSellingPrice);
                holder2.tvProfit = (TextView) v.findViewById(R.id.tvProfit);
                holder2.tvCount = (TextView) v.findViewById(R.id.tvCount);
                holder2.tvType = (TextView) v.findViewById(R.id.tvType);

                v.setTag(holder2);
        } else{
            holder2 = (ViewHolder) v.getTag();
        }
            holder2.imageview.setImageResource(R.drawable.ic_launcher);
            new DownloadImageTask(holder2.imageview).execute(shoesList.get(position).getImage());
            holder2.tvName.setText(shoesList.get(position).getName());
            holder2.sid.setText(shoesList.get(position).getSid());
        holder2.tvName.setText(shoesList.get(position).getName());
        holder2.sid.setText(shoesList.get(position).getSid());
        holder2.tvBuyingPrice.setText("Buying price: " + shoesList.get(position).getBuying_price());
        holder2.tvSellingPrice.setText("Selling price: " + shoesList.get(position).getSelling_price());
        holder2.tvProfit.setText("Profit: " + shoesList.get(position).getProfit());
        holder2.tvCount.setText("Pieces Sold: " + shoesList.get(position).getCount());

            return v;

    }

    static class ViewHolder {
        public ImageView imageview;
        public TextView sid;
        public TextView tvName;
        public TextView tvDateBought;
        public TextView tvComments;
        public TextView tvBuyingPrice;
        public TextView tvSellingPrice;
        public TextView tvProfit;
        public TextView tvCount;
        public TextView tvColor;
        public TextView tvType;

    }
    private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
        ImageView bmImage;

        public DownloadImageTask(ImageView bmImage) {
            this.bmImage = bmImage;
        }

        protected Bitmap doInBackground(String... urls) {
            String urldisplay = urls[0];
            Bitmap mIcon11 = null;
            try {
                InputStream in = new java.net.URL(urldisplay).openStream();
                mIcon11 = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }
            return mIcon11;
        }

        protected void onPostExecute(Bitmap result) {
            bmImage.setImageBitmap(result);
        }

    }
}
