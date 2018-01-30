package com.example.fifol.projectcandy;

import com.backendless.Backendless;
import com.backendless.BackendlessUser;
import com.backendless.async.callback.AsyncCallback;
import com.backendless.exceptions.BackendlessFault;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/* Created by Alfi Naim with the kadosh baruch hu on 02/10/2017.
 *  This game inspired by "Candy Crush".
 */

public class BackendlessHelper {

    private static BackendlessHelper backendlessHelper = new BackendlessHelper(); // Single instance
    private  BackendlessHelper(){} // Private constructor.

    private static BackendlessUser user;

    private Map<Integer, Integer> levelMaxSteps = new HashMap<>();
    private Map<Integer, Integer> levelTargetScores = new HashMap<>();
    private Map<Integer, Integer> levelColumns = new HashMap<>();
    private Map<Integer, Integer> levelRows = new HashMap<>();
    private Map<Integer, Integer> numOfPokemon = new HashMap<>();

    private int currentLevel = 1, masterBall = 1, punch = 1, tornado = 1;
    private boolean firstEnter = true;
    private String name = "Error";

    public void prepareUser(BackendlessUser user){
        currentLevel = ((Integer) user.getProperty("currentLevel"));
        masterBall = ((Integer) user.getProperty("masterBall"));
        punch = ((Integer) user.getProperty("punch"));
        tornado = ((Integer) user.getProperty("tornado"));
        firstEnter = ((boolean)user.getProperty("firstEnter"));
        setName((String)user.getProperty("name"));
        BackendlessHelper.user = user;
        loadLevelDetails();
    }

    private void loadLevelDetails(){
        Backendless.Data.of("levels").find(new AsyncCallback<List<Map>>() {
            @Override
            public void handleResponse(List<Map> response) {
                for (Map map : response) {
                    levelMaxSteps.put((int) map.get("level"), (int) map.get("steps"));
                    levelTargetScores.put((int) map.get("level"), (int) map.get("score"));
                    levelColumns.put((int) map.get("level"), (int) map.get("column"));
                    levelRows.put((int) map.get("level"), (int) map.get("row"));
                    numOfPokemon.put((int) map.get("level"), (int) map.get("numOfPokemon"));
                }
            }
            @Override
            public void handleFault(BackendlessFault fault) {
                System.out.println("Failed loading level details");
            }
        });
    }

    public synchronized static BackendlessHelper getInstance(){
        if (backendlessHelper == null){ //if there is no instance available... create new one
            backendlessHelper = new BackendlessHelper();
        }
        return backendlessHelper;
    }

    private void updateUser(){
        Backendless.UserService.update(user, new AsyncCallback<BackendlessUser>() {
            @Override
            public void handleResponse(BackendlessUser response) {
                System.out.println("Update user Succeeded");
            }
            @Override
            public void handleFault(BackendlessFault fault) {
                System.out.println("Update user failed");
            }
        });
    }

    public void updateLevelUp(){
        currentLevel++;
        user.setProperty("currentLevel",currentLevel);
        updateUser();
    }

    public int getCurrentLevel() {
        return currentLevel;
    }

    public Map getLevelMaxSteps() {
        return levelMaxSteps;
    }

    public Map getLevelTargetScores() {
        return levelTargetScores;
    }

    public Map getLevelColumns() { return levelColumns; }

    public Map getLevelRows() { return levelRows; }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public boolean isFirstEnter() {return firstEnter; }

    public void setFirstEnter(boolean isFirstEnter) {
        this.firstEnter = isFirstEnter;
        user.setProperty("firstEnter",isFirstEnter);
        updateUser();
    }

    public int getMasterBall() {return masterBall; }

    public void subMasterBall() {
        this.masterBall--;
        user.setProperty("masterBall",masterBall);
        updateUser();
    }

    public void addMasterBall() { //TODO in the future.
        this.masterBall++;
        user.setProperty("masterBall",masterBall);
        updateUser();
    }

    public int getTornado() {return tornado; }

    public void subTornado() {
        this.tornado--;
        user.setProperty("tornado",tornado);
        updateUser();
    }

    public void addTornado() { //TODO in the future.
        this.tornado++;
        user.setProperty("tornado",tornado);
        updateUser();
    }

    public int getPunch() {return punch; }

    public void subPunch() {
        this.punch--;
        user.setProperty("punch",punch);
        updateUser();
    }

    public void addPunch() { //TODO in the future.
        this.punch++;
        user.setProperty("punch",punch);
        updateUser();
    }
}

