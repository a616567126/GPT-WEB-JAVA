package com.intelligent.bot.utils.sys;

import java.sql.Timestamp;
import java.util.UUID;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicLong;

public class IDUtil {



    private static class inner{
        public static IDUtil idUtil = new IDUtil(0, 0);
    }

    /**
     * 初始化ID获取方法，共全局使用
     */
    public static long  getNextId(){
        return inner.idUtil.nextId();
    }

    /**
     * 开始时间截 (2010-01-01)
     */
    private final long twepoch = 1514736000L;

    /**
     * 机器id所占的位数
     */
    private final long workerIdBits = 5L;

    /**
     * 数据标识id所占的位数
     */
    private final long datacenterIdBits = 5L;

    /**
     * 支持的最大机器id，结果是31 (这个移位算法可以很快的计算出几位二进制数所能表示的最大十进制数)
     */
    private final long maxWorkerId = -1L ^ (-1L << workerIdBits);

    /**
     * 支持的最大数据标识id，结果是31
     */
    private final long maxDatacenterId = -1L ^ (-1L << datacenterIdBits);

    /**
     * 序列在id中占的位数
     */
    private final long sequenceBits = 12L;

    /**
     * 机器ID向左移12位
     */
    private final long workerIdShift = sequenceBits;

    /**
     * 数据标识id向左移17位(12+5)
     */
    private final long datacenterIdShift = sequenceBits + workerIdBits;

    /**
     * 时间截向左移22位(5+5+12)
     */
    private final long timestampLeftShift = sequenceBits + workerIdBits + datacenterIdBits;

    /**
     * 生成序列的掩码，这里为4095 (0b111111111111=0xfff=4095)
     */
    private final long sequenceMask = -1L ^ (-1L << sequenceBits);

    /**
     * 工作机器ID(0~31)
     */
    private long workerId;

    /**
     * 数据中心ID(0~31)
     */
    private long datacenterId;

    /**
     * 毫秒内序列(0~4095)
     */
    private long sequence = 0L;

    /**
     * 上次生成ID的时间截
     */
    private long lastTimestamp = -1L;

    //==============================Constructors=====================================

    /**
     * 构造函数
     *
     * @param workerId     工作ID (0~31)
     * @param datacenterId 数据中心ID (0~31)
     */
    public IDUtil(long workerId, long datacenterId) {
        if (workerId > maxWorkerId || workerId < 0) {
            throw new IllegalArgumentException(String.format("worker Id can't be greater than %d or less than 0", maxWorkerId));
        }
        if (datacenterId > maxDatacenterId || datacenterId < 0) {
            throw new IllegalArgumentException(String.format("datacenter Id can't be greater than %d or less than 0", maxDatacenterId));
        }
        this.workerId = workerId;
        this.datacenterId = datacenterId;
    }

    /**
     * 获得下一个ID (该方法是线程安全的)
     *
     * @return SnowflakeId
     */
    public synchronized long nextId() {
        long timestamp = timeGen();

        //如果当前时间小于上一次ID生成的时间戳，说明系统时钟回退过这个时候应当抛出异常
        if (timestamp < lastTimestamp) {
            throw new RuntimeException(
                    String.format("Clock moved backwards.  Refusing to generate id for %d milliseconds", lastTimestamp - timestamp));
        }

        //如果是同一时间生成的，则进行毫秒内序列
        if (lastTimestamp == timestamp) {
            sequence = (sequence + 1) & sequenceMask;
            //毫秒内序列溢出
            if (sequence == 0) {
                //阻塞到下一个毫秒,获得新的时间戳
                timestamp = tilNextMillis(lastTimestamp);
            }
        }
        //时间戳改变，毫秒内序列重置
        else {
            sequence = 0L;
        }

        //上次生成ID的时间截
        lastTimestamp = timestamp;

        //移位并通过或运算拼到一起组成64位的ID
        return ((timestamp - twepoch) << timestampLeftShift) //
                | (datacenterId << datacenterIdShift) //
                | (workerId << workerIdShift) //
                | sequence;
    }

    /**
     * 阻塞到下一个毫秒，直到获得新的时间戳
     *
     * @param lastTimestamp 上次生成ID的时间截
     * @return 当前时间戳
     */
    protected long tilNextMillis(long lastTimestamp) {
        long timestamp = timeGen();
        while (timestamp <= lastTimestamp) {
            timestamp = timeGen();
        }
        return timestamp;
    }

    /**
     * 返回以毫秒为单位的当前时间
     *
     * @return 当前时间(毫秒)
     */
    protected long timeGen() {
        //return System.currentTimeMillis();
        return SystemClock.now();
    }


    public static class SystemClock {
        private final long period;
        private final AtomicLong now;
        ExecutorService executor = Executors.newSingleThreadExecutor();

        private SystemClock(long period) {
            this.period = period;
            this.now = new AtomicLong(System.currentTimeMillis());
            scheduleClockUpdating();
        }

        private static class InstanceHolder {
            public static final SystemClock INSTANCE = new SystemClock(1);
        }

        private static SystemClock instance() {
            return InstanceHolder.INSTANCE;
        }

        private void scheduleClockUpdating() {
            ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor(new ThreadFactory() {
                @Override
                public Thread newThread(Runnable runnable) {
                    Thread thread = new Thread(runnable, "System Clock");
                    thread.setDaemon(true);
                    return thread;
                }
            });
            scheduler.scheduleAtFixedRate(new Runnable() {
                @Override
                public void run() {
                    now.set(System.currentTimeMillis());
                }
            }, period, period, TimeUnit.MILLISECONDS);
        }

        private long currentTimeMillis() {
            return now.get();
        }

        public static long now() {
            return instance().currentTimeMillis();
        }

        public static String nowDate() {
            return new Timestamp(instance().currentTimeMillis()).toString();
        }
    }
    /**
     * 获取完整大写UUID
     * @return
     */
    public static String getUUIDToUpper(){
        return UUID.randomUUID().toString().toUpperCase();
    }

    /**
     * 获取去掉"-"大写UUID
     * @return
     */
    public static String getUUIDToUpperFormat(){
        return UUID.randomUUID().toString().toUpperCase().replace("-","");
    }

    /**
     * 获取完整小写UUID
     * @return
     */
    public static String getUUIDToLower(){
        return UUID.randomUUID().toString().toLowerCase();
    }

    /**
     * 获取去掉"-"小写UUID
     * @return
     */
    public static String getUUIDToLowerFormat(){
        return UUID.randomUUID().toString().toLowerCase().replace("-","");
    }
//    public static void main(String[] args) {
//        long start = System.currentTimeMillis();
//        System.out.println(ESBootApplication.idUtil.nextId());
//    }

}
