package pullaapps.example.com.myqueue;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.gcm.GoogleCloudMessaging;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;


public class SignUp extends Activity {

    StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
    String returnString;
    String result = null;
    String result1=null;
    GoogleCloudMessaging gcm;
    Context context;
    String regId;
    String Name;
    String Email;
    String Password;
    String Mobile;
    public static final String REG_ID = "regId";
    private static final String APP_VERSION = "appVersion";

    private Button btnSignup;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        ConnectionDetector cd = new ConnectionDetector(getApplicationContext());
        context = getApplicationContext();
        Boolean isInternetPresent = cd.isConnectingToInternet(); // true or false
        StrictMode.setThreadPolicy(policy);
        if(!isInternetPresent)
            Toast.makeText(getApplicationContext(),"No Internet Connection",Toast.LENGTH_SHORT).show();

        btnSignup=(Button)findViewById(R.id.signup);
        btnSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ConnectionDetector cd = new ConnectionDetector(getApplicationContext());

                Boolean isInternetPresent = cd.isConnectingToInternet(); // true or false

                if(isInternetPresent) {
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
                            ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
                            nameValuePairs.add(new BasicNameValuePair("KEY_USER", Name));
                            nameValuePairs.add(new BasicNameValuePair("KEY_EMAIL", Email));
                            nameValuePairs.add(new BasicNameValuePair("KEY_PASSWORD", Password));
                            nameValuePairs.add(new BasicNameValuePair("KEY_MOBILE", Mobile));

                            try {
                                result = CustomHttpClient.executeHttpPost("http://myproject.byethost8.com/insert.php", nameValuePairs);
                                try {
                                    returnString = "";
                                    JSONObject jObject = new JSONObject(result);
                                    Log.i("Log Tag", "Result:" + jObject.getString("re"));
                                    returnString += jObject.getString("re");
                                    //Toast.makeText(getApplicationContext(),"Pass"+returnString, Toast.LENGTH_SHORT).show();
                                    if (returnString.equals("Record inserted successfully")) {
                                        if (TextUtils.isEmpty(regId)) {
                                            regId = registerGCM();
                                            Log.d("RegisterActivity", "GCM RegId: " + regId);
                                        }
                                    } else if (returnString.equals("Record is repeated")) {
                                        Toast.makeText(getApplicationContext(), "Account Already Exists", Toast.LENGTH_LONG).show();
                                    }
                                } catch (JSONException e) {
                                    Log.e("log_tag", "Error parsing data " + e.toString());
                                }
                            } catch (Exception e) {
                                Log.e("log_tag", "Error in http connection!!" + e.toString());
                            }
                        } else {
                            Toast.makeText(getApplicationContext(), "Passwords not matched", Toast.LENGTH_LONG).show();
                        }
                    } else {
                        Toast.makeText(getApplicationContext(), "Fields are Empty", Toast.LENGTH_LONG).show();
                    }
                }
                else{
                    Toast.makeText(getApplicationContext(),"No Internet Connection"+"\n"+"Try Again",Toast.LENGTH_SHORT).show();
                }
            }
            });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_sign_up, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public String registerGCM() {

        gcm = GoogleCloudMessaging.getInstance(this);
        regId = getRegistrationId(context);

        if (TextUtils.isEmpty(regId)) {

            registerInBackground();

            Log.d("RegisterActivity",
                    "registerGCM - successfully registered with GCM server - regId: "
                            + regId);
        } else {
            try
            {
            ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
            nameValuePairs.add(new BasicNameValuePair("regid",regId));
            nameValuePairs.add(new BasicNameValuePair("mail",Email));
            Log.e("RegTag",regId);
            Log.e("mailtag",Email);
            result1 = CustomHttpClient.executeHttpPost("http://myproject.byethost8.com/UpdateRegId.php", nameValuePairs);
            Log.e("result from ",result1);
            Toast.makeText(getApplicationContext(), "Account has been Created", Toast.LENGTH_LONG).show();
            Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
            startActivity(intent);
            finish();
            }catch (Exception e) {
                Log.e("log_tag", "Error in http connection!!" + e.toString());
            }
        }
        return regId;
    }

    private String getRegistrationId(Context context) {
        final SharedPreferences prefs = getSharedPreferences(
                SignUp.class.getSimpleName(), Context.MODE_PRIVATE);
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
                Log.d("RegisterActivity", "AsyncTask completed: " + msg);
                return msg;
            }

            @Override
            protected void onPostExecute(String msg) {
                Toast.makeText(getApplicationContext(),
                        "Registered with GCM Server." + msg, Toast.LENGTH_LONG)
                        .show();
                try {
                    ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
                    nameValuePairs.add(new BasicNameValuePair("regid",regId));
                    nameValuePairs.add(new BasicNameValuePair("mail",Email));
                    Toast.makeText(getApplicationContext(),"pulla id is"+regId,Toast.LENGTH_SHORT).show();
                    result1 = CustomHttpClient.executeHttpPost("http://myproject.byethost8.com/UpdateRegId.php", nameValuePairs);
                    Toast.makeText(getApplicationContext(), "Account has been Created", Toast.LENGTH_LONG).show();
                    Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                    startActivity(intent);
                    finish();
                    Log.e("Result of RegId",result1);

                }catch (Exception e) {
                    Log.e("log_tag", "Error in http connection!!" + e.toString());
                }

            }
        }.execute(null, null, null);
    }

    private void storeRegistrationId(Context context, String regId) {
        final SharedPreferences prefs = getSharedPreferences(
                SignUp.class.getSimpleName(), Context.MODE_PRIVATE);
        int appVersion = getAppVersion(context);
        Log.i("TAG", "Saving regId on app version " + appVersion);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(REG_ID, regId);
        editor.putInt(APP_VERSION, appVersion);
        editor.commit();
    }
}
