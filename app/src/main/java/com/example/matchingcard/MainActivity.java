package com.example.matchingcard;

import android.os.Bundle;
import android.os.Looper;
import android.os.Handler;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;


public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private int clickCount = 0;
    private ImageButton firstClickedBtn = null;
	private int matchCount = 0;
	int [] ids = new int[12];

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		ids = setupBtns();
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
			}
			clickCount = 0;
			firstClickedBtn = null;
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