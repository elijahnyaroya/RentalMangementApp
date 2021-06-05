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
import com.elisuntech.rentalmanagementapp2.DMC.MessageContactListDMC;
import com.elisuntech.rentalmanagementapp2.R;

import java.util.ArrayList;
import java.util.List;

public class MessageContactListCA extends RecyclerView.Adapter<MessageContactListCA.MyViewHolder>
        implements Filterable {
private Context context;
private List<MessageContactListDMC> tenantList;
private List<MessageContactListDMC> tenantListFiltered;
private TenantssAdapterListener listener;

public class MyViewHolder extends RecyclerView.ViewHolder {
    public TextView name, phone,roomNo,messageCounter;
    public ImageView thumbnail;
    public RelativeLayout moreActions;
    public LinearLayout rowBackground;

    public MyViewHolder(View view) {
        super(view);
        name = view.findViewById(R.id.name);
        phone = view.findViewById(R.id.phone);
        messageCounter = view.findViewById(R.id.messageCounter);
        roomNo = view.findViewById(R.id.roomNo);
        thumbnail = view.findViewById(R.id.thumbnail);
        moreActions = view.findViewById(R.id.moreActions);

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // send selected contact in callback
                listener.onTenantSelected(tenantListFiltered.get(getAdapterPosition()));
            }
        });

    }
}


    public MessageContactListCA(Context context, List<MessageContactListDMC> tenantList,TenantssAdapterListener listener) {
        this.context = context;
        this.listener = listener;
        this.tenantList = tenantList;
        this.tenantListFiltered = tenantList;

    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.message_contact_view, parent, false);

        return new MyViewHolder(itemView);
    }

    @SuppressLint("ResourceAsColor")
    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {
        final MessageContactListDMC tenants = tenantListFiltered.get(position);
        holder.name.setText(tenants.getName());
       if (tenants.getIsRead().equals("no")){
           holder.messageCounter.setText(tenants.getMessageCount());
       }else{
           holder.moreActions.setVisibility(View.GONE);
           holder.messageCounter.setText(tenants.getMessageCount());
       }
        holder.phone.setText("Phone No : "+tenants.getPhoneNo());
        holder.roomNo.setText("Room No : "+tenants.getRoomNo());

        Glide.with(context)
                .load(tenants.getImage())
                .apply(RequestOptions.circleCropTransform())
                .into(holder.thumbnail);
        //listener.onTenantSelected(tenantListFiltered.get(position));
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
                    List<MessageContactListDMC> filteredList = new ArrayList<>();
                    for (MessageContactListDMC row : tenantList) {

                        // name match condition. this might differ depending on your requirement
                        // here we are looking for name or phone number match
                        if (row.getName().toLowerCase().contains(charString.toLowerCase())
                                || row.getRoomNo().contains(charSequence)|| row.getPhoneNo().contains(charSequence)
                                || row.getMessageCount().contains(charSequence)
                                || row.getPhoneNo().contains(charSequence)) {
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
                tenantListFiltered = (ArrayList<MessageContactListDMC>) filterResults.values;
                notifyDataSetChanged();
            }
        };
    }

public interface TenantssAdapterListener {
    void onTenantSelected(MessageContactListDMC tenantS);
}
}