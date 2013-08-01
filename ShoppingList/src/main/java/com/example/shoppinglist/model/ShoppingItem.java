package com.example.shoppinglist.model;

import com.j256.ormlite.field.DatabaseField;
import com.parse.ParseObject;

/**
 * Created by eandreevici on 31/07/13.
 */
public class ShoppingItem {

    public static final String TITLE = "title";

    @DatabaseField(id = true)
    private String objectId;

    @DatabaseField(unique = true)
    private String title;

    ShoppingItem() {
    }

    public ShoppingItem(String title) {
        this.title = title;
    }

    public static ShoppingItem fromParseObject(ParseObject object) {
        ShoppingItem item = new ShoppingItem();
        item.objectId = object.getObjectId();
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

    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (o == null || !(o instanceof ShoppingItem)) {
            return false;
        }
        ShoppingItem that = (ShoppingItem) o;
        if (this.objectId == null || that.objectId == null) {
            return this.title.equals(that.title);
        }
        return this.title.equals(that.title) && this.objectId.equals(that.objectId);
    }

    @Override
    public int hashCode() {
        int result = 17;
        result = 37 * result + title.hashCode();
        if (objectId != null) {
            result = 37 * result + objectId.hashCode();
        }
        return result;
    }
}
