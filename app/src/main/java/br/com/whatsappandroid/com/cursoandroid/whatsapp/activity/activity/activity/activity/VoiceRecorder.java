package br.com.whatsappandroid.com.cursoandroid.whatsapp.activity.activity.activity.activity;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.speech.tts.Voice;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Toast;
import android.widget.ToggleButton;

import java.io.File;
import java.io.IOException;
import java.util.Date;

import br.com.whatsappandroid.com.cursoandroid.whatsapp.R;
import br.com.whatsappandroid.com.cursoandroid.whatsapp.activity.activity.activity.activity.helper.VisualizerView;
import br.com.whatsappandroid.com.cursoandroid.whatsapp.activity.activity.activity.activity.model.Gravacao;
import io.realm.Realm;

/**
 * Created by tikoextreme on 30/08/17.
 */

public class VoiceRecorder extends Activity {

    private static final String TAG = VoiceRecorder.class.getName();
    private MediaRecorder recorder; // usado para gravar áudio
    private Handler handler; // Handler para atualizar o visualizador
    private boolean recording; // estamos gravando?

    // Variáveis da interface gráfica do usuário
    private VisualizerView visualizer;
    private ToggleButton recordButton;
    private Button saveButton;
    //private Button deleteButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
    }



/*    @Override
    protected void onResume(){
        super.onResume();
        recordButton.setOnCheckedChangeListener( recordButtonListener );
    }*/

/*    @Override
    protected void onPause(){
        super.onPause();
        recordButton.setOnCheckedChangeListener( null );

        if (recorder != null){
            handler.removeCallbacks( updateVisualizer );

            visualizer.clear(); // limpa o visualizador para a próxima gravação
            recordButton.setChecked(false); // reconfigura recordButton
            recorder.release();
            recording = false;
            recorder = null;
        }
    }*/

/*    CompoundButton.OnCheckedChangeListener recordButtonListener =
            new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                    if (isChecked){
                        visualizer.clear();

                        saveButton.setEnabled(false);

                        if (recorder == null)
                            recorder = new MediaRecorder();

                        recorder.setAudioSource(MediaRecorder.AudioSource.MIC); // A fonte de áudio é o microfone
                        recorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
                        recorder.setAudioEncodingBitRate(16);
                        recorder.setAudioSamplingRate(44100);

                        try{
                            File tempFile = File.createTempFile("VoiceRecorder", ".3gp", getExternalFilesDir(null));

                            saveButton.setTag(tempFile);

                            recorder.setOutputFile(tempFile.getAbsolutePath());
                            recorder.prepare();
                            recorder.start();
                            recording = true;

                            handler.post(updateVisualizer);
                        } // fim do try
                        catch (IllegalStateException e){
                            Log.e(TAG, e.toString());
                        } // fim do catch
                        catch (IOException e){
                            Log.e(TAG, e.toString());
                        }
                    }
                    else {
                        recorder.stop();
                        recorder.reset();
                        recording = false;
                        saveButton.setEnabled(true);
                        recordButton.setEnabled(false);

                    }
                }
            };*/


/*    Runnable updateVisualizer = new Runnable() {
        @Override
        public void run() {
            if (recording){
                int x = recorder.getMaxAmplitude();
                visualizer.addAmplitude(x);
                visualizer.invalidate();
                handler.postDelayed(this, 50);
            }
        }
    };*/




}
