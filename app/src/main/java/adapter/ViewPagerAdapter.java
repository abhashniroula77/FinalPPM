package adapter;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import activity.ourYtModel;
import fragment.FragmentYoutube;

public class ViewPagerAdapter extends FragmentPagerAdapter {
    Context context;
    ourYtModel ourYModel;

    public ViewPagerAdapter(@NonNull FragmentManager fm, ourYtModel ourYModel, Context context) {
        super(fm, FragmentPagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
        this.context = context;
        this.ourYModel = ourYModel;
    }

    @NonNull
    @Override
    public Fragment getItem(int position) { //this function returns instance of fragmentYoutube when ever it is called
        Bundle bundle = new Bundle();
        bundle.putString("cid", ourYModel.getYoutubeData().get(position).getChannelId());
        FragmentYoutube fragmentYoutube = new FragmentYoutube();
        fragmentYoutube.setArguments(bundle);

        return fragmentYoutube;
    }

    @Override
    public int getCount() {
        return ourYModel.getYoutubeData().size();
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
      return ourYModel.getYoutubeData().get(position).getTitle();
    }
}
