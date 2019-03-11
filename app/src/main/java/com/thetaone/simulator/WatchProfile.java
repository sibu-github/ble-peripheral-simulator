package com.thetaone.simulator;

import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.util.Log;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Date;
import java.util.UUID;


public class WatchProfile {
    private static final String TAG = WatchProfile.class.getSimpleName();


    public static final int HEART_RATE_LIMIT = 255;
    public static final int RESPIRATION_RATE_LIMIT = 126;
    public static final int O2_SATURATION_LIMIT = 100;
    public static final int SYS_BLOOD_PRESSURE_LIMIT = 999;
    public static final int DIAS_BLOOD_PRESSURE_LIMIT = 150;
    public static final int BLOOD_GLUCOSE_LIMIT = 999;
    public static final int MOTION_LIMIT = 999999;

    public static final int TIMESTAMP_BYTE_LENGTH = 4;
    public static final int HEART_RATE_BYTE_LENGTH = 1;
    public static final int RESPIRATION_RATE_BYTE_LENGTH = 1;
    public static final int O2_SATURATION_BYTE_LENGTH = 1;
    public static final int SYS_BLOOD_PRESSURE_BYTE_LENGTH = 3;
    public static final int DIAS_BLOOD_PRESSURE_BYTE_LENGTH = 3;
    public static final int BLOOD_GLUCOSE_BYTE_LENGTH = 2;
    public static final int MOTION_BYTE_LENGTH = 4;
    public static final int POSTAMBLE_BYTE_LENGTH = 1;
    public static final int PREAMBLE_BYTE_LENGTH = 1;



    public static final int GREEN_MAX_LIMIT = 999999;
    public static final int IR_MAX_LIMIT = 999999;
    public static final int ACC_X_LIMIT = 65000;
    public static final int ACC_Y_LIMIT = 65000;
    public static final int ACC_Z_LIMIT = 65000;
    public static final int GYRO_1_LIMIT = 65000;
    public static final int GYRO_2_LIMIT = 65000;
    public static final int GYRO_3_LIMIT = 65000;

    public static final int GREEN_BYTE_LENGTH = 3;
    public static final int IR_BYTE_LENGTH = 3;
    public static final int ACC_X_BYTE_LENGTH = 2;
    public static final int ACC_Y_BYTE_LENGTH = 2;
    public static final int ACC_Z_BYTE_LENGTH = 2;
    public static final int GYRO_1_BYTE_LENGTH = 2;
    public static final int GYRO_2_BYTE_LENGTH = 2;
    public static final int GYRO_3_BYTE_LENGTH = 2;






    public static final String CUSTOM_WATCH_SERVICE_UUID = "00004805-0000-1000-8000-00805f9b34fb";
    public static final String MEASURE_NOW_UUID_STR = "00003a3b-0000-1000-8000-00805f9b34fb";
    public static final String CALIBRATE_NOW_UUID_STR = "00003a0a-0000-1000-8000-00805f9b34fb";



    public static UUID WATCH_SERVICE_UUID = UUID.fromString(CUSTOM_WATCH_SERVICE_UUID);
    public static UUID MEASURE_NOW_UUID    = UUID.fromString(MEASURE_NOW_UUID_STR);
    public static UUID CAL_NOW_UUID = UUID.fromString(CALIBRATE_NOW_UUID_STR);

    public static BluetoothGattService createWatchService() {
        BluetoothGattService service = new BluetoothGattService(WATCH_SERVICE_UUID,
                BluetoothGattService.SERVICE_TYPE_PRIMARY);

        // MEASURE_NOW characteristics
        BluetoothGattCharacteristic measureNowCharacs
                = new BluetoothGattCharacteristic(MEASURE_NOW_UUID,
                BluetoothGattCharacteristic.PROPERTY_READ | BluetoothGattCharacteristic.PROPERTY_NOTIFY,
                BluetoothGattCharacteristic.PERMISSION_READ);

        // CALIBRATE_NOW characteristics
        BluetoothGattCharacteristic calNowCharacs
                = new BluetoothGattCharacteristic(CAL_NOW_UUID,
                BluetoothGattCharacteristic.PROPERTY_READ | BluetoothGattCharacteristic.PROPERTY_NOTIFY,
                BluetoothGattCharacteristic.PERMISSION_READ);

        service.addCharacteristic(measureNowCharacs);
        service.addCharacteristic(calNowCharacs);
        return service;
    }

    public static  byte[]  measureNowResponse () {
        ArrayList<byte[]> data = new ArrayList<>();
        byte[] byteArray = new byte[20];

        long timeStamp = new Date().getTime();
        int currTime = (int)(timeStamp / 1000);
        int heartRate = (int) (Math.random() * HEART_RATE_LIMIT);
        int respirationRate = (int) (Math.random() * RESPIRATION_RATE_LIMIT);
        int spo2 = (int) (Math.random() * O2_SATURATION_LIMIT);
        int sbp = (int) (Math.random()  * SYS_BLOOD_PRESSURE_LIMIT);
        int dbp = (int) (Math.random() * DIAS_BLOOD_PRESSURE_LIMIT);
        int bg = (int) (Math.random() * BLOOD_GLUCOSE_LIMIT);
        int motion = (int) (Math.random() * MOTION_LIMIT);
        int postamble = 0;

        Log.d(TAG, "currTime: " + currTime
                + ", heartRate: " + heartRate
                + ", respirationRate: " + respirationRate
                + ", spo2: " + spo2
                + ", sbp: " + sbp
                + ", dbp: " + dbp
                + ", bg: " + bg
                + ", motion: " + motion
        );


        data.add(addData(currTime, TIMESTAMP_BYTE_LENGTH));
        data.add(addData(heartRate, HEART_RATE_BYTE_LENGTH));
        data.add(addData(respirationRate, RESPIRATION_RATE_BYTE_LENGTH));
        data.add(addData(spo2, O2_SATURATION_BYTE_LENGTH));
        data.add(addData(sbp, SYS_BLOOD_PRESSURE_BYTE_LENGTH));
        data.add(addData(dbp, DIAS_BLOOD_PRESSURE_BYTE_LENGTH));
        data.add(addData(bg, BLOOD_GLUCOSE_BYTE_LENGTH));
        data.add(addData(motion, MOTION_BYTE_LENGTH));
        data.add(addData(postamble, POSTAMBLE_BYTE_LENGTH));

        int i = 0;
        for( byte[] arrayElement : data ) {
            for(int j=0;j<arrayElement.length;j++){
                byteArray[i++] = arrayElement[j];
            }
        }
        return byteArray;
    }

    public static byte[] calNowResponse(){
        ArrayList<byte[]> data = new ArrayList<>();
        byte[] byteArray = new byte[20];


        int green = (int) (Math.random() * GREEN_MAX_LIMIT);
        int ir = (int) (Math.random() * IR_MAX_LIMIT);
        int accx = (int) (Math.random() * ACC_X_LIMIT);
        int accy = (int) (Math.random() * ACC_Y_LIMIT);
        int accz = (int) (Math.random() * ACC_Z_LIMIT);
        int gyro1 = (int) (Math.random() * GYRO_1_LIMIT);
        int gyro2 = (int) (Math.random() * GYRO_2_LIMIT);
        int gyro3 = (int) (Math.random() * GYRO_3_LIMIT);
        int postamble = 0;
        int preamble = 0;

        Log.d(TAG, "green: " + green
                            + ", ir: " + ir
                            + ", accx: " + accx
                            + ", accy: " + accy
                            + ", accz: " + accz
                            + ", gyro1: " + gyro1
                            + ", gyro2: " + gyro2
                            + ", gyro3: " + gyro3
        );




        data.add(addData(preamble, PREAMBLE_BYTE_LENGTH));
        data.add(addData(green, GREEN_BYTE_LENGTH));
        data.add(addData(ir, IR_BYTE_LENGTH));
        data.add(addData(accx, ACC_X_BYTE_LENGTH));
        data.add(addData(accy, ACC_Y_BYTE_LENGTH));
        data.add(addData(accz, ACC_Z_BYTE_LENGTH));
        data.add(addData(gyro1, GYRO_1_BYTE_LENGTH));
        data.add(addData(gyro2, GYRO_2_BYTE_LENGTH));
        data.add(addData(gyro3, GYRO_3_BYTE_LENGTH));
        data.add(addData(postamble, POSTAMBLE_BYTE_LENGTH));

        int i = 0;
        for( byte[] arrayElement : data ) {
            for(int j=0;j<arrayElement.length;j++){
                byteArray[i++] = arrayElement[j];
            }
        }
        return byteArray;
    }


 private static byte[] addData ( int value, int length ) {
        byte[] filteredData = new byte[length];
        ByteBuffer byteBuffer = ByteBuffer.allocate(4);
        byteBuffer.putInt(value);
        int skippingFactor = 4 - length;
        int increment = 0, i = 0;
        for ( byte a : byteBuffer.array() ) {
            if (increment >= skippingFactor) {
                filteredData[i++] = a;
            }
            increment++;
        }
        return filteredData;
    }
}