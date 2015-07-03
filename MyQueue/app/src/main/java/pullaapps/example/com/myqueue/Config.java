package pullaapps.example.com.myqueue;

/**
 * Created by vankayap on 4/21/2015.
 */
public interface Config {
    // used to share GCM regId with application server - using php app server
    static final String APP_SERVER_URL = "http://myproject.byethost8.com/";

    // GCM server using java
    // static final String APP_SERVER_URL =
    // "http://192.168.1.17:8080/GCM-App-Server/GCMNotification?shareRegId=1";

    // Google Project Number
    static final String GOOGLE_PROJECT_ID = "342236941325";
    static final String MESSAGE_KEY = "message";
}
