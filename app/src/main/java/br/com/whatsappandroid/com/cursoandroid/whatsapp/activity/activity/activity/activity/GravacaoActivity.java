package br.com.whatsappandroid.com.cursoandroid.whatsapp.activity.activity.activity.activity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.audiofx.Visualizer;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import java.io.File;
import java.sql.Time;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import br.com.whatsappandroid.com.cursoandroid.whatsapp.R;
import br.com.whatsappandroid.com.cursoandroid.whatsapp.activity.activity.activity.activity.helper.VisualizerView2;
import br.com.whatsappandroid.com.cursoandroid.whatsapp.activity.activity.activity.activity.model.Gravacao;
import io.realm.Realm;

public class GravacaoActivity extends AppCompatActivity {

    private static final String TAG = GravacaoActivity.class.getName();
    private MediaPlayer mediaPlayer;

    //Declarando os componentes da View
    private TextView nomePacienteTextView;
    private TextView nomeGravacaoTextView;
    private TextView emailPacienteTextView;
    private TextView tempoView;
    private TextView tempoView2;

    private ToggleButton playButton;
    private ImageView anotacaoImageView;

    private Visualizer mVisualizer;
    private MediaPlayer mMediaPlayer;


    VisualizerView2 mVisualizerView;

    private String nomePaciente;
    private String nomeGravacao;
    private String emailPaciente;
    private int idPacienteSelecionado;

    private double startTime = 0;
    private double finalTime = 0;

    private Handler myHandler = new Handler();
    private int forwardTime = 5000;
    private int backwardTime = 5000;

    private static int oneTimeOnly = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gravacao);

        mVisualizerView = (VisualizerView2) findViewById(R.id.myvisualizerview);

        Intent intent = getIntent();
        nomePaciente = (String) intent.getSerializableExtra("nomePaciente");
        nomeGravacao = (String) intent.getSerializableExtra("nomeGravacao");
        emailPaciente = (String) intent.getSerializableExtra("emailPaciente");

        nomeGravacaoTextView = (TextView) findViewById(R.id.nomeGravacao);
        nomePacienteTextView = (TextView) findViewById(R.id.nomePaciente);
        emailPacienteTextView= (TextView) findViewById(R.id.emailPaciente);

        playButton = (ToggleButton) findViewById(R.id.playButton);
        anotacaoImageView = (ImageView) findViewById(R.id.imageView4); // Botão para abrir uma caixa de dialogo para anotação sobre o áudio do paciente

        nomeGravacaoTextView.setText( nomeGravacao );
        nomePacienteTextView.setText( nomePaciente );
        emailPacienteTextView.setText( emailPaciente );

        tempoView = (TextView) findViewById(R.id.tempo);
        tempoView2 = (TextView) findViewById(R.id.textView2);


        playButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {

                String filePath = getExternalFilesDir(null).getAbsolutePath() + File.separator + nomeGravacao;


                try{
                    // configura MediaPlayer para reproduzir o arquivo em filePath
                    mediaPlayer = new MediaPlayer();
                    mediaPlayer.reset(); // reconfigura MediaPlayer
                    mediaPlayer.setDataSource(filePath);
                    mediaPlayer.prepare();

                    setupVisualizerFxAndUI();
                    // Make sure the visualizer is enabled only when you actually want to
                    // receive data, and
                    // when it makes sense to receive data.
                    mVisualizer.setEnabled(true);
                    // When the stream ends, we don't need to collect any more data. We
                    // don't do this in
                    // setupVisualizerFxAndUI because we likely want to have more,
                    // non-Visualizer related code
                    // in this callback.
                    mediaPlayer
                            .setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                                public void onCompletion(MediaPlayer mediaPlayer) {
                                    mVisualizer.setEnabled(false);
                                }
                            });

                    Toast.makeText(getApplicationContext(), "Playing sound", Toast.LENGTH_SHORT).show();
                    mediaPlayer.start();

                    finalTime = mediaPlayer.getDuration();
                    startTime = mediaPlayer.getCurrentPosition();

                    if (oneTimeOnly == 0){
                        oneTimeOnly = 1;
                    }

                    tempoView.setText(String.format("%d:%d",
                            TimeUnit.MILLISECONDS.toMinutes((long) finalTime),
                            TimeUnit.MILLISECONDS.toSeconds((long) finalTime) -
                                    TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes((long)
                                            finalTime)))
                    );

                    tempoView2.setText(String.format("%d:%d",
                            TimeUnit.MILLISECONDS.toMinutes((long) startTime),
                            TimeUnit.MILLISECONDS.toSeconds((long) startTime) -
                                    TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes((long)
                                            startTime)))
                    );

                    myHandler.postDelayed(UpdateSongTime, 100);


                    //mediaPlayer.prepare(); // prepara MediaPlayer

                    //mMediaPlayer = MediaPlayer.create(this, R.raw.teste);

                    //mediaPlayer.start();

                    //updater.run(); // começa a atualizar progressSeekBar
                }
                catch (Exception e){
                    Log.e(TAG, e.toString()); // registra exceções
                }

            }
        });

        // Ação que será executada ao clicar na imagem de anotação
        anotacaoImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LayoutInflater inflater = (LayoutInflater) getSystemService(
                        Context.LAYOUT_INFLATER_SERVICE);
                view = inflater.inflate(R.layout.name_edittext, null);
                final EditText nameEditText = (EditText) view.findViewById(R.id.nameEditText);

                AlertDialog.Builder inputDialog = new AlertDialog.Builder(GravacaoActivity.this);
                inputDialog.setView(view);
                inputDialog.setTitle("Anotação");
                inputDialog.setPositiveButton("Salvar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                     }
                });

                inputDialog.setNegativeButton("Cancelar", null);
                inputDialog.show();
            }
        });

        //initAudio();

    }

    Runnable UpdateSongTime = new Runnable() {
        public void run() {
            startTime = mediaPlayer.getCurrentPosition();
            tempoView2.setText(String.format("0%d:0%d",
                    TimeUnit.MILLISECONDS.toMinutes((long) startTime),
                    TimeUnit.MILLISECONDS.toSeconds((long) startTime) -
                            TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.
                                    toMinutes((long) startTime)))
            );
            //seekbar.setProgress((int)startTime);
            myHandler.postDelayed(this, 100);
        }
    };

    @Override
    protected void onPause() {
        super.onPause();
        if (isFinishing() && mMediaPlayer != null) {
            mVisualizer.release();
            mMediaPlayer.release();
            mMediaPlayer = null;
        }
    }

    private void initAudio() {
        setVolumeControlStream(AudioManager.STREAM_MUSIC);
        //mMediaPlayer = MediaPlayer.create(this, R.raw.teste);

        setupVisualizerFxAndUI();
        // Make sure the visualizer is enabled only when you actually want to
        // receive data, and
        // when it makes sense to receive data.
        mVisualizer.setEnabled(true);
        // When the stream ends, we don't need to collect any more data. We
        // don't do this in
        // setupVisualizerFxAndUI because we likely want to have more,
        // non-Visualizer related code
        // in this callback.
        mMediaPlayer
                .setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                    public void onCompletion(MediaPlayer mediaPlayer) {
                        mVisualizer.setEnabled(false);
                        playButton.setChecked(false);
                    }
                });
        mMediaPlayer.start();

    }

    private void setupVisualizerFxAndUI() {

        // Create the Visualizer object and attach it to our media player.
        mVisualizer = new Visualizer(mediaPlayer.getAudioSessionId());
        mVisualizer.setCaptureSize(Visualizer.getCaptureSizeRange()[1]);
        mVisualizer.setDataCaptureListener(
                new Visualizer.OnDataCaptureListener() {
                    public void onWaveFormDataCapture(Visualizer visualizer,
                                                      byte[] bytes, int samplingRate) {
                        mVisualizerView.updateVisualizer(bytes);
                    }

                    public void onFftDataCapture(Visualizer visualizer,
                                                 byte[] bytes, int samplingRate) {
                    }
                }, Visualizer.getMaxCaptureRate() / 2, true, false);
    }
}
