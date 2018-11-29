package app.fitness.fitnessapp;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class Planchas extends AppCompatActivity implements SensorEventListener, FlexionesListener{
    private TextView textView;
    private DetectorFlexiones simpleDetectorFlexiones;
    private SensorManager sensorManager;
    private Sensor luz;
    private static final String TEXT_NUM_PLANCHAS = "Flexiones: ";
    private int numFlexiones;

    TextView tvFlex;
    Button BtnStart, BtnStop;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_planchas);
        setTitle("Contador de Flexiones");

        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        assert sensorManager != null;
        luz = sensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY);
        simpleDetectorFlexiones = new DetectorFlexiones();
        simpleDetectorFlexiones.registerListener(this);

        tvFlex = (TextView) findViewById(R.id.textLight2);
        BtnStart = (Button) findViewById(R.id.btn_start_planchas);
        BtnStop = (Button) findViewById(R.id.btn_stop_planchas);

        BtnStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                numFlexiones = 0;
                sensorManager.registerListener(Planchas.this, luz, SensorManager.SENSOR_DELAY_FASTEST);
            }
        });

        BtnStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                sensorManager.unregisterListener(Planchas.this);
                tvFlex.setText(TEXT_NUM_PLANCHAS+0);
            }
        });
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }
    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_PROXIMITY) {
            simpleDetectorFlexiones.updateAccel(event.timestamp, event.values[0]);
            //System.out.println(event.values[0]);
        }
    }
    @Override
    public void flexion(long timeNs) {
        tvFlex.setText(TEXT_NUM_PLANCHAS + numFlexiones);
        numFlexiones++;
    }
}

interface FlexionesListener {
    public void flexion(long timeNs);
}

class Operaciones {
    private Operaciones() {
    }
    public static float suma(float[] array) {
        float contador = 0;
        for (float anArray : array) contador += anArray;
        return contador;
    }
}

class DetectorFlexiones {
    private static final int MAT_TAMANO = 50;
    private static final int LUZ_TAMANO = 10;
    private int contador = 0;
    private static final float UMBRAL_FLEXIONES = 2.6f;  // cambiar este umbral de acuerdo a la sensibilidad
    private float[] MatX = new float[50];
    private long ultimoTimeNs = 0;
    private float estimadoAnterior = 0;
    private static final int STEP_DELAY_NS = 250000000;

    private Planchas listener;

    public void registerListener(Planchas listener) {
        this.listener = listener;
    }

    public void updateAccel(long timeNs, float x) {
        float[] LuzActual = new float[1];
        LuzActual[0] = x;

        contador++;
        MatX[contador % MAT_TAMANO] = LuzActual[0];

        float promedio;
        promedio = Operaciones.suma(MatX) /  Math.min(contador, MAT_TAMANO);

        float estimadoActual = promedio;
        System.out.println(estimadoActual);

        if (estimadoActual > UMBRAL_FLEXIONES && estimadoAnterior <= UMBRAL_FLEXIONES && (timeNs - ultimoTimeNs > STEP_DELAY_NS)) {
            listener.flexion(timeNs);
            ultimoTimeNs = timeNs;
        }
        estimadoAnterior = estimadoActual;
    }
}