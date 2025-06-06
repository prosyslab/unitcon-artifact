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
package com.alibaba.dubbo.config.url.test;


import com.alibaba.dubbo.common.logger.Logger;
import com.alibaba.dubbo.common.logger.LoggerFactory;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;


/**
 * @author haomin.liuhm
 */
public class ExporterSideConfigUrlTest extends UrlTestBase {

    private static final Logger log = LoggerFactory.getLogger(ExporterSideConfigUrlTest.class);

    // ======================================================
    //   tests start
    // ======================================================  
    @BeforeClass
    public static void start() {


    }


    @Before
    public void setUp() {

        initServConf();

        return;
    }

    @After()
    public void teardown() {
    }

    // @Test
    // public void exporterMethodConfigUrlTest() {

    //     verifyExporterUrlGeneration(methodConfForService, methodConfForServiceTable);
    // }

    // @Test
    // public void exporterServiceConfigUrlTest() {

    //     verifyExporterUrlGeneration(servConf, servConfTable);
    // }

    // @Test
    // public void exporterProviderConfigUrlTest() {

    //     verifyExporterUrlGeneration(provConf, provConfTable);
    // }

    // @Test
    // public void exporterRegistryConfigUrlTest() {

    //     //verifyExporterUrlGeneration(regConfForService, regConfForServiceTable);
    // }


    protected <T> void verifyExporterUrlGeneration(T config, Object[][] dataTable) {

        // 1. fill corresponding config with data
        ////////////////////////////////////////////////////////////
        fillConfigs(config, dataTable, TESTVALUE1);

        // 2. export service and get url parameter string from db
        ////////////////////////////////////////////////////////////
        servConf.export();
        String paramStringFromDb = getProviderParamString();
        try {
            paramStringFromDb = URLDecoder.decode(paramStringFromDb, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            // impossible
        }


        assertUrlStringWithLocalTable(paramStringFromDb, dataTable, config.getClass().getName(), TESTVALUE1);


        // 4. unexport service
        ////////////////////////////////////////////////////////////
        servConf.unexport();
    }
}