package com.project.attractionsapp;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class TicketList extends AppCompatActivity {
    private RecyclerView recyclerView;
    private TicketAdapter adapter;
    private List<Ticket> ticketList;
    private ProgressBar progressBar;
    SwipeRefreshLayout swipeRefreshLayout;
    private FirebaseFirestore db;
    FirebaseAuth fAuth;
    private String userId;
    private String email;
    private TextView ticketsBooked;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ticket_list);
        progressBar = findViewById(R.id.progressbar);

        recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        swipeRefreshLayout = findViewById(R.id.swipeContainer);
        fAuth = FirebaseAuth.getInstance();
        userId = fAuth.getCurrentUser().getUid();
        ticketsBooked = findViewById(R.id.tickets_booked);
        db = FirebaseFirestore.getInstance();
        if (userId != null) {
            DocumentReference documentReference = db.collection("users").document(userId);
            documentReference.addSnapshotListener(this, new EventListener<DocumentSnapshot>() {
                @Override
                public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                    assert documentSnapshot != null;
                    if (documentSnapshot.exists()) {
                        email = documentSnapshot.getString("email");
                        TicketListFetch(email);
                    } else {
                        Log.d("tag", "onEvent: Document do not exists");
                    }
                }
            });

            swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {
                    TicketListFetch(email);
                    swipeRefreshLayout.setRefreshing(false);
                }
            });

        }
    }

    private void TicketListFetch(String email){
        ticketList = new ArrayList<>();
        adapter = new TicketAdapter(this, ticketList);
        recyclerView.setAdapter(adapter);

        db.collection("ticket").document(email).collection("booking").get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        progressBar.setVisibility(View.GONE);
                        if(!queryDocumentSnapshots.isEmpty()){
                            List<DocumentSnapshot> list = queryDocumentSnapshots.getDocuments();

                            for(DocumentSnapshot doc : list){
                                Ticket ticket = new Ticket(doc.getString("name"),doc.getString("attraction"),
                                        doc.getString("date"),doc.getString("totalPrice"),doc.getString("numberOfPeople"));
                                ticketList.add(ticket);
                            }
                            adapter.notifyDataSetChanged();

                        }else{
                            ticketsBooked.setVisibility(View.VISIBLE);
                        }
                    }
                });
    }
}