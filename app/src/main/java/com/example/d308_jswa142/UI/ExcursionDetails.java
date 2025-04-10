package com.example.d308_jswa142.UI;

import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.d308_jswa142.R;
import com.example.d308_jswa142.database.Repository;
import com.example.d308_jswa142.entities.Excursion;
import com.example.d308_jswa142.entities.Vacation;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class ExcursionDetails extends AppCompatActivity {

    String name;

    int excursionid;
    int vacationID;


    Repository repository;

    EditText editName;

    TextView editDate;

    Excursion currentExcursion;

    DatePickerDialog.OnDateSetListener excursionDate;
    final Calendar myCalendarStart = Calendar.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_excursion_details);

        repository = new Repository(getApplication());
        editName = findViewById(R.id.excursionname);
        vacationID = getIntent().getIntExtra("vacationID", -1);
        name = getIntent().getStringExtra("excursionName");
        excursionid = getIntent().getIntExtra("excursionID", -1);
        editName.setText(name);
        editDate = findViewById(R.id.date);
        String dateString = getIntent().getStringExtra("excursionDate");

// Fallback if the date is null or invalid
        if (dateString == null || dateString.isEmpty()) {
            dateString = "04/10/2024"; // default fallback date
        }

        editDate.setText(dateString);

        try {
            myCalendarStart.setTime(new SimpleDateFormat("MM/dd/yy", Locale.US).parse(dateString));
        } catch (ParseException e) {
            e.printStackTrace(); // log it
            Toast.makeText(this, "Could not parse date, using today.", Toast.LENGTH_SHORT).show();
            myCalendarStart.setTime(new Date());
        }

        String myFormat = "MM/dd/yy";
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
        sdf.setLenient(false);

        excursionDate = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                myCalendarStart.set(Calendar.YEAR, year);
                myCalendarStart.set(Calendar.MONTH, month);
                myCalendarStart.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                updateLabelStart();
            }
        };

        editDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Date date;
                String info = editDate.getText().toString();
                if (info.isEmpty()) info = "04/10/2024";

                try {
                    myCalendarStart.setTime(sdf.parse(info));
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                new DatePickerDialog(ExcursionDetails.this, excursionDate, myCalendarStart.get(Calendar.YEAR),
                        myCalendarStart.get(Calendar.MONTH), myCalendarStart.get(Calendar.DAY_OF_MONTH)).show();

            }
        });

        Spinner spinner = findViewById(R.id.spinner);
        ArrayList<Vacation> vacationArrayList = new ArrayList<>();
        vacationArrayList.addAll(repository.getmAllVacations());
        ArrayAdapter<Vacation> vacationAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, vacationArrayList);
        spinner.setAdapter(vacationAdapter);



        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_excursiondetails, menu);
        return true;
    }

    private void updateLabelStart () {
        String myFormat = "MM/dd/yy";
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);

        editDate.setText(sdf.format(myCalendarStart.getTime()));
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            this.finish();
            return true;
        }
        if (item.getItemId() == R.id.saveexcursion) {

            String excursionDateStr = editDate.getText().toString();
            String myFormat = "MM/dd/yy";
            SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
            sdf.setLenient(false);

            Date excursionDate = null;
            try {
                excursionDate = sdf.parse(excursionDateStr);
            } catch (ParseException e) {
                Toast.makeText(this, "Invalid date format.", Toast.LENGTH_SHORT).show();
                return true;
            }

            Spinner spinner = findViewById(R.id.spinner);
            Vacation selectedVacation = (Vacation) spinner.getSelectedItem();
            vacationID = selectedVacation.getVacationID();

            Date startDate = null;
            Date endDate = null;
            try {
                startDate = sdf.parse(selectedVacation.getStartDate());
                endDate = sdf.parse(selectedVacation.getEndDate());
            } catch (ParseException e) {
                Toast.makeText(this, "Invalid vacation dates.", Toast.LENGTH_SHORT).show();
                return true;
            }

            if (excursionDate.before(startDate) || excursionDate.after(endDate)) {
                Toast.makeText(this, "Excursion date must be within the vacation dates.", Toast.LENGTH_LONG).show();
                return true;
            }


            Excursion excursion;
            if (excursionid == -1) {
                if (repository.getmAllExcursions().isEmpty()) excursionid = 1;
                else
                    excursionid = repository.getmAllExcursions().get(repository.getmAllExcursions().size() - 1).getExcursionID() + 1;
                excursion = new Excursion(excursionid, editName.getText().toString(), vacationID, editDate.getText().toString());
                repository.insert(excursion);
                this.finish();
            } else {
                excursion = new Excursion(excursionid, editName.getText().toString(), vacationID, editDate.getText().toString());
                repository.update(excursion);
                this.finish();

            }
            return true;
        }

        if (item.getItemId() == R.id.deleteexcursion) {

            for (Excursion excursion : repository.getmAllExcursions()) {
                if (excursion.getExcursionID() == excursionid) currentExcursion = excursion;
            }
            repository.delete(currentExcursion);
            Toast.makeText(ExcursionDetails.this, currentExcursion.getExcursionName() + " was deleted", Toast.LENGTH_LONG).show();
            ExcursionDetails.this.finish();

        }

        if (item.getItemId() == R.id.alert) {
            String dateFromScreen = editDate.getText().toString();
            String myFormat = "MM/dd/yy";
            SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
            Date myDate = null;
            try {
                myDate = sdf.parse(dateFromScreen);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            Long trigger = myDate.getTime();
            Intent intent = new Intent(ExcursionDetails.this, MyReceiver.class);
            intent.putExtra("key", "Your " + name + " starts today!" );
            PendingIntent sender = PendingIntent.getBroadcast(ExcursionDetails.this, ++MainActivity.numAlert, intent,
                    PendingIntent.FLAG_ONE_SHOT | PendingIntent.FLAG_IMMUTABLE);
            AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
            alarmManager.set(AlarmManager.RTC_WAKEUP, trigger, sender);
            return true;
        }
        return super.onOptionsItemSelected(item);

    }

}