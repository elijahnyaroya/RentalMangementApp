package com.elisuntech.rentalmanagementapp2.CA;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;


import com.elisuntech.rentalmanagementapp2.DMC.tenanthistoryDMC;
import com.elisuntech.rentalmanagementapp2.PaymentDefaulters.otherspaymentHistory;
import com.elisuntech.rentalmanagementapp2.PaymentDefaulters.tenantpaymentHistory;
import com.elisuntech.rentalmanagementapp2.PaymentDefaulters.waterPaymentHistory;
import com.elisuntech.rentalmanagementapp2.R;

import java.util.ArrayList;

public class tenanthistoryCA extends RecyclerView.Adapter<tenanthistoryCA.facilityCAViewHolder>{

    private Context mContext;
    private  Activity activity;
    private  String activityfrom;
    private ArrayList<tenanthistoryDMC> facilityDCMList;

    private tenanthistoryCA.OnItemClickListener mListener;

    public interface OnItemClickListener {
        void onItemClick(int position);

    }

    public void setOnItemClickListener(tenanthistoryCA.OnItemClickListener listener){
        mListener = listener;
    }

    public tenanthistoryCA(String actityFrom,Activity Wctivity,Context context, ArrayList<tenanthistoryDMC> ListfacilityDCMList){
        mContext = context;
        activity = Wctivity;
        activityfrom = actityFrom;
        facilityDCMList = ListfacilityDCMList;
    }

    @Override
    public facilityCAViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(mContext).inflate(R.layout.paymenthistory_view,parent,false);
        return new facilityCAViewHolder(v);
    }



    @SuppressLint("ResourceAsColor")
    @Override
    public void onBindViewHolder(final facilityCAViewHolder holder, final int position) {
        final tenanthistoryDMC currentitem = facilityDCMList.get(position);

        System.out.println("AAAAAAA"+currentitem.getPaidamount());
        System.out.println("BBB"+currentitem.getDayspassed());
        System.out.println("CCCCC"+currentitem.getAmount());
        System.out.println("DDDD"+currentitem.getDatedue());
        System.out.println("EEEE"+currentitem.getId());

        if (currentitem.getDayspassed().equals("null")){
            holder.dayspassed.setText("Days passed : 0 Days");
        }else{
            holder.dayspassed.setText("Days passed : "+currentitem.getDayspassed()+" Days");
        }


        holder.dateDue.setText("Due Date : "+currentitem.getDatedue());
        holder.Amountpayable.setText("Amount : Ksh. "+currentitem.getAmount());

        holder.makePaymentdefaulter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tenantpaymentHistory obj = new tenantpaymentHistory();
                waterPaymentHistory obj1 = new waterPaymentHistory();
                otherspaymentHistory obj2 = new otherspaymentHistory();
                switch (activityfrom){
                    case "tenantpaymentHistory":
                        obj.makepayments( currentitem.getPaidamount(), currentitem.getId(), currentitem.getAmount(),activity );
                        break;
                    case  "waterPaymentHistory":
                         obj1.makepayments(currentitem.getPaidamount(), currentitem.getId(), currentitem.getAmount(),activity );
                        break;
                    case "otherspaymentHistory":
                        obj2.makepayments(currentitem.getPaidamount(), currentitem.getId(), currentitem.getAmount(),activity );
                        break;
                }

            }
        });




    }

    @Override
    public int getItemCount() {
        return facilityDCMList.size();
    }

    public class facilityCAViewHolder extends RecyclerView.ViewHolder{

        public TextView dayspassed ;
        public TextView dateDue ;
        public TextView Amountpayable ;
        public ImageView makePaymentdefaulter ;
        public facilityCAViewHolder(View itemView) {

            super(itemView);
            dayspassed =itemView.findViewById(R.id.dayspassed);
            dateDue =itemView.findViewById(R.id.dateDue);
            Amountpayable =itemView.findViewById(R.id.Amountpayable);
            makePaymentdefaulter =itemView.findViewById(R.id.makePaymentdefaulter);


            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mListener !=null){
                        int position = getAdapterPosition();
                        if(position != RecyclerView.NO_POSITION){
                            mListener.onItemClick(position);

                        }
                    }
                }
            });

        }
    }
}

