package com.vijay.jsonwizard.customviews;

import com.vijay.jsonwizard.BaseTest;
import com.vijay.jsonwizard.testutils.TestReflectionHelpers;

import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class GenericPopupDialogTest extends BaseTest {

    private GenericPopupDialog genericPopupDialog;

    @Before
    public void setUp() {
        genericPopupDialog = new GenericPopupDialog();
    }

    @Test
    public void testGetRadioButtonOptionsOpenMRSAttributes() throws Exception {
        String radioButtonItem = "{\"key\":\"respiratory_exam\",\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"concept\",\"openmrs_entity_id\":\"165367AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\",\"type\":\"native_radio\",\"label\":\"Respiratory exam\",\"label_text_style\":\"bold\",\"text_color\":\"#000000\",\"value\":\"3\",\"options\":[{\"key\":\"1\",\"text\":\"Not done\",\"openmrs_entity_parent\":\"165367AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\",\"openmrs_entity\":\"concept\",\"openmrs_entity_id\":\"1118AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\"},{\"key\":\"2\",\"text\":\"Normal\",\"openmrs_entity_parent\":\"165367AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\",\"openmrs_entity\":\"concept\",\"openmrs_entity_id\":\"1115AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\"},{\"key\":\"3\",\"text\":\"Abnormal\",\"openmrs_entity_parent\":\"165367AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\",\"openmrs_entity\":\"concept\",\"openmrs_entity_id\":\"1116AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\"}]}";
        JSONObject itemJson = new JSONObject(radioButtonItem);
        JSONArray fields = new JSONArray().put(itemJson);
        TestReflectionHelpers.setInternalState(genericPopupDialog, "subFormsFields", fields);

        JSONArray values = TestReflectionHelpers.invokeMethod(genericPopupDialog, "createValues");
        Assert.assertEquals(1, values.length());
    }

    @Test
    public void testGetCheckBoxOptionsOpenMRSAttributes() throws Exception {
        String checkboxItem = "{\n" +
                "      \"key\": \"respiratory_exam_abnormal\",\n" +
                "      \"openmrs_entity_parent\": \"\",\n" +
                "      \"openmrs_entity\": \"\",\n" +
                "      \"openmrs_entity_id\": \"\",\n" +
                "      \"type\": \"check_box\",\n" +
                "      \"label\": \"Abnormal\",\n" +
                "      \"options\": [\n" +
                "        {\n" +
                "          \"key\": \"rapid_breathing\",\n" +
                "          \"text\": \"Rapid breathing\",\n" +
                "          \"openmrs_entity_parent\": \"\",\n" +
                "          \"openmrs_entity\": \"TACHYPNEA\",\n" +
                "          \"openmrs_entity_id\": \"125061\",\n" +
                "          \"value\": \"true\",\n" +
                "        },\n" +
                "        {\n" +
                "          \"key\": \"slow_breathing\",\n" +
                "          \"text\": \"Slow breathing\",\n" +
                "          \"openmrs_entity_parent\": \"\",\n" +
                "          \"openmrs_entity\": \"\",\n" +
                "          \"openmrs_entity_id\": \"\",\n" +
                "          \"value\": \"false\"\n" +
                "        },\n" +
                "        {\n" +
                "          \"key\": \"other\",\n" +
                "          \"text\": \"Other (specify)\",\n" +
                "          \"openmrs_entity_parent\": \"\",\n" +
                "          \"openmrs_entity\": \"\",\n" +
                "          \"openmrs_entity_id\": \"\",\n" +
                "          \"value\": \"true\"\n" +
                "        }\n" +
                "      ]\n" +
                "    }";

        JSONObject checkboxItemJson = new JSONObject(checkboxItem);
        JSONArray fields = new JSONArray().put(checkboxItemJson);
        TestReflectionHelpers.setInternalState(genericPopupDialog, "subFormsFields", fields);

        JSONArray values = TestReflectionHelpers.invokeMethod(genericPopupDialog, "createValues");
        Assert.assertEquals(1, values.length());
    }
}
