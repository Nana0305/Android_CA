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
			if (selectedPositions.contains(position)) {
				selectedPositions.remove(position); // Deselect if already selected
				imageView.setAlpha(1.0f); // Reset alpha
			} else if (selectedPositions.size() < MAX_SELECTION) {
				selectedPositions.add(position); // Select the image
				imageView.setAlpha(0.5f); // Indicate selection
			}
		});

		return imageView;
	}

	public List<String> getSelectedImages() {
		List<String> selectedImages = new ArrayList<>();
		for (int pos : selectedPositions) {
			selectedImages.add(imageUrls.get(pos));
		}
		return selectedImages;
	}
}


    /* How View.inflate works
    - this method inflates a new view hierarchy from the specified XML resource
    - inflating = convert XML into actual view objects
    'context':
    - refers to the context in which the view should be created (i.e. which activity/fragment etc)
    'R.layout.grid_item':
    - refers to the resource ID of the layout file that defines the view
    - this is where the 'ImageView' layout is specified
    'null':
    - 3rd parameter can be the parent 'ViewGroup' to which the new view will be attached
    - passing null means the new view wont be attached to any parent automatically
    */

    /*
    Detailed explanation on how getView works with GridView
    getView is a method used by GridView (Android framework handles this for us automatically)

    1) Initialization (in MainActivity)
    - GridView is defined in the XML layout file
    - ImageAdapter is created and set to the GridView

    2) Populating data
    - GridView calls 'getCount()' to determine how many items to display
    - For each visible item, GridView calls 'getView()' to get the view for that item

    3) View recycling
    - GridView uses 'convertView' parameter to reuse old views to improve performance
    - If 'convertView' is null, a new view is created. If not, the old view is reused

    4) Data changes
    - when the data changes, 'notifyDataSetChanged()' method is called on the adapter
    - note that this is a BaseAdapter class method we are using, hence no need for implementation
    - this prompts GridView to refresh and call getView() for the items with the updated data

    Android Framework Responsibilities (what Android does for us behind the scenes):
    1. View recycling
    - Android handles the recycling of views via the 'convertView' mechanism to optimize
    memory usage and performance
    2. Calling Adapter methods
    - Android framework auto-calls the 'getView()', 'getCount()', 'getItem()' and 'getItemId()'
    as required to populate GridView
    3. Layout management
    - Layout of each item in the grid is managed by GridView using the views provided by
    the adapter

    Developer Responsibilities (what we have to manually code out)
    1. Implement adapter methods
    - We need to implement the required methods in the adapter to provide the data and views
    - getView is a particularly important method to create/update item views

    2. Set adapter
    - need to set adapter to the GridView (using .setAdapter() method on the gridview object
    and passing in the adapter object (refer to MainActivity.java)

    3. Handle data changes
    - update the data in the adapter and call 'notifyDataSetChanged()' to refresh view
    when data changes

    */