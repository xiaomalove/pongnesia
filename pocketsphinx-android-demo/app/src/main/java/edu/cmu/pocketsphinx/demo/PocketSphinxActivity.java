/* ====================================================================
 * Copyright (c) 2014 Alpha Cephei Inc.  All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in
 *    the documentation and/or other materials provided with the
 *    distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY ALPHA CEPHEI INC. ``AS IS'' AND
 * ANY EXPRESSED OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO,
 * THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
 * PURPOSE ARE DISCLAIMED.  IN NO EVENT SHALL CARNEGIE MELLON UNIVERSITY
 * NOR ITS EMPLOYEES BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 * DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
 * THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 * ====================================================================
 */

package edu.cmu.pocketsphinx.demo;

import static android.widget.Toast.makeText;
import static edu.cmu.pocketsphinx.SpeechRecognizerSetup.defaultSetup;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Locale;
import java.util.Set;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import edu.cmu.pocketsphinx.Assets;
import edu.cmu.pocketsphinx.Hypothesis;
import edu.cmu.pocketsphinx.RecognitionListener;
import edu.cmu.pocketsphinx.SpeechRecognizer;

public class PocketSphinxActivity implements
        RecognitionListener {
		
    /* Named searches allow to quickly reconfigure the decoder */
    private static final String KWS_SEARCH = "wakeup";
    private static final String DIGITS_SEARCH = "digits";
    private static final String MENU_SEARCH = "menu";
    private String keyWordResult="";
    private boolean update = false;
    private final Object lock = new Object();
    Set<String> valSet = new HashSet<>();
    private Pingpong game;
    private TextToSpeech t1;
    private Context context;

    /* Keyword we are looking for to activate menu */
    private static final String KEYPHRASE = "start game";

    private SpeechRecognizer recognizer;
    private HashMap<String, Integer> captions;

    public PocketSphinxActivity(Context context){
        this.context = context;
    }


    public void onCreate(Bundle state) {
//        super.onCreate(state);

        game = new Pingpong("pointwhite", "pointblack", t1);
        // Prepare the data for UI
        captions = new HashMap<String, Integer>();
        captions.put(KWS_SEARCH, R.string.kws_caption);
        captions.put(MENU_SEARCH, R.string.menu_caption);
        captions.put(DIGITS_SEARCH, R.string.digits_caption);
//        setContentView(R.layout.main);
//        ((TextView) findViewById(R.id.caption_text))
//                .setText("Preparing the recognizer");

//        t1 = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
//            @Override
//            public void onInit(int status) {
//                if (status != TextToSpeech.ERROR) {
//                    t1.setLanguage(Locale.US);
//                }
//            }
//        });
//        t1.speak("some text", TextToSpeech.QUEUE_FLUSH, null);


        // Recognizer initialization is a time-consuming and it involves IO,
        // so we execute it in async task

        new AsyncTask<Void, Void, Exception>() {
            @Override
            protected Exception doInBackground(Void... params) {
                try {
                    Assets assets = new Assets(context);
                    File assetDir = assets.syncAssets();
                    setupRecognizer(assetDir);
                } catch (IOException e) {
                    return e;
                }
                return null;
            }

            @Override
            protected void onPostExecute(Exception result) {
                if (result != null) {
//                    ((TextView) findViewById(R.id.caption_text))
//                            .setText("Failed to init recognizer " + result);
                } else {
                    switchSearch(KWS_SEARCH);
                }
            }
        }.execute();

    }

//    @Override
    public void onDestroy() {
//        super.onDestroy();
        recognizer.cancel();
        recognizer.shutdown();
    }

    /**
     * In partial result we get quick updates about current hypothesis. In
     * keyword spotting mode we can react here, in other modes we need to wait
     * for final result in onResult.
     */
    @Override
    public void onPartialResult(Hypothesis hypothesis) {
        if (hypothesis == null)
    	    return;
        String text = hypothesis.getHypstr();
        if (text.equals(KEYPHRASE)) {
            switchSearch(DIGITS_SEARCH);
            System.out.println("Game Start!!!");
//            t1.speak("Game Start!!!", TextToSpeech.QUEUE_FLUSH, null);
            game.serve();
        }
        else
        {
            if (!game.getDone()) {
                getKeyword(text);
                System.out.println(text);
                if (keyWordResult != null && !keyWordResult.equals("")) {
//               ((TextView) findViewById(R.id.result_text)).setText(keyWordResult);
                    if (update) {
                        game.gameUpdate(keyWordResult);
                        update = false;
                    }
                }
            }
            else
            {
                game.winingMessage();
                recognizer.shutdown();
                // exit app TODO
            }
        }

    }

    /**
     * This callback is called when we stop the recognizer.
     */
    @Override
    public void onResult(Hypothesis hypothesis) {
    }

    @Override
    public void onBeginningOfSpeech() {
    }

    /**
     * We stop recognizer here to get a final result
     */
    @Override
    public void onEndOfSpeech() {
    }

    private void switchSearch(String searchName) {
        recognizer.stop();
        
        // If we are not spotting, start listening with timeout (10000 ms or 10 seconds).
        if (searchName.equals(KWS_SEARCH))
            recognizer.startListening(searchName);
            //recognizer.startListening("what");
        else
            //recognizer.startListening("what",10000);
            recognizer.startListening(searchName, 100000);
    }

    private void setupRecognizer(File assetsDir) throws IOException {
        // The recognizer can be configured to perform multiple searches
        // of different kind and switch between them
        
        recognizer = defaultSetup()
                .setAcousticModel(new File(assetsDir, "en-us-ptm"))
                .setDictionary(new File(assetsDir, "cmudict-en-us.dict"))
                
                // To disable logging of raw audio comment out this call (takes a lot of space on the device)
                .setRawLogDir(assetsDir)

                // Threshold to tune for keyphrase to balance between false alarms and misses
                .setKeywordThreshold(1e-45f)
                
                // Use context-independent phonetic search, context-dependent is too slow for mobile
                .setBoolean("-allphone_ci", true)
                
                .getRecognizer();

        recognizer.addListener(this);

        /** In your application you might not need to add all those searches.
         * They are added here for demonstration. You can leave just one.
         */

        // Create keyword-activation search.
        recognizer.addKeyphraseSearch(KWS_SEARCH, KEYPHRASE);
        //recognizer.addKeyphraseSearch("test","what");
        // Create grammar-based search for selection between demos


        File menuGrammar = new File(assetsDir, "menu.gram");
        recognizer.addGrammarSearch(MENU_SEARCH, menuGrammar);

        // Create grammar-based search for digit recognition
        File digitsGrammar = new File(assetsDir, "digits.gram");
        //recognizer.addKeywordSearch(DIGITS_SEARCH, digitsGrammar);
        recognizer.addGrammarSearch(DIGITS_SEARCH, digitsGrammar);
        initValSet();
    }

    @Override
    public void onError(Exception error) {
//        ((TextView) findViewById(R.id.caption_text)).setText(error.getMessage());
    }

    private void initValSet(){
        valSet.add("rematch");
        valSet.add("score");
        valSet.add("serve");
        valSet.add("wrong");
        valSet.add("pointblack");
        valSet.add("pointwhite");
    }

    private int keywordPreposition = -1;
    private synchronized void getKeyword(String s){
        keyWordResult = null;
        String tmp = null;
        String[] a = s.split(" ");
        for (int i = 0;i<a.length;i++){
            if (valSet.contains(a[i]) && keywordPreposition < i){
                keyWordResult = a[i];
                keywordPreposition = i;
                update = true;
//                System.out.println(keyWordResult);
            }
        }
    }

    public String returnKeyWord(){
        return keyWordResult;
    }

    public void addName() throws IOException {
//        Button button = (Button) findViewById(R.id.button);
//        button.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                recognizer.shutdown();
//                SaveName sn = new SaveName();
//                try {
//                    File file = assets.syncAssets();
//                    File digitsGrammar = new File(file, "digits.gram");
//                    //recognizer.addKeywordSearch(DIGITS_SEARCH, digitsGrammar);
////                    recognizer.addGrammarSearch(DIGITS_SEARCH, digitsGrammar);
//                    sn.writeName("mary",digitsGrammar);
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//                Bundle state = null;
//                onCreate(state);
//            }
//        });
    }



    @Override
    public void onTimeout() {
        switchSearch(KWS_SEARCH);
    }
}
