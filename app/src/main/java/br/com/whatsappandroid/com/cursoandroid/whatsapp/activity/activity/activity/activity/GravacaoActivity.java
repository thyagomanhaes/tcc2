package br.com.whatsappandroid.com.cursoandroid.whatsapp.activity.activity.activity.activity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.audiofx.Visualizer;
import android.os.Environment;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import java.io.File;
import java.io.IOException;
import java.sql.Time;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import br.com.whatsappandroid.com.cursoandroid.whatsapp.R;
import br.com.whatsappandroid.com.cursoandroid.whatsapp.activity.activity.activity.activity.helper.VisualizerView;
import br.com.whatsappandroid.com.cursoandroid.whatsapp.activity.activity.activity.activity.helper.VisualizerView2;
import br.com.whatsappandroid.com.cursoandroid.whatsapp.activity.activity.activity.activity.model.Gravacao;
import br.com.whatsappandroid.com.cursoandroid.whatsapp.activity.activity.activity.activity.model.RealmInt;
import io.realm.Realm;
import io.realm.RealmList;

public class GravacaoActivity extends AppCompatActivity {

    private static final String TAG = GravacaoActivity.class.getName();
    private MediaPlayer mediaPlayer;

    //Declarando os componentes da View
    private TextView nomePacienteTextView;
    private TextView nomeGravacaoTextView;
    private TextView emailPacienteTextView;
    private TextView tempoView;
    private TextView tempoView2;
    private TextView bpmGravacao;
    private TextView textoAnotacao;

    private Button playButton;
    private Button stopButton;
    private Button botaotAnotacao;
    private Button botaoExcluir;
    private Button botaoCompartilhar;
    private ImageView anotacaoImageView;

    private Visualizer mVisualizer;
    private MediaPlayer mMediaPlayer;


    VisualizerView2 mVisualizerView;

    private String nomePaciente;
    private String nomeGravacao;
    private String emailPaciente;
    private String bpmGravacao2;
    private int idGravacaoSelecionada;

    private int idPacienteSelecionado;

    private double startTime = 0;
    private double finalTime = 0;

    private Handler myHandler = new Handler();
    private int forwardTime = 5000;
    private int backwardTime = 5000;

    private static int oneTimeOnly = 0;


    private Realm realm;
    private Gravacao gravacaoAtual;

    private Handler handler;
    private List<Integer> amplitudes;
    private VisualizerView visualizer;
    private int amp = 0;
    private boolean finished = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gravacao);

        handler = new Handler();
        amplitudes = new ArrayList<Integer>();

        //mVisualizerView = (VisualizerView2) findViewById(R.id.myvisualizerview);

        visualizer = (VisualizerView) findViewById(R.id.myvisualizerview);

        Intent intent = getIntent();
        nomePaciente = (String) intent.getSerializableExtra("nomePaciente");
        nomeGravacao = (String) intent.getSerializableExtra("nomeGravacao");
        emailPaciente = (String) intent.getSerializableExtra("emailPaciente");
        bpmGravacao2 = (String) intent.getSerializableExtra("bpmGravacao");
        idGravacaoSelecionada = (int) intent.getSerializableExtra("idGravacao");

        nomeGravacaoTextView = (TextView) findViewById(R.id.nomeGravacao);
        nomePacienteTextView = (TextView) findViewById(R.id.nomePaciente);
        emailPacienteTextView= (TextView) findViewById(R.id.emailPaciente);
        bpmGravacao = (TextView) findViewById(R.id.bpm);
        botaotAnotacao = (Button) findViewById(R.id.btAnotacao);
        botaoExcluir = (Button) findViewById(R.id.btExcluir);
        botaoCompartilhar = (Button) findViewById(R.id.btCompartilhar);
        textoAnotacao = (TextView) findViewById(R.id.txtAnotacao);

        playButton = (Button) findViewById(R.id.btPlay);
        stopButton = (Button) findViewById(R.id.btStop);
        stopButton.setEnabled(false);

        nomeGravacaoTextView.setText( nomeGravacao );
        nomePacienteTextView.setText( nomePaciente );
        emailPacienteTextView.setText( emailPaciente );
        bpmGravacao.setText( bpmGravacao2 );

        realm = Realm.getDefaultInstance();
        gravacaoAtual = new Gravacao();
        gravacaoAtual = realm.where(Gravacao.class).equalTo("idGravacao", idGravacaoSelecionada).findFirst(); // fazendo a consulta com o ID selecionado no REALM

        handler.post(updateVisualizer);

        if (gravacaoAtual.getAnotacao() == null)
            textoAnotacao.setText("Nada");
        else
            textoAnotacao.setText(gravacaoAtual.getAnotacao());

        tempoView = (TextView) findViewById(R.id.textView9);
        tempoView2 = (TextView) findViewById(R.id.textView10);

        String filePath = Environment.getExternalStorageDirectory().getPath() + "/" + nomeGravacao + ".wav";

        mediaPlayer = new MediaPlayer();
        mediaPlayer.reset(); // reconfigura MediaPlayer
        try {
            mediaPlayer.setDataSource(filePath);
            mediaPlayer.prepare();
        } catch (IOException e) {
            e.printStackTrace();
        }

        finalTime = mediaPlayer.getDuration();
        startTime = mediaPlayer.getCurrentPosition();

        if (oneTimeOnly == 0){
            oneTimeOnly = 1;
        }

        tempoView.setText(String.format("%d min, %d sec",
                TimeUnit.MILLISECONDS.toMinutes((long) finalTime),
                TimeUnit.MILLISECONDS.toSeconds((long) finalTime) -
                        TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes((long)
                                finalTime)))
        );

        tempoView2.setText(String.format("%d min, %d sec",
                TimeUnit.MILLISECONDS.toMinutes((long) startTime),
                TimeUnit.MILLISECONDS.toSeconds((long) startTime) -
                        TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes((long)
                                startTime)))
        );

        playButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try{

                    if (finished){
                        String filePath = Environment.getExternalStorageDirectory().getPath() + "/" + nomeGravacao + ".wav";

                        mediaPlayer = new MediaPlayer();
                        mediaPlayer.reset(); // reconfigura MediaPlayer
                        try {
                            mediaPlayer.setDataSource(filePath);
                            mediaPlayer.prepare();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }

                    mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                                public void onCompletion(MediaPlayer mediaPlayer) {
                                    visualizer.setEnabled(false);
                                    finished = true;
                                    Toast.makeText(getApplicationContext(), "FINISHED", Toast.LENGTH_SHORT).show();

                                    playButton.setEnabled(true);
                                    startTime = mediaPlayer.getDuration();
                                    tempoView2.setText("0 min, 0sec");

                                    mediaPlayer.reset();
                                }
                    });

                    Toast.makeText(getApplicationContext(), "Playing sound", Toast.LENGTH_SHORT).show();
                    mediaPlayer.start();

                    playButton.setEnabled(false);

                    finalTime = mediaPlayer.getDuration();
                    startTime = mediaPlayer.getCurrentPosition();

                    if (oneTimeOnly == 0){
                        oneTimeOnly = 1;
                    }

                    tempoView.setText(String.format("%d min, %d sec",
                            TimeUnit.MILLISECONDS.toMinutes((long) finalTime),
                            TimeUnit.MILLISECONDS.toSeconds((long) finalTime) -
                                    TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes((long)
                                            finalTime)))
                    );

                    tempoView2.setText(String.format("%d min, %d sec",
                            TimeUnit.MILLISECONDS.toMinutes((long) startTime),
                            TimeUnit.MILLISECONDS.toSeconds((long) startTime) -
                                    TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes((long)
                                            startTime)))
                    );

                    myHandler.postDelayed(UpdateSongTime, 100);
                }
                catch (Exception e){
                    Log.e(TAG, e.toString()); // registra exceções
                }

            }
        });

        stopButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mediaPlayer.pause();
                playButton.setEnabled(true);
            }
        });


        // Ação que será executada ao clicar na imagem de anotação
        botaotAnotacao.setOnClickListener(new View.OnClickListener() {
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

                        realm = Realm.getDefaultInstance();
                        realm.beginTransaction();

                        Gravacao gravacaoAtual = new Gravacao();
                        gravacaoAtual = realm.where(Gravacao.class).equalTo("idGravacao", idGravacaoSelecionada).findFirst();

                        gravacaoAtual.setAnotacao(nameEditText.getText().toString()); // Salvando a anotação no objeto

                        realm.copyToRealm(gravacaoAtual);

                        realm.commitTransaction();
                        realm.close();
                        textoAnotacao.setText(nameEditText.getText().toString());

                     }
                });

                inputDialog.setNegativeButton("Cancelar", null);
                inputDialog.show();
            }
        });

        botaoExcluir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                realm.beginTransaction();
                gravacaoAtual.deleteFromRealm();
                realm.commitTransaction();

                String filePath = Environment.getExternalStorageDirectory().getPath() + "/" + nomeGravacao + ".wav";
                File fileToDelete = new File(filePath);
                fileToDelete.delete();

                Toast.makeText(GravacaoActivity.this, "Gravação excluída com sucesso!", Toast.LENGTH_LONG).show();
                //Intent intent = new Intent(GravacaoActivity.this, SavedRecordings.class);
                //startActivity( intent );
                finish();
            }
        });

        botaoCompartilhar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(GravacaoActivity.this,EnviarEmailActivity.class);
                intent.putExtra("nomeAudio", nomeGravacao);
                intent.putExtra("textoAnotacao", textoAnotacao.getText().toString());
                intent.putExtra("emailPaciente", emailPaciente.toString());
                intent.putExtra("bpm", gravacaoAtual.getBpm());
                startActivity( intent );
            }
        });
    }

    Runnable updateVisualizer = new Runnable() {
        @Override
        public void run() {
            RealmList<RealmInt> itens = gravacaoAtual.getInts();
            for (RealmInt ap : itens) {
                amp = ap.getVal();

                visualizer.addAmplitude(amp);
                visualizer.invalidate();
                handler.postDelayed(this, 60);
            }
            handler.removeCallbacks(this); // Finalizando
        }
    };

    Runnable UpdateSongTime = new Runnable() {
        public void run() {
            startTime = mediaPlayer.getCurrentPosition();
            if (mediaPlayer.isPlaying()) {
                tempoView2.setText(String.format("%d min, %d sec",
                        TimeUnit.MILLISECONDS.toMinutes((long) startTime),
                        TimeUnit.MILLISECONDS.toSeconds((long) startTime) -
                                TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.
                                        toMinutes((long) startTime)))
                );
                stopButton.setEnabled(true);
            }
            else{
                startTime = 0;
                finalTime = 0;
                stopButton.setEnabled(false);
            }
            myHandler.postDelayed(this, 100);
        }
    };

    @Override
    protected void onPause() {
        super.onPause();
    }

}
