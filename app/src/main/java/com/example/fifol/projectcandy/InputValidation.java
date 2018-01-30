package com.example.fifol.projectcandy;

import android.widget.EditText;

/* Created by Alfi Naim with the kadosh baruch hu on 02/10/2017.
 *  This game inspired by "Candy Crush".
 */

public class InputValidation {

    public boolean isInputEmpty(EditText input){
        String inputText = input.getText().toString().trim();
        return(inputText.isEmpty());
}

    public boolean onlyDigitsAndLetters(EditText input){
        String inputText = input.getText().toString().trim();
        return inputText.matches("^[a-zA-Z0-9]*$");
    }

    public boolean isEmailValid(EditText email){
       String emailText = email.getText().toString().trim();
       return (android.util.Patterns.EMAIL_ADDRESS.matcher(emailText).matches());
    }
}
