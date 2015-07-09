package pullaapps.example.com.myqueue.network;

import android.content.Context;
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
import javax.net.ssl.HttpsURLConnection;

/**
 * Created by Prathyusha on 3/7/2015.
 */
public class HttpConnector {

    public JSONObject toSend;
    public String resource;
    public HttpURLConnection httpURLConnection;
    public InputStream inputStream;
    public String method;
    public int userid;
    public Context context;

    public HttpConnector(JSONObject toSend, String resource, String method,Context context) {
        this.toSend = toSend;
        this.resource = resource;
        this.method = method;
        httpURLConnection = null;
        inputStream = null;
        this.context=context;
    }

    public HttpConnector(int userid,String resource,String method,Context context)
    {
        this.userid=userid;
        this.resource=resource;
        this.method=method;
        httpURLConnection=null;
        inputStream = null;
        this.context=context;
    }

    public int makeConnection() {
        int result;
        if (method == "POST") {
           result=httpPost();
        } else {
            result=httpGet();
        }
        return result;
    }

    public int httpPost()
    {
        int flag=0;
        ConnectionDetector connectionDetector=new ConnectionDetector(context);
        Boolean isInternetPresent=connectionDetector.isConnectingToInternet();
        if(isInternetPresent) {
            try {
                URL url = new URL(resource);
                httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setRequestProperty("Content-Type",
                        "application/x-www-form-urlencoded");
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoOutput(true);
                String param = "json=" + toSend.toString();
                httpURLConnection.setFixedLengthStreamingMode(param.getBytes().length);
                PrintWriter out = new PrintWriter(httpURLConnection.getOutputStream());
                out.write(param);
                out.close();
                if (httpURLConnection.getResponseCode() == 200) {
                    flag = 1;
                    inputStream = new BufferedInputStream(httpURLConnection.getInputStream());
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
            else
            {
                flag=2;
            }
        return flag;
        }

    public int httpGet() {
        int flag = 0;
        ConnectionDetector connectionDetector = new ConnectionDetector(context);
        Boolean isInternetPresent = connectionDetector.isConnectingToInternet();
        if (isInternetPresent) {
            try {
                resource = resource + "?UserId=" + userid;
                URL url = new URL(resource);
                httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setRequestProperty("Content-Type", "application/json");
                httpURLConnection.setRequestProperty("Accept", "application/json");
                httpURLConnection.setRequestMethod("GET");
                if (httpURLConnection.getResponseCode() == 200) {
                    Log.e("in", "response");
                    flag = 1;
                    inputStream = new BufferedInputStream(httpURLConnection.getInputStream());
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        else
        {
            flag=2;
        }
        return flag;
    }

public String convertInputStream() throws IOException
        {
        String result="";
        String line;
        if(inputStream!=null)
        {
        BufferedReader bufferedReader=new BufferedReader(new InputStreamReader(inputStream));
        while((line=bufferedReader.readLine())!=null)
        {
        result+=line;
        }
        }
        if (inputStream!=null)
        inputStream.close();
        return result;
        }

public void close()
  {
      httpURLConnection.disconnect();
  }
}
