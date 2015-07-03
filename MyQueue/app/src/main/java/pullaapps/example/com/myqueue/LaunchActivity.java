package pullaapps.example.com.myqueue;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import java.util.Timer;
import java.util.TimerTask;


public class LaunchActivity extends Activity {

    private static final long DELAY = 3000;
    private boolean scheduled = false;
    private Timer splashTimer;
    SessionManager session;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launch);
        splashTimer = new Timer();
        splashTimer.schedule(new TimerTask()
        {
            @Override
            public void run()
            {
                session=new SessionManager(getApplicationContext());
                session.checkLogin();
                LaunchActivity.this.finish();
                if(session.isLoggedIn())
                startActivity(new Intent(LaunchActivity.this, Store.class));
            }
        }, DELAY);
        scheduled = true;
    }
}
