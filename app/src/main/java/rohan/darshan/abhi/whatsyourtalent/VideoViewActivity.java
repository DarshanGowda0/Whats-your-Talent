package rohan.darshan.abhi.whatsyourtalent;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;


public class VideoViewActivity extends ActionBarActivity {

    String title, name, category, views, likes, description, imageurl, videoID, emailId;

    TextView titleTv, UploaderTv, CategoryTv, ViewsTv, LikeTv, DescriptionTv;
    static Button likeButton;
    ImageView thumbnail;
    ProgressBar pb;
    public static int VIEW, LIKE;
    int liked;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_view);
        getinfo();
        init();


    }

   /* public static void likeButtonCheck() {
        int status;

        if (!TimeLineActivity.LIKE_STATUS.equals("")) {
            status = Integer.parseInt(TimeLineActivity.LIKE_STATUS);
            if (status == 1) {
                likeButton.setBackgroundResource(R.drawable.star_gold);
            }
        }
    }*/

    private void init() {
        titleTv = (TextView) findViewById(R.id.titleTV);
        DescriptionTv = (TextView) findViewById(R.id.descriptionTV);
        UploaderTv = (TextView) findViewById(R.id.uploadedUserTV);
        CategoryTv = (TextView) findViewById(R.id.categoryTv);
        ViewsTv = (TextView) findViewById(R.id.viewsTV);
        LikeTv = (TextView) findViewById(R.id.likesTv);
        pb = (ProgressBar) findViewById(R.id.pb);
        thumbnail = (ImageView) findViewById(R.id.thumbnailIV);
        new downloadImageTask(thumbnail, pb).execute(imageurl);


        SharedPreferences prefs = getSharedPreferences("SignIn", MODE_APPEND);
        String current_email_id = prefs.getString("email", "email");


//        new getLikeStatus(videoID, current_email_id).execute();
        if (liked == 0) {
            likeButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    LIKE++;
                    LikeTv.setText("" + LIKE);
                    new LikesTask().execute();
                    likeButton.setBackgroundResource(R.drawable.star_gold);
                }
            });
        }
        thumbnail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                VIEW++;
                ViewsTv.setText("" + VIEW);
                new ViewsTask().execute();
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://bitsmate.in/videoupload/uploads/" + videoID));

                intent.setDataAndType(Uri.parse("http://bitsmate.in/videoupload/uploads/" + videoID), "video/*");

                startActivity(intent);

            }
        });
        titleTv.setText(title);
        DescriptionTv.setText(description);
        UploaderTv.setText(name);
        UploaderTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(VideoViewActivity.this, ProfileActivity.class);
                i.putExtra("email", emailId);
                i.putExtra("whichAct", 1);
                startActivity(i);
            }
        });
        CategoryTv.setText(category);
        ViewsTv.setText(views);
        LikeTv.setText(likes);


    }

    private void getinfo() {
        likeButton = (Button) findViewById(R.id.likeButton);
//      needed to initialise faster
        Intent in = getIntent();
        Bundle recievedBundle = in.getBundleExtra(TimeLineActivity.VIDEO_BUNDLE);
        title = recievedBundle.getString(TimeLineActivity.VIDEO_TITLE);
        name = recievedBundle.getString(TimeLineActivity.VIDEO_USER);
        category = recievedBundle.getString(TimeLineActivity.VIDEO_CATEGORY);
        views = recievedBundle.getString(TimeLineActivity.VIDEO_VIEWS);
        likes = recievedBundle.getString(TimeLineActivity.VIDEO_LIKES);
        description = recievedBundle.getString(TimeLineActivity.VIDEO_DESCRIPTION);
        imageurl = recievedBundle.getString(TimeLineActivity.VIDEO_IMAGE_URL);
        videoID = recievedBundle.getString(TimeLineActivity.VIDEO_ID);
        emailId = recievedBundle.getString(TimeLineActivity.EMAIL_ID);

        try {
            VIEW = Integer.parseInt(views);
            LIKE = Integer.parseInt(likes);
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
    }

    public class downloadImageTask extends AsyncTask<String, Void, Bitmap> {

        ImageView bitmapImage;
        ProgressBar pb;

        @Override
        protected void onPreExecute() {
            pb.setVisibility(View.VISIBLE);
        }

        public downloadImageTask(ImageView bitmapImage, ProgressBar pbSmall) {
            this.bitmapImage = bitmapImage;
            this.pb = pbSmall;
        }

        @Override
        protected Bitmap doInBackground(String... params) {

            String url = params[0];
            url = "http://upload.wikimedia.org/wikipedia/en/8/83/So_Oregon_Spartans_logo.png";
            Bitmap bitmap = null;

            try {
                InputStream in = new URL(url).openStream();
                bitmap = BitmapFactory.decodeStream(in);

            } catch (IOException e) {
                e.printStackTrace();
            }
            return bitmap;
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            bitmapImage.setImageBitmap(bitmap);
            pb.setVisibility(View.GONE);

        }
    }


    public class ViewsTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... params) {
            Log.d(UploadService.TAG, "likes and views sending");
            try {
                HttpClient httpClient = new DefaultHttpClient();
                HttpPost httpPost = new HttpPost("http://bitsmate.in/videoupload/update_likes.php?likes=" + VIEW + "&url=" + videoID + "");
                HttpResponse response = httpClient.execute(httpPost);
                HttpEntity httpEntity = response.getEntity();
                Log.d(UploadService.TAG, "views " + EntityUtils.toString(httpEntity));
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }
    }

    public class LikesTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... params) {
            Log.d(UploadService.TAG, "likes and views sending");
            try {
                HttpClient httpClient = new DefaultHttpClient();
                HttpPost httpPost = new HttpPost("http://bitsmate.in/videoupload/update_views.php?views=" + LIKE + "&url=" + videoID + "");
                HttpResponse response = httpClient.execute(httpPost);
                HttpEntity httpEntity = response.getEntity();
                Log.d(UploadService.TAG, "likes " + EntityUtils.toString(httpEntity));
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }
    }

    public class getLikeStatus extends AsyncTask<String, Void, String> {

        String vid_id, email_id;

        public getLikeStatus(String vid_id, String email_id) {
            this.vid_id = vid_id;
            this.email_id = email_id;
        }

        public static final String LIKE_STATUS_URL = "http://bitsmate.in/videoupload/video_view.php?v_id=65&mail=abhikori199@gmail.com";

        @Override
        protected String doInBackground(String... params) {
            String status = "";
            HttpClient httpClient = new DefaultHttpClient();
            HttpPost httpPost = new HttpPost("http://bitsmate.in/videoupload/video_view.php?v_id=" + vid_id + "&mail=" + email_id);
            try {
                HttpResponse response = httpClient.execute(httpPost);
                HttpEntity entity = response.getEntity();
                Log.d(UploadService.TAG, "" + EntityUtils.toString(entity));
                status = EntityUtils.toString(entity);
                return status;
            } catch (IOException e) {
                e.printStackTrace();
            }
            return status;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            liked = Integer.parseInt(s);
            likeButton.setVisibility(View.VISIBLE);
            LikeTv.setVisibility(View.VISIBLE);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            likeButton.setVisibility(View.GONE);
            LikeTv.setVisibility(View.GONE);
        }
    }
}
