package rohan.darshan.abhi.whatsyourtalent;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
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
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.nineoldandroids.animation.Animator;
import com.oguzdev.circularfloatingactionmenu.library.FloatingActionButton;
import com.oguzdev.circularfloatingactionmenu.library.FloatingActionMenu;
import com.oguzdev.circularfloatingactionmenu.library.SubActionButton;

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


public class TimeLineActivity extends ActionBarActivity {


    public static String LIKE_STATUS;
    public Boolean abool;


    public static final String TITLE = "title", USER = "uploader_name",
            CATEGORY = "cat", VIEWS = "views", LIKES = "likes", EMAIL_ID = "email", DESCRIPTION = "desc", IMAGE_URL = "thumbnail_img", VIDEO_ID = "url";
    public static final String VIDEO_BUNDLE = "videoIntent", VIDEO_TITLE = "videoTitle", VIDEO_USER = "videoUser", VIDEO_CATEGORY = "videoCategory", VIDEO_VIEWS = "videoViews", VIDEO_LIKES = "videoLikes", VIDEO_DESCRIPTION = "videoDescription", VIDEO_IMAGE_URL = "videoImageUrl";
    android.support.v7.app.ActionBar ab;/*
    public static Bitmap[] selectedBitmap = new Bitmap[];
    public static Bitmap finalBitmap;
    public static int count = 0, check;*/
    public ArrayList<String> Titles = new ArrayList<>();
    public ArrayList<String> UserNames = new ArrayList<>();
    public ArrayList<String> Categories = new ArrayList<>();
    public ArrayList<String> Views = new ArrayList<>();
    public ArrayList<String> Likes = new ArrayList<>();
    public ArrayList<String> ImageUrls = new ArrayList<>();
    public ArrayList<String> Description = new ArrayList<>();
    public ArrayList<String> VideoId = new ArrayList<>();
    public ArrayList<String> EmailId = new ArrayList<>();
    public ProgressBar circlePB;
    public RecyclerView recList;
    RelativeLayout searchLayout;
    Button searchButton;
    EditText searchEt;
    FloatingActionMenu actionMenu;
    public static boolean searchDone = false;
    MyAdapter rohan;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_time_line);
        circlePB = (ProgressBar) findViewById(R.id.progressBarCircle);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayUseLogoEnabled(true);
//        getSupportActionBar().setLogo(R.drawable.test);

//        ab = getSupportActionBar();
       /* Button btn = (Button) findViewById(R.id.temp);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });*/
        Log.d("darshan", "" + UserNames);

        recList = (RecyclerView) findViewById(R.id.recyclerView);
        recList.setHasFixedSize(true);
        final RecyclerView.LayoutManager llm = new LinearLayoutManager(this);
//        llm.setOrientation(LinearLayoutManager.VERTICAL);
        recList.setLayoutManager(llm);
//        recList.setItemAnimator(null);

        new fetchData().execute("http://bitsmate.in/videoupload/newsfeed.php");
        Log.d("darshan", "" + UserNames);

        searchLayout = (RelativeLayout) findViewById(R.id.searchLayout);
        searchButton = (Button) findViewById(R.id.searchButton);
        searchEt = (EditText) findViewById(R.id.searchEditText);
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String searchKey = searchEt.getText().toString();
                if (searchKey.equals("") || searchKey.equals(null)) {
                    Toast.makeText(TimeLineActivity.this, "Please enter a word to search ", Toast.LENGTH_SHORT).show();
                } else {
                    clearArrays();
                    searchDone = true;
                    new fetchData().execute("http://bitsmate.in/videoupload/search.php?key=" + searchKey);
                }

            }
        });
//        recList.addOnItemTouchListener();

    }

    private void clearArrays() {
        Titles.clear();
        UserNames.clear();
        Views.clear();
        ImageUrls.clear();
        Categories.clear();
        Likes.clear();
        Description.clear();
        VideoId.clear();
    }

    private void floatingButton() {

        Drawable drawable = getResources().getDrawable(R.drawable.ic_action_add);

        ImageView icon = new ImageView(this); // Create an icon
        icon.setImageResource(R.drawable.settings);

        final FloatingActionButton actionButton = new FloatingActionButton.Builder(this)
                .setContentView(icon)
                .build();

//        actionButton.setBackgroundDrawable(drawable);

        SubActionButton.Builder itemBuilder = new SubActionButton.Builder(this);
// repeat many times:
        Drawable uploadDrawable = getResources().getDrawable(R.drawable.upload);
        ImageView itemUploadIcon = new ImageView(this);
//        itemUploadIcon.setImageDrawable(uploadDrawable);

        SubActionButton UploadButton = itemBuilder.setContentView(itemUploadIcon).build();
        UploadButton.setBackgroundDrawable(uploadDrawable);


//        button1.setBackground();

        Drawable searchDrawable = getResources().getDrawable(R.drawable.search);
        ImageView itemSearchIcon = new ImageView(this);
//        itemSearchIcon.setImageDrawable(searchDrawable);
        SubActionButton searchButton = itemBuilder.setContentView(itemSearchIcon).build();
        searchButton.setBackgroundDrawable(searchDrawable);

        Drawable userDrawable = getResources().getDrawable(R.drawable.profle);
        ImageView itemUserIcon = new ImageView(this);
//        itemSearchIcon.setImageDrawable(userDrawable);
        SubActionButton userButton = itemBuilder.setContentView(itemUserIcon).build();
        userButton.setBackgroundDrawable(userDrawable);

        YoYo.with(Techniques.BounceInDown).duration(500).playOn(actionButton);


        actionMenu = new FloatingActionMenu.Builder(this)
                .addSubActionView(UploadButton)
                .addSubActionView(searchButton)
                .addSubActionView(userButton)
                .attachTo(actionButton)

                .build();


        UploadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                YoYo.with(Techniques.SlideOutLeft).duration(500).withListener(new Animator.AnimatorListener() {
                    @Override
                    public void onAnimationStart(Animator animation) {
                        if (actionMenu.isOpen())
                            actionMenu.close(true);
                    }

                    @Override
                    public void onAnimationEnd(Animator animation) {
                        Intent i = new Intent(TimeLineActivity.this, UploadActivity.class);

                        overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
                        startActivity(i);
                    }

                    @Override
                    public void onAnimationCancel(Animator animation) {

                    }

                    @Override
                    public void onAnimationRepeat(Animator animation) {

                    }
                }).playOn(actionButton);

            }
        });

        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                actionMenu.close(true);
                searchLayout.setVisibility(View.VISIBLE);
                YoYo.with(Techniques.SlideInUp).duration(500).playOn(searchLayout);
            }
        });

        userButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                YoYo.with(Techniques.SlideOutLeft).duration(500).withListener(new Animator.AnimatorListener() {
                    @Override
                    public void onAnimationStart(Animator animation) {
                        if (actionMenu.isOpen())
                            actionMenu.close(true);
                    }

                    @Override
                    public void onAnimationEnd(Animator animation) {
                        Intent i = new Intent(TimeLineActivity.this, ProfileActivity.class);
                        i.putExtra("whichAct", 0);
                        overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
                        startActivity(i);
                    }

                    @Override
                    public void onAnimationCancel(Animator animation) {

                    }

                    @Override
                    public void onAnimationRepeat(Animator animation) {

                    }
                }).playOn(actionButton);

            }
        });
    }

   /* private List<sample> creatList(int i) {
        List<sample> result = new ArrayList<sample>();
        for (int x = 0; x < i; x++) {
            sample a = new sample();
            a.title = Titles.get(x);
            a.category = Categories.get(x);
            a.user = UserNames.get(x);
            a.views = Views.get(x);
            a.likes = Likes.get(x);
            a.imageUrl = ImageUrls.get(x);
            result.add(a);
        }
        return result;
    }*/

    /* public class sample {
         String title, category, views, likes, user, imageUrl;
     }
 */


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
        ArrayList<String> VideoId = new ArrayList<>();
        ArrayList<String> EmailId = new ArrayList<>();

        public MyAdapter(ArrayList<String> userNames,
                         ArrayList<String> titles, ArrayList<String> categories,
                         ArrayList<String> views, ArrayList<String> likes, ArrayList<String> imageUrls, ArrayList<String> description, ArrayList<String> videoID, ArrayList<String> emailId) {
            UserNames = userNames;
            Titles = titles;
            Categories = categories;
            Views = views;
            Likes = likes;
            ImageUrls = imageUrls;
            Description = description;
            VideoId = videoID;
            EmailId = emailId;
        }

        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            View itemView = LayoutInflater.
                    from(viewGroup.getContext()).
                    inflate(R.layout.single_card_view, viewGroup, false);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int n = recList.getChildPosition(v);

                    /*getLikeStatus obj = new getLikeStatus(VideoId.get(n), EMAIL_ID);
//                    try {
////                        obj.wait(1000);
//                    } catch (InterruptedException e) {
//                        e.printStackTrace();
//                    }
                    obj.execute();*/

                    Intent in = new Intent(TimeLineActivity.this, VideoViewActivity.class);
                    Bundle videoBundle = new Bundle();
                    videoBundle.putString(VIDEO_TITLE, Titles.get(n));
                    videoBundle.putString(VIDEO_DESCRIPTION, Description.get(n));
                    videoBundle.putString(VIDEO_CATEGORY, Categories.get(n));
//                    videoBundle.putString(VIDEO_IMAGE_URL, ImageUrls.get(n));
                    videoBundle.putString(VIDEO_LIKES, Likes.get(n));
                    videoBundle.putString(VIDEO_USER, UserNames.get(n));
                    videoBundle.putString(VIDEO_VIEWS, Views.get(n));
                    videoBundle.putString(VIDEO_ID, VideoId.get(n));
                    videoBundle.putString(EMAIL_ID, EmailId.get(n));
                   /* if (selectedBitmap!=null&&selectedBitmap[n] != null)
                        finalBitmap = selectedBitmap[n];
                    else
                        check = -1;*/


                    in.putExtra(VIDEO_BUNDLE, videoBundle);
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
            new downloadImageTask(myViewHolder.thumbnail, myViewHolder.pbSmall).execute("");//ImageUrls.get(i)
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

    public class fetchData extends AsyncTask<String, Integer, Boolean> {
        int length;

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            abool = aBoolean;
            if (abool == false) {
                Toast.makeText(TimeLineActivity.this, "Network error,please check your Internet connection", Toast.LENGTH_SHORT).show();
                return;
            }
            if (searchDone) {
                rohan.notifyDataSetChanged();
                YoYo.with(Techniques.SlideOutDown).duration(500).withListener(new Animator.AnimatorListener() {
                    @Override
                    public void onAnimationStart(Animator animation) {

                    }

                    @Override
                    public void onAnimationEnd(Animator animation) {
                        searchLayout.setVisibility(View.GONE);

                    }

                    @Override
                    public void onAnimationCancel(Animator animation) {

                    }

                    @Override
                    public void onAnimationRepeat(Animator animation) {

                    }
                }).playOn(searchLayout);

            } else {
                rohan = new MyAdapter(UserNames, Titles, Categories, Views, Likes, ImageUrls, Description, VideoId, EmailId);
            recList.setAdapter(rohan);
        }
            circlePB.setVisibility(View.GONE);
        }

        @Override
        protected void onPreExecute() {
            circlePB.setVisibility(View.VISIBLE);
            super.onPreExecute();
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
            if (values[0] == -1) {
                Toast.makeText(getApplicationContext(), "Network Error, Please check your data connection", Toast.LENGTH_SHORT).show();
            }

        }

        @Override
        protected Boolean doInBackground(String... params) {


            try {

                HttpClient httpClient = new DefaultHttpClient();
                HttpPost httpPost = new HttpPost(params[0]);
                HttpResponse response = null;
                try {
                    response = httpClient.execute(httpPost);
                } catch (IOException e) {
                    e.printStackTrace();
                    return false;
                }

                int status = response.getStatusLine().getStatusCode();
                if (status == 200) {
                    HttpEntity entity = response.getEntity();
                    String data = EntityUtils.toString(entity);
                    Log.i("darshan", "" + data);
                    JSONObject object = new JSONObject(data);
                    length = object.length();
                    for (int i = 0; i < object.length(); i++) {
                        JSONObject insideObject = object.getJSONObject("" + i);
//                        Log.i("darsh", "" + insideObject);

                        String title = insideObject.getString(TITLE);
                        String category = insideObject.getString(CATEGORY);
                        String name = insideObject.getString(USER);
                        String views = insideObject.getString(VIEWS);
//                        String imageUrl = insideObject.getString(IMAGE_URL);
                        String description = insideObject.getString(DESCRIPTION);
                        String likes = insideObject.getString(LIKES);
                        String url = insideObject.getString(VIDEO_ID);
                        String email = insideObject.getString(EMAIL_ID);

                        Titles.add(title);
                        Log.d(UploadService.TAG, "" + Titles);
                        UserNames.add(name);
                        Views.add(views);
//                        ImageUrls.add(imageUrl);
                        Categories.add(category);
                        Likes.add(likes);
                        Description.add(description);
                        VideoId.add(url);
                        EmailId.add(email);
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
//            selectedBitmap.add(bitmap);
//            count++;
        }
    }

    @Override
    protected void onResume() {

        super.onResume();
        floatingButton();

    }

    @Override
    public void onBackPressed() {
        if (searchLayout.getVisibility() == View.VISIBLE) {
            YoYo.with(Techniques.SlideOutDown).duration(500).withListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animation) {

                }

                @Override
                public void onAnimationEnd(Animator animation) {
                    searchLayout.setVisibility(View.GONE);

                }

                @Override
                public void onAnimationCancel(Animator animation) {

                }

                @Override
                public void onAnimationRepeat(Animator animation) {

                }
            }).playOn(searchLayout);
        } else if (actionMenu.isOpen()) {
            actionMenu.close(true);
        } else
            super.onBackPressed();
    }


}
