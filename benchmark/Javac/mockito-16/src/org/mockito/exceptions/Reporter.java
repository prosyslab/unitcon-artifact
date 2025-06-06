/*
 * Copyright (c) 2007 Mockito contributors
 * This program is made available under the terms of the MIT License.
 */
package org.mockito.exceptions;

import static org.mockito.exceptions.Pluralizer.*;
import static org.mockito.internal.util.StringJoiner.*;

import java.util.List;

import org.mockito.exceptions.base.MockitoAssertionError;
import org.mockito.exceptions.base.MockitoException;
import org.mockito.exceptions.misusing.InvalidUseOfMatchersException;
import org.mockito.exceptions.misusing.MissingMethodInvocationException;
import org.mockito.exceptions.misusing.NotAMockException;
import org.mockito.exceptions.misusing.NullInsteadOfMockException;
import org.mockito.exceptions.misusing.UnfinishedStubbingException;
import org.mockito.exceptions.misusing.UnfinishedVerificationException;
import org.mockito.exceptions.misusing.WrongTypeOfReturnValue;
import org.mockito.exceptions.verification.ArgumentsAreDifferent;
import org.mockito.exceptions.verification.NeverWantedButInvoked;
import org.mockito.exceptions.verification.NoInteractionsWanted;
import org.mockito.exceptions.verification.SmartNullPointerException;
import org.mockito.exceptions.verification.TooLittleActualInvocations;
import org.mockito.exceptions.verification.TooManyActualInvocations;
import org.mockito.exceptions.verification.VerificationInOrderFailure;
import org.mockito.exceptions.verification.WantedButNotInvoked;
import org.mockito.exceptions.verification.junit.JUnitTool;
import org.mockito.internal.debugging.Location;

/**
 * Reports verification and misusing errors.
 * <p>
 * One of the key points of mocking library is proper verification/exception
 * messages. All messages in one place makes it easier to tune and amend them.
 * <p>
 * Reporter can be injected and therefore is easily testable.
 * <p>
 * Generally, exception messages are full of line breaks to make them easy to
 * read (xunit plugins take only fraction of screen on modern IDEs).
 */
public class Reporter {

    public void checkedExceptionInvalid(Throwable t) {
        throw new MockitoException(join(
                "Checked exception is invalid for this method!",
                "Invalid: " + t
                ));
    }

    public void cannotStubWithNullThrowable() {
        throw new MockitoException(join(
                "Cannot stub with null throwable!"
                ));

    }
    
    public void unfinishedStubbing(Location location) {
        throw new UnfinishedStubbingException(join(
                "Unfinished stubbing detected here:",
                location,
                "",
                "E.g. thenReturn() may be missing.",
                "Examples of correct stubbing:",
                "    when(mock.isOk()).thenReturn(true);",
                "    when(mock.isOk()).thenThrow(exception);",
                "    doThrow(exception).when(mock).someVoidMethod();",
                "Hints:",
                " 1. missing thenReturn()",
                " 2. although stubbed methods may return mocks, you cannot inline mock creation (mock()) call inside a thenReturn method (see issue 53)",
                ""
        ));
    }

    public void missingMethodInvocation() {
        throw new MissingMethodInvocationException(""); // Unitcon modified here: removed log for exact stack trace comparison
    }

    public void unfinishedVerificationException(Location location) {
        UnfinishedVerificationException exception = new UnfinishedVerificationException(join(
                "Missing method call for verify(mock) here:",
                location,
                "",
                "Example of correct verification:",
                "    verify(mock).doSomething()",
                "",
                "Also, this error might show up because you verify final/private/equals() or hashCode() method.",
                "Those methods *cannot* be stubbed/verified.",
                ""
        ));
        
        throw exception;
    }
    
    public void notAMockPassedToVerify() {
        throw new NotAMockException(join(
                "Argument passed to verify() is not a mock!",
                "Examples of correct verifications:",
                "    verify(mock).someMethod();",
                "    verify(mock, times(10)).someMethod();",
                "    verify(mock, atLeastOnce()).someMethod();"
        ));
    }
    
    public void nullPassedToVerify() {
        throw new NullInsteadOfMockException(join(
                "Argument passed to verify() is null!",
                "Examples of correct verifications:",
                "    verify(mock).someMethod();",
                "    verify(mock, times(10)).someMethod();",
                "    verify(mock, atLeastOnce()).someMethod();",
                "Also, if you use @Mock annotation don't miss initMocks()"
        ));
    }    
    
    public void notAMockPassedToWhenMethod() {
        throw new NotAMockException(join(
                "Argument passed to when() is not a mock!",
                "Example of correct stubbing:",
                "    doThrow(new RuntimeException()).when(mock).someMethod();"
        ));
    }
    
    public void nullPassedToWhenMethod() {
        throw new NullInsteadOfMockException(join(
                "Argument passed to when() is null!",
                "Example of correct stubbing:",
                "    doThrow(new RuntimeException()).when(mock).someMethod();",                
                "Also, if you use @Mock annotation don't miss initMocks()"
        ));
    }
    
    public void mocksHaveToBePassedToVerifyNoMoreInteractions() {
        throw new MockitoException(join(
                "Method requires argument(s)!",
                "Pass mocks that should be verified, e.g:",
                "    verifyNoMoreInteractions(mockOne, mockTwo);",
                "    verifyZeroInteractions(mockOne, mockTwo);"
                ));
    }

    public void notAMockPassedToVerifyNoMoreInteractions() {
        throw new NotAMockException(join(
            "Argument(s) passed is not a mock!",
            "Examples of correct verifications:",
            "    verifyNoMoreInteractions(mockOne, mockTwo);",
            "    verifyZeroInteractions(mockOne, mockTwo);"
        ));
    }
    
    public void nullPassedToVerifyNoMoreInteractions() {
        throw new NullInsteadOfMockException(join(
                "Argument(s) passed is null!",
                "Examples of correct verifications:",
                "    verifyNoMoreInteractions(mockOne, mockTwo);",
                "    verifyZeroInteractions(mockOne, mockTwo);"
        ));
    }

    public void notAMockPassedWhenCreatingInOrder() {
        throw new NotAMockException(join(
                "Argument(s) passed is not a mock!",
                "Pass mocks that require verification in order.",
                "For example:",
                "    InOrder inOrder = inOrder(mockOne, mockTwo);"
                ));
    } 
    
    public void nullPassedWhenCreatingInOrder() {
        throw new NullInsteadOfMockException(join(
                "Argument(s) passed is null!",
                "Pass mocks that require verification in order.",
                "For example:",
                "    InOrder inOrder = inOrder(mockOne, mockTwo);"
                ));
    }
    
    public void mocksHaveToBePassedWhenCreatingInOrder() {
        throw new MockitoException(join(
                "Method requires argument(s)!",
                "Pass mocks that require verification in order.",
                "For example:",
                "    InOrder inOrder = inOrder(mockOne, mockTwo);"
                ));
    }
    
    public void inOrderRequiresFamiliarMock() {
        throw new MockitoException(join(
                "InOrder can only verify mocks that were passed in during creation of InOrder.",
                "For example:",
                "    InOrder inOrder = inOrder(mockOne);",
                "    inOrder.verify(mockOne).doStuff();"
                ));
    }
    
    public void invalidUseOfMatchers(int expectedMatchersCount, int recordedMatchersCount) {
        throw new InvalidUseOfMatchersException(join(
                "Invalid use of argument matchers!",
                expectedMatchersCount + " matchers expected, " + recordedMatchersCount + " recorded.",
                "This exception may occur if matchers are combined with raw values:",        
                "    //incorrect:",
                "    someMethod(anyObject(), \"raw String\");",
                "When using matchers, all arguments have to be provided by matchers.",
                "For example:",
                "    //correct:",
                "    someMethod(anyObject(), eq(\"String by matcher\"));",
                "",
                "For more info see javadoc for Matchers class."
        ));
    }    

    public void argumentsAreDifferent(String wanted, String actual, Location actualLocation) {
        String message = join("Argument(s) are different! Wanted:", 
                wanted,
                new Location(),
                "Actual invocation has different arguments:",
                actual,
                actualLocation,
                ""
                );
        
        if (JUnitTool.hasJUnit()) {
            throw JUnitTool.createArgumentsAreDifferentException(message, wanted, actual);
        } else {
            throw new ArgumentsAreDifferent(message);
        }
    }
    
    public void wantedButNotInvoked(PrintableInvocation wanted) {
        throw new WantedButNotInvoked(createWantedButNotInvokedMessage(wanted));
    }

    public void wantedButNotInvoked(PrintableInvocation wanted, List<? extends PrintableInvocation> invocations) {
        String allInvocations;
        if (invocations.isEmpty()) {
            allInvocations = "Actually, there were zero interactions with this mock.\n";
        } else {
            StringBuilder sb = new StringBuilder("\nHowever, there were other interactions with this mock:\n");
            for (PrintableInvocation i : invocations) {
                 sb.append(i.getLocation());
                 sb.append("\n");
            }
            allInvocations = sb.toString();
        }
        
        String message = createWantedButNotInvokedMessage(wanted);
        throw new WantedButNotInvoked(message + allInvocations);
    }

    private String createWantedButNotInvokedMessage(PrintableInvocation wanted) {
        return join(
                "Wanted but not invoked:",
                wanted.toString(),
                new Location(),
                ""
        );
    }
    
    public void wantedButNotInvokedInOrder(PrintableInvocation wanted, PrintableInvocation previous) {
        throw new VerificationInOrderFailure(join(
                    "Verification in order failure",
                    "Wanted but not invoked:",
                    wanted.toString(),
                    new Location(),
                    "Wanted anywhere AFTER following interaction:",
                    previous.toString(),
                    previous.getLocation(),
                    ""
        ));
    }

    public void tooManyActualInvocations(int wantedCount, int actualCount, PrintableInvocation wanted, Location firstUndesired) {
        String message = createTooManyInvocationsMessage(wantedCount, actualCount, wanted, firstUndesired);
        throw new TooManyActualInvocations(message);
    }

    private String createTooManyInvocationsMessage(int wantedCount, int actualCount, PrintableInvocation wanted,
            Location firstUndesired) {
        return join(
                wanted.toString(),
                "Wanted " + Pluralizer.pluralize(wantedCount) + ":",
                new Location(),
                "But was " + pluralize(actualCount) + ". Undesired invocation:",
                firstUndesired,
                ""
        );
    }
    
    public void neverWantedButInvoked(PrintableInvocation wanted, Location firstUndesired) {
        throw new NeverWantedButInvoked(join(
                wanted.toString(),
                "Never wanted here:",
                new Location(),
                "But invoked here:",
                firstUndesired,
                ""
        ));
    }    
    
    public void tooManyActualInvocationsInOrder(int wantedCount, int actualCount, PrintableInvocation wanted, Location firstUndesired) {
        String message = createTooManyInvocationsMessage(wantedCount, actualCount, wanted, firstUndesired);
        throw new VerificationInOrderFailure(join(
                "Verification in order failure:" + message
                ));
    }

    private String createTooLittleInvocationsMessage(Discrepancy discrepancy, PrintableInvocation wanted,
            Location lastActualInvocation) {
        String ending = 
            (lastActualInvocation != null)? lastActualInvocation + "\n" : "\n";
            
            String message = join(
                    wanted.toString(),
                    "Wanted " + discrepancy.getPluralizedWantedCount() + ":",
                    new Location(),
                    "But was " + discrepancy.getPluralizedActualCount() + ":", 
                    ending
            );
            return message;
    }
   
    public void tooLittleActualInvocations(Discrepancy discrepancy, PrintableInvocation wanted, Location lastActualLocation) {
        String message = createTooLittleInvocationsMessage(discrepancy, wanted, lastActualLocation);
        
        throw new TooLittleActualInvocations(message);
    }
    
    public void tooLittleActualInvocationsInOrder(Discrepancy discrepancy, PrintableInvocation wanted, Location lastActualLocation) {
        String message = createTooLittleInvocationsMessage(discrepancy, wanted, lastActualLocation);
        
        throw new VerificationInOrderFailure(join(
                "Verification in order failure:" + message
                ));
    }
    
    public void noMoreInteractionsWanted(PrintableInvocation undesired) {
        throw new NoInteractionsWanted(join(
                "No interactions wanted here:",
                new Location(),
                "But found this interaction:",
                undesired.getLocation(),
                ""
                ));
    }
    
    public void cannotMockFinalClass(Class<?> clazz) {
        throw new MockitoException(join(
                "Cannot mock/spy " + clazz.toString(),
                "Mockito cannot mock/spy following:",
                "  - final classes",
                "  - anonymous classes",
                "  - primitive types"
        ));
    }

    public void cannotStubVoidMethodWithAReturnValue() {
        throw new MockitoException(join(
                "Cannot stub a void method with a return value!",
                "Voids are usually stubbed with Throwables:",
                "    doThrow(exception).when(mock).someVoidMethod();"
             ));
    }

    public void onlyVoidMethodsCanBeSetToDoNothing() {
        throw new MockitoException(join(
                "Only void methods can doNothing()!",
                "Example of correct use of doNothing():",
                "    doNothing().",
                "    doThrow(new RuntimeException())",
                "    .when(mock).someVoidMethod();",
                "Above means:",
                "someVoidMethod() does nothing the 1st time but throws an exception the 2nd time is called"
             ));
    }

    public void wrongTypeOfReturnValue(String expectedType, String actualType, String methodName) {
        throw new WrongTypeOfReturnValue(join(
                actualType + " cannot be returned by " + methodName + "()",
                methodName + "() should return " + expectedType
                ));
    }

    public void wantedAtMostX(int maxNumberOfInvocations, int foundSize) {
        throw new MockitoAssertionError(join("Wanted at most " + pluralize(maxNumberOfInvocations) + " but was " + foundSize));
    }

    public void misplacedArgumentMatcher(Location location) {
        throw new InvalidUseOfMatchersException(join(
                "Misplaced argument matcher detected here:",
                location,
                "",
                "You cannot use argument matchers outside of verification or stubbing.",
                "Examples of correct usage of argument matchers:",
                "    when(mock.get(anyInt())).thenReturn(null);",
                "    doThrow(new RuntimeException()).when(mock).someVoidMethod(anyObject());",
                "    verify(mock).someMethod(contains(\"foo\"))",
                "",
                "Also, this error might show up because you use argument matchers with methods that cannot be mocked.",
                "Following methods *cannot* be stubbed/verified: final/private/equals()/hashCode() methods.",                
                ""
                ));
    }

    public void smartNullPointerException(Location location) {
        throw new SmartNullPointerException(join(
                "You have a NullPointerException here:",
                new Location(),
                "Because this method was *not* stubbed correctly:",
                location,
                ""
                ));
    }

    public void noArgumentValueWasCaptured() {
        throw new MockitoException(join(
                "No argument value was captured!",
                "You might have forgotten to use argument.capture() in verify()...",
                "...or you used capture() in stubbing but stubbed method was not called.",
                "Be aware that it is recommended to use capture() only with verify()",
                "",
                "Examples of correct argument capturing:",
                "    Argument<Person> argument = new Argument<Person>();",
                "    verify(mock).doSomething(argument.capture());",
                "    assertEquals(\"John\", argument.getValue().getName());",
                ""
                ));
    }

    public void extraInterfacesDoesNotAcceptNullParameters() {
        throw new MockitoException(join(
                "extraInterfaces() does not accept null parameters."
                ));
    }

    public void extraInterfacesAcceptsOnlyInterfaces(Class<?> wrongType) {
        throw new MockitoException(join(
                "extraInterfaces() accepts only interfaces.",
                "You passed following type: " + wrongType.getSimpleName() + " which is not an interface."
        ));
    }

    public void extraInterfacesCannotContainMockedType(Class<?> wrongType) {
        throw new MockitoException(join(
                "extraInterfaces() does not accept the same type as the mocked type.",
                "You mocked following type: " + wrongType.getSimpleName(), 
                "and you passed the same very interface to the extraInterfaces()"
        ));
    }

    public void extraInterfacesRequiresAtLeastOneInterface() {
        throw new MockitoException(join(
                "extraInterfaces() requires at least one interface."
        ));
    }

    public void mockedTypeIsInconsistentWithSpiedInstanceType(Class<?> mockedType, Object spiedInstance) {
        throw new MockitoException(join(
                "Mocked type must be the same as the type of your spied instance.",
                "Mocked type must be: " + spiedInstance.getClass().getSimpleName() + ", but is: " + mockedType.getSimpleName(),
                "  //correct spying:",
                "  spy = mock( ->ArrayList.class<- , withSettings().spiedInstance( ->new ArrayList()<- );",
                "  //incorrect - types don't match:",
                "  spy = mock( ->List.class<- , withSettings().spiedInstance( ->new ArrayList()<- );"
        ));
    }

    public void cannotCallRealMethodOnInterface() {
        throw new MockitoException(join(
                "Cannot call real method on java interface. Interface does not have any implementation!",
                "Calling real methods is only possible when mocking concrete classes.",
                "  //correct example:",
                "  when(mockOfConcreteClass.doStuff()).thenCallRealMethod();"
        ));
    }
}
