package lblaszka.operationchain;

import java.util.function.Consumer;
import java.util.function.Function;

public class OperationChain<T, R> {

    public static <T, R> OperationChainBuilder<T, R> start(Function<T,R> operationFunction) {
        return new OperationChainBuilder<T,R>(OperationNode.of(operationFunction));
    }

    public static <T, R> OperationChainBuilder<T, R> start(Function<T,R> operationFunction, Consumer<R> undoFunction) {
        return new OperationChainBuilder<T,R>(OperationNode.of(operationFunction, undoFunction));
    }

    private final OperationNode<T, ?> firstNode;
    private final Consumer<R> onSuccess;
    private final Consumer<Throwable> onFail;

    OperationChain(OperationNode<T, ?> firstNode, Consumer<R> onSuccess, Consumer<Throwable> onFail) {
        this.firstNode = firstNode;
        this.onSuccess = onSuccess;
        this.onFail = onFail;
    }

    public void process(T t) {
        OperationResult<?> operationResult = this.firstNode.apply(t);

        if(operationResult.isSuccess()) {
            if(this.onSuccess != null) {
                this.onSuccess.accept((R) operationResult.get());
            }
        } else {
            if(this.onFail != null) {
                this.onFail.accept(operationResult.getError());
            } else {
                operationResult.getError().printStackTrace();
            }
        }
    }

}
