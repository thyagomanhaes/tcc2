package br.com.whatsappandroid.com.cursoandroid.whatsapp.activity.activity.activity.activity.model;

import java.io.File;
import java.util.Date;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by tikoextreme on 29/08/17.
 */

public class Gravacao extends RealmObject {

    @PrimaryKey
    private int idGravacao;

    private int idPaciente;

    private String nome;
    private Date dataGravacao;
    private float duracaoGravacao;
    private String anotacao;
    private String bpm;

    private RealmList<RealmInt> ints;

    public RealmList<RealmInt> getInts() {
        return ints;
    }

    public void setInts(RealmList<RealmInt> ints) {
        this.ints = ints;
    }

    //private File arquivoAudio;

    public int getIdGravacao() {
        return idGravacao;
    }

    public void setIdGravacao(int idGravacao) {
        this.idGravacao = idGravacao;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public Date getDataGravacao() {
        return dataGravacao;
    }

    public void setDataGravacao(Date dataGravacao) {
        this.dataGravacao = dataGravacao;
    }

    public float getDuracaoGravacao() {
        return duracaoGravacao;
    }

    public void setDuracaoGravacao(float duracaoGravacao) {
        this.duracaoGravacao = duracaoGravacao;
    }

    public String getAnotacao() {
        return anotacao;
    }

    public void setAnotacao(String anotacao) {
        this.anotacao = anotacao;
    }

    public String getBpm() {
        return bpm;
    }

    public void setBpm(String bpm) {
        this.bpm = bpm;
    }

    public int getIdPaciente() {
        return idPaciente;
    }

    public void setIdPaciente(int idPaciente) {
        this.idPaciente = idPaciente;
    }

    /*    public File getArquivoAudio() {
        return arquivoAudio;
    }

    public void setArquivoAudio(File arquivoAudio) {
        this.arquivoAudio = arquivoAudio;
    }*/
}
