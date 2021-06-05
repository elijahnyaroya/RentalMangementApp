package com.elisuntech.rentalmanagementapp2.CA;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.elisuntech.rentalmanagementapp2.DMC.buysellDMC;
import com.elisuntech.rentalmanagementapp2.R;

import java.util.ArrayList;
import java.util.List;

public class buysellCA extends RecyclerView.Adapter<buysellCA.MyViewHolder>
        implements Filterable {
private Context context;
private List<buysellDMC> tenantList;
private List<buysellDMC> tenantListFiltered;
private TenantssAdapterListener listener;

public class MyViewHolder extends RecyclerView.ViewHolder {
    public TextView  productName,Location,price;
    public ImageView productImage;

    public MyViewHolder(View view) {
        super(view);
        productImage = view.findViewById(R.id.productImage);
        productName = view.findViewById(R.id.productName);
        Location = view.findViewById(R.id.Location);
        price = view.findViewById(R.id.price);




        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // send selected tenant in callback
                listener.onTenantSelected(tenantListFiltered.get(getAdapterPosition()));
            }
        });
    }
}


    public buysellCA(Context context, List<buysellDMC> tenantList, TenantssAdapterListener listener) {
        this.context = context;
        this.listener = listener;
        this.tenantList = tenantList;
        this.tenantListFiltered = tenantList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.buyandsell_listview, parent, false);

        return new MyViewHolder(itemView);
    }

    @SuppressLint("ResourceAsColor")
    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {
        final buysellDMC tenants = tenantListFiltered.get(position);

        holder.productName.setText(tenants.getName());
        holder.Location.setText(tenants.getLocation());
        holder.price.setText("Ksh. "+tenants.getPrice());

        Glide.with(context)
                .load(tenants.getImage())
                .apply(RequestOptions.centerInsideTransform())
                .into(holder.productImage);


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
                    List<buysellDMC> filteredList = new ArrayList<>();
                    for (buysellDMC row : tenantList) {
                        if (row.getName().toLowerCase().contains(charString.toLowerCase())
                                || row.getPrice().contains(charSequence)|| row.getLocation().contains(charSequence)
                                || row.getCategory().contains(charSequence)
                                || row.getTime().contains(charSequence)) {
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
                tenantListFiltered = (ArrayList<buysellDMC>) filterResults.values;
                notifyDataSetChanged();
            }
        };
    }

public interface TenantssAdapterListener {
    void onTenantSelected(buysellDMC tenantS);
}
}