package lblaszka.operationchain;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class OperationResultUnitTest {
    @Test
    public void testSuccessMethod() {
        String expected = "SUCCESS";


        OperationResult<String> success = OperationResult.success(expected);


        assertTrue(success.isSuccess());
        assertNotNull(success.get());
        assertEquals(expected, success.get());
        assertNull(success.getError());
    }
    @Test
    public void testFailMethod() {
        Throwable expected = new RuntimeException("ERROR");


        OperationResult<?> fail = OperationResult.fail(expected);


        assertFalse(fail.isSuccess());
        assertNotNull(fail.getError());
        assertEquals(expected, fail.getError());
        assertNull(fail.get());
    }
}