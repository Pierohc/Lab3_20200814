package com.example.pomodoropucp;

import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.ArrayList;

public class TareasActivity extends AppCompatActivity {


    private Spinner listViewTasks;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_tareas);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });



        listViewTasks = findViewById(R.id.listViewTasks);

        ArrayList<Tarea> tareas = getIntent().getParcelableArrayListExtra("tasks");
        ArrayList<String> tasksList = new ArrayList<>();

        if (tareas != null) {
            for (Tarea task : tareas) {
                if (task != null && task.getTarea() != null) {
                    tasksList.add(task.getTarea() + " - " + (task.isCompletado() ? "Completado" : "No completado"));
                } else {
                    tasksList.add("Tarea no disponible");
                }
            }
        } else {
            Log.e("TareasActivity", "Las tareas son null.");
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_list_item_1,
                tasksList
        );

        listViewTasks.setAdapter(adapter);

    }
}