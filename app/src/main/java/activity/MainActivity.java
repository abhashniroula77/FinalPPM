package activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.bumptech.glide.request.RequestOptions;
import com.glide.slider.library.SliderLayout;
import com.glide.slider.library.slidertypes.BaseSliderView;
import com.glide.slider.library.slidertypes.DefaultSliderView;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.newsapp.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import adapter.GridAdapter;
import adapter.NewsAdapter;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class MainActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener {
    GridView gridView;
    SliderLayout sliderLayout;
    Toolbar toolbar;
    GridAdapter gridAdapter;
    NewsAdapter newsAdapter;
    RecyclerView recyclerView;
    List<HomepageModel.News> news;
    AdView adView;
    int backendAdsShown = 0;
    List<HomepageModel.CategoryButton> categoryButtons;
    int post = 5; //retrive no of post per time
    int page = 1; //This is the page number
    boolean isFromStart = true;
    //this will note whether we are loading the api for the first time or not by default when our application is opened it is true
    ProgressBar progressBar;
    NestedScrollView nestedScrollView;
    SwipeRefreshLayout swipeRefreshLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initViews();


        page = 1;
        isFromStart = true;

        getHomeData();

        nestedScrollView.setOnScrollChangeListener(new NestedScrollView.OnScrollChangeListener() {
            @Override
            public void onScrollChange(NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                if (scrollY == (v.getChildAt(0).getMeasuredHeight() - v.getMeasuredHeight()))//this will only be executed when we reach the last item of our nested scroll view
                {
                    isFromStart = false;
                    progressBar.setVisibility(View.VISIBLE);
                    page++;
                    getHomeData();
                }
            }
        });

    }

    //creating function for loading data in homepage
    private void getHomeData() {

        //loading data using retrofit
        ApiInterface apiInterface = ApiClient.getApiClient().create(ApiInterface.class);//getting instance of retrofit
        Map<String, String> params = new HashMap<>();
        params.put("page", page + ""); //keyname and value
        params.put("posts", post + "");
        Call<HomepageModel> call = apiInterface.getHomepageApi(params);//calling  function
        call.enqueue(new Callback<HomepageModel>() { //data will be load in the background so that our ui will not be freezed
            @Override
            public void onResponse(Call<HomepageModel> call, Response<HomepageModel> response) {
                updateDataToHomepage(response.body());

            }

            @Override
            public void onFailure(Call<HomepageModel> call, Throwable t) {
                Toast.makeText(MainActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
                progressBar.setVisibility(View.GONE);
                swipeRefreshLayout.setRefreshing(false);
            }
        });

    }

    private void updateDataToHomepage(HomepageModel body) {//Loading our fetched data into homepage

        progressBar.setVisibility(View.GONE);
        swipeRefreshLayout.setRefreshing(false);
        if (isFromStart) {
            news.clear();
            categoryButtons.clear();
        }
//Loading  fetched data into slider
        for (int i = 0; i < body.getBanners().size(); i++) {
            //creating individual slider
            DefaultSliderView defaultSliderView = new DefaultSliderView(this);
            defaultSliderView.setRequestOption(new RequestOptions().centerCrop());
            defaultSliderView.image(body.getBanners().get(i).getImage());
            defaultSliderView.setOnSliderClickListener(new BaseSliderView.OnSliderClickListener() {
                @Override
                public void onSliderClick(BaseSliderView slider) {
                    //TODO: handing click on image

                }
            });
            sliderLayout.addSlider(defaultSliderView);//adding individual slider to slider layout


        }
        sliderLayout.startAutoCycle();
        sliderLayout.setPresetTransformer(SliderLayout.Transformer.Stack);
        sliderLayout.setDuration(4000);
        sliderLayout.setPresetIndicator(SliderLayout.PresetIndicators.Right_Bottom);
        int recentAds = 0;

        //working on infinite news feature
        int beforeNewsSize = news.size();

        for (int i = 0; i < body.getNews().size(); i++) { //we are adding the news here

            if((i+1)%1 == 0)
            {
                if(body.getAds().size()>backendAdsShown)
                {
                    //backend ads
                    HomepageModel.Ad ad = body.getAds().get(backendAdsShown);
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

            news.add(body.getNews().get(i));
        }
        categoryButtons.addAll(body.getCategoryButton());
        if (isFromStart) {
            recyclerView.setAdapter(newsAdapter); //we are setting the adapter when ever we load the data for the first time
            gridView.setAdapter(gridAdapter);
        } else {
            newsAdapter.notifyItemRangeInserted(beforeNewsSize, recentAds+ body.getNews().size());

        }


    }

    private void initViews() {

        sliderLayout = findViewById(R.id.slider);//return slider layout Linked xml layout into java
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        progressBar = findViewById(R.id.progressbar);
        adView = findViewById(R.id.adView);
        categoryButtons = new ArrayList<>();
        gridView = findViewById(R.id.grid_view);
        gridAdapter = new GridAdapter(this, categoryButtons);
        AdRequest adRequest = new AdRequest.Builder().addTestDevice(AdRequest.DEVICE_ID_EMULATOR).build();
        adView.loadAd(adRequest);
        nestedScrollView = findViewById(R.id.nested);




        recyclerView = findViewById(R.id.recy_news);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);

        recyclerView.setNestedScrollingEnabled(false);
        news = new ArrayList<>();
        newsAdapter = new NewsAdapter(this, news);

        swipeRefreshLayout = findViewById(R.id.swipe);
        swipeRefreshLayout.setRefreshing(true);
        recyclerView.setNestedScrollingEnabled(false);
        swipeRefreshLayout.setOnRefreshListener(this);

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int i, long id) {
                Intent intent = new Intent(MainActivity.this,CategoryActivity.class);
                intent.putExtra("cid",categoryButtons.get(i).getCid());
                intent.putExtra("cname",categoryButtons.get(i).getName());
                startActivity(intent);
            }
        });
    }



    @Override
    protected void onStop() {
        super.onStop();
        sliderLayout.stopAutoCycle();//prevent us from any memory leak
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.video) {
            //we know user has clicked youtube icon
            startActivity(new Intent(this,YoutubeActivity.class));
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onRefresh() {
        isFromStart = true;
        page = 1;
        getHomeData();
    }
}
