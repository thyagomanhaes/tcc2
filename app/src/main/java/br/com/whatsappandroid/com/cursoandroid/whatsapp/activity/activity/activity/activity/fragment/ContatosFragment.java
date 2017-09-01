package br.com.whatsappandroid.com.cursoandroid.whatsapp.activity.activity.activity.activity.fragment;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import br.com.whatsappandroid.com.cursoandroid.whatsapp.R;
import br.com.whatsappandroid.com.cursoandroid.whatsapp.activity.activity.activity.activity.GravadorActivity;
import br.com.whatsappandroid.com.cursoandroid.whatsapp.activity.activity.activity.activity.MainActivity;
import br.com.whatsappandroid.com.cursoandroid.whatsapp.activity.activity.activity.activity.PacienteAdapter;
import br.com.whatsappandroid.com.cursoandroid.whatsapp.activity.activity.activity.activity.SavedRecordings;
import br.com.whatsappandroid.com.cursoandroid.whatsapp.activity.activity.activity.activity.config.ConfiguracaoFirebase;
import br.com.whatsappandroid.com.cursoandroid.whatsapp.activity.activity.activity.activity.helper.Preferencias;
import br.com.whatsappandroid.com.cursoandroid.whatsapp.activity.activity.activity.activity.model.Contato;
import br.com.whatsappandroid.com.cursoandroid.whatsapp.activity.activity.activity.activity.model.Gravacao;
import br.com.whatsappandroid.com.cursoandroid.whatsapp.activity.activity.activity.activity.model.Paciente;
import io.realm.Realm;

/**
 * A simple {@link Fragment} subclass.
 */
public class ContatosFragment extends Fragment {

    private ListView listView;
    private ArrayAdapter adapter;
    private ArrayList<String> contatos;
    private DatabaseReference firebase;
    private ValueEventListener valueEventListenerContatos;

    private Realm realm;
    private List<Paciente> listaPacientes;

    public ContatosFragment() {
        // Required empty public constructor
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onStart() {
        super.onStart();
        //firebase.addValueEventListener( valueEventListenerContatos );
    }

    @Override
    public void onStop() {
        super.onStop();
        //firebase.removeEventListener( valueEventListenerContatos );
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        realm = Realm.getDefaultInstance();

        listaPacientes = realm.where(Paciente.class).findAll();

        //listaPacientes.get(0).setNome("Thyago");

        //contatos = new ArrayList<>();
        //contatos.add(listaPacientes.get(0).getNome());
/*        contatos.add("Mariana");
        contatos.add("leticia");*/


        //Paciente paciente = new Paciente();
        //paciente.setId(1);
        //paciente.setNome("Thyago");
        //listaPacientes.add(paciente);
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_contatos, container, false);

        listView = (ListView) view.findViewById(R.id.lv_contatos);

/*        adapter = new ArrayAdapter(
                getActivity(),
                R.layout.lista_contato,
                contatos

        );*/

        PacienteAdapter adapter = new PacienteAdapter(getActivity(), listaPacientes);

        listView.setAdapter( adapter );

        adapter.notifyDataSetChanged();


        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(getActivity(), SavedRecordings.class);
                intent.putExtra("nome", listaPacientes.get(i).getNome());
                intent.putExtra("idPaciente", listaPacientes.get(i).getId());
                intent.putExtra("emailPaciente", listaPacientes.get(i).getEmail());

                startActivity( intent );
            }
        });

 /*       listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                switch ( position){
                    case 0:
                        Toast.makeText(getActivity(), "Escolher 0", Toast.LENGTH_LONG).show();
                        Intent intent = new Intent(getActivity(), SavedRecordings.class);
                        startActivity( intent );
                        getActivity().finish();
                        break;
                }
            }
        });

        Preferencias preferencias = new Preferencias(getActivity());
        String identificadorUsuarioLogado = preferencias.getIdentificador();
        firebase = ConfiguracaoFirebase.getFirebase().child("contatos").child(identificadorUsuarioLogado);

        //Listener para recuperar contatos
        valueEventListenerContatos = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                //Limpar lista
                contatos.clear();

                for (DataSnapshot dados: dataSnapshot.getChildren()){
                    Contato contato = dados.getValue( Contato.class );
                    contatos.add( contato.getNome() );

                }
                //adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };*/







        return view;
    }

}
