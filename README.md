# LiveBus
- 由于MutableLiveData（androidx.lifecycle.MutableLiveData）在实现Activity/Service/BroadcastReceiver之间通信比较麻烦，而用EventBus无法满足与生命周期调度，故想在LiveBus的基础上，实现组件之间通信

## 快速使用
- 非粘性消息
```text
LiveBus.with("key", String.class);
```

- 粘性消息
```text
LiveBus.withSticky("key", String.class);
```
