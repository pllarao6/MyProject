package pullaapps.example.com.myqueue;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.NavUtils;
import android.support.v4.app.TaskStackBuilder;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
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
import java.net.URLConnection;
import java.util.ArrayList;


public class DisplayItem extends FragmentActivity implements AdapterView.OnItemSelectedListener {

    public ImageView imageView;
    public TextView item_name;
    public TextView item_price;
    public TextView item_total_price;
    public ProgressBar progressBar;
    public Spinner spinner;
    private int baseprice;
    private Button btn_meal;
    private Button btn_proceed;
    final String[] qtyValues = {"1","2","3","4","5","6","7","8","9","10"};
    private int count;
    Bundle bundle;
    String Image_URL ="http://myproject.byethost8.com/images/";
    final String update_url="http://myproject.byethost8.com/Cart.php";
    String itemId;
    SessionManager sessionManager;
    String userid;
    GetXMLTask task;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_item);
        sessionManager=new SessionManager(this);
        Bundle extras=getIntent().getExtras();
        bundle=extras;
        userid=bundle.getString("CustomerId");
        count=1;
        imageView = (ImageView) findViewById(R.id.imageView);
        item_name=(TextView)findViewById(R.id.text_item_name);
        item_price=(TextView)findViewById(R.id.price_value);
        item_total_price=(TextView)findViewById(R.id.total_key);
        progressBar=(ProgressBar)findViewById(R.id.progressBar);
        ArrayAdapter<String> aa = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item, qtyValues);
        aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner=(Spinner)findViewById(R.id.spinner);
        spinner.setAdapter(aa);
        spinner.setOnItemSelectedListener(this);
        if(extras!=null)
        {
            Image_URL= Image_URL+extras.getString("ItemName")+".jpg";
            //Toast.makeText(getApplicationContext(), extras.getString("MerchantId") + " " + extras.getString("ItemName") + " " + extras.getInt("ItemPrice"), Toast.LENGTH_LONG).show();
            item_name.setText(extras.getString("ItemName"));
            item_price.setText(extras.getString("ItemPrice"));
            baseprice=Integer.parseInt(extras.getString("ItemPrice"));
            item_total_price.setText(extras.getString("ItemPrice"));
            itemId=extras.getString("ItemId");
            task=new GetXMLTask();
            task.execute(new String[] { Image_URL});
        }
        getActionBar().setDisplayHomeAsUpEnabled(true);

        btn_meal=(Button)findViewById(R.id.button_meal);
        btn_meal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                task.cancel(true);
                ArrayList<String> args=new ArrayList<String>();
                args.add(update_url);
                args.add(String.valueOf(itemId));
                args.add(String.valueOf(count));
                args.add(item_total_price.getText().toString());
                args.add(bundle.getString("Key_MerchantId"));
                args.add(userid);
                new CustomHttpRequest().execute(args);
            }
        });

    }

    public void onItemSelected(AdapterView<?> parent, View view, int position,
                               long id) {
        item_total_price.setText(String.valueOf(baseprice*Integer.parseInt(qtyValues[position])));
        count=Integer.parseInt(qtyValues[position]);
        //Toast.makeText(getApplicationContext(),item_total_price.getText(),Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onNothingSelected(AdapterView<?> arg0) {
        // TODO Auto-generated method stub

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

    private class GetXMLTask extends AsyncTask<String, Void, Bitmap> {

        protected void onPreExecute()
        {
            progressBar.setVisibility(View.VISIBLE);
        }
        @Override
        protected Bitmap doInBackground(String... urls) {
            Bitmap map = null;
            try {
                URL url=new URL(urls[0]);
                HttpURLConnection conn=(HttpURLConnection)url.openConnection();
                conn.setReadTimeout(5000);
                if(conn.getResponseCode()==200)
                map = BitmapFactory.decodeStream(conn.getInputStream());
                Log.e("first","fuck");

            } catch (Exception e) {
                e.printStackTrace();
            }
            return map;
        }

        // Sets the Bitmap returned by doInBackground
        @Override
        protected void onPostExecute(Bitmap result) {
            progressBar.setVisibility(View.INVISIBLE);
            if(result!=null)
            imageView.setImageBitmap(result);
            else
                Toast.makeText(DisplayItem.this, "Image Does Not exist or Network Error", Toast.LENGTH_SHORT).show();
        }

        // Creates Bitmap from InputStream and returns it
        private Bitmap downloadImage(String url) {
            Bitmap bitmap = null;
            InputStream stream = null;
            BitmapFactory.Options bmOptions = new BitmapFactory.Options();
            bmOptions.inSampleSize = 1;
            try {
                stream = getHttpConnection(url);
                bitmap = BitmapFactory.
                        decodeStream(stream, null, bmOptions);
                stream.close();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
            return bitmap;
        }

        // Makes HttpURLConnection and returns InputStream
        private InputStream getHttpConnection(String urlString)
                throws IOException {
            InputStream stream = null;
            URL url = new URL(urlString);
            URLConnection connection = url.openConnection();

            try {
                HttpURLConnection httpConnection = (HttpURLConnection) connection;
                httpConnection.setRequestMethod("GET");
                httpConnection.connect();

                if (httpConnection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                    stream = httpConnection.getInputStream();
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            return stream;
        }
    }

    private void showCart() {
        Intent i = new Intent(this, MyCart.class);
        startActivity(i);
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_display_item, menu);
        return super.onCreateOptionsMenu(menu);
    }

    public class CustomHttpRequest extends AsyncTask<ArrayList<String>, Void, Integer> {

        Integer result = 0;
        String responseString;

        protected void onPreExecute() {
        }

        protected Integer doInBackground(ArrayList<String> ...args)
        {
            InputStream inputStream;
            try {
                ConnectivityManager cm =
                        (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);

                NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
                boolean isConnected = activeNetwork != null &&
                        activeNetwork.isConnectedOrConnecting();
                if (isConnected) {
                    Log.e("Tag", "In doBackground()");
                    URL url = new URL(args[0].get(0));
                    HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                    //httpURLConnection.setConnectTimeout(1000);
                    //httpURLConnection.setReadTimeout(2000);
                    httpURLConnection.setDoOutput(true);
                    httpURLConnection.setRequestMethod("POST");
                    String param = "ItemId="+Integer.parseInt(args[0].get(1))+"&Quant="+Integer.parseInt(args[0].get(2))+
                            "&MerchantId="+Integer.parseInt(args[0].get(4))+"&CustomerId="+Integer.parseInt(args[0].get(5))
                            +"&TotalItemPrice="+Integer.parseInt(args[0].get(3));
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
                        //parseResult(responseString);
                        result = 1;
                    } else {
                        result = 0;
                    }
                } else {
                    result = 2;
                }
            } catch (Exception e) {
                Log.e("tag", e.toString());
            }
            return result;
        }

        protected void onPostExecute(Integer res) {
            if (res == 2) {
                Toast.makeText(getApplicationContext(), "No Internet Connection", Toast.LENGTH_SHORT).show();
            } else if (res == 1) {
                Toast.makeText(getApplicationContext(),"Added to meal",Toast.LENGTH_SHORT).show();
                Intent intent=new Intent(getApplicationContext(),DisplayProducts.class);
                startActivity(intent);
                finish();
            } else {
                Log.e("TAG", "Failed to fetch data!");
            }
        }
    }

    private String convertInputStreamToString(InputStream inputStream) throws IOException {

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
            JSONArray jArray = new JSONObject(returnString.toString()).getJSONArray("stores");
            Log.d("Count Tag ","Length "+jArray.length());
            for(int i=0;i<jArray.length();i++) {
                JSONObject post = jArray.getJSONObject(i).getJSONObject("store");
            }
        }catch (JSONException e) {
            Log.e("log_tag", "Error parsing data " + e.toString());
        }
    }
}
