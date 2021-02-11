package com.project.attractionsapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class Booking extends AppCompatActivity {

    private RecyclerView recyclerView;
    private BookingAdapter adapter;
    private List<Attraction> attractionList;
    private ProgressBar progressBar;
    SwipeRefreshLayout swipeRefreshLayout;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_booking);
        progressBar = findViewById(R.id.progressbar);

        recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        swipeRefreshLayout = findViewById(R.id.swipeContainer);

        getData();
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getData();
                swipeRefreshLayout.setRefreshing(false);
            }
        });
    }

    private void getData() {
        attractionList = new ArrayList<>();
        adapter = new BookingAdapter(this, attractionList);

        recyclerView.setAdapter(adapter);
        db = FirebaseFirestore.getInstance();
        db.collection("attraction").get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        progressBar.setVisibility(View.GONE);
                        if (!queryDocumentSnapshots.isEmpty()) {
                            List<DocumentSnapshot> Attractionlist = queryDocumentSnapshots.getDocuments();

                            for (DocumentSnapshot doc : Attractionlist) {
                                Attraction attraction = new Attraction(doc.getString("name"),doc.getString("address"),
                                        doc.getString("url"),doc.getString("price"),doc.getString("availableTickets"),doc.getString("id"));
                                attractionList.add(attraction);
                            }
                            adapter.notifyDataSetChanged();
                        }
                    }
                });
    }

    public void onBackPressed() {
        startActivity(new Intent(getApplicationContext(),MainActivity.class));
        finish();
    }

}
