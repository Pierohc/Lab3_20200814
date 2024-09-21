package com.example.pomodoropucp;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import androidx.activity.EdgeToEdge;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity {

    private EditText user, passwd;
    private Button botonLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        user = findViewById(R.id.user);
        passwd = findViewById(R.id.password);
        botonLogin = findViewById(R.id.login);

        botonLogin.setOnClickListener(v -> login());
    }



    private void login() {
        String username = user.getText().toString().trim();
        String password = passwd.getText().toString().trim();

        if (username.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Por favor ingrese usuario y contraseña", Toast.LENGTH_SHORT).show();
            return;
        }

        LoginRequest loginRequest = new LoginRequest(username, password);
        RetrofitClient.getApiService().login(loginRequest).enqueue(new Callback<LoginResponse>() {
            @Override
            public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                if (response.isSuccessful()) {
                    LoginResponse loginResponse = response.body();
                    Toast.makeText(MainActivity.this, "Login exitoso. Bienvenido " +
                            loginResponse.getFirstName(), Toast.LENGTH_SHORT).show();

                    Intent intent = new Intent(MainActivity.this, TimerActivity.class);
                    intent.putExtra("nombre", loginResponse.getFirstName());
                    intent.putExtra("correo", loginResponse.getEmail());
                    intent.putExtra("apellido", loginResponse.getLastName());
                    intent.putExtra("genero", loginResponse.getGender());
                    Integer userId = loginResponse.getId();
                    intent.putExtra("id", userId);
                    startActivity(intent);


                } else {
                    Toast.makeText(MainActivity.this, "Credenciales incorrectas", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<LoginResponse> call, Throwable t) {
                Toast.makeText(MainActivity.this, "Error de conexión", Toast.LENGTH_SHORT).show();
            }
        });
    }




}