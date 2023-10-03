package lblaszka.operationchain;

import org.junit.jupiter.api.Test;

import java.util.function.Consumer;
import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class OperationNodeUnitTest {
    @Test
    public void testOfMethodCorrectCreateObjectWithGivenFunction() {
        Function<?, ?> expected = mock(Function.class);


        OperationNode<?, ?> operationNode = OperationNode.of(expected);
        operationNode.apply(null);


        verify(expected, times(1)).apply(any());
    }


    @Test
    public void testOfMethodCorrectCreateObjectWithGivenFunctionWithUndoFunction() {
        Function<?, ?> expectedFunction = mock(Function.class);
        Consumer<?> expectedConsumer = mock(Consumer.class);
        Function<?,?> throwingFunction = ign -> {throw new RuntimeException();};


        OperationNode<?, ?> operationNode = OperationNode.of((Function)expectedFunction, expectedConsumer);
        operationNode.setNextNode((OperationNode)OperationNode.of(throwingFunction));
        operationNode.apply(null);


        verify(expectedFunction, times(1)).apply(any());
        verify(expectedConsumer, times(1)).accept(any());
    }


    @Test
    public void testApplyMethodWithNullNextNode() {
        OperationNode<?, ?> operationNode = OperationNode.of(ign -> ign);


        assertDoesNotThrow(() -> operationNode.apply(null));
    }


    @Test
    public void testApplyMethodCallNextOperationNode() {
        OperationNode nextOperationNode = mock(OperationNode.class);
        when(nextOperationNode.apply(any())).thenReturn(OperationResult.success(null));

        OperationNode<?, ?> operationNode = OperationNode.of(ign -> ign);
        operationNode.setNextNode(nextOperationNode);


        operationNode.apply(null);


        verify(nextOperationNode, times(1)).apply(any());
    }


    @Test
    public void testApplyMethodCallUndoFunction() {
        OperationNode nextOperationNode = mock(OperationNode.class);
        when(nextOperationNode.apply(any())).thenReturn((OperationResult)OperationResult.fail(null));

        Consumer<?> undoFunction = mock(Consumer.class);

        OperationNode<?, ?> operationNode = OperationNode.of((Function) ign -> ign, undoFunction);
        operationNode.setNextNode(nextOperationNode);


        operationNode.apply(null);


        verify(undoFunction, times(1)).accept(any());
    }


    @Test
    public void testApplyMethodWithNullUndoFunction() {
        OperationNode nextOperationNode = mock(OperationNode.class);
        when(nextOperationNode.apply(any())).thenReturn((OperationResult)OperationResult.fail(null));

        OperationNode<?, ?> operationNode = OperationNode.of((Function) ign -> ign, null);
        operationNode.setNextNode(nextOperationNode);


        assertDoesNotThrow(() -> operationNode.apply(null));
    }

    @Test
    public void testApplyMethodReturnSuccessResult() {
        String expected = "";
        OperationNode<?, ?> operationNode = OperationNode.of(ign -> expected);


        OperationResult<?> operationResult = operationNode.apply(null);


        assertNotNull(operationResult);
        assertTrue(operationResult.isSuccess());
        assertEquals(expected, operationResult.get());
    }

    @Test
    public void testApplyMethodReturnFailResult() {
        RuntimeException expected = new RuntimeException();
        OperationNode<?, ?> operationNode = OperationNode.of(ign -> {throw expected;});


        OperationResult<?> operationResult = operationNode.apply(null);


        assertNotNull(operationResult);
        assertFalse(operationResult.isSuccess());
        assertEquals(expected, operationResult.getError());
    }

    @Test
    public void testApplyMethodReturnSuccessResultOfNextNode() {
        OperationResult expected = OperationResult.success(null);
        OperationNode nextOperationNode = mock(OperationNode.class);
        when(nextOperationNode.apply(any())).thenReturn(expected);

        OperationNode<?, ?> operationNode = OperationNode.of(ign -> ign);
        operationNode.setNextNode(nextOperationNode);


        OperationResult actual = operationNode.apply(null);


        assertEquals(expected, actual);
    }

    @Test
    public void testApplyMethodReturnFailResultOfNextNode() {
        OperationResult expected = OperationResult.fail(null);
        OperationNode nextOperationNode = mock(OperationNode.class);
        when(nextOperationNode.apply(any())).thenReturn(expected);

        OperationNode<?, ?> operationNode = OperationNode.of(ign -> ign);
        operationNode.setNextNode(nextOperationNode);


        OperationResult actual = operationNode.apply(null);


        assertEquals(expected, actual);
    }

    @Test
    public void testApplyMethodCatchExceptionFormUndoFunction() {
        OperationResult expected = OperationResult.fail(null);
        OperationNode nextOperationNode = mock(OperationNode.class);
        when(nextOperationNode.apply(any())).thenReturn(expected);

        OperationNode<?, ?> operationNode = OperationNode.of(ign -> ign, ign -> {throw new RuntimeException();});
        operationNode.setNextNode(nextOperationNode);


        assertDoesNotThrow(() -> operationNode.apply(null));
    }
}