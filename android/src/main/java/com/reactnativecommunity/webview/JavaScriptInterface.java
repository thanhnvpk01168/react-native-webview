package com.reactnativecommunity.webview;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.util.Base64;
import android.util.Log;
import android.webkit.JavascriptInterface;
import android.webkit.MimeTypeMap;
import android.widget.Toast;

import androidx.core.app.NotificationCompat;
import androidx.core.content.FileProvider;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.util.Calendar;
import java.util.Date;

public class JavaScriptInterface {
  private Context context;
  public JavaScriptInterface(Context context) {
    this.context = context;
  }

  @JavascriptInterface
  public void getBase64FromBlobData(String base64Data) throws IOException {
    convertBase64StringToPdfAndStoreIt(base64Data);
  }
  public static String getBase64StringFromBlobUrl(String blobUrl) {
    if(blobUrl.startsWith("blob")){
      return "javascript: var xhr = new XMLHttpRequest();" +
        "xhr.open('GET', '"+ blobUrl +"', true);" +
        "xhr.setRequestHeader('Content-type','application/pdf');" +
        "xhr.responseType = 'blob';" +
        "xhr.onload = function(e) {" +
        "    if (this.status == 200) {" +
        "        var blobPdf = this.response;" +
        "        var reader = new FileReader();" +
        "        reader.readAsDataURL(blobPdf);" +
        "        reader.onloadend = function() {" +
        "            base64data = reader.result;" +
        "            Android.getBase64FromBlobData(base64data);" +
        "        }" +
        "    }" +
        "};" +
        "xhr.send();";
    }
    return "javascript: console.log('It is not a Blob URL');";
  }
  private void convertBase64StringToPdfAndStoreIt(String base64PDf) throws IOException {
    final int notificationId = 1;
    String currentDateTime = DateFormat.getDateTimeInstance().format(new Date());
    final File dwldsPath = new File(Environment.getExternalStoragePublicDirectory(
      Environment.DIRECTORY_DOWNLOADS) + "/Rce_PDF_" + currentDateTime + ".pdf");
    byte[] pdfAsBytes = Base64.decode(base64PDf.replaceFirst("^data:application/pdf;base64,", ""), 0);
    FileOutputStream os;
    os = new FileOutputStream(dwldsPath, false);
    os.write(pdfAsBytes);
    os.flush();

    if (dwldsPath.exists()) {
      Intent intent = new Intent();
      intent.setAction(android.content.Intent.ACTION_VIEW);
      Uri apkURI = FileProvider.getUriForFile(context,context.getApplicationContext().getPackageName()+ ".provider", dwldsPath);
      intent.setDataAndType(apkURI, MimeTypeMap.getSingleton().getMimeTypeFromExtension("pdf"));
      intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
      PendingIntent pendingIntent = PendingIntent.getActivity(context,1, intent, PendingIntent.FLAG_CANCEL_CURRENT);
      String CHANNEL_ID = currentDateTime;
      final NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

      if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
        NotificationChannel notificationChannel= new NotificationChannel(CHANNEL_ID,"name", NotificationManager.IMPORTANCE_LOW);
        Notification notification = new Notification.Builder(context,CHANNEL_ID)
          .setContentText("Download complete")
          .setContentTitle("Rce_PDF_" + currentDateTime + ".pdf")
          .setContentIntent(pendingIntent)
          .setChannelId(CHANNEL_ID)
          .setSmallIcon(android.R.drawable.stat_sys_download_done)
          .build();
        if (notificationManager != null) {
          notificationManager.createNotificationChannel(notificationChannel);
          notificationManager.notify(notificationId, notification);
        }

      } else {
        NotificationCompat.Builder b = new NotificationCompat.Builder(context, CHANNEL_ID)
          .setDefaults(NotificationCompat.DEFAULT_ALL)
          .setWhen(System.currentTimeMillis())
          .setSmallIcon(android.R.drawable.stat_sys_download_done)
          //.setContentIntent(pendingIntent)
          .setContentTitle("Rce_PDF_" + currentDateTime + ".pdf")
          .setContentText("Download complete");

        if (notificationManager != null) {
          notificationManager.notify(notificationId, b.build());
          Handler h = new Handler();
          long delayInMilliseconds = 20000;
          h.postDelayed(new Runnable() {
            public void run() {
              notificationManager.cancel(notificationId);
            }
          }, delayInMilliseconds);
        }
      }
    }
    Toast.makeText(context, "PDF FILE DOWNLOADED!", Toast.LENGTH_SHORT).show();
  }
}
//public class JavaScriptInterface {
//  private Context context;
//  private NotificationManager nm;
//  public JavaScriptInterface(Context context) {
//    this.context = context;
//  }
//
//  @JavascriptInterface
//  public void getBase64FromBlobData(String base64Data) throws IOException {
//    convertBase64StringToPdfAndStoreIt(base64Data);
//  }
//  public static String getBase64StringFromBlobUrl(String blobUrl){
//    if(blobUrl.startsWith("blob")){
//      return "javascript: var xhr=new XMLHttpRequest();" +
//        "xhr.open('GET', '"+blobUrl+"', true);" +
//        "xhr.setRequestHeader('Content-type','application/vnd.openxmlformats-officedocument.spreadsheetml.sheet;charset=UTF-8');" +
//        "xhr.responseType = 'blob';" +
//        "xhr.onload = function(e) {" +
//        "    if (this.status == 200) {" +
//        "        var blobPdf = this.response;" +
//        "        var reader = new FileReader();" +
//        "        reader.readAsDataURL(blobPdf);" +
//        "        reader.onloadend = function() {" +
//        "            base64data = reader.result;" +
//        "            Android.getBase64FromBlobData(base64data);" +
//        "        }" +
//        "    }" +
//        "};" +
//        "xhr.send();";
//
//
//    }
//    return "javascript: console.log('It is not a Blob URL');";
//  }
//  private void convertBase64StringToPdfAndStoreIt(String base64PDf) throws IOException {
//
//    Log.e("base64PDf",base64PDf);
//    String currentDateTime = DateFormat.getDateTimeInstance().format(new Date());
//    Calendar calendar=Calendar.getInstance();
//    ;
//    final File dwldsPath = new File(Environment.getExternalStoragePublicDirectory(
//      Environment.DIRECTORY_DOWNLOADS) + "/Report" +   calendar.getTimeInMillis() + "_.xlsx");
//    byte[] pdfAsBytes = Base64.decode(base64PDf.replaceFirst("data:application/vnd.openxmlformats-officedocument.spreadsheetml.sheet;base64,", ""), 0);
//    Log.e("bytearrya",""+pdfAsBytes);
//    FileOutputStream os;
//    os = new FileOutputStream(dwldsPath, false);
//    os.write(pdfAsBytes);
//    os.flush();
//    os.close();
//
//    if(dwldsPath.exists()) {
////      sendNotification();
//      Toast.makeText(context, "sendNotification sendNotification", Toast.LENGTH_SHORT).show();
//
//      File dir = new File(Environment.getExternalStoragePublicDirectory(
//        Environment.DIRECTORY_DOWNLOADS) + "/Report" +
//        calendar.getTimeInMillis() + "_.xlsx");
//      Intent sendIntent = new Intent(Intent.ACTION_VIEW);
//      String path=   dir.getAbsolutePath();
//
//      Uri uri;
//      if (Build.VERSION.SDK_INT < 24) {
//        uri = Uri.fromFile(dir);
//      } else {
//        File file = new File(path);
//        uri = FileProvider.getUriForFile(this.context,
//          this.context.getApplicationContext().getPackageName() + ".provider", file);
////                    uri = Uri.parse("file://" + dir);
//      }
//
//      sendIntent.setDataAndType(uri, "application/vnd.ms-excel");
//      sendIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//      sendIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
//      try{
//        context.startActivity(sendIntent);
//
//      }catch (Exception e){
//        Toast.makeText(context, "Np app found to view file", Toast.LENGTH_SHORT).show();
//      }
//
//    }
//
//  }
//}
