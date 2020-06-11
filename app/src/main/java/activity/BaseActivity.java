package activity;

import android.app.Application;
import android.widget.Toast;

import com.google.android.gms.ads.MobileAds;

public class BaseActivity extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        MobileAds.initialize(this);
        Toast.makeText(this,"Base Activity Called",Toast.LENGTH_SHORT).show();
    }
}
