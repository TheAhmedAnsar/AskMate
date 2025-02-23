package com.askmate;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.util.Log;
import android.widget.RemoteViews;

import androidx.core.app.NotificationCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

        public class MyFirebaseMessagingService extends FirebaseMessagingService {

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        // Check if message contains a data payload.
        Log.d("token", "Loading notification: " );
        if (remoteMessage.getData().size() > 0) {
            String imageUrl = remoteMessage.getData().get("imageUrl");
            String headline = remoteMessage.getData().get("headline");
            String content = remoteMessage.getData().get("content");
            String body = remoteMessage.getData().get("body");

            String title = remoteMessage.getData().get("title");
            String question = remoteMessage.getData().get("question");
            String fragment = remoteMessage.getData().get("fragment");
            if (headline != null) {
                sendNotification("📢 News", headline, fragment);
            } else if (question != null) {
                sendNotification("🧠 Do you know its answer? 🤔", question, fragment);
            }
            // Use the actual title and body from the data payload

          else
            {
                sendNotification(title, body, "");
            }
            Log.d("token", "Loading notification: Answers");

//            sendNotification(headline, content);

        }
    }

//      private void sendNotification(String title, String messageBody, String imageUrl){
      private void sendNotification(String title, String messageBody, String fragment){
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
          intent.putExtra("fragment", fragment);
          intent.putExtra("title", title);
          intent.putExtra("message", messageBody);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT);

//        String channelId = "default_channel_id";
        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        RemoteViews notificationLayout = new RemoteViews(getPackageName(), R.layout.custom_notification);

        notificationLayout.setTextViewText(R.id.notification_title, title);
        notificationLayout.setTextViewText(R.id.notification_subtitle, messageBody);

        // Set image using Glide or other library
//        Bitmap bitmap = getBitmapFromURL(imageUrl);
//        notificationLayout.setImageViewBitmap(R.id.roundedImageView, bitmap);

        NotificationCompat.Builder notificationBuilder =
                new NotificationCompat.Builder(this, String.valueOf(R.string.default_notification_channel_id))
                        .setSmallIcon(R.drawable.appicon)
                        .setAutoCancel(true)
                        .setSound(defaultSoundUri)
                        .setPriority(NotificationCompat.PRIORITY_HIGH)
                        .setVibrate(new long[]{1000, 1000, 1000, 1000, 1000})
                        .setContentIntent(pendingIntent)
                        .setCustomContentView(notificationLayout);

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(String.valueOf(R.string.default_notification_channel_id),
                    "Channel human readable title",
                    NotificationManager.IMPORTANCE_HIGH);
            notificationManager.createNotificationChannel(channel);
        }

        notificationManager.notify(0, notificationBuilder.build());
    }

    private Bitmap getBitmapFromURL(String strURL) {
        try {
            URL url = new URL(strURL);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            return BitmapFactory.decodeStream(input);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }


























// working
//    private void sendNotification(String title, String messageBody, String imageUrl) {
//        Intent intent = new Intent(this, MainActivity.class);
//        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT);
//
////        String channelId = "default_channel_id";
//        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
//        NotificationCompat.Builder notificationBuilder =
//                new NotificationCompat.Builder(this, String.valueOf(R.string.default_notification_channel_id))
//                        .setSmallIcon(R.drawable.appicon)
//                        .setContentTitle(title)
//                        .setContentText(messageBody)
//                        .setAutoCancel(true)
//                        .setSound(defaultSoundUri)
//                        .setPriority(NotificationCompat.PRIORITY_HIGH)
//                        .setVibrate(new long[]{1000, 1000, 1000, 1000, 1000})
//                        .setContentIntent(pendingIntent);
//
//        // If you want to include an image in the notification, you can use a BigPictureStyle
//        if (imageUrl != null && !imageUrl.isEmpty()) {
//            NotificationCompat.BigPictureStyle bigPictureStyle = new NotificationCompat.BigPictureStyle()
//                    .bigPicture(getBitmapFromURL(imageUrl))
//                    .setBigContentTitle(title)
//                    .setSummaryText(messageBody);
//            notificationBuilder.setStyle(bigPictureStyle);
//        }
//
//        NotificationManager notificationManager =
//                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
//
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            NotificationChannel channel = new NotificationChannel(String.valueOf(R.string.default_notification_channel_id),
//                    "Channel human readable title",
//                    NotificationManager.IMPORTANCE_HIGH);
//            notificationManager.createNotificationChannel(channel);
//        }
//
//        notificationManager.notify(0, notificationBuilder.build());
//    }

//    private Bitmap getBitmapFromURL(String strURL) {
//        try {
//            URL url = new URL(strURL);
//            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
//            connection.setDoInput(true);
//            connection.connect();
//            InputStream input = connection.getInputStream();
//            return BitmapFactory.decodeStream(input);
//        } catch (IOException e) {
//            e.printStackTrace();
//            return null;
//        }
//    }
}


// update it
//import static android.app.Notification.DEFAULT_SOUND;
//import static android.app.Notification.DEFAULT_VIBRATE;
//import static android.app.Notification.VISIBILITY_PUBLIC;
//
//import com.askmate.MainActivity;
//import com.askmate.R;
//import com.google.firebase.messaging.FirebaseMessagingService;
//import com.google.firebase.messaging.RemoteMessage;
//
//import android.app.Notification;
//import android.app.NotificationChannel;
//import android.graphics.Bitmap;
//import android.graphics.BitmapFactory;
//import android.os.Build;
//import android.util.Log;
//import android.app.NotificationManager;
//import android.app.PendingIntent;
//import android.content.Context;
//import android.content.Intent;
//import android.widget.RemoteViews;
//
//import androidx.annotation.RequiresApi;
//import androidx.core.app.NotificationCompat;
//import androidx.core.app.NotificationManagerCompat;
//
//import java.io.InputStream;
//import java.net.HttpURLConnection;
//import java.net.URL;
//
//public class MyFirebaseMessagingService extends FirebaseMessagingService {
//
//    private static final String TAG = "MyFirebaseMsgService";
////    private static final String CHANNEL_ID = "news_channel";
//
//    @Override
//    public void onMessageReceived(RemoteMessage remoteMessage) {
//        Log.d(TAG, "From: " + remoteMessage.getFrom());
//        if (remoteMessage.getNotification() != null) {
//            String title = remoteMessage.getNotification().getTitle();
////            String title = remoteMessage.getData().get("headline");
//            String imageUrl = remoteMessage.getData().get("imageUrl");
//            String headlines = remoteMessage.getData().get("content");
//
//            Bitmap imageBitmap = getBitmapFromURL(imageUrl);
//
////            showNotification( headlines, imageBitmap);
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
////                sendNotification(headlines);
//                showHeadsUpNotification(headlines, title);
//            }
//        }
////        if (remoteMessage.getData().size() > 0) {
////            Log.d(TAG, "Message Notification Body: " + remoteMessage.getNotification().getBody());
////            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
////                sendNotification(remoteMessage.getNotification().getBody());
////            }
////        }
//    }
//    private void showNotification( String headlines, Bitmap imageBitmap) {
//        createNotificationChannel();
//
//        Intent intent = new Intent(this, MainActivity.class);
//        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT);
//
//        RemoteViews notificationLayout = new RemoteViews(getPackageName(), R.layout.custom_notification);
////
//        notificationLayout.setTextViewText(R.id.notification_title, headlines);
////        notificationLayout.setTextViewText(R.id.notification_text, message);
//
//
//        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, String.valueOf(R.string.default_notification_channel_id))
//                .setSmallIcon(R.drawable.appicon)
//                .setContentTitle("News")
//                .setDefaults(DEFAULT_SOUND | DEFAULT_VIBRATE) //Important for heads-up notification
//                .setPriority(NotificationCompat.PRIORITY_MAX) //Important for heads-up notification
//                .setChannelId(String.valueOf(R.string.default_notification_channel_id))
////                .setContentText(headlines)
////                .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.appicon))
////                .setStyle(new NotificationCompat.BigPictureStyle()
////                        .bigPicture(imageBitmap)
////                        .bigLargeIcon(null))
//                .setContentIntent(pendingIntent)
//                .setAutoCancel(true)
//                .setStyle(new NotificationCompat.DecoratedCustomViewStyle())
//                .setCustomContentView(notificationLayout);
//
//
//        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
//        notificationManager.notify(0, builder.build());
//    }
//
//    private void createNotificationChannel() {
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            CharSequence name = "News Channel";
//            String description = "Channel for news notifications";
//            int importance = NotificationManager.IMPORTANCE_HIGH;
//            NotificationChannel channel = new NotificationChannel(String.valueOf(R.string.default_notification_channel_id), name, importance);
//            channel.setDescription(description);
//            channel.enableVibration(true);
//
//            NotificationManager notificationManager = getSystemService(NotificationManager.class);
//            notificationManager.createNotificationChannel(channel);
//        }
//    }
//
//
//    @RequiresApi(api = Build.VERSION_CODES.N)
//    private void sendNotification(String messageBody) {
//        Intent intent = new Intent(this, MainActivity.class);
//        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT);
//
//
//
//        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, "default")
//                .setSmallIcon(R.drawable.askmate)
//                .setContentTitle("FCM Message")
//                .setPriority(NotificationCompat.PRIORITY_HIGH)
//                .setContentText(messageBody)
//                .setDefaults(DEFAULT_SOUND | DEFAULT_VIBRATE ) //Important for heads-up notification
//                .setAutoCancel(true)
//                .setContentIntent(pendingIntent);
////         .setStyle(new NotificationCompat.BigPictureStyle()
//// .bigPicture(imageBitmap));
//
//
//        int importance = NotificationManager.IMPORTANCE_HIGH;
//        NotificationChannel channel = null;
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            channel = new NotificationChannel(String.valueOf(R.string.default_notification_channel_id), "name", importance);
//            channel.setDescription("description");
//            channel.enableVibration(true);
//
//
//        }
//
//        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
//
//        notificationManager.notify(0, notificationBuilder.build());
//    }
//
//
//    private void showHeadsUpNotification(String title, String message) {
//        Intent intent = new Intent(this, MainActivity.class);
//        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT);
//
//        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, String.valueOf(R.string.default_notification_channel_id))
//                .setSmallIcon(R.drawable.appicon)
//                .setContentTitle(title)
//                .setContentText(message)
//                .setStyle(new NotificationCompat.BigTextStyle().bigText(message))
//                .setPriority(NotificationCompat.PRIORITY_HIGH) // Important for heads-up display
//                .setContentIntent(pendingIntent)
//                .setAutoCancel(true)
//                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
//                .setFullScreenIntent(pendingIntent, true) // Optional: Make notification fully expandable
//
//                // Heads-up notification configuration (API level 21 and above)
//                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
//                .setCategory(NotificationCompat.CATEGORY_SERVICE); // Optional: Set category for grouping
//
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//            builder.setDefaults(NotificationCompat.DEFAULT_LIGHTS); // Optional: Set minimal defaults
//        } else {
//            // Set defaults for pre-Lollipop (sound and vibration might be restricted)
//            builder.setDefaults(NotificationCompat.DEFAULT_SOUND | NotificationCompat.DEFAULT_VIBRATE);
//        }
//
//        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
//        notificationManager.notify(0 /* ID */, builder.build());
//    }
//
//
//    private Bitmap getBitmapFromURL(String imageUrl) {
//        try {
//            URL url = new URL(imageUrl);
//            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
//            connection.setDoInput(true);
//            connection.connect();
//            InputStream input = connection.getInputStream();
//            return BitmapFactory.decodeStream(input);
//        } catch (Exception e) {
//            e.printStackTrace();
//            return null;
//        }
//    }
//    @Override
//    public void onNewToken(String token) {
//        Log.d(TAG, "Refreshed token: " + token);
//        sendRegistrationToServer(token);
//    }
//
//    private void sendRegistrationToServer(String token) {
//        // Send token to your app server if needed
//    }
//}
//// last update part
//
////package com.askmate;
////
////import android.app.NotificationChannel;
////import android.app.NotificationManager;
////import android.app.PendingIntent;
////import android.content.Context;
////import android.content.Intent;
////import android.media.RingtoneManager;
////import android.net.Uri;
////import android.os.Build;
////import android.util.Log;
////
////import androidx.annotation.NonNull;
////import androidx.core.app.NotificationCompat;
////
////import com.google.firebase.messaging.FirebaseMessagingService;
////import com.google.firebase.messaging.RemoteMessage;
////
////public class MyFirebaseMessagingService extends FirebaseMessagingService {
////
////    private static final String TAG = "MyFirebaseMsgService";
////    private static final String NEWS_CHANNEL_ID = "News_Channel";
////
////    @Override
////    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
////        super.onMessageReceived(remoteMessage);
////
////        // Check if the message contains notification payload.
////        if (remoteMessage.getNotification() != null) {
////            String title = remoteMessage.getNotification().getTitle();
////            String body = remoteMessage.getNotification().getBody();
////
////            // Handle notification payload and display notification.
////            sendNotification(title, body);
////        }
////    }
////
////    private void sendNotification(String title, String body) {
////        Intent intent = new Intent(this, MainActivity.class);
////        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
////        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,
////                PendingIntent.FLAG_ONE_SHOT);
////
////        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
////
////        NotificationCompat.Builder notificationBuilder =
////                new NotificationCompat.Builder(this, NEWS_CHANNEL_ID)
////                        .setSmallIcon(R.mipmap.ic_launcher)
////                        .setContentTitle(title)
////                        .setContentText(body)
////                        .setAutoCancel(true)
////                        .setSound(defaultSoundUri)
////                        .setContentIntent(pendingIntent);
////
////        NotificationManager notificationManager =
////                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
////
////        // Since android Oreo notification channel is needed.
////        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
////            NotificationChannel channel = new NotificationChannel(
////                    NEWS_CHANNEL_ID,
////                    "News Channel",
////                    NotificationManager.IMPORTANCE_DEFAULT);
////            notificationManager.createNotificationChannel(channel);
////        }
////
////        notificationManager.notify(0 /* ID of notification */, notificationBuilder.build());
////    }
////
////    @Override
////    public void onNewToken(@NonNull String token) {
////        Log.d(TAG, "Refreshed token: " + token);
////
////        // If you want to send messages to this application instance or
////        // manage this apps subscriptions on the server side, send the
////        // FCM registration token to your app server.
////        // TODO: Implement this method to send token to your app server.
////    }
////}
