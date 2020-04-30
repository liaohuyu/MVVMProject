package com.android.myapplication.http.rx;

import io.reactivex.Observable;
import io.reactivex.functions.Function;
import io.reactivex.functions.Predicate;
import io.reactivex.subjects.PublishSubject;
import io.reactivex.subjects.Subject;

/**
 * on 2020/4/23
 */
public class RxBus {
    private static volatile RxBus mDefaultInstance;
    private final Subject<Object> _bus = PublishSubject.create().toSerialized();//Subject 的四种Subject，即使观察者 也是接收者

    private RxBus() {
    }

    public static RxBus getDefault() {
        if (mDefaultInstance == null) {
            synchronized (RxBus.class) {
                if (mDefaultInstance == null) {
                    mDefaultInstance = new RxBus();
                }
            }
        }
        return mDefaultInstance;
    }

    public void send(Object o) {
        _bus.onNext(o);
    }

    public Observable<Object> toObservable() {
        return _bus;
    }

    /**
     * 提供了一个新的事件,根据code进行分发
     *
     * @param code 事件code
     * @param o
     */
    public void post(int code,Object o){
        _bus.onNext(new RxBusBaseMessage(code,o));
    }

    /**
     * 根据传递的code和 eventType 类型返回特定类型(eventType)的 被观察者
     * 对于注册了code为0，class为voidMessage的观察者，那么就接收不到code为0之外的voidMessage。
     *
     * @param code      事件code
     * @param eventType 事件类型
     * @param <T>
     * @return
     */
    public <T> Observable<T> toObservable(int code, Class<T> eventType) {
        return _bus.ofType(RxBusBaseMessage.class)
                .filter(new Predicate<RxBusBaseMessage>() {
                    @Override
                    public boolean test(RxBusBaseMessage rxBusBaseMessage) throws Exception {
                        //过滤code和eventType都相同的事件
                        return rxBusBaseMessage.getCode() == code && eventType.isInstance(rxBusBaseMessage.getObject());
                    }
                }).map(new Function<RxBusBaseMessage, Object>() {
                    @Override
                    public Object apply(RxBusBaseMessage rxBusBaseMessage) throws Exception {
                        return rxBusBaseMessage.getObject();//对序列的每一项都应用一个函数来变换Observable发射的数据序列
                    }
                }).cast(eventType);//强制转换为指定类型
    }


    /**
     * 根据传递的 eventType 类型返回特定类型(eventType)的 被观察者
     *
     * @param eventType 事件类型
     * @param <T>
     * @return
     */
    public <T> Observable<T> toObservable(Class<T> eventType) {
        return _bus.ofType(eventType);
    }

    /**
     * 判断是否有订阅者
     */
    public boolean hasObservers() {
        return _bus.hasObservers();
    }

}
