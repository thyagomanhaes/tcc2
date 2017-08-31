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
import java.util.Date;

import br.com.whatsappandroid.com.cursoandroid.whatsapp.R;
import br.com.whatsappandroid.com.cursoandroid.whatsapp.activity.activity.activity.activity.GravadorActivity;
import br.com.whatsappandroid.com.cursoandroid.whatsapp.activity.activity.activity.activity.helper.VisualizerView;

/**
 * A simple {@link Fragment} subclass.
 */
public class ConversasFragment extends Fragment {

    boolean isRecording = false;
    AudioManager am = null;
    AudioRecord record = null;
    AudioTrack track = null;
    TextView textData;

    // Variáveis da interface gráfica
    private VisualizerView visualizer;
    private ToggleButton recordButton;
    private Button saveButton;
    private Button deleteButton;
    private Button pararButton;
    private Button inicioAusculta;

    private ToggleButton auscultationButton;

    private static final String TAG = ConversasFragment.class.getName();
    private MediaRecorder recorder; // para gar
    private Handler handler;
    private boolean recording;
    private boolean iniciouAusculta = false;

    private Chronometer ch;

    public ConversasFragment() {
        // Required empty public constructor
    }

    public void startChronometer(View view){
        ch.setBase(SystemClock.elapsedRealtime());
        ch.start();
    }

    private void initRecordAndTrack()
    {
        int min = AudioRecord.getMinBufferSize(8000, AudioFormat.CHANNEL_IN_MONO, AudioFormat.ENCODING_PCM_16BIT);
        record = new AudioRecord(MediaRecorder.AudioSource.VOICE_COMMUNICATION, 8000, AudioFormat.CHANNEL_IN_MONO, AudioFormat.ENCODING_PCM_16BIT,
                min);
        int maxJitter = AudioTrack.getMinBufferSize(8000, AudioFormat.CHANNEL_OUT_MONO, AudioFormat.ENCODING_PCM_16BIT);
        track = new AudioTrack(AudioManager.MODE_IN_COMMUNICATION, 8000, AudioFormat.CHANNEL_OUT_MONO, AudioFormat.ENCODING_PCM_16BIT, maxJitter,
                AudioTrack.MODE_STREAM);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_conversas, container, false);

        initRecordAndTrack();

        am = (AudioManager) getActivity().getSystemService(Context.AUDIO_SERVICE);
        am.setSpeakerphoneOn(true);

        // Thread para executar o áudio gravado em paralelo
        (new Thread()
        {
            @Override
            public void run()
            {
                recordAndPlay();
            }
        }).start();


        //recordButton = (ToggleButton) view.findViewById(R.id.recordButton);
        //saveButton = (Button) view.findViewById(R.id.saveButton);
        //saveButton.setEnabled(false);
        //deleteButton = (Button) view.findViewById(R.id.deleteButton);
        //deleteButton.setEnabled(false);
        //viewSavedRecordingsButton = (Button) view.findViewById(R.id.viewSavedRecordingsButton);
        visualizer = (VisualizerView) view.findViewById(R.id.visualizerView);

        ch = (Chronometer) view.findViewById(R.id.chronometer2);

        // Botão para Iniciar/Pausar Auscultação
        auscultationButton = (ToggleButton) view.findViewById(R.id.btAuscultation);

        ImageView imageView = (ImageView) view.findViewById(R.id.imageView);
        ImageView imageView2 = (ImageView) view.findViewById(R.id.imageView2);

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


        //recordButton.setVisibility(View.INVISIBLE);
        //saveButton.setVisibility(View.INVISIBLE);
        //deleteButton.setVisibility(View.INVISIBLE);

        //inicioAusculta = (Button) view.findViewById(R.id.btIniciarAusculta);


/*        // AÇÃO DO BOTÃO SALVAR
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(
                        Context.LAYOUT_INFLATER_SERVICE);
                View view = inflater.inflate(R.layout.name_edittext, null);
                final EditText nameEditText = (EditText) view.findViewById(R.id.nameEditText);

                AlertDialog.Builder inputDialog = new AlertDialog.Builder(getActivity());
                inputDialog.setView(view);
                inputDialog.setTitle("Gravação");
                inputDialog.setPositiveButton("Salvar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String name = nameEditText.getText().toString().trim();
                        if (name.length() != 0){
                            File tempFile = (File) v.getTag();
                            File newFile = new File(getActivity().getExternalFilesDir(null).getAbsolutePath() + File.separator + name + ".3gp");
                            tempFile.renameTo(newFile);
                            saveButton.setEnabled(false);
                            deleteButton.setEnabled(false);
                            recordButton.setEnabled(true);
                            //viewSavedRecordingsButton.setEnabled(true);
                        }
                        else {
                            Toast message = Toast.makeText(getActivity(), "ERRO PESSOAL", Toast.LENGTH_SHORT);
                            message.setGravity(Gravity.CENTER, message.getXOffset()/2, message.getYOffset()/2);
                            message.show();
                        }
                    }
                });

                inputDialog.setNegativeButton("Cancelar", null);
                inputDialog.show();
                //   inputDialog.setPositiveButton()
            }
        });*/





/*        inicioAusculta.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                iniciouAusculta = true;
                inicioAusculta.setVisibility(View.INVISIBLE);

                //recordButton.setVisibility(View.VISIBLE);
                //saveButton.setVisibility(View.VISIBLE);
                //deleteButton.setVisibility(View.VISIBLE);

                visualizer.clear();

                //saveButton.setEnabled(false);
                //deleteButton.setEnabled(false);
                //viewSavedRecordingsButton.setEnabled(false);

                if (recorder == null)
                    recorder = new MediaRecorder();
                recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
                recorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
                recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
                recorder.setAudioEncodingBitRate(16);
                recorder.setAudioSamplingRate(44100);

                try{
                    File tempFile = File.createTempFile("VoiceRecorder", ".3gp", getActivity().getExternalFilesDir(null));
                    //saveButton.setTag(tempFile);
                    //deleteButton.setTag(tempFile);
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
        });*/

        handler = new Handler();

        auscultationButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if (isChecked){
                    //iniciouAusculta = true;
                    //inicioAusculta.setVisibility(View.INVISIBLE);

                    //recordButton.setVisibility(View.VISIBLE);
                    //saveButton.setVisibility(View.VISIBLE);
                    //deleteButton.setVisibility(View.VISIBLE);

                    visualizer.clear();

                    //saveButton.setEnabled(false);
                    //deleteButton.setEnabled(false);
                    //viewSavedRecordingsButton.setEnabled(false);

                    if (recorder == null)
                        recorder = new MediaRecorder();
                    recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
                    recorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
                    recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
                    recorder.setAudioEncodingBitRate(16);
                    recorder.setAudioSamplingRate(44100);

                    try{
                        File tempFile = File.createTempFile("VoiceRecorder", ".3gp", getActivity().getExternalFilesDir(null));
                        //saveButton.setTag(tempFile);
                        //deleteButton.setTag(tempFile);
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
        //auscultationButton.setOnCheckedChangeListener(recordButtonListener);

        return view;
    }

    Runnable updateVisualizer = new Runnable() {
        @Override
        public void run() {
            if (recording){
                int x = recorder.getMaxAmplitude();
                visualizer.addAmplitude(x);
                visualizer.invalidate();
                handler.postDelayed(this, 50);
            }
        }
    };

    private void recordAndPlay()
    {
        short[] lin = new short[1024];
        int num = 0;
        am.setMode(AudioManager.MODE_IN_COMMUNICATION);
        while (true)
        {
            if (isRecording)
            {
                num = record.read(lin, 0, 1024);
                track.write(lin, 0, num);
            }
        }
    }

    private void startRecordAndPlay()
    {
        record.startRecording();
        track.play();
        isRecording = true;
    }

/*    CompoundButton.OnCheckedChangeListener recordButtonListener = new CompoundButton.OnCheckedChangeListener(){
        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            if (!isChecked){
                recorder.stop();
                ch.stop();
                recorder.reset();
                recording = false;
                //saveButton.setEnabled(true);
                //deleteButton.setEnabled(true);
                //recordButton.setEnabled(false);
            } // fim do else
        } // fim do método
    };*/

    /*
    private void initRecordAndTrack()
    {
        int min = AudioRecord.getMinBufferSize(8000, AudioFormat.CHANNEL_IN_MONO, AudioFormat.ENCODING_PCM_16BIT);
        record = new AudioRecord(MediaRecorder.AudioSource.VOICE_COMMUNICATION, 8000, AudioFormat.CHANNEL_IN_MONO, AudioFormat.ENCODING_PCM_16BIT,
                min);
        int maxJitter = AudioTrack.getMinBufferSize(8000, AudioFormat.CHANNEL_OUT_MONO, AudioFormat.ENCODING_PCM_16BIT);
        track = new AudioTrack(AudioManager.MODE_IN_COMMUNICATION, 8000, AudioFormat.CHANNEL_OUT_MONO, AudioFormat.ENCODING_PCM_16BIT, maxJitter,
                AudioTrack.MODE_STREAM);
    }

    private void recordAndPlay()
    {
        short[] lin = new short[1024];
        int num = 0;
        am.setMode(AudioManager.MODE_IN_COMMUNICATION);
        while (true)
        {
            if (isRecording)
            {
                num = record.read(lin, 0, 1024);
                track.write(lin, 0, num);
            }
        }
    }

    private void startRecordAndPlay()
    {
        record.startRecording();
        track.play();
        isRecording = true;
    }

    private void stopRecordAndPlay()
    {
        record.stop();
        track.pause();
        isRecording = false;
    }*/

}
