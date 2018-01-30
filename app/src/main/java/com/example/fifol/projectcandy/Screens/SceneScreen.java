package com.example.fifol.projectcandy.Screens;

import com.example.fifol.projectcandy.Pokemon;
import com.example.fifol.projectcandy.R;
import com.example.fifol.projectcandy.Table;

import android.animation.Animator;
import android.animation.TimeInterpolator;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Typeface;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.SoundPool;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.view.ViewCompat;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;

/* Created by Alfi Naim with the kadosh baruch hu on 02/10/2017.
 *  This game inspired by "Candy Crush".
 */

public class SceneScreen extends BaseActivity{

    public static Set<Pokemon> pokemonTrash = new HashSet<>();

    private static SoundPool soundPool;
    private static int swapSound,removeSound,gotchaSound,cantSwapSound,winSound,loseSound;

    private Animation swap_left, swap_right, swap_up, swap_down, cant_swap;
    private TextView targetText,scoreText, movesText, masterBallText, punchText, tornadoText;
    private LinearLayout main,back;

    private int levelNumber, maxlevel, maxColumn, maxRow, moves, targetScore, score = 0;
    private boolean canTouch = true;

    private Timer timer = new Timer();
    private Handler handler = new Handler();

    private Random random = new Random();
    private Table pokemonTable;

    private float pokemonWidth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scene);

        // Get the width of the screen divide it by 9 and get approximately Pokemon width.
        pokemonWidth = (Resources.getSystem().getDisplayMetrics().widthPixels)/9;

        // Change the layout direction of "fullScreen" to - left to right.
        ViewCompat.setLayoutDirection(findViewById(R.id.fullScreen), ViewCompat.LAYOUT_DIRECTION_LTR);
        loadLevelInfo(); // Loads the Current level number and all other details(column number, row number ...) with his value.

        // Layouts.
        main = findViewById(R.id.main);
        back = findViewById(R.id.back);
        // TextViews.
        targetText = findViewById(R.id.targetText);
        scoreText = findViewById(R.id.scoreText);
        movesText = findViewById(R.id.movesText);
        masterBallText = findViewById(R.id.masterBallText);
        tornadoText = findViewById(R.id.tornadoText);
        punchText = findViewById(R.id.punchText);
        //Load animations.
        swap_left = AnimationUtils.loadAnimation(this, R.anim.swap_left);
        swap_right = AnimationUtils.loadAnimation(this, R.anim.swap_right);
        swap_up = AnimationUtils.loadAnimation(this, R.anim.swap_up);
        swap_down = AnimationUtils.loadAnimation(this, R.anim.swap_down);
        cant_swap = AnimationUtils.loadAnimation(this, R.anim.cant_swap);
        //Set listeners.
        findViewById(R.id.masterBallImg).setOnClickListener(masterballClicked);
        findViewById(R.id.punchImg).setOnClickListener(punchClicked);
        findViewById(R.id.tornadoImg).setOnClickListener(tornadoClicked);
        findViewById(R.id.door).setOnClickListener(doorClicked);
        // Load and prepare the sounds.
        prepareSounds();
        // Create the Pokemon table.
        createPokemonTable();
        // Create table in the background - DarkCellTable.
        createBackTable();
        // Set fonts and textViews.
        setTexts();
        // Starts a game with a Pokemon table that has no matches.
        startWithoutMatches();
    }

    // Loads the Current level number and all other details(column number, row number ...) with his value.
    private void loadLevelInfo(){
        levelNumber = getIntent().getIntExtra("number",1);
        maxColumn = (int)backendlessHelper.getLevelColumns().get(levelNumber);
        maxRow = (int)backendlessHelper.getLevelRows().get(levelNumber);
        targetScore = (int)backendlessHelper.getLevelTargetScores().get(levelNumber);
        maxlevel = (int)backendlessHelper.getLevelTargetScores().get(999999);
    }

    // Load all the sounds used by this activity.
    private void prepareSounds(){
        soundPool = new SoundPool(6, AudioManager.STREAM_MUSIC,0);
        swapSound = soundPool.load(this, R.raw.swap,1);
        cantSwapSound = soundPool.load(this, R.raw.cant_swap,1);
        removeSound = soundPool.load(this, R.raw.remove,1);
        gotchaSound = soundPool.load(this, R.raw.gotcha,1);
        loseSound = soundPool.load(this, R.raw.lose,1);
        winSound = soundPool.load(this, R.raw.win,1);
    }

    // Create An instance of table - PokemonTable, create the view of the table and fill it with Pokemons.
    private void createPokemonTable() {
        main.setTranslationY(-(pokemonWidth)*(9-maxRow)/2); // Lift the view up for better look.
        pokemonTable = new Table(this,main,maxColumn,maxRow);
        pokemonTable.createTable(true);
        // Add Pokemons to the Pokemon table.
        fillTableWithPokemons();
    }

    // Create An instance of table - darkCellTable, create the view of the table and fill it with DarkCells.
    private void createBackTable() {
        back.setTranslationY(-(pokemonWidth)*(9-maxRow)/2); // Lift the view up for better look.
        Table darkCellTable = new Table(this,back,maxColumn,maxRow);
        darkCellTable.createTable(false);
    }

    // Fill the empty spots in the table with Pokemons.
    private void fillTableWithPokemons() {
        for (int i = 0; i < maxColumn; i++) {
            for (int j = maxRow - ((LinearLayout) pokemonTable.getTableRoot().getChildAt(i)).getChildCount() - 1; j >= 0; j--)
                ((LinearLayout) pokemonTable.getTableRoot().getChildAt(i)).addView(CreatePokemon(i, j), 0);
        }
    }

    // Create a Pokemon with row, column and type, Inflate it, add an image according to the type and add touchListener.
    private Pokemon CreatePokemon(int column, int row) {
        Pokemon pokemon = new Pokemon(this,column,row,Pokemon.typeRandomize());
        pokemon.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,0, 1.0f));
        pokemon.setImageResource(pokemon.getType());
        pokemon.setOnTouchListener(PokemonTouchListener);
        return pokemon;
    }

    //This Listener works when the player touch a POKEMON object.
    private View.OnTouchListener PokemonTouchListener = new View.OnTouchListener() {
        int startX, startY, endX, endY;
        
        @Override
        // If "canTouch" is "false" Ignore touch, if "true" get touch events.
        public boolean onTouch(View firstPokemon, MotionEvent event) {
            if(!canTouch)
                return true;

            switch (event.getAction()) {
                // When the player touch the screen, get the the X & Y position of the touch.
                case MotionEvent.ACTION_DOWN:
                    ((ImageView) firstPokemon).setImageAlpha(80);
                    startX = (int) event.getX();
                    startY = (int) event.getY();
                    break;

                // When the touch is ended, get the last X & Y position of the touch.
                case MotionEvent.ACTION_UP:
                    ((ImageView) firstPokemon).setImageAlpha(255);
                    endX = (int) event.getX();
                    endY = (int) event.getY();
                    // When listener has start and and X & Y positions, block all touch on Pokemon.
                    blockTouch();
                    // Start analyzing the touch.
                    analyzeSwipeEvent((Pokemon) firstPokemon, startX, startY, endX, endY);
                    break;
            }
            return true;
        }
    };

    // Analyze the touch event.
    private void analyzeSwipeEvent(Pokemon firstPokemon, int startX, int startY, int endX, int endY) {
        // If there is direction continue, if null - break and allow touch.
        String direction = getDirection(startX, startY, endX, endY);
        if (direction!= null){
            // In case that the swipe is in the table bounds, return the nearest Pokemon in this direction, if null - break and allow touch.
            Pokemon secondPokemon = getSecondPokemon(firstPokemon, direction);
            if(secondPokemon!= null) {
                // Check if swapping the two Pokemons can make a Match of three or more Pokemons.
                if(checkIfSwappable(firstPokemon,secondPokemon)) {
                    // If true - swap between the two Pokemon and react to the change.
                    makeSwap(firstPokemon, secondPokemon, direction);
                    // If false - don't swap.
                } else makeCantSwap(firstPokemon,secondPokemon);
            }else allowTouch();
        }else allowTouch();
    }

    // Function that gets the X & Y positions of the Actions (Down, Up) and calculates the direction of the swipe.
    // If the Length and angel of the swipe is ok, returns the direction (String) else returns "Nothing" (String).
    private String getDirection(int startX, int startY, int endX, int endY) {

        int distanceHorizontal = Math.abs(startX - endX);
        int distanceVertical = Math.abs(startY - endY);
        // Maximum sensitivity of swipe angel.
        int maxDiagonalSensitivity = (int) (pokemonWidth); // Responsive to pokemon size.
        // Minimum Length of the swipe.
        int minSwipeLength = (int) (pokemonWidth); // Responsive to pokemon size.

        // return the direction, if the direction isn't legal return null.
        if (distanceHorizontal > minSwipeLength && distanceVertical < maxDiagonalSensitivity) {
            return (startX > endX)?  "Left" :  "Right";
        } else if (distanceVertical > minSwipeLength && distanceHorizontal < maxDiagonalSensitivity) {
            return  (startY > endY)? "Up" : "Down";
        } else return null;
    }

    // Function that gets a Pokemon and direction.
    // If the swipe is within the pokemonTable bounds - return the nearest Pokemon in that direction, else return null.
    private Pokemon getSecondPokemon(Pokemon firstPokemon, String direction) {

        int column = firstPokemon.getColumn();
        int row = firstPokemon.getRow();

        switch (direction) {
            case "Left":
                if(column > 0)
                    return pokemonTable.getPokemonByCoordinates(column-1,row);
                break;
            case "Right":
                if(column < maxColumn -1)
                    return pokemonTable.getPokemonByCoordinates(column+1,row);
                break;
            case "Up":
                if(row > 0)
                    return pokemonTable.getPokemonByCoordinates(column,row-1);
                break;
            case "Down":
                if(row < maxRow -1)
                    return  pokemonTable.getPokemonByCoordinates(column,row+1);
                break;
        }
        return null;
    }

    // Switches the types of two Pokemons and check their rows and columns, return if there is a match.
    private boolean checkIfSwappable(Pokemon firstPokemon,Pokemon secondPokemon) {

        switchTypes(firstPokemon,secondPokemon);
        pokemonTable.checkOnlySwapped(firstPokemon,secondPokemon);
        return (pokemonTrash.size() > 0);
    }

    // Switch types of two Pokemons.
    private void switchTypes(Pokemon first, Pokemon second){

        int temp = first.getType();
        first.setType(second.getType());
        second.setType(temp);
    }

    // Make swap between two pokemons, then check the table for matches.
    private void makeSwap(final Pokemon first, final Pokemon second,String direction) {
        subMove();
        playSound(swapSound);

        makeAnimation(first, second, direction);

        first.postDelayed(new Runnable() {
            @Override
            public void run() {
                first.setImageResource(first.getType());
                second.setImageResource(second.getType());
            }
        }, swap_down.getDuration());

        main.postDelayed(new Runnable() {
            @Override
            public void run() {
                CheckIfGameEnded();
            }
        }, 250);
    }

    // Make sound and animation of the "cantSwap" and switch back the types of the Pokemons.
    private void makeCantSwap(Pokemon first, Pokemon second) {
        playSound(cantSwapSound);
        first.startAnimation(cant_swap);
        second.startAnimation(cant_swap);
        switchTypes(first,second);
        allowTouch();
    }

    // Make the images of all Pokemon in the trash to "Gotcha".
    private void makeGotcha() {
        for(Pokemon pokemon : pokemonTrash)
            pokemon.setImageResource(R.drawable.gotcha);
    }

    // Make animation of swap between two Pokemons.
    private void makeAnimation(Pokemon first, Pokemon second, String direction){
        switch (direction) {
            case "Left":
                first.startAnimation(swap_left);
                second.startAnimation(swap_right);
                break;
            case "Right":
                first.startAnimation(swap_right);
                second.startAnimation(swap_left);
                break;
            case "Up":
                first.startAnimation(swap_up);
                second.startAnimation(swap_down);
                break;
            case "Down":
                first.startAnimation(swap_down);
                second.startAnimation(swap_up);
                break;
        }
    }

    // Function that check if there are matches, if yes - remove them and check again.
    // The function just make the table to be without matches in the beginning of the game.
    private void startWithoutMatches() {
        pokemonTable.checkAllTable();
        while (pokemonTrash.size() > 0) {
            addToScore(pokemonTable.removeBoxes()*50);
            fillTableWithPokemons();
            pokemonTable.refreshPokemonColumns();
            pokemonTable.checkAllTable();
        }
        // Reset the score because the game is not on yet.
        resetScore();
    }

    // Check if there are matches if not pop dialog, else remove the matches.
    // If player hit the score pop playerWinDialog, If player don't have moves pop playerLoseDialog.
    private void CheckIfGameEnded() {
        if(pokemonTrash.size() == 0){
            if(score >= targetScore){
                endGameDialog(true);
                playSound(winSound);
            }
            else if(moves < 1) {
                endGameDialog(false);
                playSound(loseSound);
            }allowTouch();
        }else removeMatches();
    }

    // Make the routine - remove the matches, fill the table, refresh the table and check it again for matches.
    private void removeMatches() {
        playSound(gotchaSound);
        makeGotcha();
        main.postDelayed(new Runnable() {
            @Override
            public void run() {
                addToScore(pokemonTable.removeBoxes()*50);
                main.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        fillTableWithPokemons();
                        playSound(removeSound);
                        pokemonTable.refreshPokemonColumns();
                        pokemonTable.checkAllTable();
                        main.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                CheckIfGameEnded();
                            }
                        },700);
                    }
                }, 600);
            }
        }, 600);
    }

    // Return a random number (Int) between min and max.
    private int randNum(int min,int max){
        return random.nextInt(max)+min;
    }

    // Ignore touches on Pokemons.
    private void blockTouch(){
        canTouch = false;
    }

    // Allow touches on Pokemon.
    private void allowTouch(){
        canTouch = true;
    }

    @Override
    public void onBackPressed() {
        backBtnDialog();
    }

    private void backToLevelSelect() {
        Intent i = new Intent(this,LevelSelectScreen.class);
        startActivity(i);
    }

    // Called when the player hit the score, update the new levelNumber
    private void playerPassLevel(){
        if(backendlessHelper.getCurrentLevel() == levelNumber && levelNumber < maxlevel)
            backendlessHelper.updateLevelUp();
    }

    private void playSound(int sound){
        soundPool.play(sound,1.0f,1.0f,1,0,1.0f);
    }

    //////////////////////////// ------ SETTERS FOR GAME INFO ------////////////////////////////

    private void addToScore(int num){ // Add number (Int) to the score and update scoreText.
        score+=num;
        scoreText.setText(String.valueOf(score));
    }

    private void resetScore(){  // Reset the score (0) and update scoreText.
        score = 0;
        scoreText.setText(String.valueOf(score));
    }

    private void subMove(){  // Subtract by 1 the moves and update movesText.
        moves--;
        movesText.setText(String.valueOf(moves));
    }

    private void resetMoves(){ // Reset the moves and update movesText.
        moves = (int)backendlessHelper.getLevelMaxSteps().get(levelNumber);
        movesText.setText(String.valueOf(moves));
    }

    private void replayLevel() { // Reset the score and moves.
        resetScore();
        resetMoves();
    }

    private void setTexts(){
        // Set new Font - to all textViews & Reset the moves and targetScore
        Typeface typeface = Typeface.createFromAsset(getAssets(), "font/nice-font.ttf");
        scoreText.setTypeface(typeface);
        targetText.setTypeface(typeface);
        movesText.setTypeface(typeface);

        ((TextView)findViewById(R.id.score)).setTypeface(typeface);
        ((TextView)findViewById(R.id.target)).setTypeface(typeface);
        ((TextView)findViewById(R.id.moves)).setTypeface(typeface);

        masterBallText.setText(String.valueOf(backendlessHelper.getMasterBall()));
        tornadoText.setText(String.valueOf(backendlessHelper.getTornado()));
        punchText.setText(String.valueOf(backendlessHelper.getPunch()));

        targetText.setText(String.valueOf(targetScore));
        resetMoves();
        resetScore();

    }

    ///////////////////////////////// ------ LISTENERS ------/////////////////////////////////

    //This Listener works when the player hit the MASTERBALL button.
    View.OnClickListener masterballClicked = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if(!canTouch || backendlessHelper.getMasterBall() < 1) return;
            blockTouch();
            LinearLayout linearLayout = (LinearLayout) main.getChildAt(randNum(0,maxColumn-1));
            pokemonTrash.add((Pokemon) linearLayout.getChildAt(randNum(0,maxRow-1)));
            backendlessHelper.subMasterBall();
            masterBallText.setText(String.valueOf(backendlessHelper.getMasterBall()));
            CheckIfGameEnded();
        }
    };

    //This Listener works when the player hit the PUNCH button.
    View.OnClickListener punchClicked = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if(!canTouch || backendlessHelper.getPunch() < 1) return;
            blockTouch();
            int randX = randNum(1,maxRow-2);
            int randY = randNum(1,maxColumn-2);

            for(int i = randY-1; i < randY+2; i++ ) {
                LinearLayout linearLayout = (LinearLayout) main.getChildAt(i);
                for(int j = randX-1; j <randX +2; j++)
                    pokemonTrash.add((Pokemon) linearLayout.getChildAt(j));
            }
            backendlessHelper.subPunch();
            punchText.setText(String.valueOf(backendlessHelper.getPunch()));
            CheckIfGameEnded();
        }
    };

    //This Listener works when the player hit the TORNADO button.
    View.OnClickListener tornadoClicked = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            int randSesitivity = 8;
            if(!canTouch || backendlessHelper.getTornado() < 1) return;
            blockTouch();
            for (int i = 0; i < maxColumn; i++) {
                LinearLayout linearLayout = (LinearLayout) main.getChildAt(i);
                for (int j = 0; j < maxRow; j++)
                    if (randNum(0,randSesitivity) == 0) pokemonTrash.add((Pokemon) linearLayout.getChildAt(j));
            }
            backendlessHelper.subTornado();
            tornadoText.setText(String.valueOf(backendlessHelper.getTornado()));
            CheckIfGameEnded();
        }
    };

    ////////////////////////////////// ------ DIALOGS ------//////////////////////////////////

    //This Dialog appears when the player hit the BACK button.
    private void backBtnDialog(){
        makeSoundEnter();
        View view = getLayoutInflater().inflate(R.layout.dialog_three_btn, main, false);
        final AlertDialog dialog = new AlertDialog.Builder(this).setView(view).show();
        view.findViewById(R.id.repBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                replayLevel();
                dialog.dismiss();
            }
        });
        view.findViewById(R.id.playOnBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
        view.findViewById(R.id.quitBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                backToLevelSelect();
            }
        });
        dialog.setCanceledOnTouchOutside(false);
        dialog.setCancelable(false);
    }

    //This Dialog appears when the game ENDS.
    private void endGameDialog(final boolean status){

        View view = getLayoutInflater().inflate(R.layout.dialog_two_btn, main, false);
        final AlertDialog dialog = new AlertDialog.Builder(this).setView(view).show();

        TextView title = view.findViewById(R.id.dialog2Ttl);

        if(status) {
            System.out.println(title.getText().toString());
            title.setText(R.string.won);
        }
        else {
            System.out.println(title.getText().toString());
            title.setText(R.string.lost);
        }

        Button leftBtn = view.findViewById(R.id.leftBtn);
        leftBtn.setText(R.string.replay);
        leftBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(status) playerPassLevel();
                replayLevel();
                dialog.dismiss();
            }
        });

        Button rightBtn = view.findViewById(R.id.rightBtn);
        rightBtn.setText(R.string.back);
        rightBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(status) playerPassLevel();
                moveToActivity(LevelSelectScreen.class);
                dialog.dismiss();
            }
        });
        dialog.setCanceledOnTouchOutside(false);
        dialog.setCancelable(false);
    }
}

