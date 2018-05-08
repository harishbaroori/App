package com.harish.githubrepofinder;

import android.app.ProgressDialog;
import android.app.SearchManager;
import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.List;
import cz.msebera.android.httpclient.Header;

public class MainActivity extends AppCompatActivity {

    SearchView searchView = null;
    MenuItem searchMenuItem = null;
    RecyclerView recyclerView;
    GitHubAdapter adapter;
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initComponents();
    }

    private void initComponents(){
        recyclerView = findViewById(R.id.recycler_view);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        adapter = new GitHubAdapter(getApplicationContext());
        recyclerView.setAdapter(adapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu,menu);
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        searchMenuItem = menu.findItem(R.id.action_search);
        searchView = (SearchView)searchMenuItem.getActionView();
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        searchView.setIconifiedByDefault(false);
        searchView.setQueryHint("enter github repo name");
        searchView.setOnQueryTextListener(new GitHubQueryListener());
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

    class GitHubQueryListener implements SearchView.OnQueryTextListener{
        @Override
        public boolean onQueryTextSubmit(String query) {
            searchRepos(query);
            if(searchMenuItem != null)
                searchMenuItem.collapseActionView();
            return true;
        }

        @Override
        public boolean onQueryTextChange(String newText) {
            return false;
        }
    }

    static final String TAG = "GITHUB_FINDER";

    private void searchRepos(final String query){
        String url = "https://api.github.com/search/repositories?q="+query+"&sort=stars&order=desc&per_page=100";
        AsyncHttpClient asyncHttpClient = new AsyncHttpClient();
        asyncHttpClient.addHeader("Accept", "application/vnd.github.mercy-preview+json");
        asyncHttpClient.addHeader("user-agent","Android");
        asyncHttpClient.get(url,new JsonHttpResponseHandler(){
            @Override
            public void onStart() {
                super.onStart();
                progressDialog = new ProgressDialog(MainActivity.this);
                progressDialog.setCancelable(false);
                progressDialog.setMessage("searching for \""+query+"\"");
                progressDialog.show();
            }

            @Override
            public void onFinish() {
                super.onFinish();
                if(progressDialog != null && progressDialog.isShowing())
                    progressDialog.dismiss();
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);
                try {
                    JSONArray jsonArray = response.getJSONArray("items");
                    parseJsonContent(jsonArray);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        });
    }

    private void parseJsonContent(JSONArray jsonArray){
        List<GitHubRepoInfo> list = new ArrayList<>();
        try {
            for(int i=0;i<jsonArray.length();i++){
                JSONObject itemObject = (JSONObject) jsonArray.get(i);
                String repoName = itemObject.getString("name");
                int star = itemObject.getInt("stargazers_count");
                String owner =itemObject.getJSONObject("owner").getString("login");
                String description = itemObject.getString("description");
                Log.d(TAG,"repo  name "+repoName);
                Log.d(TAG,"star "+star);
                Log.d(TAG,"owner "+owner);
                Log.d(TAG,"description "+description);

                GitHubRepoInfo gitHubRepoInfo = new GitHubRepoInfo();
                gitHubRepoInfo.setRepoName(repoName);
                gitHubRepoInfo.setStars(star);
                gitHubRepoInfo.setOwner(owner);
                gitHubRepoInfo.setDescription(description);
                list.add(gitHubRepoInfo);
            }

            adapter.reload(list);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}