package com.example.pomodoropucp;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.util.ArrayList;
import java.util.List;

public class TimerActivity extends AppCompatActivity {

    private TextView textViewName, textViewEmail, textViewTimer;
    private ImageView iconoGenero;
    private Button buttonStartReset;
    private String nombre, apellido, correo, genero;
    private Integer id;
    private CountDownTimer countDownTimer;
    private boolean isTimerRunning = false;
    private long timeLeftInMillis = 5000;
    private long restTimeInMillis =  12000;
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

        apiService = RetrofitClient.getApiService();  // Asegúrate de tener esto en tu código

        textViewName = findViewById(R.id.nombre);
        textViewEmail = findViewById(R.id.correo);
        textViewTimer = findViewById(R.id.tiempoTxt);
        buttonStartReset = findViewById(R.id.botonIniciarReiniciar);
        iconoGenero = findViewById(R.id.iconoGenero);

        nombre = getIntent().getStringExtra("nombre");
        apellido = getIntent().getStringExtra("apellido");
        correo = getIntent().getStringExtra("correo");
        genero = getIntent().getStringExtra("genero");
        id = getIntent().getIntExtra("id", -1);
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

                apiService.getUserTasks(id).enqueue(new Callback<TareaResponse>() {
                    @Override
                    public void onResponse(Call<TareaResponse> call, Response<TareaResponse> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            List<Tarea> tareas = response.body().getTodos();
                            System.out.println(tareas);
                            System.out.println(tareas.get(0));
                            System.out.println("Primera tarea: " + tareas.get(0).getTarea());// Obtener la lista de tareas desde el objeto "todos"

                            if (!tareas.isEmpty()) {
                                Intent intent = new Intent(TimerActivity.this, TareasActivity.class);
                                intent.putParcelableArrayListExtra("tasks", new ArrayList<>(tareas));
                                startActivity(intent);
                                empezarTiempoDescanso();
                                buttonStartReset.setVisibility(View.GONE);
                            } else {
                                new MaterialAlertDialogBuilder(TimerActivity.this)
                                        .setTitle("¡Felicidades!")
                                        .setMessage("Empezó el tiempo de descanso!")
                                        .setPositiveButton("Entendido", (dialog, which) -> {
                                            empezarTiempoDescanso();
                                        })
                                        .setCancelable(false)
                                        .show();

                                buttonStartReset.setVisibility(View.GONE);
                            }
                        } else {
                            Log.e("TimerActivity", "Error al obtener las tareas");
                        }
                    }

                    @Override
                    public void onFailure(Call<TareaResponse> call, Throwable t) {
                        Log.e("TimerActivity", "Error al obtener las tareas: " + t.getMessage());
                    }
                });
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



    private void empezarTiempoDescanso() {
        countDownTimer = new CountDownTimer(restTimeInMillis, 1000) {

            @Override
            public void onTick(long millisUntilFinished) {
                timeLeftInMillis = millisUntilFinished;
                actualizarTextoTiempo();
            }

            @Override
            public void onFinish() {
                isTimerRunning = false;
                buttonStartReset.setText("Reiniciar");
                new MaterialAlertDialogBuilder(TimerActivity.this)
                        .setTitle("Atención")
                        .setMessage("Terminó el tiempo de descanso. Dale al botón de reinicio para iniciar otro ciclo.")
                        .setPositiveButton("Entendido", (dialog, which) -> {
                        })
                        .setCancelable(false)
                        .show();
                buttonStartReset.setVisibility(View.VISIBLE);

            }
        }.start();
    }



}