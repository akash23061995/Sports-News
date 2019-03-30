package com.sportskeeda;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.SearchView;
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
import io.realm.RealmResults;
import io.realm.exceptions.RealmException;

public class MainActivity extends AppCompatActivity implements RecyclerViewAdapter.ListItemClickListener,SearchView.OnQueryTextListener {
    private int checkedTabValue;
    Realm mRealm;
    private ViewPager viewPager;
    private TabLayout tabLayout;
    private ViewPagerAdapter adapter;
    ArrayList<News> newsArrayList = new ArrayList<>();
    RecyclerViewAdapter recyclerViewAdapter;
    private static boolean articleFlag = false, slideshowFlag = false, videoFlag = false; // Variables to get menu of selected items
    String link;
    ArrayList<News> newsList = new ArrayList<>();
    ArrayList<Fragment> FirstFrag = new ArrayList<>();
    int flag = 0;
    int selectedTab;
    Menu m;
   FragmentAll fragmentAll = new FragmentAll();
   FragmentArticle fragmentArticle= new FragmentArticle();
   FragmentSlideshow fragmentSlideshow= new FragmentSlideshow();
   FragmentVideo fragmentVideo= new FragmentVideo();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        requestWindowFeature(Window.FEATURE_NO_TITLE); // Creating full screen activity for splash screen
//        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);

        viewPager = findViewById(R.id.viewpager_id);

        final SwipeRefreshLayout swipe = findViewById(R.id.swiperefresh);
        swipe.setColorSchemeColors(getResources().getColor(R.color.refresh1), getResources().getColor(R.color.refresh2),
                getResources().getColor(R.color.refresh3), getResources().getColor(R.color.refresh4)
                , getResources().getColor(R.color.refresh5), getResources().getColor(R.color.refresh6), getResources().getColor(R.color.refresh7),
                getResources().getColor(R.color.refresh8));
        swipe.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {

                selectedTab = viewPager.getCurrentItem();
                Realm realm = Realm.getDefaultInstance();
                realm.beginTransaction();
                realm.deleteAll();
                realm.commitTransaction();
                fetchJSON();


            }
        });


        if (getIntent() != null) {
            Bundle extra = getIntent().getExtras();
            if (extra != null) {
                checkedTabValue = extra.getInt("tabindex");
                Log.d("Main", "onCreate: " + checkedTabValue);


            }

        }
        Realm.init(getApplicationContext());
        mRealm = Realm.getDefaultInstance();
        ActionBar actionBar = getSupportActionBar();
        actionBar.setElevation(0);
        setViewpager(articleFlag, slideshowFlag, videoFlag);

    }

    private void refresh() {
        Realm realm = Realm.getDefaultInstance();
        realm.beginTransaction();
        realm.deleteAll();
        realm.commitTransaction();
        fetchJSON();
    }

    private void setViewpager(boolean articleFlag, boolean slideshowFlag, boolean videoFlag) {
        viewPager = findViewById(R.id.viewpager_id);
        tabLayout = findViewById(R.id.tablayout_id);
        viewPager.setSaveFromParentEnabled(false);
        adapter = new ViewPagerAdapter(getSupportFragmentManager());

        adapter.AddFragment(fragmentAll, "All");

        adapter.notifyDataSetChanged();

        if (articleFlag) {
            adapter.AddFragment(fragmentArticle, "Article");
            adapter.notifyDataSetChanged();
        }
        if (slideshowFlag) {
            adapter.AddFragment(fragmentSlideshow, "Slide");
            adapter.notifyDataSetChanged();
        }
        if (videoFlag) {
            adapter.AddFragment(fragmentVideo , "Video");
            adapter.notifyDataSetChanged();
        }
        adapter.notifyDataSetChanged();
        viewPager.setAdapter(adapter);
        tabLayout.setupWithViewPager(viewPager);
        tabLayout.setTabTextColors(getResources().getColor(R.color.grey), getResources().getColor(R.color.white));
        tabLayout.getTabAt(0).setIcon(R.drawable.all24);
        if (articleFlag) {
            tabLayout.getTabAt(1).setIcon(R.drawable.article);

        }
        if (slideshowFlag) {
            if (articleFlag) {
                tabLayout.getTabAt(2).setIcon(R.drawable.slide);
            } else if (!articleFlag) {
                tabLayout.getTabAt(1).setIcon(R.drawable.slide);
            }

        }
        if (videoFlag) {
            if (articleFlag && slideshowFlag) {
                tabLayout.getTabAt(3).setIcon(R.drawable.video);
            } else if (articleFlag && !slideshowFlag) {
                tabLayout.getTabAt(2).setIcon(R.drawable.video);
            } else if (!articleFlag && !slideshowFlag) {
                tabLayout.getTabAt(1).setIcon(R.drawable.video);
            } else if (!articleFlag && slideshowFlag) {
                tabLayout.getTabAt(2).setIcon(R.drawable.video);
            }

        }

        Realm.init(getApplicationContext());
        mRealm = Realm.getDefaultInstance();
        ActionBar actionBar = getSupportActionBar();
        actionBar.setElevation(0);
//        RecyclerViewAdapter recyclerViewAdapter = new RecyclerViewAdapter(this, newsList, this, this);

        if (checkedTabValue == 1 || checkedTabValue == 2 || checkedTabValue == 3) {
            viewPager.setCurrentItem(checkedTabValue);
        }

    }

    private void readFromRealm() {
        mRealm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                RealmResults<News> results = realm.where(News.class).findAll();
                newsArrayList.addAll(results);


            }
        });
    }

    @Override
    public void onListItemClick(final int itemIndex, String Newslink) {

        Realm mRealm;
        mRealm = Realm.getDefaultInstance();

        mRealm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {

                RealmResults<News> results = realm.where(News.class).findAll();
                link = results.get(itemIndex).getPermalink();

                Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
                sharingIntent.setType("text/plain");
                sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "For more Information Click here");
                sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, link);
                startActivity(Intent.createChooser(sharingIntent, "Share Using"));
            }
        });


    }

    private void fetchJSON() {

        /*
         * Fetching fresh data from API and updating realm database
         * */
        String url = getString(R.string.url);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, String.format(url),
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        JSONObject jsonObject = null;
                        JSONArray news_array = null;
                        try {
                            jsonObject = new JSONObject(response);
//                    Toast.makeText(getApplicationContext(), jsonObject.toString(),Toast.LENGTH_SHORT).show();
                        } catch (JSONException e) {

                            Toast.makeText(getApplicationContext(), "json error 1" + e.toString(), Toast.LENGTH_SHORT).show();
                        }

                        try {
                            news_array = jsonObject.getJSONArray("feed");
//                      Toast.makeText(getApplicationContext(), jokes_array.getString(5), Toast.LENGTH_SHORT).show();
                        } catch (JSONException e) {
                            Toast.makeText(getApplicationContext(), "json error 2" + e.toString(),
                                    Toast.LENGTH_SHORT).show();
                        }
                        for (int j = 0; j < news_array.length(); j++) {
                            String title = null;
                            String author = null;
                            String imageurl = null;
                            JSONObject obj = null;
                            String type = null;
                            String permalink = null;
                            try {
                                obj = news_array.getJSONObject(j);

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


                        addDataToRealm(newsList);

                        flag = 1;
                        showNewActivity(flag);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(), "volleyError" + error.toString(),
                        Toast.LENGTH_SHORT).show();
                flag = 2;
                showNewActivity(flag);

            }
        });

        SingletonVolley.getInstance(getApplicationContext()).addToRequestQueue(stringRequest);
    }

    private void showNewActivity(int flag) {
        switch (flag) {
            case 1:

                Intent i = new Intent(this, MainActivity.class);
                Bundle bundle = new Bundle();

                bundle.putInt("selectedTab", selectedTab);
                i.putExtras(bundle);
                startActivity(i);
                finish();

                break;
            case 2:
                Intent i1 = new Intent(this, ErrorActivity.class);
                i1.putExtra("flag", flag);
                startActivity(i1);

                break;
            case 3:

                Intent i2 = new Intent(this, ErrorActivity.class);
                i2.putExtra("flag", flag);
                startActivity(i2);
                break;
        }
    }

    private void addDataToRealm(final ArrayList<News> newsArrayList) {

        Realm realm = null;
        Realm.init(getApplicationContext());

        try {

            realm = Realm.getDefaultInstance();
            realm.executeTransaction(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {


                    try {

                        int i;
                        for (i = 0; i < newsArrayList.size(); i++) {
                            newsArrayList.get(i).news_title = newsArrayList.get(i).getTitle();
                            newsArrayList.get(i).news_author = newsArrayList.get(i).getAuthor();
                            newsArrayList.get(i).news_image = newsArrayList.get(i).getImg();
                            newsArrayList.get(i).news_type = newsArrayList.get(i).getType();
                            newsArrayList.get(i).news_permalink = newsArrayList.get(i).getPermalink();

                            realm.copyToRealm(newsArrayList.get(i));
                        }

                    } catch (RealmException e) {
                        Toast.makeText(getApplicationContext(),
                                "Realm error + " + e.toString(), Toast.LENGTH_SHORT).show();
                        flag = 3;
                    }

                }
            });
        } finally {
            if (realm != null) {
                realm.close();
            }
        }
    }

    @Override
    public void onBackPressed() {

        int count = getFragmentManager().getBackStackEntryCount();

        if (count == 0) {

            // super.onBackPressed();
            AlertDialog.Builder builder1 = new AlertDialog.Builder(this);
            builder1.setMessage("Do you want to Exit?");
            builder1.setCancelable(true);

            builder1.setPositiveButton(
                    "OK",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            moveTaskToBack(true);
                            dialog.cancel();
                        }
                    });

            builder1.setNegativeButton(
                    "Cancel",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.cancel();
                        }
                    });
            AlertDialog alert11 = builder1.create();
            alert11.show();


        } else {
            getFragmentManager().popBackStack();
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.menu, menu);
        MenuItem menuItem= menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView)menuItem.getActionView();
        searchView.setOnQueryTextListener(this);
        MenuItem itemArticle = menu.findItem(R.id.action_article);
        MenuItem itemSlide = menu.findItem(R.id.action_slideshow);
        MenuItem itemVideo = menu.findItem(R.id.action_video);
//        if (itemArticle.isChecked()) {
//            articleFlag = true;
//        }
//        if (!itemArticle.isChecked()) {
//            articleFlag = false;
//        }
//        if (itemSlide.isChecked()) {
//            slideshowFlag = true;
//        }
//        if (!itemSlide.isChecked()) {
//            slideshowFlag = false;
//        }
//        if (itemVideo.isChecked()) {
//            videoFlag = true;
//        }
//        if (!itemVideo.isChecked()) {
//            videoFlag = false;
//        }


        this.m = menu;
        if (articleFlag == true) {
            MenuItem item = m.findItem(R.id.action_article);
            item.setChecked(true);

        }
        if (slideshowFlag == true) {
            MenuItem item = m.findItem(R.id.action_slideshow);
            item.setChecked(true);

        }
        if (videoFlag == true) {
            MenuItem item = m.findItem(R.id.action_video);
            item.setChecked(true);

        }
        MenuItem menuItemArticle = menu.findItem(R.id.action_article);
        menuItemArticle.setCheckable(true);
        MenuItem menuItemSlideshow = menu.findItem(R.id.action_slideshow);
        menuItemSlideshow.setCheckable(true);
        MenuItem menuItemVideo = menu.findItem(R.id.action_video);
        menuItemVideo.setCheckable(true);


        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {

            case R.id.action_article:
                if (item.isChecked()) {

                    articleFlag = false;

                    setViewpager(articleFlag, slideshowFlag, videoFlag);
                    //Set the next  tab as selected tab
                    TabLayout.Tab tab = tabLayout.getTabAt(0);
                    if (tab != null) {
                        tab.select();
                    }
                    item.setChecked(false);
                } else {
                    articleFlag = true;
                    setViewpager(articleFlag, slideshowFlag, videoFlag);
                    item.setChecked(true);
                }
                return true;

            case R.id.action_slideshow:
                if (item.isChecked()) {
                    slideshowFlag = false;
                    setViewpager(articleFlag, slideshowFlag, videoFlag);
                    //Set the next  tab as selected tab
                    TabLayout.Tab tab = tabLayout.getTabAt(0);
                    if (tab != null) {
                        tab.select();
                    }
                    item.setChecked(false);
                } else {


                    slideshowFlag = true;
                    setViewpager(articleFlag, slideshowFlag, videoFlag);
                    item.setChecked(true);
                }
                return true;
            case R.id.action_video:
//            viewPager.setCurrentItem(3);
                if (item.isChecked()) {

                    videoFlag = false;
                    setViewpager(articleFlag, slideshowFlag, videoFlag);
                    //Set the next  tab as selected tab
                    TabLayout.Tab tab = tabLayout.getTabAt(0);
                    if (tab != null) {
                        tab.select();
                    }
                    item.setChecked(false);
                } else {

                    videoFlag = true;
                    setViewpager(articleFlag, slideshowFlag, videoFlag);
                    item.setChecked(true);
                }

                return true;
        }
        return true;
    }



    @Override
    public boolean onQueryTextSubmit(String query) {
        return true;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        String userInput = newText.toLowerCase();
        int tabNumber = viewPager.getCurrentItem();

        ArrayList<News> newList = new ArrayList<>();
        ArrayList<News> filteredList = new ArrayList<>();

        switch (tabNumber){
            case 0:
                newList = fragmentAll.getNewsArrayList();
                break;
            case 1:
                newList = fragmentArticle.getNewsArrayList();
                break;
            case 2:
                newList= fragmentSlideshow.getNewsArrayList();
                break;
            case 3:
                newList = fragmentVideo.getNewsArrayList();
                break;
        }
        for(News news: newList){
            String title1= news.getTitle();
            if(title1.toLowerCase().contains(userInput)){
                filteredList.add(news);
            }
            else{
                switch (tabNumber){
                    case 0:
                        fragmentAll.updateEmptyList();
                        break;
                    case 1:
                       fragmentArticle.updateEmptyList();
                        break;
                    case 2:
                       fragmentSlideshow.updateEmptyList();
                        break;
                    case 3:
                        fragmentVideo.updateEmptyList();
                        break;
                }
            }
        }
        if(filteredList.size()>0) {
            switch (tabNumber){
                case 0:
                    fragmentAll.updateList(filteredList);
                    break;
                case 1:
                   fragmentArticle.updateList(filteredList);
                    break;
                case 2:
                   fragmentSlideshow.updateList(filteredList);
                    break;
                case 3:
                    fragmentVideo.updateList(filteredList);
                    break;
            }
        }




        return true;
    }
}
