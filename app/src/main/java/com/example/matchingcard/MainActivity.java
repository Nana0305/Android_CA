package com.example.matchingcard;

import android.content.DialogInterface;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Looper;
import android.os.Handler;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.Collections;


public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private int clickCount = 0;
    private ImageButton firstClickedBtn = null;
	private int matchCount = 0;
	int [] ids = new int[12];
	private final Handler handler = new Handler();
	private long startTime = 0;
	private TextView timerTextView;
	private MediaPlayer mediaPlayer;
	private final Integer[] drawableIds = {
			R.drawable.floral_0,
			R.drawable.floral_1,
			R.drawable.floral_2,
			R.drawable.floral_3,
			R.drawable.floral_4,
			R.drawable.floral_5,
			R.drawable.floral_0,
			R.drawable.floral_1,
			R.drawable.floral_2,
			R.drawable.floral_3,
			R.drawable.floral_4,
			R.drawable.floral_5
	};
	private final ArrayList<Integer> drawableList = new ArrayList<>();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		ids = setupBtns();
		Collections.addAll(drawableList, drawableIds);
		Collections.shuffle(drawableList);
		timerTextView = findViewById(R.id.timer);
		startTime = System.currentTimeMillis();
		handler.postDelayed(runnable, 0);
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
		String title = getString(R.string.success);
		String msg = getString(R.string.play_again);
		AlertDialog.Builder dlg = new AlertDialog.Builder(this)
				.setTitle(title)
				.setMessage(msg)
				.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						Intent intent = getIntent();
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
		int btnId = btn.getId();
		int resId = -1;
		if (btnId == R.id.btn0) {
			resId = drawableList.get(0);
		} else if (btnId == R.id.btn1) {
			resId = drawableList.get(1);
		} else if (btnId == R.id.btn2){
			resId = drawableList.get(2);
		} else if (btnId == R.id.btn3) {
			resId = drawableList.get(3);
		} else if (btnId == R.id.btn4) {
			resId = drawableList.get(4);
		} else if (btnId == R.id.btn5) {
			resId = drawableList.get(5);
		} else if (btnId == R.id.btn6) {
			resId = drawableList.get(6);
		}else if (btnId == R.id.btn7){
			resId = drawableList.get(7);
		}else if (btnId == R.id.btn8){
			resId = drawableList.get(8);
		}else if (btnId == R.id.btn9){
			resId = drawableList.get(9);
		}else if (btnId == R.id.btn10){
			resId = drawableList.get(10);
		}else if (btnId == R.id.btn11){
			resId = drawableList.get(11);
		}
		if (resId != -1){
			btn.setImageResource(resId);
			btn.setEnabled(false);
			btn.setTag(resId);
		}
	}

	private void secondFlip(ImageButton btn, TextView matches) {
		Handler handler = new Handler(Looper.getMainLooper());
		firstFlip(btn);
		if (getBtnDrawableId(btn) == getBtnDrawableId(firstClickedBtn)) {
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
			btn.setTag(R.drawable.pic_back_cross);
		}
	}

	private int getBtnDrawableId(ImageButton btn){
		if (btn != null && btn.getDrawable() != null) {
			return (Integer) btn.getTag();
		}
		return -1;
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