package com.example.pomodoropucp;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.util.ArrayList;

public class TimerActivity extends AppCompatActivity {

    private TextView textViewName, textViewEmail, textViewTimer;
    private ImageView iconoGenero;
    private Button buttonStartReset;
    private String nombre, apellido, correo, genero, id;
    private CountDownTimer countDownTimer;
    private boolean isTimerRunning = false;
    private long timeLeftInMillis = 1500000;
    private long restTimeInMillis = 300000; // 5 minutos de descanso en milisegundos
    private ApiService apiService;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_timer);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });


        textViewName = findViewById(R.id.nombre);
        textViewEmail = findViewById(R.id.correo);
        textViewTimer = findViewById(R.id.tiempoTxt);
        buttonStartReset = findViewById(R.id.botonIniciarReiniciar);
        iconoGenero = findViewById(R.id.iconoGenero);

        nombre = getIntent().getStringExtra("nombre");
        apellido = getIntent().getStringExtra("apellido");
        correo = getIntent().getStringExtra("correo");
        genero = getIntent().getStringExtra("genero");
        id = getIntent().getStringExtra("id");
        textViewName.setText(nombre + ' ' + apellido);
        textViewEmail.setText(correo);


        if (genero != null) {
            if (genero.equalsIgnoreCase("male")) {
                iconoGenero.setImageResource(R.drawable.male);
            } else if (genero.equalsIgnoreCase("female")) {
                iconoGenero.setImageResource(R.drawable.female);
            }
        }

        actualizarTextoTiempo();

        buttonStartReset.setOnClickListener(v -> {
            if (isTimerRunning) {
                reiniciarTiempo();
            } else {
                iniciarTiempo();
            }
        });

    }

    private void iniciarTiempo() {
        countDownTimer = new CountDownTimer(timeLeftInMillis, 1000) {

            @Override
            public void onTick(long millisUntilFinished) {
                timeLeftInMillis = millisUntilFinished;
                actualizarTextoTiempo();
            }

            @Override
            public void onFinish() {
                isTimerRunning = false;
                buttonStartReset.setText("Reiniciar");

                // Mostrar el diálogo de descanso
                new MaterialAlertDialogBuilder(TimerActivity.this)
                        .setTitle("Tiempo de descanso")
                        .setMessage("Debes dejar de trabajar y tomar un descanso ahora.")
                        .setPositiveButton("Aceptar", (dialog, which) -> {
                            // Iniciar el temporizador de descanso automáticamente
                            iniciarTiempo();
                            // Obtener las tareas del usuario
                            revisarTareas();
                        })
                        .setCancelable(false)
                        .show();
            }
        }.start();

        isTimerRunning = true;
        buttonStartReset.setText("Reiniciar");
    }

    private void reiniciarTiempo() {
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }
        timeLeftInMillis = 1500000;
        actualizarTextoTiempo();
        buttonStartReset.setText("Iniciar");
        isTimerRunning = false;
    }

    private void actualizarTextoTiempo() {
        int minutes = (int) (timeLeftInMillis / 1000) / 60;
        int seconds = (int) (timeLeftInMillis / 1000) % 60;
        String timeFormatted = String.format("%02d:%02d", minutes, seconds);
        textViewTimer.setText(timeFormatted);
    }





    private void revisarTareas() {
        apiService.getUserTasks(userId).enqueue(new Callback<List<Todo>>() {
            @Override
            public void onResponse(Call<List<Todo>> call, Response<List<Todo>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Todo> todos = response.body();
                    if (todos.isEmpty()) {
                        // Si no hay tareas, mostrar el diálogo indicando que empezó el tiempo de descanso
                        showNoTasksDialog();
                    } else {
                        // Si hay tareas, redirigir a TareasActivity
                        Intent intent = new Intent(TimerActivity.this, TareasActivity.class);
                        intent.putParcelableArrayListExtra("tasks", new ArrayList<>(todos));
                        startActivity(intent);
                    }
                }
            }

            @Override
            public void onFailure(Call<List<Todo>> call, Throwable t) {
                Log.e("TimerActivity", "Error al obtener las tareas: " + t.getMessage());
            }
        });
    }

    private void showNoTasksDialog() {
        new MaterialAlertDialogBuilder(TimerActivity.this)
                .setTitle("Tiempo de descanso")
                .setMessage("No tienes tareas pendientes. El tiempo de descanso ha comenzado.")
                .setPositiveButton("Aceptar", (dialog, which) -> dialog.dismiss())
                .setCancelable(false)
                .show();
    }




}