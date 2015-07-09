package pullaapps.example.com.myqueue;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.gcm.GoogleCloudMessaging;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import pullaapps.example.com.myqueue.network.ConnectionDetector;
import pullaapps.example.com.myqueue.network.HttpConnector;


public class SignUp extends Activity {

    //StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
    String returnString;
    String result = null;
    String result1 = null;
    GoogleCloudMessaging gcm;
    private Context context;
    private String Name;
    private String Email;
    private String Password;
    private String Mobile;
    private String regId;
    public static final String REG_ID = "regId";
    private static final String APP_VERSION = "appVersion";
    private Boolean isInternetPresent;
    private Button btnSignup;
    private final String signupURL = "http://myproject.byethost8.com/signup_user.php";
    private JSONObject toSend;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        ConnectionDetector cd = new ConnectionDetector(getApplicationContext());
        context = getApplicationContext();
        isInternetPresent = cd.isConnectingToInternet(); // true or false
        //StrictMode.setThreadPolicy(policy);
        if (!isInternetPresent)
            Toast.makeText(getApplicationContext(), "No Internet Connection", Toast.LENGTH_SHORT).show();

        btnSignup = (Button) findViewById(R.id.signup);
        btnSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ConnectionDetector cd = new ConnectionDetector(getApplicationContext());

                isInternetPresent = cd.isConnectingToInternet(); // true or false

                if (isInternetPresent) {
                    EditText txtName = (EditText) findViewById(R.id.name_txt);
                    Name = txtName.getText().toString();

                    EditText txtMail = (EditText) findViewById(R.id.email);
                    Email = txtMail.getText().toString();

                    EditText txtPassword = (EditText) findViewById(R.id.password);
                    Password = txtPassword.getText().toString();

                    EditText txtRePassword = (EditText) findViewById(R.id.repassword);
                    String RePassword = txtRePassword.getText().toString();

                    EditText txtMobile = (EditText) findViewById(R.id.mobile);
                    Mobile = txtMobile.getText().toString();

                    if (Name.trim().length() > 0 && Email.trim().length() > 0 && Password.trim().length() > 0 && Mobile.trim().length() > 0 && RePassword.trim().length() > 0) {
                        if (Password.trim().equals(RePassword.trim())) {
                            try {
                                toSend = new JSONObject();
                                toSend.put("KEY_USER", Name);
                                toSend.put("KEY_EMAIL", Email);
                                toSend.put("KEY_PASSWORD", Password);
                                toSend.put("KEY_MOBILE", Mobile);
                                registerGCM();
                            } catch (Exception e) {
                                Log.e("log_tag", "Error in http connection!!" + e.toString());
                            }
                        } else {
                            Toast.makeText(getApplicationContext(), "Passwords not matched", Toast.LENGTH_LONG).show();
                        }
                    } else {
                        Toast.makeText(getApplicationContext(), "Fields are Empty", Toast.LENGTH_LONG).show();
                    }
                } else {
                    Toast.makeText(getApplicationContext(), "No Internet Connection" + "\n" + "Try Again", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void registerGCM() {

        gcm = GoogleCloudMessaging.getInstance(this);
        regId = getRegistrationId(context);

        if (TextUtils.isEmpty(regId)) {

            registerInBackground();

            Log.d("RegisterActivity",
                    "registerGCM - successfully registered with GCM server - regId: "
                            + regId);
        }
        else
        {
            try {
                toSend.put("KEY_REGID",regId);
                new SignUpBackground().execute(toSend);
            }catch (Exception e)
            {
                e.printStackTrace();
            }
        }
    }

    private void registerInBackground() {
        new AsyncTask<String, Void, String>() {

            protected String doInBackground(String... params) {
                String msg = "";
                try {
                    if (gcm == null) {
                        gcm = GoogleCloudMessaging.getInstance(context);
                    }
                    regId = gcm.register(Config.GOOGLE_PROJECT_ID);
                    Log.d("RegisterActivity", "registerInBackground - regId: "
                            + regId);
                    msg = "Device registered, registration ID=" + regId;

                    storeRegistrationId(context, regId);
                } catch (IOException ex) {
                    msg = "Error :" + ex.getMessage();
                    Log.d("RegisterActivity", "Error: " + msg);
                }
                return msg;
            }

            @Override
            protected void onPostExecute(String msg) {
                try {
                    toSend.put("KEY_REGID",regId);
                    new SignUpBackground().execute(toSend);
                }catch (Exception e)
                {
                    e.printStackTrace();
                }
            }
        }.execute(null,null,null);
    }

    private String getRegistrationId(Context context) {
        final SharedPreferences prefs = getSharedPreferences(
                getResources().getString(R.string.mysharedpreference), Context.MODE_PRIVATE);
        String registrationId = prefs.getString(REG_ID, "");
        if (registrationId.isEmpty()) {
            Log.i("TAG", "Registration not found.");
            return "";
        }
        int registeredVersion = prefs.getInt(APP_VERSION, Integer.MIN_VALUE);
        int currentVersion = getAppVersion(context);
        if (registeredVersion != currentVersion) {
            Log.i("TAG", "App version changed.");
            return "";
        }
        return registrationId;
    }

    private static int getAppVersion(Context context) {
        try {
            PackageInfo packageInfo = context.getPackageManager()
                    .getPackageInfo(context.getPackageName(), 0);
            return packageInfo.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            Log.d("RegisterActivity",
                    "I never expected this! Going down, going down!" + e);
            throw new RuntimeException(e);
        }
    }


    private void storeRegistrationId(Context context, String regId) {
        final SharedPreferences prefs = getSharedPreferences(
                getResources().getString(R.string.mysharedpreference), Context.MODE_PRIVATE);
        int appVersion = getAppVersion(context);
        Log.i("TAG", "Saving regId on app version " + appVersion);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(REG_ID, regId);
        editor.putInt(APP_VERSION, appVersion);
        editor.commit();
    }

    class SignUpBackground extends AsyncTask<JSONObject, Void, Integer> {

        private ProgressDialog progressDialog;
        private HttpConnector httpConnector;
        private String response;
        int res;
        @Override
        protected void onPreExecute()
        {
            progressDialog=new ProgressDialog(SignUp.this);
            progressDialog.setMessage("Processing");
            progressDialog.show();
        }

        @Override
        protected Integer doInBackground(JSONObject... params) {
                Log.d("request",params[0].toString());
                httpConnector=new HttpConnector(params[0],signupURL,"POST",getApplicationContext());
                res=httpConnector.makeConnection();
            return res;
        }

        @Override
        protected void onPostExecute(Integer res) {
            if(progressDialog.isShowing())
                progressDialog.dismiss();
            if (res == 1) {
                try {
                    response = httpConnector.convertInputStream();
                    parseResult(response);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else if (res == 2) {
                Toast.makeText(getApplicationContext(), "Check Your Internet Connection", Toast.LENGTH_LONG).show();
            }
            else
            {
                Toast.makeText(getApplicationContext(), "Error while processing", Toast.LENGTH_LONG).show();
            }
        }
        }

    public void parseResult(String response)
    {
        try {
            returnString = "";
            JSONObject jObject = new JSONObject(response);
            Log.i("Log Tag", "Result:" + jObject.getString("re"));
            returnString += jObject.getString("re");
            //Toast.makeText(getApplicationContext(),"Pass"+returnString, Toast.LENGTH_SHORT).show();
            if (returnString.equals("Record inserted successfully"))
            {
                Toast.makeText(getApplicationContext(), "Success.Welcome to our family", Toast.LENGTH_LONG).show();
                Intent i=new Intent(context,LoginActivity.class);
                startActivity(i);
                finish();
            } else if (returnString.equals("Record is repeated")) {
                Toast.makeText(getApplicationContext(), "Account Already Exists", Toast.LENGTH_LONG).show();
            }
            else if(returnString.equals("Inserting problem in database"))
            {
                Toast.makeText(getApplicationContext(), "Inserting Problem in Database", Toast.LENGTH_LONG).show();
            }
        } catch (JSONException e) {
            Log.e("log_tag", "Error parsing data " + e.toString());
        }
    }
}