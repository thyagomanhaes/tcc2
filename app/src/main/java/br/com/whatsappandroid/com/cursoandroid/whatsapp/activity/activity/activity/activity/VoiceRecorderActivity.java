package br.com.whatsappandroid.com.cursoandroid.whatsapp.activity.activity.activity.activity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.MediaRecorder;
import android.os.Handler;
import android.os.SystemClock;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import br.com.whatsappandroid.com.cursoandroid.whatsapp.R;
import br.com.whatsappandroid.com.cursoandroid.whatsapp.activity.activity.activity.activity.helper.VisualizerView;
import br.com.whatsappandroid.com.cursoandroid.whatsapp.activity.activity.activity.activity.model.Gravacao;
import io.realm.Realm;

public class VoiceRecorderActivity extends AppCompatActivity {

    private static final String TAG = VoiceRecorder.class.getName();
    private MediaRecorder recorder; // usado para gravar áudio
    private Handler handler; // Handler para atualizar o visualizador
    private boolean recording; // estamos gravando?

    // Variáveis da interface gráfica do usuário
    private VisualizerView visualizer;
    private ToggleButton recordButton;
    private Button saveButton;
    //private Button deleteButton;

    private Chronometer ch;

    private List<Integer> amplitudes;
    private TextView qtdAmplitudes;
    private int total = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        visualizer = (VisualizerView) findViewById(R.id.visualizerView2);
        recordButton = (ToggleButton) findViewById(R.id.recordButton2);
        saveButton = (Button) findViewById(R.id.saveButton2);
        ch = (Chronometer) findViewById(R.id.chronometer);
        qtdAmplitudes = (TextView) findViewById(R.id.amplitudes);

        handler = new Handler();
        amplitudes = new ArrayList<Integer>();

        recordButton.setOnCheckedChangeListener(recordButtonListener);

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                LayoutInflater inflater = (LayoutInflater) getSystemService(
                        Context.LAYOUT_INFLATER_SERVICE);
                View view = inflater.inflate(R.layout.name_edittext, null);
                final EditText nameEditText = (EditText) view.findViewById(R.id.nameEditText);

                AlertDialog.Builder inputDialog = new AlertDialog.Builder(VoiceRecorderActivity.this);
                inputDialog.setView(view);
                inputDialog.setTitle("Gravação");
                inputDialog.setPositiveButton("Salvar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String name = nameEditText.getText().toString().trim();

                        if (name.length() != 0){
                            File tempFile = (File) v.getTag();
                            File newFile = new File(getExternalFilesDir(null).getAbsolutePath() + File.separator + name + ".3gp");
                            tempFile.renameTo(newFile);

                            Realm realm = Realm.getDefaultInstance();// ... Utilização da base...realm.close();
                            realm.beginTransaction();

                            Number maxId = realm.where(Gravacao.class).max("idGravacao");

                            int nextId = (maxId == null) ? 1 : maxId.intValue() + 1;

                            Gravacao gravacao = new Gravacao();
                            gravacao.setIdGravacao(nextId);
                            gravacao.setNome(name); // nome da gravação informada pelo usuario
                            gravacao.setDataGravacao(new Date());
                            gravacao.setBpm(String.valueOf(total));
                            //gravacao.setArquivoAudio(newFile);

                            realm.copyToRealm(gravacao);

                            Toast.makeText(VoiceRecorderActivity.this, "Gravação: " + gravacao.getNome() + " cadastrada com sucesso!", Toast.LENGTH_LONG).show();

                            realm.commitTransaction();
                            realm.close();



                            saveButton.setEnabled(false);
                            //deleteButton.setEnabled(false);
                            recordButton.setEnabled(true);
                            //viewSavedRecordingsButton.setEnabled(true);
                        }
                        else {
                            Toast message = Toast.makeText(VoiceRecorderActivity.this, "ERRO PESSOAL", Toast.LENGTH_SHORT);
                            message.setGravity(Gravity.CENTER, message.getXOffset()/2, message.getYOffset()/2);
                            message.show();
                        }
                    }
                });

                inputDialog.setNegativeButton("Cancelar", null);
                inputDialog.show();
                //   inputDialog.setPositiveButton()
            }
        });

    }

    Runnable updateVisualizer = new Runnable() {
        @Override
        public void run() {
            if (recording){
                int x = recorder.getMaxAmplitude();
                amplitudes.add(x);
                visualizer.addAmplitude(x);
                visualizer.invalidate();
                handler.postDelayed(this, 50);
            }
        }
    };

    CompoundButton.OnCheckedChangeListener recordButtonListener = new CompoundButton.OnCheckedChangeListener(){
        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            if (isChecked){
                visualizer.clear();

                saveButton.setEnabled(false);
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
                    File tempFile = File.createTempFile("VoiceRecorder", ".3gp", getExternalFilesDir(null));
                    saveButton.setTag(tempFile);
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
            } // fim do if
            else{
                recorder.stop();
                recorder.reset();
                recording = false;
                ch.stop();
                saveButton.setEnabled(true);
                //deleteButton.setEnabled(true);
                recordButton.setEnabled(false);

                for (Integer valor : amplitudes){
                    if (valor > 4000)
                        total += valor;
                    Log.d("AMPLITUDE", String.valueOf(valor));
                }
                qtdAmplitudes.setText(String.valueOf(total*6) + "BPM");

            } // fim do else
        } // fim do método
    };
}
