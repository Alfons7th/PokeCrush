package com.example.fifol.projectcandy;

import com.example.fifol.projectcandy.Screens.SceneScreen;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;

/* Created by Alfi Naim with the kadosh baruch hu on 02/10/2017.
 *  This game inspired by "Candy Crush".
 */

public class Table {

    private int maxColumn, maxRow;
    private LinearLayout tableRoot, columns[];
    private Context context;

    public Table(Context context, LinearLayout tableRoot, int maxColumn, int maxRow) {
        this.context = context;
        this.tableRoot = tableRoot;
        this.maxColumn = maxColumn;
        this.maxRow = maxRow;
        this.columns = new LinearLayout[this.maxColumn];
        printTableProperties();
    }

    public void createTable(Boolean pokemon){
        for (int i = 0; i < this.maxColumn; i++) {
            LinearLayout column = (LinearLayout) LayoutInflater.from(this.context).inflate(R.layout.column, this.tableRoot, false);
            this.tableRoot.addView(column);
            this.columns[i] = column;
            if(!pokemon){
                for (int j = 0; j < this.maxRow; j++)
                    column.addView(createDarkCell());
            }
        }
    }

    // Inflate a view from "back" layout on the containerView - It's a DarkCell.
    private View createDarkCell(){
        return LayoutInflater.from(this.context).inflate(R.layout.back, this.tableRoot, false);
    }


    private void printTableProperties(){
        System.out.println("***************--Table--***************");
        System.out.println("tableRoot: "+ this.tableRoot.getResources().getResourceEntryName(this.tableRoot.getId()));
        System.out.println("Column: "+this.maxColumn);
        System.out.println("Row: "+this.maxRow);
        System.out.println("***************************************");
    }

    public Pokemon getPokemonByCoordinates(int column, int row) {
       return (Pokemon)columns[column].getChildAt(row);
    }

    private void checkOneColumn(int column) {

        int tempType = 0;
        int counter = 0;

        for (int i = 0; i < this.maxRow; i++) {
            Pokemon pokemon = getPokemonByCoordinates(column,i);
            if (pokemon.getType() != tempType) {
                if (counter > 2)
                    addColumnToTrash(column, i, counter);

                tempType = pokemon.getType();
                counter = 1;

            } else
                counter++;
        }if(counter > 2)
            addColumnToTrash(column,this.maxRow,counter);
    }

    private void checkOneRow(int row) {

        int tempType = 0;
        int counter = 0;

        for (int i = 0; i < this.maxColumn; i++) {
            Pokemon pokemon = getPokemonByCoordinates(i,row);
            if (pokemon.getType() != tempType) {
                if (counter > 2)
                    addRowToTrash(row, i, counter);

                tempType = pokemon.getType();
                counter = 1;

            } else
                counter++;
        }if(counter > 2)
            addRowToTrash(row,this.maxColumn,counter);
    }

    private void addColumnToTrash(int column, int lastIndex, int counter) {
        for (int i = lastIndex - counter ; i < lastIndex ; i++)
            SceneScreen.pokemonTrash.add(getPokemonByCoordinates(column,i));
    }

    private void addRowToTrash(int row, int lastIndex, int counter) {
        for (int i = lastIndex - counter ; i < lastIndex ; i++)
            SceneScreen.pokemonTrash.add(getPokemonByCoordinates(i,row));
    }

    public void checkAllTable() {
        for (int i = 0; i < this.maxColumn; i++)
            checkOneColumn(i);
        for (int i = 0; i < this.maxRow; i++)
            checkOneRow(i);
    }

    public void checkOnlySwapped(Pokemon firstPokemon, Pokemon secondPokemon){

        int firstColumn = firstPokemon.getColumn();
        int firstRow = firstPokemon.getRow();
        int secondColumn = secondPokemon.getColumn();
        int secondRow = secondPokemon.getRow();

        if(firstColumn == secondColumn) {
            checkOneColumn(firstColumn);
            checkOneRow(firstRow);
            checkOneRow(secondRow);
        }else{
            checkOneRow(firstRow);
            checkOneColumn(firstColumn);
            checkOneColumn(secondColumn);
        }
    }

    public void refreshPokemonColumns() {
        for (int i = 0; i < this.maxColumn; i++)
            for (int j = 0; j < this.maxRow; j++)
                getPokemonByCoordinates(i,j).setRow(j);
    }

    public int removeBoxes() {
        int count=0;
        LinearLayout parentLayout;
        for(Pokemon pokemon : SceneScreen.pokemonTrash) {
            parentLayout = (LinearLayout) pokemon.getParent();
            parentLayout.removeView(pokemon);
            count++;
        }
        SceneScreen.pokemonTrash.clear();
        return count;
    }

    public LinearLayout getTableRoot(){
        return this.tableRoot;
    }

}