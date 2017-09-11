package br.com.whatsappandroid.com.cursoandroid.whatsapp.activity.activity.activity.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.CheckBox;

import br.com.whatsappandroid.com.cursoandroid.whatsapp.R;

public class EnviarEmailActivity extends AppCompatActivity {

    private CheckBox checkAudio;
    private CheckBox checkBPM;
    private CheckBox checkAnotacao;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_enviar_email);

        checkAudio = (CheckBox) findViewById(R.id.checkBoxAudio);
        checkBPM = (CheckBox) findViewById(R.id.checkBoxBPM);
        checkAnotacao = (CheckBox) findViewById(R.id.checkBoxAnotacao);

    }
}
