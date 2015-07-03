package pullaapps.example.com.myqueue;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

public class Custom_list_one extends BaseAdapter {
    private LayoutInflater layoutInflater;
    ViewHolder holder;
    private ArrayList<Meal> mcartList;
    int cartCounter;
    Context context;

    public Custom_list_one(Context context, ArrayList<Meal> cart_list) {
        layoutInflater = LayoutInflater.from(context);
        mcartList = cart_list;
        this.cartCounter = mcartList.size();
        this.context = context;
    }

    @Override
    public int getCount() {

        return cartCounter;
    }

    @Override
    public Object getItem(int arg0) {

        return mcartList.get(arg0);
    }

    @Override
    public long getItemId(int arg0) {

        return arg0;
    }

    public View getView(final int position, View convertView, ViewGroup parent) {
        Meal tempProduct = mcartList.get(position);
        if (convertView == null) {
            convertView = layoutInflater.inflate(R.layout.list_vieworder, null);
            holder = new ViewHolder();
            holder.name = (TextView) convertView.findViewById(R.id.product_name);
            holder.product_mrp = (TextView) convertView.findViewById(R.id.product_mrp);
            holder.product_mrpvalue = (TextView) convertView.findViewById(R.id.product_mrpvalue);
            holder.qty = (TextView) convertView.findViewById(R.id.qty_value);
            holder.product_value = (TextView) convertView.findViewById(R.id.product_value);
            holder.qty_text = (TextView) convertView.findViewById(R.id.qty_text);
            holder.store_name = (TextView) convertView.findViewById(R.id.text_store);
            holder.pay=(ImageView)convertView.findViewById(R.id.paystatus);
            holder.process=(ImageView)convertView.findViewById(R.id.processstatus);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.name.setText(tempProduct.getItemName());

        holder.product_mrpvalue.setText("Rs " + tempProduct.getItemPrice());

        holder.store_name.setText(tempProduct.getStoreName());

        holder.qty.setText(String.valueOf(tempProduct.getQuantity()));

        holder.product_value.setText("Rs " + tempProduct.getItemPrice() * tempProduct.getQuantity() + "");

        holder.pay.setImageResource((tempProduct.get_status()==1)?(R.drawable.yes):(R.drawable.no));

        holder.process.setImageResource((tempProduct.get_processed()==1)?(R.drawable.yes):(R.drawable.no));

        return convertView;
    }

    class ViewHolder {
        TextView name;
        TextView product_mrp;
        TextView product_mrpvalue;
        TextView qty_text;
        TextView product_value;
        TextView qty;
        TextView store_name;
        ImageView pay;
        ImageView process;
    }
}
