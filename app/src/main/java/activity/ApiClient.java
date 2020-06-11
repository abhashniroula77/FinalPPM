package activity;

import androidx.appcompat.app.AppCompatActivity;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiClient extends AppCompatActivity {

  public static final String BASE_URL = "http://10.0.2.2/newsapp/wp-json/api/";
   //public static final String BAS_URL = "http://192.168.1.128/newsapp/wp-json/api/";//for testing in devices base url of api http because we are using postman
    private static Retrofit retrofit =  null;

    //inorder to consume data from backend through network we need networking library we user retrofit for that

    public static Retrofit getApiClient()
    {
        HttpLoggingInterceptor logging = new HttpLoggingInterceptor(); //okhttp provided in github
        logging.level(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(logging)
                .build();
        if(retrofit == null) //if retrofit is null then only we are going  to create the instance of retrofit
        {
            retrofit = new Retrofit.Builder().baseUrl(BASE_URL).client(client).addConverterFactory(GsonConverterFactory.create())
                    .build(); // we are using builder pattern here
        }
        return retrofit;
    }



}