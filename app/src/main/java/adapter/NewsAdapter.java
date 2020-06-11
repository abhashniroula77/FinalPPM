package adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.newsapp.R;

import java.util.List;

import activity.HomepageModel;
import activity.NewsDetailActivity;

public class NewsAdapter extends RecyclerView.Adapter {
    Context context;
    List <HomepageModel.News> news;
    int image_left = 1;
    int image_top = 2;
    int ads = 3;

    public NewsAdapter(Context context, List<HomepageModel.News> news) {
        this.context = context;//blue context represents up context blue onw and other represents inside newsadapter
        this.news = news; //same as context


    }

    @Override
    public int getItemViewType(int position) { //this is a important method it helps us to retrive two different view

        if (news.get(position).isAds()) {
            return ads;
        } else {
            if ((position + 1) % 8 == 0 || position == 0)//if position == 0 or for every 8 position return top image or left image
            {
                return image_top;
            } else {
                return image_left;
            }
        }

    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == image_left) {
            View v = LayoutInflater.from(context).inflate(R.layout.item_news, parent, false);
            return new ViewHolder(v);
        } else if (viewType == ads) {
            View v = LayoutInflater.from(context).inflate(R.layout.item_ads, parent, false);
            return new AdsViewHolder(v);
        }
        else {
            View v = LayoutInflater.from(context).inflate(R.layout.item_news_image, parent, false);
            return new ViewHolder(v);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

//loading news from backend  or load ads from google adMob or backend
     HomepageModel.News singleNews = news.get(holder.getAdapterPosition());//getting the adapter position
     if(singleNews.isAds())
     {
         AdsViewHolder adsViewHolder = (AdsViewHolder) holder;

       //load Ads from google or backend

         if(singleNews.isAdsFromGoogle())
         {
             //load ads from google admob
             adsViewHolder.adView.setVisibility(View.VISIBLE);
             adsViewHolder.relativeLayout.setVisibility(View.GONE);
             AdView adView = adsViewHolder.adView;
             adView.loadAd(new AdRequest.Builder().addTestDevice(AdRequest.DEVICE_ID_EMULATOR).build());




         }
         else
         {
             //loads ads from backend
             adsViewHolder.adView.setVisibility(View.GONE);
             adsViewHolder.relativeLayout.setVisibility(View.VISIBLE);

             adsViewHolder.adsTitle.setText(singleNews.getTitle());
             adsViewHolder.adsDes.setText(singleNews.getPostContent());
             Glide.with(context).load(singleNews.getImage()).into(adsViewHolder.adsImage);


         }


     }
     else
     {
         //load news from backend
         ViewHolder viewHolder = (ViewHolder) holder;
         viewHolder.newsTitle.setText(removeHtml(singleNews.getTitle()).trim());
         viewHolder.newsDescp.setText(removeHtml(singleNews.getPostContent()).trim());
         if(singleNews.getSource() != null)//preventing our program from null pointer exception
         {
             viewHolder.newsSource.setText(singleNews.getSource());
         }
         if(singleNews.getImage().length()<=1)
         {
             viewHolder.newsSource.setText(singleNews.getSource());
         }
         else {
             viewHolder.newsImage.setVisibility(View.VISIBLE);
             Glide.with(context).load(singleNews.getImage()).into(viewHolder.newsImage);
         }
     viewHolder.newsViews.setText(singleNews.getViews()+"Views");

     }




    }

    @Override
    public int getItemCount() {
        return news.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        View holder;
        ImageView newsImage;
        TextView newsTitle,newsDescp,newsDate,newsSource,newsViews;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            holder = itemView;
            newsImage = holder.findViewById(R.id.news_image);
            newsTitle = holder.findViewById(R.id.news_title);
            newsDescp = holder.findViewById(R.id.news_description);
            newsDate = holder.findViewById(R.id.news_date);
            newsViews = holder.findViewById(R.id.news_view);
            newsSource = holder.findViewById(R.id.news_source);
            holder.setOnClickListener(this);


        }

        @Override
        public void onClick(View v) {//when somebody clicks button this onclick method is called
            Intent intent = new Intent(context, NewsDetailActivity.class);//linking the activity when the button is clicked
            intent.putExtra("pid",news.get(getAdapterPosition()).getPid());
            context.startActivity(intent);


        }
    }
//removing symbols empty spaces from the application
    public static String removeHtml(String html) {
        html = html.replaceAll("<(.*?)\\>", " ");
        html = html.replaceAll("<(.*?)\\\n", " ");
        html = html.replaceFirst("(.*?)\\>", " ");
        html = html.replaceAll("&nbsp;", " ");
        html = html.replaceAll("&amp;", " ");
        return html;
    }

    //Creating class for loading of advertisement
    public class AdsViewHolder extends RecyclerView.ViewHolder

    {
        View holder;
        TextView adsTitle , adsDes;
        ImageView adsImage;
        AdView adView;
        RelativeLayout relativeLayout;


        public AdsViewHolder(@NonNull View itemView) {
            super(itemView);
            holder = itemView;
            adsTitle = holder.findViewById(R.id.ads_titles);
            adsDes = holder.findViewById(R.id.ads_description);
            adsImage = holder.findViewById(R.id.ads_images);
            adView = holder.findViewById(R.id.adsView);
            relativeLayout = holder.findViewById(R.id.custom_ads_wrapper);


        }
    }

}
