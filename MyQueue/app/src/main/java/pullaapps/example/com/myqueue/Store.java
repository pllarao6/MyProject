package pullaapps.example.com.myqueue;

import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;

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
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import pullaapps.example.com.myqueue.network.ConnectionDetector;
import pullaapps.example.com.myqueue.network.HttpConnector;


public class Store extends BaseActivity implements ConnectionCallbacks,
        OnConnectionFailedListener, LocationListener {

    SessionManager session;

    private static final String TAG = Store.class.getSimpleName();

 /*Starting Location Variables*/

    private final static int PLAY_SERVICES_RESOLUTION_REQUEST = 1000;
    private final static int CONNECTION_FAILURE_RESOLUTION_REQUEST = 9000;
    private Location mLastLocation;
    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;
    private static int UPDATE_INTERVAL = 1000*20;
    private static int FATEST_INTERVAL = 1000*10;
    private static int DISPLACEMENT = 10;
    private double mLatitude,mLongitude;
    private String latitude;
    private String longitude;

/*Ending Location Variables*/

    private TransparentProgressDialog pd;
    private View inflatedView;

    private String result="";
    private ArrayAdapter<String> adp;
    private RelativeLayout rl;

    private List<String> spinnerValues;
    private List<String> spinnerSubs;
    private List<Integer> MerchantId;

    private ListView listView;
    private ArrayList<StoreDrawerItem> StoreItms;
    private final String addressURL = "http://myproject.byethost8.com/address.php";
    Bundle bundle;
    private SessionManager sessionManager;
    private String imageURL="http://myproject.byethost8.com/images/";
    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        inflatedView=getLayoutInflater().inflate(R.layout.activity_store, frameLayout);
        sessionManager=new SessionManager(getApplicationContext());
        setTitle(titles[1]);
        flag2=1;
        flag1=0;
        flag3=0;
        context=getApplicationContext();
        sessionManager.checkLogin();
        if (!sessionManager.isLoggedIn()) {
         finish();
        }
        rl = (RelativeLayout)findViewById(R.id.rl);
        spinnerValues = new ArrayList<String>();
        spinnerSubs=new ArrayList<String>();
        MerchantId=new ArrayList<Integer>();
        StoreItms = new ArrayList<StoreDrawerItem>();
        LocationManager lm = (LocationManager) getSystemService(LOCATION_SERVICE);
        /*if(!lm.isProviderEnabled(LocationManager.GPS_PROVIDER) ||
                !lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
            // Build the alert dialog
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Location Services Not Active");
            builder.setMessage("Please enable Location Services and GPS");
            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialogInterface, int i) {
                    // Show location settings when the user acknowledges the alert dialog
                    Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                    startActivity(intent);
                }
            });
            Dialog alertDialog = builder.create();
            alertDialog.setCanceledOnTouchOutside(false);
            alertDialog.show();
        }*/

        if (checkPlayServices()) {
            Log.d(TAG,"In CheckPlayServices Condition");
            buildGoogleApiClient();
            createLocationRequest();
        }
    }

    protected void onStart() {
        super.onStart();
        Log.d(TAG,"In Start");
        if(mGoogleApiClient!=null)
            mGoogleApiClient.connect();
        pd = new TransparentProgressDialog(this, R.drawable.spinner,"Retrieving Stores");
        if(!pd.isShowing())
            pd.show();
    }

    protected void onResume() {
        super.onResume();
        Log.d("Tag","In Resume");
        checkPlayServices();
        if(mGoogleApiClient.isConnected())
            startLocationUpdates();
    }

    protected void onPause() {
        super.onPause();
        Log.d("Tag","In Pause");
        if (pd.isShowing())
            pd.dismiss();
        stopLocationUpdates();
    }


    protected void onStop() {
        super.onStop();
        if (mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }
    }



    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API).build();
        Log.d(TAG,"In buildGoogleApiClient");
    }


    protected void createLocationRequest() {
        Log.d(TAG,"In createRequest");
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(UPDATE_INTERVAL);
        mLocationRequest.setFastestInterval(FATEST_INTERVAL);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        //mLocationRequest.setSmallestDisplacement(DISPLACEMENT);
    }


    private boolean checkPlayServices() {
        Log.d("Tag","In CheckPlayServices");
        int resultCode = GooglePlayServicesUtil
                .isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
                GooglePlayServicesUtil.getErrorDialog(resultCode, this,
                        PLAY_SERVICES_RESOLUTION_REQUEST).show();
            } else {
                Toast.makeText(getApplicationContext(),
                        "This device is not supported.", Toast.LENGTH_LONG)
                        .show();
                finish();
            }
            return false;
        }
        return true;
    }

    protected void startLocationUpdates()
    {
        Log.d(TAG,"In StartLocationUpdates");
        LocationServices.FusedLocationApi.requestLocationUpdates(
                mGoogleApiClient, mLocationRequest, this);

    }

    protected void stopLocationUpdates() {
        Log.d("Tag","IN StopLocationUpdates");
        LocationServices.FusedLocationApi.removeLocationUpdates(
                mGoogleApiClient, this);
    }

    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.i(TAG, "Connection failed: ConnectionResult.getErrorCode() = "
                + connectionResult.getErrorCode());
        if (connectionResult.hasResolution()) {
            try {
                // Start an Activity that tries to resolve the error
                connectionResult.startResolutionForResult(this, CONNECTION_FAILURE_RESOLUTION_REQUEST);
                /*
                 * Thrown if Google Play services canceled the original
                 * PendingIntent
                 */
            } catch (IntentSender.SendIntentException e) {
                // Log the error
                e.printStackTrace();
            }
        } else {
            /*
             * If no resolution is available, display a dialog to the
             * user with the error.
             */
            Log.i(TAG, "Location services connection failed with code " + connectionResult.getErrorCode());
        }
    }

    public void onConnected(Bundle arg0) {
        //Toast.makeText(getApplicationContext(),"In Connected",Toast.LENGTH_SHORT).show();
        if(mGoogleApiClient.isConnected()) {
            Log.d("Tag","in onConnected");
            startLocationUpdates();
        }
    }

    public void onConnectionSuspended(int arg0) {
        mGoogleApiClient.connect();
    }

    public void onLocationChanged(Location location) {
        Log.d("Tag","in OnlocationChanged");
        if (mGoogleApiClient.isConnected() && location != null) {
            mLastLocation = location;
            mLatitude = mLastLocation.getLatitude();
            mLongitude =mLastLocation.getLongitude();
            LatLng latLng = new LatLng(mLatitude, mLongitude);
            latitude = String.valueOf(mLatitude);
            longitude = String.valueOf(mLongitude);
            Log.d(TAG,"lat"+latitude+", lon"+longitude);
            //Toast.makeText(getApplicationContext(), "Store at" + latitude+" , "+ longitude ,Toast.LENGTH_LONG).show();
            if (latitude != null && longitude != null) {
                Date currentDate = new Date(location.getTime());
                Toast.makeText(getApplicationContext(), "New Time " + currentDate, Toast.LENGTH_LONG).show();
                new CustomHttpRequest().execute(addressURL);
                //stopLocationUpdates();
            }
        }
    }


    private class DrawerItemClickListener implements ListView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int pos, long id) {
            //Toast.makeText(getApplicationContext(), "Store " + spinnerValues.get(pos).toString(), Toast.LENGTH_LONG).show();
            getDetails(pos);
        }
    }


    public void getDetails(int pos)
    {
        Intent i=new Intent(getApplicationContext(),DisplayProducts.class);
        i.putExtra("KEY_MERCHANT_ADDRESS",spinnerSubs.get(pos));
        sessionManager.setMerchantInfo(String.valueOf(MerchantId.get(pos)),spinnerValues.get(pos));
        startActivity(i);
    }

    class CustomHttpRequest extends AsyncTask<String, Void, Integer> {

        private String responseString="";
        private HttpConnector httpConnector;
        private JSONObject toSend;
        int flag=0;
        protected void onPreExecute() {
        }

        protected Integer doInBackground(String... args) {
            try {
                toSend=new JSONObject();
                sessionManager.setInfo(latitude,longitude);
                toSend.put("KEY_LATITUDE",latitude);
                toSend.put("KEY_LONGITUDE",longitude);
                httpConnector=new HttpConnector(toSend,addressURL,"POST",context);
                flag=httpConnector.makeConnection();
                if(flag==1)
                {
                    responseString=httpConnector.convertInputStream();
                }
            } catch (Exception e) {
                Log.e("tag", e.toString());
            }
            return flag;
        }

        protected void onPostExecute(Integer res) {
            if(httpConnector!=null)
                httpConnector.close();
            if (pd.isShowing())
                pd.dismiss();
            if (res == 2) {
                Toast.makeText(getApplicationContext(), "No Internet Connection", Toast.LENGTH_SHORT).show();
            } else if (res == 1) {
                Log.e("response from server",responseString);
                parseResult(responseString);
                listView = (ListView) findViewById(R.id.listview);
                MyAdapter adapter = new MyAdapter(getApplicationContext(), StoreItms);
                listView.setAdapter(adapter);
                listView.setOnItemClickListener(new DrawerItemClickListener());
            } else {
                Toast.makeText(getApplicationContext(), "Failed to fetch data", Toast.LENGTH_SHORT).show();
                Log.e("TAG", "Failed to fetch data!");
            }
        }
    }

    private void parseResult(String returnString) {
        spinnerValues.clear();
        spinnerSubs.clear();
        MerchantId.clear();
        StoreItms.clear();
        try {
            JSONArray jArray = new JSONObject(returnString.toString()).getJSONArray("stores");
            for(int i=0;i<jArray.length();i++) {
                JSONObject post = jArray.getJSONObject(i).getJSONObject("store");
                spinnerValues.add(post.getString("StoreName"));
                spinnerSubs.add(post.getString("Address"));
                MerchantId.add(post.getInt("Id"));
            }
            for(int i=0;i<spinnerValues.size();i++)
            {
                StoreDrawerItem storesData = new StoreDrawerItem();
                storesData.setTitle(spinnerValues.get(i));
                storesData.setsubTitle(spinnerSubs.get(i));
                String imageString = spinnerValues.get(i).toLowerCase();
                String imageurl=imageURL+imageString+".png";
                storesData.setImageURL(imageurl);
                Log.e("imageurl",imageurl);
                StoreItms.add(storesData);
            }
        }catch (JSONException e) {
            Log.e("log_tag", "Error parsing data " + e.toString());
        }
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_store, menu);
        return super.onCreateOptionsMenu(menu);
    }

    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId()) {
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
        Intent i = new Intent(getApplicationContext(), MyCart.class);
        startActivity(i);
    }

    @Override
    public void onDestroy()
    {
        // Remove adapter reference from list
        listView.setAdapter(null);
        super.onDestroy();
    }
}