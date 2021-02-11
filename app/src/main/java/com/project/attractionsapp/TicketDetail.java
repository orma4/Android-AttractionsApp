package com.project.attractionsapp;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import com.google.firebase.firestore.FirebaseFirestore;

public class TicketDetail extends AppCompatActivity {


    private TextView attractionName;
    private TextView buyerName;
    private TextView numberOfTickets;
    private TextView totalPrice;
    private TextView date;
    private FirebaseFirestore db;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ticket_detail);
        db = FirebaseFirestore.getInstance();

        attractionName = findViewById(R.id.ticket_attraction_name);
        buyerName = findViewById(R.id.buyer_name_tv);
        totalPrice = findViewById(R.id.tv_total_price);
        numberOfTickets = findViewById(R.id.num_of_tickets);
        date = findViewById(R.id.ticket_date);

        final Ticket ticket = (Ticket) getIntent().getSerializableExtra("data");
        if (ticket != null) {
            buyerName.setText(ticket.getName());
            attractionName.setText(ticket.getAttraction());
            totalPrice.setText(ticket.getTotalPrice()+"$");
            numberOfTickets.setText(ticket.getNumberOfPeople()+" Tickets");
            date.setText(ticket.getDate());


        }

    }

    public static Intent getActIntent(Activity activity) {
        return new Intent(activity, TicketDetail.class);
    }
}