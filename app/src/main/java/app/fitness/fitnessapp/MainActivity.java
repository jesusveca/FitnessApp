package app.fitness.fitnessapp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {
    Button btnPasos, btnBiceps, btnPlanchas;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
//        setTitle("Fitness Tracker Usando Sensores");

        btnPasos = (Button)findViewById(R.id.button_paso);
        btnBiceps = (Button)findViewById(R.id.button_brazo);
        btnPlanchas=(Button)findViewById(R.id.button_salto);


        btnPasos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), Pasos.class);
                startActivity(intent);
            }
        });

        btnPlanchas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), Planchas.class);
                startActivity(intent);
            }
        });

        btnBiceps.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), Brazos.class);
                startActivity(intent);
            }
        });


    }
}
