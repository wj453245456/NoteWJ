package wj.com.note;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.IBinder;
import android.os.SystemClock;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import org.litepal.crud.DataSupport;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import wj.com.note.db.Note;

public class alarm extends Service {
    public alarm() {
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                Log.d("LongRunningService", "executed at " + new Date().toString());
              //比较系统时间
                SimpleDateFormat df=new SimpleDateFormat("yy MM dd  HH:mm");
                    Date date = new Date();
                List<Note> noteList = DataSupport.where("alarm is not null").find(Note.class);
                for (Note note:noteList){
                    try {
                        if (df.parse(note.getAlarm()).getTime()<date.getTime()){
                            note.save();Log.d("alarm", " aa"+note.getAlarm());
                            //通知
                            Intent i = new Intent(alarm.this,noteitemActivity.class);
                            i.putExtra("i_d",""+note.getId());
                            Log.d("ss", "run: "+note.getId());
                            PendingIntent pi = PendingIntent.getActivity(alarm.this , 0,i, PendingIntent.FLAG_UPDATE_CURRENT);
                            NotificationManager notificationManager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
                            Notification notification = new NotificationCompat.Builder(getBaseContext())
                                    .setContentTitle("提示:")
                                    .setContentText(note.getNoteText())
                                    .setWhen(System.currentTimeMillis())
                                    .setSmallIcon(R.id.loaction_icon)
                                    .setAutoCancel(true)
                                    .setContentIntent(pi)
                                    .setFullScreenIntent(pi,true)
                                    .setVibrate(new long[]{0,500,250,500})
                                    .setLights(Color.GREEN,1000,1000)
                                    .build();
                            notificationManager.notify(note.getId(),notification);

                        }
                    }catch (ParseException e){
                        e.printStackTrace();
                    }
                }

            }
        }).start();
        return START_STICKY;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        //时间监听器
        AlarmBrodercast receiver = new AlarmBrodercast();
        registerReceiver(receiver,new IntentFilter(Intent.ACTION_TIME_TICK));

    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
