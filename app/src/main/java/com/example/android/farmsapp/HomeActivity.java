package com.example.android.farmsapp;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.graphics.Color;
import android.os.Handler;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Set;
import java.util.UUID;

public class HomeActivity extends AppCompatActivity {

    private ImageView colorBack,colorPreview,colorStatus,lengthBack,lengthStatus,moistureStatus,moistureBack;
    private TextView colorOpen,color,lengthOpen,length,moisture,moistureOpen;
    private ConstraintLayout colorLayout,lengthLayout,moistureLayout;
    private String address,openMoisture = "7", openDiameter = "6",openColor = "8",currentOption = "",text = "",closeSignal = "0";
    private BluetoothAdapter myBluetoothAdapter;
    private BluetoothSocket bluetoothSocket;
    private static final UUID myUUID =  UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    private SendReceive sendReceive;
    private Handler handler;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        connectBluetooth();
        setupView();
        activateListeners();
    }

    private void connectBluetooth() {

        address = getIntent().getStringExtra("bluetooth_address");
        myBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        BluetoothDevice bt = myBluetoothAdapter.getRemoteDevice(address);

        try{

            bluetoothSocket = bt.createInsecureRfcommSocketToServiceRecord(myUUID);
            bluetoothSocket.connect();

            sendReceive = new SendReceive(bluetoothSocket);
            sendReceive.start();

            Toast.makeText(this, "Bluetooth Connected!", Toast.LENGTH_SHORT).show();


        }catch(IOException e){
            e.printStackTrace();
        }

    }

    private void activateListeners() {

        colorOpen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                sendReceive.write(openColor.getBytes());
                currentOption = openColor;
                colorLayout.setVisibility(View.VISIBLE);

            }
        });

        colorBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                sendReceive.write(closeSignal.getBytes());
                currentOption = closeSignal;
                colorLayout.setVisibility(View.GONE);

            }
        });

        lengthOpen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                sendReceive.write(openDiameter.getBytes());
                currentOption = openDiameter;
                lengthLayout.setVisibility(View.VISIBLE);

            }
        });

        lengthBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                sendReceive.write(closeSignal.getBytes());
                currentOption = closeSignal;
                lengthLayout.setVisibility(View.GONE);

            }
        });

        moistureOpen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Log.d("SERIAL_CHECK","MOISTURE OPEN!");
                sendReceive.write(openMoisture.getBytes());
                currentOption = openMoisture;
                moistureLayout.setVisibility(View.VISIBLE);

            }
        });

        moistureBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                sendReceive.write(closeSignal.getBytes());
                currentOption = closeSignal;
                moistureLayout.setVisibility(View.GONE);

            }
        });


    }

    private void setupView() {

        colorBack = (ImageView) findViewById(R.id.imageViewColorBack);
        colorLayout = (ConstraintLayout) findViewById(R.id.constraintLayoutColor);
        colorOpen = (TextView) findViewById(R.id.textViewHomeColor);
        colorPreview = (ImageView) findViewById(R.id.imageViewColorPreview);
        color = (TextView) findViewById(R.id.textViewColorColor);
        colorStatus = (ImageView) findViewById(R.id.imageViewColorStatus);

        lengthBack = (ImageView) findViewById(R.id.imageViewLengthBack);
        lengthStatus = (ImageView) findViewById(R.id.imageViewLengthStatus);
        length = (TextView) findViewById(R.id.textViewLengthLength);
        lengthLayout = (ConstraintLayout) findViewById(R.id.constraintLayoutLength);
        lengthOpen = (TextView) findViewById(R.id.textViewHomeLength);

        moistureLayout = (ConstraintLayout) findViewById(R.id.constraintLayoutMoisture);
        moistureBack = (ImageView) findViewById(R.id.imageViewMoistureBack);
        moistureStatus = (ImageView) findViewById(R.id.imageViewMoistureStatus);
        moisture = (TextView) findViewById(R.id.textViewMoistureMoisture);
        moistureOpen = (TextView) findViewById(R.id.textViewHomeMoisture);

        handler = new Handler() {
            public void handleMessage(android.os.Message msg) {
                switch (msg.what) {
                    case 1:
                        byte[] readBuff = (byte[]) msg.obj;
                        String tempMessage = new String(readBuff, 0, msg.arg1);
                        text += tempMessage;

                        if(text.charAt(text.length() - 1) == '>'){

                            if(currentOption.equals(openColor)){

                                String dummyText = "",red = "", green = "", blue = "";

                                for (int x = 0; x < text.length(); x++) {

                                    if (text.charAt(x) == '<') {
                                        dummyText = "";
                                    } else if (text.charAt(x) == ':') {

                                        red = dummyText;
                                        dummyText = "";

                                    } else if (text.charAt(x) == ';') {

                                        green = dummyText;
                                        dummyText = "";

                                    } else if (text.charAt(x) == '>') {

                                        blue = dummyText;
                                        dummyText = "";

                                    } else {
                                        dummyText += text.charAt(x);
                                    }

                                }

                                Log.d("SERIAL_CHECK","GREEN IS " + green);
                                Log.d("SERIAL_CHECK","RED IS " + red);


                                String redText = Integer.toHexString(Integer.parseInt(red));
                                String greenText = Integer.toHexString(Integer.parseInt(green));
                                String blueText = Integer.toHexString(Integer.parseInt(blue));

                                String colorText = "#" + redText + greenText + blueText;
                                Log.d("SERIAL_CHECK","COLOR TEXT IS " + colorText);


                                if(colorText.length() == 7) {

                                    if (Integer.parseInt(green) < Integer.parseInt(red)) {
                                        colorStatus.setImageResource(R.drawable.home_ripe);
                                    } else
                                        colorStatus.setImageResource(R.drawable.home_unripe);

                                    color.setText(colorText);

                                    int bgColor = Color.parseColor(colorText);
                                    colorPreview.setBackgroundColor(bgColor);

                                }


                            }else if(currentOption.equals(openDiameter)){
                                String diameterText = "";
                                for(int x = 1; x < text.length() - 1;x++){
                                    diameterText += text.charAt(x);
                                }
                                length.setText(diameterText + " cm");

                                if(diameterText.length() <= 3 && !diameterText.contains("<") && !diameterText.contains(">") && diameterText.length() > 0) {

                                    if(diameterText.length() >= 3) {

                                        if (!(diameterText.charAt(2) == '-')) {

                                            if (Double.parseDouble(diameterText) >= 2.7) {

                                                lengthStatus.setImageResource(R.drawable.home_ripe);

                                            } else {

                                                lengthStatus.setImageResource(R.drawable.home_unripe);

                                            }

                                        }
                                    }
                                }

                            } else if (currentOption.equals(openMoisture)) {

                                Log.d("SERIAL_CHECK","IN MOISTURE!");

                                String moistureText = "";
                                for(int x = 1; x < text.length() - 1; x++){
                                    moistureText += text.charAt(x);
                                }
                                moisture.setText(moistureText + "%");

                                if(moistureText.length() <= 5) {

                                    if (Integer.parseInt(moistureText) >= 60) {
                                        moistureStatus.setImageResource(R.drawable.moisture_humid);
                                    } else
                                        moistureStatus.setImageResource(R.drawable.moisture_dry);
                                }

                            }

                            text = "";
                        }

                        /*



                        Log.d("SERIAL_CHECK","IN HANDLE");

                        if(tempMessage.equals("<")){
                            text = "";
                        }

                        if(tempMessage.equals(">")){
                            text += tempMessage;

                            Log.d("SERIAL_CHECK","TEXT OUTSIDE IS " + text);

                            if(currentOption.equals(openDiameter)){
                                String diameterText = "";
                                for(int x = 1; x < text.length() - 1;x++){
                                    diameterText += text.charAt(x);
                                }
                                length.setText(diameterText + " cm");
                            } else if (currentOption.equals(openMoisture)) {

                                Log.d("SERIAL_CHECK","IN MOISTURE!");

                                String moistureText = "";
                                for(int x = 1; x < text.length() - 1; x++){
                                    moistureText += text.charAt(x);
                                }
                                moisture.setText(moistureText + "%");

                            }else if(currentOption.equals(openColor)){

                                int red = 0,green = 0,blue = 0;
                                String tempColor = "";

                                Log.d("SERIAL_CHECK","TEXT INSIDE COLOR IS " + text);

                                for(int x = 1; x < text.length() - 1; x++){

                                    if(text.charAt(x) == ':'){
                                        red = Integer.parseInt(tempColor);
                                        tempColor = "";
                                    }else if(text.charAt(x) == ';'){
                                        green = Integer.parseInt(tempColor);
                                        tempColor = "";
                                    }else if(text.charAt(x) == '>'){
                                        blue = Integer.parseInt(tempColor);
                                        tempColor = "";
                                    }
                                    else if(text.charAt(x) != '<' && text.charAt(x) != '>'){
                                        tempColor += text.charAt(x);
                                    }

                                }
                                String redText = Integer.toHexString(red);
                                String greenText = Integer.toHexString(green);
                                String blueText = Integer.toHexString(blue);

                                String colorText = "#" + redText + greenText + blueText;
                                color.setText(colorText);
                                Log.d("SERIAL_CHECK","COLOR IS " + colorText);

                            }


                            text = "";

                        }else{
                            text += tempMessage;
                        }

                        */


                }
            }
        };

    }

    public class SendReceive extends Thread{

        InputStream inputStream;
        OutputStream outputStream;
        BluetoothSocket socket;

        public SendReceive(BluetoothSocket socket){

            this.socket = socket;
            try {
                inputStream = socket.getInputStream();
                outputStream = socket.getOutputStream();
            }catch (Exception e){
                e.printStackTrace();
            }

        }

        public void run(){
            byte[] buffer = new byte[256];
            int bytes;

            while(true){
                try{

                    bytes = inputStream.read(buffer);
                    handler.obtainMessage(1,bytes,-1,buffer).sendToTarget();

                }catch (Exception e){
                    break;
                }
            }

        }

        public void write(byte[] bytes){
            try{

                outputStream.write(bytes);

            }catch(Exception e){
                e.printStackTrace();
            }
        }
    }
}
