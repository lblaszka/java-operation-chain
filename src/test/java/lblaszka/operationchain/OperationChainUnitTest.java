package lblaszka.operationchain;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.function.Consumer;
import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.Mockito.*;

class OperationChainUnitTest {
    private final String OPERATION_FUNCTION_RETURN = "RETURN";

    private final Function<String, ?> throwingFunction = ign -> {throw new RuntimeException();};

    private Function<String, String> operationFunction;
    private Consumer<String> undoFunction;

    @BeforeEach
    public void before() {
        operationFunction = mock(Function.class);
        when(operationFunction.apply(any())).thenReturn(OPERATION_FUNCTION_RETURN);

        undoFunction = mock(Consumer.class);
    }

    @Test
    public void testStartMethodCreateBuilderWithGivenFunction() {
        String expected = "";


        OperationChain.start(operationFunction)
                .createChain()
                .process(expected);


        verify(operationFunction, times(1)).apply(expected);
    }

    @Test
    public void testStartMethodCreateBuilderWithGivenFunctionAndUndoFunction() {
        String expected = "";


        OperationChain.start(operationFunction, undoFunction)
                .next(throwingFunction)
                .createChain(ign -> {}, ign -> {})
                .process(expected);


        verify(operationFunction, times(1)).apply(expected);
        verify(undoFunction, times(1)).accept(OPERATION_FUNCTION_RETURN);
    }

    @Test
    public void testProcessMethodCallFirstNode() {
        String expected = "";

        OperationNode<String,?> operationNode = mock(OperationNode.class);
        when(operationNode.apply(any())).thenReturn(OperationResult.success(null));

        OperationChain<String,?> operationChain = new OperationChain<>(operationNode, ign -> {}, ign -> {});


        operationChain.process(expected);


        verify(operationNode, times(1)).apply(expected);
    }

    @Test
    public void testProcessMethodCorrectCallOnSuccess() {
        String expected = "";

        OperationNode<String,?> operationNode = mock(OperationNode.class);
        when(operationNode.apply(any())).thenReturn((OperationResult) OperationResult.success(expected));

        Consumer<String> onSuccess = mock(Consumer.class);

        OperationChain<String, String> operationChain = new OperationChain<>(operationNode, onSuccess, ign -> {});


        operationChain.process(expected);


        verify(onSuccess, times(1)).accept(expected);
    }

    @Test
    public void testProcessMethodNotThrowIfOnSuccessIsNull() {
        OperationNode<String,?> operationNode = mock(OperationNode.class);
        when(operationNode.apply(any())).thenReturn(OperationResult.success(null));

        Consumer<String> onSuccess = mock(Consumer.class);

        OperationChain<String, String> operationChain = new OperationChain<>(operationNode, null, ign -> {});


        assertDoesNotThrow(() -> operationChain.process(""));
    }

    @Test
    public void testProcessMethodCorrectCallOnFail() {
        Throwable expected = new RuntimeException();

        OperationNode<String,?> operationNode = mock(OperationNode.class);
        when(operationNode.apply(any())).thenReturn((OperationResult) OperationResult.fail(expected));

        Consumer<Throwable> onFail = mock(Consumer.class);

        OperationChain<String, String> operationChain = new OperationChain<>(operationNode, ign -> {}, onFail);


        operationChain.process("");


        verify(onFail, times(1)).accept(expected);
    }

    @Test
    public void testProcessMethodNotThrowIfOnFailIsNull() {
        OperationNode<String,?> operationNode = mock(OperationNode.class);
        when(operationNode.apply(any())).thenReturn((OperationResult) OperationResult.fail(new RuntimeException()));

        OperationChain<String, String> operationChain = new OperationChain<>(operationNode, ign -> {}, null);


        assertDoesNotThrow(() -> operationChain.process(""));
    }

}