package com.project.attractionsapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class ConfirmBooking extends AppCompatActivity {

    private EditText date;
    private DatePickerDialog.OnDateSetListener mDateSetListener;
    private TextView buyerName;
    private TextView attractionName;
    private TextView price;
    private TextView totalPrice;
    private EditText confirmPrice;
    private EditText ticketsAmount;
    private FirebaseFirestore db;
    StorageReference storageReference;
    private Button bookingBtn;
    ImageView attractionImage;
    String userId;
    String email;
    String phone;
    FirebaseAuth fAuth;
    String attractionId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirm_booking);
        db = FirebaseFirestore.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference("attraction");

        attractionName = findViewById(R.id.attraction_name_tv);
        price = findViewById(R.id.price_tv);
        totalPrice = findViewById(R.id.total_price);
        date = findViewById(R.id.order_date);
        buyerName = findViewById(R.id.buyer_name);
        ticketsAmount = findViewById(R.id.tickets_amount);
        confirmPrice = findViewById(R.id.confirm_price);
        fAuth = FirebaseAuth.getInstance();
        userId = fAuth.getCurrentUser().getUid();
        attractionImage = findViewById(R.id.attractionImageView);
        bookingBtn = findViewById(R.id.booking_btn);

        final Attraction attraction = (Attraction) getIntent().getSerializableExtra("data");
        if (attraction != null) {
            final int totalAvailable = Integer.parseInt(attraction.getAvailableTickets());
            attractionName.setText(attraction.getName());
            price.setText(attraction.getPrice().toString()+"$");
            attractionId  = attraction.getId();

            DocumentReference documentReference = db.collection("users").document(userId);
            documentReference.addSnapshotListener(this, new EventListener<DocumentSnapshot>() {
                @Override
                public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                    assert documentSnapshot != null;
                    if(documentSnapshot.exists()){
                        buyerName.setText(documentSnapshot.getString("fName"));
                        email = documentSnapshot.getString("email");
                        phone = documentSnapshot.getString("phone");
                    }
                    else {
                        Log.d("tag", "onEvent: Document do not exists");
                    }
                }
            });

            date.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Calendar cal = Calendar.getInstance();
                    int year = cal.get(Calendar.YEAR);
                    int month = cal.get(Calendar.MONTH);
                    int day = cal.get(Calendar.DAY_OF_MONTH);

                    DatePickerDialog dialog = new DatePickerDialog(ConfirmBooking.this, android.R.style.Theme_Holo_Light_Dialog_MinWidth,
                            mDateSetListener,year,month,day);
                    dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                    dialog.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);
                    dialog.show();
                }
            });

            mDateSetListener = new DatePickerDialog.OnDateSetListener() {
                @Override
                public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                    String date =  day+ "/" +  (month+1) + "/" + year;
                    ConfirmBooking.this.date.setText(date);
                }
            };

            ticketsAmount.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) { }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                    if (charSequence.length() > 0){
                        int ticketsAmount = Integer.parseInt(ConfirmBooking.this.ticketsAmount.getText().toString());
                        int ticketPrice = Integer.parseInt(String.valueOf(attraction.getPrice()));
                        String totalPrice = String.valueOf(ticketsAmount * ticketPrice);

                        ConfirmBooking.this.totalPrice.setText(totalPrice);

                    }
                    else{
                        totalPrice.setText("0");
                    }
                }

                @Override
                public void afterTextChanged(Editable editable) { }
            });

            Picasso.get().load(attraction.getUrl()).fit().centerCrop().into(attractionImage);

            bookingBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (TextUtils.isEmpty(ticketsAmount.getText())){
                        ticketsAmount.setError("Tickets Amount field is Empty!");
                        return;
                    }else if (TextUtils.isEmpty(confirmPrice.getText())){
                        confirmPrice.setError("Confirm Price field is Empty!");
                        return;
                    }else if (Integer.parseInt(totalPrice.getText().toString()) != Integer.parseInt(confirmPrice.getText().toString())){
                        confirmPrice.setError("Confirm Price doesn't match the total price!");
                        return;
                    }else if (totalAvailable < Integer.parseInt(String.valueOf(ticketsAmount.getText()))){
                        ticketsAmount.setError("There are only " + totalAvailable +" Tickets Available!");
                        return;
                    }else if (date.getText().toString().equals("")){
                        date.setError("The order date field is empty!");
                        return;
                    }else if (Integer.parseInt(String.valueOf(ticketsAmount.getText())) == 0){
                        ticketsAmount.setError("Please Enter Tickets Amount");
                        return;
                    }else{
                        int updatedTicketsAmount = totalAvailable - Integer.parseInt(String.valueOf(ticketsAmount.getText()));
                        attraction.setAvailableTickets(String.valueOf(updatedTicketsAmount));

                        final Map<String, Object> ticketOrder = new HashMap<>();
                        ticketOrder.put("attraction", attractionName.getText().toString());
                        ticketOrder.put("name", buyerName.getText().toString());
                        ticketOrder.put("numberOfPeople", ticketsAmount.getText().toString());
                        ticketOrder.put("totalPrice", totalPrice.getText().toString());
                        ticketOrder.put("date", date.getText().toString());

                        final Map<String, Object> buyerDetails = new HashMap<>();
                        buyerDetails.put("fName", buyerName.getText().toString());
                        buyerDetails.put("email",email);
                        buyerDetails.put("phone",phone);

                        db.collection("attraction").document(attractionId)
                                .set(attraction)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        db.collection("ticket").document(email).set(buyerDetails).addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                db.collection("ticket").document(email).collection("booking").document(attraction.getId())
                                                        .set(ticketOrder)
                                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                            @Override
                                                            public void onSuccess(Void aVoid) {
                                                                Toast.makeText(ConfirmBooking.this, "Order Succeeded! Check out the Ticket History", Toast.LENGTH_LONG).show();
                                                                startActivity(new Intent(getApplicationContext(),Booking.class));
                                                                finish();
                                                            }
                                                        })
                                                        .addOnFailureListener(new OnFailureListener() {
                                                            @Override
                                                            public void onFailure(@NonNull Exception e) {
                                                                Toast.makeText(ConfirmBooking.this, "Order failed! try again", Toast.LENGTH_LONG).show();
                                                            }
                                                        });
                                            }
                                        }).addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Toast.makeText(ConfirmBooking.this, "Buyer details failed to enter", Toast.LENGTH_LONG).show();
                                            }
                                        });

                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) { }
                                });

                    }
                }
            });
        }
    }

    public static Intent getActIntent(Activity activity) {
        return new Intent(activity, ConfirmBooking.class);
    }
}
