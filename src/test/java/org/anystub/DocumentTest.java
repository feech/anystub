package org.anystub;

import org.junit.Test;

import javax.activation.UnsupportedDataTypeException;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

import static java.util.Arrays.asList;
import static org.anystub.Document.ars;
import static org.junit.Assert.*;

/**
 * test for document class
 * Created by Kirill on 9/2/2016.
 */
public class DocumentTest {

    @Test
    public void equalTest() {
        assertEquals(new Document("qwe", "ewwq", "123"), new Document("qwe", "ewwq", "123"));
        assertEquals(new Document(), new Document());
        assertNotEquals(new Document("qwe", "ewwq", "1234"), new Document("qwe", "ewwq", "123"));
        assertNotEquals(new Document("qwe", "ewwq", "1234"), new Document("qwe", "ewwq"));
        assertNotEquals(new Document("qwe", "ewwq"), new Document("qwe", "ewwq", "123"));
        assertNotEquals(new Document("qwe", "ewwq"), new Document("qwe", null, "123"));
    }

    @Test
    public void equalToTest() {
        assertTrue(new Document("123", "321").keyEqual_to("123", "321"));
        assertTrue(new Document("123", "321", "asd").keyEqual_to("123", "321", "asd"));
        assertFalse(new Document("123", "321").keyEqual_to("123"));
    }

    @Test
    public void match_toTest() {
        assertTrue(new Document("qwe").setValues("321").match_to());
        assertTrue(new Document("qwe").setValues("321").match_to("qwe"));
        assertFalse(new Document("qwe").setValues("321").match_to("q"));
        assertFalse(new Document("qwe").setValues("321").match_to("qwe", null));
    }

    @Test
    public void matchEx_toTest() {
        assertTrue(new Document("qwe").setValues("321").matchEx_to(ars()));
        assertTrue(new Document("qwe").setValues("321").matchEx_to());
        assertTrue(new Document("qwe").setValues("321").matchEx_to("qwe"));
        assertTrue(new Document("qwe").setValues("321").matchEx_to("q.*"));
        assertTrue(new Document("qwe").setValues("321").matchEx_to(".*w.*"));
        assertTrue(new Document("qwe").setValues("321").matchEx_to(".*e$"));
        assertTrue(new Document("qwe").setValues("321").matchEx_to("^q.*"));
        assertTrue(new Document("qwe", "321").setValues("321").matchEx_to("qwe"));
        assertTrue(new Document("qwe", "321").setValues("321").matchEx_to("qwe", null));
        assertTrue(new Document("qwe").setValues("321").matchEx_to(ars("qwe"), ars("3.*")));
        assertTrue(new Document("qwe").setValues("321").matchEx_to(ars("qwe"), ars(".2.")));
        assertFalse(new Document("qwe").setValues("321").matchEx_to(ars("qwe"), ars("^2.*")));
        assertFalse(new Document("qwe").setValues("321").matchEx_to("f"));
        assertFalse(new Document("qwe").setValues("321").matchEx_to(ars(), ars("qwe", null)));
    }

    @Test(expected = AssertionError.class)
    public void assert_toTest() {
        // expected two values in key. first one is equal to "qwe", second one is any value
        new Document("qwe").setValues("321").assert_to("qwe", null);
    }

    @Test(expected = AssertionError.class)
    public void assert_to2Test() {
        // expected two values in key are equal to two tested values
        new Document("qwe", "asd", "123").setValues("321").assert_to("qwe", "dsa");
    }

    @Test(expected = AssertionError.class)
    public void assertEx_toTest() {
        // expected two values in key are matched to two tested values
        new Document("qwe", "asd", "123").setValues("321").assertEx_to("qwe", ".X.");
    }

    @Test(expected = AssertionError.class)
    public void assertEx_to2Test() {
        // expected value contains two values "qwe" and any other. actually it has only one value "321"
        new Document("qwe").setValues("321").assertEx_to(ars(), ars("qwe", null));
    }

    @Test(expected = NoSuchElementException.class)
    public void exceptionTest() {
        Document doc = new Document(new NoSuchElementException("aaaa"), "123");
        List<String> exception = doc.getException();
        assertEquals(2, exception.size());
        doc.getVals();
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void exceptionWMessageTest() {
        new Document(new IndexOutOfBoundsException("aaaa"), "123").getVals();
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void exceptionWoMessageTest() {
        new Document(new IndexOutOfBoundsException(), "123").getVals();
    }

    @Test(expected = RuntimeException.class)
    public void exceptionNotFountTest() {
        Document document = new Document("123");
        document.setException(asList("nonexistentException", "msg"));
        document.getVals();
    }

    @Test
    public void aroTest() throws UnsupportedDataTypeException {
        String[] aro = Document.aro("sdf", 2, "ssdf");
        assertArrayEquals(ars("sdf", null, null, "ssdf"), aro);
        aro = Document.aro("sdf", 0, "ssdf");
        assertArrayEquals(ars("sdf", "ssdf"), aro);
    }

    @Test
    public void nullHolder() {
        Map<String, Object> map = new Document("12").setNull().toMap();

        Document document = new Document(map);
        assertNull(document.getVals());
    }

    @Test
    public void emptyHolder() {
        Map<String, Object> map = new Document("12").setValues(new String[]{null}).toMap();

        Document document = new Document(map);

        assertNull(document.get());
        assertNull(document.getVals().next());

    }
}