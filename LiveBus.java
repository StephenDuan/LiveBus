/*
  **  Copyright [2020] [dzg of copyright owner]
  *
  * Licensed under the Apache License, Version 2.0 (the "License");
  * you may not use this file except in compliance with the License.
  * You may obtain a copy of the License at
  *
  *     http://www.apache.org/licenses/LICENSE-2.0
  *
  * Unless required by applicable law or agreed to in writing, software
  * distributed under the License is distributed on an "AS IS" BASIS,
  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  * See the License for the specific language governing permissions and
  * limitations under the License.
 */


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;

import java.util.HashMap;
import java.util.Map;

/**
 * @author dzuguang
 * bus 事件总线 activity service fragment receiver之间通信
 */
public class LiveBus {

    private Map<String, MutableLiveData<Object>> bus;

    private LiveBus(){
        bus = new HashMap<>();
    }

    public static LiveBus getInstance() {
        return LiveBus.SingletonHolder.INSTANCE;
    }

    private static class SingletonHolder {
        private static final LiveBus INSTANCE = new LiveBus();
    }

    /**
     * 粘性消息 接收订阅之前的消息
     * @param name
     * @param tClass
     * @param <T>
     * @return
     */
    public <T>MutableLiveData<T> withSticky(String name, Class<T> tClass) {
        if (!bus.containsKey(name)){
            bus.put(name, new MutableLiveData<>());
        }
        return (MutableLiveData<T>) bus.get(name);
    }

    public MutableLiveData withSticky(String name) {
        return withSticky(name, Object.class);
    }

    public <E>MutableLiveData<E> with(String name, Class<E> tClass) {
        if (!bus.containsKey(name)){
            bus.put(name, new BusMutableLiveData<>());
        }
        return (MutableLiveData<E>) bus.get(name);
    }

    public MutableLiveData with(String name) {
        return with(name, Object.class);
    }


    private static class ObserverWrapper<T> implements Observer<T> {

        private Observer<T> observer;

        public ObserverWrapper(Observer<T> observer) {
            this.observer = observer;
        }

        @Override
        public void onChanged(@Nullable T t) {
            if (observer != null) {
                if (isCallOnObserve()) {
                    return;
                }
                observer.onChanged(t);
            }
        }

        private boolean isCallOnObserve() {
            StackTraceElement[] stackTraceElements = Thread.currentThread().getStackTrace();
            if (stackTraceElements != null && stackTraceElements.length > 0) {
                for (StackTraceElement element : stackTraceElements) {
                    if ("androidx.lifecycle.LifecycleRegistry".equals(element.getClassName()) &&
                            "addObserver".equals(element.getMethodName())) {
                        return true;
                    }
                }
            }
            return false;
        }
    }

    private static class BusMutableLiveData<T> extends MutableLiveData<T> {

        @Override
        public void observe(@NonNull LifecycleOwner owner, @NonNull Observer<? super T> observer) {
            super.observe(owner, new ObserverWrapper(observer));
        }
    }
}
