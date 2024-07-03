package com.example.flipcard;

import java.util.ArrayList;
import java.util.Collections;

import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.ImageView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity {
    private GridLayout gridLayout;
    private ImageView[] imageViews = new ImageView[12];
    private int[] images = {
            R.drawable.lakers, R.drawable.lakers,
            R.drawable.nuggets, R.drawable.nuggets,
            R.drawable.celtics, R.drawable.celtics,
            R.drawable.cavaliers, R.drawable.cavaliers,
            R.drawable.hawks, R.drawable.hawks,
            R.drawable.pacers, R.drawable.pacers
    };

    private int[] cardState = new int[12]; // store card in button
    private int clickedFirst, clickedSecond;
    private int cardNumber;
    private boolean isStop;
    private int counter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initgame();

        gridLayout = findViewById(R.id.gridLayout);
        for (int i = 0; i < gridLayout.getChildCount(); i++) {
            imageViews[i] = (ImageView) gridLayout.getChildAt(i);
            imageViews[i].setTag(i);
            imageViews[i].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int theCard = (int)v.getTag();
                    if (!isStop) {
                        doStuff(imageViews[theCard], theCard);
                    }
                }
            });
        }
    }
    private void initgame(){
        counter = 0;
        isStop = false;
        cardNumber = 1;
        shuffleCards();
        gridLayout = findViewById(R.id.gridLayout);
        for (int i = 0; i < imageViews.length; i++) {
            imageViews[i] = (ImageView)gridLayout.getChildAt(i);
            imageViews[i].setEnabled(true);
            imageViews[i].setImageResource(R.drawable.card_back);
        }
        for (int i = 0; i< cardState.length; i++){
            cardState[i] = -1;
        }
    }
    private void shuffleCards() {
        ArrayList<Integer> imageList = new ArrayList<>();
        for (int image : images) {
            imageList.add(image);
        }
        Collections.shuffle(imageList);
        for (int i = 0; i < images.length; i++) {
            images[i] = imageList.get(i);
        }
    }
    private void doStuff(ImageView imageView, int card) {

        if (cardState[card] == -1) {
            cardState[card] = images[card];// store image
        }
        imageView.setImageResource(cardState[card]);
        if (cardNumber == 1) {
            clickedFirst = card;
            cardNumber = 2;
            imageView.setEnabled(false);
        } else if (cardNumber == 2) {
            clickedSecond = card;
            cardNumber = 1;
            isStop = true;
            imageView.setEnabled(false);

            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    checkIfMatched();
                }
            }, 1000);

        }
    }
    private void checkIfMatched(){
        if (images[clickedFirst] == images[clickedSecond]) {
            imageViews[clickedFirst].setVisibility(View.VISIBLE);
            imageViews[clickedSecond].setVisibility(View.VISIBLE);
            // Disable matched cards
            imageViews[clickedFirst].setEnabled(false);
            imageViews[clickedSecond].setEnabled(false);
            counter++;
            checkEnd();
        } else {
            imageViews[clickedFirst].setImageResource(R.drawable.card_back);
            imageViews[clickedSecond].setImageResource(R.drawable.card_back);
            imageViews[clickedFirst].setEnabled(true);
            imageViews[clickedSecond].setEnabled(true);
        }
        isStop = false;
    }
    private void checkEnd(){
        if (counter == 6){
            String message = getString(R.string.message);
            AlertDialog.Builder dlg = new AlertDialog.Builder(this)
                    .setMessage(message)
                    .setPositiveButton(R.string.try_again, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            initgame();
                        }
                    });
            dlg.show();
        }
    }
}