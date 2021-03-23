package com.otaTest.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.otaTest.CustomOBjects.CustBluetootDevices;
import com.otaTest.R;

import java.net.PortUnreachableException;
import java.util.ArrayList;


public class FragmentScanAdapter extends RecyclerView.Adapter<FragmentScanAdapter.ScanItemViewHolder> {
    private ArrayList<CustBluetootDevices> customBluetoothdevices;
    private Context context;
    private ScanOnItemClickInterface scanOnItemClickInterface;
    public FragmentScanAdapter(ArrayList<CustBluetootDevices> loc_custBluetootDevicesArrayList){
        customBluetoothdevices=loc_custBluetootDevicesArrayList;
    }

    @NonNull
    @Override
    public ScanItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context=parent.getContext();
        View itemView = LayoutInflater.from(context).inflate(R.layout.fragment_scan_item_layout, parent, false);
        return new FragmentScanAdapter.ScanItemViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ScanItemViewHolder scanItemViewHolder, int position) {
        scanItemViewHolder.bindBluetoothDeviceDetails(customBluetoothdevices.get(position),scanItemViewHolder);
    }

    @Override
    public int getItemCount() {
        return customBluetoothdevices.size();
    }

    public class ScanItemViewHolder  extends  RecyclerView.ViewHolder implements View.OnClickListener{
        TextView bleAddress_textView;
        TextView device_name;
        Button connectButton_button,ota_update_button;
        public ScanItemViewHolder(@NonNull View itemView) {
            super(itemView);
            this.bleAddress_textView=(TextView)itemView.findViewById(R.id.ble_address);
            this.connectButton_button=(Button)itemView.findViewById(R.id.connect_button);
            this.ota_update_button=(Button)itemView.findViewById(R.id.ota_button);
            this.device_name=(TextView)itemView.findViewById(R.id.device_name_text);
        }

        @Override
        public void onClick(View itemView) {
            itemView.setOnClickListener(this);
        }

        void bindBluetoothDeviceDetails(CustBluetootDevices custBluetootDevices,ScanItemViewHolder scanItemViewHolder){
            bleAddress_textView.setText(custBluetootDevices.getBleAddress());
            device_name.setText(custBluetootDevices.getDeviceName());
            if(custBluetootDevices.isConnected()){
                connectButton_button.setText("DisConnect");
                connectButton_button.setTextColor(context.getResources().getColor(R.color.connect_color));
                ota_update_button.setVisibility(View.VISIBLE);
            }else if(!(custBluetootDevices.isConnected())){
                connectButton_button.setText("Connecet");
                connectButton_button.setTextColor(context.getResources().getColor(R.color.disconnect_color));
                ota_update_button.setVisibility(View.GONE);
            }

            scanItemViewHolder.connectButton_button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(scanOnItemClickInterface!=null){
                        scanOnItemClickInterface.ClickedItem(custBluetootDevices,getAdapterPosition());
                    }
                }
            });

            scanItemViewHolder.ota_update_button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(scanOnItemClickInterface!=null){
                        scanOnItemClickInterface.otaUpdate(custBluetootDevices,getAdapterPosition());
                    }
                }
            });
        }
    }

    public interface ScanOnItemClickInterface{
        public void ClickedItem(CustBluetootDevices custBluetootDevices,int positionClicked);
        public void otaUpdate(CustBluetootDevices custBluetootDevices,int positionSelected);
    }
    public void setOnItemClickLIstner(ScanOnItemClickInterface loc_scanOnItemClickInterface){
        this.scanOnItemClickInterface=loc_scanOnItemClickInterface;
    }
}
