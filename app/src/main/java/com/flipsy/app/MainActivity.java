package com.flipsy.app;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.Menu;
import android.view.MenuItem;


public class MainActivity extends SingleFragmentActivity {

    @Override
    public Fragment createFragment() {
        return new GalleryFragment();
    }
}

