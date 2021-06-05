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
import com.elisuntech.rentalmanagementapp2.DMC.watermeterReadingDMC;
import com.elisuntech.rentalmanagementapp2.R;

import java.util.ArrayList;
import java.util.List;


public class watermeterReadingCA extends RecyclerView.Adapter<watermeterReadingCA.MyViewHolder>
        implements Filterable {
    private Context context;
    private List<watermeterReadingDMC> tenantList;
    private List<watermeterReadingDMC> tenantListFiltered;
    private meterReadingAdapterListener listener;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView name, phone,tenantIDNo,meterNo,houseNo;
        TextView readmeter;
        public ImageView thumbnail;
        public RelativeLayout moreActions;
        public LinearLayout rowBackground;

        public MyViewHolder(View view) {
            super(view);
            name = view.findViewById(R.id.name);
            phone = view.findViewById(R.id.phone);
            thumbnail = view.findViewById(R.id.thumbnail);
            tenantIDNo = view.findViewById(R.id.tenantIDNo);
            meterNo = view.findViewById(R.id.meterNo);
            houseNo = view.findViewById(R.id.houseNo);
            readmeter = view.findViewById(R.id.readmeter);



            readmeter.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    // send selected tenant in callback
                    listener.onTenantSelected(tenantListFiltered.get(getAdapterPosition()));
                }
            });
        }
    }


    public watermeterReadingCA(Context context, List<watermeterReadingDMC> tenantList, meterReadingAdapterListener listener) {
        this.context = context;
        this.listener = listener;
        this.tenantList = tenantList;
        this.tenantListFiltered = tenantList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.watermeterreading_listview, parent, false);

        return new MyViewHolder(itemView);
    }

    @SuppressLint("ResourceAsColor")
    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {
        final watermeterReadingDMC tenants = tenantListFiltered.get(position);

        //TODO 3333 this data will be pulled monthly that is for every month
        //TODO and those who will not pay in that month will be transferred to defaulters
        holder.name.setText(tenants.getTenantName());
        holder.meterNo.setText("Meter No : "+tenants.getMeterNo());
        holder.tenantIDNo.setText("ID No : "+tenants.getTenantID());
        holder.phone.setText("Phone No : "+tenants.getTenantPhone());
        holder.houseNo.setText("House No : "+tenants.getHouseNo());

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
                    List<watermeterReadingDMC> filteredList = new ArrayList<>();
                    for (watermeterReadingDMC row : tenantList) {

                        // name match condition. this might differ depending on your requirement
                        // here we are looking for name or phone number match
                        if (row.getTenantName().toLowerCase().contains(charString.toLowerCase())
                                || row.getHouseNo().contains(charSequence)|| row.getTenantID().contains(charSequence)
                                || row.getTenantID().contains(charSequence)
                                || row.getTenantPhone().contains(charSequence)|| row.getMeterNo().contains(charSequence)) {
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
                tenantListFiltered = (ArrayList<watermeterReadingDMC>) filterResults.values;
                notifyDataSetChanged();
            }
        };
    }

    public interface meterReadingAdapterListener {
        void onTenantSelected(watermeterReadingDMC tenantS);
    }
}