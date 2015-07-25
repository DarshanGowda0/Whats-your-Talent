package rohan.darshan.abhi.whatsyourtalent;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.ActionBarActivity;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.nineoldandroids.animation.Animator;

import java.io.ByteArrayOutputStream;


public class UploadActivity extends ActionBarActivity implements View.OnClickListener {

    private static final int ACTION_TAKE_VIDEO = 1;
    private static final int ACTION_SELECT_VIDEO = 2;
    ImageView record, attach;
    EditText title, description;
    Button category, Upload;
    Bitmap bitmap;
    static int serverResponseCode;
    private Uri fileUri;
    private static String FILE_PATH;
    public static String titleString;
    public static String descriptionString;
    public static String categoryString;
    public String[] categoryItems = {"Dance", "Music", "Sports"};
    public static final int MEDIA_TYPE_VIDEO = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload);
        record = (ImageView) findViewById(R.id.recordVideo);
        attach = (ImageView) findViewById(R.id.attachVideo);
        record.setOnClickListener(this);
        attach.setOnClickListener(this);
        title = (EditText) findViewById(R.id.titleEditText);
        description = (EditText) findViewById(R.id.descriptionEditText);
        category = (Button) findViewById(R.id.selectCategoriesButton);
        Upload = (Button) findViewById(R.id.uploadButton);
        description = (EditText) findViewById(R.id.descriptionEditText);
        category.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               /* Intent intent = new Intent(UploadActivity.this, SignIn.class);
                startActivity(intent);*/

                callCategoryButton();

            }
        });
        Upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                titleString = title.getText().toString();
                descriptionString = description.getText().toString();
                Log.d("darshan", titleString + " " + descriptionString + " " + FILE_PATH);
                String passBitmapString = passBit(bitmap);
                if (titleString.equals(null) || titleString.equals("")) {
                    Toast.makeText(UploadActivity.this, "Please enter a Title", Toast.LENGTH_SHORT).show();
                } else if ((descriptionString.equals(null) || descriptionString.equals(""))) {
                    Toast.makeText(UploadActivity.this, "Please enter a Description", Toast.LENGTH_SHORT).show();
                } else if ((FILE_PATH.equals(null) || FILE_PATH.equals(""))) {
                    Toast.makeText(UploadActivity.this, "Please record or select a video", Toast.LENGTH_SHORT).show();

                } else {
                    Intent in = new Intent(UploadActivity.this, UploadService.class);
                    in.putExtra("URL", FILE_PATH);
                    in.putExtra("title", titleString);
                    in.putExtra("description", descriptionString);
                    in.putExtra("category", categoryString);
                    in.putExtra("bitmapValue", passBitmapString);
                    startService(in);
                    Log.d(UploadService.TAG, "Upload started");
                }

            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_upload, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.attachVideo:
                YoYo.with(Techniques.Tada).withListener(new Animator.AnimatorListener() {
                    @Override
                    public void onAnimationStart(Animator animation) {

                    }

                    @Override
                    public void onAnimationEnd(Animator animation) {
                        Intent in = new Intent(Intent.ACTION_PICK);
                        in.setType("video/*");
                        startActivityForResult(in, ACTION_SELECT_VIDEO);
                    }

                    @Override
                    public void onAnimationCancel(Animator animation) {

                    }

                    @Override
                    public void onAnimationRepeat(Animator animation) {

                    }
                }).duration(500).playOn(v);

                break;
            case R.id.recordVideo:
                YoYo.with(Techniques.Tada).withListener(new Animator.AnimatorListener() {
                    @Override
                    public void onAnimationStart(Animator animation) {

                    }

                    @Override
                    public void onAnimationEnd(Animator animation) {
                        Intent i = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
                        startActivityForResult(i, ACTION_TAKE_VIDEO);
                    }

                    @Override
                    public void onAnimationCancel(Animator animation) {

                    }

                    @Override
                    public void onAnimationRepeat(Animator animation) {

                    }
                }).duration(500).playOn(v);

                break;

        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        String videoPath = "";
        switch (requestCode) {
            case ACTION_TAKE_VIDEO:
                if (resultCode == RESULT_OK) {
                    Uri videoUri = data.getData();
//                    FILE_PATH = videoUri.toString();
                    videoPath = getRealPathFromURI(videoUri);
                    FILE_PATH = videoPath;
                    bitmap = thumbNail(FILE_PATH);
                    Toast.makeText(this, "" + FILE_PATH, Toast.LENGTH_LONG).show();

                }
                break;
            case ACTION_SELECT_VIDEO:
                if (resultCode == RESULT_OK) {
                    Uri selectedVideo = data.getData();
                    videoPath = getRealPathFromURI(selectedVideo);
                    FILE_PATH = videoPath;
                    bitmap = thumbNail(FILE_PATH);
                    Toast.makeText(this, "" + videoPath, Toast.LENGTH_LONG).show();
                }
                break;

        }
    }

    public String getRealPathFromURI(Uri contentUri) {
        String[] proj = {MediaStore.Images.Media.DATA};
        Cursor cursor = managedQuery(contentUri, proj, null, null, null);
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DATA);
        cursor.moveToFirst();
        return cursor.getString(column_index);
    }


    /*public static int upLoad2Server(String sourceFileUri) {
        Log.i("darsh", "uploadCalled");
        String upLoadServerUri = "http://bitsmate.in/videoupload/upload_video.php";
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
            Log.e("Huzza", "Source File Does not exist");
            return 0;
        }
        try { // open a URL connection to the Servlet
            FileInputStream fileInputStream = new FileInputStream(sourceFile);
            URL url = new URL(upLoadServerUri);
            conn = (HttpURLConnection) url.openConnection(); // Open a HTTP  connection to  the URL
            conn.setDoInput(true); // Allow Inputs
            conn.setDoOutput(true); // Allow Outputs
            conn.setUseCaches(false); // Don't use a Cached Copy
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Connection", "Keep-Alive");
            conn.setRequestProperty("ENCTYPE", "multipart/form-data");
            conn.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary);
            conn.setRequestProperty("uploaded_file", fileName);
            dos = new DataOutputStream(conn.getOutputStream());

            dos.writeBytes(twoHyphens + boundary + lineEnd);
            dos.writeBytes("Content-Disposition: form-data; name=\"uploaded_file\";filename=\"" + fileName + "$" + titleString + "$"
                    + descriptionString + "$255" + "\"" + lineEnd);
            dos.writeBytes(lineEnd);


            bytesAvailable = fileInputStream.available(); // create a buffer of  maximum size
            Log.i("Huzza", "Initial .available : " + bytesAvailable);

            bufferSize = Math.min(bytesAvailable, maxBufferSize);
            buffer = new byte[bufferSize];

            // read file and write it into form...
            bytesRead = fileInputStream.read(buffer, 0, bufferSize);

            while (bytesRead > 0) {
                dos.write(buffer, 0, bufferSize);
                bytesAvailable = fileInputStream.available();
                bufferSize = Math.min(bytesAvailable, maxBufferSize);
                bytesRead = fileInputStream.read(buffer, 0, bufferSize);
            }

            // send multipart form data necesssary after file data...
            dos.writeBytes(lineEnd);
            dos.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);

            // Responses from the server (code and message)
            serverResponseCode = conn.getResponseCode();
            String serverResponseMessage = conn.getResponseMessage();

            Log.i("Upload file to server", "HTTP Response is : " + serverResponseMessage + ": " + serverResponseCode);
            // close streams
            Log.i("Upload file to server", fileName + " File is written");
            fileInputStream.close();
            dos.flush();
            dos.close();
        } catch (MalformedURLException ex) {
            ex.printStackTrace();
            Log.e("Upload file to server", "error: " + ex.getMessage(), ex);
        } catch (Exception e) {
            e.printStackTrace();
        }
//this block will give the response of upload link
        try {
            BufferedReader rd = new BufferedReader(new InputStreamReader(conn
                    .getInputStream()));
            String line;
            while ((line = rd.readLine()) != null) {
                Log.i("Huzza", "RES Message: " + line);
            }
            rd.close();
        } catch (IOException ioex) {
            Log.e("Huzza", "error: " + ioex.getMessage(), ioex);
        }
        return serverResponseCode;  // like 200 (Ok)

    } // end upLoad2Server
*/

    /*class test extends AsyncTask<String, Void, Integer> {

        @Override
        protected Integer doInBackground(String... params) {


            int success = upLoad2Server(params[0]);
            Log.e("darshan", "" + success);


            return success;
        }


    }*/

    public String passBit(Bitmap bm) {
        String ba1 = null;
        try {
            ByteArrayOutputStream bao = new ByteArrayOutputStream();
            bm.compress(Bitmap.CompressFormat.JPEG, 90, bao);
            byte[] ba = bao.toByteArray();
            ba1 = Base64.encodeToString(ba, Base64.DEFAULT);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return ba1;

    }

    private void callCategoryButton() {

        new AlertDialog.Builder(this).setTitle("Select a Category")
                .setSingleChoiceItems(categoryItems, 1, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        categoryString = categoryItems[which];
                    }
                })
                .setPositiveButton("Okay", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                }).show();

    }

    public Bitmap thumbNail(String path) {
        Bitmap mBitmap;
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        retriever.setDataSource(path);
        mBitmap = retriever.getFrameAtTime(5);
        return mBitmap;

    }
}

