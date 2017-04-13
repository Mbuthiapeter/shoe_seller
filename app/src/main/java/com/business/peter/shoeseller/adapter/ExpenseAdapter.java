package com.business.peter.shoeseller.adapter;

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

import com.business.peter.shoeseller.R;
import com.business.peter.shoeseller.Shoe;

import java.io.InputStream;
import java.util.ArrayList;

/**
 * Created by Victor on 01/03/2017.
 */
public class ExpenseAdapter extends ArrayAdapter<Expense> {
    ArrayList<Expense> expensesList;
    LayoutInflater vi;
    int Resource;
    ViewHolder holder;

    public ExpenseAdapter(Context context, int resource, ArrayList<Expense> objects) {
        super(context, resource, objects);
        vi = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        Resource = resource;
        expensesList = objects;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // convert view = design
        View v = convertView;
        if (v == null) {
            holder = new ViewHolder();
            v = vi.inflate(Resource, null);
            holder.tvAmount = (TextView) v.findViewById(R.id.tvAmount);
            holder.tvExpense = (TextView) v.findViewById(R.id.tvExpense);
            holder.tvExpId = (TextView) v.findViewById(R.id.tvExpId);

            v.setTag(holder);
        } else {
            holder = (ViewHolder) v.getTag();
        }
        holder.tvExpId.setText(expensesList.get(position).getExpId());
        holder.tvAmount.setText("Amount: " + expensesList.get(position).getAmount());
        holder.tvExpense.setText("Expense: " + expensesList.get(position).getExpense());

        return v;

    }

    static class ViewHolder {
        public TextView tvAmount;
        public TextView tvExpense;
        public TextView tvExpId;
    }

}
