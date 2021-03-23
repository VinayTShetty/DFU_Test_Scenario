package com.otaTest.UUID;

import java.util.UUID;

public class FirmwareUUID {
    public static final UUID SERVICE_UUID = UUID.fromString("0000ab00-2687-4433-2208-abf9b34fb000");
    public static final UUID CHARCTERSTICS_UUID =UUID.fromString("0000ab01-2687-4433-2208-abf9b34fb000");
    /**
     * client chanrcterstic UUID,for enabling the Charcterstic notification.
     */
    public static String CLIENT_CHARACTERISTIC_CONFIG = "00002902-0000-1000-8000-00805f9b34fb";
}
