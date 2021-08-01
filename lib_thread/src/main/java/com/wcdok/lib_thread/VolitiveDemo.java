package com.wcdok.lib_thread;

/**
 * @author: wcd
 * @email: wcdwangyi@163.com
 * @date: 7/26/21 8:58 AM
 * @desc: 仅添加volatile是不能保证数据同步的，volatile只能保证可见性，但是不能保证原子性操作
 *
 * 如果保证原子性：有2种方案
 * （1）加锁:只
 * （2）使用原子类
 */
public class VolitiveDemo {
    private static Object object = new Object();
    public static void main(String[] args) {
        Runnable runnable = new SyncTask();
        for (int i = 0; i < 100; i++) {
            new Thread(runnable,"thread-"+i).start();

        }

    }

    private static class VolatileTask implements  Runnable{

        private volatile int count = 0;
        @Override
        public void run() {
            for (int i = 0; i < 1000; i++) {
                count++;
                System.out.println(Thread.currentThread().getName()+":wcd:"+count);
            }

        }
    }
    private static class SyncTask implements  Runnable{

        private int count = 0;
        @Override
        public void run() {
//            for (int i = 0; i < 10000; i++) {
//                //这种写法不好，因为在循环中加锁，就会导致反复的进出锁代码块
//                synchronized (SyncTask.class){
//                    count++;
//                }
//                System.out.println(Thread.currentThread().getName()+":wcd:"+count);
//            }
            synchronized (object){
                for (int i = 0; i < 10000; i++) {
                    //这种写法不好，因为在循环中加锁，就会导致反复的进出锁代码块
                    count++;
                    System.out.println(Thread.currentThread().getName()+":wcd:"+count);
                }

            }

        }
    }
}
