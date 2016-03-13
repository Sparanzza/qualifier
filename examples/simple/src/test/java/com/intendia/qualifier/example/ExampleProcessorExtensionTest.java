package com.intendia.qualifier.example;

import static com.intendia.qualifier.ComparableQualifier.COMPARABLE_COMPARATOR;
import static com.intendia.qualifier.example.ExampleModelExampleInner__.ExampleInnerMetadata;
import static com.intendia.qualifier.example.ExampleModel__.ExampleModelMetadata;
import static com.intendia.qualifier.example.ExampleModel__.stringListValue;
import static java.util.Objects.requireNonNull;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import com.intendia.qualifier.ComparableQualifier;
import com.intendia.qualifier.Extension;
import com.intendia.qualifier.PropertyQualifier;
import com.intendia.qualifier.Qualifier;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.TimeUnit;
import org.junit.Assert;
import org.junit.Test;

public class ExampleProcessorExtensionTest {
    @Test public void test_processor_extensions_are_executed() {
        Object expectedType = ExampleModel.ExampleInnerInterface.class;
        assertNotNull(ExampleModel__.stringValue.data("simple.loaded"));
        assertEquals(1, ExampleModel__.stringValue.data("simple.integer"));
        assertEquals("s", ExampleModel__.stringValue.data("simple.string"));
        assertEquals(expectedType, ExampleModel__.stringValue.data("simple.type"));
        assertEquals("literal", ExampleModel__.stringValue.data("simple.literal"));
    }

    @Test public void assert_that_typed_extension_works() {
        ExampleManualExtension<String> q = ExampleManualExtension.of(ExampleModel__.stringValue);
        Class<?> expectedType = ExampleModel.ExampleInnerInterface.class;
        assertNotNull(ExampleModel__.stringValue.data("simple.loaded"));
        assertEquals(Integer.valueOf(1), q.getExampleInteger());
        assertEquals("s", q.getExampleString());
        assertEquals(expectedType, q.getExampleType());
    }

    @Test public void assert_that_qualifier_extension_works() {
        Qualifier<ExampleModel> q = ExampleModelMetadata;
        assertEquals("string value", q.data(Extension.<String>key("extension.string")));
        assertEquals(true, q.data(Extension.<Boolean>key("extension.boolean")));
        assertEquals(Integer.valueOf(1), q.data(Extension.<Integer>key("extension.int")));
        assertEquals(TimeUnit.SECONDS, q.data(Extension.<TimeUnit>key("extension.enum")));
        Assert.assertEquals(Color.valueOf("red"), q.data(Extension.<Color>key("extension.valueOf")));
        assertEquals(String.class, q.data(Extension.<Class<?>>key("extension.class")));
    }

    @Test public void assert_that_auto_qualifier_works() {
        Object expectedType = ExampleModel.ExampleInnerInterface.class;
        Object expectedLink = Color__.self;
        assertEquals(TimeUnit.SECONDS, ExampleModel__.stringValue.data("exampleAuto.enumeration"));
        assertEquals(1, ExampleModel__.stringValue.data("exampleAuto.integer"));
        assertEquals("s", ExampleModel__.stringValue.data("exampleAuto.string"));
        assertEquals(expectedType, ExampleModel__.stringValue.data("exampleAuto.type"));
        assertEquals(expectedLink, ExampleModel__.stringValue.data("exampleAuto.link"));
    }

    @Test public void test_works() throws Exception {
        assertNotNull(ExampleModelMetadata);
        assertNotNull(ExampleInnerMetadata);
        assertEquals(List.class, stringListValue.getType());
    }

    @Test public void assert_comparator_can_be_override() {
        Comparator<ExampleModel> stringComparator = ExampleModel__.stringValue.getPropertyComparator();
        Qualifier<ExampleModel> override = ExampleModelMetadata.override(COMPARABLE_COMPARATOR, stringComparator);
        // this is easy, just confirm comparable returns the override qualifier
        assertEquals(stringComparator, ComparableQualifier.of(override).getTypeComparator());
        // this is the important, confirm that identity decorator maintains the override comparator
        assertEquals(stringComparator, PropertyQualifier.asProperty(override).getPropertyComparator());
    }

    @Test public void assert_paths() {
        Qualifier<ExampleModel> q = ExampleModelMetadata;

        PropertyQualifier<ExampleModel, ExampleModel> qSelf = PropertyQualifier.asProperty(q);
        assertEquals("", qSelf.getPath());
        assertEquals("self", qSelf.getName());
        assertEquals("override", qSelf.getPath("override"));

        PropertyQualifier<ExampleModel, String> qString = ExampleModel__.stringValue;
        assertEquals("stringValue", qString.getPath());
        assertEquals("stringValue", qString.getName());
        assertEquals("override", qString.getPath("override"));

        PropertyQualifier<ExampleModel, String> qColor = ExampleModel__.colorValue.compose(Color__.name);
        assertEquals("colorValue.name", qColor.getPath());
        assertEquals("name", qColor.getName());
        assertEquals("colorValue.override", qColor.getPath("override"));
    }

    @Test public void assert_property_resolution_works() {
        Qualifier<ExampleModel> q = ExampleModelMetadata;
        assertEquals("colorValue",requireNonNull(q.getProperty("colorValue")).getPath());
        assertEquals("colorValue.name",requireNonNull(q.getProperty("colorValue.name")).getPath());
    }
}
