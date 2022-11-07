# synchronized 和 lock

## synchronized 实现原理
Java中每一个对象都可以作为锁，这是synchronized实现同步的基础：
- 普通同步方法，锁是当前实例对象
- 静态同步方法，锁是当前类的class对象
- 同步方法块，锁是括号里面的对象

通过代码解析:
有如下代码:
```java
public class SynchronizedTest {
    public synchronized void test1() {

    }

    public void test2() {
        synchronized (this) {
            
        }
    }
}
```

利用`javap`工具查看生成的class文件信息:
```sh
% javac test/SynchronizedTest.java

% javap -c -p test/SynchronizedTest.class
Compiled from "SynchronizedTest.java"
public class test.SynchronizedTest {
  public test.SynchronizedTest();
    Code:
       0: aload_0
       1: invokespecial #1                  // Method java/lang/Object."<init>":()V
       4: return

  public synchronized void test1();
    Code:
       0: return

  public void test2();
    Code:
       0: aload_0
       1: dup
       2: astore_1
       3: monitorenter
       4: aload_1
       5: monitorexit
       6: goto          14
       9: astore_2
      10: aload_1
      11: monitorexit
      12: aload_2
      13: athrow
      14: return
    Exception table:
       from    to  target type
           4     6     9   any
           9    12     9   any
}
```

从上面可以看出，**同步代码块是使用monitorenter和monitorexit指令实现的** ，同步方法（在这看不出来需要看JVM底层实现）依靠的是方法修饰符上的ACC_SYNCHRONIZED实现。

> 同步代码块：monitorenter指令是在编译后插入到同步代码块的开始位置，monitorexit指令插入到同步代码块的结束位置，JVM需要保证每一个monitorenter都有一个monitorexit与之相对应。任何对象都有一个monitor与之相关联，当且一个monitor被持有之后，他将处于锁定状态。线程执行到monitorenter指令时，将会尝试获取对象所对应的monitor所有权，即尝试获取对象的锁；
> 同步方法：synchronized方法则会被翻译成普通的方法调用和返回指令如:invokevirtual、areturn指令，在VM字节码层面并没有任何特别的指令来实现被synchronized修饰的方法，而是在Class文件的方法表中将该方法的access_flags字段中的synchronized标志位置1，表示该方法是同步方法并使用调用该方法的对象或该方法所属的Class在JVM的内部对象表示Klass做为锁对象。

## Lock
lock 是一个接口
lock接口中每个方法的使用：`lock()`、`tryLock()`、`tryLock(long time, TimeUnit unit)`、`lockInterruptibly()`是用来获取锁的。 `unLock()`方法是用来释放锁的。 四个获取锁方法的区别：
1. lock() 方法是平时使用最多的方法，用来获取锁，如果锁已经被其他线程获取，则进行等待。
2. tryLock() 方法有返回值，表示是否获取锁成功 如果获取成功 返回 true，失败则表示锁被其他线程获取 返回 false 不会等待
3. tryLock(long time, TimeUnit unit) 方法类似 tryLock() 不过参数中的时间表示如果获取不到锁会等待一定的时间 在时间限制内如果还是无法拿到锁会返回 false
4. lockInterruptibly() 通过该方法获取锁时，如果线程正在等待锁，则这个线程可通过 interrupt() 方法中断等待(A B 两个线程同时通过 lock.lockInterruptibly() 获取某个锁，若此时A获取到了锁，B只有等待，此时B可以调用 threadB.interrupt() 方法中断等待) 注意: **当一个线程已经获取到锁之后，是不会被 interrupt() 方法中断的**

### ReentrantLock(可重入锁)
直接使用 `lock` 接口的话，需要实现很多方法，并不方便，ReentrantLock 是JDK中一个实现了Lock接口的类，并且 ReentrantLock 提供了更多的方法，ReentrantLock的意思是可重入锁

### ReadWriteLock(读写锁)
`ReadWriteLock` 也是一个接口，其中定义了两个方法;一个用来获取`读锁` 一个用来获取 `写锁` 将文件的读写操作分开，分成2个锁分配给线程，从而多个线程可以同时进行读操作，`ReentrantReadWriteLock` 实现了 `ReadWriteLock`

### ReentrantReadWriteLock
ReentrantReadWriteLock里面提供了很多丰富的方法，不过最主要的两个方法：readlock()和writelock用来获取读锁和写锁
注意：
- 如果有一个线程已经占用了读锁，则此时其他线程如果要申请写锁，则申请写锁的线程会一直等待释放读锁。
- 如果有一个线程已经占用了写锁，则此时其他线程如果申请写锁或者读锁，则申请的线程会一直等待释放写锁。

## 公平锁 & 非公平锁
公平锁: 在分配锁前是否有线程在等待获取该锁，优先将锁分配给排队时间最长的线程
非公平锁: 在分配锁时不考虑线程排队等待情况，**效率相比公平锁要高**
`ReentrantLock` 默认为非公平锁
```java
public ReentrantLock(boolean fair) {
    sync = fair ? new FairSync() : new NonfairSync();
}
```
公平锁使用`FairSync`同步组件，非公平锁使用`NonfairSync`同步组件。FairSync与NonfairSync均是ReentrantLock的内部类，又共同继承了ReentrantLock的一个内部类Sync。FairSync与NonfairSync不同的地方在于重写Sync的lock()方法与tryAcquire()方法。

## 共享锁 & 排他锁
- 排他锁 独占锁 互斥锁:
    - 每次仅允许一个线程持有该锁, ReentrantLock 为排他锁
- 共享锁: 
    - 允许多个线程同时获取该锁，并发访问共享资源，ReentrantReadWriteLock 中的读锁为共享锁的实现

## synchronized 和 lock 的区别
| 类别 | synchronized | lock |
| -- | -- | -- |
| 存在层次 | java 关键字，在jvm层面上 | java接口 |
| 用法 | synchronized 可以加在方法上，也可以加在特定代码块中，括号表示需要锁的对象 | lock只能写在代码里，不能直接修改方法 一般使用 `ReentrantLock`类作为锁，在加锁和解锁处需要通过 `lock()` 和 `unlock()` 显示指出 所以一般会在 `finally` 代码块中写 `unlock()` 防止死锁 |
| 实现 | synchronized 是**托管给 jvm 执行**的 | lock 是 java 写的控制锁的代码 |
| 锁的释放 | **完成或异常会自动释放锁** 1.以获取锁的线程执行完同步代码，释放锁 2.线程执行发生异常，jvm会让线程释放锁 | lock**发生异常时候，不会主动释放占有的锁，必须手动unlock来释放锁**，因此在`finally`中必须释放锁，不然容易造成线程死锁 |
| 锁的获取 | 假设A线程获得锁 B线程会等待; A线程若阻塞 B线程会一直等待 | 依不同的实现方式情况而定 |
| 锁状态 | 无法判断 | 可以判断 Lock可以通过**trylock**来知道有没有获取锁 |
| 锁类型 | **可重入 不可中断 非公平** | **可重入 可中断**(可以用interrupt来中断等待) **可公平**(两者皆可) |
| 性能 | 少量同步 | 大量同步 可以提高多个线程进行读操作的效率 可以通过readwritelock实现读写分离 |
| 调度 | 使用 `Object` 的 `wait` `notify` `notifyAll` 调度机制 | 使用 `Condition` 进行线程之间的调度 |

**在性能上来说，如果竞争资源不激烈，两者的性能是差不多的，而当竞争资源非常激烈时（即有大量线程同时竞争），此时Lock的性能要远远优于synchronized。**
- 性能区别上附加:
    - 在 java1.5 中，synchronized 是性能低效的;因为这是一个重量级操作，需要调用操作接口，导致有可能加锁消耗的系统时间比加锁以外的操作还多
    - 在 java1.6 中，synchronized 进行了很多优化，适应自旋 锁消除 锁粗化 轻量级锁 偏向锁;导致synchronized 的性能不必lock差。
    - **资源竞争激励的情况下，lock性能会比synchronize好，竞争不激励的情况下，synchronize比lock性能好**，synchronize会根据锁的竞争情况，从偏向锁-->轻量级锁-->重量级锁升级
    - **synchronized 原始采用的是CPU悲观锁机制，即线程获得的是独占锁。** 独占锁意味着其他线程只能依靠阻塞来等待线程释放锁。而在CPU转换线程阻塞时会引起线程上下文切换，当有很多线程竞争锁的时候，会引起CPU频繁的上下文切换导致效率很低。
    - **lock 使用的是乐观锁。即每次不加锁而是假设没有冲突而去完成某项操作，如果因为冲突失败就重试，直到成功为止。乐观锁实现机制就是CAS操作(compare and swap)** 在 `ReentrantLock` 代码中 获得锁的方法是 `compareAndSetState` 这个实际上就是调用 CPU 提供的特殊指令 (现代的CPU提供了指令，可以自动更新共享数据，而且能够检测到其他线程的干扰，而 compareAndSet() 就用这些代替了锁定。这个算法称作非阻塞算法，意思是一个线程的失败或者挂起不应该影响其他线程的失败或挂起的算法。)

synchronized原语和ReentrantLock在一般情况下没有什么区别，但是在非常复杂的同步应用中，请考虑使用ReentrantLock，特别是遇到下面2种需求的时候。
1. **某个线程在等待一个锁的控制权的这段时间需要中断**
    - 如果A、B 2个线程去竞争锁，A线程得到了锁，B线程等待，但是A线程这个时候实在有太多事情要处理，就是一直不返回，B线程可能就会等不及了，想中断自己，不再等待这个锁了，转而处理其他事情。这个时候ReentrantLock就提供了2种机制：可中断/可不中断
1.  **需要分开处理一些 `wait-notify` ReentrantLock 里面的 `Condition` 能够控制 notify 哪个线程**
    - Lock可以绑定多个condition。例如，ReentrantLock用来实现分组唤醒需要唤醒的线程们，可以精确唤醒，而不是像synchronized要么随机唤醒一个(notify)，要么唤醒全部线程(notifyAll)。
1. 具有**公平锁**功能，每个到来的线程都将排队等候
