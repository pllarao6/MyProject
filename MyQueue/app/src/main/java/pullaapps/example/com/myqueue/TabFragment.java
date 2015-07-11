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

import pullaapps.example.com.myqueue.network.HttpConnector;


public class TabFragment extends Fragment {

    private String diet_name;
	Activity activity;
	List<String> itemNames;
    List<Integer> itemId;
	List<Integer> itemPrices;
	ArrayList<MealDrawerItem> mealItems;
    ListView listView;
    private View view;
    private String merchant_id;
    private String merchant_name;
    private Communicator communicator;
    public String TAG="Fragment_State";
    private String Lat;
    private String Lon;
    private SessionManager sessionManager;
    private MealAdapter mealAdapter;
    private HashMap<String, String> location;
    private HashMap<String,String> merchant;
    private final String mealURL = "http://myproject.byethost8.com/RetrieveItem.php";
    private Context context;

    @Override
    public void onAttach(Activity activity)
	{
		super.onAttach(activity);
        if (activity instanceof Communicator) {
            communicator = (Communicator) activity;
        } else {
            throw new ClassCastException(activity.toString()
                    + " must implement MyListFragment.Communicator");
        }
        context=activity.getApplicationContext();
	}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity=getActivity();
        sessionManager=new SessionManager(activity);
        Bundle bundle = getArguments();
        diet_name = bundle.getString("KEY_DIET");
        location=sessionManager.getLocationInfo();
        Lat=location.get("latitude");
        Lon=location.get("longitude");
        merchant=sessionManager.getMerchantInfo();
        merchant_id=merchant.get("merchant_id");
        merchant_name=merchant.get("merchant_name");
        Log.e("Asynctask","increate"+" "+diet_name);
        itemNames=new ArrayList<String>();
        itemPrices=new ArrayList<Integer>();
        itemId=new ArrayList<Integer>();
        mealItems=new ArrayList<MealDrawerItem>();
        new CustomHttpRequest().execute(mealURL);
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

     class CustomHttpRequest extends AsyncTask<String, Void, Integer> {

        private Integer result=0;
        private String responseString;
        private TransparentProgressDialog pDialog;
        private HttpConnector httpConnector;
        private JSONObject toSend;

        protected void onPreExecute() {
            pDialog = new TransparentProgressDialog(getActivity(),R.drawable.spinner,"Retrieving Yummy Meals..");
            pDialog.show();
        }

        protected Integer doInBackground(String... args) {
            try {
                toSend = new JSONObject();
                toSend.put("KEY_MERCHANT", merchant_name);
                toSend.put("KEY_DIET", diet_name);
                httpConnector = new HttpConnector(toSend, mealURL, "POST", context);
                result=httpConnector.makeConnection();
                if(result==1)
                {
                    responseString=httpConnector.convertInputStream();
                }
            }catch (Exception e)
            {
                e.printStackTrace();
            }
            return result;
        }

        protected void onPostExecute(Integer res) {
            if(httpConnector!=null)
                httpConnector.close();

            if (pDialog.isShowing())
                pDialog.dismiss();

            if(res==2)
            {
                Toast.makeText(activity,"No Internet Connection",Toast.LENGTH_SHORT).show();
            }

            else if (res == 1) {
                    parseResult(responseString);
                    mealItems.clear();
                    listView=(ListView)view.findViewById(R.id.listView);
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
                }
            else {
                Log.e("TAG", "Failed to fetch data!");
            }
        }
    }

    private void parseResult(String returnString) {
        try {
            itemNames.clear();
            itemPrices.clear();
            itemId.clear();
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
        listView.setAdapter(null);
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

}
