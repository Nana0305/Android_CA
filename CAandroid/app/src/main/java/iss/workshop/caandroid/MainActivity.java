package iss.workshop.caandroid;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;

import nus.iss.androidca.R;

public class MainActivity extends AppCompatActivity {
    private EditText urlInput;
    private Button fetchButton;
    private ProgressBar progressBar;
    private TextView progressText;
    private RecyclerView recyclerView;
    private ImageAdapter adapter;

    private List<String> imageUrls = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main); // 确保 XML 文件正确引用并存在

        urlInput = findViewById(R.id.urlInput);
        fetchButton = findViewById(R.id.fetchButton);
        progressBar = findViewById(R.id.progressBar);
        progressText = findViewById(R.id.progressText);
        recyclerView = findViewById(R.id.recyclerView);

        for (int i = 1; i <= 20; i++) {
            imageUrls.add("");
        }

        adapter = new ImageAdapter(imageUrls);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 4));
        recyclerView.setAdapter(adapter);

        fetchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String url = urlInput.getText().toString();
                if (!url.startsWith("http://") && !url.startsWith("https://")) {
                    url = "https://" + url;
                }
                fetchImages(url);
            }
        });
    }

    private void fetchImages(String url) {
        progressBar.setVisibility(View.VISIBLE);
        progressText.setVisibility(View.VISIBLE);
        progressText.setText("Downloading 0 of 20 images...");

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    URL urlObj = new URL(url);
                    URLConnection connection = urlObj.openConnection();
                    connection.setRequestProperty(
                            "User-Agent",
                            "Mozilla/5.0 (Macintosh; U; Intel Mac OS X 10.4; en-US; rv:1.9.2.2) Gecko/20100316 Firefox/3.6.2"
                    );

                    InputStream inputStream = connection.getInputStream();
                    Document doc = Jsoup.parse(inputStream, "UTF-8", url);

                    Elements images = doc.select("img[src]");
                    int totalImages = Math.min(images.size(), 20);
                    List<String> newImageUrls = new ArrayList<>();

                    for (int i = 0; i < totalImages; i++) {
                        String imgUrl = images.get(i).absUrl("src");
                        if (!imgUrl.isEmpty()) {
                            newImageUrls.add(imgUrl);
                            int finalI = i;
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    progressText.setText("Downloading " + (finalI + 1) + " of " + totalImages + " images...");
                                    progressBar.setProgress((finalI + 1) * 100 / totalImages);
                                    Log.d("MainActivity", "Downloading image: " + imgUrl);
                                }
                            });
                        }
                    }

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            progressBar.setVisibility(View.GONE);
                            progressText.setVisibility(View.GONE);
                            adapter.updateData(newImageUrls);
                            Log.d("MainActivity", "Images updated: " + newImageUrls);
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            progressBar.setVisibility(View.GONE);
                            progressText.setText("Error fetching images: " + e.getMessage());
                            Log.e("MainActivity", "Error fetching images: " + e.getMessage());
                        }
                    });
                }
            }
        }).start();
    }
}
