package com.alibaba.dubbo.remoting.buffer;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

/**
 * @author <a href="mailto:gang.lvg@taobao.com">kimi</a>
 */
public class ChannelBufferStreamTest {

    // @Test
    // public void testAll() throws Exception {
    //     ChannelBuffer buf = ChannelBuffers.dynamicBuffer();

    //     try {
    //         new ChannelBufferOutputStream(null);
    //         fail();
    //     } catch (NullPointerException e) {
    //         // Expected
    //     }

    //     ChannelBufferOutputStream out = new ChannelBufferOutputStream(buf);
    //     assertSame(buf, out.buffer());
    //     out.write(new byte[0]);
    //     out.write(new byte[]{1, 2, 3, 4});
    //     out.write(new byte[]{1, 3, 3, 4}, 0, 0);
    //     out.close();

    //     try {
    //         new ChannelBufferInputStream(null);
    //         fail();
    //     } catch (NullPointerException e) {
    //         // Expected
    //     }

    //     try {
    //         new ChannelBufferInputStream(null, 0);
    //         fail();
    //     } catch (NullPointerException e) {
    //         // Expected
    //     }

    //     try {
    //         new ChannelBufferInputStream(buf, -1);
    //     } catch (IllegalArgumentException e) {
    //         // Expected
    //     }

    //     try {
    //         new ChannelBufferInputStream(buf, buf.capacity() + 1);
    //     } catch (IndexOutOfBoundsException e) {
    //         // Expected
    //     }

    //     ChannelBufferInputStream in = new ChannelBufferInputStream(buf);

    //     assertTrue(in.markSupported());
    //     in.mark(Integer.MAX_VALUE);

    //     assertEquals(buf.writerIndex(), in.skip(Long.MAX_VALUE));
    //     assertFalse(buf.readable());

    //     in.reset();
    //     assertEquals(0, buf.readerIndex());

    //     assertEquals(4, in.skip(4));
    //     assertEquals(4, buf.readerIndex());
    //     in.reset();


    //     byte[] tmp = new byte[13];
    //     in.read(tmp);

    //     assertEquals(1, tmp[0]);
    //     assertEquals(2, tmp[1]);
    //     assertEquals(3, tmp[2]);
    //     assertEquals(4, tmp[3]);

    //     assertEquals(-1, in.read());
    //     assertEquals(-1, in.read(tmp));

    //     in.close();

    //     assertEquals(buf.readerIndex(), in.readBytes());
    // }
}
