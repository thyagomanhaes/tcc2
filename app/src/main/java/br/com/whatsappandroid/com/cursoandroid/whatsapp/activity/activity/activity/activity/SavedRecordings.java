package br.com.whatsappandroid.com.cursoandroid.whatsapp.activity.activity.activity.activity;

import android.app.ListActivity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Layout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.ToggleButton;

import org.w3c.dom.Text;

import java.io.File;
import java.lang.reflect.Array;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import br.com.whatsappandroid.com.cursoandroid.whatsapp.R;

/*import static br.com.whatsappandroid.com.cursoandroid.whatsapp.R.id.progressSeekBar;
import static br.com.whatsappandroid.com.cursoandroid.whatsapp.R.id.view;*/

public class SavedRecordings extends ListActivity {

    private static final String TAG = SavedRecordings.class.getName();

    private SavedRecordingsAdapter savedRecordingsAdapter;
    private MediaPlayer mediaPlayer;
    private SeekBar progressSeekbar;
    private Handler handler;
    private TextView nowPlayingTextView;
    private ToggleButton playPauseButton; // Controla a reprodução de áudio
    private Button botaoNovaGravacao;

    private String nomePaciente;
    private int idPacienteSelecionado;
    private String emailPaciente;

    private TextView nomePacienteView;
    private TextView idPacienteView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.saved_recordings);

        Intent intent = getIntent();
        nomePaciente = (String) intent.getSerializableExtra("nome");
        idPacienteSelecionado = (int) intent.getSerializableExtra("idPaciente");
        emailPaciente = (String) intent.getSerializableExtra("emailPaciente");

        nomePacienteView = (TextView) findViewById(R.id.nomePaciente);
        idPacienteView = (TextView) findViewById(R.id.idPaciente);

        nomePacienteView.setText( "Nome: " + nomePaciente );
        idPacienteView.setText( "ID: " + String.valueOf(idPacienteSelecionado) );

        ListView listView = getListView();
        savedRecordingsAdapter = new SavedRecordingsAdapter(this, new ArrayList<String>(Arrays.asList(getExternalFilesDir(null).list())));
        listView.setAdapter( savedRecordingsAdapter );

        // Botão para Iniciar uma nova Gravação
        botaoNovaGravacao = (Button) findViewById(R.id.btNovaGravacao);

        botaoNovaGravacao.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SavedRecordings.this, VoiceRecorderActivity.class);
                startActivity( intent );
            }
        });



/*        handler = new Handler();

        progressSeekbar = (SeekBar) findViewById(progressSeekBar);
        progressSeekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });*/

 /*       playPauseButton = (ToggleButton) findViewById(R.id.playPauseButton);
        playPauseButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

            }
        });*/

        /*nowPlayingTextView = (TextView) findViewById(R.id.nowPlayingTextView);*/

    }


    // cria o objeto MediaPlayer
    @Override
    protected void onResume()
    {
        super.onResume();
        mediaPlayer = new MediaPlayer(); // reproduz as gravações
    } // fim do método onResume


    // libera o objeto MediaPlayer
    @Override
    protected void onPause()
    {
        super.onPause();
        if (mediaPlayer != null)
        {
            //handler.removeCallbacks(updater); // para de atualizar a // interface gráfica
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer = null;
        } // fim do if

    }

/*    // atualiza a SeekBar a cada segundo
    Runnable updater = new Runnable()
    {
        @Override
        public void run()
        {
            if (mediaPlayer.isPlaying())
            {
                // atualiza a posição da SeekBar
                progressSeekbar.setProgress(mediaPlayer.getCurrentPosition());
                handler.postDelayed(this, 100);
            } // fim do if

        } // fim do método run

    }; // fim de Runnable*/

    private static class ViewHolder {
        TextView nameTextView;
        TextView nameTextView2;

        ImageView emailButton;
        ImageView deleteButton;
    }

    private class SavedRecordingsAdapter extends ArrayAdapter<String>{
        private List<String> items;
        private LayoutInflater inflater;

        public SavedRecordingsAdapter(Context context, List<String> items){
            super(context, -1, items);
            Collections.sort(items, String.CASE_INSENSITIVE_ORDER);
            this.items = items;
            inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }


        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            ViewHolder viewHolder;

            if (convertView == null){
                convertView = inflater.inflate(R.layout.saved_recordings_row, null);

                viewHolder = new ViewHolder();

                viewHolder.nameTextView = (TextView) convertView.findViewById(R.id.nome);
                viewHolder.nameTextView2 = (TextView) convertView.findViewById(R.id.endereco);
                viewHolder.emailButton =  (ImageView) convertView.findViewById(R.id.seta);


//                viewHolder.deleteButton = (ImageView) convertView.findViewById(R.id.deleteButton);*/
                convertView.setTag(viewHolder); // armazena como tag da View
            }
            else
                viewHolder = (ViewHolder) convertView.getTag();

            String item = items.get(position);
            viewHolder.nameTextView.setText(item);

            //viewHolder.nomePacienteView.setText(nomePaciente);

            SimpleDateFormat sdff = new SimpleDateFormat("dd/MM/yyyy");
            String dia = (sdff.format(new Date()));


            viewHolder.nameTextView2.setText(dia);

            viewHolder.emailButton.setTag(item); // falta as ações
            /*viewHolder.deleteButton.setTag(item);
            viewHolder.deleteButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(final View v) {
                    AlertDialog.Builder confirmDialog = new AlertDialog.Builder(SavedRecordings.this);
                    confirmDialog.setTitle("Deseja mesmo apagar?");
                    confirmDialog.setMessage("Hein mofio?");
                    confirmDialog.setPositiveButton("Apagar", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            File fileToDelete = new File(getExternalFilesDir(null) + File.separator + (String) v.getTag());
                            fileToDelete.delete();
                            savedRecordingsAdapter.remove((String) v.getTag());
                        }
                    });
                    confirmDialog.setNegativeButton("Cancelar", null);
                    confirmDialog.show();
                }
            });

            viewHolder.emailButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Uri data = Uri.fromFile( new File(getExternalFilesDir(null), (String) v.getTag()));

                    Intent myIntent = new Intent(Intent.ACTION_SEND);
                    myIntent.setType("audio/mp3");
                    //String shareBody = "Beleza";
                    myIntent.putExtra(Intent.EXTRA_STREAM, data);
                    //myIntent.putExtra(Intent.EXTRA_TEXT,"suave");
                    startActivity(Intent.createChooser(myIntent, "Share Using"));
                }
            });*/

            return convertView;
        }
    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
        //playPauseButton.setChecked(true);
        //handler.removeCallbacks( updater );

        TextView nameTextView = ((TextView) v.findViewById(R.id.nome));
        String name = nameTextView.getText().toString();
        String filePath = getExternalFilesDir(null).getAbsolutePath() + File.separator + name;

        //nowPlayingTextView.setText( name );
        Intent intent = new Intent(SavedRecordings.this, GravacaoActivity.class);
        intent.putExtra("nomeGravacao", name);
        intent.putExtra("nomePaciente", nomePaciente);
        intent.putExtra("emailPaciente", emailPaciente);
        //intent.putExtra("idPaciente", idPacienteSelecionado);
        startActivity( intent );


/*        try
        {
            // configura MediaPlayer para reproduzir o arquivo em filePath
            mediaPlayer.reset(); // reconfigura MediaPlayer
            mediaPlayer.setDataSource(filePath);
            mediaPlayer.prepare(); // prepara MediaPlayer


            mediaPlayer.start();

            //updater.run(); // começa a atualizar progressSeekBar

        } // fim do try

        catch (Exception e)

        {

            Log.e(TAG, e.toString()); // registra exceções

        } // fim do catch*/

    }
}
