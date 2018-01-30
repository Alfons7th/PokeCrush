package com.example.fifol.projectcandy;

import android.content.Context;
import android.support.v7.widget.AppCompatImageView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.example.fifol.projectcandy.Screens.SceneScreen;

import java.util.Random;

/* Created by Alfi Naim with the kadosh baruch hu on 02/10/2017.
 *  This game inspired by "Candy Crush".
 */

public class Pokemon extends AppCompatImageView {

    private Context context;
    private int row;
    private int column;
    private int type;
    private boolean alive;

    private static Random random = new Random();

    private static int maxTypes = 6;
    private static int[] pokemonTypes = {
            R.drawable.bullbasaur,
            R.drawable.charmander,
            R.drawable.jigglypuff,
            R.drawable.snorlax,
            R.drawable.meowth,
            R.drawable.pikachu };

    // Constractor.
    public Pokemon(Context context, int column, int row, int type) {
        super(context);
        this.context = context;
        this.row = row;
        this.column = column;
        this.type = type;
        this.alive = true;
    }

    public void setRow(int row) {
        this.row = row;
    }

    public int getRow() {
        return row;
    }


    public void setColumn(int column) {
        this.column = column;
    }

    public int getColumn() {return column;}


    public void setType(int type) {
        this.type = type;
    }

    public int getType() {
        return type;
    }


    public void setAlive(boolean alive) {
        this.alive = alive;
    }

    public boolean isAlive() {
        return alive;
    }


    public static int typeRandomize(){
        return pokemonTypes[random.nextInt(maxTypes)];
    }
}
