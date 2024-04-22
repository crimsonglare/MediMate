package com.example.medimate;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

public class CreateAlarmActivity extends AppCompatActivity {

    private TimePicker timePicker;
    private EditText medicineNameEditText;
    private Button saveAlarmButton;

    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_alarm);

        timePicker = findViewById(R.id.timePicker);
        medicineNameEditText = findViewById(R.id.medicineNameEditText);
        saveAlarmButton = findViewById(R.id.saveAlarmButton);

        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            String userUid = currentUser.getUid();
            databaseReference = FirebaseDatabase.getInstance().getReference().child("Users").child(userUid).child("Alarms");
        }

        saveAlarmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveAlarm();
            }
        });
    }

    private void saveAlarm() {
        int hour = timePicker.getCurrentHour();
        int minute = timePicker.getCurrentMinute();
        String time = String.format("%02d:%02d", hour, minute);
        String medicineName = medicineNameEditText.getText().toString().trim();

        if (medicineName.isEmpty()) {
            Toast.makeText(this, "Please enter a medicine name", Toast.LENGTH_SHORT).show();
            return;
        }

        // Save the alarm to Firebase Realtime Database
        String alarmId = databaseReference.push().getKey();
        if (alarmId != null) {
            Map<String, Object> alarmData = new HashMap<>();
            alarmData.put("time", time);
            alarmData.put("medicineName", medicineName);
            // Add more data as needed

            databaseReference.child(alarmId).setValue(alarmData, new DatabaseReference.CompletionListener() {
                @Override
                public void onComplete(@NonNull DatabaseError error, @NonNull DatabaseReference ref) {
                    if (error == null) {
                        Toast.makeText(CreateAlarmActivity.this, "Alarm saved successfully", Toast.LENGTH_SHORT).show();
                        finish();
                    } else {
                        Toast.makeText(CreateAlarmActivity.this, "Failed to save alarm: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            });
        } else {
            Toast.makeText(this, "Failed to save alarm", Toast.LENGTH_SHORT).show();
        }
    }

}
