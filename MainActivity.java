package xbee.udootest;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.hardware.usb.UsbManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import org.w3c.dom.Text;

import me.palazzetti.adktoolkit.AdkManager;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;

import weka.classifiers.Classifier;
import weka.classifiers.lazy.IBk;
import weka.core.Attribute;
import weka.core.FastVector;
import weka.core.Instance;
import weka.core.Instances;
import wlsvm.WLSVM;



public class MainActivity extends Activity{

//	private static final String TAG = "UDOO_AndroidADKFULL";	 

    private AdkManager mAdkManager;

    private ToggleButton buttonLED;
    private TextView distance;
    private TextView pulse;
    private TextView position;

    private TextView aoneone;
    private TextView aonetwo;
    private TextView aonethree;

    private TextView atwoone;
    private TextView atwotwo;
    private TextView atwothree;

    private TextView athreeone;
    private TextView athreetwo;
    private TextView athreethree;

    private AdkReadTask mAdkReadTask;

    Button startbutton;

    private int countstate;
    private boolean isthereclassifier;
    private String heartinfo;
    private Instances heartTester;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAdkManager = new AdkManager((UsbManager) getSystemService(Context.USB_SERVICE));

//		register a BroadcastReceiver to catch UsbManager.ACTION_USB_ACCESSORY_DETACHED action
        registerReceiver(mAdkManager.getUsbReceiver(), mAdkManager.getDetachedFilter());

        buttonLED = (ToggleButton) findViewById(R.id.toggleButtonLED);
        distance  = (TextView) findViewById(R.id.textView_distance);
        pulse  = (TextView) findViewById(R.id.textView_pulse);
        position  = (TextView) findViewById(R.id.textView_position);

        File root = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
        File f = new File(root, "iris_train.arff");

        BufferedReader inputReader;

        inputReader = readFile(f);
        // you need to code the readFile() that takes in a File object and returns a BufferedReader

        Classifier ibk = new IBk();

        try {
            Instances data = new Instances(inputReader);
            data.setClassIndex(data.numAttributes() - 1);

            ibk.buildClassifier(data);
        }

        catch(Exception e){

        }

        Instances test = null;

        try {
            f = new File(root, "iris_test.arff");
            inputReader = readFile(f);
            test = new Instances(inputReader);

        }

        catch (Exception e) {

        }

        finally {
            if (test != null) {
                test.setClassIndex(test.numAttributes() - 1);
            }
        }

        int correct = 0;
        int wrong = 0;

        int oneone = 0;
        int onetwo = 0;
        int onethree = 0;
        int twoone = 0;
        int twotwo = 0;
        int twothree = 0;
        int threeone = 0;
        int threetwo = 0;
        int threethree = 0;

        try {
            for (int i = 0; i < test.numInstances(); i++) {
                double pred = ibk.classifyInstance(test.instance(i));
                double act = test.instance(i).classValue();

                if (pred == act) {
                    correct +=1;
                    if (pred == 1.0) {
                        oneone +=1;
                    }

                    else if (pred == 2.0) {
                        twotwo +=1;
                    }

                    else {
                        threethree +=1;
                    }
                }

                else {
                    if (pred == 1.0 && act == 2.0) {
                        onetwo +=1;
                    }

                    else if (pred == 1.0 && act == 3.0) {
                        onethree +=1;
                    }

                    else if (pred == 2.0 && act == 1.0) {
                        twoone +=1;
                    }

                    else if (pred == 2.0 && act == 3.0) {
                        twothree +=1;
                    }

                    else if (pred == 3.0 && act == 1.0) {
                        threeone +=1;
                    }

                    else if (pred == 3.0 && act == 2.0) {
                        threetwo +=1;
                    }

                    wrong +=1;
                }

                // TODO: compare the prediction results with the actual class label

            }


        }

        catch (Exception ex) {

        }

        // TODO: report the number of correct and incorrect

        aoneone = (TextView)findViewById(R.id.aa);
        aonetwo = (TextView)findViewById(R.id.ab);
        aonethree = (TextView)findViewById(R.id.ac);

        atwoone = (TextView)findViewById(R.id.ba);
        atwotwo = (TextView)findViewById(R.id.bb);
        atwothree = (TextView)findViewById(R.id.bc);

        athreeone = (TextView)findViewById(R.id.ca);
        athreetwo = (TextView)findViewById(R.id.cb);
        athreethree = (TextView)findViewById(R.id.cc);

        aoneone.setText(Integer.toString(oneone));
        aonetwo.setText(Integer.toString(onetwo));
        aonethree.setText(Integer.toString(onethree));

        atwoone.setText(Integer.toString(twoone));
        atwotwo.setText(Integer.toString(twotwo));
        atwothree.setText(Integer.toString(twothree));

        athreeone.setText(Integer.toString(threeone));
        athreetwo.setText(Integer.toString(threetwo));
        athreethree.setText(Integer.toString(threethree));


        startbutton = (Button)findViewById(R.id.statebutton);

        startbutton.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        countstate +=1;
                    }
                }
        );

        if (countstate == 0) {
            ((TextView)findViewById (R.id.statebutton)).setText ("Start Resting");
        }

        else if (countstate == 1) {
            ((TextView)findViewById (R.id.statebutton)).setText ("Stop Resting");
        }

        else if (countstate == 2) {
            ((TextView)findViewById (R.id.statebutton)).setText ("Start Stressed");
        }

        else if (countstate == 3) {
            ((TextView)findViewById (R.id.statebutton)).setText ("Stop Stressed");
        }



        BufferedReader heartReader;
        BufferedReader heartResult;

        File heartclass = new File(root, "iris_train.arff");
        heartReader = readFile(heartclass);
        Classifier heartClassifier = new IBk();

        double heartPred = 0;

        try {
            Instances heartData = new Instances(heartReader);
            heartData.setClassIndex(heartData.numAttributes() - 1);
            heartClassifier.buildClassifier(heartData);
        }

        catch(Exception e){

        }

        try {
            heartPred = heartClassifier.classifyInstance(heartTester.firstInstance());

            if (heartPred == 1.0) {
                ((TextView)findViewById (R.id.heartbeat)).setText ("Resting");
            }

            else if (heartPred == 2.0) {
                ((TextView)findViewById (R.id.heartbeat)).setText ("Stressed");
            }
        }

        catch (Exception ex) {

        }

    }

    public static BufferedReader readFile(File f) {
        FileReader fr = null;

        try {
            fr = new FileReader(f);

        }

        catch (Exception ex) {

        }

        finally {

            if (fr != null) {
                return new BufferedReader(fr);
            }

            else return null;
        }
    }

    public static BufferedReader stringBuffer(String don) {
        Reader inputString = new StringReader(don);
        return new BufferedReader(inputString);
    }

    @Override
    public void onResume() {
        super.onResume();
        mAdkManager.open();

        mAdkReadTask = new AdkReadTask();
        mAdkReadTask.execute();
    }

    @Override
    public void onPause() {
        super.onPause();
        mAdkManager.close();

        mAdkReadTask.pause();
        mAdkReadTask = null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mAdkManager.getUsbReceiver());
    }

    // ToggleButton method - send message to SAM3X
    public void blinkLED(View v){
        if (buttonLED.isChecked()) {
            // writeSerial() allows you to write a single char or a String object.
            mAdkManager.writeSerial("1");
        } else {
            mAdkManager.writeSerial("0");
        }
    }

    /*
     * We put the readSerial() method in an AsyncTask to run the
     * continuous read task out of the UI main thread
     */
    private class AdkReadTask extends AsyncTask<Void, String, Void> {

        private boolean running = true;

        public void pause(){
            running = false;
        }



        protected Void doInBackground(Void... params) {
//	    	Log.i("ADK demo bi", "start adkreadtask");
            while(running) {
                publishProgress(mAdkManager.readSerial()) ;
            }
            return null;
        }

        filewriter writehere = new filewriter();
        BufferedWriter writerfile = null;
        FastVector fvWekaAttributes = new FastVector(4);
        private int countbuffer;
        private int bufferpulse,oxygenpulse,pospulse;

        protected void onProgressUpdate(String... progress) {

            float pulseRate= (int)progress[0].charAt(0);
            float oxygenLvl= (int)progress[0].charAt(1);
            float pos= (int)progress[0].charAt(2);
            int max = 255;
            if (pulseRate>max) pulseRate=max;
            if (oxygenLvl>max) oxygenLvl=max;
            if (pos>max) pos=max;

//            DecimalFormat df = new DecimalFormat("#.#");
            distance.setText(pulseRate + " (bpm)");
            pulse.setText(oxygenLvl + " (pct)");
            position.setText(pos + "");

            if (countstate == 1) {

                if (writerfile == null) {
                    writerfile = writehere.initialize();
                }

                writehere.whenAppendStringUsingBufferedWritter_thenOldContentShouldExistToo(writerfile,pulseRate,oxygenLvl,pos,"Relaxed");
            }

            else if (countstate == 3) {
                writehere.whenAppendStringUsingBufferedWritter_thenOldContentShouldExistToo(writerfile,pulseRate,oxygenLvl,pos,"Stressed");
            }

            else if (countstate == 4) {
                try {
                    writerfile.close();
                    writerfile = null;
                    isthereclassifier = true;
                }

                catch (Exception ex){

                }

                finally {
                    countstate = 0;
                }
            }

            if (isthereclassifier) {

                if (countbuffer<11) {
                    bufferpulse += pulseRate;
                    oxygenpulse+= oxygenLvl;
                    pospulse += pos;
                    countbuffer +=1;
                }

                else {
                    Instances testingSet = new Instances("TestingInstance", fvWekaAttributes, 1);

// Setting the column containing class labels:
                    testingSet.setClassIndex(testingSet.numAttributes() - 1);

// Create and fill an instance, and add it to the testingSet
                    Instance iExample = new Instance(testingSet.numAttributes());

                    iExample.setValue((Attribute)fvWekaAttributes.elementAt(0), bufferpulse/countbuffer);
                    iExample.setValue((Attribute)fvWekaAttributes.elementAt(1), oxygenpulse/countbuffer);
                    iExample.setValue((Attribute)fvWekaAttributes.elementAt(2), pospulse/countbuffer);
                    iExample.setValue((Attribute)fvWekaAttributes.elementAt(3),
                            "Relaxed"); // dummy

// add the instance
                    testingSet.add(iExample);
                    heartTester = testingSet;
                    countbuffer= 0;
                    oxygenpulse = 0;
                    pospulse = 0;
                    countbuffer = 0;
                }

            }

        }
    }


}
