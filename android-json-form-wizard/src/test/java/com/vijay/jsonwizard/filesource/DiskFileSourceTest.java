package com.vijay.jsonwizard.filesource;

import android.content.Context;
import android.os.Environment;

import org.jeasy.rules.api.Rules;
import org.json.JSONObject;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.MockedStatic;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;

public class DiskFileSourceTest {

    @Rule
    public TemporaryFolder storageDirectory = new TemporaryFolder();

    @Mock
    private Context context;

    private DiskFileSource diskFileSource;

    private MockedStatic<Environment> environmentMock;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.openMocks(this);
        diskFileSource = Mockito.spy(DiskFileSource.INSTANCE);
        File downloadsDir = storageDirectory.newFolder("downloads");
        environmentMock = Mockito.mockStatic(Environment.class);
        environmentMock.when(() -> Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS))
                .thenReturn(downloadsDir);
    }

    @After
    public void tearDown() {
        if (environmentMock != null) {
            environmentMock.close();
        }
    }

    @Test
    public void testGetRulesFromFileConvertsFileToRules() throws Exception {
        String specifiedString = "1";
        String relevance = "---\n" +
                "name: step1_last_name\n" +
                "description: last_name\n" +
                "priority: 1\n" +
                "condition: \"step1_first_Name.equalsIgnoreCase('Doe')\"\n" +
                "actions:\n" +
                "    - \" calculation = " + specifiedString + "\"";

        InputStream inputStream = new ByteArrayInputStream(relevance.getBytes());
        Mockito.doReturn(inputStream).when(diskFileSource).getInputStream(Mockito.any(File.class));

        Rules rules = diskFileSource.getRulesFromFile(context, "test_rule");
        Assert.assertFalse(rules.isEmpty());

        org.jeasy.rules.api.Rule rule = rules.iterator().next();
        Assert.assertEquals("step1_last_name", rule.getName());
        Assert.assertEquals("last_name", rule.getDescription());
        Assert.assertEquals(1, rule.getPriority());
    }


    @Test
    public void testGetFormFromFileReadsFile() throws Exception {
        String expected = "{\n" +
                "        \"key\": \"user_image\",\n" +
                "        \"openmrs_entity_parent\": \"\",\n" +
                "        \"openmrs_entity\": \"\",\n" +
                "        \"openmrs_entity_id\": \"\",\n" +
                "        \"type\": \"choose_image\",\n" +
                "        \"uploadButtonText\": \"Take a photo of the child\"\n" +
                "      }";
        InputStream inputStream = new ByteArrayInputStream(expected.getBytes());
        Mockito.doReturn(inputStream).when(diskFileSource).getInputStream(Mockito.any(File.class));

        JSONObject jsonObject = diskFileSource.getFormFromFile(context, "test");
        Assert.assertNotNull(jsonObject);
        Assert.assertEquals("user_image", jsonObject.getString("key"));
        Assert.assertEquals("choose_image", jsonObject.getString("type"));
        Assert.assertEquals("Take a photo of the child", jsonObject.getString("uploadButtonText"));
    }
}
