package rohan.darshan.abhi.whatsyourtalent;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.RemoteViews;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;


public class UploadService extends IntentService {
    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     *
     * @param name Used to name the worker thread, important only for debugging.
     */

    public static int serverResponseCode;
    public static String TAG = "abhiDarshanRohan";
    public static final String thumbnail_SERVER = "http://bitsmate.in/videoupload/upload_thumbnail.php";
    public static String DESC, CATEGORY, EMAIL, TITLE, BITMAP;
    public static final String SERVER_URL = "http://bitsmate.in/videoupload/upload_video.php";
    NotificationManager notificationManager;
    Notification notification;
    static String line = "";


    public UploadService(String name) {
        super(name);
    }

    public UploadService() {
        super("UploadService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {


        String filepath;
        filepath = intent.getStringExtra("URL");
        DESC = intent.getStringExtra("title");
        TITLE = intent.getStringExtra("description");
        BITMAP = intent.getStringExtra("bitmapValue");
        EMAIL = "darshan.gowda";
        CATEGORY = intent.getStringExtra("category");
        Log.d("darshan", filepath);

        PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), 0, intent, 0);
        notification = new Notification(R.drawable.upload, "Upload", System.currentTimeMillis());
//       if the notification doesn't go then change the flag to Auto_cancel.. that ll make it go if they click on it or swipe it
        notification.flags = Notification.FLAG_AUTO_CANCEL;
        notification.contentView = new RemoteViews(getApplicationContext().getPackageName(), R.layout.upload_progressbar);
        notification.contentIntent = pendingIntent;
        notification.contentView.setTextViewText(R.id.textView, "Uploading...");
        notification.contentView.setImageViewResource(R.id.iconUpload, R.drawable.upload);
//        notification.contentView.setProgressBar(R.id.PB,100,0,false);
        getApplicationContext();
        notificationManager = (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
        // id=12350
        notificationManager.notify(12350, notification);


        int success = upLoad2Server(filepath);
        if (success == 200) {
            Log.d(TAG, "succesfully uploaded");
//            notification.contentView.setProgressBar(R.id.PB,100,bytesRead,false);
            notification.contentView.setTextViewText(R.id.textView, line);// "Successfully uploaded"
            notification.contentView.setImageViewResource(R.id.iconUpload, R.drawable.upload);
            notificationManager.notify(12350, notification);

        }


    }

    public static int upLoad2Server(String sourceFileUri) {
        Log.i(TAG, "uploadCalled");
        String upLoadServerUri = SERVER_URL;
        // String [] string = sourceFileUri;
        String fileName = sourceFileUri;

        HttpURLConnection conn = null;
        DataOutputStream dos = null;
        DataInputStream inStream = null;
        String lineEnd = "\r\n";
        String twoHyphens = "--";
        String boundary = "*****";
        int bytesRead, bytesAvailable, bufferSize;
        byte[] buffer;
        int maxBufferSize = 1 * 1024 * 1024;
        String responseFromServer = "";

        File sourceFile = new File(sourceFileUri);
        if (!sourceFile.isFile()) {
            Log.e(TAG, "Source File Does not exist");
            return 0;
        }
        try { // open a URL connection to the Servlet
            FileInputStream fileInputStream = new FileInputStream(sourceFile);
            URL url = new URL(upLoadServerUri);
            conn = (HttpURLConnection) url.openConnection(); // Open a HTTP  connection to  the URL
            conn.setDoInput(true); // Allow Inputs
            conn.setDoOutput(true); // Allow Outputs
            conn.setUseCaches(false); // Don't use a Cached Copy
            conn.setConnectTimeout(7000);
            conn.setChunkedStreamingMode(1024);
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Connection", "Keep-Alive");
            conn.setRequestProperty("ENCTYPE", "multipart/form-data");
            conn.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary);
            conn.setRequestProperty("uploaded_file", fileName);
            dos = new DataOutputStream(conn.getOutputStream());

            dos.writeBytes(twoHyphens + boundary + lineEnd);
            dos.writeBytes("Content-Disposition: form-data; name=\"uploaded_file\";filename=\"" + fileName + "$" + DESC + "$" + CATEGORY + "$" + EMAIL + "$" + TITLE + "$" + "\"" + lineEnd);
            dos.writeBytes(lineEnd);


            bytesAvailable = fileInputStream.available(); // create a buffer of  maximum size
            Log.i(TAG, "Initial .available : " + bytesAvailable);

            bufferSize = Math.min(bytesAvailable, maxBufferSize);
            buffer = new byte[bufferSize];

            // read file and write it into form...
            bytesRead = fileInputStream.read(buffer, 0, bufferSize);
            Log.d(TAG, "writing bytes " + bytesRead);

            while (bytesRead > 0) {
                dos.write(buffer, 0, bufferSize);
                bytesAvailable = fileInputStream.available();
                bufferSize = Math.min(bytesAvailable, maxBufferSize);
                bytesRead = fileInputStream.read(buffer, 0, bufferSize);
                Log.d(TAG, " " + bytesRead);
            }

            // send multipart form data necesssary after file data...
            dos.writeBytes(lineEnd);
            dos.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);

            // Responses from the server (code and message)
            serverResponseCode = conn.getResponseCode();
            String serverResponseMessage = conn.getResponseMessage();

            Log.i(TAG, "HTTP Response is : " + serverResponseMessage + ": " + serverResponseCode);
//            Toast.makeText(UploadActivity.class,"",Toast.LENGTH_SHORT).show();
            // close streams
            Log.i(TAG, fileName + " File is written");
            fileInputStream.close();
            dos.flush();
            dos.close();
        } catch (MalformedURLException ex) {
            ex.printStackTrace();
            Log.e(TAG, "error: " + ex.getMessage(), ex);
        } catch (Exception e) {
            e.printStackTrace();
        }
//this block will give the response of upload link
        try {
            BufferedReader rd = new BufferedReader(new InputStreamReader(conn
                    .getInputStream()));
            /*while ((line = rd.readLine()) != null) {
                Log.i(TAG, "RES Message: " + line);
            }*/
            line = rd.readLine();
            Log.d(TAG, "line is " + line);
            sendBitmap(BITMAP, line);
            rd.close();
        } catch (IOException ioex) {
            Log.e(TAG, "error: " + ioex.getMessage(), ioex);
        }
        return serverResponseCode;  // like 200 (Ok)

    } // end upLoad2Server


    public static void sendBitmap(String ba1, String videoId) {

        HttpContext localContext = new BasicHttpContext();
        Log.d(TAG, "sendingBitmap " + videoId);
        String temp = ba1 + "$dar$" + videoId;
        ArrayList<NameValuePair> nameValuePairs = new ArrayList<>();
        nameValuePairs.add(new BasicNameValuePair("image", ba1));
        nameValuePairs.add(new BasicNameValuePair("vid_id", videoId));

//        ArrayList<NameValuePair> nameValuePairs2 = new ArrayList<>();
//        nameValuePairs.add(new BasicNameValuePair("VideoId", videoId));

        try {


            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httppost = new
                    HttpPost(thumbnail_SERVER);


            MultipartEntity multipartEntity = new MultipartEntity(HttpMultipartMode.BROWSER_COMPATIBLE);
            multipartEntity.addPart(nameValuePairs.get(1).getName(), new StringBody(nameValuePairs.get(1).getValue()));
            multipartEntity.addPart(nameValuePairs.get(1).getName(), new StringBody(nameValuePairs.get(2).getValue()));

            httppost.setEntity(multipartEntity);
//            httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs2));


            HttpResponse response = httpclient.execute(httppost, localContext);
            HttpEntity entity = response.getEntity();
            Log.d(TAG, "" + EntityUtils.toString(entity));
//            is = entity.getContent();
            //Toast.makeText(SignUpActivity.this, "Joining Failed", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Log.e(TAG, "Error in http connection " + e.toString());
        }


    }

}
