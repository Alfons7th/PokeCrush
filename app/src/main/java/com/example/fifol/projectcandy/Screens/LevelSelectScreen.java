package com.example.fifol.projectcandy.Screens;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import com.example.fifol.projectcandy.R;

/* Created by Alfi Naim with the kadosh baruch hu on 02/10/2017.
 *  This game inspired by "Candy Crush".
 */

public class LevelSelectScreen extends BaseActivity {

    private int currentLevel;
    private LinearLayout main;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_level_select);

        main = findViewById(R.id.main);

        currentLevel = backendlessHelper.getCurrentLevel();
        buildScene();
    }

    private void buildScene() {
        int counter = 1,row = 5, column = 5;
        for (int i = 0; i < row; i++) {
            LinearLayout linearLayout = (LinearLayout) LayoutInflater.from(this).inflate(R.layout.level_select_row, main, false);
            main.addView(linearLayout);
            for (int j = 0; j < column; j++) {
                Button box = (Button) LayoutInflater.from(this).inflate(R.layout.level_select_btn, linearLayout, false);
                if (counter <= currentLevel) {
                    box.setBackgroundResource(R.drawable.select_level_button);
                    box.setOnClickListener(levelChosen);
                } else
                    box.setBackgroundResource(R.drawable.lock);
                box.setText(String.valueOf(counter++));
                linearLayout.addView(box);
            }
        }
    }

    View.OnClickListener levelChosen = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            makeSoundEnter();
            int number = Integer.parseInt(((Button) v).getText().toString());
            Intent i = new Intent(LevelSelectScreen.this, SceneScreen.class);
            i.putExtra("number", number);
            startActivity(i);
        }
    };

    @Override
    public void onBackPressed() {
        moveToActivity(LoggedUserScreen.class);
    }

    public void backBtnClick(View view) {
        moveToActivity(LoggedUserScreen.class);
    }
}