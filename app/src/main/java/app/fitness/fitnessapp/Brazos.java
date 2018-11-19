package app.fitness.fitnessapp;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class Brazos extends AppCompatActivity implements SensorEventListener, BrazosListener{
    private TextView textView;
    private DetectorBrazos simpleDetectorBrazos;
    private SensorManager sensorManager;
    private Sensor luz;
    private static final String TEXT_NUM_PLANCHAS = "Brazos: ";
    private int numFlexiones;

    private String valores;

    TextView tvFlex;
    Button BtnStart, BtnStop;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_brazos);

        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        assert sensorManager != null;
        luz = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
        simpleDetectorBrazos = new DetectorBrazos();
        simpleDetectorBrazos.registerListener(this);

        tvFlex = (TextView) findViewById(R.id.textLight2);
        BtnStart = (Button) findViewById(R.id.btn_start_planchas);
        BtnStop = (Button) findViewById(R.id.btn_stop_planchas);

        BtnStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                numFlexiones = 0;
                sensorManager.registerListener(Brazos.this, luz, SensorManager.SENSOR_DELAY_FASTEST);
            }
        });

        BtnStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                sensorManager.unregisterListener(Brazos.this);
                tvFlex.setText(TEXT_NUM_PLANCHAS+0);
            }
        });
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD) {
            simpleDetectorBrazos.updateAccel(event.timestamp, event.values[0], event.values[1], event.values[2]);
        }
    }

    @Override
    public void flexion(long timeNs) {
        tvFlex.setText(TEXT_NUM_PLANCHAS + numFlexiones);
        numFlexiones++;
    }
}


interface BrazosListener {
    public void flexion(long timeNs);
}

class OperacionesBrazos {
    private OperacionesBrazos() {
    }
    public static float suma(float[] array) {
        float contador = 0;
        for (float anArray : array) contador += anArray;
        return contador;
    }
}

class DetectorBrazos {
    private static final int MAT_TAMANO = 50;
    private static final int LUZ_TAMANO = 10;
    private int contador = 0;
    private static final float UMBRAL_FLEXIONES = 20f;  // cambiar este umbral de acuerdo a la sensibilidad
    private float[] MatX = new float[50];
    private float[] MatY = new float[50];
    private float[] MatZ = new float[50];

    private long ultimoTimeNs = 0;
    private float estimadoAnterior = 0;
    private static final int STEP_DELAY_NS = 250000000;

    private Brazos listener;

    public void registerListener(Brazos listener) {
        this.listener = listener;
    }

    public void updateAccel(long timeNs, float x, float y, float z) {
        float[] LuzActual = new float[3];
        LuzActual[0] = x;
        LuzActual[1] = y;
        LuzActual[2] = z;

        contador++;
        MatX[contador % MAT_TAMANO] = LuzActual[0];
        MatY[contador % MAT_TAMANO] = LuzActual[1];
        MatZ[contador % MAT_TAMANO] = LuzActual[2];

        System.out.println("Valor x: "+x+"  y: "+y+"    z: "+z);




//        float promedio;
//        promedio = OperacionesBrazos.suma(MatX) /  Math.min(contador, MAT_TAMANO);
//
//        float estimadoActual = promedio;
//
//        if (estimadoActual > UMBRAL_FLEXIONES && estimadoAnterior <= UMBRAL_FLEXIONES && (timeNs - ultimoTimeNs > STEP_DELAY_NS)) {
//            listener.flexion(timeNs);
//            ultimoTimeNs = timeNs;
//        }
//        estimadoAnterior = estimadoActual;
    }
}
