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

package com.mbientlab.metawear;

import static org.junit.Assert.assertArrayEquals;

import com.mbientlab.metawear.module.Settings;

import org.junit.Before;
import org.junit.Test;

/**
 * Created by lkasso on 04/01/2021.
 */

public class TestSettingsRev10 extends UnitTestBase {
    private Settings settings;

    @Before
    public void setup() throws Exception {
        junitPlatform.addCustomModuleInfo(new byte[]{0x11, (byte) 0x80, 0x00, 0x0a, 0x07, 0x00});
        connectToBoard();

        settings = mwBoard.getModule(Settings.class);
    }

    @Test
    public void enableForce1MPhy() {
        byte[] expected = new byte[]{0x11, (byte) 0x1d, 0x01};

        settings.enableForce1MPhy(true);
        assertArrayEquals(expected, junitPlatform.getLastCommand());
    }

    @Test
    public void disableForce1MPhy() {
        byte[] expected = new byte[]{0x11, (byte) 0x1d, 0x00};

        settings.enableForce1MPhy(false);
        assertArrayEquals(expected, junitPlatform.getLastCommand());
    }
}
