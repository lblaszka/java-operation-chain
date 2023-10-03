package lblaszka.operationchain;

import org.junit.jupiter.api.Test;

import java.util.function.Consumer;
import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class OperationChainBuilderUnitTest {

    @Test
    public void testNextMethodCorrectLinkNextNode() {
        String expected = "";

        Function<String, ?> function = mock(Function.class);

        OperationChain<String> operationChain = new OperationChainBuilder<String, String>(OperationNode.of(ign -> ign))
                .next(function).createChain();


        operationChain.process(expected);


        verify(function, times(1)).apply(expected);
    }

    @Test
    public void testNextMethodWithUndoFunctionCorrectLinkNextNode() {
        String expected = "";

        Function<String, String> function = mock(Function.class);
        when(function.apply(expected)).thenReturn(expected);

        Consumer<String> undoFunction = mock(Consumer.class);

        Function<String, ?> throwingFunction = ign -> {throw new RuntimeException();};

        OperationChain<String> operationChain = new OperationChainBuilder<String, String>(OperationNode.of(ign -> ign))
                .next(function, undoFunction)
                .next(throwingFunction)
                .createChain();


        operationChain.process(expected);


        verify(function, times(1)).apply(expected);
        verify(undoFunction, times(1)).accept(expected);
    }

    @Test
    public void testCreateChainMethod() {
        OperationChain<String> operationChain = new OperationChainBuilder<String, String>(OperationNode.of(ign -> ign))
                .createChain();


        assertNotNull(operationChain);
    }

    @Test
    public void testCreateChainMethodWithOnSuccessFunction() {
        String expected = "";

        Consumer<String> onSuccess = mock(Consumer.class);

        new OperationChainBuilder<String, String>(OperationNode.of(ign -> ign))
                .createChain(onSuccess)
                .process(expected);


        verify(onSuccess, times(1)).accept(expected);
    }

    @Test
    public void testCreateChainMethodWithOnSuccessAndOnFailFunctions() {
        String exceptionSuccess = "SUCCESS";
        RuntimeException expectedFail = new RuntimeException();

        Function<String, String> function = value -> {
           if(exceptionSuccess.equals(value))
               return value;
           throw expectedFail;
        };

        Consumer<String> onSuccess = mock(Consumer.class);
        Consumer<Throwable> onFail = mock(Consumer.class);

        OperationChain<String> operationChain = new OperationChainBuilder<>(OperationNode.of(function))
                .createChain(onSuccess, onFail);

        operationChain.process(exceptionSuccess);
        operationChain.process("FAIL");


        verify(onSuccess, times(1)).accept(exceptionSuccess);
        verify(onFail, times(1)).accept(expectedFail);
    }
}