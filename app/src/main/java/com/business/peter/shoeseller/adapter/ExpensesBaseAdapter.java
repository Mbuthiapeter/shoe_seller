package com.business.peter.shoeseller.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.business.peter.shoeseller.R;
import com.business.peter.shoeseller.model.ListData;
import com.business.peter.shoeseller.model.ListSection;

import java.util.List;

/**
 * Created by Victor on 02/03/2017.
 */
public class ExpensesBaseAdapter extends BaseAdapter {
    private static final int VIEW_TYPE_NONE = 0;
    private static final int VIEW_TYPE_SECTION = 1;
    private static final int VIEW_TYPE_ITEM = 2;

    private LayoutInflater layoutInflater;
    private List<ListData> dataList;

    public ExpensesBaseAdapter(Context context, List<ListData> dataList) {
        this.dataList = dataList;
        this.layoutInflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return dataList != null ? dataList.size() : 0;
    }

    @Override
    public ListData getItem(int position) {
        if (dataList.isEmpty()) {
            return null;
        } else {
            return dataList.get(position);
        }
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (getItemViewType(position) == VIEW_TYPE_SECTION) {
            return getSectionView(position, convertView, parent);
        } else if (getItemViewType(position) == VIEW_TYPE_ITEM) {
            return getItemView(position, convertView, parent);
        }
        return null;
    }

    @NonNull
    private View getItemView(int position, View convertView, ViewGroup parent) {
        ItemViewHolder itemViewHolder;
        if (convertView == null) {
            convertView = layoutInflater.inflate(R.layout.expense_history_list, parent, false);
            itemViewHolder = new ItemViewHolder(convertView);
            convertView.setTag(itemViewHolder);
        } else {
            itemViewHolder = (ItemViewHolder) convertView.getTag();
        }
        Expense listHist= (Expense) getItem(position);
        itemViewHolder.setAmount(listHist.getAmount());
        itemViewHolder.setExpense(listHist.getExpense());
        itemViewHolder.setExpId(listHist.getExpId());
        return convertView;
    }

    @NonNull
    private View getSectionView(int position, View convertView, ViewGroup parent) {
        SectionViewHolder sectionViewHolder;
        if (convertView == null) {
            convertView = layoutInflater.inflate(R.layout.layout_list_section, parent, false);
            sectionViewHolder = new SectionViewHolder(convertView);
            convertView.setTag(sectionViewHolder);
        } else {
            sectionViewHolder = (SectionViewHolder) convertView.getTag();
        }
        sectionViewHolder.setTitle(((ListSection) getItem(position)).getTitle());
        return convertView;
    }

    @Override
    public int getItemViewType(int position) {
        if (getCount() > 0) {
            ListData listData = getItem(position);
            if (listData instanceof ListSection) {
                return VIEW_TYPE_SECTION;
            } else if (listData instanceof Expense) {
                return VIEW_TYPE_ITEM;
            } else {
                return VIEW_TYPE_NONE;
            }
        } else {
            return VIEW_TYPE_NONE;
        }
    }

    @Override
    public int getViewTypeCount() {
        return 3;
    }

    class SectionViewHolder {
        TextView tvTitle;

        public SectionViewHolder(View itemView) {
            tvTitle = (TextView) itemView.findViewById(R.id.text_view_title);
        }

        public void setTitle(String title) {
            tvTitle.setText(title);
        }
    }

    class ItemViewHolder {
        TextView tvAmount, tvExpId, tvExpense;

        public ItemViewHolder(View itemView) {
            tvAmount = (TextView) itemView.findViewById(R.id.tvAmount);
            tvExpId = (TextView) itemView.findViewById(R.id.tvExpId);
            tvExpense = (TextView) itemView.findViewById(R.id.tvExpense);
        }

        public void setAmount(String amount) {
            tvAmount.setText(amount);
        }
        public void setExpense(String expense) { tvExpense.setText(expense); }
        public void setExpId(String expId) {tvExpId.setText(expId); }
    }
}
