package pullaapps.example.com.myqueue;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v4.app.TaskStackBuilder;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.TextView;

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
import java.util.ArrayList;


public class DisplayOrder extends Activity {

    int orderid;
    ArrayList<Meal> cart_list;
    TextView orderId;
    TextView date;
    TextView time;
    TextView total;
    public Custom_list_one custom_adpter;
    ListView lv1;
    String responseString;
    final String URL = "http://myproject.byethost8.com/OrderDetails.php";
    SessionManager sessionManager;
    int userid;
    String date_value;
    String time_vaue;
    boolean isFirst;

    Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context=getApplicationContext();
        setContentView(R.layout.activity_display_order);
        Intent i=getIntent();
        sessionManager=new SessionManager(this);
        orderid=Integer.parseInt(i.getStringExtra("orderid"));
        date_value=i.getStringExtra("date");
        time_vaue=i.getStringExtra("time");
        orderId=(TextView)findViewById(R.id.order_value);
        date=(TextView)findViewById(R.id.date);
        time=(TextView)findViewById(R.id.time);
        cart_list = new ArrayList<Meal>();
        total=(TextView)findViewById(R.id.total_value);
        userid=Integer.parseInt(sessionManager.getUserId());
        custom_adpter = new Custom_list_one(this, cart_list);
        lv1 = (ListView) findViewById(R.id.lv);
        lv1.setAdapter(custom_adpter);
        isFirst=true;
    }

    public void onResume() {
        super.onResume();
        getCartData();
        Log.e("tag","registrating of receiver");
        context .registerReceiver(mMessageReceiver, new IntentFilter("unique_name"));
        Log.e("tag","After registrating of receiver");
    }

    //Must unregister onPause()
    @Override
    protected void onPause() {
        super.onPause();
        context.unregisterReceiver(mMessageReceiver);
    }

    public void getCartData() {
            new CustomHttpRequest().execute(URL);
    }

    public class CustomHttpRequest extends AsyncTask<String,Void,Integer>
    {
        InputStream inputStream;
        int result;
        private TransparentProgressDialog pDialog;
        protected void onPreExecute()
        {
            pDialog = new TransparentProgressDialog(DisplayOrder.this,R.drawable.spinner,"Updating Order..");
            pDialog.show();
        }

        protected Integer doInBackground(String... args)
        {
            try {
                ConnectivityManager cm =
                        (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);

                NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
                boolean isConnected = activeNetwork != null &&
                        activeNetwork.isConnectedOrConnecting();
                if (isConnected) {
                    Log.d("Tag", "In doBackground()");
                    java.net.URL url = new java.net.URL(args[0]);
                    HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                    httpURLConnection.setDoOutput(true);
                    httpURLConnection.setRequestMethod("POST");
                    String param=null;
                    param = "Key_Customer=" + userid + "&Key_Order=" +orderid;
                    httpURLConnection.setFixedLengthStreamingMode(param.getBytes().length);
                    PrintWriter out = new PrintWriter(httpURLConnection.getOutputStream());
                    out.print(param);
                    out.flush();
                    out.close();
                    int statusCode = httpURLConnection.getResponseCode();
                    if (statusCode == 200) {
                        inputStream = new BufferedInputStream(httpURLConnection.getInputStream());
                        responseString = convertInputStreamToString(inputStream);
                        Log.e("tag", responseString);
                        parseResult(responseString);
                        result = 1;
                    } else {
                        result = 0;
                    }
                }
                else
                {
                    result=2;
                }
            }catch(Exception e){
                Log.e("tag", e.toString());
            }
            return result;
        }

        protected void onPostExecute(Integer res)
        {
            if (pDialog.isShowing())
                pDialog.dismiss();
            if(res==2)
            {

            }
            else if(res==1)
            {
                updateUI();
            }
            else
            {

            }
        }
    }

    private String convertInputStreamToString(InputStream inputStream) throws IOException
    {

        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));

        StringBuffer sb = new StringBuffer("");
        String line = "";
        String NL = System.getProperty("line.separator");
        while ((line = bufferedReader.readLine()) != null) {
            sb.append(line+NL);
        }

            /* Close Stream */
        if (null != inputStream) {
            inputStream.close();
        }

        return sb.toString();
    }

    private void parseResult(String returnString) {
        try {
            cart_list.clear();
            Meal tempMeal;
            JSONArray jArray = new JSONObject(returnString.toString()).getJSONArray("carts");
            Log.d("Count Tag ","Length "+jArray.length());
            for(int i=0;i<jArray.length();i++) {
                JSONObject post = jArray.getJSONObject(i);
                tempMeal=new Meal();
                tempMeal.setID(post.getInt("ItemId"));
                tempMeal.setItemName(post.getString("Name"));
                tempMeal.setQuantity(post.getInt("Quantity"));
                tempMeal.setCustomerId(post.getInt("CustomerId"));
                tempMeal.setMerchantId(post.getInt("MerchantId"));
                tempMeal.setItemPrice(post.getInt("Price"));
                tempMeal.setItemTotalPrice(post.getInt("ItemTotalPrice"));
                tempMeal.setStoreName(post.getString("Merchant"));
                tempMeal.set_processed(Integer.parseInt(post.getString("Processed")));
                tempMeal.set_status(Integer.parseInt(post.getString("Status")));
                cart_list.add(tempMeal);
            }
        }catch (JSONException e) {
            Log.e("log_tag", "Error parsing data " + e.toString());
        }
    }


    private void updateUI()
    {
        //totalCartItemCount = cart_list.size();
        int totalCartValue = 0;

        for (int temp1 = 0; temp1 < cart_list.size(); temp1++) {
            totalCartValue = totalCartValue + cart_list.get(temp1).getItemTotalPrice();
        }
        orderId.setText(String.valueOf(orderid));
        date.setText(date_value);
        time.setText(time_vaue);
        //itemCount.setText("(" + totalCartItemCount + ")");
        total.setText("Rs"+String.valueOf(totalCartValue));
        if(isFirst) {
            custom_adpter = new Custom_list_one(this, cart_list);
            lv1.setAdapter(custom_adpter);
            isFirst=false;
        }
        else {
            custom_adpter.notifyDataSetChanged();
        }
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
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
            case R.id.action_search:
                // search action
                return true;
            case R.id.cart:
                // location found
                showCart();
                return true;
            case R.id.action_refresh:
                // refresh
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
    private void showCart() {
        Intent i = new Intent(this, MyCart.class);
        startActivity(i);
    }

    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Meal temp;
            Log.e("on display receiver","by pulla");
            // Extract data included in the Intent
            String orderid = intent.getStringExtra("orderid");
            String date = intent.getStringExtra("date");
            String time = intent.getStringExtra("time");
            Log.e("DisplayOrder by pulla",orderid+" "+date+"::"+time);
            String store=intent.getStringExtra("store");
            for(int i=0;i<cart_list.size();i++)
            {
                temp=cart_list.get(i);
                if(temp.getStoreName().equals(store))
                {
                    Log.e("item"+i,temp.getStoreName());
                    Log.e("Process",temp.get_processed()+"");
                    if(temp.get_processed()==0)
                    {
                        temp.set_processed(1);
                    }
                }
            }
            custom_adpter.notifyDataSetChanged();
        }
    };
}