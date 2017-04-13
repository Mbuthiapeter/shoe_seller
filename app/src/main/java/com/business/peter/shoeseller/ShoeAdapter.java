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
 * Created by Victor on 13/09/2016.
 */
public class ShoeAdapter extends ArrayAdapter<Shoe> {
    ArrayList<Shoe> shoesList;
    LayoutInflater vi;
    int Resource;
    ViewHolder holder;

public ShoeAdapter(Context context, int resource, ArrayList<Shoe> objects) {
        super(context, resource, objects);
        vi = (LayoutInflater) context
        .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        Resource = resource;
        shoesList = objects;
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
        holder.tvDescription = (TextView) v.findViewById(R.id.tvDescriptionn);
        holder.tvBuyingPrice = (TextView) v.findViewById(R.id.tvBuyingPrice);
        holder.tvDateBought = (TextView) v.findViewById(R.id.tvDateBought);
        holder.tvType = (TextView) v.findViewById(R.id.tvType);
        holder.tvColor = (TextView) v.findViewById(R.id.tvColor);

        v.setTag(holder);
        } else {
        holder = (ViewHolder) v.getTag();
        }
        holder.imageview.setImageResource(R.drawable.ic_launcher);
        new DownloadImageTask(holder.imageview).execute(shoesList.get(position).getImage());
        holder.tvName.setText(shoesList.get(position).getName());
        holder.sid.setText(shoesList.get(position).getSid());
        holder.tvDescription.setText(shoesList.get(position).getDescription());
        holder.tvBuyingPrice.setText("Buying price: " + shoesList.get(position).getBuying_price());
        holder.tvDateBought.setText("Date bought: " + shoesList.get(position).getDate_bought());
        holder.tvColor.setText("Color: " + shoesList.get(position).getColor());
        holder.tvType.setText("Type: " + shoesList.get(position).getType());

        return v;

        }

  static class ViewHolder {
    public ImageView imageview;
    public TextView sid;
    public TextView tvName;
    public TextView tvDateBought;
    public TextView tvDescription;
    public TextView tvBuyingPrice;
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
