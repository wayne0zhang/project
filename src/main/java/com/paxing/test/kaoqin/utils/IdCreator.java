package com.paxing.test.kaoqin.utils;

import lombok.extern.slf4j.Slf4j;

/**
 * snowFlake (雪花算法)生成全局唯一id
 * <p>
 * 雪花算法的返回是一个64位的long型id，类似于各种一些的位数定义，64位分成了几个部分：
 * - 第一位：符号位，保持0，表示正数
 * - 第2-42位：共41位，时间戳未，以毫秒表示的时间戳
 * - 第43-47位：共5位，机房表示，最多31个机房
 * - 第48-52位：共5位，机器表示，每个机房最多31个机器
 * - 第53-64位：共12位，生成的序列
 * 所以，对于每个机器，每毫秒可以生成最多4096个序列。
 * <p>
 * 测试结果：0.5s 可生成100w 不重复id
 *
 * ps:一个二进制负数的知识：对应的正数，取反（每一位取反），+1，即为对应的负数
 *
 * @author wtzhang (zhangwentao001@lianjia.com)
 * @summary
 * @since 2019/2/28
 */
@Slf4j
public class IdCreator {

    private static class IdCreatorHolder {
        private static final IdCreator INSTANCE = new IdCreator();
    }

    public static IdCreator get() {
        return IdCreatorHolder.INSTANCE;
    }

    /**
     * 这个就是代表了机器id
     */
    private long workerId;

    /**
     * 这个就是代表了机房id
     */
    private long dataCenterId;

    /**
     * 这个就是代表了一毫秒内生成的多个id的最新序号
     */
    private long sequence;

    public IdCreator() {
        this(0, 0, 0);
    }

    public IdCreator(long workerId, long dataCenterId, long sequence) {
        // sanity check for workerId
        // 这儿不就检查了一下，要求就是你传递进来的机房id和机器id不能超过32，不能小于0
        if (workerId > maxWorkerId || workerId < 0) {

            throw new IllegalArgumentException(
                    String.format("worker Id can't be greater than %d or less than 0", maxWorkerId));
        }

        if (dataCenterId > maxDatacenterId || dataCenterId < 0) {

            throw new IllegalArgumentException(
                    String.format("datacenter Id can't be greater than %d or less than 0", maxDatacenterId));
        }
        this.workerId = workerId;
        this.dataCenterId = dataCenterId;
        this.sequence = sequence;
    }

    /**
     * 计算标记时间
     */
    private long twepoch = 1288834974657L;
    private long workerIdBits = 5L;
    private long datacenterIdBits = 5L;

    // 这个是二进制运算，就是5 bit最多只能有31个数字，也就是说机器id最多只能是32以内
    private long maxWorkerId = -1L ^ (-1L << workerIdBits);
    // 这个是一个意思，就是5 bit最多只能有31个数字，机房id最多只能是32以内
    private long maxDatacenterId = -1L ^ (-1L << datacenterIdBits);
    private long sequenceBits = 12L;
    private long workerIdShift = sequenceBits;
    private long datacenterIdShift = sequenceBits + workerIdBits;
    private long timestampLeftShift = sequenceBits + workerIdBits + datacenterIdBits;
    private long sequenceMask = -1L ^ (-1L << sequenceBits);
    private long lastTimestamp = -1L;

    public long getWorkerId() {
        return workerId;
    }

    public long getDataCenterId() {
        return dataCenterId;
    }

    public long getTimestamp() {
        return System.currentTimeMillis();
    }

    // 这个是核心方法，通过调用nextId()方法，让当前这台机器上的snowflake算法程序生成一个全局唯一的id
    public synchronized long nextId() {
        // 这儿就是获取当前时间戳，单位是毫秒
        long timestamp = timeGen();
        if (timestamp < lastTimestamp) {
            System.err.printf(
                    "clock is moving backwards. Rejecting requests until %d.", lastTimestamp);
            throw new RuntimeException(
                    String.format("Clock moved backwards. Refusing to generate id for %d milliseconds",
                            lastTimestamp - timestamp));
        }

        // 下面是说假设在同一个毫秒内，又发送了一个请求生成一个id
        // 这个时候就得把seqence序号给递增1，最多就是4096
        if (lastTimestamp == timestamp) {

            // 这个意思是说一个毫秒内最多只能有4096个数字，无论你传递多少进来，
            //这个位运算保证始终就是在4096这个范围内，避免你自己传递个sequence超过了4096这个范围
            sequence = (sequence + 1) & sequenceMask;
            if (sequence == 0) {
                timestamp = tilNextMillis(lastTimestamp);
            }

        } else {
            sequence = 0;
        }
        // 这儿记录一下最近一次生成id的时间戳，单位是毫秒
        lastTimestamp = timestamp;
        // 这儿就是最核心的二进制位运算操作，生成一个64bit的id
        // 先将当前时间戳左移，放到41 bit那儿；将机房id左移放到5 bit那儿；将机器id左移放到5 bit那儿；将序号放最后12 bit
        // 最后拼接起来成一个64 bit的二进制数字，转换成10进制就是个long型
        return ((timestamp - twepoch) << timestampLeftShift) |
                (dataCenterId << datacenterIdShift) |
                (workerId << workerIdShift) | sequence;
    }

    private long tilNextMillis(long lastTimestamp) {

        long timestamp = timeGen();

        while (timestamp <= lastTimestamp) {
            timestamp = timeGen();
        }
        return timestamp;
    }

    private long timeGen() {
        return System.currentTimeMillis();
    }


    //---------------测试---------------
    public static void main(String[] args) {

        IdCreator worker = new IdCreator(1, 1, 15);

        long x = System.currentTimeMillis();
        for (int i = 0; i < 30; i++) {
            System.out.println(worker.nextId());
        }
        System.out.println(System.currentTimeMillis() - x);

        long number = -1;
        //原始数二进制
        System.out.println(Long.toBinaryString(0));
        System.out.println(Long.toBinaryString(1));
        System.out.println(Long.toBinaryString(Long.MAX_VALUE));
        System.out.println(Long.toBinaryString(Long.MIN_VALUE));
        System.out.println(Long.toBinaryString(number));
        number = number << 5;
        System.out.println(Long.toBinaryString(number));
        number = -1L ^ number;
        //左移一位
        System.out.println(Long.toBinaryString(number));
        number = number >> 1;
        //右移一位

        System.out.println(Long.toBinaryString(number));

    }


}
