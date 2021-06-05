package com.elisuntech.rentalmanagementapp2.CA;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;

import com.elisuntech.rentalmanagementapp2.DMC.termsandcondition_aboutDMC;
import com.elisuntech.rentalmanagementapp2.R;

import java.util.List;


public class termsandcondition_aboutCA  extends RecyclerView.Adapter<termsandcondition_aboutCA.MyViewHolder> {
    private Context context;
    private List<termsandcondition_aboutDMC> tenantList;
    private List<termsandcondition_aboutDMC> tenantListFiltered;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView description ;
        public TextView title ;


        public MyViewHolder(View view) {
            super(view);
            description =itemView.findViewById(R.id.description);
            title =itemView.findViewById(R.id.title);

        }
    }


    public termsandcondition_aboutCA(Context context, List<termsandcondition_aboutDMC> tenantList) {
        this.context = context;
        this.tenantList = tenantList;
        this.tenantListFiltered = tenantList;
    }

    @Override
    public termsandcondition_aboutCA.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.termsandcondition_about_listview, parent, false);

        return new termsandcondition_aboutCA.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(termsandcondition_aboutCA.MyViewHolder holder, final int position) {
        final termsandcondition_aboutDMC currentitem = tenantListFiltered.get(position);


        holder.description.setText(currentitem.getDescription());
        holder.title.setText(currentitem.getTitle());

    }

    @Override
    public int getItemCount() {
        return tenantListFiltered.size();
    }

}