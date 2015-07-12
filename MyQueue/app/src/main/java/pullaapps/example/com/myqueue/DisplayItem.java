package pullaapps.example.com.myqueue;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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

import org.json.JSONObject;
import java.net.HttpURLConnection;
import java.net.URL;

import pullaapps.example.com.myqueue.network.HttpConnector;


public class DisplayItem extends FragmentActivity implements AdapterView.OnItemSelectedListener {

    private ImageView imageView;
    private TextView item_name;
    private TextView item_price;
    private TextView item_total_price;
    private ProgressBar progressBar;
    private Spinner spinner;
    private int baseprice;
    private Button btn_meal;
    final String[] qtyValues = {"1","2","3","4","5","6","7","8","9","10"};
    private int quantity;
    private Bundle bundle;
    String imageURL ="http://myproject.byethost8.com/images/";
    final String updateCartURL="http://myproject.byethost8.com/Cart.php";
    private String itemId;
    private SessionManager sessionManager;
    private String userid;
    GetXMLTask task;
    private ArrayAdapter<String> aa;
    private Context context;
    private ImageLoader imageLoader;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_item);
        sessionManager=new SessionManager(this);
        context=getApplicationContext();
        bundle=getIntent().getExtras();
        userid=bundle.getString("CustomerId");
        quantity=1;
        imageView = (ImageView) findViewById(R.id.imageView);
        item_name=(TextView)findViewById(R.id.text_item_name);
        item_price=(TextView)findViewById(R.id.price_value);
        item_total_price=(TextView)findViewById(R.id.total_key);
        progressBar=(ProgressBar)findViewById(R.id.progressBar);
        aa = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item, qtyValues);
        aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner=(Spinner)findViewById(R.id.spinner);
        spinner.setAdapter(aa);
        spinner.setOnItemSelectedListener(this);
        imageLoader=new ImageLoader(context);

        if(bundle!=null)
        {
            imageURL= imageURL + bundle.getString("ItemName")+".jpg";
            //Toast.makeText(getApplicationContext(), extras.getString("MerchantId") + " " + extras.getString("ItemName") + " " + extras.getInt("ItemPrice"), Toast.LENGTH_LONG).show();
            item_name.setText(bundle.getString("ItemName"));
            item_price.setText(bundle.getString("ItemPrice"));
            baseprice=Integer.parseInt(bundle.getString("ItemPrice"));
            item_total_price.setText(bundle.getString("ItemPrice"));
            itemId=bundle.getString("ItemId");
            //task=new GetXMLTask();
            //task.execute(new String[] { Image_URL});
            imageLoader.DisplayImage(imageURL,imageView);
        }

        getActionBar().setDisplayHomeAsUpEnabled(true);
        btn_meal=(Button)findViewById(R.id.button_meal);
        btn_meal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                JSONObject toSend = null;
                try {
                    toSend = new JSONObject();
                    toSend.put("ItemId", Integer.parseInt(itemId));
                    toSend.put("Quant", quantity);
                    toSend.put("MerchantId", Integer.parseInt(bundle.getString("Key_MerchantId")));
                    toSend.put("CustomerId", Integer.parseInt(userid));
                   } catch (Exception e) {
                    e.printStackTrace();
                }
                new CustomHttpRequest().execute(toSend);
            }
        });
    }

    public void onItemSelected(AdapterView<?> parent, View view, int position,
                               long id) {
        item_total_price.setText(String.valueOf(baseprice*Integer.parseInt(qtyValues[position])));
        quantity=Integer.parseInt(qtyValues[position]);
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

     private class CustomHttpRequest extends AsyncTask<JSONObject, Void, Integer> {

        private Integer result = 0;
        private String responseString;
        private HttpConnector httpConnector;
        protected void onPreExecute() {
        }

        protected Integer doInBackground(JSONObject... args)
        {
            try {
                    httpConnector=new HttpConnector(args[0],updateCartURL,"POST",context);
                    result=httpConnector.makeConnection();
                    if(result==1)
                    {
                        responseString=httpConnector.convertInputStream();
                        Log.e("response",responseString);
                    }
            } catch (Exception e) {
                Log.e("tag", e.toString());
            }
            return result;
        }

        protected void onPostExecute(Integer res) {
            Boolean flag=parseResult(responseString);
            if(res == 2) {
                Toast.makeText(getApplicationContext(), "No Internet Connection", Toast.LENGTH_SHORT).show();
            } else if (res == 1) {
                if (flag==true) {
                    Toast.makeText(getApplicationContext(), "Added to meal", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(getApplicationContext(), DisplayProducts.class);
                    startActivity(intent);
                    finish();
                }else {
                    Toast.makeText(getApplicationContext(), "Failed to add to meal...", Toast.LENGTH_SHORT).show();
                }
            } else {
                Log.e("TAG", "Failed to fetch data!");
            }
        }
    }

    private Boolean parseResult(String response)
    {
        Boolean flag=false;
        try {
            JSONObject jsonObject=new JSONObject(response);
            flag=jsonObject.getBoolean("re");
        }catch (Exception e)
        {
            e.printStackTrace();
        }
        return flag;
    }
}
