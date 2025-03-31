package com.example.d308_jswa142.UI;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

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

import java.util.ArrayList;
import java.util.List;

public class VacationDetails extends AppCompatActivity {
    String title;
    String hotel;
    int vacationID;

    EditText editTitle;
    EditText editName;
    Repository repository;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_vacation_details);

        editTitle = findViewById(R.id.titletext);
        editName = findViewById(R.id.hotelname);
        vacationID = getIntent().getIntExtra("vacationID", -1);
        title = getIntent().getStringExtra("vacationName");
        hotel = getIntent().getStringExtra("hotelName");

        editTitle.setText(title);
        editName.setText(hotel);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            FloatingActionButton fab = findViewById(R.id.floatingActionButton2);
            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(VacationDetails.this, ExcursionDetails.class);
                    startActivity(intent);
                }
            });
            return insets;
        });
        RecyclerView recyclerView = findViewById(R.id.excursionrecyclerview);
        repository = new Repository(getApplication());
        final ExcursionAdapter excursionAdapter = new ExcursionAdapter(this);
        recyclerView.setAdapter(excursionAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        List<Excursion> filteredExcursions = new ArrayList<>();
        for (Excursion e : repository.getmAllExcursions()) {
            if (e.getVacationID() == vacationID) filteredExcursions.add(e);
        }
        excursionAdapter.setExcursions(filteredExcursions);

    }
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_vacationdetails, menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId()== R.id.saveproduct){
            Vacation vacation;
            if(vacationID == -1) {
                if(repository.getmAllVacations().isEmpty()) vacationID = 1;
                else vacationID = repository.getmAllVacations().get(repository.getmAllVacations().size() - 1).getVacationID() + 1;
                vacation = new Vacation(vacationID, editTitle.getText().toString(), editName.getText().toString());
                repository.insert(vacation);
                this.finish();
            }
            else{
                vacation = new Vacation(vacationID, editTitle.getText().toString(), editName.getText().toString());
                repository.update(vacation);
                this.finish();
            }

        }
        return true;
    }
}