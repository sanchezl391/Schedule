/**
 * Author: Luis Sanchez
 */

package edu.utep.cs.cs4330.schedule;

import android.util.Log;

import java.util.List;

public class Parser {

    /**
     * Checks if a string matches the categories
     * @param text String that may contain a category
     * @param categories list of categories
     * @return the category found in the text if there was any
     */
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
}
