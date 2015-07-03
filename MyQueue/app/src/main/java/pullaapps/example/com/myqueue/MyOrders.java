package pullaapps.example.com.myqueue;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
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


public class MyOrders extends BaseActivity {

    public View inflatedView;
    SessionManager sessionManager;
    int userid;
    final String URL = "http://myproject.byethost8.com/Order.php";
    ArrayList<Order> order_list;
    custom_list_one custom_adpter;
    ListView lv;
    int totalOrderItemCount = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        inflatedView=getLayoutInflater().inflate(R.layout.activity_my_orders, frameLayout);
        setTitle(titles[2]);
        flag2=0;
        flag1=0;
        flag3=1;
        sessionManager=new SessionManager(this);
        userid=Integer.parseInt(sessionManager.getUserId());
        order_list=new ArrayList<Order>();
        getOrders();
        lv = (ListView) findViewById(R.id.listView);
    }

    class custom_list_one extends BaseAdapter {
        private LayoutInflater layoutInflater;
        ViewHolder holder;
        private ArrayList<Order> morderList;
        int orderCounter;
        Context context;

        public custom_list_one(Context context, ArrayList<Order> order_list) {
            layoutInflater = LayoutInflater.from(context);
            morderList = order_list;
            this.orderCounter = morderList.size();
            this.context = context;
        }

        @Override
        public int getCount() {

            return orderCounter;
        }

        @Override
        public Object getItem(int arg0) {

            return morderList.get(arg0);
        }

        @Override
        public long getItemId(int arg0) {

            return arg0;
        }

        public View getView(final int position, View convertView, ViewGroup parent) {
            Order tempProduct = morderList.get(position);
            if (convertView == null) {
                convertView = layoutInflater.inflate(R.layout.list_orders, null);
                holder = new ViewHolder();
                holder.orderid = (TextView) convertView.findViewById(R.id.order_value);
                holder.date = (TextView) convertView.findViewById(R.id.date);
                holder.time = (TextView) convertView.findViewById(R.id.time);
                holder.total=(TextView)convertView.findViewById(R.id.total);
                holder.pay=(ImageView)convertView.findViewById(R.id.pay_status_view);
                holder.process=(ImageView)convertView.findViewById(R.id.process_status_view);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            holder.orderid.setText(String.valueOf(tempProduct.getOrderid()));

            holder.date.setText(tempProduct.getDate());

            holder.time.setText(tempProduct.getTime());

            holder.total.setText("Rs "+String.valueOf(tempProduct.getTotal()));

            holder.pay.setImageResource((tempProduct.getPayStatus()==1)?R.drawable.yes:R.drawable.no);

            holder.process.setImageResource((tempProduct.getProcessStatus()==1)?R.drawable.yes:R.drawable.no);

            return convertView;
        }

        class ViewHolder {
            TextView orderid;
            TextView date;
            TextView time;
            TextView total;
            ImageView pay;
            ImageView process;
        }
    }

    public void getOrders()
    {
        new CustomHttpRequest().execute(URL);
    }

    public class CustomHttpRequest extends AsyncTask<String,Void,Integer>
    {
        InputStream inputStream;
        int result;
        String responseString;
        private TransparentProgressDialog pDialog;
        protected void onPreExecute()
        {
            pDialog = new TransparentProgressDialog(MyOrders.this,R.drawable.spinner,"Updating Cart..");
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
                    param = "KEY_CUSTOMER=" + userid;
                    httpURLConnection.setFixedLengthStreamingMode(param.getBytes().length);
                    PrintWriter out = new PrintWriter(httpURLConnection.getOutputStream());
                    out.print(param);
                    out.flush();
                    out.close();
                    int statusCode = httpURLConnection.getResponseCode();
                    if (statusCode == 200) {
                        inputStream = new BufferedInputStream(httpURLConnection.getInputStream());
                        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
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
            order_list.clear();
            Order tempOrder;
            JSONArray jArray = new JSONObject(returnString.toString()).getJSONArray("orders");
            Log.d("Count Tag ","Length "+jArray.length());
            for(int i=0;i<jArray.length();i++) {
                JSONObject post = jArray.getJSONObject(i);
                tempOrder=new Order();
                tempOrder.setOrderid(Integer.parseInt(post.getString("orderid")));
                tempOrder.setDate(post.getString("orderdate"));
                tempOrder.setTime(post.getString("ordertime"));
                tempOrder.setTotal(Integer.parseInt(post.getString("total")));
                tempOrder.setPayStatus(post.getInt("pay_status"));
                tempOrder.setProcessStatus(post.getInt("process_status"));
                order_list.add(tempOrder);
            }
        }catch (JSONException e) {
            Log.e("log_tag", "Error parsing data " + e.toString());
        }
    }

    private class DrawerItemClickListener implements ListView.OnItemClickListener {

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int pos, long id) {
            Intent i=new Intent(MyOrders.this,DisplayOrder.class);
            i.putExtra("orderid",order_list.get(pos).getOrderid()+"");
            i.putExtra("date",order_list.get(pos).getDate());
            i.putExtra("time",order_list.get(pos).getTime());
            startActivity(i);
            }

        }
    private void updateUI()
    {
        totalOrderItemCount = order_list.size();
        lv.setOnItemClickListener(new DrawerItemClickListener());
        custom_adpter = new custom_list_one(this, order_list);
        lv.setAdapter(custom_adpter);
    }
}
