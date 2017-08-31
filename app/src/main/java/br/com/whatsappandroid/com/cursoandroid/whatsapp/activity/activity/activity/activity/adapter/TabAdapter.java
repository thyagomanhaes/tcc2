package br.com.whatsappandroid.com.cursoandroid.whatsapp.activity.activity.activity.activity.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import br.com.whatsappandroid.com.cursoandroid.whatsapp.activity.activity.activity.activity.fragment.ContatosFragment;
import br.com.whatsappandroid.com.cursoandroid.whatsapp.activity.activity.activity.activity.fragment.ConversasFragment;

/**
 * Created by Thyago on 23/07/2017.
 */

public class TabAdapter extends FragmentStatePagerAdapter{

    private String[] tituloAbas = {"AO VIVO", "PACIENTES"};

    public TabAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        Fragment fragment = null;

        switch ( position ){
            case 0:
                fragment = new ConversasFragment();
                break;
            case 1:
                fragment = new ContatosFragment();
                break;

        }
        return fragment;
    }

    @Override
    public int getCount() {
        return tituloAbas.length;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return tituloAbas[ position ];
    }
}
