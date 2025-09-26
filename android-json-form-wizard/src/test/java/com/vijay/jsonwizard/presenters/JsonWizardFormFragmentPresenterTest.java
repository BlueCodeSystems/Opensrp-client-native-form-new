package com.vijay.jsonwizard.presenters;

import android.content.Context;
import android.content.res.Resources;
import android.widget.LinearLayout;

import com.vijay.jsonwizard.constants.JsonFormConstants;
import com.vijay.jsonwizard.fragments.JsonWizardFormFragment;
import com.vijay.jsonwizard.interactors.JsonFormInteractor;
import com.vijay.jsonwizard.interfaces.JsonApi;
import com.vijay.jsonwizard.interfaces.OnFieldsInvalid;
import com.vijay.jsonwizard.testutils.TestReflectionHelpers;
import com.vijay.jsonwizard.utils.AppExecutors;
import com.vijay.jsonwizard.utils.ValidationStatus;

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

import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executor;

import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class JsonWizardFormFragmentPresenterTest {

    private JsonWizardFormFragmentPresenter presenter;

    @Mock
    private JsonFormInteractor interactor;

    @Mock
    private JsonWizardFormFragment formFragment;

    @Mock
    private JsonApi jsonApi;

    @Mock
    private JSONObject mJsonObject;

    @Mock
    private JSONObject mStepDetails;

    @Mock
    private Context context;

    @Mock
    private Resources resources;

    @Mock
    private AppExecutors appExecutors;

    private Executor executor;

    private AutoCloseable closeable;

    @Before
    public void setUp() {
        closeable = MockitoAnnotations.openMocks(this);
        doReturn(new JSONObject().toString()).when(formFragment).getCurrentJsonState();
        doReturn(mJsonObject).when(jsonApi).getmJSONObject();
        doReturn(jsonApi).when(formFragment).getJsonApi();
        doReturn(mock(OnFieldsInvalid.class)).when(formFragment).getOnFieldsInvalidCallback();
        doReturn(context).when(formFragment).getContext();
        doReturn(resources).when(context).getResources();
        doReturn("string").when(resources).getString(anyInt());
        presenter = new JsonWizardFormFragmentPresenter(formFragment, interactor);
        TestReflectionHelpers.setInternalState(presenter, "viewRef", new WeakReference<>(formFragment));
        doReturn("step1").when(mStepDetails).optString(anyString());
        TestReflectionHelpers.setInternalState(presenter, "mStepDetails", mStepDetails);
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
    public void testOnNextClickShouldPerformCorrectAction() throws JSONException, InterruptedException {
        Mockito.doAnswer((Answer<Void>) invocation -> {
            Runnable runnable = invocation.getArgument(0);
            runnable.run();
            return null;
        }).when(executor).execute(Mockito.any(Runnable.class));
        when(formFragment.getJsonApi().getAppExecutors()).thenReturn(appExecutors);
        when(appExecutors.diskIO()).thenReturn(executor);
        Thread.sleep(1000);

        try (MockedStatic<JsonWizardFormFragment> wizardFragmentStatic = Mockito.mockStatic(JsonWizardFormFragment.class)) {
            wizardFragmentStatic.when(() -> JsonWizardFormFragment.getFormFragment(anyString())).thenReturn(formFragment);

            presenter = Mockito.spy(presenter);
            mJsonObject.put(JsonFormConstants.VALIDATE_ON_SUBMIT, true);
            presenter.onNextClick(mock(LinearLayout.class));
            verifyMovesToNextStep(1);

            mJsonObject.put(JsonFormConstants.VALIDATE_ON_SUBMIT, false);
            presenter.onNextClick(mock(LinearLayout.class));
            verifyMovesToNextStep(2);

            Map<String, ValidationStatus> invalidFields = new HashMap<>();
            invalidFields.put("step1#key", mock(ValidationStatus.class));
            TestReflectionHelpers.setInternalState(presenter, "invalidFields", invalidFields);
            TestReflectionHelpers.setInternalState(presenter, "mStepName", JsonFormConstants.STEP1);
            presenter.onNextClick(mock(LinearLayout.class));
            verify(formFragment).showSnackBar(Mockito.eq("string"));
        }
    }

    private void verifyMovesToNextStep(int times) {
        verify(presenter, times(times)).executeRefreshLogicForNextStep();
    }
}
