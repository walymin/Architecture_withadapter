package architecture_o.network.base;

import java.util.IdentityHashMap;
import java.util.Map;

import architecture_o.network.bean.JavaBean;
import io.reactivex.Observable;
import io.reactivex.ObservableOnSubscribe;


/**
 * Created by Herbert on 2017/3/14.
 */

public class RequestObservableFactory {
    // Maps for storing strong references to RequestBase while they are subscribed to.
    // This is needed if users create Observables without manually maintaining a reference to them.
    // In that case RequestBase might be GC'ed too early.
    ThreadLocal<StrongReferenceCounter<RequestBase>> requestRefs = new ThreadLocal<StrongReferenceCounter<RequestBase>>(){
        @Override
        protected StrongReferenceCounter<RequestBase> initialValue() {
            return new StrongReferenceCounter<>();
        }
    };

    public <T extends JavaBean, E extends RequestBase<T>>
    Observable<E> from(final E request){
       return Observable.create((ObservableOnSubscribe<E>)subscribe -> {
            requestRefs.get().acquireReference(request);
            final RequestStatusBase.OnResultListener listener = (struct, result, reason) -> {
                if (!subscribe.isDisposed())
                    subscribe.onNext(request);
            };
            request.setOnResultListener(listener);
        });
        //// TODO: 2017/4/19 完善
        /*return Observable.create(new Observable.OnSubscribe<E>() {
            @Override
            public void call(final Subscriber<? super E> subscriber) {
                requestRefs.get().acquireReference(request);
                final RequestStatusBase.OnResultListener listener = new RequestStatusBase.OnResultListener() {
                    @Override
                    public void onResult(RequestStatusBase struct, RequestStatusBase.StructResult result, String reason) {
                        if(!subscriber.isUnsubscribed()){
                            subscriber.onNext(request);
                        }
                    }
                };
                request.setOnResultListener(listener);
                subscriber.add(Subscriptions.create(new Action0() {
                    @Override
                    public void call() {
                        request.unsetOnResultListener();
                        requestRefs.get().releaseReference(request);
                    }
                }));
//                subscriber.onNext(request);
            }
        });*/
    }

    // Helper class for keeping track of strong references to objects.
    private static class StrongReferenceCounter<K> {

        private final Map<K, Integer> references = new IdentityHashMap<K, Integer>();

        public void acquireReference(K object) {
            Integer count = references.get(object);
            if (count == null) {
                references.put(object, 1);
            } else {
                references.put(object, count + 1);
            }
        }

        public void releaseReference(K object) {
            Integer count = references.get(object);
            if (count == null) {
                throw new IllegalStateException("Object does not have any references: " + object);
            } else if (count > 0) {
                references.put(object, count - 1);
            } else {
                references.remove(object);
            }
        }
    }
}
