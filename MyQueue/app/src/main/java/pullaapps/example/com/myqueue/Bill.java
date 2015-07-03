package pullaapps.example.com.myqueue;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class Bill extends BaseActivity1 {

    ArrayList<Meal> cart_list;
    public View inflatedView;
    TextView orderId;
    TextView date;
    TextView time;
    String responseString;
    custom_list_one custom_adpter;
    ListView lv1;
    TextView total;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        inflatedView=getLayoutInflater().inflate(R.layout.activity_bill, frameLayout);
        getActionBar().setDisplayHomeAsUpEnabled(true);
        Intent i=getIntent();
        responseString=i.getStringExtra("Json");
        Log.e("Tagggy",responseString);
        orderId=(TextView)findViewById(R.id.OrderId_text);
        date=(TextView)findViewById(R.id.date);
        time=(TextView)findViewById(R.id.time);
        cart_list = new ArrayList<Meal>();
        total=(TextView)findViewById(R.id.total_value);
        getCartData(responseString);
        custom_adpter = new custom_list_one(this, cart_list);
        lv1 = (ListView) findViewById(R.id.listView);
        lv1.setAdapter(custom_adpter);
    }



    class custom_list_one extends BaseAdapter {
        private LayoutInflater layoutInflater;
        ViewHolder holder;
        private ArrayList<Meal> mcartList;
        int cartCounter;
        Context context;

        public custom_list_one(Context context, ArrayList<Meal> cart_list) {
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
                convertView = layoutInflater.inflate(R.layout.check_list, null);
                holder = new ViewHolder();
                holder.name = (TextView) convertView.findViewById(R.id.product_name);
                holder.product_mrp = (TextView) convertView.findViewById(R.id.product_mrp);
                holder.product_mrpvalue = (TextView) convertView.findViewById(R.id.product_mrpvalue);
                holder.qty = (TextView) convertView.findViewById(R.id.qty_value);
                holder.product_value = (TextView) convertView.findViewById(R.id.product_value);
                holder.qty_text = (TextView) convertView.findViewById(R.id.qty_text);
                holder.store_name = (TextView) convertView.findViewById(R.id.text_store);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            holder.name.setText(tempProduct.getItemName());

            holder.product_mrpvalue.setText("Rs " + tempProduct.getItemPrice());

            holder.store_name.setText(tempProduct.getStoreName());

            holder.qty.setText(String.valueOf(tempProduct.getQuantity()));

            holder.product_value.setText("Rs " + tempProduct.getItemPrice() * tempProduct.getQuantity() + "");

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
        }
    }

    public void getCartData(String str)
    {
        int tot=0;
        try {
            cart_list.clear();
            Meal tempMeal;
            JSONObject jsonObject=new JSONObject(str);
            orderId.setText(String.valueOf(jsonObject.getInt("orderId")));
			date.setText(jsonObject.getString("orderDate"));
            int customer=jsonObject.getInt("CustomerId");
			time.setText(jsonObject.getString("orderTime"));
            JSONArray jArray = jsonObject.getJSONArray("Items");
            Log.d("Count Tag ", "Length " + jArray.length());
            for(int i=0;i<jArray.length();i++) {
                JSONObject post = jArray.getJSONObject(i);
                tempMeal=new Meal();
                tempMeal.setID(post.getInt("ItemId"));
                tempMeal.setItemName(post.getString("ItemName"));
                tempMeal.setQuantity(post.getInt("Quantity"));
                tempMeal.setCustomerId(customer);
                tempMeal.setMerchantId(post.getInt("MerchantId"));
                tempMeal.setItemPrice(post.getInt("ItemPrice"));
                tempMeal.setItemTotalPrice(post.getInt("ItemTotalPrice"));
                tempMeal.setStoreName(post.getString("StoreName"));
                tot=tot+post.getInt("ItemTotalPrice");
                cart_list.add(tempMeal);
            }
          total.setText("Rs "+tot);
        }catch (JSONException e) {
            Log.e("log_tag", "Error parsing data " + e.toString());
        }
    }
}
