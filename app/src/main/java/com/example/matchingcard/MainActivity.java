package com.example.matchingcard;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.EditText;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.squareup.picasso.Picasso;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class MainActivity extends AppCompatActivity {
	private EditText urlInput;
	private GridView imageGrid;
	private Button fetchButton;
	private ProgressBar progressBar;
	private TextView progressText;
	private ImageAdapter imageAdapter;
	private Thread fetchThread;
	private boolean fetching;
	private boolean fetch_completed;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		urlInput = findViewById(R.id.url_input);
		imageGrid = findViewById(R.id.image_grid);
		fetchButton = findViewById(R.id.fetch_button);
		progressBar = findViewById(R.id.progressBar);
		progressText = findViewById(R.id.progress_text);

		imageAdapter = new ImageAdapter(this);
		imageGrid.setAdapter(imageAdapter);
		imageGrid.setEnabled(false);

		fetchButton.setOnClickListener(v -> fetchImageLinks());

		// Listener for image selection changes
		imageAdapter.setOnSelectionChangedListener(this::onImageSelectionChanged);
	}

	private void fetchImageLinks() {
		if (fetching && fetchThread != null) {
			fetch_completed = false;
			fetchThread.interrupt(); // Interrupt the ongoing fetch
		}

		// Clear GridView when a new fetch starts
		if (fetching) {
			imageAdapter.setImageUrls(new ArrayList<>());
			fetch_completed = false;
		}

		fetching = true;
		fetchThread = new Thread(() -> {
			try {
				String url = urlInput.getText().toString();
				fetch_completed = false;

				Document doc = Jsoup.connect(url)
						.userAgent("Mozilla")
						.timeout(10000)
						.get();

				Elements photoItems = doc.select("div.photo-grid-item");
				List<String> imageLinks = new ArrayList<>();
				int totalImages = Math.min(20, photoItems.size());

				Handler handler = new Handler(Looper.getMainLooper());

				handler.post(() -> {
					progressBar.setVisibility(ProgressBar.VISIBLE);
					progressText.setVisibility(TextView.VISIBLE);
					progressText.setText("Downloading 0 out of " + totalImages + " images...");
				});

				for (int i = 0; i < totalImages; i++) {
					if (Thread.currentThread().isInterrupted()) {
						handler.post(() -> {
							progressBar.setVisibility(ProgressBar.GONE);
							progressText.setVisibility(TextView.GONE);
							// Toast message indicating interrupted download
							Toast.makeText(MainActivity.this, "Fetch interrupted.", Toast.LENGTH_LONG).show();
							imageAdapter.setImageUrls(new ArrayList<>()); // Clear GridView
						});
						fetching = false;
						fetch_completed = true;
						return;
					}

					Element item = photoItems.get(i);
					Element img = item.select("img").first();
					if (img != null) {
						String src = img.attr("src");
						imageLinks.add(src);

						int finalI = i + 1;
						handler.post(() -> {
							progressText.setText("Downloading " + finalI + " out of " + totalImages + " images...");
							progressBar.setProgress((int) ((finalI / (float) totalImages) * 100));
						});

						// Load image and update GridView immediately
						final String imageUrl = src;
						handler.post(() -> {
							imageAdapter.addImageUrl(imageUrl); // Add new image URL to adapter
							imageAdapter.notifyDataSetChanged(); // Notify adapter of data change
						});

						try {
							Thread.sleep(600);
						} catch (InterruptedException e) {
							Thread.currentThread().interrupt();
						}
					}
				}

				handler.post(() -> {
					progressBar.setVisibility(ProgressBar.GONE);
					progressText.setVisibility(TextView.GONE);
					fetching = false;
					fetch_completed= true;
					imageAdapter.setFetchCompleted(true);
				});
			} catch (Exception e) {
				Handler handler = new Handler(Looper.getMainLooper());
				handler.post(() -> {
					Toast.makeText(MainActivity.this, "Failed to fetch images.", Toast.LENGTH_LONG).show();
					progressBar.setVisibility(ProgressBar.GONE);
					progressText.setVisibility(TextView.GONE);
					imageAdapter.setImageUrls(new ArrayList<>()); // Clear GridView on error
				});
				fetching = false;
			}
		});
		fetchThread.start();
	}

	private void onImageSelectionChanged(List<String> selectedImages) {
		if (selectedImages.size() == 6) {
			// Start storing images immediately after selecting 6 images
			downloadImages(selectedImages);
		}
	}

	private void downloadImages(List<String> imageUrls) {
		if (!(Objects.equals(Environment.getExternalStorageState(), Environment.MEDIA_MOUNTED))) {
			Toast.makeText(MainActivity.this, "External storage not available.", Toast.LENGTH_LONG).show();
			return;
		}

		File directory = new File(getExternalFilesDir(Environment.DIRECTORY_PICTURES), "DownloadedImages");
		if (!directory.exists()) {
			directory.mkdir();
		}

		for (String imageUrl : imageUrls) {
			try {
				File file = new File(directory, imageUrl.substring(imageUrl.lastIndexOf('/') + 1));
				Picasso.get().load(imageUrl).into(new com.squareup.picasso.Target() {
					@Override
					public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
						try (FileOutputStream fos = new FileOutputStream(file)) {
							bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
						} catch (IOException e) {
							e.printStackTrace();
						}
					}

					@Override
					public void onBitmapFailed(Exception e, Drawable errorDrawable) {
						e.printStackTrace();
					}

					@Override
					public void onPrepareLoad(Drawable placeholderDrawable) {}
				});
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		// Launch the new activity after all images are downloaded
		new Handler(Looper.getMainLooper()).postDelayed(() -> {
			Intent intent = new Intent(MainActivity.this, GameActivity.class);
			intent.putExtra("image_directory", directory.getAbsolutePath());
			startActivity(intent);
		}, 1000); // Adjust delay if necessary
	}
}
