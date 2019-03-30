package com.sportskeeda;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import io.realm.Realm;
import io.realm.annotations.Required;
import io.realm.exceptions.RealmException;

public class Splash extends AppCompatActivity {

    ArrayList<News> newsList = new ArrayList<>();
    Realm mRealm;
    int flagNextActivity = 0;
    Animation animation;
    ImageView logo_name;
//    String URL = this.getResources().getString(R.string.url);


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE); // Creating full screen activity for splash screen
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_splash);
        Realm.init(getApplicationContext());
        mRealm = Realm.getDefaultInstance();
        ProgressBar progressBar = findViewById(R.id.progress_bar);
        logo_name = findViewById(R.id.logo_id);
        logo_name.startAnimation(AnimationUtils.loadAnimation(this, R.anim.fast_fadein));

        // Initializing realm database. The news objects fetched will be stored into this database for quicker access next time.
//        Realm.init(getApplicationContext());
//        mRealm = Realm.getDefaultInstance();

        String url = getString(R.string.url);
        StringRequest requestApiData = new StringRequest(Request.Method.GET, String.format(url), new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                /*
                This method is called if the volley networking call is successful.
                The call is asynchronous.
                 */

                JSONObject jsonObject = null;
                JSONArray newsArray = null;
                try {
                    jsonObject = new JSONObject(response);
                } catch (JSONException e) {
                    Toast.makeText(getApplicationContext(), "json error 1" + e.toString(), Toast.LENGTH_SHORT).show();
                }

                try {
                    newsArray = jsonObject.getJSONArray("feed");
                } catch (JSONException e) {
                    Toast.makeText(getApplicationContext(), "json error 2" + e.toString(),
                            Toast.LENGTH_SHORT).show();
                }
                for (int j = 0; j < newsArray.length(); j++) {
                    String title = null;
                    String author = null;
                    String imageurl = null;
                    JSONObject obj = null;
                    String type = null;
                    String permalink = null;
                    try {
                        obj = newsArray.getJSONObject(j);

// Get Author name
                        author = obj.getJSONObject("author").getString("name");
                    } catch (JSONException e) {
                        Toast.makeText(getApplicationContext(), "json error 3" + e.toString(),
                                Toast.LENGTH_SHORT).show();
                    }
                    try {
// Get title
                        title = obj.getString("title");
//Get Image url and type and permalink
                        permalink = obj.getString("permalink");
                        imageurl = obj.getString("thumbnail");
                        type = obj.getString("type");

                    } catch (JSONException e) {
                        Toast.makeText(getApplicationContext(), "json error4" + e.toString(),
                                Toast.LENGTH_SHORT).show();
                    }

                    News news = new News();
                    news.setAuthor(author);
                    news.setImg(imageurl);
                    news.setTitle(title);
                    news.setType(type);
                    news.setPermalink(permalink);
                    newsList.add(news);

                }

                deleteRealmIfExists();
                addDataToRealm(newsList);
                flagNextActivity = 1;
                showNextActivity(flagNextActivity);




            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                /**
                 * This method is called when there is error in volley networking call.
                 */
                flagNextActivity = 2;
                showNextActivity(flagNextActivity);

            }
        });

        //Getting instance of Singleton class to add request to the request queue.
        SingletonVolley.getInstance(getApplicationContext()).addToRequestQueue(requestApiData);

    }


    private void showNextActivity(int flagNextActivity) {

        switch (flagNextActivity){
            case 1: //The case of success.
                Intent successIntent = new Intent(Splash.this,MainActivity.class);
                startActivity(successIntent);

                break;

            case 2:
                Intent errorIntent = new Intent(Splash.this, ErrorActivity.class);
                errorIntent.putExtra("flag", flagNextActivity);
                startActivity(errorIntent);

                break;


            case 3:
                Intent errorIntent2 = new Intent(Splash.this, ErrorActivity.class);
                errorIntent2.putExtra("flag", flagNextActivity);
                startActivity(errorIntent2);

        }
    }

    private void deleteRealmIfExists() {
        Realm realm = Realm.getDefaultInstance();
        realm.beginTransaction();
        realm.deleteAll();

        realm.commitTransaction();
    }

    private void addDataToRealm(final ArrayList<News> newsList){

        Realm realm = null;
        Realm.init(getApplicationContext());

        try {

            realm = Realm.getDefaultInstance();
            realm.executeTransaction(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {


                    try {

                        int i = 0;
                        for (i = 0; i < newsList.size(); i++) {
                            newsList.get(i).news_title= newsList.get(i).getTitle();
                            newsList.get(i).news_author = newsList.get(i).getAuthor();
                            newsList.get(i).news_image = newsList.get(i).getImg();
                            newsList.get(i).news_type = newsList.get(i).getType();
                            newsList.get(i).news_permalink = newsList.get(i).getPermalink();

                            realm.copyToRealm(newsList.get(i));
                        }

                    } catch (RealmException e) {
                        Toast.makeText(getApplicationContext(),
                                "Realm error + "+ e.toString(), Toast.LENGTH_SHORT).show();
                        flagNextActivity= 3;
                    }

                }
            });
        } finally {
            if (realm != null) {
                realm.close();
            }
        }


    }
}
