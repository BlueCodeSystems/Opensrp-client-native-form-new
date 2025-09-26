package com.vijay.jsonwizard.presenters;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import com.vijay.jsonwizard.constants.JsonFormConstants;
import com.vijay.jsonwizard.fragments.JsonFormFragment;
import com.vijay.jsonwizard.interfaces.JsonApi;
import com.vijay.jsonwizard.testutils.TestReflectionHelpers;
import com.vijay.jsonwizard.utils.AppExecutors;
import com.vijay.jsonwizard.views.JsonFormFragmentView;
import com.vijay.jsonwizard.widgets.CountDownTimerFactory;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.MockedStatic;
import org.mockito.stubbing.Answer;

import java.util.concurrent.Executor;

public class JsonFormFragmentPresenterTest {

    private JsonFormFragmentPresenter presenter;

    @Mock
    private JsonFormFragment jsonFormFragment;

    @Mock
    private AppExecutors appExecutors;

    private Executor executor;

    private AutoCloseable closeable;

    @Before
    public void setUp() throws JSONException {
        closeable = MockitoAnnotations.openMocks(this);
        setUpJsonFormFragment(true);
        presenter = new JsonFormFragmentPresenter(jsonFormFragment);
        executor = mock(Executor.class);
        appExecutors = mock(AppExecutors.class);
    }

    @After
    public void tearDown() throws Exception {
        if (closeable != null) {
            closeable.close();
        }
    }

    @Test
    public void testValidateOnSubmitShouldReturnCorrectValidationStatus() throws JSONException {
        assertTrue(presenter.validateOnSubmit());
        setUpJsonFormFragment(false);
        assertFalse(presenter.validateOnSubmit());
    }

    @Test
    public void testMoveToNextStepShouldMoveToNextStepIfExists() throws Exception {
        JsonApi jsonApi = jsonFormFragment.getJsonApi();
        doReturn("step1").when(jsonApi).nextStep();
        JsonFormFragmentView view = mock(JsonFormFragmentView.class);
        presenter.attachView(view);

        try (MockedStatic<JsonFormFragment> jsonFormFragmentStatic = Mockito.mockStatic(JsonFormFragment.class)) {
            jsonFormFragmentStatic.when(() -> JsonFormFragment.getFormFragment(anyString())).thenReturn(jsonFormFragment);

            boolean movedToNext = TestReflectionHelpers.invokeMethod(presenter, "moveToNextStep");
            assertTrue(movedToNext);
            verify(view).hideKeyBoard();
            verify(view).transactThis(eq(jsonFormFragment));

            doReturn("").when(jsonApi).nextStep();
            movedToNext = TestReflectionHelpers.invokeMethod(presenter, "moveToNextStep");
            assertFalse(movedToNext);
        }
    }

    @Test
    public void testCheckAndStopCountdownAlarmShouldStopAlarm() throws Exception {
        Mockito.doAnswer((Answer<Void>) invocation -> {
            Runnable runnable = invocation.getArgument(0);
            runnable.run();
            return null;
        }).when(executor).execute(Mockito.any(Runnable.class));
        Mockito.when(jsonFormFragment.getJsonApi().getAppExecutors()).thenReturn(appExecutors);
        Mockito.when(appExecutors.diskIO()).thenReturn(executor);
        Thread.sleep(1000);
        bootStrapCurrentJsonState();

        try (MockedStatic<CountDownTimerFactory> timerFactoryMock = Mockito.mockStatic(CountDownTimerFactory.class)) {
            presenter.checkAndStopCountdownAlarm();
            timerFactoryMock.verify(CountDownTimerFactory::stopAlarm);
        }
    }

    private void setUpJsonFormFragment(boolean validationStatus) throws JSONException {
        JsonApi jsonApi = mock(JsonApi.class);
        JSONObject mJsonObject = new JSONObject();
        mJsonObject.put(JsonFormConstants.VALIDATE_ON_SUBMIT, validationStatus);
        doReturn(jsonApi).when(jsonFormFragment).getJsonApi();
        doReturn(mJsonObject).when(jsonApi).getmJSONObject();
    }

    private void bootStrapCurrentJsonState() throws JSONException {
        JSONObject currentJsonState = new JSONObject();
        JSONArray fields = new JSONArray();
        JSONObject step1 = new JSONObject();
        step1.put("fields", fields);
        currentJsonState.put("step1", step1);
        TestReflectionHelpers.setInternalState(presenter, "mStepName", "step1");

        JSONObject timerObj = new JSONObject();
        timerObj.put(JsonFormConstants.COUNTDOWN_TIME_VALUE, 12);
        fields.put(timerObj);

        doReturn(currentJsonState.toString()).when(jsonFormFragment).getCurrentJsonState();
    }
}
