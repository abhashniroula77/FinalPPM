package activity;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.newsapp.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import adapter.NewsAdapter;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CategoryActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener {

    RecyclerView recyclerView;
    Toolbar toolbar;
    SwipeRefreshLayout swipeRefreshLayout;
    ProgressBar progressBar;
    int page = 1;
    int post = 10;
    NewsAdapter newsAdapter;
    List<HomepageModel.News> news = new ArrayList<>();
    boolean isStartFirst = true ;
    boolean shouldFetchData = true;
    int  backendAdsShown = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category);

        recyclerView = findViewById(R.id.news_recy);
        toolbar = findViewById(R.id.toolbar);
        swipeRefreshLayout = findViewById(R.id.swipe);
        progressBar = findViewById(R.id.progressbar);
        AdView adView = findViewById(R.id.adView);

        AdRequest adRequest = new AdRequest.Builder().addTestDevice(AdRequest.DEVICE_ID_EMULATOR).build();
        adView.loadAd(adRequest);
        progressBar.setVisibility(View.GONE);
        swipeRefreshLayout.setRefreshing(true);
        swipeRefreshLayout.setOnRefreshListener(this);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);
        setSupportActionBar(toolbar);


        toolbar.setNavigationIcon(R.drawable.icon_arrow_back_white);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        toolbar.setTitleTextColor(Color.WHITE);
        toolbar.setTitle(getIntent().getStringExtra("cname"));
        newsAdapter = new NewsAdapter(this,news);
        isStartFirst  = true;
        page = 1;
        getCategoryData();

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                int visibleItemCount = linearLayoutManager.getChildCount();
                int totalItemCount = linearLayoutManager.getItemCount();
                int passVisibleCount = linearLayoutManager.findFirstCompletelyVisibleItemPosition();
                if((passVisibleCount+visibleItemCount) == totalItemCount){
                    if (shouldFetchData){
                        shouldFetchData = false;
                        isStartFirst = false;
                        progressBar.setVisibility(View.VISIBLE);
                        page++;
                        getCategoryData();
                    }
                }
            }
        });
    }

    private void getCategoryData() {

        //loading data using retrofit
        ApiInterface apiInterface = ApiClient.getApiClient().create(ApiInterface.class);//getting instance of retrofit
        Map<String, String> params = new HashMap<>();
        params.put("page", page + ""); //keyname and value
        params.put("posts", post + "");
        params.put("cid",getIntent().getIntExtra("cid",0)+"");
        Call<HomepageModel> call = apiInterface.getNewsByCId(params);//calling  function
        call.enqueue(new Callback<HomepageModel>() { //data will be load in the background so that our ui will not be freezed
            @Override
            public void onResponse(Call<HomepageModel> call, Response<HomepageModel> response) {


                swipeRefreshLayout.setRefreshing(false);
                progressBar.setVisibility(View.GONE);
                int beforeSize = news.size();
                if(isStartFirst)
                {
                    news.clear();
                }
                int recentAds = 0;

                for (int i = 0; i < response.body().getNews().size(); i++) { //we are adding the news here

                    if((i+1)%1 == 0)
                    {
                        if(response.body().getAds().size()>backendAdsShown)
                        {
                            //backend ads
                            HomepageModel.Ad ad = response.body().getAds().get(backendAdsShown);
                            HomepageModel.News singleNews = new HomepageModel.News(ad.getPid(),ad.getTitle(),ad.getLink(),ad.getDescription(),ad.getImage());
                            news.add(singleNews);
                            backendAdsShown++;
                            singleNews.setAds(true);
                            singleNews.setAdsFromGoogle(false);


                        }
                        else{
                            //google ads
                            HomepageModel.News singleNews = new HomepageModel.News();
                            singleNews.setAdsFromGoogle(true);
                            singleNews.setAds(true);
                            news.add(singleNews);

                        }
                        recentAds++;
                    }

                    news.add(response.body().getNews().get(i));
                }
                if(isStartFirst)
                {
                    recyclerView.setAdapter(newsAdapter);

                }
                else{
                    newsAdapter.notifyItemRangeInserted(beforeSize,recentAds+response.body().getNews().size());
                }

                shouldFetchData = true;

            }

            @Override
            public void onFailure(Call<HomepageModel> call, Throwable t) {
                Toast.makeText(CategoryActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
                progressBar.setVisibility(View.GONE);
                swipeRefreshLayout.setRefreshing(false);
            }
        });

    }

    @Override
    public void onRefresh() {
        isStartFirst = true;
        page = 1;
        shouldFetchData = true;
        getCategoryData();

    }
}
