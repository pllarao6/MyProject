package pullaapps.example.com.myqueue;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import java.util.HashMap;

/**
 * Created by vankayap on 3/5/2015.
 */

public class SessionManager {

    SharedPreferences pref;

    // Editor for Shared preferences
    SharedPreferences.Editor editor;

    // Context
    Context _context;

    // Shared pref mode
    int PRIVATE_MODE = 0;

    // Sharedpref file name
    private static final String PREF_NAME = "AppSharedPreferences";

    // All Shared Preferences Keys
    private static final String IS_LOGIN = "IsLoggedIn";

    public static final String KEY_NAME = "name";

    public static final String KEY_EMAIL = "email";

    public static final String KEY_PASSWORD= "password";

    public static final String KEY_LAT="latitude";

    public static final String KEY_LON="longitude";

    public static final String KEY_MERCHANT="merchant_name";

    public static final String KEY_MERCHANTID="merchant_id";

    public static final String KEY_USERID="userid";

    // Constructor
    public SessionManager(Context context){
        this._context = context;
        pref = _context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = pref.edit();
    }

    public void createLoginSession(String name, String email, String password,String userid){
        // Storing login value as TRUE
        editor.putBoolean(IS_LOGIN, true);

        // Storing name in pref
        editor.putString(KEY_NAME, name);

        // Storing email in pref
        editor.putString(KEY_EMAIL, email);

        // Storing password in pref
        editor.putString(KEY_PASSWORD,password);
        // commit changes

        editor.putString(KEY_USERID, userid);

        editor.commit();
    }

    public void setInfo(String Lat,String Lon)
    {
        editor.putString(KEY_LAT,Lat);
        editor.putString(KEY_LON,Lon);
        editor.commit();
    }

    // Check login method wil check user login status
    // If false it will redirect user to login page
    //Else won't do anything

    public void checkLogin(){
        // Check login status
        if(!this.isLoggedIn()){
            // user is not logged in redirect him to Login Activity
            Intent i = new Intent(_context, LoginActivity.class);

            // Closing all the Activities
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

            // Add new Flag to start new Activity
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

            // Staring Login Activity
            _context.startActivity(i);
        }

    }

    // Get stored session data

    public HashMap<String, String> getUserDetails(){
        HashMap<String, String> user = new HashMap<String, String>();
        // user name
        user.put(KEY_NAME, pref.getString(KEY_NAME, null));

        // user email id
        user.put(KEY_EMAIL, pref.getString(KEY_EMAIL, null));

        // return user
        return user;
    }

    public String getUserId()
    {
        return pref.getString(KEY_USERID,null);
    }
    //Get Info

    public HashMap<String, String> getLocationInfo()
    {
        HashMap<String, String> location=new HashMap<String, String>();

        location.put(KEY_LAT,pref.getString(KEY_LAT,null));
        location.put(KEY_LON,pref.getString(KEY_LON,null));

        return location;
    }

    public void setMerchantInfo(String merchantid,String merchant)
    {
        editor.putString(KEY_MERCHANTID,merchantid);
        editor.putString(KEY_MERCHANT,merchant);
        editor.commit();
    }

    public HashMap<String, String> getMerchantInfo()
    {
        HashMap<String,String> merchant=new HashMap<String,String>();
        merchant.put(KEY_MERCHANTID, pref.getString(KEY_MERCHANTID, null));
        merchant.put(KEY_MERCHANT,pref.getString(KEY_MERCHANT,null));
        return merchant;
    }
    //Clear session details

    public void logoutUser(){
        // Clearing all data from Shared Preferences
        editor.clear();
        editor.commit();

        // After logout redirect user to Loing Activity
        Intent i = new Intent(_context, LoginActivity.class);
        // Closing all the Activities
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        // Add new Flag to start new Activity
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        // Staring Login Activity
        _context.startActivity(i);
    }


    //Quick check for login
    // Get Login State
    public boolean isLoggedIn(){
        return pref.getBoolean(IS_LOGIN, false);
    }
}
