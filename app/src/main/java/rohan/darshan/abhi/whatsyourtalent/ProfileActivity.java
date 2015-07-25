package rohan.darshan.abhi.whatsyourtalent;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;


public class ProfileActivity extends ActionBarActivity {

    ImageView dp;
    TextView UserNameTv, facebookTv, TwitterTv, WebsiteTv;
    String FACEBOOK = "", TWITTER = "", WEBSITE = "", IMAGE_URL = "";
    String displayFB = "", displayTWITTER = "", displayUSER = "", displayWEBSITE = "";
    RecyclerView recyclerView;
    MyAdapter darshan;
    //    String ProfileUrl = "http://bitsmate.in/videoupload/profile_videos.php?email=abhikori1994@gmail.com";
    String EMAIL_ID;

    public ArrayList<String> Titles = new ArrayList<>();
    public ArrayList<String> UserNames = new ArrayList<>();
    public ArrayList<String> Categories = new ArrayList<>();
    public ArrayList<String> Views = new ArrayList<>();
    public ArrayList<String> Likes = new ArrayList<>();
    public ArrayList<String> ImageUrls = new ArrayList<>();
    public ArrayList<String> Description = new ArrayList<>();
    public ArrayList<String> VideoId = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        SharedPreferences prefs = getSharedPreferences("SignIn", MODE_APPEND);
        Intent in = getIntent();
        int which = in.getIntExtra("whichAct", 0);
        if (which == 1) {
            EMAIL_ID = in.getStringExtra("email");
        } else {
            EMAIL_ID = prefs.getString("email", "email");
        }

        init();

        if (which == 1) {
            getProfileInfo();
        } else {
            displayUSER = prefs.getString("user", "");
            displayFB = prefs.getString("fb", "");
            displayTWITTER = prefs.getString("twitter", "");
            displayWEBSITE = prefs.getString("web", "");

            if (displayFB.equals("")) {
                facebookTv.setVisibility(View.GONE);
            }
            if (displayTWITTER.equals("")) {
                TwitterTv.setVisibility(View.GONE);
            }
            if (displayWEBSITE.equals("")) {
                WebsiteTv.setVisibility(View.GONE);
            }

        }
        new fetchProfileData().execute("");

        {
            UserNameTv.setText(displayUSER);
            facebookTv.setText(displayFB);
            TwitterTv.setText(displayTWITTER);
            WebsiteTv.setText(displayWEBSITE);
        }

    }

    public static final String TAG = "DARSHANROHANABHI";

    private void getProfileInfo() {
        Log.d(TAG, "getProfileInfo Called");
        new info().execute();
    }

    private void init() {
        dp = (ImageView) findViewById(R.id.dpIv);
        UserNameTv = (TextView) findViewById(R.id.UserNameTv);
        facebookTv = (TextView) findViewById(R.id.fbTv);
        TwitterTv = (TextView) findViewById(R.id.twitterTv);
        WebsiteTv = (TextView) findViewById(R.id.websiteTv);
        recyclerView = (RecyclerView) findViewById(R.id.ProfileRecyclerView);
        recyclerView.setHasFixedSize(true);
        final RecyclerView.LayoutManager llm = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(llm);
    }


    public class fetchProfileData extends AsyncTask<String, Integer, Boolean> {


        @Override
        protected Boolean doInBackground(String... params) {

            try {
                Log.d("DARSHANROHANABHI", "fetching started");
                HttpClient httpClient = new DefaultHttpClient();
                HttpPost httpPost = new HttpPost("http://bitsmate.in/videoupload/profile_videos.php?email=" + EMAIL_ID);
                HttpResponse response = null;
                try {
                    response = httpClient.execute(httpPost);
                } catch (IOException e) {
                    e.printStackTrace();
                    return false;
                }

                int status = response.getStatusLine().getStatusCode();
                Log.i("DARSHANROHANABHI", "" + status);

                if (status == 200) {
                    HttpEntity entity = response.getEntity();
                    String data = EntityUtils.toString(entity);
                    Log.i("DARSHANROHANABHI", "" + data);
                    JSONObject object = new JSONObject(data);
                    int length = object.length();
                    for (int i = 0; i < object.length(); i++) {
                        JSONObject insideObject = object.getJSONObject("" + i);
//                        Log.i("darsh", "" + insideObject);

                        String title = insideObject.getString(TimeLineActivity.TITLE);
                        String category = insideObject.getString(TimeLineActivity.CATEGORY);
                        String name = insideObject.getString(TimeLineActivity.USER);
                        String views = insideObject.getString(TimeLineActivity.VIEWS);
//                        String imageUrl = insideObject.getString(TimeLineActivity.IMAGE_URL);
                        String description = insideObject.getString(TimeLineActivity.DESCRIPTION);
                        String likes = insideObject.getString(TimeLineActivity.LIKES);
                        String url = insideObject.getString(TimeLineActivity.VIDEO_ID);

                        Titles.add(title);
                        Log.d(UploadService.TAG, "" + Titles);
                        UserNames.add(name);
                        Views.add(views);
//                        ImageUrls.add(imageUrl);
                        Categories.add(category);
                        Likes.add(likes);
                        Description.add(description);
                        VideoId.add(url);
                        publishProgress(i);
                    }
                    return true;

                }

            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return false;
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            if (aBoolean == false) {
                Toast.makeText(ProfileActivity.this, "Network error,please check your Internet connection", Toast.LENGTH_SHORT).show();
                return;
            }
            darshan = new MyAdapter(UserNames, Titles, Categories, Views, Likes, ImageUrls, Description, VideoId);
            recyclerView.setAdapter(darshan);
            super.onPostExecute(aBoolean);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }
    }

    public class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyViewHolder> {

        public class MyViewHolder extends RecyclerView.ViewHolder {
            protected TextView title, category, user, views, likes;
            protected ImageView thumbnail;
            protected ProgressBar pbSmall;


            public MyViewHolder(View itemView) {
                super(itemView);
                title = (TextView) itemView.findViewById(R.id.title);
                category = (TextView) itemView.findViewById(R.id.category);
                user = (TextView) itemView.findViewById(R.id.userName);
                views = (TextView) itemView.findViewById(R.id.views);
                likes = (TextView) itemView.findViewById(R.id.likes);
                thumbnail = (ImageView) itemView.findViewById(R.id.videoThumbnail);
                pbSmall = (ProgressBar) itemView.findViewById(R.id.progressBarSmall);

            }
        }

        ArrayList<String> Titles = new ArrayList<>();
        ArrayList<String> UserNames = new ArrayList<>();
        ArrayList<String> Categories = new ArrayList<>();
        ArrayList<String> Views = new ArrayList<>();
        ArrayList<String> Likes = new ArrayList<>();
        ArrayList<String> ImageUrls = new ArrayList<>();
        ArrayList<String> Description = new ArrayList<>();
        public ArrayList<String> VideoId = new ArrayList<>();


        public MyAdapter(ArrayList<String> userNames,
                         ArrayList<String> titles, ArrayList<String> categories,
                         ArrayList<String> views, ArrayList<String> likes, ArrayList<String> imageUrls, ArrayList<String> description, ArrayList<String> videoID) {
            UserNames = userNames;
            Titles = titles;
            Categories = categories;
            Views = views;
            Likes = likes;
            ImageUrls = imageUrls;
            Description = description;
            VideoId = videoID;
        }

        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            View itemView = LayoutInflater.
                    from(viewGroup.getContext()).
                    inflate(R.layout.single_card_view, viewGroup, false);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int n = recyclerView.getChildPosition(v);


                    Intent in = new Intent(ProfileActivity.this, VideoViewActivity.class);
                    Bundle videoBundle = new Bundle();
                    videoBundle.putString(TimeLineActivity.VIDEO_TITLE, Titles.get(n));
                    videoBundle.putString(TimeLineActivity.VIDEO_DESCRIPTION, Description.get(n));
                    videoBundle.putString(TimeLineActivity.VIDEO_CATEGORY, Categories.get(n));
//                    videoBundle.putString(TimeLineActivity.VIDEO_IMAGE_URL, ImageUrls.get(n));
                    videoBundle.putString(TimeLineActivity.VIDEO_LIKES, Likes.get(n));
                    videoBundle.putString(TimeLineActivity.VIDEO_USER, UserNames.get(n));
                    videoBundle.putString(TimeLineActivity.VIDEO_VIEWS, Views.get(n));
                    videoBundle.putString(TimeLineActivity.VIDEO_ID, VideoId.get(n));
                   /* if (selectedBitmap!=null&&selectedBitmap[n] != null)
                        finalBitmap = selectedBitmap[n];
                    else
                        check = -1;*/


                    in.putExtra(TimeLineActivity.VIDEO_BUNDLE, videoBundle);
                    startActivity(in);
                }
            });


            return new MyViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(MyViewHolder myViewHolder, int i) {
            myViewHolder.title.setText(Titles.get(i));
            myViewHolder.category.setText(Categories.get(i));
            myViewHolder.user.setText(UserNames.get(i));
            myViewHolder.views.setText(Views.get(i));
            myViewHolder.likes.setText(Likes.get(i));
            new downloadImageTask1(myViewHolder.thumbnail, myViewHolder.pbSmall).execute("");//ImageUrls.get(i)
            setAnimation(myViewHolder.itemView, i);
        }

        private void setAnimation(View viewToAnimate, int position) {
            // If the bound view wasn't previously displayed on screen, it's animated
            int lastPosition = 0;
            if (position > lastPosition) {
                Animation animation = AnimationUtils.loadAnimation(getApplicationContext(), android.R.anim.slide_in_left);
                viewToAnimate.startAnimation(animation);
                lastPosition = position;
            }
        }

        @Override
        public int getItemCount() {
            return Titles.size();
        }
    }

    public class downloadImageTask1 extends AsyncTask<String, Void, Bitmap> {

        ImageView bitmapImage;
        ProgressBar pb;

        @Override
        protected void onPreExecute() {
            pb.setVisibility(View.VISIBLE);
        }

        public downloadImageTask1(ImageView bitmapImage, ProgressBar pbSmall) {
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

    public class info extends AsyncTask<Void, Void, Bitmap> {

        @Override
        protected Bitmap doInBackground(Void... params) {

            Bitmap bitmap = null;

            Log.d(TAG, "getProfileInfo background Called");


            HttpClient httpClient = new DefaultHttpClient();
            HttpPost httpPost = new HttpPost("http://bitsmate.in/videoupload/profile.php?email_id=" + EMAIL_ID);
            try {
                HttpResponse response = httpClient.execute(httpPost);
                HttpEntity entity = response.getEntity();
                String jsonData = EntityUtils.toString(entity);
                Log.d("DARSHANROHANABHI", jsonData);

                JSONObject object = new JSONObject(jsonData);
                JSONObject insideObject = object.getJSONObject("");
                displayFB = insideObject.getString(FACEBOOK);
                displayTWITTER = insideObject.getString(TWITTER);
                displayWEBSITE = insideObject.getString(WEBSITE);
                String dpUrl = insideObject.getString(IMAGE_URL);

                InputStream in = new URL(dpUrl).openStream();
                bitmap = BitmapFactory.decodeStream(in);

            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return bitmap;
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            dp.setImageBitmap(bitmap);
            if (displayFB.equals("")) {
                facebookTv.setVisibility(View.GONE);
            }
            if (displayTWITTER.equals("")) {
                TwitterTv.setVisibility(View.GONE);
            }
            if (displayWEBSITE.equals("")) {
                WebsiteTv.setVisibility(View.GONE);
            }
            super.onPostExecute(bitmap);
        }
    }

}
