package com.example.d308_jswa142.UI;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.d308_jswa142.R;
import com.example.d308_jswa142.database.Repository;
import com.example.d308_jswa142.entities.Excursion;
import com.example.d308_jswa142.entities.Vacation;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;

public class VacationList extends AppCompatActivity {
    private Repository repository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_vacation_list);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            FloatingActionButton fab = findViewById(R.id.floatingActionButton);
            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(VacationList.this, VacationDetails.class);
                    startActivity(intent);
                }
            });
            RecyclerView recyclerView = findViewById(R.id.recyclerview);
            repository = new Repository(getApplication());
            List<Vacation> allVacations = repository.getmAllVacations();
            final VacationAdapter vacationAdapter = new VacationAdapter(this);
            recyclerView.setAdapter(vacationAdapter);
            recyclerView.setLayoutManager(new LinearLayoutManager(this));
            vacationAdapter.setVacations(allVacations);

            return insets;

        });

    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.menu_vacation_list, menu);
        return true;
    }

    @Override
    protected void onResume() {

        super.onResume();

        if (repository == null) {
            repository = new Repository(getApplication());
        }

        List<Vacation> allVacations = repository.getmAllVacations();
        RecyclerView recyclerView = findViewById(R.id.recyclerview);
        final VacationAdapter vacationAdapter = new VacationAdapter(this);
        recyclerView.setAdapter(vacationAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        vacationAdapter.setVacations(allVacations);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        if (item.getItemId() == R.id.mysample) {
            repository = new Repository(getApplication());
            //Toast.makeText(VacationList.this, "put in sample data", Toast.LENGTH_LONG).show();
            Vacation vacation = new Vacation(0, "Paris", "Chateau des Fleurs");
            repository.insert(vacation);
            vacation = new Vacation(0, "New York", "The Manhattan");
            repository.insert(vacation);
            Excursion excursion = new Excursion(0, "Scuba Diving", 1);
            repository.insert(excursion);
            excursion = new Excursion(0, "Cheese Tasting", 1);
            repository.insert(excursion);

            return true;
        }

        if (item.getItemId() == android.R.id.home) {
            this.finish();
            return true;
        }

        return true;
    }
}