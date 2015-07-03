package pullaapps.example.com.myqueue;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.os.PowerManager;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.WakefulBroadcastReceiver;
import android.util.Log;

import com.google.android.gms.gcm.GoogleCloudMessaging;

public class GcmBroadcastReceiver extends WakefulBroadcastReceiver {

    private static final String TAG = "GcmBroadcastReceiver";
    private Context ctx;
    public static final int NOTIFICATION_ID = 1;
    private NotificationManager mNotificationManager;
    NotificationCompat.Builder builder;
    @Override
    public void onReceive(Context context, Intent intent) {
        ctx = context;

        PowerManager mPowerManager = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
        PowerManager.WakeLock mWakeLock = mPowerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, TAG);
        mWakeLock.acquire();

        try {
            GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(context);

            String messageType = gcm.getMessageType(intent);

            if (GoogleCloudMessaging.MESSAGE_TYPE_SEND_ERROR.equals(messageType)) {
                sendNotification("Send error"+"false","","");

            } else if (GoogleCloudMessaging.MESSAGE_TYPE_DELETED.equals(messageType)) {
                sendNotification("Deleted messages on server"+"false","","");

            } else {
                if (!intent.hasExtra("registration_id")) {
                    String orderid = intent.getStringExtra("OrderId");
                    String date = intent.getStringExtra("Date");
                    String time = intent.getStringExtra("Time");
                    String store = intent.getStringExtra("Store");
                    Log.e("all extras", intent.getExtras().toString());
                    Intent i = new Intent("unique_name");

                    //put whatever data you want to send, if any
                    i.putExtra("orderid", orderid);
                    i.putExtra("date", date);
                    i.putExtra("time", time);
                    i.putExtra("store", store);
                    //send broadcast
                    ctx.sendBroadcast(i);
                    Log.e("After Broadcast", "heo");
                    sendNotification(orderid, date, time);
                }
            }
        }
        catch (Exception e)
        {

        } finally {
            mWakeLock.release();
        }
    }

    private void sendNotification(String orderid,String date,String time) {

        Intent resultIntent=new Intent(ctx,DisplayOrder.class);
        resultIntent.putExtra("orderid",orderid);
        resultIntent.putExtra("date",date);
        resultIntent.putExtra("time",time);
        TaskStackBuilder stackBuilder=TaskStackBuilder.create(ctx);
        stackBuilder.addParentStack(DisplayOrder.class);
        stackBuilder.addNextIntent(resultIntent);
        PendingIntent contentIntent = stackBuilder.getPendingIntent(0,PendingIntent.FLAG_UPDATE_CURRENT);
        mNotificationManager = (NotificationManager) ctx.getSystemService(Context.NOTIFICATION_SERVICE);

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(
                ctx).setSmallIcon(R.drawable.gcm_cloud)
                .setContentTitle("Order Status")
                .setAutoCancel(true)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(orderid))
                .setContentText(orderid);

        mBuilder.setContentIntent(contentIntent);
        mNotificationManager.notify(NOTIFICATION_ID, mBuilder.build());
        Log.e(TAG, "Notification sent successfully.");
    }
}
