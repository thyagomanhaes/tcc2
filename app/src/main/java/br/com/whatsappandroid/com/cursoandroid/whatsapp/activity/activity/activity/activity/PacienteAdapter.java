package br.com.whatsappandroid.com.cursoandroid.whatsapp.activity.activity.activity.activity;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.List;

import br.com.whatsappandroid.com.cursoandroid.whatsapp.R;
import br.com.whatsappandroid.com.cursoandroid.whatsapp.activity.activity.activity.activity.model.Paciente;

/**
 * Created by Thyago on 28/08/2017.
 */

public class PacienteAdapter extends ArrayAdapter<Paciente> {

    private final Context context;
    private final List<Paciente> elementos;

    public PacienteAdapter(Context context, List<Paciente> elementos){
        super (context, R.layout.linha, elementos);
        this.context = context;
        this.elementos = elementos;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View rowView = inflater.inflate(R.layout.linha, parent, false);

        TextView nome = (TextView) rowView.findViewById(R.id.txtNome);
        TextView idPaciente = (TextView) rowView.findViewById(R.id.txtEnd);

        nome.setText(elementos.get(position).getNome());
        idPaciente.setText(Integer.toString(elementos.get(position).getId()));

        return rowView;
    }
}
