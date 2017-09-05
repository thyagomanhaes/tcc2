package br.com.whatsappandroid.com.cursoandroid.whatsapp.activity.activity.activity.activity.fragment;


import android.content.Context;
import android.content.DialogInterface;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioRecord;
import android.media.AudioTrack;
import android.media.Image;
import android.media.MediaRecorder;
import android.media.audiofx.AcousticEchoCanceler;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.bumptech.glide.Glide;

import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;

import br.com.whatsappandroid.com.cursoandroid.whatsapp.R;
import br.com.whatsappandroid.com.cursoandroid.whatsapp.activity.activity.activity.activity.GravadorActivity;
import br.com.whatsappandroid.com.cursoandroid.whatsapp.activity.activity.activity.activity.helper.VisualizerView;

/**
 * A simple {@link Fragment} subclass.
 */
public class ConversasFragment extends Fragment {

    // Variáveis da interface gráfica
    private VisualizerView visualizer;
    private ToggleButton auscultationButton;

    private static final String TAG = ConversasFragment.class.getName();
    private MediaRecorder recorder; // para gravar
    private Handler handler;
    private boolean recording;
    private boolean iniciouAusculta = false;

    private Chronometer ch;


    private String TAG2 = "AUDIO_RECORD_PLAYBACK";
    private boolean isRunning = true;
    private Thread m_thread;               /* Thread for running the Loop */

    private AudioRecord recorder2 = null;
    private AudioTrack track = null;

    int bufferSize = 320;                  /* Buffer for recording data */
    byte buffer[] = new byte[bufferSize];
    private Button botaoStart;
    private Button botaoStop;
    private int amplitudeCalculada;

    private TextView dataHora;
    private double lastLevel = 0;


    private static final float MAX_REPORTABLE_AMP = 32767f;
    private static final float MAX_REPORTABLE_DB = 90.3087f;

    public ConversasFragment() {
        // Required empty public constructor
    }

    public void startChronometer(View view){
        ch.setBase(SystemClock.elapsedRealtime());
        ch.start();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_conversas, container, false);

        // Capturando os elementos da interface gráfica
        visualizer = (VisualizerView) view.findViewById(R.id.visualizerView); // Visualizador para o gráfico
        ch = (Chronometer) view.findViewById(R.id.chronometer2); // Cronômetro
        auscultationButton = (ToggleButton) view.findViewById(R.id.btAuscultation); // Botão para Iniciar/Pausar Auscultação
        dataHora =(TextView) view.findViewById(R.id.data);

        String data = "dd/MM/yyyy";
        String hora = "h:mm a";
        String data1, hora1;
        java.util.Date agora = new java.util.Date();;
        SimpleDateFormat formata = new SimpleDateFormat(data);
        data1 = formata.format(agora);
        formata = new SimpleDateFormat(hora);
        hora1 = formata.format(agora);

        dataHora.setText(data1 + " - " + hora1);

        // PEGANDO O BOTÃO DE START E PAUSA
        botaoStart = (Button) view.findViewById(R.id.StartButton);
        botaoStop = (Button) view.findViewById(R.id.StopButton);

        handler = new Handler();


        botaoStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG2, "======== Start Button Pressed ==========");
                isRunning = true;
                do_loopback(isRunning); // primeira


                //recorder2 = new AudioRecord(MediaRecorder.AudioSource.MIC, 8000,
                 //       AudioFormat.CHANNEL_IN_MONO,
                 //       AudioFormat.ENCODING_PCM_16BIT, bufferSize);

                //recorder2.startRecording();
                handler.post(updateVisualizer);

            }
        });

        botaoStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG2, "======== Stop Button Pressed ==========");
                isRunning = false;
                do_loopback(isRunning);
                //handler.post(updateVisualizer);
            }
        });

        ImageView imageView = (ImageView) view.findViewById(R.id.imageView);   // Gif 1
        ImageView imageView2 = (ImageView) view.findViewById(R.id.imageView2); // Gif 2

        //load with glide but you can download image gif or share link
        Glide.with(getActivity())
                .load(R.drawable.heartbeat3)
                .asGif()
                .placeholder(R.drawable.heartbeat3)
                .crossFade().into(imageView);

        Glide.with(getActivity())
                .load(R.drawable.heartbeat3)
                .asGif()
                .placeholder(R.drawable.heartbeat3)
                .crossFade().into(imageView2);



        auscultationButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if (isChecked){
                    visualizer.clear();

                    if (recorder == null)
                        recorder = new MediaRecorder();
                    recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
                    recorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
                    recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
                    recorder.setAudioEncodingBitRate(16);
                    recorder.setAudioSamplingRate(44100);

                    try{
                        File tempFile = File.createTempFile("VoiceRecorder", ".3gp", getActivity().getExternalFilesDir(null));
                        recorder.setOutputFile(tempFile.getAbsolutePath());
                        recorder.prepare();
                        recorder.start();
                        ch.setBase(SystemClock.elapsedRealtime());
                        ch.start();
                        recording = true;
                        handler.post(updateVisualizer);
                    } // fim do try
                    catch (IllegalStateException e){
                        Log.e(TAG, e.toString());
                    } catch (IOException e){
                        Log.e(TAG, e.toString());
                    } // fim do catch
                }

                else {
                    recorder.stop();
                    ch.stop();
                    recorder.reset();
                    recording = false;
                } // fim do else
            }
        });

        return view;
    }

    Runnable updateVisualizer = new Runnable() {
        @Override
        public void run() {
            if (isRunning){
                //int x = recorder.getMaxAmplitude();
                visualizer.addAmplitude(amplitudeCalculada);
                visualizer.invalidate();
                handler.postDelayed(this, 50);
            }
        }
    };

    private void do_loopback(final boolean flag)
    {
        m_thread = new Thread(new Runnable() {
            public void run() {
                run_loop(flag);
            }
        });
        m_thread.start();
    }

    private void readAudioBuffer() {

        try {
            short[] buffer = new short[bufferSize];

            int bufferReadResult = 1;

            if (recorder2 != null) {

                // Sense the voice...
                bufferReadResult = recorder2.read(buffer, 0, bufferSize);
                double sumLevel = 0;
                for (int i = 0; i < bufferReadResult; i++) {
                    sumLevel += buffer[i];
                }
                lastLevel = Math.abs((sumLevel / bufferReadResult));
                Log.d("LAST LEVEL", String.valueOf(lastLevel));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void run_loop (boolean isRunning)
    {

        /** == If Stop Button is pressed == **/
        if (isRunning == false) {
            Log.d(TAG2, "=====  Stop Button is pressed ===== ");

            if (AudioRecord.STATE_INITIALIZED == recorder2.getState()){
                recorder2.stop();
                recorder2.release();
            }
            if (AudioTrack.STATE_INITIALIZED == track.getState()){
                track.stop();
                track.release();
            }
            return;
        }


        /** ======= Initialize AudioRecord and AudioTrack ======== **/
        recorder2 = findAudioRecord(recorder2);
        if (recorder2 == null) {
            Log.e(TAG2, "======== findAudioRecord : Returned Error! =========== ");
            return;
        }

        track = findAudioTrack(track);
        if (track == null) {
            Log.e(TAG2, "======== findAudioTrack : Returned Error! ========== ");
            return;
        }

        if ((AudioRecord.STATE_INITIALIZED == recorder2.getState()) &&
                (AudioTrack.STATE_INITIALIZED == track.getState()))
        {
            recorder2.startRecording();
            Log.d(TAG2, "========= Recorder Started... =========");
            track.play();
            Log.d(TAG2, "========= Track Started... =========");
        }
        else
        {
            Log.d(TAG2, "==== Initilazation failed for AudioRecord or AudioTrack =====");
            return;
        }

        /** ------------------------------------------------------ **/

    /* Recording and Playing in chunks of 320 bytes */
        bufferSize = 320;

        int cAmplitude = 0;
        int bufferReadResult = 1;

        while (isRunning == true)
        {
        /* Read & Write to the Device */
            recorder2.read(buffer, 0, bufferSize);

/*            double sumLevel = 0;
            for (int i =0; i < bufferReadResult ; i++){
                sumLevel += buffer[i];
            }
            amplitudeCalculada = (int) Math.abs( (sumLevel/bufferReadResult))*1000;*/

/*            int max = 0;
            for (short s : buffer){
                amplitudeCalculada = Math.abs(s) * 100;
                Log.d("AMPLITUDE", String.valueOf(Math.abs(s)));
            }*/


/*
            //int readSize = recorder2.read(buffer, 0, buffer.length);
            int sum = 0;
            double amplitude = 0;
            for (int i = 0; i < bufferReadSize; i++ ){
                sum += Math.abs(buffer[i]);
            }
            if (bufferReadSize > 0){
                amplitude = sum / bufferReadSize;
                //amplitudeCalculada = (int) Math.sqrt(amplitude);
            }
            //amplitudeCalculada = (int) (MAX_REPORTABLE_DB + (20 * Math.log10(amplitude / MAX_REPORTABLE_AMP)));
            Log.d("AMPLITUDE", String.valueOf(amplitude));
            //amplitudeCalculada = calculateDecibel(buffer);
*/
            //Log.d("AMPLITUDE", String.valueOf(amplitudeCalculada));
            track.write(buffer, 0, bufferSize);
            amplitudeCalculada = (buffer[0] & 0xff) << 8 | buffer[1];
            amplitudeCalculada = Math.abs(amplitudeCalculada);
            Log.d("AMPLITUDE", String.valueOf(amplitudeCalculada));
        }
        Log.i(TAG2, "Loopback exit");
        return;
    }

    public AudioTrack findAudioTrack (AudioTrack track)
    {
        Log.d(TAG2, "===== Initializing AudioTrack API ====");
        int m_bufferSize = AudioTrack.getMinBufferSize(8000,
                AudioFormat.CHANNEL_OUT_MONO,
                AudioFormat.ENCODING_PCM_16BIT);

        if (m_bufferSize != AudioTrack.ERROR_BAD_VALUE)
        {
            track = new AudioTrack(AudioManager.STREAM_MUSIC, 8000,
                    AudioFormat.CHANNEL_OUT_MONO,
                    AudioFormat.ENCODING_PCM_16BIT, m_bufferSize,
                    AudioTrack.MODE_STREAM);

            if (track.getState() == AudioTrack.STATE_UNINITIALIZED) {
                Log.e(TAG2, "===== AudioTrack Uninitialized =====");
                return null;
            }
        }
        return track;
    }

    public AudioRecord findAudioRecord (AudioRecord recorder)
    {
        Log.d(TAG2, "===== Initializing AudioRecord API =====");
        int m_bufferSize = AudioRecord.getMinBufferSize(8000,
                AudioFormat.CHANNEL_IN_MONO,
                AudioFormat.ENCODING_PCM_16BIT);

        if (m_bufferSize != AudioRecord.ERROR_BAD_VALUE)
        {
            recorder = new AudioRecord(MediaRecorder.AudioSource.MIC, 8000,
                    AudioFormat.CHANNEL_IN_MONO,
                    AudioFormat.ENCODING_PCM_16BIT, m_bufferSize);

            if (recorder.getState() == AudioRecord.STATE_UNINITIALIZED) {
                Log.e(TAG2, "====== AudioRecord UnInitilaised ====== ");
                return null;
            }
        }
        return recorder;
    }

}
