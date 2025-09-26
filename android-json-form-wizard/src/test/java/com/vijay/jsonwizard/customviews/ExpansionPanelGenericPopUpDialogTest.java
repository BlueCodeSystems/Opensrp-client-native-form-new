package com.vijay.jsonwizard.customviews;

import com.vijay.jsonwizard.BaseTest;
import com.vijay.jsonwizard.constants.JsonFormConstants;
import com.vijay.jsonwizard.testutils.TestReflectionHelpers;

import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class ExpansionPanelGenericPopUpDialogTest extends BaseTest {

    private ExpansionPanelGenericPopupDialog dialog;

    @Before
    public void setUp() {
        dialog = new ExpansionPanelGenericPopupDialog();
    }

    @Test
    public void testAddRequiredFields() throws Exception {
        JSONObject accordion = new JSONObject();
        accordion.put(JsonFormConstants.KEY, "accordion_key");

        JSONObject requiredField = new JSONObject();
        requiredField.put(JsonFormConstants.KEY, "field_key");
        requiredField.put(JsonFormConstants.TYPE, JsonFormConstants.EDIT_TEXT);
        JSONObject requiredMetadata = new JSONObject();
        requiredMetadata.put(JsonFormConstants.VALUE, "true");
        requiredField.put(JsonFormConstants.V_REQUIRED, requiredMetadata);

        JSONArray formFields = new JSONArray().put(requiredField);
        dialog.setSubFormsFields(formFields);

        TestReflectionHelpers.invokeMethod(dialog, "addRequiredFields", accordion);

        JSONArray required = accordion.optJSONArray(JsonFormConstants.REQUIRED_FIELDS);
        Assert.assertNotNull(required);
        Assert.assertEquals(1, required.length());
        Assert.assertEquals("field_key", required.getString(0));
    }
}
