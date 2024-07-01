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

import org.w3c.dom.Text;

import java.util.Timer;
import java.util.TimerTask;


public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private int clickCount = 0;
    private ImageButton firstClickedBtn = null;
	private int matchCount = 0;
	int [] ids = new int[12];
	private Handler handler = new Handler();
	private long startTime = 0;
	private TextView timerTextView;
	private MediaPlayer mediaPlayer;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		ids = setupBtns();
		timerTextView = findViewById(R.id.timer);
		startTime = System.currentTimeMillis();
		handler.postDelayed(runnable, 0);
	}
	private Runnable runnable = new Runnable() {
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
		if (mediaPlayer != null) {
			mediaPlayer.release();
			mediaPlayer = null;
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
		int id = btn.getId();
		int resId = -1;
		if (id == R.id.btn0 || id == R.id.btn6) {
			resId = R.drawable.floral_1;
		} else if (id == R.id.btn1 || id == R.id.btn7) {
			resId = R.drawable.floral_2;
		} else if (id == R.id.btn2 || id == R.id.btn8) {
			resId = R.drawable.floral_3;
		} else if (id == R.id.btn3 || id == R.id.btn9) {
			resId = R.drawable.floral_4;
		} else if (id == R.id.btn4 || id == R.id.btn10) {
			resId = R.drawable.floral_5;
		} else if (id == R.id.btn5 || id == R.id.btn11) {
			resId = R.drawable.floral_6;
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
		handler.postDelayed(() -> {
			if (getBtnDrawableId(btn) != getBtnDrawableId(firstClickedBtn)) {
				flipBack(btn);
				flipBack(firstClickedBtn);
			} else {
				matchCount++;
				matches.setText(matchCount + " of 6 matches");
				mediaPlayer = MediaPlayer.create(this, R.raw.right);
				mediaPlayer.start();
				if (matchCount == 6){
					onDestroy();
				}
			}
			clickCount = 0;
			firstClickedBtn = null;
			mediaPlayer = MediaPlayer.create(this, R.raw.wrong);
			mediaPlayer.start();
			setButtonStatus(ids, true);
			}, 1000);
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