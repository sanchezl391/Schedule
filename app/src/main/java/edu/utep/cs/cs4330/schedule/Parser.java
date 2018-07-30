package edu.utep.cs.cs4330.schedule;

import android.util.Log;

import java.util.List;

public class Parser {

    protected String getKeyword(String text, List<String> categories){ // needs categories and text
        boolean categoryFound = false;

        String lowercaseTitle = text.toLowerCase();
        String category = "";

        for (String cat : categories) { // Higher up keywords take precedence
            String lowercaseCategory = cat.toLowerCase();
            categoryFound = lowercaseTitle.contains(lowercaseCategory);
            if(categoryFound) {
                category = cat;
                break;
            }
        }

        if(category.length() > 0) {
            category = categories.get(categories.indexOf(category));

            Log.e("Found Keyword: ", category);
        }
        return category;
    }

    public void parseNote(String title, String body, List<String> categories){ // needs title, body, categories

        boolean categoryFound = false;

        String lowercaseTitle = title.toLowerCase();
        String lowerCaseBody = body.toLowerCase();
        String category = "";

        for (String cat : categories) { // Higher up keywords take precedence
            String lowercaseCategory = cat.toLowerCase();
            categoryFound = lowercaseTitle.contains(lowercaseCategory);
            if(categoryFound) {
                category = cat;
                break;
            }
        }

        if(category.length() > 0) {
            category = categories.get(categories.indexOf(category));

            Log.e("Found Keyword: ", category);
            return;
        }

        for (String cat : categories) { // Higher up keywords take precedence
            String lowercaseCategory = cat.toLowerCase();
            categoryFound = lowerCaseBody.contains(lowercaseCategory);
            if(categoryFound) {
                category = cat;
                break;
            }
        }

        if(categoryFound){
            // print keyword
            category = categories.get(categories.indexOf(category));

            Log.e("Found Keyword: ", category);
        }

    }

}
