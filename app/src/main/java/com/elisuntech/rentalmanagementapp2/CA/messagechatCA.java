package com.elisuntech.rentalmanagementapp2.CA;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;

import com.elisuntech.rentalmanagementapp2.DMC.messagechatDMC;
import com.elisuntech.rentalmanagementapp2.R;
import com.elisuntech.rentalmanagementapp2.commonMethods.sharedPreference;

import java.util.ArrayList;

public class messagechatCA extends RecyclerView.Adapter<messagechatCA.facilityCAViewHolder>{

    private Context mContext;
    private Activity activity;
    private  String activityfrom;
    private ArrayList<messagechatDMC> facilityDCMList;

    public static int counterNo ;
    private messagechatCA.OnItemClickListener mListener;

    public interface OnItemClickListener {
        void onItemClick(int position);

    }

    public void setOnItemClickListener(messagechatCA.OnItemClickListener listener){
        mListener = listener;
    }


    public messagechatCA(Context context, ArrayList<messagechatDMC> ListfacilityDCMList){
        mContext = context;
        facilityDCMList = ListfacilityDCMList;
    }

    @Override
    public facilityCAViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(mContext).inflate(R.layout.chat_message_listvew,parent,false);
        return new facilityCAViewHolder(v);
    }

//TODO ensure that when registering LANDLOARDS you generate their unique ids that will not bring conflict as it is now
    //TODO do not use auto generted ID as their unique id from the database

    @SuppressLint("ResourceAsColor")
    @Override
    public void onBindViewHolder(final facilityCAViewHolder holder, final int position) {
        final messagechatDMC currentitem = facilityDCMList.get(position);
        counterNo = getItemCount()-1;
        System.out.println("AAAAAAA"+currentitem.getMessage());
        System.out.println("BBB"+currentitem.getMessageID());
        System.out.println("CCCCC"+currentitem.getTenantID());
        System.out.println("DDD"+currentitem.getSender());


        String userLoggedinID = sharedPreference.getIsLoggedIn();

        if (userLoggedinID.equals(currentitem.getTenantID())){
            holder.text_message_outgoing.setVisibility(View.VISIBLE);
            holder.text_message_outgoing.setText(currentitem.getMessage());
        }else{
            holder.text_message_incoming.setVisibility(View.VISIBLE);
            holder.text_message_incoming.setText(currentitem.getMessage());
        }


    }

    @Override
    public int getItemCount() {
        return facilityDCMList.size();
    }

    public class facilityCAViewHolder extends RecyclerView.ViewHolder{

        public TextView text_message_outgoing ;
        public TextView text_message_incoming ;

        public facilityCAViewHolder(View itemView) {
            super(itemView);


            text_message_outgoing =itemView.findViewById(R.id.text_message_outgoing);
            text_message_incoming =itemView.findViewById(R.id.text_message_incoming);

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

