package pullaapps.example.com.myqueue;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v4.app.TaskStackBuilder;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;

import pullaapps.example.com.myqueue.network.HttpConnector;


public class MyCart extends BaseActivity1 {

    private ArrayList<Meal> cart_list;
    int totalCartItemCount =0;
    int totalCartValue = 0;
    private final String[] qtyValues = {"1","2","3","4","5","6","7","8","9","10"};
    private final String selectURL="http://myproject.byethost8.com/RetrieveCart.php";
    private final String updateURL="http://myproject.byethost8.com/UpdateCart.php";
    private final String deleteURL="http://myproject.byethost8.com/DeleteCart.php";
    boolean isFirst;
    private int spinner_pos;
    private int parent_pos;
    custom_list_one custom_adpter;
    private TextView itemText;
    private TextView itemCount;
    private TextView totalAmount;
    private Button checkout;
    private ListView lv1;
    TextView cartEmpty;
    private String temp;
    private View inflatedView;
    private SessionManager sessionManager;
    private String lat;
    private String lon;
    private int userid;
    private Context context;
    private HashMap<String, String> location;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        inflatedView=getLayoutInflater().inflate(R.layout.activity_my_cart, frameLayout);
        context=getApplicationContext();
        getCartData("create");
        sessionManager=new SessionManager(this);
        isFirst=true;
        itemText = (TextView) findViewById(R.id.item_text);
        itemCount = (TextView) findViewById(R.id.item_count);
        totalAmount = (TextView) findViewById(R.id.total_amount);
        checkout = (Button) findViewById(R.id.checkout);
        lv1 = (ListView) findViewById(R.id.listView1);
        cartEmpty = (TextView) findViewById(R.id.cart_empty);
        cart_list = new ArrayList<Meal>();
        location=sessionManager.getLocationInfo();
        lat=location.get("latitude");
        lon=location.get("longitude");
        userid=Integer.parseInt(sessionManager.getUserId());
        checkout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(),"Processing",Toast.LENGTH_SHORT).show();
                Intent intent=new Intent(getApplicationContext(),CheckOut.class);
                startActivity(intent);
            }
        });
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
                convertView = layoutInflater.inflate(R.layout.list_one, null);
                holder = new ViewHolder();
                holder.name = (TextView) convertView.findViewById(R.id.product_name);
                holder.product_mrp = (TextView) convertView.findViewById(R.id.product_mrp);
                holder.product_mrpvalue = (TextView) convertView.findViewById(R.id.product_mrpvalue);
                holder.qty = (Spinner) convertView.findViewById(R.id.spinner1);
                holder.cancel = (ImageButton) convertView.findViewById(R.id.delete);
                holder.product_value = (TextView) convertView.findViewById(R.id.product_value);
                holder.qty_text = (TextView) convertView.findViewById(R.id.qty_text);
                holder.store_name=(TextView)convertView.findViewById(R.id.text_store);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            holder.name.setText(tempProduct.getItemName());

            holder.product_mrpvalue.setText("Rs " + tempProduct.getItemPrice());

            holder.store_name.setText(tempProduct.getStoreName());

            ArrayAdapter<String> aa = new ArrayAdapter<String>(context, R.layout.qty_spinner, qtyValues);

            aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

            holder.qty.setAdapter(aa);

            holder.qty.setSelection(tempProduct.getQuantity() - 1);

            holder.product_value.setText("Rs " + tempProduct.getItemPrice() * tempProduct.getQuantity() + "");

            holder.cancel.setOnClickListener(new MyPersonalClickListener("button_delete", tempProduct));

            holder.qty.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int selectionIndex, long id) {
                    //if user has changed the quantity, then save it in the DB. refresh cart_list

                    if ((parent.getSelectedItemPosition() + 1) != cart_list.get(position).getQuantity()) {
                        spinner_pos = parent.getSelectedItemPosition()+1;
                        parent_pos = position;
                        Log.d("tag_quantity",spinner_pos+"");
                        getCartData("update");
                        Log.d("tag_status","After Updation");
                    }
                }

                @Override
                public void onNothingSelected(AdapterView<?> arg0) {

                }
            });
            return convertView;
        }

        class ViewHolder {
            TextView name;
            TextView product_mrp;
            TextView product_mrpvalue;
            TextView qty_text;
            TextView product_value;
            ImageButton cancel;
            Spinner qty;
            TextView store_name;
        }
    }

    public class MyPersonalClickListener implements View.OnClickListener {

        String button_name;
        Meal prod_name;

        public MyPersonalClickListener(String button_name, Meal prod_name) {
            this.prod_name = prod_name;
            this.button_name = button_name;
        }

        @Override
        public void onClick(View v)
        {

            if (button_name == "button_delete") {

                deleteCartData(String.valueOf(prod_name.getID()),String.valueOf(prod_name.getMerchantId()));

            }
        }
    }

    public void getCartData(String x)
    {
        if(x=="create")
        new CustomHttpRequest().execute(selectURL,"create");
        if (x=="update")
           new CustomHttpRequest().execute(updateURL,"update");
    }

    public void deleteCartData( String x,String y)
    {
        new CustomHttpRequest().execute(deleteURL,"delete",x,y);
    }

    public class CustomHttpRequest extends AsyncTask<String,Void,Integer>
    {
        private int result;
        private String responseString;
        private TransparentProgressDialog pDialog;
        private JSONObject toSend;
        private HttpConnector httpConnector;
        protected void onPreExecute()
        {
            pDialog = new TransparentProgressDialog(MyCart.this,R.drawable.spinner,"Updating Cart..");
            pDialog.show();
        }

        protected Integer doInBackground(String... args)
        {
            temp=args[1];
            try {
                toSend=new JSONObject();
                if(args[1]=="create") {
                    toSend.put("KEY_CUSTOMER", userid);
                    toSend.put("Key_Lat", lat);
                    toSend.put("Key_Lon", lon);
                    httpConnector=new HttpConnector(toSend,args[0],"POST",context);
                }
                else if(args[1]=="update"){
                    int TotalItemPrice=spinner_pos*(cart_list.get(parent_pos).getItemPrice());
                    Log.d("Tag_TotalItemPrice",TotalItemPrice+"");
                    toSend.put("ItemId",cart_list.get(parent_pos).getID());
                    toSend.put("Quant",spinner_pos);
                    toSend.put("Key_Merchant",cart_list.get(parent_pos).getMerchantId());
                    toSend.put("Key_Customer",userid);
                    toSend.put("Key_Lat",lat);
                    toSend.put("Key_Lon",lon);
                    httpConnector=new HttpConnector(toSend,args[0],"POST",context);
                    }
                    else if(args[1]=="delete")
                    {
                        toSend.put("ItemId",args[2]);
                        toSend.put("Key_Customer",userid);
                        toSend.put("Key_Merchant",args[3]);
                        toSend.put("Key_Lat", lat);
                        toSend.put("Key_Lon",lon);
                        httpConnector=new HttpConnector(toSend,args[0],"POST",context);
                    }
                     result= httpConnector.makeConnection();
                    if (result==1) {
                        responseString = httpConnector.convertInputStream();
                    }
                }catch(Exception e){
                    Log.e("tag", e.toString());
                    }
            return result;
        }

        protected void onPostExecute(Integer res)
        {
            if(httpConnector!=null)
                httpConnector.close();
            if (pDialog.isShowing())
                pDialog.dismiss();
            if(res==2)
            {
                Toast.makeText(context,"No Internet connection..Try again",Toast.LENGTH_SHORT).show();
            }
            else if(res==1)
            {
                    parseResult(responseString);
                    if(temp=="delete")
                       updateUI1();
                    else
                       updateUI();
            }
            else
            {
                Toast.makeText(context,"Failed to update data",Toast.LENGTH_SHORT).show();
            }
        }
    }


    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                Intent upIntent = NavUtils.getParentActivityIntent(this);
                if (NavUtils.shouldUpRecreateTask(this, upIntent)) {
                    // This activity is NOT part of this app's task, so create a new task
                    // when navigating up, with a synthesized back stack.
                    TaskStackBuilder.create(this)
                            // Add all of this activity's parents to the back stack
                            .addNextIntentWithParentStack(upIntent)
                                    // Navigate up to the closest parent
                            .startActivities();
                } else {
                    // This activity is part of this app's task, so simply
                    // navigate up to the logical parent activity.
                    NavUtils.navigateUpTo(this, upIntent);
                }
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void parseResult(String returnString) {
        try {
            cart_list.clear();
            Meal tempMeal;
            JSONArray jArray = new JSONObject(returnString.toString()).getJSONArray("carts");
            Log.d("Count Tag ","Length "+jArray.length());
            for(int i=0;i<jArray.length();i++) {
                JSONObject post = jArray.getJSONObject(i).getJSONObject("cart");
                tempMeal=new Meal();
                tempMeal.setID(post.getInt("ItemId"));
                tempMeal.setItemName(post.getString("Name"));
                tempMeal.setQuantity(post.getInt("Quantity"));
                tempMeal.setCustomerId(post.getInt("CustomerId"));
                tempMeal.setMerchantId(post.getInt("MerchantId"));
                tempMeal.setItemPrice(post.getInt("Price"));
                tempMeal.setItemTotalPrice(post.getInt("ItemTotalPrice"));
                tempMeal.setStoreName(post.getString("StoreName"));
                cart_list.add(tempMeal);
            }
        }catch (JSONException e) {
            Log.e("log_tag", "Error parsing data " + e.toString());
        }
    }

    private void updateUI()
    {
            totalCartItemCount = cart_list.size();
            totalCartValue = 0;
            for (int temp1 = 0; temp1 < cart_list.size(); temp1++) {
                totalCartValue = totalCartValue + cart_list.get(temp1).getItemTotalPrice();
            }

            if (totalCartItemCount == 0) {
                itemText.setVisibility(View.INVISIBLE);
                itemCount.setVisibility(View.INVISIBLE);
                totalAmount.setVisibility(View.INVISIBLE);
                checkout.setVisibility(View.INVISIBLE);
                lv1.setVisibility(View.INVISIBLE);
                cartEmpty.setVisibility(View.VISIBLE);
            } else {
                itemText.setVisibility(View.VISIBLE);
                itemCount.setVisibility(View.VISIBLE);
                totalAmount.setVisibility(View.VISIBLE);
                checkout.setVisibility(View.VISIBLE);
                lv1.setVisibility(View.VISIBLE);
                cartEmpty.setVisibility(View.INVISIBLE);
            }

            itemCount.setText("(" + totalCartItemCount + ")");
            totalAmount.setText("Rs " + totalCartValue);
        if(isFirst) {
            custom_adpter = new custom_list_one(this, cart_list);
            lv1.setAdapter(custom_adpter);
            isFirst=false;
        }
        else
             custom_adpter.notifyDataSetChanged();
    }

    private void updateUI1()
    {
        totalCartItemCount = cart_list.size();
        totalCartValue = 0;
        for (int temp1 = 0; temp1 < cart_list.size(); temp1++) {
            totalCartValue = totalCartValue + cart_list.get(temp1).getItemTotalPrice();
        }

        if (totalCartItemCount == 0) {
            itemText.setVisibility(View.INVISIBLE);
            itemCount.setVisibility(View.INVISIBLE);
            totalAmount.setVisibility(View.INVISIBLE);
            checkout.setVisibility(View.INVISIBLE);
            lv1.setVisibility(View.INVISIBLE);
            cartEmpty.setVisibility(View.VISIBLE);
        } else {
            itemText.setVisibility(View.VISIBLE);
            itemCount.setVisibility(View.VISIBLE);
            totalAmount.setVisibility(View.VISIBLE);
            checkout.setVisibility(View.VISIBLE);
            lv1.setVisibility(View.VISIBLE);
            cartEmpty.setVisibility(View.INVISIBLE);
        }

            itemCount.setText("(" + totalCartItemCount + ")");
            totalAmount.setText("Rs " + totalCartValue);
            custom_adpter=null;
            custom_adpter = new custom_list_one(this, cart_list);
            lv1.setAdapter(custom_adpter);
        }
}
