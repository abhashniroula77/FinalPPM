package activity;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;
import com.newsapp.R;

import adapter.ViewPagerAdapter;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class YoutubeActivity extends AppCompatActivity {

    Toolbar toolbar;
    ViewPager viewPager;
    TabLayout tabLayout;
    ViewPagerAdapter viewPagerAdapter;
    

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_youtube);
        initViews();
    }

    private void initViews() {
        toolbar = findViewById(R.id.toolbar);
        viewPager = findViewById(R.id.view_pager);
        tabLayout = findViewById(R.id.tabLayout);
        tabLayout.setupWithViewPager(viewPager);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Youtube Videos");

        toolbar.setNavigationIcon(R.drawable.icon_arrow_back_white);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        getYouTubeData();
    }

    private void getYouTubeData() {
        ApiInterface apiInterface = ApiClient.getApiClient().create(ApiInterface.class);
        Call<ourYtModel> call = apiInterface.getYoutubeDataFromServer();
        call.enqueue(new Callback<ourYtModel>() {
            @Override
            public void onResponse(Call<ourYtModel> call, Response<ourYtModel> response) {
               viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager(),response.body(),YoutubeActivity.this);
               viewPager.setAdapter(viewPagerAdapter);
            }

            @Override
            public void onFailure(Call<ourYtModel> call, Throwable t) {
                Toast.makeText(YoutubeActivity.this,t.getMessage(),Toast.LENGTH_SHORT).show();

            }
        });


    }
}
