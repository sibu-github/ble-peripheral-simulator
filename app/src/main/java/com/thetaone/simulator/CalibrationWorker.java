package com.thetaone.simulator;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattServer;

public class CalibrationWorker extends Thread {
    BluetoothGattServer mBluetoothGattServer;
    BluetoothDevice device;
    BluetoothGattCharacteristic characteristic;


    CalibrationWorker(BluetoothGattServer mBluetoothGattServer,
                      BluetoothDevice device,
                      BluetoothGattCharacteristic characteristic){
        this.mBluetoothGattServer = mBluetoothGattServer;
        this.device= device;
        this.characteristic = characteristic;
    }

    @Override
    public void run() {
        super.run();
        for(int i=0; i< 26*60; i++){
            byte[] data = WatchProfile.calNowResponse();
            characteristic.setValue(data);
            boolean flag = mBluetoothGattServer.notifyCharacteristicChanged(device,characteristic,true);
            if(flag)
                System.out.println("Calibration sent");
            else
                System.out.println("Calibration not sent");


            try{
                Thread.sleep(60000/(26 * 60));
            } catch (InterruptedException e){
                e.printStackTrace();
            }
        }

    }
}
