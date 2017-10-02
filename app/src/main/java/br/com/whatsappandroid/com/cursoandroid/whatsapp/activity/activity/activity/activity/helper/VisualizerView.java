package br.com.whatsappandroid.com.cursoandroid.whatsapp.activity.activity.activity.activity.helper;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Thyago on 27/07/2017.
 */

public class VisualizerView extends View {

    private static final int LINE_WIDTH = 3;
    private static final int LINE_SCALE = 30;

    private List<Float> amplitudes;
    private int width;
    private int height;
    private Paint linePaint;

    public VisualizerView(Context context, AttributeSet attrs)
    {
        super(context, attrs); // chama o construtor da superclasse
        linePaint = new Paint(); // cria Paint para linhas
        linePaint.setColor(Color.RED); // configura a cor como verde
        linePaint.setStrokeWidth(LINE_WIDTH); // configura a largura do traço
        linePaint.setStyle(Paint.Style.FILL);
        getRootView().setBackgroundColor(Color.LTGRAY);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        width = w;
        height = h;
        amplitudes = new ArrayList<Float>(width/ LINE_WIDTH);
    }

    public void clear(){
        amplitudes.clear();
    }

    public void addAmplitude(float amplitude){
        amplitudes.add(amplitude);
        if (amplitudes.size() * LINE_WIDTH >= width){
            amplitudes.remove(0);
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        int middle = height / 2; // obtém o centro da View
        float curX = 0; // inicia curX em zero

        // para cada item do ArrayList amplitudes
        for (float power : amplitudes)
        {
            float scaledHeight = power / LINE_SCALE; // muda a escala da linha
            curX += LINE_WIDTH; // soma LINE_WIDTH a X
            // desenha uma linha representando esse item do ArrayList amplitudes
            canvas.drawLine(curX, middle + scaledHeight / 2, curX,
                        middle - scaledHeight / 2, linePaint);

        } // fim do for
    }
}
