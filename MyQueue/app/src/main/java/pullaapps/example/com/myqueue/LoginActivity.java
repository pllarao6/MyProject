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

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;


public class LoginActivity extends Activity {

    private Button btnSignup,btnLogin;
    private EditText txtEmail,txtPassword;
    SessionManager session;
    String returnString,result;
    //StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
    SessionManager sessionManager;
    String userid;
    String regId;
    GoogleCloudMessaging gcm;
    Context context;
    public static final String REG_ID = "regId";
    private static final String APP_VERSION = "appVersion";
    ArrayList<NameValuePair> postParameters;
    String mail;
    private String password;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        session = new SessionManager(getApplicationContext());
        btnLogin=(Button)findViewById(R.id.login);
        btnSignup=(Button)findViewById(R.id.signup);
        context = getApplicationContext();
        sessionManager=new SessionManager(this);
        //StrictMode.setThreadPolicy(policy);
        btnSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(getApplicationContext(),SignUp.class);
                startActivity(intent);
            }
        });

       btnLogin.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               txtEmail = (EditText) findViewById(R.id.username);
               txtPassword = (EditText) findViewById(R.id.password);
               mail = txtEmail.getText().toString();
               password = txtPassword.getText().toString();
               ConnectionDetector cd = new ConnectionDetector(getApplicationContext());
               Boolean isInternetPresent = cd.isConnectingToInternet(); // true or false
               if(isInternetPresent) {
                   if (mail.trim().length() > 0 && password.trim().length() > 0) {
                       //Toast.makeText(getApplicationContext(), "In Http execution",Toast.LENGTH_SHORT).show();
                       postParameters = new ArrayList<NameValuePair>();
                       postParameters.add(new BasicNameValuePair("KEY_EMAIL", mail));
                       postParameters.add(new BasicNameValuePair("KEY_PASSWORD", password));
                       registerGCM();
                   } else {
                       Toast.makeText(getApplicationContext(), "Login failed.." + "Please enter username and password", Toast.LENGTH_LONG).show();
                   }
               }
               else{
                   Toast.makeText(getApplicationContext(),"No Internet Connection",Toast.LENGTH_SHORT).show();
                   }
           }
       });
    }

    public void registerGCM() {

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
            loginInBackground();
        }
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
                loginInBackground();
            }
                }.execute(null,null,null);
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

    private void loginInBackground(){
        new AsyncTask<String, Void, String>() {

       private ProgressDialog dialog;
       protected void onPreExecute()
       {
           dialog = new ProgressDialog(LoginActivity.this);
           dialog.setMessage("Verifying.., please wait.");
           dialog.show();
       }

       protected String doInBackground(String... params) {
           postParameters.add(new BasicNameValuePair("KEY_REGID", regId));
           //Toast.makeText(context,mail+password,Toast.LENGTH_SHORT).show();
           try {
               result = CustomHttpClient.executeHttpPost("http://myproject.byethost8.com/select.php", postParameters);
               //Toast.makeText(getApplicationContext(), "After Http execution "+result,Toast.LENGTH_SHORT).show();
           } catch (Exception e) {
               Log.e("log_tag", "Error in http connection!!" + e.toString());
           }
           return result;
       }

       protected void onPostExecute(String result) {
           if (dialog.isShowing()) {
               dialog.dismiss();
           }
           try {
               returnString = "";
               JSONObject jObject = new JSONObject(result);
               Log.i("Log Tag", "Result:" + jObject.getString("re"));
               Log.i("Log Tag", "Result :" + jObject.getInt("id"));
               returnString += jObject.getString("re");
               userid = String.valueOf(jObject.getInt("id"));
               //Toast.makeText(getApplicationContext(), returnString, Toast.LENGTH_LONG).show();
               //Get an output to the screen
           } catch (JSONException e) {
               Log.e("log_tag", "Error parsing data " + e.toString());
           }
           if (returnString.equals("ok")) {
               session.createLoginSession("User", mail, password, userid);
               Intent intent = new Intent(getApplicationContext(), Store.class);
               startActivity(intent);
               finish();
           } else {
               // username / password doesn't match
               Toast.makeText(getApplicationContext(), "Login failed.." + "Username/Password is incorrect", Toast.LENGTH_LONG).show();
           }
       }
        }.execute(null,null,null);
    }
}
