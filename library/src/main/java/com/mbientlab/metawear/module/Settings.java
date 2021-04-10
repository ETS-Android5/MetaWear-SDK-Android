/*
 * Copyright 2014-2015 MbientLab Inc. All rights reserved.
 *
 * IMPORTANT: Your use of this Software is limited to those specific rights granted under the terms of a software
 * license agreement between the user who downloaded the software, his/her employer (which must be your
 * employer) and MbientLab Inc, (the "License").  You may not use this Software unless you agree to abide by the
 * terms of the License which can be found at www.mbientlab.com/terms.  The License limits your use, and you
 * acknowledge, that the Software may be modified, copied, and distributed when used in conjunction with an
 * MbientLab Inc, product.  Other than for the foregoing purpose, you may not use, reproduce, copy, prepare
 * derivative works of, modify, distribute, perform, display or sell this Software and/or its documentation for any
 * purpose.
 *
 * YOU FURTHER ACKNOWLEDGE AND AGREE THAT THE SOFTWARE AND DOCUMENTATION ARE PROVIDED "AS IS" WITHOUT WARRANTY
 * OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING WITHOUT LIMITATION, ANY WARRANTY OF MERCHANTABILITY, TITLE,
 * NON-INFRINGEMENT AND FITNESS FOR A PARTICULAR PURPOSE. IN NO EVENT SHALL MBIENTLAB OR ITS LICENSORS BE LIABLE OR
 * OBLIGATED UNDER CONTRACT, NEGLIGENCE, STRICT LIABILITY, CONTRIBUTION, BREACH OF WARRANTY, OR OTHER LEGAL EQUITABLE
 * THEORY ANY DIRECT OR INDIRECT DAMAGES OR EXPENSES INCLUDING BUT NOT LIMITED TO ANY INCIDENTAL, SPECIAL, INDIRECT,
 * PUNITIVE OR CONSEQUENTIAL DAMAGES, LOST PROFITS OR LOST DATA, COST OF PROCUREMENT OF SUBSTITUTE GOODS, TECHNOLOGY,
 * SERVICES, OR ANY CLAIMS BY THIRD PARTIES (INCLUDING BUT NOT LIMITED TO ANY DEFENSE THEREOF), OR OTHER SIMILAR COSTS.
 *
 * Should you have any questions regarding your right to use this Software, contact MbientLab via email:
 * hello@mbientlab.com.
 */

package com.mbientlab.metawear.module;

import com.mbientlab.metawear.ActiveDataProducer;
import com.mbientlab.metawear.CodeBlock;
import com.mbientlab.metawear.ConfigEditorBase;
import com.mbientlab.metawear.Observer;
import com.mbientlab.metawear.ForcedDataProducer;
import com.mbientlab.metawear.MetaWearBoard.Module;
import com.mbientlab.metawear.impl.Util;

import java.util.Arrays;
import java.util.Locale;

import bolts.Task;

/**
 * Configures Bluetooth settings and auxiliary hardware and firmware features
 * @author Laura Kassovic
 */
public interface Settings extends Module {
    /**
     * Bluetooth LE advertising configuration
     * @author Eric Tsai
     */
    class BleAdvertisementConfig {
        /** Name the device advertises as */
        public String deviceName;
        /** Time between each advertise event, in milliseconds (ms) */
        public int interval;
        /** How long the device should advertise for with 0 indicating no timeout, in seconds (s) */
        public short timeout;
        /** BLE radio's transmitting strength */
        public byte txPower;
        /** Scan response */
        public byte[] scanResponse;

        public BleAdvertisementConfig() {
            scanResponse = new byte[0];
        }

        public BleAdvertisementConfig(String deviceName, int interval, short timeout, byte txPower, byte[] scanResponse) {
            this.deviceName = deviceName;
            this.interval = interval;
            this.timeout = timeout;
            this.txPower = txPower;
            this.scanResponse = scanResponse;
        }

        @Override
        public boolean equals(Object o) {
            //< Generated by IntelliJ
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            BleAdvertisementConfig that = (BleAdvertisementConfig) o;

            return interval == that.interval && timeout == that.timeout && txPower == that.txPower &&
                    deviceName.equals(that.deviceName) && Arrays.equals(scanResponse, that.scanResponse);

        }

        @Override
        public int hashCode() {
            //< Generated by IntelliJ
            int result = deviceName.hashCode();
            result = 31 * result + interval;
            result = 31 * result + (int) timeout;
            result = 31 * result + (int) txPower;
            result = 31 * result + Arrays.hashCode(scanResponse);
            return result;
        }

        @Override
        public String toString() {
            return String.format(Locale.US, "{Device Name: %s, Adv Interval: %d, Adv Timeout: %d, Tx Power: %d, Scan Response: %s}",
                    deviceName, interval, timeout, txPower, Util.arrayToHexString(scanResponse));
        }
    }
    /**
     * Interface for modifying the Bluetooth LE advertising configuration
     * @author Eric Tsai
     */
    interface BleAdvertisementConfigEditor extends ConfigEditorBase {
        /**
         * Set the device's advertising name
         * @param name    Device name, max of 8 ASCII characters
         * @return Calling object
         */
        BleAdvertisementConfigEditor deviceName(String name);
        /**
         * Set the advertising interval
         * @param interval    Time between advertise events
         * @return Calling object
         */
        BleAdvertisementConfigEditor interval(short interval);
        /**
         * Set how long to advertise for
         * @param timeout     How long to advertise for, between [0, 180] seconds where 0 indicates no timeout
         * @return Calling object
         */
        BleAdvertisementConfigEditor timeout(byte timeout);
        /**
         * Sets advertising transmitting power.  If a non valid value is set, the nearest valid value will be used instead
         * @param power    Valid values are: 4, 0, -4, -8, -12, -16, -20, -30
         * @return Calling object
         */
        BleAdvertisementConfigEditor txPower(byte power);
        /**
         * Set a custom scan response packet
         * @param response    Byte representation of the response
         * @return Calling object
         */
        BleAdvertisementConfigEditor scanResponse(byte[] response);
    }
    /**
     * Starts ble advertising
     */
    void startBleAdvertising();
    /**
     * Edit the ble advertising configuration
     * @return Editor object to modify the settings
     */
    BleAdvertisementConfigEditor editBleAdConfig();
    /**
     * Read the current ble advertising configuration
     * @return Task that is completed once the advertising config has been received
     */
    Task<BleAdvertisementConfig> readBleAdConfigAsync();
    /**
     * Wrapper class containing the connection parameters
     * @author Eric Tsai
     */
    class BleConnectionParameters {
        /** Minimum time the central device asks for data from the peripheral, in milliseconds (ms) */
        public final float minConnectionInterval;
        /** Maximum time the central device asks for data from the peripheral, in milliseconds (ms */
        public final float maxConnectionInterval;
        /** How many times the peripheral can choose to discard data requests from the central device */
        public final short slaveLatency;
        /** Timeout from the last data exchange until the ble link is considered lost */
        public final short supervisorTimeout;

        public BleConnectionParameters(float minConnectionInterval, float maxConnectionInterval, short slaveLatency, short supervisorTimeout) {
            this.minConnectionInterval = minConnectionInterval;
            this.maxConnectionInterval = maxConnectionInterval;
            this.slaveLatency = slaveLatency;
            this.supervisorTimeout = supervisorTimeout;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            BleConnectionParameters that = (BleConnectionParameters) o;

            return Float.compare(that.minConnectionInterval, minConnectionInterval) == 0 &&
                    Float.compare(that.maxConnectionInterval, maxConnectionInterval) == 0 &&
                    slaveLatency == that.slaveLatency && supervisorTimeout == that.supervisorTimeout;

        }

        @Override
        public int hashCode() {
            int result = (minConnectionInterval != +0.0f ? Float.floatToIntBits(minConnectionInterval) : 0);
            result = 31 * result + (maxConnectionInterval != +0.0f ? Float.floatToIntBits(maxConnectionInterval) : 0);
            result = 31 * result + (int) slaveLatency;
            result = 31 * result + (int) supervisorTimeout;
            return result;
        }

        @Override
        public String toString() {
            return String.format(Locale.US, "{min conn interval: %.2f, max conn interval: %.2f, slave latency: %d, supervisor timeout: %d}",
                    minConnectionInterval, maxConnectionInterval, slaveLatency, supervisorTimeout);
        }
    }
    /**
     * Interface for editing the Bluetooth LE connection parameters
     * @author Eric Tsai
     */
    interface BleConnectionParametersEditor extends ConfigEditorBase {
        /**
         * Sets the lower bound of the connection interval
         * @param interval    Lower bound, at least 7.5ms
         * @return Calling object
         */
        BleConnectionParametersEditor minConnectionInterval(float interval);
        /**
         * Sets the upper bound of the connection interval
         * @param interval    Upper bound, at most 4000ms
         * @return Calling object
         */
        BleConnectionParametersEditor maxConnectionInterval(float interval);
        /**
         * Sets the number of connection intervals to skip
         * @param latency    Number of connection intervals to skip, between [0, 1000]
         * @return Calling object
         */
        BleConnectionParametersEditor slaveLatency(short latency);
        /**
         * Sets the maximum amount of time between data exchanges until the connection is considered to be lost
         * @param timeout    Timeout value between [10, 32000] ms
         * @return Calling object
         */
        BleConnectionParametersEditor supervisorTimeout(short timeout);
    }
    /**
     * Edit the ble connection parameters
     * @return Editor object to modify the connection parameters
     */
    BleConnectionParametersEditor editBleConnParams();
    /**
     * Read the current ble connection parameters
     * @return Task that is completed once the connection parameters have been received
     */
    Task<BleConnectionParameters> readBleConnParamsAsync();
    /**
     * Wrapper class encapsulating the battery state data
     * @author Eric Tsai
     */
    final class BatteryState {
        /** Percent charged, between [0, 100] */
        public final byte charge;
        /** Battery voltage level in V */
        public final float voltage;

        public BatteryState(byte charge, float voltage) {
            this.charge = charge;
            this.voltage = voltage;
        }

        @Override
        public String toString() {
            return String.format(Locale.US, "{charge: %d%%, voltage: %.3fV}", charge, voltage);
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            BatteryState that = (BatteryState) o;

            return charge == that.charge && Float.compare(that.voltage, voltage) == 0;
        }

        @Override
        public int hashCode() {
            int result = (int) charge;
            result = 31 * result + (voltage != +0.0f ? Float.floatToIntBits(voltage) : 0);
            return result;
        }
    }
    /**
     * Produces battery data that can be used with the firmware features
     * @author Eric Tsai
     */
    interface BatteryDataProducer extends ForcedDataProducer {
        /**
         * Get the name for battery charge data
         * @return Battery charge data name
         */
        String chargeName();
        /**
         * Get the name for battery voltage data
         * @return Battery voltage data name
         */
        String voltageName();
    }
    /**
     * Gets an object to use the battery data
     * @return Object representing battery data, null if battery data is not supported
     */
    BatteryDataProducer battery();
    /**
     * Gets an object to control power status notifications
     * @return Object representing power status notifications, null if power status not supported
     */
    ActiveDataProducer powerStatus();
    /**
     * Reads the current power status if available
     * @return Task holding the power status; 1 if power source is attached, 0 otherwise
     */
    Task<Byte> readCurrentPowerStatusAsync();
    /**
     * Gets an object to control charging status notifications
     * @return Object representing charging status notifications, null if charging status not supported
     */
    ActiveDataProducer chargeStatus();
    /**
     * Reads the current charge status
     * @return Task holding the charge status; 1 if battery is charging, 0 otherwise
     */
    Task<Byte> readCurrentChargeStatusAsync();
    /**
     * Programs a task that will be execute on-board when a disconnect occurs
     * @param codeBlock    MetaWear commands composing the task
     * @return Task holding the result of the program request
     */
    Task<Observer> onDisconnectAsync(CodeBlock codeBlock);
    /**
     * Turns on the 3V regulator
     * Needed if IOs / peripherals need 3V power from the MetaSensor. MMS Only.
     * @param enable    True to enable (turn on) the 3V power, False to disable
     * @return True if feature is supported, false if regulator cannot be enabled (if not MMS)
     */
    boolean enable3VRegulator(boolean enable);
}
