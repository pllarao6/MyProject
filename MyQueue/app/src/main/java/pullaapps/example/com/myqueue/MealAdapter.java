package pullaapps.example.com.myqueue;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filterable;
import android.widget.TextView;

import java.io.Serializable;
import java.util.ArrayList;
import android.widget.*;
public class MealAdapter extends BaseAdapter implements Serializable,Filterable {
    Context mContext;
	Activity activity;
    ArrayList<MealDrawerItem> mealList;
	ArrayList<MealDrawerItem> filteredList;
	private MealFilter mealFilter;
    private  LayoutInflater mInflater;
    public MealAdapter(Context mContext, ArrayList<MealDrawerItem> listarray) {
        super();
        this.mContext = mContext;
        this.mealList=listarray;
		this.filteredList=listarray;
		getFilter();
    }

    public Object getItem(int position) {
        return filteredList.get(position);
    }
    public int getCount() {
        // TODO Auto-generated method stub
        return filteredList.size();
    }


    public long getItemId(int position) {
        return position;
    }

    public static class MealHolder
    {
        TextView title_item;
        TextView subtitle_item;       
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        MealHolder mealHolder;

        if(convertView==null)
        {
            mealHolder=new MealHolder();

            LayoutInflater mInflater = (LayoutInflater) mContext
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = mInflater.inflate(R.layout.mealitem, null);
            
            mealHolder.title_item = (TextView) convertView.findViewById(R.id.text_item_name);
            mealHolder.subtitle_item = (TextView) convertView.findViewById(R.id.text_item_price);

            convertView.setTag(mealHolder);
        }
        else
        {
            mealHolder=(MealHolder)convertView.getTag();
        }
        MealDrawerItem itm=filteredList.get(position);
        mealHolder.title_item.setText(itm.getTitle());

        mealHolder.subtitle_item.setText(String.valueOf(itm.getPrice()));
        return convertView;
    }
	/**
     * Get custom filter
     * @return filter
     */
    @Override
    public Filter getFilter() {
        if (mealFilter == null) {
            mealFilter = new MealFilter();
        }

        return mealFilter;
    }

    /**
     * Keep reference to children view to avoid unnecessary calls
     */
    static class ViewHolder {
        TextView iconText;
        TextView name;
    }

    /**
     * Custom filter for friend list
     * Filter content in friend list according to the search text
     */
    private class MealFilter extends Filter {

        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            FilterResults filterResults = new FilterResults();
            if (constraint!=null && constraint.length()>0) {
                ArrayList<MealDrawerItem> tempList = new ArrayList<MealDrawerItem>();

                // search content in friend list
                for (MealDrawerItem meal : mealList) {
                    if (meal.getTitle().toLowerCase().contains(constraint.toString().toLowerCase())) {
                        tempList.add(meal);
                    }
                }

                filterResults.count = tempList.size();
                filterResults.values = tempList;
            } else {
                filterResults.count = mealList.size();
                filterResults.values = mealList;
            }

            return filterResults;
        }

        /**
         * Notify about filtered list to ui
         * @param constraint text
         * @param results filtered result
         */
        @SuppressWarnings("unchecked")
        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            filteredList = (ArrayList<MealDrawerItem>) results.values;
            notifyDataSetChanged();
        }
    }
}