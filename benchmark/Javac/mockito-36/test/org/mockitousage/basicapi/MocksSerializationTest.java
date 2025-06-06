/*
 * Copyright (c) 2007 Mockito contributors
 * This program is made available under the terms of the MIT License.
 */
package org.mockitousage.basicapi;

import static org.mockito.Mockito.*;

import java.io.*;
import java.util.*;

import org.junit.Test;
import org.mockito.InOrder;
import org.mockito.internal.matchers.Any;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.mockitousage.IMethods;
import org.mockitoutil.TestBase;

@SuppressWarnings("serial")
public class MocksSerializationTest extends TestBase implements Serializable {

    private static final long serialVersionUID = 6160482220413048624L;

    @Test
    public void shouldAllowMockToBeSerializable() throws Exception {
        // given
        IMethods mock = mock(IMethods.class, withSettings().extraInterfaces(Serializable.class).serializable());

        // when-serialize then-deserialize
        serializeAndBack(mock);
    }

    @Test
    public void shouldAllowMockAndBooleanValueToSerializable() throws Exception {
        // given
        IMethods mock = mock(IMethods.class, withSettings().extraInterfaces(Serializable.class).serializable());
        when(mock.booleanReturningMethod()).thenReturn(true);

        // when
        ByteArrayOutputStream serialized = serializeMock(mock);

        // then
        IMethods readObject = deserializeMock(serialized, IMethods.class);
        assertTrue(readObject.booleanReturningMethod());
    }

    @Test
    public void shouldAllowMockAndStringValueToBeSerializable() throws Exception {
        // given
        IMethods mock = mock(IMethods.class, withSettings().extraInterfaces(Serializable.class).serializable());
        String value = "value";
        when(mock.stringReturningMethod()).thenReturn(value);

        // when
        ByteArrayOutputStream serialized = serializeMock(mock);

        // then
        IMethods readObject = deserializeMock(serialized, IMethods.class);
        assertEquals(value, readObject.stringReturningMethod());
    }

    @Test
    public void shouldAllMockAndSerializableValueToBeSerialized() throws Exception {
        // given
        IMethods mock = mock(IMethods.class, withSettings().extraInterfaces(Serializable.class).serializable());
        List<?> value = Collections.emptyList();
        when(mock.objectReturningMethodNoArgs()).thenReturn(value);

        // when
        ByteArrayOutputStream serialized = serializeMock(mock);

        // then
        IMethods readObject = deserializeMock(serialized, IMethods.class);
        assertEquals(value, readObject.objectReturningMethodNoArgs());
    }

    @Test
    public void shouldSerializeMethodCallWithParametersThatAreSerializable() throws Exception {
        IMethods mock = mock(IMethods.class, withSettings().extraInterfaces(Serializable.class).serializable());
        List<?> value = Collections.emptyList();
        when(mock.objectArgMethod(value)).thenReturn(value);

        // when
        ByteArrayOutputStream serialized = serializeMock(mock);

        // then
        IMethods readObject = deserializeMock(serialized, IMethods.class);
        assertEquals(value, readObject.objectArgMethod(value));
    }

    @Test
    public void shouldSerializeMethodCallsUsingAnyStringMatcher() throws Exception {
        IMethods mock = mock(IMethods.class, withSettings().extraInterfaces(Serializable.class).serializable());
        List<?> value = Collections.emptyList();
        when(mock.objectArgMethod(anyString())).thenReturn(value);

        // when
        ByteArrayOutputStream serialized = serializeMock(mock);

        // then
        IMethods readObject = deserializeMock(serialized, IMethods.class);
        assertEquals(value, readObject.objectArgMethod(""));
    }

    @Test
    public void shouldVerifyCalledNTimesForSerializedMock() throws Exception {
        IMethods mock = mock(IMethods.class, withSettings().extraInterfaces(Serializable.class).serializable());
        List<?> value = Collections.emptyList();
        when(mock.objectArgMethod(anyString())).thenReturn(value);
        mock.objectArgMethod("");

        // when
        ByteArrayOutputStream serialized = serializeMock(mock);

        // then
        IMethods readObject = deserializeMock(serialized, IMethods.class);
        verify(readObject, times(1)).objectArgMethod("");
    }

    @Test
    public void shouldVerifyEvenIfSomeMethodsCalledAfterSerialization() throws Exception {
        //given
        IMethods mock = mock(IMethods.class, withSettings().extraInterfaces(Serializable.class).serializable());

        // when
        mock.simpleMethod(1);
        ByteArrayOutputStream serialized = serializeMock(mock);
        IMethods readObject = deserializeMock(serialized, IMethods.class);
        readObject.simpleMethod(1);

        // then
        verify(readObject, times(2)).simpleMethod(1);

        //this test is working because it seems that java serialization mechanism replaces all instances
        //of serialized object in the object graph (if there are any)
    }

    class Bar implements Serializable {
        Foo foo;
    }

    class Foo implements Serializable {
        Bar bar;
        Foo() {
            bar = new Bar();
            bar.foo = this;
        }
    }

    @Test
    public void shouldSerializationWork() throws Exception {
        //given
        Foo foo = new Foo();
        //when
        foo = serializeAndBack(foo);
        //then
        assertSame(foo, foo.bar.foo);
    }

    @Test
    public void shouldStubEvenIfSomeMethodsCalledAfterSerialization() throws Exception {
        //given
        IMethods mock = mock(IMethods.class, withSettings().extraInterfaces(Serializable.class).serializable());

        // when
        when(mock.simpleMethod(1)).thenReturn("foo");
        ByteArrayOutputStream serialized = serializeMock(mock);
        IMethods readObject = deserializeMock(serialized, IMethods.class);
        when(readObject.simpleMethod(2)).thenReturn("bar");

        // then
        assertEquals("foo", readObject.simpleMethod(1));
        assertEquals("bar", readObject.simpleMethod(2));
    }

    @Test
    public void shouldVerifyCallOrderForSerializedMock() throws Exception {
        IMethods mock = mock(IMethods.class, withSettings().extraInterfaces(Serializable.class).serializable());
        IMethods mock2 = mock(IMethods.class, withSettings().extraInterfaces(Serializable.class).serializable());
        mock.arrayReturningMethod();
        mock2.arrayReturningMethod();

        // when
        ByteArrayOutputStream serialized = serializeMock(mock);
        ByteArrayOutputStream serialized2 = serializeMock(mock2);

        // then
        IMethods readObject = deserializeMock(serialized, IMethods.class);
        IMethods readObject2 = deserializeMock(serialized2, IMethods.class);
        InOrder inOrder = inOrder(readObject, readObject2);
        inOrder.verify(readObject).arrayReturningMethod();
        inOrder.verify(readObject2).arrayReturningMethod();
    }

    @Test
    public void shouldRememberInteractionsForSerializedMock() throws Exception {
        IMethods mock = mock(IMethods.class, withSettings().extraInterfaces(Serializable.class).serializable());
        List<?> value = Collections.emptyList();
        when(mock.objectArgMethod(anyString())).thenReturn(value);
        mock.objectArgMethod("happened");

        // when
        ByteArrayOutputStream serialized = serializeMock(mock);

        // then
        IMethods readObject = deserializeMock(serialized, IMethods.class);
        verify(readObject, never()).objectArgMethod("never happened");
    }

    @Test
    public void shouldSerializeWithStubbingCallback() throws Exception {

        // given
        IMethods mock = mock(IMethods.class, withSettings().extraInterfaces(Serializable.class).serializable());
        final String string = "return value";
        when(mock.objectArgMethod(anyString())).thenAnswer(new Answer<Object>() {
            public Object answer(InvocationOnMock invocation) {
                invocation.getArguments();
                invocation.getMock();
                return string;
            }
        });

        // when
        ByteArrayOutputStream serialized = serializeMock(mock);

        // then
        IMethods readObject = deserializeMock(serialized, IMethods.class);
        assertEquals(string, readObject.objectArgMethod(""));
    }

    @SuppressWarnings("unchecked")
    @Test
    public void shouldSerializeWithRealObjectSpy() throws Exception {
        // given
        List<Object> list = new ArrayList<Object>();
        List<Object> spy = mock(ArrayList.class, withSettings()
                        .spiedInstance(list)
                        .defaultAnswer(CALLS_REAL_METHODS)
                        .serializable());
        when(spy.size()).thenReturn(100);

        // when
        ByteArrayOutputStream serialized = serializeMock(spy);

        // then
        List<?> readObject = deserializeMock(serialized, List.class);
        assertEquals(100, readObject.size());
    }

    @Test
    public void shouldSerializeObjectMock() {}
// Defects4J: flaky method
//     @Test
//     public void shouldSerializeObjectMock() throws Exception {
//         // given
//         Any mock = mock(Any.class);
// 
//         // when
//         ByteArrayOutputStream serialized = serializeMock(mock);
// 
//         // then
//         deserializeMock(serialized, Any.class);
//     }

    @Test
    public void shouldSerializeRealPartialMock() {}
// Defects4J: flaky method
//     @Test
//     public void shouldSerializeRealPartialMock() throws Exception {
//         // given
//         Any mock = mock(Any.class, withSettings().serializable());
//         when(mock.matches(anyObject())).thenCallRealMethod();
// 
//         // when
//         ByteArrayOutputStream serialized = serializeMock(mock);
// 
//         // then
//         Any readObject = deserializeMock(serialized, Any.class);
//         readObject.matches("");
//     }

}