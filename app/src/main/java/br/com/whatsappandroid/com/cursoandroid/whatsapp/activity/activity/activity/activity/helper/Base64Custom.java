package br.com.whatsappandroid.com.cursoandroid.whatsapp.activity.activity.activity.activity.helper;

import android.util.Base64;

/**
 * Created by Thyago on 24/07/2017.
 */

public class Base64Custom {

    public static String codificarBase64(String texto){
        return Base64.encodeToString( texto.getBytes(), Base64.DEFAULT ).replaceAll("(\\n|\\r)","");
    }
    public static String decodificarBase64(String textoCodificado){
        return new String (Base64.decode(textoCodificado, Base64.DEFAULT));
    }
}
