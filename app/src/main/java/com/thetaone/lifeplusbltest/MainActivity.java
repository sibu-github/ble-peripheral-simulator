package com.thetaone.lifeplusbltest;

import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import android.widget.TextView;
import android.widget.Toast;
import java.util.ArrayList;
import java.util.Date;
import java.util.Set;
import java.util.UUID;

public class MainActivity extends AppCompatActivity {
    Button b1,b2,b3,b4;
    TextView textView;
    private BluetoothAdapter BA;
    private Set<BluetoothDevice> pairedDevices;
    private BluetoothDevice[] btArray = new BluetoothDevice[10];
    ListView lv;
    final String APP_NAME="LifePlus";
    final UUID serverUUID = UUID.fromString("313d03b4-0eab-4868-a038-8580f0a8736b");
    Handler handler;
    SendReceive sendReceive;
    static final int STATE_LISTENING = 0, STATE_CONNECTING = 2, STATE_CONNECTED = 3, STATE_CONNECTION_FAILED = 4, STATE_MSG_RECIEVED = 5, SERVER_STARTING=6,SERVER_STARTED=7;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        b1 = (Button) findViewById(R.id.button);
        b2=(Button)findViewById(R.id.button2);
        b3=(Button)findViewById(R.id.button3);
        b4=(Button)findViewById(R.id.button4);

        BA = BluetoothAdapter.getDefaultAdapter();
        lv = (ListView)findViewById(R.id.listView);
        textView= findViewById(R.id.textView);
        handler = new Handler(new Handler.Callback() {
            @Override
            public boolean handleMessage(Message msg) {
                switch (msg.what){
                    case STATE_CONNECTED:
                        Toast.makeText(getApplicationContext(),"Client connected",Toast.LENGTH_SHORT).show();
                        break;
                    case SERVER_STARTED:
                        Toast.makeText(getApplicationContext(),"Server started",Toast.LENGTH_SHORT).show();
                        break;
                    case SERVER_STARTING:
                        Toast.makeText(getApplicationContext(),"Server starting..",Toast.LENGTH_SHORT).show();
                        break;
                    case STATE_MSG_RECIEVED:
                        byte[] buffer = (byte[]) msg.obj;
//                        String message = new String(buffer,0,msg.arg1);
//                        textView.setText(message);
//                        print("recieved msg: "+ message);

                        print("in STATE_MSG_RECIEVED");
                        if(buffer[0] == 0 &&  buffer.length > 1) {
                            // measure now response sending
                            print("buffer[0] == 0");
                            byte[] byteArray = returnMeasureNowResponce();
                            sendReceive.write(byteArray);
                        } else if (buffer[0] == 1  && buffer.length > 1) {
                            // clibration now response sending
                            print("buffer[0] == 1");
                            try {
                                byte[] returnArray = new byte[26*60*20];
                                int i=0;
                                long startTime = System.currentTimeMillis();
                                while (startTime+60000 > System.currentTimeMillis() ){
                                    Thread.sleep((int)1000/26);
                                    //----
                                     byte[] tempArray = returnMeasureNowResponce();
                                     for(byte b: tempArray){
                                         returnArray[i++] = b;
                                         print(Byte.toString(b));
                                     }
                                }

                                sendReceive.write(returnArray);

                            }catch (Exception e){
                                e.printStackTrace();
                            }

                        }
                        else {
                            print("in else");
                            StringBuffer stringBuffer = new StringBuffer();
                            for(int i=0;i<msg.arg1;i++){
                                stringBuffer.append(Byte.toString(buffer[i])+", ");
                            }
                            textView.setText(stringBuffer.toString());
                        }
                        break;
                }
                return false;
            }
        });
    }
    void print(String obj){
        System.out.println(obj);
    }
    public void on(View v){
        if (!BA.isEnabled()) {
            Intent turnOn = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(turnOn, 0);
            Toast.makeText(getApplicationContext(), "Turned on",Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(getApplicationContext(), "Already on", Toast.LENGTH_LONG).show();
        }
    }

    public void off(View v){
        BA.disable();
        Toast.makeText(getApplicationContext(), "Turned off" ,Toast.LENGTH_LONG).show();
    }


    public  void visible(View v){
        Intent getVisible = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
        startActivityForResult(getVisible, 0);
    }

    public void sendZero(View v){
        try{
            sendReceive.write(new byte[]{0});
            Toast.makeText(getApplicationContext(),"Zero sent to server",Toast.LENGTH_SHORT).show();
        }catch (Exception e){
            Toast.makeText(getApplicationContext(),"server not connected",Toast.LENGTH_SHORT).show();
        }

    }

    public void sendOne(View v){
        try{
            sendReceive.write(new byte[]{1});
            Toast.makeText(getApplicationContext(),"One sent to server",Toast.LENGTH_SHORT).show();
        }catch (Exception e){
            Toast.makeText(getApplicationContext(),"server not connected",Toast.LENGTH_SHORT).show();
        }
    }


    public void list(View v){
        pairedDevices = BA.getBondedDevices();
        ArrayList list = new ArrayList();
        int i=0;
        for(BluetoothDevice bt : pairedDevices){
            list.add(bt.getName());
            btArray[i] = bt;
            i++;
        }
        Toast.makeText(getApplicationContext(), "Showing Paired Devices",Toast.LENGTH_SHORT).show();

        final ArrayAdapter adapter = new ArrayAdapter(this,android.R.layout.simple_list_item_1, list);

        lv.setAdapter(adapter);
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                try {
                    ClientClass clientClass = new ClientClass(btArray[i]);
                    clientClass.start();
                    Toast.makeText(getApplicationContext(),"Server connected",Toast.LENGTH_LONG).show();
                }catch (Exception e){
                    Toast.makeText(getApplicationContext(),e.getMessage(),Toast.LENGTH_LONG).show();
                }
            }
        });
    }
    private class ServerClass extends Thread{
        private BluetoothServerSocket serverSocket;
        public ServerClass(){
            try {
                Log.d("Server","starting...");
                System.out.println("Server | starting...");
                handler.obtainMessage(SERVER_STARTING).sendToTarget();
                serverSocket = BA.listenUsingInsecureRfcommWithServiceRecord(APP_NAME,serverUUID);
                handler.obtainMessage(SERVER_STARTED).sendToTarget();
                Log.d("Server","started");
                System.out.println("Server | started...");
            }catch (IOException e){
                e.printStackTrace();
                Log.d("Server","start failed");
                System.out.println("Server | start failed...");
            }
        }
        @Override
        public void run() {
            super.run();
            print("server run method");
            BluetoothSocket connSocket = null;
            while(connSocket==null){
                Log.d("Server","in loop");
                try{
                    connSocket = serverSocket.accept();
                    if(connSocket!=null){
                        Log.d("Server","Connected");
                        System.out.println("Server | Connected...");
                        sendReceive = new SendReceive(connSocket);
                        sendReceive.start();
                        handler.obtainMessage(STATE_CONNECTED).sendToTarget();
                        break;
//                    Toast.makeText(getApplicationContext(),"Connected",Toast.LENGTH_LONG).show();
                    }
                    else{
                        Log.d("Server","Connection failed");
                        System.out.println("Server | Connection failed...");
//                    Toast.makeText(getApplicationContext(),"Connection Failed",Toast.LENGTH_LONG).show();
                    }
                }catch (IOException e){
                    e.printStackTrace();
                    Log.d("Server","Connection failed");
                    System.out.println("Server | Connection failed...");
                }


            }
        }
    }

    private  byte[]  returnMeasureNowResponce () {
        ArrayList<byte[]> data = new ArrayList<>();
        byte[] byteArray = new byte[20];
        int timeStamp = (int) new Date().getTime();
        int heartRate = (int) (Math.random()* 126);
        int respirationRate = (int) (Math.random()* 127);
        int spo2 = (int) (Math.random()* 127);
        int sbp = (int) (Math.random() * 16777216);
        int dbp = (int) (Math.random() * 16777216);
        int bg = (int) (Math.random() * 65535);
        int motion = (int) (Math.random() * 2147483647);
        int postamble = (int) (Math.random()* 127);

        byte[] returnData = addData(timeStamp, 4);
        data.add(returnData);

        byte[] returnData1 = addData(heartRate, 1);
        data.add(returnData1);

        byte[] returnData2 = addData(respirationRate, 1);
        data.add(returnData2);

        byte[] returnData3 = addData(spo2, 1);
        data.add(returnData3);

        byte[] returnData4 = addData(sbp, 3);
        data.add(returnData4);

        byte[] returnData5 = addData(dbp, 3);
        data.add(returnData5);

        byte[] returnData6 = addData(bg, 2);
        data.add(returnData6);

        byte[] returnData7 = addData(motion, 4);
        data.add(returnData7);

        byte[] returnData8 = addData(postamble, 1);
        data.add(returnData8);
        int i = 0;
        for( byte[] arrayElement : data ) {
            for(int j=0;j<arrayElement.length;j++){
                byteArray[i++] = arrayElement[j];
            }
        }
        return byteArray;
    }

    private class ClientClass extends Thread{
        private BluetoothSocket socket;
        private BluetoothDevice device;
        public ClientClass(BluetoothDevice device1){
            device = device1;
            try{
                socket = device1.createRfcommSocketToServiceRecord(serverUUID);
            }catch (IOException e){
                e.printStackTrace();
            }
        }

        @Override
        public void run() {
            super.run();
            try {
                Log.d("Client","Connecting..");
                socket.connect();
                if(socket.isConnected()){
                    Log.d("Client","Connected");
                    sendReceive = new SendReceive(socket);
                    sendReceive.start();
                    }
//
//                Toast.makeText(getApplicationContext(),"Connected",Toast.LENGTH_LONG).show();
            }catch (IOException e){
                e.printStackTrace();
                Log.d("Client","Connection failed");
//                Toast.makeText(getApplicationContext(),"Connection Failed",Toast.LENGTH_LONG).show();
            }
        }
    }

    public  byte[] addData ( int value, int length )
    {
        byte[] filteredData = new byte[length];
        ByteBuffer byteBuffer = ByteBuffer.allocate(4);
        byteBuffer.putInt(value);
        int skippingFactor = 4-length;
        int increment = 0,i=0;
        for ( byte a : byteBuffer.array() ) {
            if (increment >= skippingFactor) {
                filteredData[i++] = a;
            }
            increment++;
        }
        return filteredData;
    }

    public void startServer(View v){
        ServerClass server = new ServerClass();
        server.start();
    }

    private class SendReceive extends Thread{
        private final BluetoothSocket socket;
        private final InputStream inputStream;
        private final OutputStream outputStream;

        public SendReceive(BluetoothSocket socket1){
            socket = socket1;
            InputStream tempIn = null;
            OutputStream tempOut = null;
            try{
                tempIn = socket.getInputStream();
                tempOut = socket.getOutputStream();
            }catch (Exception e){
            }
            inputStream = tempIn;
            outputStream = tempOut;
        }

        @Override
        public void run() {
            super.run();
            print("Something received.");
            while (true){
                byte[] buffer = new byte[1024];
                int bytes;
                try {
                    bytes = inputStream.read(buffer);
                    print("received bytes = "+ Integer.toString(bytes));
                    handler.obtainMessage(STATE_MSG_RECIEVED,bytes,-1,buffer).sendToTarget();
                }catch (Exception e){
                    print("Error in SendRecieve run method");
                    e.printStackTrace();
                }
            }

        }
        public void write(byte[] buffer){
            try{
                outputStream.write(buffer);
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }
}
