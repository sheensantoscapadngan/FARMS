package com.example.android.farmsapp;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Set;
import java.util.UUID;

public class BluetoothActivity extends AppCompatActivity implements BluetoothPageAdapter.ConnectCallback{

    private RecyclerView recyclerView;
    private ImageView back;
    private BluetoothPageAdapter adapter;
    private ArrayList<String> nameList,addressList;
    private BluetoothAdapter myBluetoothAdapter;
    private BluetoothSocket bluetoothSocket;
    private Set<BluetoothDevice> deviceList;
    private static final UUID myUUID =  UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bluetooth);

        setupViews();
        activateListeners();
        setupRecyclerView();
        setupBluetooth();
        loadPairedDevices();
    }

    private void setupRecyclerView() {

        adapter = new BluetoothPageAdapter(nameList,addressList,this);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

    }

    private void activateListeners() {

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                finish();

            }
        });


    }

    private void setupViews() {

        recyclerView = (RecyclerView) findViewById(R.id.recyclerViewBluetooth);
        back = (ImageView) findViewById(R.id.imageViewBluetoothBack);

        nameList = new ArrayList<>();
        addressList = new ArrayList<>();
    }

    private void setupBluetooth() {

        myBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        deviceList = myBluetoothAdapter.getBondedDevices();

    }

    private void loadPairedDevices() {

        if(deviceList.size() > 0){
            for(BluetoothDevice device : deviceList){
                addressList.add(device.getAddress());
                nameList.add(device.getName());
            }
            adapter.notifyDataSetChanged();
        }

    }


    @Override
    public void notifyBluetoothConnect(String address) {

        Intent intent = new Intent(this,HomeActivity.class);
        intent.putExtra("bluetooth_address",address);
        startActivity(intent);

    }
}
