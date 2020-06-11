package adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.newsapp.R;

import java.util.List;

import activity.HomepageModel;
import de.hdodenhof.circleimageview.CircleImageView;

public class GridAdapter extends BaseAdapter {
    LayoutInflater layoutInflater;
    Context context;
    List<HomepageModel.CategoryButton> categoryButtons;

    public GridAdapter(Context context, List<HomepageModel.CategoryButton> categoryButtons) {
        this.context = context;
        layoutInflater = (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
        this.categoryButtons = categoryButtons;


    }

    @Override
    public int getCount() {
        return categoryButtons.size();
    }

    @Override
    public Object getItem(int i) {
        return categoryButtons.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        ViewHolder holder = null;
        if (view == null) {
            view = layoutInflater.inflate(R.layout.item_category, null);
            holder = new ViewHolder();
            holder.circleImageView = view.findViewById(R.id.category_image);

            holder.categoryName = view.findViewById(R.id.category_name);
            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }
        holder.categoryName.setText(categoryButtons.get(i).getName());
        Glide.with(context).load(categoryButtons.get(i).getImage()).into(holder.circleImageView);
        if(categoryButtons.get(i).getColor() != null){
            holder.circleImageView.setCircleBackgroundColor(Color.parseColor(categoryButtons.get(i).getColor()));
            holder.circleImageView.setBorderColor(Color.parseColor(categoryButtons.get(i).getColor()));
        }

        return view;
    }

    static class ViewHolder {
        CircleImageView circleImageView;
        TextView categoryName;
    }


        String imageName;
    }

