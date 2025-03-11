package com.developerali.bongbilling.Helpers;

import static android.content.Context.MODE_PRIVATE;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Helpers {

    public static String DESCRIPTION;
    public static int DISCOUNT = 0;
    public static String START_DATE;
    public static String END_DATE;

    public static void plusBtn(Context context, TextView textView){
        String no = textView.getText().toString();
        int number = Integer.parseInt(no);

        if (number < 10){
            int newNum = number + 1 ;
            textView.setText(String.valueOf(newNum));
        }else {
            Toast.makeText(context, "value exceed", Toast.LENGTH_LONG).show();
        }
    }

    public static String convertDateFormat(String inputDate) {
        try {
            // Define input and output date formats
            SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            SimpleDateFormat outputFormat = new SimpleDateFormat("dd/MM/yyyy");

            // Parse input date string
            Date date = inputFormat.parse(inputDate);

            // Format and return the new date string
            return outputFormat.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
            return "Invalid Date";
        }
    }

    public static void minusBtn(Context context, TextView textView){
        String no = textView.getText().toString();
        int number = Integer.parseInt(no);

        if (number > 0){
            int newNum = number - 1 ;
            textView.setText(String.valueOf(newNum));
        }else {
            Toast.makeText(context, "value exceed", Toast.LENGTH_LONG).show();
        }
    }

    public static String getLastPartAfterSplit(String input, String delimiter) {
        if (input == null || input.isEmpty()) {
            return "";
        }
        String[] parts = input.split(": ");
        if (parts.length > 1) {
            return parts[parts.length - 1];
        } else {
            return "";
        }
    }

    public static void saveTextToSharedPref(Activity activity, String key, String value) {
        SharedPreferences sharedPreferences = activity.getSharedPreferences("MyPrefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(key, value);
        editor.apply();
    }


    public static String getTextFromSharedPref(Activity activity, String key) {
        SharedPreferences sharedPreferences = activity.getSharedPreferences("MyPrefs", MODE_PRIVATE);
        return sharedPreferences.getString(key, null);
    }


    public static String capitalizeSentences(String input) {
        if (input == null || input.isEmpty()) return input;
        String[] sentences = input.split("(?<=[.!?])\\s*"); // Split sentences using punctuation marks
        StringBuilder result = new StringBuilder();

        for (String sentence : sentences) {
            if (!sentence.isEmpty()) {
                String[] words = sentence.split("\\s+");
                StringBuilder capitalizedSentence = new StringBuilder();

                for (String word : words) {
                    if (!word.isEmpty()) {
                        capitalizedSentence.append(Character.toUpperCase(word.charAt(0)))
                                .append(word.substring(1)) // Append the rest of the word
                                .append(" "); // Add a space after each word
                    }
                }
                result.append(capitalizedSentence.toString().trim())
                        .append(" ");
            }
        }

        return result.toString().trim(); // Remove any extra spaces at the end
    }


    public static void setCapitalizedTextWatcher(EditText editText) {
        editText.addTextChangedListener(new TextWatcher() {
            private boolean isEditing = false;
            private int cursorPosition = 0;

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                cursorPosition = editText.getSelectionStart();
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                if (isEditing) return;

                isEditing = true;
                String originalText = s.toString();
                String formattedText = capitalizeEachWord(originalText);

                if (!originalText.equals(formattedText)) {
                    editText.setText(formattedText);
                    editText.setSelection(Math.min(cursorPosition, formattedText.length())); // Restore cursor position
                }
                isEditing = false;
            }
        });
    }

    private static String capitalizeEachWord(String input) {
        if (input == null || input.isEmpty()) return input;
        String[] words = input.split(" ");
        StringBuilder result = new StringBuilder();
        for (String word : words) {
            if (!word.isEmpty()) {
                result.append(Character.toUpperCase(word.charAt(0)))
                        .append(word.substring(1).toLowerCase());
            }
            result.append(" ");
        }
        return result.toString().trim();
    }


}
