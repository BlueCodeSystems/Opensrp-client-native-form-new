package com.vijay.jsonwizard.utils.zing;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Typeface;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.View;
import android.widget.ImageView;

import androidx.appcompat.widget.AppCompatTextView;
import androidx.test.core.app.ApplicationProvider;

import com.vijay.jsonwizard.BaseTest;
import com.vijay.jsonwizard.R;
import com.vijay.jsonwizard.constants.JsonFormConstants;
import com.vijay.jsonwizard.interfaces.CommonListener;
import com.vijay.jsonwizard.testutils.TestReflectionHelpers;
import com.vijay.jsonwizard.utils.FormUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;

import java.util.HashMap;
import java.util.Map;

import static org.mockito.ArgumentMatchers.eq;

public class FormUtilsTest extends BaseTest {

    private Context context;
    private Resources resources;
    private DisplayMetrics displayMetrics;
    private FormUtils formUtils;

    private final String optionKey = "";
    private final String keyValue = "Tests";
    private final String itemText = "Tim Apple";
    private final String itemKey = "my_test";

    @Before
    public void setUp() {
        context = ApplicationProvider.getApplicationContext();
        resources = context.getResources();
        displayMetrics = resources.getDisplayMetrics();
        formUtils = new FormUtils();
    }

    @Test
    public void testSpToPx() {
        float spValue = 30f;
        int expected = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, spValue, displayMetrics);
        int px = FormUtils.spToPx(context, spValue);
        Assert.assertEquals(expected, px);
    }

    @Test
    public void testDpToPx() {
        float dpValue = 30f;
        int expected = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dpValue, displayMetrics);
        int px = FormUtils.dpToPixels(context, dpValue);
        Assert.assertEquals(expected, px);
    }

    @Test
    public void testGetValueFromSpOrDpOrPxWithAnSpInput() {
        String spString = "30sp";
        int expected = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 30, displayMetrics);
        int px = FormUtils.getValueFromSpOrDpOrPx(spString, context);
        Assert.assertEquals(expected, px);
    }

    @Test
    public void testGetValueFromSpOrDpOrPxWithADpInput() {
        String dpString = "30dp";
        int expected = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 30, displayMetrics);
        int px = FormUtils.getValueFromSpOrDpOrPx(dpString, context);
        Assert.assertEquals(expected, px);
    }

    @Test
    public void testGetValueFromSpOrDpOrPxWithAPxInput() {
        String pxString = "30px";
        int px = FormUtils.getValueFromSpOrDpOrPx(pxString, context);
        Assert.assertEquals(30, px);
    }

    @Test
    public void testGetValueFromSpOrDpOrPxWithAnyString() {
        String value = "String";
        int expected = (int) resources.getDimension(R.dimen.default_label_text_size);
        int px = FormUtils.getValueFromSpOrDpOrPx(value, context);
        Assert.assertEquals(expected, px);
    }

    @Test
    public void testGetValueFromSpOrDpOrPxWithEmptyString() {
        int px = FormUtils.getValueFromSpOrDpOrPx("", context);
        Assert.assertEquals(0, px);
    }

    @Test
    public void testGetValueFromSpOrDpOrPxWithNull() {
        int px = FormUtils.getValueFromSpOrDpOrPx(null, context);
        Assert.assertEquals(0, px);
    }

    @Test
    public void showInfoIconLabelHasImage() throws JSONException {
        HashMap<String, String> imageAttributes = new HashMap<>(2);
        imageAttributes.put(JsonFormConstants.LABEL_INFO_HAS_IMAGE, "true");
        imageAttributes.put(JsonFormConstants.LABEL_INFO_IMAGE_SRC, "random_image_src");
        ImageView testImageView = Mockito.mock(ImageView.class);
        CommonListener listener = Mockito.mock(CommonListener.class);
        JSONObject jsonObject = new JSONObject();
        jsonObject.put(JsonFormConstants.KEY, "key");
        jsonObject.put(JsonFormConstants.TYPE, "type");
        String stepName = "step_name_test";
        JSONArray canvasIds = new JSONArray();

        formUtils.showInfoIcon(stepName, jsonObject, listener, imageAttributes, testImageView, canvasIds);

        Mockito.verify(testImageView).setTag(eq(R.id.label_dialog_image_src), eq(imageAttributes.get(JsonFormConstants.LABEL_INFO_IMAGE_SRC)));
        Mockito.verify(testImageView).setTag(eq(R.id.key), eq(jsonObject.getString(JsonFormConstants.KEY)));
        Mockito.verify(testImageView).setTag(eq(R.id.type), eq(jsonObject.getString(JsonFormConstants.TYPE)));
        Mockito.verify(testImageView).setTag(eq(R.id.address), eq(stepName + ":" + jsonObject.getString(JsonFormConstants.KEY)));
        Mockito.verify(testImageView).setTag(eq(R.id.canvas_ids), eq(canvasIds.toString()));
        Mockito.verify(testImageView).setOnClickListener(eq(listener));
        Mockito.verify(testImageView).setVisibility(eq(View.VISIBLE));
    }

    @Test
    public void showInfoIconLabelHasText() throws JSONException {
        HashMap<String, String> imageAttributes = new HashMap<>(2);
        imageAttributes.put(JsonFormConstants.LABEL_INFO_TEXT, "test_text");
        imageAttributes.put(JsonFormConstants.LABEL_INFO_TITLE, "test_title");
        ImageView testImageView = Mockito.mock(ImageView.class);
        CommonListener listener = Mockito.mock(CommonListener.class);
        JSONObject jsonObject = new JSONObject();
        jsonObject.put(JsonFormConstants.KEY, "key");
        jsonObject.put(JsonFormConstants.TYPE, "type");
        String stepName = "step_name_test";
        JSONArray canvasIds = new JSONArray();

        formUtils.showInfoIcon(stepName, jsonObject, listener, imageAttributes, testImageView, canvasIds);

        Mockito.verify(testImageView).setTag(eq(R.id.label_dialog_info), eq(imageAttributes.get(JsonFormConstants.LABEL_INFO_TEXT)));
        Mockito.verify(testImageView).setTag(eq(R.id.label_dialog_title), eq(imageAttributes.get(JsonFormConstants.LABEL_INFO_TITLE)));
        Mockito.verify(testImageView).setTag(eq(R.id.key), eq(jsonObject.getString(JsonFormConstants.KEY)));
        Mockito.verify(testImageView).setTag(eq(R.id.type), eq(jsonObject.getString(JsonFormConstants.TYPE)));
        Mockito.verify(testImageView).setTag(eq(R.id.address), eq(stepName + ":" + jsonObject.getString(JsonFormConstants.KEY)));
        Mockito.verify(testImageView).setTag(eq(R.id.canvas_ids), eq(canvasIds.toString()));
        Mockito.verify(testImageView).setOnClickListener(eq(listener));
        Mockito.verify(testImageView).setVisibility(eq(View.VISIBLE));
    }

    @Test
    public void showInfoIconLabelIsDynamic() throws JSONException {
        HashMap<String, String> imageAttributes = new HashMap<>(2);
        imageAttributes.put(JsonFormConstants.LABEL_IS_DYNAMIC, "true");
        imageAttributes.put(JsonFormConstants.LABEL_INFO_TITLE, "test_title");
        ImageView testImageView = Mockito.mock(ImageView.class);
        CommonListener listener = Mockito.mock(CommonListener.class);
        JSONObject jsonObject = new JSONObject();
        jsonObject.put(JsonFormConstants.KEY, "key");
        jsonObject.put(JsonFormConstants.TYPE, "type");
        jsonObject.put(JsonFormConstants.DYNAMIC_LABEL_INFO, new JSONArray());
        String stepName = "step_name_test";
        JSONArray canvasIds = new JSONArray();

        formUtils.showInfoIcon(stepName, jsonObject, listener, imageAttributes, testImageView, canvasIds);

        Mockito.verify(testImageView).setTag(eq(R.id.dynamic_label_info), eq(jsonObject.getJSONArray(JsonFormConstants.DYNAMIC_LABEL_INFO)));
        Mockito.verify(testImageView).setTag(eq(R.id.label_dialog_title), eq(imageAttributes.get(JsonFormConstants.LABEL_INFO_TITLE)));
        Mockito.verify(testImageView).setTag(eq(R.id.key), eq(jsonObject.getString(JsonFormConstants.KEY)));
        Mockito.verify(testImageView).setTag(eq(R.id.type), eq(jsonObject.getString(JsonFormConstants.TYPE)));
        Mockito.verify(testImageView).setTag(eq(R.id.address), eq(stepName + ":" + jsonObject.getString(JsonFormConstants.KEY)));
        Mockito.verify(testImageView).setTag(eq(R.id.canvas_ids), eq(canvasIds.toString()));
        Mockito.verify(testImageView).setOnClickListener(eq(listener));
        Mockito.verify(testImageView).setVisibility(eq(View.VISIBLE));
    }

    @Test
    public void testSetTextStyleBold() {
        AppCompatTextView mockTextView = Mockito.mock(AppCompatTextView.class);
        FormUtils.setTextStyle(JsonFormConstants.BOLD, mockTextView);
        Mockito.verify(mockTextView).setTypeface(ArgumentMatchers.<Typeface>isNull(), eq(Typeface.BOLD));
    }

    @Test
    public void testSetTextStyleItalic() {
        AppCompatTextView mockTextView = Mockito.mock(AppCompatTextView.class);
        FormUtils.setTextStyle(JsonFormConstants.ITALIC, mockTextView);
        Mockito.verify(mockTextView).setTypeface(ArgumentMatchers.<Typeface>isNull(), eq(Typeface.ITALIC));
    }

    @Test
    public void testSetTextStyleBoldItalic() {
        AppCompatTextView mockTextView = Mockito.mock(AppCompatTextView.class);
        FormUtils.setTextStyle(JsonFormConstants.BOLD_ITALIC, mockTextView);
        Mockito.verify(mockTextView).setTypeface(ArgumentMatchers.<Typeface>isNull(), eq(Typeface.BOLD_ITALIC));
    }

    @Test
    public void testSetTextStyleNormal() {
        AppCompatTextView mockTextView = Mockito.mock(AppCompatTextView.class);
        FormUtils.setTextStyle(JsonFormConstants.NORMAL, mockTextView);
        Mockito.verify(mockTextView).setTypeface(ArgumentMatchers.<Typeface>isNull(), eq(Typeface.NORMAL));
    }

    @Test
    public void testSetTextStyleUnknownFallsBackToNormal() {
        AppCompatTextView mockTextView = Mockito.mock(AppCompatTextView.class);
        FormUtils.setTextStyle("normal", mockTextView);
        Mockito.verify(mockTextView).setTypeface(ArgumentMatchers.<Typeface>isNull(), eq(Typeface.NORMAL));
    }

    @Test
    public void testAddAssignedValueForCheckBox() {
        Map<String, String> value = formUtils.addAssignedValue(itemKey, optionKey, keyValue, "check_box", itemText);
        Assert.assertNotNull(value);
        Map<String, String> expectedValue = new HashMap<>();
        expectedValue.put(itemKey, optionKey + ":" + itemText + ":" + keyValue + ";" + "check_box");
        Assert.assertEquals(expectedValue, value);
    }

    @Test
    public void testAddAssignedValueForNativeRadio() {
        Map<String, String> value = formUtils.addAssignedValue(itemKey, optionKey, keyValue, "native_radio", itemText);
        Assert.assertNotNull(value);
        Map<String, String> expectedValue = new HashMap<>();
        expectedValue.put(itemKey, keyValue + ":" + itemText + ";" + "native_radio");
        Assert.assertEquals(expectedValue, value);
    }

    @Test
    public void testAddAssignedValueForOtherWidget() {
        Map<String, String> value = formUtils.addAssignedValue(itemKey, optionKey, keyValue, "date_picker", itemText);
        Assert.assertNotNull(value);
        Map<String, String> expectedValue = new HashMap<>();
        expectedValue.put(itemKey, keyValue + ";" + "date_picker");
        Assert.assertEquals(expectedValue, value);
    }

    @Test
    public void testExtractOptionOpenMRSAttributes() throws Exception {
        String optionItem = "{\n" +
                "          \"key\": \"1\",\n" +
                "          \"text\": \"Not done\",\n" +
                "          \"openmrs_entity_parent\": \"\",\n" +
                "          \"openmrs_entity\": \"concept\",\n" +
                "          \"openmrs_entity_id\": \"165269AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\"\n" +
                "        }";
        JSONObject optionItemJson = new JSONObject(optionItem);
        JSONArray valuesArray = new JSONArray();
        TestReflectionHelpers.invokeMethod(formUtils, "extractOptionOpenMRSAttributes", valuesArray, optionItemJson, "respiratory_exam_radio_button");
        Assert.assertEquals(1, valuesArray.length());
    }
}
