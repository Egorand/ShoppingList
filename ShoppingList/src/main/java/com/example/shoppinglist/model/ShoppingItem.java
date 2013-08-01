package com.example.shoppinglist.model;

import com.j256.ormlite.field.DatabaseField;
import com.parse.ParseObject;

/**
 * Created by eandreevici on 31/07/13.
 */
public class ShoppingItem {

    public static final String TITLE = "title";

    @DatabaseField(id = true)
    private String title;

    ShoppingItem() {
    }

    public ShoppingItem(String title) {
        this.title = title;
    }

    public static ShoppingItem fromParseObject(ParseObject object) {
        ShoppingItem item = new ShoppingItem();
        item.title = object.getString(TITLE);
        return item;
    }

    public ParseObject toParseObject() {
        ParseObject object = new ParseObject(getClass().getSimpleName());
        object.put(TITLE, title);
        return object;
    }

    @Override
    public String toString() {
        return title;
    }
}
