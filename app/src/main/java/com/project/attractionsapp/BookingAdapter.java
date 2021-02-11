package com.project.attractionsapp;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

public class BookingAdapter extends RecyclerView.Adapter<BookingAdapter.AttractionViewHolder> {

    private Context context;
    private List<Attraction> attractionsList;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private String docID;


    public BookingAdapter(Context context, List<Attraction> attractionList) {
        this.context = context;
        this.attractionsList = attractionList;
    }

    public Context getContext(){
        return this.context;
    }
    @NonNull
    @Override
    public AttractionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new AttractionViewHolder(
                LayoutInflater.from(context).inflate(R.layout.layout_booking, parent, false)
        );
    }
    @Override
    public void onBindViewHolder(@NonNull final AttractionViewHolder holder, final int position) {
        final Attraction attraction = attractionsList.get(position);
        holder.nameTextView.setText(attraction.getName());
        holder.addressTextView.setText("Address : " +attraction.getAddress());
        holder.priceTextView.setText("Price:  " + attraction.getPrice() + "$");
        holder.availableTextView.setText("There are " + attraction.getAvailableTickets() + " available tickets");
        Picasso.get()
                .load(attraction.getUrl())
                .placeholder(R.mipmap.ic_launcher)
                .fit()
                .centerCrop()
                .into(holder.imageview);
        if(Integer.parseInt(attraction.getAvailableTickets()) == 0){
            holder.bookingBtn.setEnabled(false);
        }else {
            holder.bookingBtn.setEnabled(true);
        }

        holder.bookingBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                context.startActivity(ConfirmBooking.getActIntent((Activity) context).putExtra("data", attractionsList.get(position)));
            }
        });

        holder.editBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                context.startActivity(UpdateAttraction.getActIntent((Activity) context).putExtra("data", attractionsList.get(position)));
            }
        });

        holder.deleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog alertDialog = new AlertDialog.Builder(getContext())
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .setTitle("Are you sure to delete this attraction?")
                        .setMessage("This action is irreversible!")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                docID = attraction.getId();
                                db.collection("attraction").document(docID).delete();
                                Toast.makeText(getContext(),"Attraction deleted!",Toast.LENGTH_LONG).show();
                                getContext().startActivity(new Intent(getContext(),Booking.class));
                            }
                        })
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                            }
                        })
                        .show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return attractionsList.size();
    }


    class AttractionViewHolder extends RecyclerView.ViewHolder {

        public TextView nameTextView, addressTextView, priceTextView, availableTextView;
        public Button bookingBtn;
        public Button editBtn;
        public Button deleteBtn;
        public ImageView imageview;
        public CardView cvMain;

        public AttractionViewHolder(View itemView) {
            super(itemView);

            nameTextView = itemView.findViewById(R.id.tv_name);
            addressTextView = itemView.findViewById(R.id.tv_address);
            priceTextView = itemView.findViewById(R.id.tv_price);
            availableTextView = itemView.findViewById(R.id.tv_available_tickets);
            imageview = itemView.findViewById(R.id.imageView);
            cvMain = (CardView) itemView.findViewById(R.id.cv_attractions);
            bookingBtn = itemView.findViewById(R.id.btn_booking);
            editBtn = itemView.findViewById(R.id.btn_edit);
            deleteBtn = itemView.findViewById(R.id.btn_delete);

        }
    }
}
