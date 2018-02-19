package org.mockito;

import org.mockito.internal.MockitoCore;
import org.mockito.internal.creation.MockSettingsImpl;
import org.mockito.internal.framework.DefaultMockitoFramework;
import org.mockito.internal.session.DefaultMockitoSessionBuilder;
import org.mockito.internal.verification.VerificationModeFactory;
import org.mockito.session.MockitoSessionBuilder;
import org.mockito.stubbing.Answer;
import org.mockito.stubbing.OngoingStubbing;
import org.mockito.stubbing.Stubber;
import org.mockito.verification.After;
import org.mockito.verification.Timeout;
import org.mockito.verification.VerificationAfterDelay;
import org.mockito.verification.VerificationMode;
import org.mockito.verification.VerificationWithTimeout;

import java.security.AccessController;
import java.security.PrivilegedAction;

/**
 * Wraps Mockito API with calls to AccessController.
 * This is useful if you want to mock in a SecurityManager environment,
 * but contain the permissions to only mocking test libraries.
 * <p>
 * Instead of:
 * <pre>
 * grant {
 *   permission java.lang.RuntimePermission "reflectionFactoryAccess";
 * };
 * </pre>
 * You can just change maven dependencies to use securemock.jar, and then:
 * <pre>
 * grant codeBase "/url/to/securemock.jar" {
 *   permission java.lang.RuntimePermission "reflectionFactoryAccess";
 * };
 * </pre>
 */
@SuppressWarnings("unchecked")
public class Mockito extends ArgumentMatchers {

    static final MockitoCore MOCKITO_CORE = new MockitoCore();

    public static final Answer<Object> RETURNS_DEFAULTS = Answers.RETURNS_DEFAULTS;
    public static final Answer<Object> RETURNS_SMART_NULLS = Answers.RETURNS_SMART_NULLS;
    public static final Answer<Object> RETURNS_MOCKS = Answers.RETURNS_MOCKS;
    public static final Answer<Object> RETURNS_DEEP_STUBS = Answers.RETURNS_DEEP_STUBS;
    public static final Answer<Object> CALLS_REAL_METHODS = Answers.CALLS_REAL_METHODS;
    public static final Answer<Object> RETURNS_SELF = Answers.RETURNS_SELF;

    public static <T> T mock(Class<T> classToMock) {
        T mockedClass = AccessController.doPrivileged((PrivilegedAction<T>) () ->
                mock(classToMock, withSettings()));
        if (mockedClass == null) {
            throw new IllegalStateException("unable to mock " + classToMock);
        }
        return mockedClass;
    }

    public static <T> T mock(final Class<T> classToMock, final String name) {
        return AccessController.doPrivileged((PrivilegedAction<T>) () ->
                mock(classToMock, withSettings()
                        .name(name)
                        .defaultAnswer(RETURNS_DEFAULTS)));
    }
    
    public static MockingDetails mockingDetails(final Object toInspect) {
        return AccessController.doPrivileged((PrivilegedAction<MockingDetails>) () ->
                MOCKITO_CORE.mockingDetails(toInspect));
    }

    public static <T> T mock(final Class<T> classToMock, final Answer defaultAnswer) {
        return AccessController.doPrivileged((PrivilegedAction<T>) () ->
                mock(classToMock, withSettings().defaultAnswer(defaultAnswer)));
    }
    
    public static <T> T mock(final Class<T> classToMock, final MockSettings mockSettings) {
        return AccessController.doPrivileged((PrivilegedAction<T>) () ->
                MOCKITO_CORE.mock(classToMock, mockSettings));
    }
    
    public static <T> T spy(final T object) {
        return AccessController.doPrivileged((PrivilegedAction<T>) () ->
                MOCKITO_CORE.mock((Class<T>) object.getClass(), withSettings()
                        .spiedInstance(object)
                        .defaultAnswer(CALLS_REAL_METHODS)));
    }

    public static <T> T spy(Class<T> classToSpy) {
        return AccessController.doPrivileged((PrivilegedAction<T>) () ->
                MOCKITO_CORE.mock(classToSpy, withSettings()
                .useConstructor()
                .defaultAnswer(CALLS_REAL_METHODS)));
    }

    public static <T> OngoingStubbing<T> when(final T methodCall) {
        return AccessController.doPrivileged((PrivilegedAction<OngoingStubbing<T>>) () ->
                MOCKITO_CORE.when(methodCall));
    }
    
    public static <T> T verify(final T mock) {
        return AccessController.doPrivileged((PrivilegedAction<T>) () ->
                MOCKITO_CORE.verify(mock, times(1)));
    }
    
    public static <T> T verify(final T mock, final VerificationMode mode) {
        return AccessController.doPrivileged((PrivilegedAction<T>) () ->
                MOCKITO_CORE.verify(mock, mode));
    }
    
    public static <T> void reset(final T ... mocks) {
        AccessController.doPrivileged((PrivilegedAction<Void>) () -> {
            MOCKITO_CORE.reset(mocks);
            return null;
        });
    }

    public static <T> void clearInvocations(T ... mocks) {
        AccessController.doPrivileged((PrivilegedAction<Void>) () -> {
            MOCKITO_CORE.clearInvocations(mocks);
            return null;
        });
    }

    public static void verifyNoMoreInteractions(final Object... mocks) {
        AccessController.doPrivileged((PrivilegedAction<Void>) () -> {
            MOCKITO_CORE.verifyNoMoreInteractions(mocks);
            return null;
        });
    }
    
    public static void verifyZeroInteractions(final Object... mocks) {
        AccessController.doPrivileged((PrivilegedAction<Void>) () -> {
            MOCKITO_CORE.verifyNoMoreInteractions(mocks);
            return null;
        });
    }
    
    public static Stubber doThrow(final Throwable... toBeThrown) {
        return AccessController.doPrivileged((PrivilegedAction<Stubber>) () ->
                MOCKITO_CORE.stubber().doThrow(toBeThrown));
    }
    
    public static Stubber doThrow(final Class<? extends Throwable> toBeThrown) {
        return AccessController.doPrivileged((PrivilegedAction<Stubber>) () ->
                MOCKITO_CORE.stubber().doThrow(toBeThrown));
    }

    public static Stubber doThrow(Class<? extends Throwable> toBeThrown, Class<? extends Throwable>... toBeThrownNext) {
        return AccessController.doPrivileged((PrivilegedAction<Stubber>) () ->
                MOCKITO_CORE.stubber().doThrow(toBeThrown, toBeThrownNext));
    }

    public static Stubber doCallRealMethod() {
        return AccessController.doPrivileged((PrivilegedAction<Stubber>) () ->
                MOCKITO_CORE.stubber().doCallRealMethod());
    }
    
    public static Stubber doAnswer(final Answer answer) {
        return AccessController.doPrivileged((PrivilegedAction<Stubber>) () ->
                MOCKITO_CORE.stubber().doAnswer(answer));
    }  
    
    public static Stubber doNothing() {
        return AccessController.doPrivileged((PrivilegedAction<Stubber>) () ->
                MOCKITO_CORE.stubber().doNothing());
    }
    
    public static Stubber doReturn(final Object toBeReturned) {
        return AccessController.doPrivileged((PrivilegedAction<Stubber>) () ->
                MOCKITO_CORE.stubber().doReturn(toBeReturned));
    }

    public static Stubber doReturn(Object toBeReturned, Object... toBeReturnedNext) {
        return  AccessController.doPrivileged((PrivilegedAction<Stubber>) () ->
                MOCKITO_CORE.stubber().doReturn(toBeReturned, toBeReturnedNext));
    }
    
    public static InOrder inOrder(final Object... mocks) {
        return AccessController.doPrivileged((PrivilegedAction<InOrder>) () ->
                MOCKITO_CORE.inOrder(mocks));
    }
    
    public static Object[] ignoreStubs(final Object... mocks) {
        return AccessController.doPrivileged((PrivilegedAction<Object[]>) () ->
                MOCKITO_CORE.ignoreStubs(mocks));
    }
    
    public static VerificationMode times(final int wantedNumberOfInvocations) {
        return AccessController.doPrivileged((PrivilegedAction<VerificationMode>) () ->
                VerificationModeFactory.times(wantedNumberOfInvocations));
    }
    
    public static VerificationMode never() {
        return AccessController.doPrivileged((PrivilegedAction<VerificationMode>) () ->
                times(0));
    }
    
    public static VerificationMode atLeastOnce() {
        return AccessController.doPrivileged((PrivilegedAction<VerificationMode>)
                VerificationModeFactory.atLeastOnce());
    }
    
    public static VerificationMode atLeast(final int minNumberOfInvocations) {
        return AccessController.doPrivileged((PrivilegedAction<VerificationMode>) () ->
                VerificationModeFactory.atLeast(minNumberOfInvocations));
    }
    
    public static VerificationMode atMost(final int maxNumberOfInvocations) {
        return AccessController.doPrivileged((PrivilegedAction<VerificationMode>) () ->
                VerificationModeFactory.atMost(maxNumberOfInvocations));
    }
    
    public static VerificationMode calls(final int wantedNumberOfInvocations) {
        return AccessController.doPrivileged((PrivilegedAction<VerificationMode>) () ->
                VerificationModeFactory.calls(wantedNumberOfInvocations));
    }
    
    public static VerificationMode only() {
        return AccessController.doPrivileged((PrivilegedAction<VerificationMode>)
                VerificationModeFactory::only);
    }
    
    public static VerificationWithTimeout timeout(final int millis) {
        return AccessController.doPrivileged((PrivilegedAction<VerificationWithTimeout>) () ->
                new Timeout(millis, VerificationModeFactory.times(1)));
    }

    public static VerificationAfterDelay after(long millis) {
        return AccessController.doPrivileged((PrivilegedAction<VerificationAfterDelay>) () ->
                new After(millis, VerificationModeFactory.times(1)));
    }
    
    public static void validateMockitoUsage() {
        AccessController.doPrivileged((PrivilegedAction<Void>) () -> {
            MOCKITO_CORE.validateMockitoUsage();
            return null;
        });
    }
    
    public static MockSettings withSettings() {
        return AccessController.doPrivileged((PrivilegedAction<MockSettings>) () ->
                new MockSettingsImpl().defaultAnswer(RETURNS_DEFAULTS));
    }

    public static VerificationMode description(String description) {
        return AccessController.doPrivileged((PrivilegedAction<VerificationMode>) () ->
                times(1).description(description));
    }

    public static MockitoFramework framework() {
        return AccessController.doPrivileged((PrivilegedAction<MockitoFramework>)
                DefaultMockitoFramework::new);
    }

    public static MockitoSessionBuilder mockitoSession() {
        return AccessController.doPrivileged((PrivilegedAction<MockitoSessionBuilder>)
                DefaultMockitoSessionBuilder::new);
    }
}
