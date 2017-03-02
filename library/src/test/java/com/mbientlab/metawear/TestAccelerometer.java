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

import com.mbientlab.metawear.builder.RouteComponent;
import com.mbientlab.metawear.module.Accelerometer;
import com.mbientlab.metawear.data.Acceleration;
import com.mbientlab.metawear.module.AccelerometerBma255;
import com.mbientlab.metawear.module.AccelerometerBmi160;
import com.mbientlab.metawear.module.AccelerometerMma8452q;
import com.mbientlab.metawear.builder.RouteBuilder;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameter;
import org.junit.runners.Parameterized.Parameters;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

import bolts.Capture;
import bolts.Continuation;
import bolts.Task;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

/**
 * Created by etsai on 9/1/16.
 */
@RunWith(Parameterized.class)
public class TestAccelerometer extends UnitTestBase {
    @Parameters
    public static Collection<Object[]> data() {
        return Arrays.asList(new Object[][]{
                { MetaWearBoardInfo.RG },
                { MetaWearBoardInfo.R },
                { MetaWearBoardInfo.ENVIRONMENT },
        });
    }

    @Parameter
    public MetaWearBoardInfo boardInfo;

    private Accelerometer accelerometer;

    @Before
    public void setup() throws Exception {
        junitPlatform.boardInfo= boardInfo;
        connectToBoard();

        accelerometer = mwBoard.getModule(Accelerometer.class);
    }

    @Test
    public void setOdrCommand() {
        byte[] expected= null;

        if (boardInfo == MetaWearBoardInfo.RG) {
            expected = new byte[]{0x03, 0x03, 0x27, 0x03};
            accelerometer.configure()
                    .odr(55.f)
                    .commit();
        } else if (boardInfo == MetaWearBoardInfo.R) {
            expected= new byte[] {0x03, 0x03, 0x00, 0x00, 0x20, 0x00, 0x00};
            accelerometer.configure()
                    .odr(35.25f)
                    .commit();
        } else if (boardInfo == MetaWearBoardInfo.ENVIRONMENT) {
            expected= new byte[] {0x03, 0x03, 0x0a, 0x03};
            accelerometer.configure()
                    .odr(50.f)
                    .commit();
        }

        assertArrayEquals(expected, junitPlatform.getLastCommand());
    }

    @Test
    public void setOdrValue() {
        float expected = -1, actual;

        if (boardInfo == MetaWearBoardInfo.RG) {
            expected = 50f;
            accelerometer.configure()
                    .odr(55.f)
                    .commit();
        } else if (boardInfo == MetaWearBoardInfo.R) {
            expected= 50f;
            accelerometer.configure()
                    .odr(35.25f)
                    .commit();
        } else if (boardInfo == MetaWearBoardInfo.ENVIRONMENT) {
            expected= 62.5f;
            accelerometer.configure()
                    .odr(50.f)
                    .commit();
        }
        actual= accelerometer.getOdr();

        assertEquals(expected, actual, 0.001f);
    }

    @Test
    public void setRangeCommand() {
        byte[] expected= null;

        if (boardInfo == MetaWearBoardInfo.RG) {
            expected = new byte[] {0x03, 0x03, 0x28, 0x0c};
            accelerometer.configure()
                    .range(14.75f)
                    .commit();
        } else if (boardInfo == MetaWearBoardInfo.R) {
            expected= new byte[] {0x03, 0x03, 0x02, 0x00, 0x18, 0x00, 0x00};
            accelerometer.configure()
                    .range(7.3333f)
                    .commit();
        } else if (boardInfo == MetaWearBoardInfo.ENVIRONMENT) {
            expected= new byte[] {0x03, 0x03, 0x0b, 0x0c};
            accelerometer.configure()
                    .range(20f)
                    .commit();
        }

        assertArrayEquals(expected, junitPlatform.getLastCommand());
    }

    @Test
    public void setRangeValue() {
        float expected= -1, actual;

        if (boardInfo == MetaWearBoardInfo.RG) {
            expected = 16f;
            accelerometer.configure()
                    .range(14.75f)
                    .commit();
        } else if (boardInfo == MetaWearBoardInfo.R) {
            expected= 8.f;
            accelerometer.configure()
                    .range(7.3333f)
                    .commit();
        } else if (boardInfo == MetaWearBoardInfo.ENVIRONMENT) {
            expected= 16f;
            accelerometer.configure()
                    .range(20f)
                    .commit();
        }
        actual= accelerometer.getRange();

        assertEquals(expected, actual, 0.001f);
    }

    @Test
    public void subscribeAccStream() {
        byte[] expected = new byte[] {0x03, 0x04, 0x01};
        accelerometer.acceleration().addRouteAsync(new RouteBuilder() {
            @Override
            public void configure(RouteComponent source) {
                source.multicast()
                        .to().stream(null)
                        .to().split()
                            .index(0).stream(null)
                            .index(1).stream(null)
                            .index(2).stream(null);
            }
        }).continueWith(new Continuation<Route, Void>() {
            @Override
            public Void then(Task<Route> task) throws Exception {
                return null;
            }
        });

        assertArrayEquals(expected, junitPlatform.getLastCommand());
    }

    @Test
    public void unsubscribeAccStream() {
        byte[] expected = new byte[] {0x03, 0x04, 0x00};
        accelerometer.acceleration().addRouteAsync(new RouteBuilder() {
            @Override
            public void configure(RouteComponent source) {
                source.multicast()
                        .to().stream(null)
                        .to().split()
                            .index(0).stream(null)
                            .index(1).stream(null)
                            .index(2).stream(null);
            }
        }).continueWith(new Continuation<Route, Void>() {
            @Override
            public Void then(Task<Route> task) throws Exception {
                task.getResult().unsubscribe(0);
                task.getResult().unsubscribe(1);
                task.getResult().unsubscribe(2);
                task.getResult().unsubscribe(3);
                return null;
            }
        });

        assertArrayEquals(expected, junitPlatform.getLastCommand());
    }

    @Test
    public void enableAccSampling() {
        byte[] expected= null;

        if (boardInfo == MetaWearBoardInfo.RG || boardInfo == MetaWearBoardInfo.ENVIRONMENT) {
            expected = new byte[] {0x03, 0x02, 0x01, 0x00};
        } else if (boardInfo == MetaWearBoardInfo.R) {
            expected = new byte[] {0x03, 0x02, 0x01};
        }
        accelerometer.acceleration().start();

        assertArrayEquals(expected, junitPlatform.getLastCommand());
    }

    @Test
    public void disableAccSampling() {
        byte[] expected = null;

        if (boardInfo == MetaWearBoardInfo.RG || boardInfo == MetaWearBoardInfo.ENVIRONMENT) {
            expected = new byte[] {0x03, 0x02, 0x00, 0x01};
        } else if (boardInfo == MetaWearBoardInfo.R) {
            expected = new byte[] {0x03, 0x02, 0x00};
        }
        accelerometer.acceleration().stop();

        assertArrayEquals(expected, junitPlatform.getLastCommand());
    }

    @Test
    public void receiveAccData() {
        Acceleration expected= null;
        byte[] response= null;
        final Capture<Acceleration> actual= new Capture<>();


        if (boardInfo == MetaWearBoardInfo.ENVIRONMENT) {
            // (-4.7576f, 2.2893f, 2.9182f)
            expected = new Acceleration(Float.intBitsToFloat(0xc0983e00), Float.intBitsToFloat(0x40128400), Float.intBitsToFloat(0x403ac400));
            response= new byte[] { 0x03, 0x04, (byte) 0xe1, (byte) 0xb3, (byte) 0xa1, 0x24, (byte) 0xb1, 0x2e };
            accelerometer.configure()
                    .range(8f)
                    .commit();
        } else if (boardInfo == MetaWearBoardInfo.RG) {
            // (-1.872f, -2.919f, -1.495f)
            expected= new Acceleration(Float.intBitsToFloat(0xbfefa800), Float.intBitsToFloat(0xc03ad800), Float.intBitsToFloat(0xbfbf5800));
            response= new byte[] {0x03, 0x04, 0x16, (byte) 0xc4, (byte) 0x94, (byte) 0xa2, 0x2a, (byte) 0xd0};
            accelerometer.configure()
                    .range(4f)
                    .commit();
        } else if (boardInfo == MetaWearBoardInfo.R) {
            // (-1.450f, -2.555f, 0.792f)
            expected= new Acceleration(Float.intBitsToFloat(0xbfb9999a), Float.intBitsToFloat(0xc023851f), Float.intBitsToFloat(0x3f4ac083));
            response= new byte[] {0x03, 0x04, 0x56, (byte) 0xfa, 0x05, (byte) 0xf6, 0x18, 0x03};
        }

        accelerometer.acceleration().addRouteAsync(new RouteBuilder() {
            @Override
            public void configure(RouteComponent source) {
                source.stream(new Subscriber() {
                    @Override
                    public void apply(Data data, Object ... env) {
                        ((Capture<Acceleration>) env[0]).set(data.value(Acceleration.class));
                    }
                });
            }
        }).continueWith(new Continuation<Route, Void>() {
            @Override
            public Void then(Task<Route> task) throws Exception {
                task.getResult().setEnvironment(0, actual);
                return null;
            }
        });

        sendMockResponse(response);

        assertEquals(expected, actual.get());
    }

    @Test
    public void receiveSingleAxisAccData() {
        float[] expected= null;
        byte[] response= null;
        final float[] actual= new float[3];

        if (boardInfo == MetaWearBoardInfo.ENVIRONMENT) {
            // (-4.7576f, 2.2893f, 2.9182f)
            expected = new float[] { -4.7576f, 2.2893f, 2.9182f };
            response= new byte[] { 0x03, 0x04, (byte) 0xe1, (byte) 0xb3, (byte) 0xa1, 0x24, (byte) 0xb1, 0x2e };
            accelerometer.configure()
                    .range(8f)
                    .commit();
        } else if (boardInfo == MetaWearBoardInfo.RG) {
            // (-1.872f, -2.919f, -1.495f)
            expected = new float[] { -1.872f, -2.919f, -1.495f };
            response= new byte[] {0x03, 0x04, 0x16, (byte) 0xc4, (byte) 0x94, (byte) 0xa2, 0x2a, (byte) 0xd0};
            accelerometer.configure()
                    .range(4f)
                    .commit();
        } else if (boardInfo == MetaWearBoardInfo.R) {
            // (-1.450f, -2.555f, 0.792f)
            expected = new float[] { -1.450f, -2.555f, 0.792f };
            response= new byte[] {0x03, 0x04, 0x56, (byte) 0xfa, 0x05, (byte) 0xf6, 0x18, 0x03};
        }

        accelerometer.acceleration().addRouteAsync(new RouteBuilder() {
            @Override
            public void configure(RouteComponent source) {
                source.split()
                        .index(0).stream(new Subscriber() {
                            @Override
                            public void apply(Data data, Object... env) {
                                ((float[]) env[0])[0]= data.value(Float.class);
                            }
                        })
                        .index(1).stream(new Subscriber() {
                            @Override
                            public void apply(Data data, Object... env) {
                                ((float[]) env[0])[1]= data.value(Float.class);
                            }
                        })
                        .index(2).stream(new Subscriber() {
                            @Override
                            public void apply(Data data, Object... env) {
                                ((float[]) env[0])[2]= data.value(Float.class);
                            }
                        });
            }
        }).continueWith(new Continuation<Route, Void>() {
            @Override
            public Void then(Task<Route> task) throws Exception {
                task.getResult().setEnvironment(0, (Object) actual);
                task.getResult().setEnvironment(1, (Object) actual);
                task.getResult().setEnvironment(2, (Object) actual);
                return null;
            }
        });

        sendMockResponse(response);

        assertArrayEquals(expected, actual, 0.001f);
    }

    @Test
    public void receivedPackedData() {
        Acceleration[] expected= null;
        byte[] response= null;
        Acceleration[] actual = new Acceleration[3];

        if (boardInfo == MetaWearBoardInfo.ENVIRONMENT) {
            expected = new Acceleration[] {
                    new Acceleration(Float.intBitsToFloat(0x3f98c400), Float.intBitsToFloat(0xbe556000), Float.intBitsToFloat(0x406eca00)),
                    new Acceleration(Float.intBitsToFloat(0x3fa4e400), Float.intBitsToFloat(0xbf91dc00), Float.intBitsToFloat(0x407ffa00)),
                    new Acceleration(Float.intBitsToFloat(0x3ff65400), Float.intBitsToFloat(0xbffa7c00), Float.intBitsToFloat(0x407fe200))
            };
            response = new byte[] {0x03, 0x1c, 0x31, 0x26, 0x55, (byte) 0xf9, 0x65, 0x77, 0x39, 0x29, (byte) 0x89, (byte) 0xdb,
                    (byte) 0xfd, 0x7f, (byte) 0x95, 0x3d, 0x61, (byte) 0xc1, (byte) 0xf1, 0x7f};
            accelerometer.configure()
                    .range(4f)
                    .commit();
        } else if (boardInfo == MetaWearBoardInfo.RG) {
            expected = new Acceleration[] {
                    new Acceleration(Float.intBitsToFloat(0xc0913c00), Float.intBitsToFloat(0x3f553000), Float.intBitsToFloat(0xbe05c000)),
                    new Acceleration(Float.intBitsToFloat(0xc03fa800), Float.intBitsToFloat(0x3f64d000), Float.intBitsToFloat(0x3e15c000)),
                    new Acceleration(Float.intBitsToFloat(0xbcec0000), Float.intBitsToFloat(0x3eb42000), Float.intBitsToFloat(0x3d850000))
            };
            response= new byte[] {0x03, 0x1c, 0x62, (byte) 0xb7, 0x53, 0x0d, (byte) 0xe9, (byte) 0xfd, 0x16, (byte) 0xd0, 0x4d,
                    0x0e, 0x57, 0x02, (byte) 0x8a, (byte) 0xff, (byte) 0xa1, 0x05, 0x0a, 0x01};
            accelerometer.configure()
                    .range(8f)
                    .commit();
        } else if (boardInfo == MetaWearBoardInfo.R) {
            expected = new Acceleration[] {
                    new Acceleration(Float.intBitsToFloat(0xc0948312), Float.intBitsToFloat(0x40b5999a), Float.intBitsToFloat(0xbe70a3d7)),
                    new Acceleration(Float.intBitsToFloat(0xbfb49ba6), Float.intBitsToFloat(0x3fa16873), Float.intBitsToFloat(0x403072b0)),
                    new Acceleration(Float.intBitsToFloat(0xbf9d9168), Float.intBitsToFloat(0xbfea1cac), Float.intBitsToFloat(0xc05d8106))
            };
            response= new byte[] {0x03, 0x12, (byte) 0xdf, (byte) 0xed, 0x2b, 0x16, 0x15, (byte) 0xff, 0x7d, (byte) 0xfa,
                    (byte) 0xed, 0x04, (byte) 0xc5, 0x0a, 0x31, (byte) 0xfb, (byte) 0xdb, (byte) 0xf8, 0x7b, (byte) 0xf2};
        }

        final ArrayList<Acceleration> received = new ArrayList<>();
        accelerometer.packedAcceleration().addRouteAsync(new RouteBuilder() {
            @Override
            public void configure(RouteComponent source) {
                source.stream(new Subscriber() {
                    @Override
                    public void apply(Data data, Object... env) {
                        ((ArrayList<Acceleration>) env[0]).add(data.value(Acceleration.class));
                    }
                });
            }
        }).continueWith(new Continuation<Route, Void>() {
            @Override
            public Void then(Task<Route> task) throws Exception {
                task.getResult().setEnvironment(0, received);
                return null;
            }
        });

        sendMockResponse(response);
        received.toArray(actual);

        assertArrayEquals(expected, actual);
    }

    @Test
    public void getBma255Module() {
        if (boardInfo == MetaWearBoardInfo.RG || boardInfo == MetaWearBoardInfo.R) {
            assertNull(mwBoard.getModule(AccelerometerBma255.class));
        } else {
            assertNotNull(mwBoard.getModule(AccelerometerBma255.class));
        }
    }

    @Test
    public void getBmi160Module() {
        if (boardInfo == MetaWearBoardInfo.ENVIRONMENT || boardInfo == MetaWearBoardInfo.R) {
            assertNull(mwBoard.getModule(AccelerometerBmi160.class));
        } else {
            assertNotNull(mwBoard.getModule(AccelerometerBmi160.class));
        }
    }

    @Test
    public void getMma8452qModule() {
        if (boardInfo == MetaWearBoardInfo.ENVIRONMENT || boardInfo == MetaWearBoardInfo.RG) {
            assertNull(mwBoard.getModule(AccelerometerMma8452q.class));
        } else {
            assertNotNull(mwBoard.getModule(AccelerometerMma8452q.class));
        }
    }
}
