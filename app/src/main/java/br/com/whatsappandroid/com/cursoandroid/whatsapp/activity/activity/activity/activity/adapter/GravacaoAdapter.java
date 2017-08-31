package br.com.whatsappandroid.com.cursoandroid.whatsapp.activity.activity.activity.activity.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

import br.com.whatsappandroid.com.cursoandroid.whatsapp.R;
import br.com.whatsappandroid.com.cursoandroid.whatsapp.activity.activity.activity.activity.model.Gravacao;
import br.com.whatsappandroid.com.cursoandroid.whatsapp.activity.activity.activity.activity.model.Paciente;

/**
 * Created by tikoextreme on 29/08/17.
 */

public class GravacaoAdapter extends ArrayAdapter<Gravacao> {

    private final Context context;
    private final List<Gravacao> elementos;

    public GravacaoAdapter(Context context, List<Gravacao> elementos){
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

        return rowView;
    }
}
