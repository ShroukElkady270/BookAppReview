package com.example.mobileappproject;

import android.app.ActionBar;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;

import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toolbar;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;
import androidx.drawerlayout.widget.DrawerLayout;


public class postlistActivity extends AppCompatActivity {

    private Context mContext;
    FloatingActionButton create;
    Post selectedPost;
    PostAdapter adapter;

    Toolbar toolbar;

    ArrayList<Post> postlist;

    private Spinner sortSpinner;

    SearchView searchView;

    DatabaseHelper myDB;
    ListView mpostlist;
    private static final String TAG = "postlistActivity";

    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mToggle;
    NavigationView navigationView;

    TextView titleTextview;
    ActionBar.LayoutParams layoutparams;


    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.post_list);

        mContext = this;
        create = findViewById(R.id.create);
        toolbar =findViewById(R.id.app_bar);
        setActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        mpostlist = findViewById(R.id.post_list);
        myDB = new DatabaseHelper(this);
        postlist = new ArrayList<>();

        //sortSpinner =(Spinner)findViewById(R.id.sortspinner);

        mDrawerLayout = findViewById(R.id.drawer_layout);
        mToggle = new ActionBarDrawerToggle(this,mDrawerLayout, R.string.open_drawer,R.string.close_drawer);

        mDrawerLayout.addDrawerListener(mToggle);
        mToggle.syncState();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        navigationView = findViewById(R.id.nav_view);
        toolbar.setNavigationIcon(ResourcesCompat.getDrawable(getResources(), R.drawable.ic_action_name, null));
        toolbar.setOverflowIcon(ResourcesCompat.getDrawable(getResources(), R.drawable.ic_sort_black_24dp, null));
        setupDrawerContent(navigationView);

        populateListview(getPostlist());


        // click the list_item
        mpostlist.setOnItemClickListener( new AdapterView.OnItemClickListener(){

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id){
                selectedPost = postlist.get(position);

                Intent detailIntent = new Intent(mContext, PostDetailActivity.class);
                detailIntent.putExtra("title", selectedPost.gettitle());
                detailIntent.putExtra("author", selectedPost.getauthor());
                detailIntent.putExtra("quote", selectedPost.getquote());
                detailIntent.putExtra("rate", selectedPost.getrate());
                detailIntent.putExtra("tag", selectedPost.gethashtag());
                detailIntent.putExtra("review", selectedPost.getReviews());
                detailIntent.putExtra("datetime", selectedPost.getDatetime());
                detailIntent.putExtra("bookcover",selectedPost.getBookcover());

                startActivity(detailIntent);

            }
        });


        //create new posts
        create.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent createIntent = new Intent(mContext, BookListActivity.class);
                startActivity(createIntent);
            }


        });

    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_search, menu);
        MenuItem item = menu.findItem(R.id.menuSearch);
        searchView = (SearchView) item.getActionView();

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {

                String newText = s.toLowerCase();
                ArrayList<Post> newList = new ArrayList<>();
                for (Post p : postlist) {
                    String title = p.gettitle().toLowerCase();
                    String quote = p.getquote().toLowerCase();
                    String tags = p.gethashtag().toLowerCase();
                    String thought = p.getReviews().toLowerCase();
                    if (title.contains(newText)
                            || quote.contains(newText)
                            || tags.contains(newText)
                            || thought.contains(newText))
                        newList.add(p);
                }

                populateListview(newList);

                return false;

            }
        });



        return true;
    }


    public ArrayList<Post> getPostlist(){
        Log.d(TAG,"populateListView: Displaying data in the ListView");
        //get the data dan append to a list
        ArrayList<Post> currentlist= new ArrayList<Post>();
        Cursor data = myDB.getAllData();
        while (data.moveToNext()){
            String book = data.getString(1);
            String author =data.getString(2);//tag
            String hashtag =data.getString(3);//rate
            String quote =data.getString(4);//author
            String rate = data.getString(5);//quote
            String thoughts =data.getString(6);
            String record = data.getString(7);
            byte[] bookCover = data.getBlob(8);
            String datetime =data.getString(9);
            Post post = new Post(book, author,hashtag, quote, rate, thoughts, bookCover, datetime);
            currentlist.add(post);
        }
        Collections.reverse(currentlist);
        postlist = currentlist;
        return postlist;
    }


    public ArrayList<Post> sorthighest(){
        Collections.sort(postlist, new Comparator<Post>() {
            @Override
            public int compare(Post p1, Post p2) {
                return Double.compare(p1.ratedouble, p2.ratedouble);
            }
        });
        Collections.reverse(postlist);
        return postlist;
    }

    private void populateListview( ArrayList<Post> postlist){
        adapter = new PostAdapter(this,postlist);
        mpostlist.setAdapter(adapter);
    }



    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        if(mToggle.onOptionsItemSelected(item)){
            return true;
        }
        switch (item.getItemId()){
            case R.id.menuSortNewest:
                populateListview(getPostlist());
                break;
            case R.id.menuSortRating:
                populateListview(sorthighest());
                break;
            default:

        }

        return super.onOptionsItemSelected(item);
    }

    public void selectItemDrawer(MenuItem menuItem){
        switch(menuItem.getItemId()){
            case R.id.nav_contact:
                Intent intent = new Intent(Intent.ACTION_MAIN);
                intent.addCategory(Intent.CATEGORY_APP_EMAIL);
                intent.putExtra(Intent.EXTRA_EMAIL, "yany@oxy.edu");
                intent.putExtra(Intent.EXTRA_SUBJECT, "Subject");
                startActivity(Intent.createChooser(intent, "ChoseEmailClient"));
                break;
        }
        menuItem.setChecked(true);
        mDrawerLayout.closeDrawers();
    }
    public void setupDrawerContent(NavigationView navigationView){
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                selectItemDrawer(item);
                return true;
            }
        });
    }

}
