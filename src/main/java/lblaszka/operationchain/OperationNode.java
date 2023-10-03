package lblaszka.operationchain;

import java.util.function.Consumer;
import java.util.function.Function;

public class OperationNode<T, R> {
    static <T,R> OperationNode<T,R> of(Function<T, R> operationFunction) {
        return of(operationFunction, null);
    }

    static <T,R> OperationNode<T,R> of(Function<T, R> operationFunction, Consumer<R> undoFunction) {
        return new OperationNode<>(operationFunction, undoFunction);
    }

    private final Function<T, R> operationFunction;
    private final Consumer<R> undoFunction;

    private OperationNode<R, ?> nextNode;

    public OperationNode(Function<T, R> operationFunction, Consumer<R> undoFunction) {
        this.operationFunction = operationFunction;
        this.undoFunction = undoFunction;
    }

    synchronized void setNextNode(OperationNode<R, ?> nextNode) {
        if(this.nextNode != null) {
            throw new IllegalStateException("\'nextNode\' is not null. Probably this object was used in another builder");
        }

        this.nextNode = nextNode;
    }

    public OperationResult<?> apply(T t) {
        R r;
        try{
            r = this.operationFunction.apply(t);
        } catch (Exception e) {
            return OperationResult.fail(e);
        }

        if(this.nextNode == null) {
            return OperationResult.success(r);
        }

        OperationResult<?> operationResult = this.nextNode.apply(r);

        if(operationResult.isSuccess()) {
            return operationResult;
        }

        if(this.undoFunction != null) {
            try {
                this.undoFunction.accept(r);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return operationResult;
    }
}
