package com.example.matchingcard;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import com.squareup.picasso.Picasso;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ImageAdapter extends BaseAdapter {
	private Context context;
	private List<String> imageUrls = new ArrayList<>();
	private Set<Integer> selectedPositions = new HashSet<>();
	private final int MAX_SELECTION = 6;
	private OnSelectionChangedListener onSelectionChangedListener;
	private boolean isGridEnabled = true;

	public ImageAdapter(Context context) {
		this.context = context;
	}

	public void setImageUrls(List<String> urls) {
		this.imageUrls = urls;
		notifyDataSetChanged();
	}

	public void addImageUrl(String url) {
		this.imageUrls.add(url);
	}

	@Override
	public int getCount() {
		return imageUrls.size();
	}

	@Override
	public Object getItem(int position) {
		return imageUrls.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ImageView imageView;
		if (convertView == null) {
			LayoutInflater inflater = LayoutInflater.from(context);
			imageView = (ImageView) inflater.inflate(R.layout.grid_item, parent, false);
		} else {
			imageView = (ImageView) convertView;
		}

		// Set imageView dimensions to be a square
		int width = parent.getWidth() / 4; // 4 columns
		ViewGroup.LayoutParams params = imageView.getLayoutParams();
		params.width = width;
		params.height = width; // height = width for squares
		imageView.setLayoutParams(params);

		// Load image into the ImageView
		Picasso.get().load(imageUrls.get(position)).into(imageView);

		// Handle selection state
		if (selectedPositions.contains(position)) {
			imageView.setAlpha(0.5f); // Indicate selection
		} else {
			imageView.setAlpha(1.0f);
		}

		imageView.setOnClickListener(v -> {
			if (isGridEnabled) {
				if (selectedPositions.contains(position)) {
					selectedPositions.remove(position); // Deselect if already selected
					imageView.setAlpha(1.0f); // Reset alpha
				} else if (selectedPositions.size() < MAX_SELECTION) {
					selectedPositions.add(position); // Select if not selected
					imageView.setAlpha(0.5f); // Dim image
				}

				if (onSelectionChangedListener != null) {
					List<String> selectedImages = new ArrayList<>();
					for (int pos : selectedPositions) {
						selectedImages.add(imageUrls.get(pos));
					}
					onSelectionChangedListener.onSelectionChanged(selectedImages);
				}
			}
		});

		return imageView;
	}

	public void setOnSelectionChangedListener(OnSelectionChangedListener listener) {
		this.onSelectionChangedListener = listener;
	}

	public interface OnSelectionChangedListener {
		void onSelectionChanged(List<String> selectedImages);
	}

	// Method to enable or disable GridView interactions
	public void setGridEnabled(boolean enabled) {
		this.isGridEnabled = enabled;
	}
}