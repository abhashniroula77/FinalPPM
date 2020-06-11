package activity;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.youtube.player.YouTubeBaseActivity;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerView;
import com.newsapp.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import adapter.YoutubeAdapter;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class VideoDetailsActivity extends YouTubeBaseActivity implements YouTubePlayer.OnInitializedListener {

    TextView videoTitle, channelName,labelSimilar;
    RecyclerView recyclerView;
    YouTubePlayerView youTubePlayerView;
    String cid,vid,title,cname;
    YoutubeAdapter youtubeAdapter;
    List<YtModel.Item> items = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_details);
        cid =getIntent().getStringExtra("cid");
        vid =getIntent().getStringExtra("vid");
        title =getIntent().getStringExtra("title");
        cname =getIntent().getStringExtra("cname");

        initViews();
        videoTitle.setText(title);
        channelName.setText(cname);
        labelSimilar.setText("More From"+cname);
        youTubePlayerView.initialize(getString(R.string.google_api_key),this);
        loadData();
    }

    private void loadData() {

        ApiInterface apiInterface = ApiClient.getApiClient().create(ApiInterface.class);//getting instance of retrofit
        Map<String, String> params = new HashMap<>();
        params.put("part","snippet");
        params.put("channelId",cid);
        params.put("maxResults","15");

        params.put("key",getString(R.string.google_api_key));

        Call<YtModel> call = apiInterface.getYoutubeServerData(params);//calling  function
        call.enqueue(new Callback<YtModel>() {
            @Override
            public void onResponse(Call<YtModel> call, Response<YtModel> response) {
                items.addAll(response.body().getItems());
                items.remove(0);
                labelSimilar.setVisibility(View.VISIBLE);
                recyclerView.setAdapter(youtubeAdapter);


            }

            @Override
            public void onFailure(Call<YtModel> call, Throwable t) {
                Toast.makeText(VideoDetailsActivity.this,t.getMessage(),Toast.LENGTH_SHORT).show();


            }
        });

    }

    private void initViews() {
        videoTitle = findViewById(R.id.video_title);
        channelName = findViewById(R.id.channel_name);
        labelSimilar = findViewById(R.id.label_similar);
        recyclerView = findViewById(R.id.video_recy);
        youTubePlayerView = findViewById(R.id.youtube_player);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);
        youtubeAdapter = new YoutubeAdapter(this,items,true);

    }

    @Override
    public void onInitializationSuccess(YouTubePlayer.Provider provider, YouTubePlayer youTubePlayer, boolean b) {
        youTubePlayer.cueVideo(vid);
    }

    @Override
    public void onInitializationFailure(YouTubePlayer.Provider provider, YouTubeInitializationResult youTubeInitializationResult) {

    }
}
