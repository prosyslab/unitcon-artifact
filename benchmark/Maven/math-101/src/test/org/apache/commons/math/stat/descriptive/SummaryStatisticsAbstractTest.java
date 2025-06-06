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
package org.apache.commons.math.stat.descriptive;


import junit.framework.TestCase;

import org.apache.commons.math.TestUtils;
/**
 * Test cases for the {@link SummaryStatisticsImpl} class.
 *
 * @version $Revision: 602305 $ $Date: 2007-12-08 03:51:23 +0100 (sam., 08 dec. 2007) $
 * @deprecated should be moved down into SummaryStatisticsTest
 *   when SummaryStatisticsImpl is removed in 2.0
 */

public abstract class SummaryStatisticsAbstractTest extends TestCase {
    private double one = 1;
    private float twoF = 2;
    private long twoL = 2;
    private int three = 3;
    private double mean = 2;
    private double sumSq = 18;
    private double sum = 8;
    private double var = 0.666666666666666666667;
    private double std = Math.sqrt(var);
    private double n = 4;
    private double min = 1;
    private double max = 3;
    private double tolerance = 10E-15;

    public SummaryStatisticsAbstractTest(String name) {
        super(name);
    }

    protected abstract SummaryStatistics createSummaryStatistics();

    /** test stats */
    public void testStats() {
        SummaryStatistics u = createSummaryStatistics();
        assertEquals("total count",0,u.getN(),tolerance);
        u.addValue(one);
        u.addValue(twoF);
        u.addValue(twoL);
        u.addValue(three);
        assertEquals("N",n,u.getN(),tolerance);
        assertEquals("sum",sum,u.getSum(),tolerance);
        assertEquals("sumsq",sumSq,u.getSumsq(),tolerance);
        assertEquals("var",var,u.getVariance(),tolerance);
        assertEquals("std",std,u.getStandardDeviation(),tolerance);
        assertEquals("mean",mean,u.getMean(),tolerance);
        assertEquals("min",min,u.getMin(),tolerance);
        assertEquals("max",max,u.getMax(),tolerance);
        u.clear();
        assertEquals("total count",0,u.getN(),tolerance);    
    }     

    public void testN0andN1Conditions() throws Exception {
        SummaryStatistics u = createSummaryStatistics();
        assertTrue("Mean of n = 0 set should be NaN", 
                Double.isNaN( u.getMean() ) );
        assertTrue("Standard Deviation of n = 0 set should be NaN", 
                Double.isNaN( u.getStandardDeviation() ) );
        assertTrue("Variance of n = 0 set should be NaN", 
                Double.isNaN(u.getVariance() ) );

        /* n=1 */
        u.addValue(one);
        assertTrue("mean should be one (n = 1)", 
                u.getMean() == one);
        assertTrue("geometric should be one (n = 1) instead it is " + u.getGeometricMean(), 
                u.getGeometricMean() == one);
        assertTrue("Std should be zero (n = 1)", 
                u.getStandardDeviation() == 0.0);
        assertTrue("variance should be zero (n = 1)", 
                u.getVariance() == 0.0);

        /* n=2 */               
        u.addValue(twoF);
        assertTrue("Std should not be zero (n = 2)", 
                u.getStandardDeviation() != 0.0);
        assertTrue("variance should not be zero (n = 2)", 
                u.getVariance() != 0.0);

    }

    public void testProductAndGeometricMean() throws Exception {
        SummaryStatistics u = createSummaryStatistics();
        u.addValue( 1.0 );
        u.addValue( 2.0 );
        u.addValue( 3.0 );
        u.addValue( 4.0 );

        assertEquals( "Geometric mean not expected", 2.213364, 
                u.getGeometricMean(), 0.00001 );
    }

    public void testNaNContracts() {
        SummaryStatistics u = createSummaryStatistics();
        assertTrue("mean not NaN",Double.isNaN(u.getMean())); 
        assertTrue("min not NaN",Double.isNaN(u.getMin())); 
        assertTrue("std dev not NaN",Double.isNaN(u.getStandardDeviation())); 
        assertTrue("var not NaN",Double.isNaN(u.getVariance())); 
        assertTrue("geom mean not NaN",Double.isNaN(u.getGeometricMean()));

        u.addValue(1.0);

        assertEquals( "mean not expected", 1.0, 
                u.getMean(), Double.MIN_VALUE);
        assertEquals( "variance not expected", 0.0, 
                u.getVariance(), Double.MIN_VALUE);
        assertEquals( "geometric mean not expected", 1.0, 
                u.getGeometricMean(), Double.MIN_VALUE);

        u.addValue(-1.0);

        assertTrue("geom mean not NaN",Double.isNaN(u.getGeometricMean()));

        u.addValue(0.0);

        assertTrue("geom mean not NaN",Double.isNaN(u.getGeometricMean()));

        //FiXME: test all other NaN contract specs
    }

    public void testGetSummary() {
        SummaryStatistics u = createSummaryStatistics();
        StatisticalSummary summary = u.getSummary();
        verifySummary(u, summary);
        u.addValue(1d);
        summary = u.getSummary();
        verifySummary(u, summary);
        u.addValue(2d);
        summary = u.getSummary();
        verifySummary(u, summary);
        u.addValue(2d);
        summary = u.getSummary();
        verifySummary(u, summary);     
    }

    public void testSerialization() {
        SummaryStatistics u = createSummaryStatistics();
        // Empty test
        TestUtils.checkSerializedEquality(u);
        SummaryStatistics s = (SummaryStatistics) TestUtils.serializeAndRecover(u);
        StatisticalSummary summary = s.getSummary();
        verifySummary(u, summary);

        // Add some data
        u.addValue(2d);
        u.addValue(1d);
        u.addValue(3d);
        u.addValue(4d);
        u.addValue(5d);

        // Test again
        TestUtils.checkSerializedEquality(u);
        s = (SummaryStatistics) TestUtils.serializeAndRecover(u);
        summary = s.getSummary();
        verifySummary(u, summary);

    }

    public void testEqualsAndHashCode() {
        SummaryStatistics u = createSummaryStatistics();
        SummaryStatistics t = null;
        int emptyHash = u.hashCode();
        assertTrue("reflexive", u.equals(u));
        assertFalse("non-null compared to null", u.equals(t));
        assertFalse("wrong type", u.equals(new Double(0)));
        t = createSummaryStatistics();
        assertTrue("empty instances should be equal", t.equals(u));
        assertTrue("empty instances should be equal", u.equals(t));
        assertEquals("empty hash code", emptyHash, t.hashCode());

        // Add some data to u
        u.addValue(2d);
        u.addValue(1d);
        u.addValue(3d);
        u.addValue(4d);
        assertFalse("different n's should make instances not equal", t.equals(u));
        assertFalse("different n's should make instances not equal", u.equals(t));
        assertTrue("different n's should make hashcodes different", 
                u.hashCode() != t.hashCode());

        //Add data in same order to t
        t.addValue(2d);
        t.addValue(1d);
        t.addValue(3d);
        t.addValue(4d);
        assertTrue("summaries based on same data should be equal", t.equals(u));
        assertTrue("summaries based on same data should be equal", u.equals(t));
        assertEquals("summaries based on same data should have same hashcodes", 
                u.hashCode(), t.hashCode());   

        // Clear and make sure summaries are indistinguishable from empty summary
        u.clear();
        t.clear();
        assertTrue("empty instances should be equal", t.equals(u));
        assertTrue("empty instances should be equal", u.equals(t));
        assertEquals("empty hash code", emptyHash, t.hashCode());
        assertEquals("empty hash code", emptyHash, u.hashCode());
    }

    private void verifySummary(SummaryStatistics u, StatisticalSummary s) {
        assertEquals("N",s.getN(),u.getN());
        TestUtils.assertEquals("sum",s.getSum(),u.getSum(),tolerance);
        TestUtils.assertEquals("var",s.getVariance(),u.getVariance(),tolerance);
        TestUtils.assertEquals("std",s.getStandardDeviation(),u.getStandardDeviation(),tolerance);
        TestUtils.assertEquals("mean",s.getMean(),u.getMean(),tolerance);
        TestUtils.assertEquals("min",s.getMin(),u.getMin(),tolerance);
        TestUtils.assertEquals("max",s.getMax(),u.getMax(),tolerance);   
    }
}
