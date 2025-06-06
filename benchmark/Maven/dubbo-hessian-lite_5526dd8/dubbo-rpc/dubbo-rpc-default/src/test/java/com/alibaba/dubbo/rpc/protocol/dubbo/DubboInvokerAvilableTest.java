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
package com.alibaba.dubbo.rpc.protocol.dubbo;


import com.alibaba.dubbo.common.Constants;
import com.alibaba.dubbo.common.URL;
import com.alibaba.dubbo.common.extension.ExtensionLoader;
import com.alibaba.dubbo.remoting.exchange.ExchangeClient;
import com.alibaba.dubbo.rpc.ProxyFactory;
import com.alibaba.dubbo.rpc.protocol.dubbo.support.ProtocolUtils;

import junit.framework.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.lang.reflect.Field;

import static org.junit.Assert.fail;

/**
 * 测试 dubboInvoker的Avilable状态
 *
 * @author chao.liuc
 */
public class DubboInvokerAvilableTest {
    private static DubboProtocol protocol = DubboProtocol.getDubboProtocol();
    private static ProxyFactory proxy = ExtensionLoader.getExtensionLoader(ProxyFactory.class).getAdaptiveExtension();

    // @BeforeClass
    // public static void setUpBeforeClass() throws Exception {
    // }

    // @Before
    // public void setUp() throws Exception {
    // }

    // @Test
    // public void test_Normal_available() {
    //     URL url = URL.valueOf("dubbo://127.0.0.1:20883/hi");
    //     ProtocolUtils.export(new DemoServiceImpl(), IDemoService.class, url);

    //     DubboInvoker<?> invoker = (DubboInvoker<?>) protocol.refer(IDemoService.class, url);
    //     Assert.assertEquals(true, invoker.isAvailable());
    //     invoker.destroy();
    //     Assert.assertEquals(false, invoker.isAvailable());
    // }

    // @Test
    // public void test_Normal_ChannelReadOnly() throws Exception {
    //     URL url = URL.valueOf("dubbo://127.0.0.1:20883/hi");
    //     ProtocolUtils.export(new DemoServiceImpl(), IDemoService.class, url);

    //     DubboInvoker<?> invoker = (DubboInvoker<?>) protocol.refer(IDemoService.class, url);
    //     Assert.assertEquals(true, invoker.isAvailable());

    //     getClients(invoker)[0].setAttribute(Constants.CHANNEL_ATTRIBUTE_READONLY_KEY, Boolean.TRUE);

    //     Assert.assertEquals(false, invoker.isAvailable());

    //     //恢复状态，invoker共享连接
    //     getClients(invoker)[0].removeAttribute(Constants.CHANNEL_ATTRIBUTE_READONLY_KEY);
    // }

    // @Test
    // public void test_NoInvokers() throws Exception {
    //     URL url = URL.valueOf("dubbo://127.0.0.1:20883/hi?connections=1");
    //     ProtocolUtils.export(new DemoServiceImpl(), IDemoService.class, url);

    //     DubboInvoker<?> invoker = (DubboInvoker<?>) protocol.refer(IDemoService.class, url);

    //     ExchangeClient[] clients = getClients(invoker);
    //     clients[0].close();
    //     Assert.assertEquals(false, invoker.isAvailable());

    // }

    // @Test
    // public void test_Lazy_ChannelReadOnly() throws Exception {
    //     URL url = URL.valueOf("dubbo://127.0.0.1:20883/hi?lazy=true&connections=1");
    //     ProtocolUtils.export(new DemoServiceImpl(), IDemoService.class, url);

    //     DubboInvoker<?> invoker = (DubboInvoker<?>) protocol.refer(IDemoService.class, url);
    //     Assert.assertEquals(true, invoker.isAvailable());

    //     try {
    //         getClients(invoker)[0].setAttribute(Constants.CHANNEL_ATTRIBUTE_READONLY_KEY, Boolean.TRUE);
    //         fail();
    //     } catch (IllegalStateException e) {

    //     }
    //     //invoke method --> init client

    //     IDemoService service = (IDemoService) proxy.getProxy(invoker);
    //     Assert.assertEquals("ok", service.get());

    //     Assert.assertEquals(true, invoker.isAvailable());
    //     getClients(invoker)[0].setAttribute(Constants.CHANNEL_ATTRIBUTE_READONLY_KEY, Boolean.TRUE);
    //     Assert.assertEquals(false, invoker.isAvailable());
    // }

    private ExchangeClient[] getClients(DubboInvoker<?> invoker) throws Exception {
        Field field = DubboInvoker.class.getDeclaredField("clients");
        field.setAccessible(true);
        ExchangeClient[] clients = (ExchangeClient[]) field.get(invoker);
        Assert.assertEquals(1, clients.length);
        return clients;
    }

    public interface IDemoService {
        public String get();
    }

    public class DemoServiceImpl implements IDemoService {
        public String get() {
            return "ok";
        }
    }
}