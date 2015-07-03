package pullaapps.example.com.myqueue;


import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
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
import java.util.List;


public class TabFragment extends Fragment {

    String name;
	Activity activity;
	List<String> itemNames;
    List<Integer> itemId;
	List<Integer> itemPrices;
	ArrayList<MealDrawerItem> mealItems;
    ListView listView;
    View view;
    String merchant_id;
    String merchant_name;
    private Communicator communicator;
    public String TAG="Fragment_State";
    String Lat;
    String Lon;
    SessionManager sessionManager;
    public MealAdapter mealAdapter;
	public void onAttach(Activity activity)
	{
		super.onAttach(activity);
        if (activity instanceof Communicator) {
            communicator = (Communicator) activity;
        } else {
            throw new ClassCastException(activity.toString()
                    + " must implemenet MyListFragment.Communicator");
        }
	}
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity=getActivity();
        sessionManager=new SessionManager(activity);
        Bundle bundle = getArguments();
        name = bundle.getString("name");
        HashMap<String, String> location;
        location=sessionManager.getLocationInfo();
        Lat=location.get("latitude");
        Lon=location.get("longitude");
        HashMap<String,String> merchant;
        merchant=sessionManager.getMerchantInfo();
        merchant_id=merchant.get("merchant_id");
        merchant_name=merchant.get("merchant_name");
        final String url = "http://myproject.byethost8.com/RetrieveItem.php";
        Log.e("Asynctask","increate"+" "+name);
        new CustomHttpRequest().execute(url);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                                 Bundle savedInstanceState) {
        view=inflater.inflate(R.layout.fragment_tab, container, false);
        return view;
    }

    public void onActivityCreated(Bundle savedInstanceState)
	{
		super.onActivityCreated(savedInstanceState);
	}

	public void onStart()
	{
		super.onStart();
	}
	
	public void onResume()
	{
		super.onResume();
	}


    public interface Communicator {
        public void Message(Bundle args);
        public void setMealAdapter(MealAdapter adapter);
    }

	private class DrawerItemClickListener implements ListView.OnItemClickListener {

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int pos, long id) {
            //Toast.makeText(activity,itemNames.get(pos),Toast.LENGTH_SHORT).show();
            Bundle args=new Bundle();
            args.putString("ItemId",String.valueOf(itemId.get(pos).intValue()));
            args.putString("ItemName", itemNames.get(pos));
            args.putString("ItemPrice", String.valueOf(itemPrices.get(pos).intValue()));
            args.putString("CustomerId",sessionManager.getUserId());
            args.putString("Key_MerchantId",merchant_id);
            args.putString("Key_Lat",Lat);
            args.putString("Key_Lon",Lon);
            communicator.Message(args);
        }

    }

    public class CustomHttpRequest extends AsyncTask<String, Void, Integer> {

        Integer result=0;
        String responseString;
        private TransparentProgressDialog pDialog;
        protected void onPreExecute() {
            pDialog = new TransparentProgressDialog(getActivity(),R.drawable.spinner,"Retrieving Yummy Meals..");
            pDialog.show();
        }

        protected Integer doInBackground(String... args) {

            InputStream inputStream;
            try {
                ConnectivityManager cm =
                        (ConnectivityManager) activity.getSystemService(Context.CONNECTIVITY_SERVICE);

                NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
                boolean isConnected = activeNetwork != null &&
                        activeNetwork.isConnectedOrConnecting();
                if (isConnected) {
                    Log.d("Tag","In doBackground()");
                    URL url = new URL("http://myproject.byethost8.com/RetrieveItem.php");
                    HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                    httpURLConnection.setDoOutput(true);
                    httpURLConnection.setRequestMethod("POST");
                    String param = "KEY_MERCHANT=" + merchant_name + "&KEY_DIET=" + name;
                    httpURLConnection.setFixedLengthStreamingMode(param.getBytes().length);
                    PrintWriter out = new PrintWriter(httpURLConnection.getOutputStream());
                    out.print(param);
                    out.flush();
                    out.close();
                    int statusCode = httpURLConnection.getResponseCode();
                    if (statusCode == 200) {
                        inputStream = new BufferedInputStream(httpURLConnection.getInputStream());
                        responseString = convertInputStreamToString(inputStream);
                        Log.d("tag", responseString);
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

        protected void onPostExecute(Integer res) {
            if (pDialog.isShowing())
                pDialog.dismiss();
            if(res==2)
            {
                Toast.makeText(activity,"No Internet Connection",Toast.LENGTH_SHORT).show();
            }
            else if (res == 1) {
                    listView=(ListView)view.findViewById(R.id.listView);
                    mealItems=new ArrayList<MealDrawerItem>();
                    for(int i=0;i<itemNames.size();i++)
                    {
                        mealItems.add(new MealDrawerItem(itemNames.get(i),itemPrices.get(i).intValue()));
                    }
                    mealAdapter = new MealAdapter(activity, mealItems);
                    listView.setAdapter(mealAdapter);
                    listView.setTextFilterEnabled(true);
                    listView.setOnItemClickListener(new DrawerItemClickListener());
					Log.e("tag_check","After Setting Adapter");
                    communicator.setMealAdapter(mealAdapter);
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
            itemNames=new ArrayList<String>();
            itemPrices=new ArrayList<Integer>();
            itemId=new ArrayList<Integer>();
            JSONArray jArray = new JSONObject(returnString).getJSONArray("dishes");
            Log.d("Count Tag ", "Length " + jArray.length());
            for (int i = 0; i < jArray.length(); i++) {
                JSONObject post = jArray.getJSONObject(i).getJSONObject("dish");
                Log.i("TAG", post.getString("Name"));
                itemId.add(new Integer(post.getInt("Id")));
                itemNames.add(post.getString("Name"));
                itemPrices.add(new Integer(post.getInt("Price")));
            }
        } catch (JSONException e) {
            Log.e("log_tag", "Error parsing data " + e.toString());
        }
    }

    public void onDestroy()
    {
        super.onDestroy();
        mealItems.clear();
    }

    public void onDetach()
    {
        super.onDetach();
        Log.e("Tag","In Fragment Detach()");
    }

   public MealAdapter getMealAdapter()
   {
       return mealAdapter;
   }
   
   public int getnum()
   {
	   return 2;
   }
}
