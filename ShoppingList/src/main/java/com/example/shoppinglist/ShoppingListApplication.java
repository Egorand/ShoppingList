package com.example.shoppinglist;

import android.app.Application;

import com.parse.Parse;
import com.parse.ParseACL;

/**
 * Created by eandreevici on 31/07/13.
 */
public class ShoppingListApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        Parse.initialize(this, "vbe3ryexv2crGguiExNrdiwb3Uqd8Pv7ViouoEk6", "UdtYx3BzBC2wzpTWCNph7170QIR3Y057eFi7O6Ti");
        ParseACL defaultACL = new ParseACL();
        defaultACL.setPublicReadAccess(true);
        ParseACL.setDefaultACL(defaultACL, true);
    }
}
