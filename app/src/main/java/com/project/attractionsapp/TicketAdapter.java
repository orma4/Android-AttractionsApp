package com.project.attractionsapp;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class TicketAdapter extends RecyclerView.Adapter<TicketAdapter.TicketViewHolder>{
    private Context context;
    private List<Ticket> ticketList;

    public TicketAdapter(Context context, List<Ticket> ticketList) {
        this.context = context;
        this.ticketList = ticketList;
    }

    @NonNull
    @Override
    public TicketAdapter.TicketViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new TicketAdapter.TicketViewHolder(
                LayoutInflater.from(context).inflate(R.layout.layout_ticket, parent, false)
        );
    }

    @Override
    public void onBindViewHolder(@NonNull TicketAdapter.TicketViewHolder holder, final int position) {
        Ticket ticket= ticketList.get(position);
        holder.attractionName.setText(ticket.getAttraction());
        holder.attractionName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                context.startActivity(TicketDetail.getActIntent((Activity) context).putExtra("data", ticketList.get(position)));
            }
        });
    }

    @Override
    public int getItemCount() {
        return ticketList.size();
    }

    class TicketViewHolder extends RecyclerView.ViewHolder {
        public TextView attractionName;

        public TicketViewHolder(View itemView) {
            super(itemView);
            attractionName = itemView.findViewById(R.id.attraction_name_textview);
        }
    }
}
