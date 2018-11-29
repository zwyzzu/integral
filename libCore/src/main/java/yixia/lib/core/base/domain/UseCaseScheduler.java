package yixia.lib.core.base.domain;

/**
 * Interface for schedulers, see {@link UseCaseThreadPoolScheduler}.
 */
public interface UseCaseScheduler {

    <V extends UseCase.ResponseValue> void notifyResponse(
            V response,
            UseCase.UseCaseCallback<V> useCaseCallback);

    <V extends UseCase.ResponseValue> void onError(UseCase.UseCaseCallback<V> useCaseCallback);
}
