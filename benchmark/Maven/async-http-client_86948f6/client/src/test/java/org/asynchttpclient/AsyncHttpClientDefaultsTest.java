package org.asynchttpclient;

import static org.asynchttpclient.config.AsyncHttpClientConfigDefaults.ASYNC_CLIENT_CONFIG_ROOT;

import org.asynchttpclient.config.AsyncHttpClientConfigDefaults;
import org.asynchttpclient.config.AsyncHttpClientConfigHelper;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.lang.reflect.Method;

// @Test
// public class AsyncHttpClientDefaultsTest {

//     public void testDefaultMaxTotalConnections() {
//         Assert.assertEquals(AsyncHttpClientConfigDefaults.defaultMaxConnections(), -1);
//         testIntegerSystemProperty("maxConnections", "defaultMaxConnections", "100");
//     }

//     public void testDefaultMaxConnectionPerHost() {
//         Assert.assertEquals(AsyncHttpClientConfigDefaults.defaultMaxConnectionsPerHost(), -1);
//         testIntegerSystemProperty("maxConnectionsPerHost", "defaultMaxConnectionsPerHost", "100");
//     }

//     public void testDefaultConnectTimeOut() {
//         Assert.assertEquals(AsyncHttpClientConfigDefaults.defaultConnectTimeout(), 5 * 1000);
//         testIntegerSystemProperty("connectTimeout", "defaultConnectTimeout", "100");
//     }

//     public void testDefaultPooledConnectionIdleTimeout() {
//         Assert.assertEquals(AsyncHttpClientConfigDefaults.defaultPooledConnectionIdleTimeout(), 60 * 1000);
//         testIntegerSystemProperty("pooledConnectionIdleTimeout", "defaultPooledConnectionIdleTimeout", "100");
//     }

//     public void testDefaultReadTimeout() {
//         Assert.assertEquals(AsyncHttpClientConfigDefaults.defaultReadTimeout(), 60 * 1000);
//         testIntegerSystemProperty("readTimeout", "defaultReadTimeout", "100");
//     }

//     public void testDefaultRequestTimeout() {
//         Assert.assertEquals(AsyncHttpClientConfigDefaults.defaultRequestTimeout(), 60 * 1000);
//         testIntegerSystemProperty("requestTimeout", "defaultRequestTimeout", "100");
//     }

//     public void testDefaultConnectionTtl() {
//         Assert.assertEquals(AsyncHttpClientConfigDefaults.defaultConnectionTtl(), -1);
//         testIntegerSystemProperty("connectionTtl", "defaultConnectionTtl", "100");
//     }

//     public void testDefaultFollowRedirect() {
//         Assert.assertFalse(AsyncHttpClientConfigDefaults.defaultFollowRedirect());
//         testBooleanSystemProperty("followRedirect", "defaultFollowRedirect", "true");
//     }

//     public void testDefaultMaxRedirects() {
//         Assert.assertEquals(AsyncHttpClientConfigDefaults.defaultMaxRedirects(), 5);
//         testIntegerSystemProperty("maxRedirects", "defaultMaxRedirects", "100");
//     }

//     public void testDefaultCompressionEnforced() {
//         Assert.assertFalse(AsyncHttpClientConfigDefaults.defaultCompressionEnforced());
//         testBooleanSystemProperty("compressionEnforced", "defaultCompressionEnforced", "true");
//     }

//     public void testDefaultUserAgent() {
//         Assert.assertEquals(AsyncHttpClientConfigDefaults.defaultUserAgent(), "AHC/2.0");
//         testStringSystemProperty("userAgent", "defaultUserAgent", "MyAHC");
//     }

//     public void testDefaultUseProxySelector() {
//         Assert.assertFalse(AsyncHttpClientConfigDefaults.defaultUseProxySelector());
//         testBooleanSystemProperty("useProxySelector", "defaultUseProxySelector", "true");
//     }

//     public void testDefaultUseProxyProperties() {
//         Assert.assertFalse(AsyncHttpClientConfigDefaults.defaultUseProxyProperties());
//         testBooleanSystemProperty("useProxyProperties", "defaultUseProxyProperties", "true");
//     }

//     public void testDefaultStrict302Handling() {
//         Assert.assertFalse(AsyncHttpClientConfigDefaults.defaultStrict302Handling());
//         testBooleanSystemProperty("strict302Handling", "defaultStrict302Handling", "true");
//     }

//     public void testDefaultAllowPoolingConnection() {
//         Assert.assertTrue(AsyncHttpClientConfigDefaults.defaultKeepAlive());
//         testBooleanSystemProperty("keepAlive", "defaultKeepAlive", "false");
//     }

//     public void testDefaultMaxRequestRetry() {
//         Assert.assertEquals(AsyncHttpClientConfigDefaults.defaultMaxRequestRetry(), 5);
//         testIntegerSystemProperty("maxRequestRetry", "defaultMaxRequestRetry", "100");
//     }

//     public void testDefaultDisableUrlEncodingForBoundRequests() {
//         Assert.assertFalse(AsyncHttpClientConfigDefaults.defaultDisableUrlEncodingForBoundRequests());
//         testBooleanSystemProperty("disableUrlEncodingForBoundRequests", "defaultDisableUrlEncodingForBoundRequests", "true");
//     }

//     public void testDefaultAcceptAnyCertificate() {
//         Assert.assertFalse(AsyncHttpClientConfigDefaults.defaultAcceptAnyCertificate());
//         testBooleanSystemProperty("acceptAnyCertificate", "defaultAcceptAnyCertificate", "true");
//     }

//     private void testIntegerSystemProperty(String propertyName, String methodName, String value) {
//         String previous = System.getProperty(ASYNC_CLIENT_CONFIG_ROOT + propertyName);
//         System.setProperty(ASYNC_CLIENT_CONFIG_ROOT + propertyName, value);
//         AsyncHttpClientConfigHelper.reloadProperties();
//         try {
//             Method method = AsyncHttpClientConfigDefaults.class.getMethod(methodName, new Class[] {});
//             Assert.assertEquals(method.invoke(null, new Object[] {}), Integer.parseInt(value));
//         } catch (Exception e) {
//             Assert.fail("Couldn't find or execute method : " + methodName, e);
//         }
//         if (previous != null)
//             System.setProperty(ASYNC_CLIENT_CONFIG_ROOT + propertyName, previous);
//         else
//             System.clearProperty(ASYNC_CLIENT_CONFIG_ROOT + propertyName);
//     }

//     private void testBooleanSystemProperty(String propertyName, String methodName, String value) {
//         String previous = System.getProperty(ASYNC_CLIENT_CONFIG_ROOT + propertyName);
//         System.setProperty(ASYNC_CLIENT_CONFIG_ROOT + propertyName, value);
//         AsyncHttpClientConfigHelper.reloadProperties();
//         try {
//             Method method = AsyncHttpClientConfigDefaults.class.getMethod(methodName, new Class[] {});
//             Assert.assertEquals(method.invoke(null, new Object[] {}), Boolean.parseBoolean(value));
//         } catch (Exception e) {
//             Assert.fail("Couldn't find or execute method : " + methodName, e);
//         }
//         if (previous != null)
//             System.setProperty(ASYNC_CLIENT_CONFIG_ROOT + propertyName, previous);
//         else
//             System.clearProperty(ASYNC_CLIENT_CONFIG_ROOT + propertyName);
//     }

//     private void testStringSystemProperty(String propertyName, String methodName, String value) {
//         String previous = System.getProperty(ASYNC_CLIENT_CONFIG_ROOT + propertyName);
//         System.setProperty(ASYNC_CLIENT_CONFIG_ROOT + propertyName, value);
//         AsyncHttpClientConfigHelper.reloadProperties();
//         try {
//             Method method = AsyncHttpClientConfigDefaults.class.getMethod(methodName, new Class[] {});
//             Assert.assertEquals(method.invoke(null, new Object[] {}), value);
//         } catch (Exception e) {
//             Assert.fail("Couldn't find or execute method : " + methodName, e);
//         }
//         if (previous != null)
//             System.setProperty(ASYNC_CLIENT_CONFIG_ROOT + propertyName, previous);
//         else
//             System.clearProperty(ASYNC_CLIENT_CONFIG_ROOT + propertyName);
//     }
// }
