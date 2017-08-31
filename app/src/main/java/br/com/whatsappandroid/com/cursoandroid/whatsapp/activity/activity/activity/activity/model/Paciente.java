package br.com.whatsappandroid.com.cursoandroid.whatsapp.activity.activity.activity.activity.model;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by Thyago on 28/08/2017.
 */

public class Paciente extends RealmObject {

    @PrimaryKey
    private int id;

    private String nome;

    // Lista de Gravações que o paciente possui
    private RealmList<Gravacao> gravacoes;

    //private String email;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public RealmList<Gravacao> getGravacoes() {
        return gravacoes;
    }

    public void setGravacoes(RealmList<Gravacao> gravacoes) {
        this.gravacoes = gravacoes;
    }
}
