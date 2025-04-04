package com.example.d308_jswa142.UI;

import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class VacationDetails extends AppCompatActivity {
    String title;
    String hotel;
    int vacationID;

    EditText editTitle;
    EditText editName;

    TextView editEdate;

    TextView editSdate;
    Repository repository;

    Vacation currentVacation;
    int numExcursions;

    DatePickerDialog.OnDateSetListener startDate;
    DatePickerDialog.OnDateSetListener endDate;
    final Calendar myCalendarStart = Calendar.getInstance();
    final Calendar myCalendarEnd = Calendar.getInstance();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_vacation_details);
        repository = new Repository(getApplication());

        editTitle = findViewById(R.id.titletext);
        editName = findViewById(R.id.hotelname);
        editSdate = findViewById(R.id.sdate);
        editEdate = findViewById(R.id.edate);
        vacationID = getIntent().getIntExtra("vacationID", -1);
        title = getIntent().getStringExtra("vacationName");
        hotel = getIntent().getStringExtra("hotelName");

        editTitle.setText(title);
        editName.setText(hotel);


        String myFormat = "MM/dd/yy";
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
        sdf.setLenient(false);

        for (Vacation vac : repository.getmAllVacations()) {
            if (vac.getVacationID() == vacationID) {
                currentVacation = vac;
                editSdate.setText(vac.getStartDate());
                editEdate.setText(vac.getEndDate());

                try {
                    myCalendarStart.setTime(sdf.parse(vac.getStartDate()));
                    myCalendarEnd.setTime(sdf.parse(vac.getEndDate()));
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                break;
            }
        }


        editSdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Date date;
                String info = editSdate.getText().toString();
                if (info.isEmpty()) info = "04/10/2024";
                try {
                    myCalendarStart.setTime(sdf.parse(info));
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                new DatePickerDialog(VacationDetails.this, startDate, myCalendarStart.get(Calendar.YEAR),
                        myCalendarStart.get(Calendar.MONTH), myCalendarStart.get(Calendar.DAY_OF_MONTH)).show();

            }
        });

        editEdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Date date;
                String info = editEdate.getText().toString();
                if (info.isEmpty()) info = "12/10/2026";
                try {
                    myCalendarEnd.setTime(sdf.parse(info));
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                new DatePickerDialog(VacationDetails.this, endDate, myCalendarEnd.get(Calendar.YEAR),
                        myCalendarEnd.get(Calendar.MONTH), myCalendarEnd.get(Calendar.DAY_OF_MONTH)).show();

            }
        });
        startDate = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                myCalendarStart.set(Calendar.YEAR, year);
                myCalendarStart.set(Calendar.MONTH, month);
                myCalendarStart.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                myCalendarEnd.set(Calendar.YEAR, year);
                myCalendarEnd.set(Calendar.MONTH, month);
                myCalendarEnd.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                updateLabelStart();
            }
        };

        endDate = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                myCalendarEnd.set(Calendar.YEAR, year);
                myCalendarEnd.set(Calendar.MONTH, month);
                myCalendarEnd.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                updateLabelStart();
            }
        };



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

        String myFormat = "MM/dd/yy";
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
        sdf.setLenient(false);


        if(item.getItemId()== R.id.savevacation){

            try {
                Date startDate = sdf.parse(editSdate.getText().toString());
                Date endDate = sdf.parse(editEdate.getText().toString());


                if (!endDate.after(startDate)) {
                    Toast.makeText(this, "End date must be after start date", Toast.LENGTH_SHORT).show();
                    return true;
                }


            } catch (ParseException e) {
                Toast.makeText(this, "Please enter a valid date in MM/dd/yy format", Toast.LENGTH_SHORT).show();
                return true;
            }


            Vacation vacation;
            if(vacationID == -1) {
                if(repository.getmAllVacations().isEmpty()) vacationID = 1;
                else vacationID = repository.getmAllVacations().get(repository.getmAllVacations().size() - 1).getVacationID() + 1;
                vacation = new Vacation(vacationID, editTitle.getText().toString(), editName.getText().toString(), editSdate.getText().toString(), editEdate.getText().toString());
                repository.insert(vacation);
                this.finish();
            }
            else{
                vacation = new Vacation(vacationID, editTitle.getText().toString(), editName.getText().toString(), editSdate.getText().toString(), editEdate.getText().toString());
                repository.update(vacation);
                this.finish();
            }

        }
        if(item.getItemId()== R.id.deletevacation) {
            for(Vacation vac:repository.getmAllVacations()) {
                if(vac.getVacationID() == vacationID) currentVacation = vac;
            }
            numExcursions = 0;
            for(Excursion excursion: repository.getmAllExcursions()) {
                if(excursion.getVacationID() == vacationID) ++ numExcursions;
            }
            if(numExcursions == 0) {
                repository.delete(currentVacation);
                Toast.makeText(VacationDetails.this, currentVacation.getVacationName() + "was deleted", Toast.LENGTH_SHORT).show();
            }
            else {
                Toast.makeText(VacationDetails.this, "Can't delete a vacation with excursions", Toast.LENGTH_SHORT).show();
            }
        }

        if (item.getItemId() == R.id.share) {
            Intent sentIntent = new Intent();
            sentIntent.setAction(Intent.ACTION_SEND);

            String vacationDetails =
                    "Hotel Name: " + editName.getText().toString() + "\n" +
                            "Start Date: " + editSdate.getText().toString() + "\n" +
                            "End Date: " + editEdate.getText().toString();

            sentIntent.putExtra(Intent.EXTRA_TEXT, vacationDetails);
            sentIntent.putExtra(Intent.EXTRA_TITLE, editTitle.getText().toString());
            sentIntent.setType("text/plain");
            Intent shareIntent = Intent.createChooser(sentIntent, null);
            startActivity(shareIntent);
            return true;
        }
        if (item.getItemId() == R.id.valert) {
            String dateFromScreen = editSdate.getText().toString();
            String dateFromScreen2 = editEdate.getText().toString();
            Date myDate = null;
            Date myDate2 = null;
            try {
                myDate = sdf.parse(dateFromScreen);
                myDate2 = sdf.parse(dateFromScreen2);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            Long triggerStart = myDate.getTime();
            Long triggerEnd = myDate2.getTime();
            AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
            Intent startIntent = new Intent(VacationDetails.this, MyReceiver.class);
            startIntent.putExtra("key", "Your " + title + " vacation starts today!");
            PendingIntent startSender = PendingIntent.getBroadcast(VacationDetails.this, ++MainActivity.numAlert, startIntent,
                    PendingIntent.FLAG_ONE_SHOT | PendingIntent.FLAG_IMMUTABLE);
            alarmManager.set(AlarmManager.RTC_WAKEUP, triggerStart, startSender);

            Intent endIntent = new Intent(VacationDetails.this, MyReceiver.class);
            endIntent.putExtra("key", "Your " + title + " vacation ends today!");
            PendingIntent endSender = PendingIntent.getBroadcast(VacationDetails.this, ++MainActivity.numAlert, endIntent,
                    PendingIntent.FLAG_ONE_SHOT | PendingIntent.FLAG_IMMUTABLE);
            alarmManager.set(AlarmManager.RTC_WAKEUP, triggerEnd, endSender);

            Toast.makeText(this, "Alerts set for start and end dates!", Toast.LENGTH_SHORT).show();

            return true;
        }
        return true;
    }
    private void updateLabelStart () {
        String myFormat = "MM/dd/yy";
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);

        editSdate.setText(sdf.format(myCalendarStart.getTime()));
        editEdate.setText(sdf.format(myCalendarEnd.getTime()));
    }



}