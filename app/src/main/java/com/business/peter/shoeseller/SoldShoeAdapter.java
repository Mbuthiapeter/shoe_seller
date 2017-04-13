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
public class SoldShoeAdapter extends ArrayAdapter<SoldShoe> {
    ArrayList<SoldShoe> shoesList;
    LayoutInflater vi;
    int Resource;
    ViewHolder holder,holder2;

    private static final int TYPES_COUNT = 2;
    private static final int TYPE_ANALYSIS = 0;
    private static final int TYPE_SALES = 1;

    public SoldShoeAdapter(Context context, int resource, ArrayList<SoldShoe> objects) {
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
            holder = new ViewHolder();
            v = vi.inflate(Resource, null);


                holder.imageview = (ImageView) v.findViewById(R.id.downloadImage);
                holder.tvName = (TextView) v.findViewById(R.id.tvName);
                holder.sid = (TextView) v.findViewById(R.id.sid);
                holder.tvComments = (TextView) v.findViewById(R.id.tvComments);
                holder.tvBuyingPrice = (TextView) v.findViewById(R.id.tvBuyingPrice);
                holder.tvSellingPrice = (TextView) v.findViewById(R.id.tvSellingPrice);
                holder.tvProfit = (TextView) v.findViewById(R.id.tvProfit);
                holder.tvDateBought = (TextView) v.findViewById(R.id.tvDateBought);
                holder.tvSize = (TextView) v.findViewById(R.id.tvSize);
                holder.tvColor = (TextView) v.findViewById(R.id.tvColor);
                holder.tvType = (TextView) v.findViewById(R.id.tvType);

                v.setTag(holder);

        } else {
            holder = (ViewHolder) v.getTag();
        }

            holder.imageview.setImageResource(R.drawable.ic_launcher);
            new DownloadImageTask(holder.imageview).execute(shoesList.get(position).getImage());
            holder.tvName.setText(shoesList.get(position).getName());
            holder.sid.setText(shoesList.get(position).getSid());
            holder.tvComments.setText(shoesList.get(position).getComment());
            holder.tvBuyingPrice.setText("Buying price: " + shoesList.get(position).getBuying_price());
            holder.tvSellingPrice.setText("Selling price: " + shoesList.get(position).getSelling_price());
            holder.tvProfit.setText("Profit: " + shoesList.get(position).getProfit());
            holder.tvDateBought.setText("Date bought: " + shoesList.get(position).getDate_bought());
            holder.tvSize.setText("Size sold: " + shoesList.get(position).getSize());
            holder.tvType.setText("Type: " + shoesList.get(position).getType());
            holder.tvColor.setText("Color: " + shoesList.get(position).getColor());

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
        public TextView tvSize;
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
