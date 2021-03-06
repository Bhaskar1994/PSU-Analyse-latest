package com.example.abhishekbaghel.aero_analyse;



/*
import android.Manifest;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.UUID;

public class MainActivity extends AppCompatActivity {

    //Declaring the variables
    Button btnRecord,btnStopRecord,btnStop,btnPlay;
    String pathSave ="";
    MediaRecorder mediaRecorder;
    MediaPlayer mediaPlayer;

    final int REQUSET_PERMISSION_CODE =1000;
    // Used to load the 'native-lib' library on application startup.
    static {
        System.loadLibrary("native-lib");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnPlay =(Button)findViewById(R.id.btnPlay);
        btnRecord =(Button)findViewById(R.id.btnStartRecord);
        btnStop =(Button)findViewById(R.id.btnStop);
        btnStopRecord =(Button)findViewById(R.id.btnStopRecord);

        if (checkPermissionFromDevice()){
            btnRecord.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    pathSave = Environment.getExternalStorageDirectory()
                            .getAbsolutePath() + "/" + UUID.randomUUID().toString() + "audio_record.wav";
                    setupMediaRecorder();
                    try {
                        mediaRecorder.prepare();
                        mediaRecorder.start();

                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    btnPlay.setEnabled(false);
                    btnStop.setEnabled(false);
                    Toast.makeText(MainActivity.this, "Recording..", Toast.LENGTH_SHORT).show();

                }
            });

            btnStopRecord.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mediaRecorder.stop();
                    btnStopRecord.setEnabled(false);
                    btnPlay.setEnabled(true);
                    btnRecord.setEnabled(true);
                    btnStop.setEnabled(false);
                }
            });

            btnPlay.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    btnStop.setEnabled(true);
                    btnStopRecord.setEnabled(false);
                    btnRecord.setEnabled(false);

                    mediaPlayer = new MediaPlayer();
                    try{
                        mediaPlayer.setDataSource(pathSave);
                        mediaPlayer.prepare();

                    }catch (IOException e){
                        e.printStackTrace();
                    }
                    mediaPlayer.start();
                    Toast.makeText(MainActivity.this, "Playing..",Toast.LENGTH_SHORT).show();

                }
            });

            btnStop.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    btnRecord.setEnabled(true);
                    btnStop.setEnabled(false);
                    btnStopRecord.setEnabled(false);
                    btnPlay.setEnabled(true);

                    if (mediaPlayer != null){
                        mediaPlayer.stop();
                        mediaPlayer.release();
                        setupMediaRecorder();

                    }

                }
            });
        }
        else {
            requsetPermission();
        }

        // Example of a call to a native method
       // TextView tv = (TextView) findViewById(R.id.sample_text);
        //tv.setText(stringFromJNI());
    }

    private void setupMediaRecorder() {
        mediaRecorder = new MediaRecorder();
        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.);
    }

    private void requsetPermission() {
        ActivityCompat.requestPermissions(this, new String[]{
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.RECORD_AUDIO
        },REQUSET_PERMISSION_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode){
            case REQUSET_PERMISSION_CODE:
            {
                if (grantResults.length > 0 && grantResults[0]==PackageManager.PERMISSION_GRANTED)
                    Toast.makeText(this,"Permission Granted",Toast.LENGTH_SHORT).show();
                else
                    Toast.makeText(this,"Permission Denied",Toast.LENGTH_SHORT).show();

            }
            break;

        }
    }

    private boolean checkPermissionFromDevice() {
        int write_external_storage_result = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        int record_audio_result = ContextCompat.checkSelfPermission(this,Manifest.permission.RECORD_AUDIO);
        return write_external_storage_result == PackageManager.PERMISSION_GRANTED &&
                record_audio_result == PackageManager.PERMISSION_GRANTED;

    }





    /**
     * A native method that is implemented by the 'native-lib' native library,
     * which is packaged with this application.
     */
   // public native String stringFromJNI();*/



        import java.io.BufferedOutputStream;
        import java.io.ByteArrayOutputStream;
        import java.io.File;
        import java.io.FileNotFoundException;
        import java.io.FileOutputStream;
        import java.io.IOException;
        import java.io.RandomAccessFile;
        import java.text.SimpleDateFormat;
        import java.util.Date;

        import android.Manifest;
        import android.app.Activity;
        import android.app.AlertDialog;
        import android.app.Notification;
        import android.app.NotificationManager;
        import android.app.PendingIntent;
        import android.content.Context;
        import android.content.DialogInterface;
        import android.content.Intent;
        import android.content.pm.PackageManager;
        import android.location.Location;
        import android.location.LocationListener;
        import android.location.LocationManager;
        import android.media.AudioFormat;
        import android.media.AudioRecord;
        import android.media.MediaRecorder;
        import android.net.Uri;
        import android.os.Build;
        import android.os.Bundle;
        import android.os.Environment;
        import android.os.Looper;
        import android.os.StatFs;
        import android.provider.Settings;
        import android.support.annotation.NonNull;
        import android.support.v4.app.ActivityCompat;
        import android.util.Log;
        import android.view.View;
        import android.view.View.OnClickListener;
        import android.widget.Button;
        import android.widget.EditText;
        import android.widget.ImageButton;
        import android.widget.ProgressBar;
        import android.widget.Spinner;
        import android.widget.TextView;

        import static android.content.ContentValues.TAG;


/**
 * An activity that allows the user to record full-quality audio at a variety of sample rates, and
 * save to a WAV file
 *
 *
 */
public class MainActivity extends Activity {

    private static final int WAV_HEADER_LENGTH = 44;
    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd_HH.mm.ss");
    private static final int NOTICE_RECORD = 0;

    private Button actionButton;
    private ImageButton newTimestamp;
    private EditText editText;
    private String filename;
    private String rawFileName;
    private ProgressBar saving;
    private Spinner spinner;
    private View startedRecording;
    private TextView startedRecordingTime;

    private AlertDialog dialog;

    private File outFile;
    private File rawFile;

    private boolean isListening;

    //gps variables
    private Button b;
    private Button w;
    private TextView t;
    private LocationManager locationManager;
    private LocationListener listener;
    private Looper looper;


    String longitude;
    String latitude;

    /**
     * The sample rate at which we'll record, and save, the WAV file.
     */
    public int sampleRate = 8000;
    private NotificationManager notificationManager;






    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // set up GUI references
        actionButton = (Button) findViewById(R.id.actionButton);
        newTimestamp = (ImageButton) findViewById(R.id.newTimestamp);
        editText = (EditText) findViewById(R.id.editText);
        saving = (ProgressBar) findViewById(R.id.saving);
        spinner = (Spinner) findViewById(R.id.spinner);
        startedRecording = findViewById(R.id.startedRecording);
        startedRecordingTime = (TextView)findViewById(R.id.startedRecordingTime);

        //GPS
        t = (TextView) findViewById(R.id.textView);
        b = (Button) findViewById(R.id.button);
        w = (Button) findViewById(R.id.weather_button);
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        //------------------


        // get a generic dialog ready for alerts
        dialog = new AlertDialog.Builder(this).create();
        dialog.setButton(AlertDialog.BUTTON_POSITIVE, "OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int which) {
                dialogInterface.dismiss();
            }
        });
        notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        // add GUI functionality
        saving.setVisibility(View.GONE);
        editText.setSingleLine(true);

        newTimestamp.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                String timedFilename = "Rec_";
                Date date = new Date();
                timedFilename += dateFormat.format(date);
                editText.setText(timedFilename);
            }
        });

        actionButton.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {

                // if we're already recording... start saving
                if (isListening) {
                    endRecording();
                } else {
                    beginRecording();
                }
            }

        });


        //GPS---------
        listener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                //getTime-Return the UTC time of this fix, in milliseconds since January 1, 1970.
                //getAccuracy- Get the estimated horizontal accuracy of this location, radial, in meters.
                t.append("\n " + location.getLongitude() + "\n" + location.getLatitude() + "\n" + location.getAccuracy() + "\n" + location.getTime());
                latitude = Double.toString(location.getLatitude());
                longitude =Double.toString(location.getLongitude());
            }

            @Override
            public void onStatusChanged(String s, int i, Bundle bundle) {

            }

            @Override
            public void onProviderEnabled(String s) {

            }

            @Override
            public void onProviderDisabled(String s) {

                Intent i = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(i);
            }
        };

        configure_button();
        //------------------------------
       //Weather button------------------------------------
        w.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {

                // Move to weather activity
                Intent intent = new Intent(MainActivity.this,WeatherActivity.class);
                intent.putExtra("lat", latitude);
                intent.putExtra("long",longitude);
                startActivity(intent);

            }

        });

    }

    /**
     * End the recording, saving and finalising the file
     */
    private void endRecording() {
        isListening = false;
        Thread thread = new Thread() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        startedRecording.setVisibility(View.GONE);
                        actionButton.setEnabled(false);
                        actionButton.setText("Saving...");
                        saving.setVisibility(View.VISIBLE);
                    }
                });

                if (outFile != null) {
                    appendHeader(outFile);

                    Intent scanWav = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
                    scanWav.setData(Uri.fromFile(outFile));
                    sendBroadcast(scanWav);

                    outFile = null;
                    notificationManager.cancel(NOTICE_RECORD);
                }

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        actionButton.setEnabled(true);
                        editText.setEnabled(true);
                        newTimestamp.setEnabled(true);
                        actionButton.setText("Start recording");
                        saving.setVisibility(View.GONE);
                    }
                });
            }
        };
        thread.start();
    }

    /**
     * Begin the recording after verifying that we can, if we can't then tell the user and return
     */
    private void beginRecording() {

        // check that there's somewhere to record to
        String state = Environment.getExternalStorageState();
        Log.d("FS State", state);
        if (state.equals(Environment.MEDIA_SHARED)) {
            showDialog("Unmount USB storage", "Please unmount USB storage before starting to record.");
            return;
        } else if (state.equals(Environment.MEDIA_REMOVED)) {
            showDialog("Insert SD Card", "Please insert an SD card. You need something to record onto.");
            return;
        }

        // check that the user's supplied a file name
        filename = editText.getText().toString();
        if (filename.equals("") || filename == null) {
            showDialog("Enter a file name", "Please give your file a name. It's the least it deserves.");
            return;
        }
        if (!filename.endsWith(".wav")) {
            filename += ".wav";
        }
        rawFileName = editText.getText().toString();
        if (rawFileName.equals("") || rawFileName == null) {
            showDialog("Enter a file name", "Please give your file a name. It's the least it deserves.");
            return;
        }
        if (!rawFileName.endsWith(".raw")) {
            rawFileName += ".raw";
        }


        // ask if file should be overwritten
        File userFile = new File(Environment.getExternalStorageDirectory() + "/" + filename);
        if (userFile.exists()) {
            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
            builder.setTitle("File already exists").setMessage(
                    "Do you want to overwrite the existing " + "file with that name?").setCancelable(false)
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int id) {
                            dialogInterface.dismiss();
                            startRecording();
                        }
                    }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int id) {
                    dialogInterface.cancel();
                }
            });
            AlertDialog alert = builder.create();
            alert.show();
        } else { // otherwise, start recording
            startRecording();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        isListening = false;
    }

    public void startRecording() {
        sampleRate = Integer.parseInt((String) spinner.getSelectedItem());
        isListening = true;
        editText.setEnabled(false);
        newTimestamp.setEnabled(false);
        actionButton.setText("Stop recording");
        Thread s = new Thread(new SpaceCheck());
        s.start();
        Thread t = new Thread(new Capture());
        t.start();
        startedRecordingTime.setText(dateFormat.format(new Date()));
        startedRecording.setVisibility(View.VISIBLE);
        //setNotification();
    }

  /*  private void setNotification() {
        CharSequence notificationTitle = getText(R.string.notification_title);
        Notification notification = new Notification(R.drawable.icon, notificationTitle, System.currentTimeMillis());
        int flags = Notification.FLAG_ONGOING_EVENT | Notification.FLAG_NO_CLEAR;
        notification.setLatestEventInfo(getApplicationContext(), notificationTitle,
                getText(R.string.notifification_content),
                PendingIntent.getActivity(this,0,new Intent(this, Hertz.class),0));
        notification.flags |= flags;
        notificationManager.notify(NOTICE_RECORD, notification);
    }*/

    /**
     * Monitors the available SD card space while recording.
     *
     *
     */
    private class SpaceCheck implements Runnable {
        @Override
        public void run() {
            String sdDirectory = Environment.getExternalStorageDirectory().toString();
            StatFs stats = new StatFs(sdDirectory);
            while (isListening) {
                stats.restat(sdDirectory);
                final long freeBytes = (long) stats.getAvailableBlocks() * (long) stats.getBlockSize();
                if (freeBytes < 5242880) { // less than 5MB remaining
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            showDialog("Low on disk space", "There isn't enough space " + "left on your SD card (" + freeBytes
                                    + "b) , but what you've " + "recorded up to now has been saved.");
                            actionButton.performClick();
                        }
                    });
                    return;
                }

                try {
                    Thread.sleep(3000);
                } catch (InterruptedException e) {
                }
            }
        }
    }

    /**
     * Capture raw audio data from the hardware and saves it to a buffer in the enclosing class.
     *
     *
     *
     */
    private class Capture implements Runnable {

        private final int channelConfig = AudioFormat.CHANNEL_CONFIGURATION_MONO;
        private final int audioEncoding = AudioFormat.ENCODING_PCM_16BIT;

        // the actual output format is big-endian, signed

        @Override
        public void run() {
            // We're important...
            android.os.Process.setThreadPriority(android.os.Process.THREAD_PRIORITY_URGENT_AUDIO);

            // Allocate Recorder and Start Recording...
            int minBufferSize = AudioRecord.getMinBufferSize(sampleRate, channelConfig, audioEncoding);
            if (AudioRecord.ERROR_BAD_VALUE == minBufferSize || AudioRecord.ERROR == minBufferSize){
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        showDialog("Error recording audio", "Your audio hardware doesn't support the sampling rate you have specified." +
                                "Try a lower sampling rate, if that doesn't work your audio hardware might be broken.");
                        actionButton.performClick();
                    }
                });
                return;
            }
            int bufferSize = 2 * minBufferSize;
            AudioRecord recordInstance =
                    new AudioRecord(MediaRecorder.AudioSource.MIC, sampleRate, channelConfig, audioEncoding,
                            bufferSize);
            if (recordInstance.getState() != AudioRecord.STATE_INITIALIZED) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        showDialog("Error recording audio", "Unable to access the audio recording hardware - is your mic working?");
                        actionButton.performClick();
                    }
                });
                return;
            }

            byte[] tempBuffer = new byte[bufferSize];

            String sdDirectory = Environment.getExternalStorageDirectory().toString();
            outFile = new File(sdDirectory + "/" + filename);
            rawFile = new File(sdDirectory+"/"+rawFileName);
            if (outFile.exists())
                outFile.delete();

            FileOutputStream outStream = null;
            FileOutputStream rawOutputStream = null;
            try {
                outFile.createNewFile();
                rawFile.createNewFile();
                outStream = new FileOutputStream(outFile);
                rawOutputStream = new FileOutputStream(rawFile);
                outStream.write(createHeader(0));// Write a dummy header for a file of length 0 to get updated later
            } catch (Exception e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        showDialog("Error creating file", "The WAV file you specified "
                                + "couldn't be created. Try again with a " + "different filename.");
                        outFile = null;
                        actionButton.performClick();
                    }
                });
                return;
            }

            recordInstance.startRecording();

            try {
                while (isListening) {
                    recordInstance.read(tempBuffer, 0, bufferSize);
                    outStream.write(tempBuffer);
                    rawOutputStream.write(tempBuffer);
                }
            } catch (final IOException e) {
                runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        showDialog("IO Exception", "An exception occured when writing to disk or reading from the microphone\n"
                                + e.getLocalizedMessage()
                                + "\nWhat you have recorded so far should be saved to disk.");
                        actionButton.performClick();
                    }

                });
            } catch (OutOfMemoryError om) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        showDialog("Out of memory", "The system has been " + "too strong for too long - but what you "
                                + "recorded up to now has been saved.");
                        System.gc();
                        actionButton.performClick();
                    }
                });
            }

            // we're done recording
            Log.d("Capture", "Stopping recording");
            recordInstance.stop();
            try {
                outStream.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void showDialog(String title, String message){
        dialog.setTitle(title);
        dialog.setMessage(message);
        dialog.show();
    }

    /**
     * Appends a WAV header to a file containing raw audio data. Uses different strategies depending
     * on amount of free disk space.
     *
     * @param file The file containing 16-bit little-endian PCM data.
     */
    public void appendHeader(File file) {

        int bytesLength = (int) file.length();
        byte[] header = createHeader(bytesLength - WAV_HEADER_LENGTH);

        try {
            RandomAccessFile ramFile = new RandomAccessFile(file, "rw");
            ramFile.seek(0);
            ramFile.write(header);
            ramFile.close();
        } catch (FileNotFoundException e) {
            Log.e("Aero", "Tried to append header to invalid file: " + e.getLocalizedMessage());
            return;
        } catch (IOException e) {
            Log.e("Aero", "IO Error during header append: " + e.getLocalizedMessage());
            return;
        }

    }

    /**
     * Creates a valid WAV header for the given bytes, using the class-wide sample rate
     *
     *  bytes The sound data to be appraised
     * @return The header, ready to be written to a file
     */
    public byte[] createHeader(int bytesLength) {

        int totalLength = bytesLength + 4 + 24 + 8;
        byte[] lengthData = intToBytes(totalLength);
        byte[] samplesLength = intToBytes(bytesLength);
        byte[] sampleRateBytes = intToBytes(this.sampleRate);
        byte[] bytesPerSecond = intToBytes(this.sampleRate * 2);

        ByteArrayOutputStream out = new ByteArrayOutputStream();

        try {
            out.write(new byte[] {'R', 'I', 'F', 'F'});
            out.write(lengthData);
            out.write(new byte[] {'W', 'A', 'V', 'E'});

            out.write(new byte[] {'f', 'm', 't', ' '});
            out.write(new byte[] {0x10, 0x00, 0x00, 0x00}); // 16 bit chunks
            out.write(new byte[] {0x01, 0x00, 0x01, 0x00}); // mono
            out.write(sampleRateBytes); // sampling rate
            out.write(bytesPerSecond); // bytes per second
            out.write(new byte[] {0x02, 0x00, 0x10, 0x00}); // 2 bytes per sample
            out.write(new byte[] {'d', 'a', 't', 'a'});
            out.write(samplesLength);
        } catch (IOException e) {
            Log.e("Create WAV", e.getMessage());
        }

        return out.toByteArray();
    }

    /**
     * Turns an integer into its little-endian four-byte representation
     *
     * @param in The integer to be converted
     * @return The bytes representing this integer
     */
    public static byte[] intToBytes(int in) {
        byte[] bytes = new byte[4];
        for (int i = 0; i < 4; i++) {
            bytes[i] = (byte) ((in >>> i * 8) & 0xFF);
        }
        return bytes;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case 10:
                configure_button();
                break;
            default:
                break;
        }
    }


    void configure_button() {
        // first check for permissions
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.INTERNET,Manifest.permission.RECORD_AUDIO,Manifest.permission.WRITE_EXTERNAL_STORAGE}
                        , 10);
            }
            return;
        }
        // this code won't execute IF permissions are not allowed, because in the line above there is return statement.
        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                locationManager.requestSingleUpdate("gps", listener, looper);
                //locationManager.requestLocationUpdates("gps", 5000, 0, listener);

            }
        });
    }


}