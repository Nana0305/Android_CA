package com.example.matchingcard;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.os.Bundle;
import androidx.constraintlayout.helper.widget.Flow;
import android.os.Handler;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public class GameActivity extends AppCompatActivity implements View.OnClickListener {
	private int clickCount = 0;
	private ImageButton firstClickedBtn = null;
	private int matchCount = 0;
	private int [] ids = new int[12];
	private final Handler handler = new Handler();
	private long startTime = 0;
	private TextView timerTextView;
	private MediaPlayer mediaPlayer;
	private String imageDirectory;
	private final List<Drawable> drawables = new ArrayList<>();
	private final ArrayList<Drawable> drawableList = new ArrayList<>();
	private Flow picGrid;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_game);

		Intent intent = getIntent();
		if (intent != null && intent.hasExtra("image_directory")) {
			imageDirectory = intent.getStringExtra("image_directory");
		}
		loadDrawablesFromDirectory(imageDirectory);
		Collections.addAll(drawableList, doubleTo12(drawables));
		Collections.shuffle(drawableList);
		ids = setupBtns();
		timerTextView = findViewById(R.id.timer);
		picGrid = findViewById(R.id.pic_grid);
		startTime = System.currentTimeMillis();
		handler.postDelayed(runnable, 0);
	}
	private void loadDrawablesFromDirectory(String directoryPath) {
		File directory = new File(directoryPath);
		if (directory.exists() && directory.isDirectory()) {
			File[] imageFiles = directory.listFiles();
			if (imageFiles != null) {
				for (File imageFile : imageFiles) {
					// Convert file to Drawable
					Drawable drawable = Drawable.createFromPath(imageFile.getAbsolutePath());
					if (drawable != null) {
						drawables.add(drawable);
					}
				}
			}
		}
	}

	private Drawable[] doubleTo12(List<Drawable> originalList){
		Drawable[] doubledArray = new Drawable[12];
		for (int i =0; i <6; i++){
			doubledArray[2*i] = originalList.get(i);
			doubledArray[2*i+1] = originalList.get(i);
		}
		return doubledArray;
	}

	private final Runnable runnable = new Runnable() {
		@Override
		public void run() {
			long millis = System.currentTimeMillis() - startTime;
			int seconds = (int) (millis/1000);
			int minutes = seconds /60;
			int hours = minutes/60;
			seconds = seconds%60;
			minutes = minutes%60;
			timerTextView.setText(String.format("%02d:%02d:%02d", hours, minutes, seconds));
			handler.postDelayed(this, 1000);
		}
	};
	@Override
	protected void onDestroy(){
		super.onDestroy();
		handler.removeCallbacks(runnable);
		clearDownloadedImages();
		String title = getString(R.string.success);
		String msg = getString(R.string.play_again);
		AlertDialog.Builder dlg = new AlertDialog.Builder(this)
				.setTitle(title)
				.setMessage(msg)
				.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						Intent intent = new Intent(GameActivity.this, MainActivity.class);
						finish();
						startActivity(intent);
					}
				})
				.setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						finish();
					}
				})
				.setIcon(android.R.drawable.ic_dialog_alert);
		dlg.show();
	}
	private void clearDownloadedImages(){
		File directory = new File(imageDirectory);
		if (directory.exists() && directory.isDirectory()) {
			File[] imageFiles = directory.listFiles();
			if (imageFiles != null) {
				for (File imageFile : imageFiles) {
					imageFile.delete();
				}
			}
		}
	}

	protected int[] setupBtns() {
		int[] ids = {
				R.id.btn0,
				R.id.btn1,
				R.id.btn2,
				R.id.btn3,
				R.id.btn4,
				R.id.btn5,
				R.id.btn6,
				R.id.btn7,
				R.id.btn8,
				R.id.btn9,
				R.id.btn10,
				R.id.btn11
		};
		for (int id : ids) {
			ImageButton btn = findViewById(id);
			if (btn != null) {
				btn.setOnClickListener(this);
			}
		}
		return ids;
	}

	@Override
	public void onClick(View v) {
		TextView matches = findViewById(R.id.matches);
		ImageButton btn = (ImageButton) v;
		if (clickCount == 0) {
			firstFlip(btn);
			firstClickedBtn = btn;
			clickCount++;
		} else if (clickCount == 1) {
			clickCount++;
			setButtonStatus(ids, false);
			secondFlip(btn, matches);
		} else {
			clickCount = 0;
			firstClickedBtn = null;
		}
	}

	private void firstFlip(ImageButton btn) {
		int index = getButtonIndex(btn.getId());
		if (index != -1) {
			Drawable drawable = drawableList.get(index);
			btn.setImageDrawable(drawable);
			btn.setTag(drawable);
			btn.setEnabled(false);
		}
	}
	private int getButtonIndex(int btnId) {
		for (int i = 0; i < ids.length; i++) {
			if (ids[i] == btnId) {
				return i;
			}
		}
		return -1;
	}

	private void secondFlip(ImageButton btn, TextView matches) {
		firstFlip(btn);
		if (btn.getTag() == firstClickedBtn.getTag()) {
			matchCount++;
			matches.setText(matchCount + " of 6 matches");
			playSound(R.raw.right);
			if (matchCount == 6) {
				if (mediaPlayer != null && mediaPlayer.isPlaying()) {
					mediaPlayer.pause();
				}
				playSound(R.raw.win);
				onDestroy();
			} else {
				clickCount = 0;
				firstClickedBtn = null;
				setButtonStatus(ids, true);
			}
		} else {
			playSound(R.raw.wrong);
			handler.postDelayed(() -> {
				flipBack(btn);
				flipBack(firstClickedBtn);
				clickCount = 0;
				firstClickedBtn = null;
				setButtonStatus(ids, true);
			}, 1000);
		}
	}
	private void playSound(int soundResId){
		if (mediaPlayer != null){
			mediaPlayer.release();
		}
		mediaPlayer = MediaPlayer.create(this, soundResId);
		mediaPlayer.start();
	}

	private void flipBack(ImageButton btn){
		if (btn != null){
			btn.setEnabled(true);
			btn.setImageResource(R.drawable.pic_back_cross);
			btn.setTag(null);
		}
	}

	private void setButtonStatus(int[] ids, boolean clickable){
		for (int id : ids) {
			ImageButton btn = findViewById(id);
			if (btn != null) {
				btn.setClickable(clickable);
			}
		}
	}
}