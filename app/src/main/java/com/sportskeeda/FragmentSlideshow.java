package com.sportskeeda;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

import io.realm.Realm;
import io.realm.RealmResults;

public class FragmentSlideshow extends Fragment implements RecyclerViewAdapter.ListItemClickListener {
   View v;
    private RecyclerView myrecyclerview;
    TextView searchEmpty;
    RecyclerViewAdapter recyclerViewAdapter;
    String link;
    ArrayList<News> newsArrayList = new ArrayList<News>();
    Realm mRealm;
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mRealm = Realm.getDefaultInstance();


 }

    public FragmentSlideshow() {

    }
    private void read_from_realm() {
        mRealm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {

                RealmResults<News> results = realm.where(News.class).equalTo("type", "slideshow").findAll();;
                newsArrayList.clear();
                newsArrayList.addAll(results);



            }
        });
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        read_from_realm();
        v= inflater.inflate(R.layout.all_fragment,container,false);
        myrecyclerview=(RecyclerView)v.findViewById(R.id.recyclerviewall);
        searchEmpty = v.findViewById(R.id.searchempty);
        recyclerViewAdapter = new RecyclerViewAdapter(getContext(),newsArrayList,getActivity(),this);
        myrecyclerview.setLayoutManager(new LinearLayoutManager(getActivity()));
        myrecyclerview.setAdapter(recyclerViewAdapter);

        return v;
    }

    public ArrayList<News> getNewsArrayList(){

        return newsArrayList;

    }


    public void onListItemClick(final int itemIndex,String Newslink) {

        Realm mRealm;
        mRealm = Realm.getDefaultInstance();

        mRealm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {

                RealmResults<News> results = realm.where(News.class).equalTo("type", "slideshow").findAll();
                link=results.get(itemIndex).getPermalink();

                Intent sharingIntent = new Intent(Intent.ACTION_SEND);
                sharingIntent.setType("text/plain");
                sharingIntent.putExtra(Intent.EXTRA_TEXT,"For More Information Click Here: "+ link);
                startActivity(Intent.createChooser(sharingIntent, "Share Using"));
            }
        });


    }
    public void updateList(ArrayList<News> newArrayList) {
        if(newArrayList!=null){
            myrecyclerview.setVisibility(View.VISIBLE);
            searchEmpty.setVisibility(View.INVISIBLE);
            recyclerViewAdapter = new RecyclerViewAdapter(getContext(), newArrayList, getActivity(), this);
            myrecyclerview.setLayoutManager(new LinearLayoutManager(getActivity()));
            myrecyclerview.setAdapter(recyclerViewAdapter);
        }

    }
    public void updateEmptyList() {

        searchEmpty.setVisibility(View.VISIBLE);
        myrecyclerview.setVisibility(View.INVISIBLE);


    }
}
