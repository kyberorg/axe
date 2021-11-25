package io.kyberorg.yalsee.test.unit;

import io.kyberorg.yalsee.models.Link;
import io.kyberorg.yalsee.result.OperationResult;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junitpioneer.jupiter.Issue;

/**
 * Test Suite for {@link OperationResult}.
 *
 * @since 3.7
 */
@Issue("https://github.com/kyberorg/yalsee/issues/613")
public class OperationResultTest extends UnitTest {

    /**
     * Tests that {@link OperationResult#addPayload(Object)} adds Value.
     */
    @Test
    public void addPayloadAddsValue() {
        Link linkObject = new Link();
        OperationResult operationResult = OperationResult.success().addPayload(linkObject);
        Link storedObject = operationResult.getPayload(Link.class);
        Assertions.assertEquals(linkObject, storedObject);
    }

    /**
     * Tests that {@link OperationResult#addPayload(Object)} stores value under default Key.
     */
    @Test
    public void addPayloadStoresUnderDefaultKey() {
        String customKey = "MY_KEY";
        Link linkObject = new Link();
        OperationResult operationResult = OperationResult.success().addPayload(linkObject);
        Link storedObject = operationResult.getPayload(customKey, Link.class);
        Assertions.assertNull(storedObject);
    }

    /**
     * Tests that {@link OperationResult#addPayload(Object)} can store {@code null}
     * and {@link OperationResult#getPayload(Class)} can get it.
     */
    @Test
    public void addPayloadStoresNullAndGetPayloadCanGetIt() {
        OperationResult operationResult = OperationResult.success().addPayload(null);
        Link storedObject = operationResult.getPayload(Link.class);
        Assertions.assertNull(storedObject);
    }

    /**
     * Tests that {@link OperationResult#getPayload(Class)} throws {@link ClassCastException}
     * when wrong class requested.
     */
    @Test
    public void getPayloadThrowsClassCastExceptionWhenWrongClassRequested() {
        Assertions.assertThrows(ClassCastException.class, () -> {
            Link linkObject = new Link();
            OperationResult operationResult = OperationResult.success().addPayload(linkObject);
            operationResult.getPayload(Boolean.class);
        });
    }

    /**
     * Tests that {@link OperationResult#getStringPayload()} provides stored {@link String}.
     */
    @Test
    public void getStringPayloadGetsString() {
        String str = "TheString";
        OperationResult operationResult = OperationResult.success().addPayload(str);
        String storedString = operationResult.getStringPayload();
        Assertions.assertEquals(str, storedString);
    }

    /**
     * Tests that {@link OperationResult#getStringPayload()} provides {@code null} if it is stored.
     */
    @Test
    public void getStringPayloadCanGetNull() {
        OperationResult operationResult = OperationResult.success().addPayload(null);
        String storedString = operationResult.getStringPayload();
        Assertions.assertNull(storedString);
    }

    /**
     * Tests that {@link OperationResult#getStringPayload()} throws {@link ClassCastException}
     * when stored non {@link String} object.
     */
    @Test
    public void getStringPayloadThrowsClassCastExceptionWhenNotStringStored() {
        Assertions.assertThrows(ClassCastException.class, () -> {
            Link linkObject = new Link();
            OperationResult operationResult = OperationResult.success().addPayload(linkObject);
            operationResult.getStringPayload();
        });
    }

    /**
     * Tests that {@link OperationResult#addPayload(String, Object)} adds Value under given Key.
     */
    @Test
    public void addPayloadWithKeyAddsValueUnderGivenKey() {
        String customKey = "MY_KEY";
        Link linkObject = new Link();
        OperationResult operationResult = OperationResult.success().addPayload(customKey, linkObject);
        Link storedObject = operationResult.getPayload(customKey, Link.class);
        Assertions.assertEquals(linkObject, storedObject);
    }

    /**
     * Tests that {@link OperationResult#addPayload(String, Object)} added value
     * cannot be retrieved using {@link OperationResult#getPayload(Class)}.
     */
    @Test
    public void addPayloadWithKeyStoresValueCannotBeRetrievedByDefaultMethod() {
        String customKey = "MY_KEY";
        Link linkObject = new Link();
        OperationResult operationResult = OperationResult.success().addPayload(customKey, linkObject);
        Link storedObject = operationResult.getPayload(Link.class);
        Assertions.assertNull(storedObject);
    }

    /**
     * Tests that {@link OperationResult#addPayload(String, Object)} can store {@code null} and
     * {@link OperationResult#getPayload(String, Class)} can get it.
     */
    @Test
    public void addPayloadWithKeyStoresNullAndGetPayloadWithKeyCanGetIt() {
        String customKey = "MY_KEY";
        OperationResult operationResult = OperationResult.success().addPayload(customKey, null);
        Link storedObject = operationResult.getPayload(customKey, Link.class);
        Assertions.assertNull(storedObject);
    }

    /**
     * Tests that {@link OperationResult#addPayload(String, Object)} throws {@link IllegalArgumentException}
     * when Key is {@code null}.
     */
    @Test
    public void addPayloadWithKeyThrowsIllegalArgumentExceptionWhenKeyIsNull() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            String str = "storeMe";
            OperationResult.success().addPayload(null, str);
        });
    }

    /**
     * Tests that {@link OperationResult#getPayload(String, Class)} throws {@link ClassCastException}
     * when wrong class requested.
     */
    @Test
    public void getPayloadWithKeyThrowsClassCastExceptionWhenWrongClassRequested() {
        Assertions.assertThrows(ClassCastException.class, () -> {
            String customKey = "MY_KEY";
            Link linkObject = new Link();
            OperationResult operationResult = OperationResult.success().addPayload(customKey, linkObject);
            operationResult.getPayload(customKey, Boolean.class);
        });
    }

    /**
     * Tests that {@link OperationResult#getPayload(String, Class)} throws {@link IllegalArgumentException}
     * when key is {@code null}.
     */
    @Test
    public void getPayloadWithKeyThrowsIllegalArgumentExceptionWhenKeyIsNull() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            String customKey = "MY_KEY";
            Link linkObject = new Link();
            OperationResult operationResult = OperationResult.success().addPayload(customKey, linkObject);
            operationResult.getPayload(null, Link.class);
        });
    }

    /**
     * Tests that {@link OperationResult#getStringPayload(String)} get {@link String} stored under given key.
     */
    @Test
    public void getStringPayloadWithKeyGetsStringStoredUnderGivenKey() {
        String customKey = "MY_KEY";
        String str = "TheString";
        OperationResult operationResult = OperationResult.success().addPayload(customKey, str);
        String storedString = operationResult.getStringPayload(customKey);
        Assertions.assertEquals(str, storedString);
    }

    /**
     * Tests that {@link OperationResult#getStringPayload(String)} can get {@code null} stored.
     */
    @Test
    public void getStringPayloadWithKeyCanGetNull() {
        String customKey = "MY_KEY";
        OperationResult operationResult = OperationResult.success().addPayload(customKey, null);
        String storedString = operationResult.getStringPayload(customKey);
        Assertions.assertNull(storedString);
    }

    /**
     * Tests that {@link OperationResult#getStringPayload(String)} throws {@link ClassCastException}
     * when wrong class requested.
     */
    @Test
    public void getStringPayloadWithKeyThrowsClassCastExceptionWhenNotStringStored() {
        Assertions.assertThrows(ClassCastException.class, () -> {
            String customKey = "MY_KEY";
            Link linkObject = new Link();
            OperationResult operationResult = OperationResult.success().addPayload(customKey, linkObject);
            operationResult.getStringPayload(customKey);
        });
    }

    /**
     * Tests that {@link OperationResult#getStringPayload(String)} throws {@link IllegalArgumentException}
     * when key is {@code null}.
     */
    @Test
    public void getStringPayloadWithKeyThrowsIllegalArgumentExceptionWhenKeyIsNull() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            String customKey = "MY_KEY";
            String str = "TheString";
            OperationResult operationResult = OperationResult.success().addPayload(customKey, str);
            operationResult.getStringPayload(null);
        });
    }
}
