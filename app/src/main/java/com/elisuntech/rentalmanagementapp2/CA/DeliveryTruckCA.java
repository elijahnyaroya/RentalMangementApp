package com.elisuntech.rentalmanagementapp2.CA;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.elisuntech.rentalmanagementapp2.DMC.DeliveryTruckDMC;
import com.elisuntech.rentalmanagementapp2.R;

import java.util.ArrayList;
import java.util.List;

public class DeliveryTruckCA extends RecyclerView.Adapter<DeliveryTruckCA.MyViewHolder> implements Filterable {
    private Context context;
    private List<DeliveryTruckDMC> tenantList;
    private List<DeliveryTruckDMC> tenantListFiltered;
    private DelivertTruckAdapterListener listener;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView price ;
        public TextView name ;
        public TextView location ;
        public TextView tvVerified ;
        public TextView vehicle ;
        public ImageView thumbnail ,verifiedbadge;
        Button callbutton;

        public MyViewHolder(View view) {
            super(view);
            price =itemView.findViewById(R.id.price);
            name =itemView.findViewById(R.id.name);
            location =itemView.findViewById(R.id.location);
            tvVerified =itemView.findViewById(R.id.tvVerified);
            thumbnail =itemView.findViewById(R.id.thumbnail);
            verifiedbadge =itemView.findViewById(R.id.verifiedbadge);
            vehicle =itemView.findViewById(R.id.vehicle);
            callbutton =itemView.findViewById(R.id.callbutton);

            callbutton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    // send selected tenant in callback
                    listener.onTenantSelected(tenantListFiltered.get(getAdapterPosition()));
                }
            });
        }
    }


    public DeliveryTruckCA(Context context, List<DeliveryTruckDMC> tenantList, DeliveryTruckCA.DelivertTruckAdapterListener listener) {
        this.context = context;
        this.listener = listener;
        this.tenantList = tenantList;
        this.tenantListFiltered = tenantList;
    }

    @Override
    public DeliveryTruckCA.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.deliverytruck_listview, parent, false);

        return new DeliveryTruckCA.MyViewHolder(itemView);
    }

    @SuppressLint("ResourceAsColor")
    @Override
    public void onBindViewHolder(DeliveryTruckCA.MyViewHolder holder, final int position) {
        final DeliveryTruckDMC currentitem = tenantListFiltered.get(position);


        holder.price.setText("Price : "+currentitem.getPrice());
        holder.name.setText("Name : "+currentitem.getName());
        holder.location.setText("Location "+currentitem.getLocation());
        holder.vehicle.setText("Vihicle : "+currentitem.getVehicle());
        Glide.with(context).load(currentitem.getProfileimage()).into(holder.thumbnail);
        if (currentitem.getTvVerified().equals("verified")){
            Glide.with(context).load(currentitem.getBadgeimage()).into(holder.verifiedbadge);
            holder.tvVerified.setText("Verified");
            holder.tvVerified.setTextColor(context.getResources().getColor(R.color.colorPrimary));
        }else{
            Glide.with(context).load(currentitem.getBadgeimage()).into(holder.verifiedbadge);
            holder.tvVerified.setText("Not verified");
        }


    }

    @Override
    public int getItemCount() {
        return tenantListFiltered.size();
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                String charString = charSequence.toString();
                if (charString.isEmpty()) {
                    tenantListFiltered = tenantList;
                } else {
                    List<DeliveryTruckDMC> filteredList = new ArrayList<>();
                    for (DeliveryTruckDMC row : tenantList) {

                        // name match condition. this might differ depending on your requirement
                        // here we are looking for name or phone number match
                        if (row.getName().toLowerCase().contains(charString.toLowerCase())
                                || row.getTvVerified().toLowerCase().contains(charSequence)|| row.getLocation().toLowerCase().contains(charSequence)
                                || row.getPrice().contains(charSequence)
                                || row.getVehicle().toLowerCase().contains(charSequence)) {
                            filteredList.add(row);
                        }
                    }

                    tenantListFiltered = filteredList;
                }

                FilterResults filterResults = new FilterResults();
                filterResults.values = tenantListFiltered;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                tenantListFiltered = (ArrayList<DeliveryTruckDMC>) filterResults.values;
                notifyDataSetChanged();
            }
        };
    }

    public interface DelivertTruckAdapterListener {
        void onTenantSelected(DeliveryTruckDMC tenantS);
    }
}