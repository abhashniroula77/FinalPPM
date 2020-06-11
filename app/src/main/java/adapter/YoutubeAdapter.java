package adapter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.newsapp.R;

import java.util.ArrayList;
import java.util.List;

import activity.VideoDetailsActivity;
import activity.YtModel;

public class YoutubeAdapter extends RecyclerView.Adapter<YoutubeAdapter.ViewHolder> {
    Context context;

    List<YtModel.Item> items = new ArrayList<>();
    boolean isImageLeft = false;

    public YoutubeAdapter(Context context, List<YtModel.Item> items,boolean isImageLeft) {
        this.context = context;
        this.items = items;
        this.isImageLeft = isImageLeft;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v;
        if(isImageLeft)
        {
            v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_video_image_left,parent,false);
        }
        else{
            v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_video,parent,false);

        }
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
      YtModel.Item singleVideoItem = items.get(holder.getAdapterPosition());
      if(singleVideoItem.getSnippet().getThumbnails().getMaxres() != null)
      {
          Glide.with(context).load(singleVideoItem.getSnippet().getThumbnails().getMaxres().getUrl()).placeholder(R.drawable.icon_youtube).into(holder.videoThumbnail);
      }
      else if (singleVideoItem.getSnippet().getThumbnails().getStandard() != null)
      {
          Glide.with(context).load(singleVideoItem.getSnippet().getThumbnails().getStandard().getUrl()).placeholder(R.drawable.icon_youtube).into(holder.videoThumbnail);
      }
      else if(singleVideoItem.getSnippet().getThumbnails().getHigh() != null)
      {
          Glide.with(context).load(singleVideoItem.getSnippet().getThumbnails().getHigh().getUrl()).placeholder(R.drawable.icon_youtube).into(holder.videoThumbnail);
      }
      else
      {
          Glide.with(context).load(singleVideoItem.getSnippet().getThumbnails().getDefault().getUrl()).placeholder(R.drawable.icon_youtube).into(holder.videoThumbnail);

      }
      holder.videoTitle.setText(singleVideoItem.getSnippet().getTitle());
    }


    @Override
    public int getItemCount() {
        return items.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        ImageView videoThumbnail;
        TextView videoTitle;
        View v;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            v = itemView;
            videoThumbnail = v.findViewById(R.id.thumbnail);
            videoTitle = v.findViewById(R.id.video_title);
            v.setOnClickListener(this);

        }

        @Override
        public void onClick(View v) {
            YtModel.Item clickedVideo = items.get(getAdapterPosition());
            Uri uri = Uri.parse(clickedVideo.getSnippet().getThumbnails().getDefault().getUrl());
            Intent intent = new Intent(context, VideoDetailsActivity.class);
            intent.putExtra("vid",uri.getPathSegments().get(1));
            intent.putExtra("cid",clickedVideo.getSnippet().getChannelId());
            intent.putExtra("cname",clickedVideo.getSnippet().getChannelTitle());
            intent.putExtra("title",clickedVideo.getSnippet().getTitle());
            context.startActivity(intent);

        }
    }
}
