/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.qpid.proton.amqp.transport;

import static org.junit.Assert.assertEquals;

import org.apache.qpid.proton.amqp.Binary;
import org.apache.qpid.proton.amqp.UnsignedInteger;
import org.junit.Test;

public class TransferTest {

    // @Test
    // public void testCopyTransfers() {
    //     Transfer transfer = new Transfer();
    //     transfer.setHandle(UnsignedInteger.ONE);
    //     transfer.setDeliveryTag(new Binary(new byte[] {0, 1}));
    //     transfer.setMessageFormat(UnsignedInteger.ZERO);
    //     transfer.setDeliveryId(UnsignedInteger.valueOf(127));
    //     transfer.setAborted(false);
    //     transfer.setBatchable(true);
    //     transfer.setRcvSettleMode(ReceiverSettleMode.SECOND);

    //     final Transfer copyOf = transfer.copy();

    //     assertEquals(transfer.getHandle(), copyOf.getHandle());
    //     assertEquals(transfer.getMessageFormat(), copyOf.getMessageFormat());
    //     assertEquals(transfer.getDeliveryTag(), copyOf.getDeliveryTag());
    //     assertEquals(transfer.getDeliveryId(), copyOf.getDeliveryId());
    //     assertEquals(transfer.getAborted(), copyOf.getAborted());
    //     assertEquals(transfer.getBatchable(), copyOf.getBatchable());
    //     assertEquals(transfer.getRcvSettleMode(), copyOf.getRcvSettleMode());
    // }
}
