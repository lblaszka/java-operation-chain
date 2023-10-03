package lblaszka.operationchain;

class OperationResult<T> {
    public static <T> OperationResult<T> success(T t) {
        return new OperationResult<>(t, true);
    }

    public static OperationResult<?> fail(Throwable error) {
        return new OperationResult<>(error, false);
    }

    private final T t;
    private final Throwable error;
    private final boolean success;

    public OperationResult(T t, boolean success) {
        this.t = t;
        this.error = null;
        this.success = success;
    }

    public OperationResult(Throwable error, boolean success) {
        this.t = null;
        this.error = error;
        this.success = success;
    }

    public boolean isSuccess() {
        return this.success;
    }

    public T get() {
        return this.t;
    }

    public Throwable getError() {
        return this.error;
    }
}
