package lblaszka.operationchain;

import java.util.function.Consumer;
import java.util.function.Function;

public class OperationChainBuilder<T, R> {

    private final OperationNode<T,?> firstChainNode;
    private final OperationNode<?,R> lastChainNode;

    OperationChainBuilder(OperationNode<T,R> operationNode) {
        this.firstChainNode = operationNode;
        this.lastChainNode = operationNode;
    }

    private OperationChainBuilder(OperationNode<T,?> firstChainNode, OperationNode<?, R> lastOperationNode) {
        this.firstChainNode = firstChainNode;
        this.lastChainNode = lastOperationNode;
    }

    public <LR> OperationChainBuilder<T, LR> next(Function<R,LR> operationFunction) {
        return this.next(OperationNode.of(operationFunction));
    }

    public <LR> OperationChainBuilder<T, LR> next(Function<R,LR> operationFunction, Consumer<LR> undoFunction) {
        return this.next(OperationNode.of(operationFunction, undoFunction));
    }

    private <LR> OperationChainBuilder<T, LR> next(OperationNode<R, LR> operationNode) {
        this.lastChainNode.setNextNode(operationNode);
        return new OperationChainBuilder<T, LR>(this.firstChainNode, operationNode);
    }

    public OperationChain<T> createChain() {
        return this.createChain(null, null);
    }

    public OperationChain<T> createChain(Consumer<R> onSuccess) {
        return this.createChain(onSuccess, null);
    }

    public OperationChain<T> createChain(Consumer<R> onSuccess, Consumer<Throwable> onFail) {
        return new OperationChain<>(this.firstChainNode, onSuccess, onFail);
    }
}
