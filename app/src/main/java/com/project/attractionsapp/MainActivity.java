/* Final Project
Campus: Ashdod
Author: Or Maman, ID: 311392450
Author2: Sami Saliba, ID: 313552234
*/

package com.project.attractionsapp;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;


public class MainActivity extends AppCompatActivity {
    private TextView userName;
    FirebaseAuth fAuth;
    FirebaseFirestore fStore;
    String userId;

@Override
protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    userName = findViewById(R.id.user_name);
    fAuth = FirebaseAuth.getInstance();
    fStore = FirebaseFirestore.getInstance();
    userId = fAuth.getCurrentUser().getUid();
    DocumentReference documentReference = fStore.collection("users").document(userId);
    documentReference.addSnapshotListener(this, new EventListener<DocumentSnapshot>() {
        @Override
        public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
            assert documentSnapshot != null;
            if(documentSnapshot.exists()){
                userName.setText(documentSnapshot.getString("fName"));
            }
            else {
                Log.d("tag", "onEvent: Document do not exists");
            }
        }
    });
}

    public void Logout(View view) {
        FirebaseAuth.getInstance().signOut();
        startActivity(new Intent(getApplicationContext(),Login.class));
        finish();
    }

    public void Profile(View view) {
        startActivity(new Intent(getApplicationContext(),Profile.class));
    }

    public void Booking(View view) {
        startActivity(new Intent(getApplicationContext(),Booking.class));
    }

    public void TicketsHistory(View view) {
        startActivity(new Intent(getApplicationContext(),TicketList.class));
    }

    public void Insert(View view) {
        startActivity(new Intent(getApplicationContext(),InsertAttraction.class));
    }
}
