package com.thetaone.simulator;

import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Date;
import java.util.UUID;

/**
 * Implementation of the Bluetooth GATT Time Profile.
 * https://www.bluetooth.com/specifications/adopted-specifications
 */
public class WatchProfile {
    private static final String TAG = WatchProfile.class.getSimpleName();

    /* Current Time Service UUID */
    public static UUID WATCH_SERVICE_UUID = UUID.fromString("00001805-0000-1000-8000-00805f9b34fb");
    /* Mandatory Current Time Information Characteristic */
    public static UUID MEASURE_NOW_UUID    = UUID.fromString("00002a2b-0000-1000-8000-00805f9b34fb");
    /* Optional Local Time Information Characteristic */
    public static UUID CAL_NOW_UUID = UUID.fromString("00002a0f-0000-1000-8000-00805f9b34fb");
    /* Mandatory Client Characteristic Config Descriptor */
    public static UUID CLIENT_CONFIG = UUID.fromString("00002902-0000-1000-8000-00805f9b34fb");

    /**
     * Return a configured {@link BluetoothGattService} instance for the
     * Current Time Service.
     */
    public static BluetoothGattService createWatchService() {
        BluetoothGattService service = new BluetoothGattService(WATCH_SERVICE_UUID,
                BluetoothGattService.SERVICE_TYPE_PRIMARY);

        // Current Time characteristic
        BluetoothGattCharacteristic measureNowCharacs = new BluetoothGattCharacteristic(MEASURE_NOW_UUID,
                //Read-only characteristic, supports notifications
                BluetoothGattCharacteristic.PROPERTY_READ | BluetoothGattCharacteristic.PROPERTY_NOTIFY,
                BluetoothGattCharacteristic.PERMISSION_READ);
        BluetoothGattDescriptor configDescriptor = new BluetoothGattDescriptor(CLIENT_CONFIG,
                //Read/write descriptor
                BluetoothGattDescriptor.PERMISSION_READ | BluetoothGattDescriptor.PERMISSION_WRITE);
        measureNowCharacs.addDescriptor(configDescriptor);

        // Local Time Information characteristic
        BluetoothGattCharacteristic calNowCharacs = new BluetoothGattCharacteristic(CAL_NOW_UUID,
                //Read-only characteristic
                BluetoothGattCharacteristic.PROPERTY_READ | BluetoothGattCharacteristic.PROPERTY_NOTIFY,
                BluetoothGattCharacteristic.PERMISSION_READ);

        service.addCharacteristic(measureNowCharacs);
        service.addCharacteristic(calNowCharacs);

        return service;
    }

    public static  byte[]  measureNowResponse () {
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

    public static byte[] calNowResponse(){
        try {
            int TOTAL_NUM_OF_PACKETS = 26*60;
            byte[] returnArray = new byte[TOTAL_NUM_OF_PACKETS*20];
            int i=0;
            for(int j=0;j<TOTAL_NUM_OF_PACKETS;j++){
                byte[] tempArray = measureNowResponse();
                for(byte b: tempArray){
                    returnArray[i++] = b;
                }
            }


//            for(byte b: returnArray){
//                System.out.println(b);
//            }
//            long startTime = System.currentTimeMillis();
//            int z=0;
//            while (startTime+ 60000 > System.currentTimeMillis() ){ // 60000
////                Thread.sleep((int)1000/26);
//
//                  if(z<100000){
//                      z++;
//                  }
//                  else{
//                      z=0;
//                      System.out.println("I am alive.");
//                  }
//            }

            return returnArray;

        }catch (Exception e){
            e.printStackTrace();
            System.out.println("cal now execption");
            return null;
        }
    }


 private  static byte[] addData ( int value, int length )
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

}