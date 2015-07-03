package pullaapps.example.com.myqueue;

import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by vankayap on 4/16/2015.
 */
public class CustomHttpConnector1 extends AsyncTask<JSONObject,Void,Integer>
{
    InputStream inputStream;
    String responseString;
    int result;
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
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
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
        Log.d("Tag_res",x.intValue()+"");
    }
}

