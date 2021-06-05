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
import com.elisuntech.rentalmanagementapp2.DMC.defaulterDMC;
import com.elisuntech.rentalmanagementapp2.R;

import java.util.ArrayList;
import java.util.List;

public class defaulterCA extends RecyclerView.Adapter<defaulterCA.MyViewHolder>
        implements Filterable {
    private Context context;
    private List<defaulterDMC> tenantList;
    private List<defaulterDMC> tenantListFiltered;
    private TenantssAdapterListener listener;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView name, phone,tenantIDNo,amount,paymentDate;
        public ImageView thumbnail,paidVerified;
        public RelativeLayout moreActions;
        public LinearLayout rowBackground;

        public MyViewHolder(View view) {
            super(view);
            name = view.findViewById(R.id.name);
            phone = view.findViewById(R.id.phone);
            thumbnail = view.findViewById(R.id.thumbnail);
            tenantIDNo = view.findViewById(R.id.tenantIDNo);
            amount = view.findViewById(R.id.amount);
            paymentDate = view.findViewById(R.id.paymentDate);
            moreActions = view.findViewById(R.id.moreActions);
            rowBackground = view.findViewById(R.id.rowBackground);
            paidVerified = view.findViewById(R.id.paidVerified);



            moreActions.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    // send selected tenant in callback
                    listener.onTenantSelected(tenantListFiltered.get(getAdapterPosition()));
                }
            });
        }
    }


    public defaulterCA(Context context, List<defaulterDMC> tenantList, TenantssAdapterListener listener) {
        this.context = context;
        this.listener = listener;
        this.tenantList = tenantList;
        this.tenantListFiltered = tenantList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.defaulters_view, parent, false);

        return new MyViewHolder(itemView);
    }

    @SuppressLint("ResourceAsColor")
    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {
        final defaulterDMC tenants = tenantListFiltered.get(position);
        holder.name.setText(tenants.getTenantname());
        //holder.phone.setText("House No : "+tenants.getHouseNo());
        holder.amount.setText("Ksh."+tenants.getAmount());
        holder.tenantIDNo.setText("ID No : "+tenants.getTenantid());
        holder.phone.setText("Phone No : "+tenants.getHouseno());
        holder.paymentDate.setText("Due date : "+tenants.getPaymentdate());

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
                    List<defaulterDMC> filteredList = new ArrayList<>();
                    for (defaulterDMC row : tenantList) {

                        // name match condition. this might differ depending on your requirement
                        // here we are looking for name or phone number match
                        if (row.getTenantid().toLowerCase().contains(charString.toLowerCase())
                                || row.getHouseno().contains(charSequence)|| row.getTenantid().contains(charSequence)
                                || row.getTenantname().contains(charSequence)
                                || row.getPaymentdate().contains(charSequence)) {
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
                tenantListFiltered = (ArrayList<defaulterDMC>) filterResults.values;
                notifyDataSetChanged();
            }
        };
    }

    public interface TenantssAdapterListener {
        void onTenantSelected(defaulterDMC tenantS);
    }
}