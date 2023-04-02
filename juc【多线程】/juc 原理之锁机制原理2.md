# 多线程第三版 

## JUC之锁机制原理

### AQS原理

1. Abstract : 因为它并不知道怎么上锁。模板方法设计模式即可，暴露出上锁逻辑
2. Queue：线程阻塞队列
3. Synchronizer：同步
4. CAS+state 完成多线程抢锁逻辑
5. Queue 完成抢不到锁的线程排队

#### AQS核心代码

获取锁的代码。

```java
public final void acquire(int arg) {
    if (!tryAcquire(arg) && // 子类判定获取锁失败返回false，那么这里取反，表示为 true
        acquireQueued(addWaiter(Node.EXCLUSIVE), arg)) // 获取失败后添加到阻塞队列
        selfInterrupt();
}

// 子类实现获取锁的逻辑，AQS并不知道你怎么用这个state来上锁
protected boolean tryAcquire(int arg) {
    throw new UnsupportedOperationException();
}
```

释放锁的代码。

```java
public final boolean release(int arg) {
    // 子类判定释放锁成功
    if (tryRelease(arg)) {
        // 检查阻塞队列唤醒即可
        Node h = head;
        if (h != null && h.waitStatus != 0)
            unparkSuccessor(h);
        return true;
    }
    return false;
}

// 子类实现获取锁的逻辑，AQS并不知道你怎么用这个state来上锁
protected boolean tryRelease(int arg) {
    throw new UnsupportedOperationException();
}
```

#### 总结

子类只需要实现自己的获取锁逻辑和释放锁逻辑即可，至于排队阻塞等待、唤醒机制均由AQS来完成。

### ReentrantLock 原理

#### 概念

基于AQS实现的可重入锁实现类。

#### 核心变量和构造器

```java
public class ReentrantLock implements Lock, java.io.Serializable {
    private final Sync sync;
    public ReentrantLock() {
        // 默认为非公平锁。为何默认为非公平锁？因为通过大量测试下来，发现非公平锁的性能优于公平锁
        sync = new NonfairSync();
    }
    public ReentrantLock(boolean fair) {
        // 由fair变量来表明选择锁类型
        sync = fair ? new FairSync() : new NonfairSync();
    }

    abstract static class Sync extends AbstractQueuedSynchronizer {
        abstract void lock();
        // 非公平锁标准获取锁方法
        final boolean nonfairTryAcquire(int acquires) {
            final Thread current = Thread.currentThread();
            int c = getState();
            // 当执行到这里时，正好获取所得线程释放了锁，那么可以尝试抢锁
            if (c == 0) {
                // 继续抢锁，不看有没有线程排队
                if (compareAndSetState(0, acquires)) {
                    setExclusiveOwnerThread(current);
                    return true;
                }
            }
            // 当前线程就是持有锁的线程，表明锁重入
            else if (current == getExclusiveOwnerThread()) {
                // 利用state整形变量进行次数记录
                int nextc = c + acquires;
                // 如果超过了int表示范围，表明符号溢出，所以抛出异常0111 1111 + 1 = 1000 0000 
                if (nextc < 0)
                    throw new Error("Maximum lock count exceeded");
                setState(nextc);
                return true;
            }
            // 返回false 表明需要AQS来将当前线程放入阻塞队列，然后进行阻塞操作等待唤醒获取锁
            return false;
        }

        // 公平锁和非公平锁公用方法，因为在释放锁的时候，并不区分是否公平
        protected final boolean tryRelease(int releases) {
            int c = getState() - releases;
            // 如果当前线程不是上锁的那个线程
            if (Thread.currentThread() != getExclusiveOwnerThread())
                throw new IllegalMonitorStateException();
            boolean free = false;
            // 不是重入锁，那么当前线程一定是释放锁了，然后我们把当前AQS用于保存当前锁对象的变量ExclusiveOwnerThread设置为null，表明释放锁成功
            if (c == 0) {
                free = true;
                setExclusiveOwnerThread(null);
            }
            // 注意：此时state全局变量没有改变，也就意味着在setState之前，没有别的线程能够获取锁，这时保证了以上的操作原子性
            setState(c);
            // 告诉AQS，我当前释放锁成功了，你可以去唤醒正在等待锁的线程了
            return free;
        }

        protected final boolean isHeldExclusively() {
            return getExclusiveOwnerThread() == Thread.currentThread();
        }

        final ConditionObject newCondition() {
            return new ConditionObject();
        }

    }

    static final class NonfairSync extends Sync {
        // 由ReentrantLock调用获取锁
        final void lock() {
            // 非公平锁，直接抢锁，不管有没有线程排队
            if (compareAndSetState(0, 1))
                // 上锁成功，那么标识当前线程为获取锁的线程
                setExclusiveOwnerThread(Thread.currentThread());
            else
                // 抢锁失败，进入AQS的标准获取锁流程
                acquire(1);
        }

        protected final boolean tryAcquire(int acquires) {
            // 使用父类提供的获取非公平锁的方法来获取锁
            return nonfairTryAcquire(acquires);
        }
    }

    static final class FairSync extends Sync {
        // 由ReentrantLock调用
        final void lock() {
            // 没有尝试抢锁，直接进入AQS标准获取锁流程
            acquire(1);
        }
        
        // AQS调用，子类自己实现获取锁的流程
        protected final boolean tryAcquire(int acquires) {
            final Thread current = Thread.currentThread();
            int c = getState();
            // 此时有可能正好获取锁的线程释放了锁，也有可能本身就没有线程获取锁
            if (c == 0) {
                // 注意：这里和非公平锁的区别在于：hasQueuedPredecessors看看队列中是否有线程正在排队，没有的话再通过CAS抢锁
                if (!hasQueuedPredecessors() &&
                    compareAndSetState(0, acquires)) {
                    // 抢锁成功
                    setExclusiveOwnerThread(current);
                    return true;
                }
            }
            // 当前线程就是获取锁的线程，那么这里是锁重入，和非公平锁操作一模一样
            else if (current == getExclusiveOwnerThread()) {
                int nextc = c + acquires;
                if (nextc < 0)
                    throw new Error("Maximum lock count exceeded");
                setState(nextc);
                return true;
            }
            // 返回false 表明需要AQS来将当前线程放入阻塞队列，然后进行阻塞操作等待唤醒获取锁
            return false;
        }
    }
}
```

#### 核心方法

1. 获取锁操作

   ```java
   public void lock() {
       // 直接通过sync同步器上锁
       sync.lock();
   }
   ```

2. 释放锁操作

   ```java
   public void unlock() {
       sync.release(1);
   }

### ReentrantReadWriteLock 原理

#### 用例

将原来的锁，分割为两把锁：读锁、写锁。适用于读多写少的场景，读锁可以并发，写锁与其他锁互斥。写写互斥、写读互斥、读读兼容。

```java
public class ThreadDemo {
    static volatile int a;

    public static void readA() {
        System.out.println(a);
    }

    public static void writeA() {
        a++;
    }

    public static void main(String[] args) {
        ReentrantReadWriteLock reentrantReadWriteLock = new ReentrantReadWriteLock();
        ReentrantReadWriteLock.ReadLock readLock = reentrantReadWriteLock.readLock();
        ReentrantReadWriteLock.WriteLock writeLock = reentrantReadWriteLock.writeLock();
        Thread readThread1 = new Thread(() -> {
            readLock.lock();
            try {
                readA();
            } finally {
                readLock.unlock();
            }

        });
        Thread readThread2 = new Thread(() -> {
            readLock.lock();
            try {
                readA();
            } finally {
                readLock.unlock();
            }
        });

        Thread writeThread = new Thread(() -> {
            writeLock.lock();
            try {
                writeA();
            } finally {
                writeLock.unlock();
            }
        });

        readThread1.start();
        readThread2.start();
        writeThread.start();
    }
}
```

#### 核心变量和构造器

该接口用于获取读锁和写锁对象

```java
public interface ReadWriteLock {
    // 用于获取读锁
    Lock readLock();
    // 用于获取写锁
    Lock writeLock();
}
```

readerLock和writerLock变量用于支撑以上描述的ReadWriteLock接口的读锁和写锁方法。通过构造方法得知，读写锁对象的创建和用例均依赖于公平锁或者非公平锁同步器。

```java
public class ReentrantReadWriteLock implements ReadWriteLock {
    // 读锁对象
    private final ReentrantReadWriteLock.ReadLock readerLock;
    // 写锁对象
    private final ReentrantReadWriteLock.WriteLock writerLock;
    // 同步器
    final Sync sync;
    // 默认构造器，创建了非公平锁
    public ReentrantReadWriteLock() {
        this(false);
    }
    // 根据fair变量，来选择创建不同的锁：公平锁 FairSync 和非公平锁 NonfairSync
    public ReentrantReadWriteLock(boolean fair) {
        sync = fair ? new FairSync() : new NonfairSync();
        // 用同步器来创建读写锁对象
        readerLock = new ReadLock(this);
        writerLock = new WriteLock(this);
    }
    public ReentrantReadWriteLock.WriteLock writeLock() { return writerLock; }
    public ReentrantReadWriteLock.ReadLock  readLock()  { return readerLock; }    
}
```

### 核心内部类

1. Sync类

   核心变量和构造器

   我们说读锁可以多个线程同时持有，而写锁只允许一个线程持有，此时我们称 读锁-----共享锁  写锁------互斥锁（排他锁）。然后我们在AQS中了解到一个变量state，它是32位的值，那么我们这里将其切割为高16位和低16位。

   ```java
   abstract static class Sync extends AbstractQueuedSynchronizer {
       // 高16位用于表示读锁
       static final int SHARED_SHIFT   = 16;
       // 用于对高16位操作：加1 减1 
       static final int SHARED_UNIT    = (1 << SHARED_SHIFT);
       // 最大读锁量
       static final int MAX_COUNT      = (1 << SHARED_SHIFT) - 1;
       // 用于获取低16位的值。例如 获取低八位：0000 0000 1111 1111
       static final int EXCLUSIVE_MASK = (1 << SHARED_SHIFT) - 1;
   
       /** 获取当前持有读锁的线程数量  */
       static int sharedCount(int c)    { return c >>> SHARED_SHIFT; }
       /** 获取当前持有写锁的线程数量 */
       static int exclusiveCount(int c) { return c & EXCLUSIVE_MASK; }
   
       // 高16位为所有读锁获取，那么我想知道每个线程对于读锁重入的次数？采用ThreadLocal来进行统计，每个线程自己统计自己的
       static final class HoldCounter {
           int count = 0;
           final long tid = getThreadId(Thread.currentThread());
       }
       // 继承自ThreadLocal，重写了其中的initialValue方法，该方法将在线程第一次获取该变量时调用初始化HoldCounter计数器
       static final class ThreadLocalHoldCounter
           extends ThreadLocal<HoldCounter> {
           public HoldCounter initialValue() {
               return new HoldCounter();
           }
       }
       // 创建ThreadLocal对象
       private transient ThreadLocalHoldCounter readHolds;
       // 缓存最后一个线程获取的读锁数量
       private transient HoldCounter cachedHoldCounter;
       // 保存获取到该锁的第一个读锁线程
       private transient Thread firstReader = null;
       // 保存第一个该锁的第一个读锁线程获取到的读锁数量
       private transient int firstReaderHoldCount;
   
       Sync() {
           // 构造器中初始化ThreadLocalHoldCounter ThreadLocal对象
           readHolds = new ThreadLocalHoldCounter();
           // 用于保证可见性，使用了state变量的volatile语义
           setState(getState()); 
       }
   }
   ```

   tryAcquire获取写锁的流程

   由AQS调用，用于子类实现自己的上锁逻辑，和原有获取互斥锁保持一致，

   ```java
   protected final boolean tryAcquire(int acquires) {
       // 获取当前线程
       Thread current = Thread.currentThread();
       // 获取当前状态值和互斥锁的数量
       int c = getState();
       int w = exclusiveCount(c);
       // 状态值有效
       if (c != 0) {
           // 有线程获取到了读锁或者当前线程不是持有互斥锁的线程
           if (w == 0 ||  // 有线程获取到了读锁
               current != getExclusiveOwnerThread()) // 有线程获取到了写锁
               // 返回false 让AQS执行阻塞操作
               return false;
           // 写锁重入，而又由于写锁的数量保存在低16位，所以直接加就行了
           if (w + exclusiveCount(acquires) > MAX_COUNT)
               throw new Error("Maximum lock count exceeded");
           setState(c + acquires);
           return true;
       }
       // 既没有读锁，也没有写锁
       if (writerShouldBlock() || // 由子类实现判断当前线程是否应该获取写锁
           !compareAndSetState(c, c + acquires)) // 通过CAS抢写锁
           return false;
       // 获取写锁成功，那么将当前线程标识为获取互斥锁的线程对象
       setExclusiveOwnerThread(current);
       return true;
   }
   ```

   tryAcquireShared获取读锁的流程获取写锁的流程

   ```java
   protected final int tryAcquireShared(int unused) {
       // 获取到当前线程对象
       Thread current = Thread.currentThread();
       // 获取到当前状态值
       int c = getState();
       if (exclusiveCount(c) != 0 && // 有没有线程持有写锁
           getExclusiveOwnerThread() != current) // 如果有线程获取到了互斥锁，那么进一步看看是不是当前线程
           // 不是当前线程，那么直接返回-1，告诉AQS获取共享锁失败
           return -1;
       int r = sharedCount(c);
       if (!readerShouldBlock() &&
           r < MAX_COUNT &&
           compareAndSetState(c, c + SHARED_UNIT)) {
           if (r == 0) {
               firstReader = current;
               firstReaderHoldCount = 1;
           } else if (firstReader == current) {
               firstReaderHoldCount++;
           } else {
               HoldCounter rh = cachedHoldCounter;
               if (rh == null || rh.tid != getThreadId(current))
                   cachedHoldCounter = rh = readHolds.get();
               else if (rh.count == 0)
                   readHolds.set(rh);
               rh.count++;
           }
           return 1;
       }
       return fullTryAcquireShared(current);
   }
   ```

   tryRelease获取写锁的流程

   ```java
   protected final boolean tryRelease(int releases) {
       if (!isHeldExclusively())
           throw new IllegalMonitorStateException();
       int nextc = getState() - releases;
       boolean free = exclusiveCount(nextc) == 0;
       if (free)
           setExclusiveOwnerThread(null);
       setState(nextc);
       return free;
   }
   ```

   tryReleaseShared获取读锁的流程获取写锁的流程
