/*
 * Copyright 1999-2011 Alibaba Group.
 *  
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *  
 *      http://www.apache.org/licenses/LICENSE-2.0
 *  
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.alibaba.dubbo.common.serialize.serialization;

import com.alibaba.dubbo.common.serialize.ObjectInput;
import com.alibaba.dubbo.common.serialize.ObjectOutput;
import com.alibaba.dubbo.common.serialize.support.hessian.Hessian2Serialization;

import org.junit.Ignore;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.util.Arrays;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

/**
 * @author ding.lid
 */
public class Hessian2SerializationTest extends AbstractSerializationPersionFailTest {
    {
        serialization = new Hessian2Serialization();
    }

    // Hessian2 

    // @Test
    // public void test_boolArray_withType() throws Exception {
    //     boolean[] data = new boolean[]{true, false, true};

    //     ObjectOutput objectOutput = serialization.serialize(url, byteArrayOutputStream);
    //     objectOutput.writeObject(data);
    //     objectOutput.flushBuffer();

    //     ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(
    //             byteArrayOutputStream.toByteArray());
    //     ObjectInput deserialize = serialization.deserialize(url, byteArrayInputStream);

    //     assertTrue(Arrays.equals(data, (boolean[]) deserialize.readObject(boolean[].class)));

    //     try {
    //         deserialize.readObject(boolean[].class);
    //         fail();
    //     } catch (ArrayIndexOutOfBoundsException e) {
    //     }
    //     // NOTE： Hessian2抛出了ArrayIndexOutOfBoundsException 而不是 IOException！！
    //     // 容忍这个问题！！
    // }

    // @Ignore("type missing, char[] -> String")
    // @Test
    // public void test_charArray() throws Exception {
    // }

    // @Test
    // public void test_shortArray_withType() throws Exception {
    //     short[] data = new short[]{37, 39, 12};

    //     ObjectOutput objectOutput = serialization.serialize(url, byteArrayOutputStream);
    //     objectOutput.writeObject(data);
    //     objectOutput.flushBuffer();

    //     ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(
    //             byteArrayOutputStream.toByteArray());
    //     ObjectInput deserialize = serialization.deserialize(url, byteArrayInputStream);

    //     assertArrayEquals(data, (short[]) deserialize.readObject(short[].class));

    //     try {
    //         deserialize.readObject(short[].class);
    //         fail();
    //     } catch (ArrayIndexOutOfBoundsException e) {
    //     }
    //     // NOTE： Hessian2抛出了ArrayIndexOutOfBoundsException 而不是 IOException！！
    //     // 容忍这个问题！！
    // }

    // @Test
    // public void test_intArray_withType() throws Exception {
    //     int[] data = new int[]{234, 0, -1};

    //     ObjectOutput objectOutput = serialization.serialize(url, byteArrayOutputStream);
    //     objectOutput.writeObject(data);
    //     objectOutput.flushBuffer();

    //     ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(
    //             byteArrayOutputStream.toByteArray());
    //     ObjectInput deserialize = serialization.deserialize(url, byteArrayInputStream);

    //     assertArrayEquals(data, (int[]) deserialize.readObject());

    //     try {
    //         deserialize.readObject(int[].class);
    //         fail();
    //     } catch (ArrayIndexOutOfBoundsException e) {
    //     }
    //     // NOTE： Hessian2抛出了ArrayIndexOutOfBoundsException 而不是 IOException！！
    //     // 容忍这个问题！！
    // }

    // @Test
    // public void test_longArray_withType() throws Exception {
    //     long[] data = new long[]{234, 0, -1};

    //     ObjectOutput objectOutput = serialization.serialize(url, byteArrayOutputStream);
    //     objectOutput.writeObject(data);
    //     objectOutput.flushBuffer();

    //     ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(
    //             byteArrayOutputStream.toByteArray());
    //     ObjectInput deserialize = serialization.deserialize(url, byteArrayInputStream);

    //     assertArrayEquals(data, (long[]) deserialize.readObject());

    //     try {
    //         deserialize.readObject(long[].class);
    //         fail();
    //     } catch (ArrayIndexOutOfBoundsException e) {
    //     }
    //     // NOTE： Hessian2抛出了ArrayIndexOutOfBoundsException 而不是 IOException！！
    //     // 容忍这个问题！！
    // }

    // @Test
    // public void test_floatArray_withType() throws Exception {
    //     float[] data = new float[]{37F, -3.14F, 123456.7F};

    //     ObjectOutput objectOutput = serialization.serialize(url, byteArrayOutputStream);
    //     objectOutput.writeObject(data);
    //     objectOutput.flushBuffer();

    //     ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(
    //             byteArrayOutputStream.toByteArray());
    //     ObjectInput deserialize = serialization.deserialize(url, byteArrayInputStream);

    //     assertArrayEquals(data, (float[]) deserialize.readObject(), 0.0001F);

    //     try {
    //         deserialize.readObject(float[].class);
    //         fail();
    //     } catch (ArrayIndexOutOfBoundsException e) {
    //     }
    //     // NOTE： Hessian2抛出了ArrayIndexOutOfBoundsException 而不是 IOException！！
    //     // 容忍这个问题！！
    // }

    // @Test
    // public void test_doubleArray_withType() throws Exception {
    //     double[] data = new double[]{37D, -3.14D, 123456.7D};

    //     ObjectOutput objectOutput = serialization.serialize(url, byteArrayOutputStream);
    //     objectOutput.writeObject(data);
    //     objectOutput.flushBuffer();

    //     ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(
    //             byteArrayOutputStream.toByteArray());
    //     ObjectInput deserialize = serialization.deserialize(url, byteArrayInputStream);

    //     assertArrayEquals(data, (double[]) deserialize.readObject(double[].class), 0.0001);

    //     try {
    //         deserialize.readObject(double[].class);
    //         fail();
    //     } catch (ArrayIndexOutOfBoundsException e) {
    //     }
    //     // NOTE： Hessian2抛出了ArrayIndexOutOfBoundsException 而不是 IOException！！
    //     // 容忍这个问题！！
    // }

    // @Test
    // public void test_StringArray_withType() throws Exception {

    //     String[] data = new String[]{"1", "b"};


    //     ObjectOutput objectOutput = serialization.serialize(url, byteArrayOutputStream);
    //     objectOutput.writeObject(data);
    //     objectOutput.flushBuffer();

    //     ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(
    //             byteArrayOutputStream.toByteArray());
    //     ObjectInput deserialize = serialization.deserialize(url, byteArrayInputStream);

    //     assertArrayEquals(data, deserialize.readObject(String[].class));

    //     try {
    //         deserialize.readObject(String[].class);
    //         fail();
    //     } catch (ArrayIndexOutOfBoundsException e) {
    //     }
    //     // NOTE： Hessian2抛出了ArrayIndexOutOfBoundsException 而不是 IOException！！
    //     // 容忍这个问题！！
    // }

    // @Ignore("type missing, Byte -> Integer")
    // @Test
    // public void test_ByteWrap() throws Exception {
    // }

    // // FIXME
    // @Ignore("Bad Stream read other type data")
    // @Test
    // public void test_MediaContent_badStream() throws Exception {
    // }
}