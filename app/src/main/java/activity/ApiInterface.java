package activity;

import java.util.Map;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.QueryMap;

public interface ApiInterface {
    @GET("homepage_api") //fetching data from homepage api
    //foundation of retrofit to  use for api
    Call<HomepageModel> getHomepageApi(@QueryMap Map<String, String> params);//fetching data from gson gson to java object library is going to convert json obect into homepage model
//we are using this approach to call every api

    @GET("news_by_pid")

    Call<HomepageModel> getNewsDetailsById(@QueryMap Map<String, String> params);

    @GET("news_by_cid")

    Call<HomepageModel> getNewsByCId (@QueryMap Map<String, String> params);

    @GET("youtube") //register route in function as youtube

    Call<ourYtModel> getYoutubeDataFromServer();

    @GET("https://www.googleapis.com/youtube/v3/activities") //register route in function as youtube

    Call<YtModel> getYoutubeServerData(@QueryMap Map<String, String> params);





}
