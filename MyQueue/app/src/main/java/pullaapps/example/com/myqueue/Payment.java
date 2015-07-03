package pullaapps.example.com.myqueue;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.RelativeLayout;
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


public class Payment extends BaseActivity1 {

    public View inflatedView;
    public int totalCartValue;
    public TextView textView_total;
    public TextView credit_txt;
    public TextView debit_txt;
    public Button btn_credit;
    public Button btn_debit;
    public RelativeLayout credit_layout;
    public RelativeLayout debit_layout;
    NumberPicker np_month;
    NumberPicker np_year;
    EditText editText_credit_number;
    JSONObject toSend;
    int userid;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        inflatedView=getLayoutInflater().inflate(R.layout.activity_payment, frameLayout);
        Intent i=getIntent();
        ArrayList<Meal> cart_list=(ArrayList<Meal>)i.getSerializableExtra("CartList");
        getActionBar().setDisplayHomeAsUpEnabled(true);
        SessionManager sessionManager=new SessionManager(this);
        userid=Integer.parseInt(sessionManager.getUserId());
        try {
            toSend = new JSONObject();
            toSend.put("msg", "hello");
            toSend.put("CustomerId",userid);
            JSONArray list=new JSONArray();
            JSONObject temp;
        for (int temp1 = 0; temp1 < cart_list.size(); temp1++) {
            totalCartValue = totalCartValue + cart_list.get(temp1).getItemTotalPrice();
            temp=new JSONObject();
            temp.put("MerchantId",cart_list.get(temp1).getMerchantId());
            temp.put("ItemId",cart_list.get(temp1).getID());
            temp.put("ItemName",cart_list.get(temp1).getItemName());
            temp.put("Quantity",cart_list.get(temp1).getQuantity());
            temp.put("ItemPrice",cart_list.get(temp1).getItemPrice());
            temp.put("ItemTotalPrice",cart_list.get(temp1).getItemTotalPrice());
            temp.put("StoreName",cart_list.get(temp1).getStoreName());
            list.put(temp);
        }
            toSend.put("Items",list);
            Log.d("Json Output",toSend.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        textView_total=(TextView)findViewById(R.id.total_amount);
        textView_total.setText("Rs "+String.valueOf(totalCartValue));
        credit_txt=(TextView)findViewById(R.id.credit_card);
        debit_txt=(TextView)findViewById(R.id.debit_card);
        btn_credit=(Button)findViewById(R.id.btn_credit_card);
        btn_debit=(Button)findViewById(R.id.btn_debit_card);
        editText_credit_number=(EditText)findViewById(R.id.card_number);
        credit_layout=(RelativeLayout)findViewById(R.id.layout_credit_card);
        debit_layout=(RelativeLayout)findViewById(R.id.layout_debit_card);
        np_month = (NumberPicker) findViewById(R.id.monthPicker);
        np_month.setMinValue(1);
        np_month.setMaxValue(12);
        np_month.setWrapSelectorWheel(false);
        np_month.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {

            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                // TODO Auto-generated method stub

                String Old = "Old Value : ";
                String New = "New Value : ";
                Toast.makeText(getApplicationContext(),New+newVal,Toast.LENGTH_SHORT).show();
            }
        });
        np_year = (NumberPicker) findViewById(R.id.yearPicker);
        np_year.setMinValue(2015);
        np_year.setMaxValue(2030);
        np_year.setWrapSelectorWheel(false);
        np_year.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {

            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                // TODO Auto-generated method stub

                String Old = "Old Value : ";
                String New = "New Value : ";
                Toast.makeText(getApplicationContext(),New+newVal,Toast.LENGTH_SHORT).show();
            }
        });
        credit_txt.setOnClickListener(new View.OnClickListener() {
            @Override

            public void onClick(View v) {
                if(credit_layout.getVisibility()==View.GONE)
                {
                    credit_layout.setVisibility(View.VISIBLE);
                    editText_credit_number.requestFocus();
                }
                else
                {
                    credit_layout.setVisibility(View.GONE);
                }
                if(debit_layout.getVisibility()==View.VISIBLE)
                    debit_layout.setVisibility(View.GONE);
            }
        });

        btn_credit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(),"Redirecting To Payment Gateway for Credit Card",Toast.LENGTH_SHORT).show();
                CustomHttpConnector customHttpConnector=new CustomHttpConnector();
                customHttpConnector.execute(new JSONObject[] {toSend});
            }
        });

        debit_txt.setOnClickListener(new View.OnClickListener() {
            @Override

            public void onClick(View v) {
                if(debit_layout.getVisibility()==View.GONE)
                {
                    debit_layout.setVisibility(View.VISIBLE);
                }
                else
                {
                    debit_layout.setVisibility(View.GONE);
                }
                if(credit_layout.getVisibility()==View.VISIBLE) {
                    credit_layout.setVisibility(View.GONE);
                }
            }
        });

        btn_debit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(),"Redirecting To Payment Gateway for Debit Card",Toast.LENGTH_SHORT).show();
            }
        });
    }

    public class CustomHttpConnector extends AsyncTask<JSONObject,Void,Integer>
    {
        InputStream inputStream;
        String responseString;
        int result;
        TransparentProgressDialog pDialog;
        protected void onPreExecute()
        {
            pDialog = new TransparentProgressDialog(Payment.this,R.drawable.spinner,"Processing..");
            pDialog.show();
        }
        public Integer  doInBackground(JSONObject... data)
        {
            try {
                JSONObject json=data[0];
                java.net.URL url = new URL("http://myproject.byethost8.com/UpdateCartFinal.php");
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setDoOutput(true);
                httpURLConnection.setRequestMethod("POST");
                String param = null;
                param="json="+json.toString();
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
            }catch(Exception e)
            {

            }
            return result;
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
        private void parseResult(String returnString)
        {
        /*JSONObject jsonObject;
        try {
            jsonObject=new JSONObject(returnString);
            Log.d("Tag_Mssg",jsonObject.getString("msg"));
         }
         catch (JSONException e) {
            Log.e("log_tag", "Error parsing data " + e.toString());
        }*/
        }

        public void onPostExecute(Integer x)
        {
            if (pDialog.isShowing())
                pDialog.dismiss();
            Log.d("Tag_res",x.intValue()+"");
            Intent i=new Intent(Payment.this,DisplayOrder.class);
            Log.e("PayTagggy",responseString);
            try {
                JSONObject jsonObject = new JSONObject(responseString);
                String orderid=String.valueOf(jsonObject.getInt("orderId"));
                String date=jsonObject.getString("orderDate");
                String time=jsonObject.getString("orderTime");
                i.putExtra("orderid",orderid);
                i.putExtra("date",date);
                i.putExtra("time",time);
            }catch (Exception e)
            {
                Log.e("Error in Json",e.toString());
            }
            startActivity(i);
        }
    }
}
