package br.com.whatsappandroid.com.cursoandroid.whatsapp.activity.activity.activity.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;

import br.com.whatsappandroid.com.cursoandroid.whatsapp.R;

public class EnviarEmailActivity extends AppCompatActivity {

    private CheckBox checkAudio;
    private CheckBox checkBPM;
    private CheckBox checkAnotacao;

    private TextView nomeAudioTxT;
    private TextView anotacaoTxt;
    private Button botaoEnviar;


    private String nomeAudio;
    private String anotacao;
    private String emailPaciente;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_enviar_email);

        checkAudio = (CheckBox) findViewById(R.id.checkBoxAudio);
        checkBPM = (CheckBox) findViewById(R.id.checkBoxBPM);
        checkAnotacao = (CheckBox) findViewById(R.id.checkBoxAnotacao);

        nomeAudioTxT = (TextView) findViewById(R.id.txtNomeAudio);
        anotacaoTxt = (TextView) findViewById(R.id.txtAnotacao);
        botaoEnviar = (Button) findViewById(R.id.btEnviarEmail);

        Intent intent = getIntent();

        nomeAudio = (String) intent.getSerializableExtra("nomeAudio");
        anotacao = (String) intent.getSerializableExtra("textoAnotacao");
        emailPaciente = (String) intent.getSerializableExtra("textoAnotacao");

        nomeAudioTxT.setText(nomeAudio);
        anotacaoTxt.setText(anotacao);

        botaoEnviar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (checkAudio.isChecked()){
                    File newFile = new File (Environment.getExternalStorageDirectory().getPath() + "/" + nomeAudio + ".wav");
                    //Uri uri = Uri.parse(filePath);
                    String[] TO = {emailPaciente};
                    Intent share = new Intent(Intent.ACTION_SEND);
                    share.setType("audio/*");
                    //share.setData(Uri.parse("mailto:"));
                    share.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(newFile));
                    share.putExtra(Intent.EXTRA_SUBJECT, "Título?");
                    share.putExtra(Intent.EXTRA_TEXT, "Diagnóstico Médico: " + anotacao);
                    share.putExtra(Intent.EXTRA_EMAIL, TO);
                    startActivity( Intent.createChooser(share, "Share Sound File"));
                }
                else {
                    Toast.makeText(EnviarEmailActivity.this, "POr favor, selecione uma opção", Toast.LENGTH_SHORT).show();
                }

            }
        });


    }
}
