package edu.cmu.pocketsphinx.demo;

import android.app.Activity;
import android.os.Bundle;

/**
 * Created by xitaowang on 11/5/15.
 */
public class Run extends Activity{
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        PocketSphinxActivity psa = new PocketSphinxActivity(Run.this);
        psa.onCreate();
    }
}
