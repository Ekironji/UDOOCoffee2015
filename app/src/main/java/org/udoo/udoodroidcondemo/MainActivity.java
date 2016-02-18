/*
 * Created by Ekironji feat. Lisapp & Ugappz
 * UDOO Team
 */

package org.udoo.udoodroidcondemo;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.hardware.usb.UsbManager;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.widget.Toast;

import org.udoo.udoodroidcondemo.org.udoo.udooodroidconsemo.fragments.MainFragment;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Locale;

import me.palazzetti.adktoolkit.AdkManager;

public class MainActivity extends Activity {

	private final String TAG = "UDOOMario";
	private final String PREFS_NAME = "MarioPrefs";

	// ADK
	private AdkManager mAdkManager;

	// command to arduino
	private final String ESPRESSO_SENDSTRING = "0";
	private final String LONG_SENDSTRING 	= "1";
	private final String CAPPUCCINO_SENDSTRING 	= "2";
	private final String CHOCO_SENDSTRING 	= "3";

	// speech variables
	SpeechRecognizer mSpeechRecognizer;

	// speech key
	private ArrayList<String> yes_strings = new ArrayList<String>(Arrays.asList("yes", "ok", "great", "nice", "yeah"));
	private ArrayList<String> no_strings = new ArrayList<String>(Arrays.asList("no", "don't"));
	private ArrayList<String> espresso_strings = new ArrayList<String>(Arrays.asList("espresso", "expresso", "short"));
	private ArrayList<String> long_strings = new ArrayList<String>(Arrays.asList("american", "long", "longer", "normal"));
	private ArrayList<String> cappuccino_strings = new ArrayList<String>(Arrays.asList("cappuccino", "cappuccio", "latte"));
	private ArrayList<String> chocolate_strings = new ArrayList<String>(Arrays.asList("chocolate", "choco"));

	private ArrayList<String> allwords = new ArrayList<String>();

	private boolean running = false;
	private String mLastFetchedId = "1";

	TextToSpeech tts;

    MainFragment mMainFragment = null;


	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		mAdkManager = new AdkManager((UsbManager) getSystemService(Context.USB_SERVICE));
		registerReceiver(mAdkManager.getUsbReceiver(), mAdkManager.getDetachedFilter());

        mMainFragment = new MainFragment();

	    allwords.addAll(yes_strings);
	    allwords.addAll(no_strings);
	    allwords.addAll(espresso_strings);
	    allwords.addAll(long_strings);
	    allwords.addAll(cappuccino_strings);
	    allwords.addAll(chocolate_strings);

//      faceImage.setImageResource(R.drawable.normal);

        mSpeechRecognizer = SpeechRecognizer.createSpeechRecognizer(getApplicationContext());
        MyRecognitionListener listener = new MyRecognitionListener();
        mSpeechRecognizer.setRecognitionListener(listener);

        // Get last stored values
        /*SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
        mLastFetchedId = settings.getString("lastId", "1");*/

        tts = new TextToSpeech(getApplicationContext(),
        	      new TextToSpeech.OnInitListener() {
	            @Override
	            public void onInit(int status) {
	            	if (status == TextToSpeech.SUCCESS) {

	                    int result = tts.setLanguage(Locale.UK);
//	                    tts.setPitch(0.7F);

	                    if (result == TextToSpeech.LANG_MISSING_DATA) {
	                        Log.e("TTS", "Lang missing data");
	                            // missing data, install it
	                            Intent installIntent = new Intent();
	                            installIntent.setAction(TextToSpeech.Engine.ACTION_INSTALL_TTS_DATA);
	                            startActivity(installIntent);
	                    } else {
	                    	tts.speak("hello", TextToSpeech.QUEUE_FLUSH, null);
	                    	Log.i(TAG, "hello");
	                    }

	                } else {
	                    Log.e("TTS", "Initilization Failed!");
	                }
	            }
            });

        if (savedInstanceState == null) {
            getFragmentManager().beginTransaction()
                    .add(R.id.container, mMainFragment)
                    .commit();
        }
	}

	@Override
	protected void onPause() {
	    super.onPause();
	    if(mSpeechRecognizer!=null){
	    	mSpeechRecognizer.stopListening();
	    	mSpeechRecognizer.cancel();
	    	mSpeechRecognizer.destroy();

	    }
	    if(tts !=null){
	         tts.stop();
	         tts.shutdown();
	    }
	    mSpeechRecognizer = null;
	    tts = null;
	    mAdkManager.close();
	}

    @Override
    protected void onStop() {
        super.onStop();
        SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putString("lastId", mLastFetchedId);
        editor.commit();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mAdkManager.getUsbReceiver());
    }

	@Override
	protected void onResume() {
	    super.onResume();
	    if(mSpeechRecognizer == null){
        	mSpeechRecognizer = SpeechRecognizer.createSpeechRecognizer(getApplicationContext());
            MyRecognitionListener listener = new MyRecognitionListener();
            mSpeechRecognizer.setRecognitionListener(listener);
        }
	    mAdkManager.open();
	}

    private boolean searchCommands (String result) {
        boolean found = false;
        String stringFounded = "";
        for (String string : allwords ) {
        	if (result.toLowerCase(Locale.US).contains(string.toLowerCase(Locale.US))) {
        		stringFounded = string;
        		found = true;
        		
        		/*if (hi_strings.contains(stringFounded)) tts.speak("Hi", TextToSpeech.QUEUE_FLUSH, null);
        		else*/ if (yes_strings.contains(stringFounded)) coffeeCase();
        		else if (no_strings.contains(stringFounded)) notCoffeeCase();
        		else if (espresso_strings.contains(stringFounded)) espressoCoffeeCase();
        		else if (long_strings.contains(stringFounded)) longCoffeeCase();
        		else if (cappuccino_strings.contains(stringFounded)) cappuccinoCoffeeCase();
        		else if (chocolate_strings.contains(stringFounded)) chocolateCoffeeCase();

        		break;
        	}
        }
        return found;
    }

    private void coffeeCase(){
    	tts.speak("ok a coffee,  do you prefer an espresso or american coffee?", TextToSpeech.QUEUE_FLUSH, null);
    	Log.i(TAG, "coffecase");
    	new CountDownTimer(4000, 1000) {

            public void onTick(long millisUntilFinished) {
                //do nothing, just let it tick
            }

            public void onFinish() {
            	startVoiceRecognitionActivity();
            }
         }.start();
    }

    private void espressoCoffeeCase() {
    	Log.i(TAG, "espressocoffecase");
    	mAdkManager.write(ESPRESSO_SENDSTRING);
    	tts.speak("ok, an espresso", TextToSpeech.QUEUE_FLUSH, null);
    	new CountDownTimer(5000, 1000) {

            public void onTick(long millisUntilFinished) {
                //do nothing, just let it tick
            }

            public void onFinish() {
            	tts.speak("Ok sir, enjoy your espresso.", TextToSpeech.QUEUE_FLUSH, null);
            	new CountDownTimer(5000, 1000) {

                    public void onTick(long millisUntilFinished) {
                        //do nothing, just let it tick
                    }

                    public void onFinish() {
                    	returnToNormalState(1000);
                    }
                 }.start();
            }
         }.start();

    }

    private void longCoffeeCase() {
    	Log.i(TAG, "longcoffecase");
    	mAdkManager.write(LONG_SENDSTRING);
    	tts.speak("ok, a long coffee", TextToSpeech.QUEUE_FLUSH, null);
    	new CountDownTimer(5000, 1000) {

            public void onTick(long millisUntilFinished) {
                //do nothing, just let it tick
            }

            public void onFinish() {
            	tts.speak("Ok sir, enjoy your long coffee.", TextToSpeech.QUEUE_FLUSH, null);
            	new CountDownTimer(5000, 1000) {

                    public void onTick(long millisUntilFinished) {
                        //do nothing, just let it tick
                    }

                    public void onFinish() {
                    	returnToNormalState(1000);
                    }
                 }.start();
            }
         }.start();

    }

    private void cappuccinoCoffeeCase() {
    	Log.i(TAG, "cappuccinocase");
    	mAdkManager.write(CAPPUCCINO_SENDSTRING);
    	tts.speak("ok, a cappuccino", TextToSpeech.QUEUE_FLUSH, null);
    	new CountDownTimer(5000, 1000) {

            public void onTick(long millisUntilFinished) {
                //do nothing, just let it tick
            }

            public void onFinish() {
            	tts.speak("Ok sir, enjoy your cappuccino", TextToSpeech.QUEUE_FLUSH, null);
            	new CountDownTimer(5000, 1000) {

                    public void onTick(long millisUntilFinished) {
                        //do nothing, just let it tick
                    }

                    public void onFinish() {
                    	returnToNormalState(1000);
                    }
                 }.start();
            }
         }.start();

    }

    private void chocolateCoffeeCase() {
    	Log.i(TAG, "chocolatecase");
    	mAdkManager.write(CHOCO_SENDSTRING);
    	tts.speak("ok, a chocolate", TextToSpeech.QUEUE_FLUSH, null);
    	new CountDownTimer(5000, 1000) {

            public void onTick(long millisUntilFinished) {
                //do nothing, just let it tick
            }

            public void onFinish() {
            	tts.speak("Ok sir, enjoy your chocolate", TextToSpeech.QUEUE_FLUSH, null);
            	new CountDownTimer(5000, 1000) {

                    public void onTick(long millisUntilFinished) {
                        //do nothing, just let it tick
                    }

                    public void onFinish() {
                    	returnToNormalState(1000);
                    }
                 }.start();
            }
         }.start();

    }

    private void notCoffeeCase() {
    	Log.i(TAG, "notcoffeecase");
    	tts.speak("so what do you want, sir?", TextToSpeech.QUEUE_FLUSH, null);
    	new CountDownTimer(3000, 1000) {

            public void onTick(long millisUntilFinished) {
                //do nothing, just let it tick
            }

            public void onFinish() {
            	 	startVoiceRecognitionActivity();
            }
         }.start();
    }
    
    private void returnToNormalState(int millis) {
    	new CountDownTimer(millis, 1000) {

            public void onTick(long millisUntilFinished) {
                //do nothing, just let it tick
            }

            public void onFinish() {
            	Log.i(TAG, "returnToNormalState");
                mMainFragment.setVoiceButtonBackgroundResource(R.drawable.caffe_prima);
            }
         }.start();
    }
      
    @SuppressWarnings("unused")
	private void showToastMessage(String message){
    	  Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
    
	/**
     * Fire an intent to start the voice recognition activity.
     */
    private void startVoiceRecognitionActivity() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, this.getPackageName());
    	
        mSpeechRecognizer.startListening(intent);        
        new CountDownTimer(5000, 1000) {

            public void onTick(long millisUntilFinished) {
                //do nothing, just let it tick
            }

            public void onFinish() {
            	Log.i(TAG, "stop countdown");
            	mSpeechRecognizer.stopListening();
            	mMainFragment.setVoiceButtonBackgroundResource(R.drawable.caffe_prima);
            }
         }.start();
    }
	
	class MyRecognitionListener implements RecognitionListener {

        @Override
        public void onBeginningOfSpeech() {
                Log.d("Speech", "onBeginningOfSpeech");
        }

		@Override
        public void onBufferReceived(byte[] buffer) {
                Log.d("Speech", "onBufferReceived");
        }

        @Override
        public void onEndOfSpeech() {
                Log.d("Speech", "onEndOfSpeech");
        }

        @Override
        public void onError(int error) {
                Log.d("Speech", "onError: " + error);
//                if (error == 7 ){
//                	tts.speak("Sorry, but I didn't understand", TextToSpeech.QUEUE_FLUSH, null);
//                }
        }

        @Override
        public void onEvent(int eventType, Bundle params) {
                Log.d("Speech", "onEvent");
        }

        @Override
        public void onPartialResults(Bundle partialResults) {
                Log.d("Speech", "onPartialResults");
        }

        @Override
        public void onReadyForSpeech(Bundle params) {
                Log.d("Speech", "onReadyForSpeech");
                mMainFragment.setVoiceButtonBackgroundResource(R.drawable.caffe_seconda);
        }
        

        @Override
        public void onResults(Bundle results) {
                Log.d("Speech", "onResults");
                ArrayList<String> resultsArray = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
                mMainFragment.setDebugText("Received text: " + resultsArray.get(0));
		        
		        if (!searchCommands(resultsArray.get(0))) {
		        	tts.speak("Sorry, but I didn't understand, could you repeat?", TextToSpeech.QUEUE_FLUSH, null);
		        	new CountDownTimer(4000, 1000) {

		                public void onTick(long millisUntilFinished) {
		                    //do nothing, just let it tick
		                }

		                public void onFinish() {
		                	Log.i(TAG, "stop countdown");
		                	startVoiceRecognitionActivity();
		                }
		             }.start();
		        	//showToastMessage("Sentence is not recognized");
		        }
		        
                for (int i = 0; i < resultsArray.size();i++ ) {
                        Log.d("Speech", "result=" + resultsArray.get(i));           
                }
        }

        @Override
        public void onRmsChanged(float rmsdB) {
//               Log.d("Speech", "onRmsChanged");
        }

	}

    public void startRecognition(){
        startVoiceRecognitionActivity();
    }


    public void speak(String text){
        tts.speak(text, TextToSpeech.QUEUE_FLUSH, null);
    }
}
