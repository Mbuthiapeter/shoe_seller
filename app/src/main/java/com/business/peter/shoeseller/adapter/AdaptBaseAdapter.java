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
import com.business.peter.shoeseller.model.ListSales;
import com.business.peter.shoeseller.model.ListSection;

import java.util.List;

/**
 * Created by Victor on 03/01/2017.
 */
public class AdaptBaseAdapter extends BaseAdapter {
    private static final int VIEW_TYPE_NONE = 0;
    private static final int VIEW_TYPE_SECTION = 1;
    private static final int VIEW_TYPE_ITEM = 2;

    private LayoutInflater layoutInflater;
    private List<ListData> dataList;

    public AdaptBaseAdapter(Context context, List<ListData> dataList) {
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
            convertView = layoutInflater.inflate(R.layout.sale_list_item, parent, false);
            itemViewHolder = new ItemViewHolder(convertView);
            convertView.setTag(itemViewHolder);
        } else {
            itemViewHolder = (ItemViewHolder) convertView.getTag();
        }
        ListSales listSales= (ListSales) getItem(position);
        itemViewHolder.setName(listSales.getName());
        itemViewHolder.setSize(listSales.getSize());
        itemViewHolder.setBuying_price(listSales.getBuying_price());
        itemViewHolder.setSelling_price(listSales.getSelling_price());
        itemViewHolder.setProfit(listSales.getProfit());
        itemViewHolder.setSid(listSales.getSid());
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
            } else if (listData instanceof ListSales) {
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
        TextView tvName, tvSize, tvBuying_price, tvSelling_price, tvProfit, tvSid;

        public ItemViewHolder(View itemView) {
            tvName = (TextView) itemView.findViewById(R.id.name);
            tvSize = (TextView) itemView.findViewById(R.id.size);
            tvBuying_price = (TextView) itemView.findViewById(R.id.buying_price);
            tvSelling_price = (TextView) itemView.findViewById(R.id.selling_price);
            tvProfit = (TextView) itemView.findViewById(R.id.profit);
            tvSid = (TextView) itemView.findViewById(R.id.sid);
        }

        public void setName(String name) {
            tvName.setText(name);
        }
        public void setSize(String size) {
            tvSize.setText(size);
        }
        public void setBuying_price(String buying_price) {
            tvBuying_price.setText(buying_price);
        }
        public void setSelling_price(String selling_price) {tvSelling_price.setText(selling_price); }
        public void setProfit(String profit) {
            tvProfit.setText(profit);
        }
        public void setSid(String sid) {
            tvSid.setText(sid);
        }
    }
}
