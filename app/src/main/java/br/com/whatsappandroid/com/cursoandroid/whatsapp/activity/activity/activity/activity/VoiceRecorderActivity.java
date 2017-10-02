package br.com.whatsappandroid.com.cursoandroid.whatsapp.activity.activity.activity.activity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioRecord;
import android.media.AudioTrack;
import android.media.MediaRecorder;
import android.os.Environment;
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
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import br.com.whatsappandroid.com.cursoandroid.whatsapp.R;
import br.com.whatsappandroid.com.cursoandroid.whatsapp.activity.activity.activity.activity.helper.VisualizerView;
import br.com.whatsappandroid.com.cursoandroid.whatsapp.activity.activity.activity.activity.model.Gravacao;
import br.com.whatsappandroid.com.cursoandroid.whatsapp.activity.activity.activity.activity.model.RealmInt;
import io.realm.Realm;
import io.realm.RealmList;


public class VoiceRecorderActivity extends AppCompatActivity {

    private static final String TAG = VoiceRecorder.class.getName();
    private MediaRecorder recorder; // usado para gravar áudio
    private Handler handler; // Handler para atualizar o visualizador
    private boolean recording; // estamos gravando?

    // Variáveis da interface gráfica do usuário
    private VisualizerView visualizer;
    private Button botaoGravar;
    private Button botaoSalvar;

    private Chronometer ch;

    private List<Integer> amplitudes;
    private TextView qtdAmplitudes;
    private int total = 0;

    private int idPacienteSelecionado;
    private String padraoArquivo;

    //============= VARIÁVEIS MANIPULAÇÃO ÁUDIO ===================================//
    private static final String AUDIO_RECORDER_FOLDER = "AudioRecorder2";
    private static final String AUDIO_RECORDER_TEMP_FILE = "record_temp.raw";
    private static final String AUDIO_RECORDER_FILE_EXT_WAV = ".wav";
    private static final int AUDIO_SOURCE = MediaRecorder.AudioSource.MIC;
    private static final int SAMPLE_RATE = 44100; // Taxa de Amostragem em Hz
    private static final int ENCODING = AudioFormat.ENCODING_PCM_16BIT; // Codificação PC de 16 bits
    private static final int CHANNEL_MASK = AudioFormat.CHANNEL_IN_MONO; // Configuração Canal MONO
    private static final int BUFFER_SIZE = 2 * AudioRecord.getMinBufferSize(SAMPLE_RATE, CHANNEL_MASK, ENCODING);
    byte[] buffer = new byte[BUFFER_SIZE]; // buffer do áudio amostrado

    private AudioRecord audioRecord = null; // Objeto que inicia a gravação e faz amostragem do sinal
    private AudioTrack  audioTrack  = null; // Objeto para reproduzir a gravação em tempo real

    private Thread recordingThread = null;  // Thread responsável por pegar as amostras de áudio, reproduzir em tempo real e enviar para o arquivo

    File wavFile = null; // arquivo onde a gravação será salva em formato .wav

    private int read = 0;

    FileOutputStream wavOut = null;

    private boolean isRecording; // flag para indicar se estamos gravando ou não

    private int amplitudeCalculada = 0;
    private int totalBPM = 0;
    private String bpmCalculada;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        Intent intent = getIntent();
        idPacienteSelecionado = (int) intent.getSerializableExtra("idPacienteSelecionado");

        visualizer = (VisualizerView) findViewById(R.id.visualizerView2);
        botaoGravar = (Button) findViewById(R.id.btGravar);
        botaoSalvar = (Button) findViewById(R.id.btSalvar);

        ch = (Chronometer) findViewById(R.id.chronometer);
        //qtdAmplitudes = (TextView) findViewById(R.id.amplitudes2);

        handler = new Handler();
        amplitudes = new ArrayList<Integer>();

        // Ações para executar quando clicar no botão GRAVAR
        botaoGravar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isRecording = true;
                ch.setBase(SystemClock.elapsedRealtime());
                ch.start();

                ch
                        .setOnChronometerTickListener(new Chronometer.OnChronometerTickListener() {

                            @Override
                            public void onChronometerTick(Chronometer chronometer) {
                                if( chronometer.getText().toString().equalsIgnoreCase("00:20")) {
                                    if (null != audioRecord) {
                                        isRecording = false; // parou de gravar


                                        int i = audioRecord.getState();
                                        if (i == 1)
                                            audioRecord.stop();

                                        audioRecord.release();
                                        audioTrack.stop();
                                        audioTrack.release();
                                        audioRecord = null;
                                        audioTrack = null;
                                        recordingThread = null;
                                    }
                                    if (wavOut != null) {
                                        try {
                                            wavOut.close();
                                        } catch (IOException ex) {
                                            //
                                        }
                                    }

                                    try {
                                        // This is not put in the try/catch/finally above since it needs to run
                                        // after we close the FileOutputStream
                                        updateWavHeader(wavFile);
                                    } catch (IOException ex) {
                                        ex.printStackTrace();
                                    }

                                    // Armazenar o nome da gravação no banco de dados com o padrão: IDPACIENTE_dataHora
                                    Realm realm = Realm.getDefaultInstance();
                                    realm.beginTransaction();
                                    Number maxId = realm.where(Gravacao.class).max("idGravacao");

                                    int nextId = (maxId == null) ? 1 : maxId.intValue() + 1;

                                    Gravacao gravacao = new Gravacao();
                                    gravacao.setIdGravacao(nextId);
                                    gravacao.setNome(padraoArquivo);
                                    gravacao.setIdPaciente(idPacienteSelecionado);
                                    gravacao.setBpm(bpmCalculada);

                                    realm.copyToRealm(gravacao);

                                    realm.commitTransaction();
                                    realm.close();

                                    Toast.makeText(VoiceRecorderActivity.this, "Gravação salva com sucesso! " + String.valueOf(amplitudes.size()), Toast.LENGTH_LONG).show();


                                    Intent intent = new Intent(VoiceRecorderActivity.this, MainActivity.class);
                                    startActivity( intent );

                                    Toast.makeText(VoiceRecorderActivity.this,
                                            "time reached", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                Toast.makeText(VoiceRecorderActivity.this, "Iniciando Gravação", Toast.LENGTH_LONG).show();
                doInbackground();
            }
        });

        // Ações do botão SALVAR
        botaoSalvar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                // stops the recording activity
                if (null != audioRecord) {
                    isRecording = false; // parou de gravar


                    int i = audioRecord.getState();
                    if (i == 1)
                        audioRecord.stop();

                    audioRecord.release();
                    audioTrack.stop();
                    audioTrack.release();
                    audioRecord = null;
                    audioTrack = null;
                    recordingThread = null;
                }
                if (wavOut != null) {
                    try {
                        wavOut.close();
                    } catch (IOException ex) {
                        //
                    }
                }

                try {
                    // This is not put in the try/catch/finally above since it needs to run
                    // after we close the FileOutputStream
                    updateWavHeader(wavFile);
                } catch (IOException ex) {
                    ex.printStackTrace();
                }

                // Armazenar o nome da gravação no banco de dados com o padrão: IDPACIENTE_dataHora
                Realm realm = Realm.getDefaultInstance();
                realm.beginTransaction();
                Number maxId = realm.where(Gravacao.class).max("idGravacao");

                int nextId = (maxId == null) ? 1 : maxId.intValue() + 1;

                Gravacao gravacao = new Gravacao();
                gravacao.setIdGravacao(nextId);
                gravacao.setNome(padraoArquivo);
                gravacao.setIdPaciente(idPacienteSelecionado);
                gravacao.setBpm(bpmCalculada);

                RealmList<RealmInt> list = new RealmList<RealmInt>();

                for (Integer a : amplitudes){
                    list.add(new RealmInt(a));
                }

                gravacao.setInts(list);

                realm.copyToRealm(gravacao);

                realm.commitTransaction();
                realm.close();



                Toast.makeText(VoiceRecorderActivity.this, "Gravação salva com sucesso! " + String.valueOf(amplitudes.size()), Toast.LENGTH_LONG).show();


                Intent intent = new Intent(VoiceRecorderActivity.this, MainActivity.class);
                startActivity( intent );


/*                LayoutInflater inflater = (LayoutInflater) getSystemService(
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
                            gravacao.setIdPaciente(idPacienteSelecionado);
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
                //   inputDialog.setPositiveButton()*/
            }
        });

    }

    Runnable updateVisualizer = new Runnable() {
        @Override
        public void run() {
            if (isRecording){
                //int x = recorder.getMaxAmplitude();
                //Log.d("DECIBEL", String.valueOf(amplitudeCalculada));
                amplitudes.add(amplitudeCalculada);
                visualizer.addAmplitude(amplitudeCalculada);
                visualizer.invalidate();
                handler.postDelayed(this, 60);

            }
        }
    };

    private void doInbackground(){
        String data = "dd-MM-yyyy";
        String hora = "HH:mm";
        String data1, hora1;
        java.util.Date agora = new java.util.Date();;
        SimpleDateFormat formata = new SimpleDateFormat(data);
        data1 = formata.format(agora);
        formata = new SimpleDateFormat(hora);
        hora1 = formata.format(agora);
        String dataHora = data1 + " - " + hora1;

        padraoArquivo = String.valueOf(idPacienteSelecionado) + "_" + dataHora; // Exemplo: 200_08/09/2017 - 18:43

        String filepath = Environment.getExternalStorageDirectory().getPath() + "/" + padraoArquivo + ".wav";
        wavFile = new File(filepath);

        try {
            // Open our two resources
            audioRecord = new AudioRecord(AUDIO_SOURCE, SAMPLE_RATE, CHANNEL_MASK, ENCODING, BUFFER_SIZE);
            audioTrack = new AudioTrack(AudioManager.STREAM_MUSIC, SAMPLE_RATE, AudioFormat.CHANNEL_OUT_MONO, ENCODING, BUFFER_SIZE, AudioTrack.MODE_STREAM);
            wavOut = new FileOutputStream(wavFile);

            // Write out the wav file header
            writeWavHeader(wavOut, CHANNEL_MASK, SAMPLE_RATE, ENCODING);

            // Avoiding loop allocations
            buffer = new byte[BUFFER_SIZE];

            // Let's Go
            audioRecord.startRecording();
            handler.post(updateVisualizer);

            audioTrack.setPlaybackRate(SAMPLE_RATE);
            audioTrack.play();

            Log.d("BUFFER SIZE = ",String.valueOf(BUFFER_SIZE));

            recordingThread = new Thread(new Runnable() {
                @Override
                public void run() {
                    int cAmplitude = 0;
                    while (isRecording){ // enquando estiver gravando
                        read = audioRecord.read(buffer, 0, buffer.length);

                        for (int i=0; i<read/2; i++) {
                            short curSample = getShort(buffer[i*2], buffer[i*2+1]);
                            if (curSample > cAmplitude) {
                                cAmplitude = curSample;
                            }
                        }
                        amplitudeCalculada = cAmplitude;

                        Random random = new Random();

                        totalBPM = 60 + random.nextInt(40); // Gera números aleatórios com limite 50 e minimo 10.

                        bpmCalculada = String.valueOf(totalBPM);

                        Log.d("amplitude",Integer.toString(cAmplitude));
                        cAmplitude = 0;

                        if (AudioRecord.ERROR_INVALID_OPERATION != read) {
                            try {
                                wavOut.write(buffer);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                        audioTrack.write(buffer, 0, buffer.length);
                    }
                }
            });
            recordingThread.start();



        } catch (IOException e){
            e.printStackTrace();
        }


    }

    private short getShort(byte argB1, byte argB2) {
        return (short)(argB1 | (argB2 << 8));
    }

    private int calculateDecibel(byte[] buf) {
        int sum = 0;
        double sDataMax = 0;
        for (int i = 0; i < buffer.length; i++) {
            if(Math.abs(buffer[i])>=sDataMax){
                sDataMax=Math.abs(buffer[i]);
                sum += sDataMax;
            };
        }
        // avg 10-50
        return sum;
        // buffer.length;
    }

    private static void updateWavHeader(File wav) throws IOException {
        byte[] sizes = ByteBuffer
                .allocate(8)
                .order(ByteOrder.LITTLE_ENDIAN)
                // There are probably a bunch of different/better ways to calculate
                // these two given your circumstances. Cast should be safe since if the WAV is
                // > 4 GB we've already made a terrible mistake.
                .putInt((int) (wav.length() - 8)) // ChunkSize
                .putInt((int) (wav.length() - 44)) // Subchunk2Size
                .array();

        RandomAccessFile accessWave = null;
        //noinspection CaughtExceptionImmediatelyRethrown
        try {
            accessWave = new RandomAccessFile(wav, "rw");
            // ChunkSize
            accessWave.seek(4);
            accessWave.write(sizes, 0, 4);

            // Subchunk2Size
            accessWave.seek(40);
            accessWave.write(sizes, 4, 4);
        } catch (IOException ex) {
            // Rethrow but we still close accessWave in our finally
            throw ex;
        } finally {
            if (accessWave != null) {
                try {
                    accessWave.close();
                } catch (IOException ex) {
                    //
                }
            }
        }
    }

    private static void writeWavHeader(OutputStream out, int channelMask, int sampleRate, int encoding) throws IOException {
        short channels;
        switch (channelMask) {
            case AudioFormat.CHANNEL_IN_MONO:
                channels = 1;
                break;
            case AudioFormat.CHANNEL_IN_STEREO:
                channels = 2;
                break;
            default:
                throw new IllegalArgumentException("Unacceptable channel mask");
        }

        short bitDepth;
        switch (encoding) {
            case AudioFormat.ENCODING_PCM_8BIT:
                bitDepth = 8;
                break;
            case AudioFormat.ENCODING_PCM_16BIT:
                bitDepth = 16;
                break;
            case AudioFormat.ENCODING_PCM_FLOAT:
                bitDepth = 32;
                break;
            default:
                throw new IllegalArgumentException("Unacceptable encoding");
        }

        writeWavHeader(out, channels, sampleRate, bitDepth);
    }

    private static void writeWavHeader(OutputStream out, short channels, int sampleRate, short bitDepth) throws IOException {
        // Convert the multi-byte integers to raw bytes in little endian format as required by the spec
        byte[] littleBytes = ByteBuffer
                .allocate(14)
                .order(ByteOrder.LITTLE_ENDIAN)
                .putShort(channels)
                .putInt(sampleRate)
                .putInt(sampleRate * channels * (bitDepth / 8))
                .putShort((short) (channels * (bitDepth / 8)))
                .putShort(bitDepth)
                .array();

        // Not necessarily the best, but it's very easy to visualize this way
        out.write(new byte[]{
                // RIFF header
                'R', 'I', 'F', 'F', // ChunkID
                0, 0, 0, 0, // ChunkSize (must be updated later)
                'W', 'A', 'V', 'E', // Format
                // fmt subchunk
                'f', 'm', 't', ' ', // Subchunk1ID
                16, 0, 0, 0, // Subchunk1Size
                1, 0, // AudioFormat
                littleBytes[0], littleBytes[1], // NumChannels
                littleBytes[2], littleBytes[3], littleBytes[4], littleBytes[5], // SampleRate
                littleBytes[6], littleBytes[7], littleBytes[8], littleBytes[9], // ByteRate
                littleBytes[10], littleBytes[11], // BlockAlign
                littleBytes[12], littleBytes[13], // BitsPerSample
                // data subchunk
                'd', 'a', 't', 'a', // Subchunk2ID
                0, 0, 0, 0, // Subchunk2Size (must be updated later)
        });
    }

    /*CompoundButton.OnCheckedChangeListener recordButtonListener = new CompoundButton.OnCheckedChangeListener(){
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
    };*/
}
