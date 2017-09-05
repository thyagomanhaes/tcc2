package br.com.whatsappandroid.com.cursoandroid.whatsapp.activity.activity.activity.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import br.com.whatsappandroid.com.cursoandroid.whatsapp.R;
import br.com.whatsappandroid.com.cursoandroid.whatsapp.activity.activity.activity.activity.fragment.ContatosFragment;
import br.com.whatsappandroid.com.cursoandroid.whatsapp.activity.activity.activity.activity.model.Contato;
import br.com.whatsappandroid.com.cursoandroid.whatsapp.activity.activity.activity.activity.model.Paciente;
import io.realm.Realm;

public class CadastroPaciente extends AppCompatActivity {

    private Button btSalvar;
    private Button btCancelar;
    private EditText id;
    private EditText nome;
    private EditText email;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cadastro_paciente);


        btSalvar = (Button) findViewById(R.id.btSalvar);
        btCancelar = (Button) findViewById(R.id.btCancelar);

        id = (EditText) findViewById(R.id.textID);
        nome = (EditText) findViewById(R.id.textNome);
        email = (EditText) findViewById(R.id.editEmail);

        btSalvar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Realm realm = Realm.getDefaultInstance();// ... Utilização da base...realm.close();
                realm.beginTransaction();

        Paciente paciente = new Paciente();
        paciente.setNome(nome.getText().toString());
        paciente.setId(Integer.parseInt(id.getText().toString()));
        paciente.setEmail(email.getText().toString());

        realm.copyToRealm(paciente);

        Toast.makeText(CadastroPaciente.this, "Paciente: " + paciente.getNome() + " cadastrado com sucesso!", Toast.LENGTH_LONG).show();

        realm.commitTransaction();
        realm.close();

        Intent intent = new Intent(CadastroPaciente.this, MainActivity.class);
        startActivity( intent );
        finish();

            }
        });



    }
}
