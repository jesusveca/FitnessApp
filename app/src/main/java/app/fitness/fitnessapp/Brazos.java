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
//            tvFlex.setText("x: "+event.values[0]+"  y: "+event.values[1]+"   z: "+event.values[2]);
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
        for (int i = 0; i < array.length; i++)
            contador += array[i];
        return contador;
    }
    public static float magnitud(float[] array) {
        float contador = 0;
        for (int i = 0; i < array.length; i++)
            contador += array[i] * array[i];
        return (float) Math.sqrt(contador);
    }
    public static float prodPunto(float[] A, float[] B) {
        return (float) A[0] * B[0] + A[1] * B[1] + A[2] * B[2];
    }
}

class DetectorBrazos {
    private static final int ACCEL_TAMANO = 50;
    private static final int VEL_TAMANO = 10;

    private static final float UMBRAL_PASOS = 15f;  // cambiar este umbral de acuerdo a la sensibilidad

    private static final int STEP_DELAY_NS = 250000000;

    private int accelContador = 0;
    private float[] accelX = new float[ACCEL_TAMANO];
    private float[] accelY = new float[ACCEL_TAMANO];
    private float[] accelZ = new float[ACCEL_TAMANO];
    private int velContador = 0;
    private float[] velMat = new float[VEL_TAMANO];
    private long ultimoTimeNs = 0;
    private float estimadoAnterior = 0;

    private BrazosListener listener;

    public void registerListener(BrazosListener listener) {
        this.listener = listener;
    }

    public void updateAccel(long timeNs, float x, float y, float z) {
        float[] AceleracionActual = new float[3];
        AceleracionActual[0] = x;
        AceleracionActual[1] = y;
        AceleracionActual[2] = z;

        accelContador++; // en las siguientes operaciones actualizaremos nuestra suposion de donde estar el vector z global
        accelX[accelContador % ACCEL_TAMANO] = AceleracionActual[0];
        accelY[accelContador % ACCEL_TAMANO] = AceleracionActual[1];
        accelZ[accelContador % ACCEL_TAMANO] = AceleracionActual[2];

        float[] worldZ = new float[3];
        worldZ[0] = PasosOperaciones.suma(accelX) / Math.min(accelContador, ACCEL_TAMANO);
        worldZ[1] = PasosOperaciones.suma(accelY) / Math.min(accelContador, ACCEL_TAMANO);
        worldZ[2] = PasosOperaciones.suma(accelZ) / Math.min(accelContador, ACCEL_TAMANO);

        float factorNormalizacion = PasosOperaciones.magnitud(worldZ);

        worldZ[0] = worldZ[0] / factorNormalizacion;
        worldZ[1] = worldZ[1] / factorNormalizacion;
        worldZ[2] = worldZ[2] / factorNormalizacion;

        float currentZ = PasosOperaciones.prodPunto(worldZ, AceleracionActual) - factorNormalizacion;
        velContador++;
        velMat[velContador % VEL_TAMANO] = currentZ;

        float estimadoActual = PasosOperaciones.suma(velMat);

        if (estimadoActual > UMBRAL_PASOS && estimadoAnterior <= UMBRAL_PASOS && (timeNs - ultimoTimeNs > STEP_DELAY_NS)) {
            listener.flexion(timeNs);
            ultimoTimeNs = timeNs;
        }
        estimadoAnterior = estimadoActual;
    }
}
