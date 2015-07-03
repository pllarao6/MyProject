package pullaapps.example.com.myqueue;

import pullaapps.example.com.myqueue.network.*;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONObject;

import java.io.InputStream;


public class Profile extends BaseActivity  {

    public View inflatedView;
    private static final String USER_URL="http://myproject.byethost8.com/getUserData.php";
    private TextView mName;
    private TextView mEmail;
    private TextView mMobile;
    private SessionManager sessionManager;
    private String userid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        inflatedView=getLayoutInflater().inflate(R.layout.activity_profile, frameLayout);
        setTitle(titles[0]);
        flag1=1;
        flag2=0;
        flag3=0;
        sessionManager=new SessionManager(this);
        userid=sessionManager.getUserId();
        mName=(TextView)findViewById(R.id.name);
        mEmail=(TextView)findViewById(R.id.email);
        mMobile=(TextView)findViewById(R.id.mobile);
    }

    @Override
    protected void onStart()
    {
        super.onStart();
    }

    public void onResume()
    {
        super.onResume();
        new CustomAsyncTask().execute(USER_URL);
    }

    public void onPause()
    {
        super.onPause();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_profile, menu);
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

    class CustomAsyncTask extends AsyncTask<String,Void,Integer>
    {
        HttpConnector httpConnector;
        String response;
        @Override
        protected void onPreExecute()
        {


        }

        @Override
        protected Integer doInBackground(String... params) {
            int flag=0;
            try
            {
                Log.i("userid",userid);
                httpConnector=new HttpConnector(Integer.parseInt(userid),params[0],"GET");
                flag=httpConnector.makeConnection();
                if(flag==1)
                {
                        response=httpConnector.convertInputStream();
                }
            }catch (Exception e)
            {
                e.printStackTrace();
            }
            return flag;
        }

        @Override
        protected void onPostExecute(Integer res)
        {
            httpConnector.close();
            if(res==1)
            {
                parseResult(response);
            }
            else {
                Toast.makeText(getApplicationContext(),"Failed to update Data",Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void parseResult(String response)
    {
        JSONObject jsonObject;
        Log.i("taggy",response);
        try
        {
            jsonObject=new JSONObject(response);
            mName.setText(jsonObject.getString("Name"));
            mEmail.setText(jsonObject.getString("Email"));
            mMobile.setText(jsonObject.getString("Mobile"));
        }catch (Exception e)
        {
            e.printStackTrace();
        }
    }
}
