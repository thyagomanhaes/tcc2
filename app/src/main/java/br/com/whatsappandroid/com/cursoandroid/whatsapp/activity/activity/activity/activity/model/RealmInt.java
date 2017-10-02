package br.com.whatsappandroid.com.cursoandroid.whatsapp.activity.activity.activity.activity.model;

import io.realm.RealmObject;

/**
 * Created by Thyago on 30/09/2017.
 */

public class RealmInt extends RealmObject {
    private int val;

    public RealmInt(){

    }

    public RealmInt(int val){
        this.val = val;
    }

    public int getVal() {
        return val;
    }

    public void setVal(int val) {
        this.val = val;
    }
}
