package com.gordon.thomas.attendanceapp;
/*
Author: Thomas Gordon
April 2021
 */
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;

import java.util.UUID;

public class GattProfile {

    private static final String LOG_TAG = GattProfile.class.getSimpleName();

    public static UUID STUDENT_ID_SERVICE = UUID.fromString("78f9604b-1b22-4081-8f5c-0bca9cbaa82b");
    public static UUID STUDENT_ID = UUID.fromString("8c60237a-26dd-4b06-90a2-fa02cae82275");

    public static BluetoothGattService createStudentNumberService() {
        BluetoothGattService gattService = new BluetoothGattService(STUDENT_ID_SERVICE,
                BluetoothGattService.SERVICE_TYPE_PRIMARY);

        BluetoothGattCharacteristic studentNumber = new BluetoothGattCharacteristic(STUDENT_ID,
                BluetoothGattCharacteristic.PROPERTY_READ,
                BluetoothGattCharacteristic.PERMISSION_READ);

        gattService.addCharacteristic(studentNumber);
        return  gattService;
    }
}