package com.elisuntech.rentalmanagementapp2.CA;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.elisuntech.rentalmanagementapp2.DMC.waterbillDMC;
import com.elisuntech.rentalmanagementapp2.R;

import java.util.ArrayList;
import java.util.List;

public class waterbillCA extends RecyclerView.Adapter<waterbillCA.MyViewHolder>
        implements Filterable {
    private Context context;
    private List<waterbillDMC> tenantList;
    private List<waterbillDMC> tenantListFiltered;
    private waterAdapterListener listener;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView name, phone,tenantIDNo,amount,waterusints;
        public ImageView thumbnail,paidVerified,more;
        public RelativeLayout moreActions;
        public LinearLayout rowBackground;

        public MyViewHolder(View view) {
            super(view);
            name = view.findViewById(R.id.name);
            phone = view.findViewById(R.id.phone);
            thumbnail = view.findViewById(R.id.thumbnail);
            tenantIDNo = view.findViewById(R.id.tenantIDNo);
            amount = view.findViewById(R.id.amount);
            rowBackground = view.findViewById(R.id.rowBackground);
            paidVerified = view.findViewById(R.id.paidVerified);
            waterusints = view.findViewById(R.id.waterusints);
            more = view.findViewById(R.id.more);



            more.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    // send selected tenant in callback
                    listener.onTenantSelected(tenantListFiltered.get(getAdapterPosition()));
                }
            });
        }
    }


    public waterbillCA(Context context, List<waterbillDMC> tenantList, waterAdapterListener listener) {
        this.context = context;
        this.listener = listener;
        this.tenantList = tenantList;
        this.tenantListFiltered = tenantList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.waterbill_listview, parent, false);

        return new MyViewHolder(itemView);
    }

    @SuppressLint("ResourceAsColor")
    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {
        final waterbillDMC tenants = tenantListFiltered.get(position);

        //TODO 3333 this data will be pulled monthly that is for every month
        //TODO and those who will not pay in that month will be transferred to defaulters
        holder.name.setText(tenants.getTenantName());

        if (tenants.getPaymentStatus().equals("paid")){
            holder.paidVerified.setVisibility(View.VISIBLE);
            holder.more.setVisibility(View.GONE);
        }

        //holder.phone.setText("House No : "+tenants.getHouseNo());
        holder.amount.setText("Amount : "+tenants.getAmount());
        holder.tenantIDNo.setText("ID No : "+tenants.getHouseNo());
        holder.phone.setText("Phone No : "+tenants.getPhoneNo());
        holder.waterusints.setText("Consumption : "+(Integer.parseInt(tenants.getMeterReadings())-Integer.parseInt(tenants.getLastReading()))+" units");

        Glide.with(context)
                .load(tenants.getTenantImage())
                .apply(RequestOptions.circleCropTransform())
                .into(holder.thumbnail);

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
                    List<waterbillDMC> filteredList = new ArrayList<>();
                    for (waterbillDMC row : tenantList) {

                        // name match condition. this might differ depending on your requirement
                        // here we are looking for name or phone number match
                        if (row.getTenantName().toLowerCase().contains(charString.toLowerCase())
                                || row.getHouseNo().contains(charSequence)|| row.getTenantID().contains(charSequence)
                                || row.getIdNo().contains(charSequence)
                                || row.getPhoneNo().contains(charSequence)|| row.getPaymentStatus().contains(charSequence)) {
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
                tenantListFiltered = (ArrayList<waterbillDMC>) filterResults.values;
                notifyDataSetChanged();
            }
        };
    }

    public interface waterAdapterListener {
        void onTenantSelected(waterbillDMC tenantS);
    }
}