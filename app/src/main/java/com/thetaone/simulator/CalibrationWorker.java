package com.thetaone.simulator;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattServer;

public class CalibrationWorker extends Thread {
    BluetoothGattServer mBluetoothGattServer;
    BluetoothDevice device;
    BluetoothGattCharacteristic characteristic;
    int requestId, responseCode, offset;
    CalibrationWorker(BluetoothGattServer mBluetoothGattServer,BluetoothDevice device,int requestId,int responseCode,int offset,BluetoothGattCharacteristic characteristic){
        this.mBluetoothGattServer = mBluetoothGattServer;
        this.requestId = requestId;
        this.responseCode = responseCode;
        this.offset = offset;
        this.device= device;
        this.characteristic = characteristic;
    }

    @Override
    public void run() {
        super.run();
        for(int i=0;i<26*60;i++){
            byte[] data = WatchProfile.measureNowResponse();
            characteristic.setValue(data);
//        boolean flag = mBluetoothGattServer.sendResponse(device,requestId,responseCode,offset,data);
            boolean flag = mBluetoothGattServer.notifyCharacteristicChanged(device,characteristic,true);
            if(flag)
                System.out.println("Calibration sent");
            else
                System.out.println("Calibration not sent");
        }

    }
}
