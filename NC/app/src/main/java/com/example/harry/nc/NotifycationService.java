package com.example.harry.nc;

/**
 * Created by harry on 3/27/17.
 */

import android.support.v4.app.NotificationCompat;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;

public class NotifycationService extends Service {

    // 获取消息线程
    private MessageThread messageThread = null;

    // 点击查看
    private Intent messageIntent = null;
    private PendingIntent messagePendingIntent = null;

    // 通知栏消息
    private int messageNotificationID = 1000;
    private NotificationCompat.Builder messageNotification = null;
    private NotificationManager messageNotificatioManager = null;

    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // 初始化



         messageNotification = new NotificationCompat.Builder(this);

         //messageNotification.setContentTitle("New mail from ");
         messageNotification.setContentText("News");
         messageNotification.setSmallIcon(R.drawable.sample_layout);

        messageNotificatioManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        messageIntent = new Intent(this, MainActivity.class);
        messagePendingIntent = PendingIntent.getActivity(this, 0,
                messageIntent, 0);

        // 开启线程
        messageThread = new MessageThread();
        messageThread.isRunning = true;
        messageThread.start();



        return super.onStartCommand(intent, flags, startId);
    }

    /**
     * 从服务器端获取消息
     *
     */
    class MessageThread extends Thread {
        // 设置是否循环推送
        public boolean isRunning = true;

        public void run() {
            // while (isRunning) {
            try {
                // 间隔时间
                Thread.sleep(1000);
                // 获取服务器消息
                String serverMessage = getServerMessage();
                if (serverMessage != null && !"".equals(serverMessage)) {
                    // 更新通知栏

                    messageNotification.setContentTitle("New Notification about COMP512");
                  //  messageNotification.setContentText("Time to learn about notifications!");
                   // messageNotification.setSubText("Tap to view documentation about notifications.");


                    messageNotificatioManager.notify(messageNotificationID,
                            messageNotification.build());
                    // 每次通知完，通知ID递增一下，避免消息覆盖掉
                    messageNotificationID++;
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            // }
        }
    }

    @Override
    public void onDestroy() {
        // System.exit(0);
        messageThread.isRunning = false;
        super.onDestroy();
    }

    /**
     * 模拟发送消息
     *
     * @return 返回服务器要推送的消息，否则如果为空的话，不推送
     */
    public String getServerMessage() {
        return "NEWS!";
    }
}
