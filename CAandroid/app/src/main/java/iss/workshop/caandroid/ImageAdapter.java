package iss.workshop.caandroid;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import androidx.recyclerview.widget.RecyclerView;


import java.util.List;
import org.jsoup.Jsoup;
import com.bumptech.glide.Glide;

import nus.iss.androidca.R;

public class ImageAdapter extends RecyclerView.Adapter<ImageAdapter.ImageViewHolder> {

    private List<String> imageUrls;

    public ImageAdapter(List<String> imageUrls) {
        this.imageUrls = imageUrls;
    }

    public static class ImageViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;

        public ImageViewHolder(View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.imageView);
        }
    }

    @Override
    public ImageViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_image, parent, false);
        return new ImageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ImageViewHolder holder, int position) {
        String url = imageUrls.get(position);
        Log.d("ImageAdapter", "Binding image URL: " + url);
        if (!url.isEmpty()) {
            Glide.with(holder.itemView.getContext())
                    .load(url)
                    .into(holder.imageView);
        } else {
            holder.imageView.setImageResource(R.drawable.placeholder);
        }
    }

    @Override
    public int getItemCount() {
        return imageUrls.size();
    }

    public void updateData(List<String> newImageUrls) {
        if (!newImageUrls.isEmpty()) {
            imageUrls.clear();
            imageUrls.addAll(newImageUrls);
            notifyDataSetChanged();
            Log.d("ImageAdapter", "Data updated: " + newImageUrls);
        }
    }
}
