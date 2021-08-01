package com.wcdok.comp_aop;

import android.util.Log;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;

/**
 * @author: wcd
 * @email: wcdwangyi@163.com
 * @date: 7/26/21 2:42 PM
 * @desc: https://juejin.cn/post/6844904112396615688
 *下面👇，我们就来看看要掌握 AspectJ 的使用，我们需要了解的一些 核心概念。
 * 1、横切关注点
 * 对哪些方法进行拦截，拦截后怎么处理。
 * 2、切面（Aspect）
 * 类是对物体特征的抽象，切面就是对横切关注点的抽象。
 * 3、连接点（JoinPoint）
 * JPoint 是一个程序的关键执行点，也是我们关注的重点。它就是指被拦截到的点（如方法、字段、构造器等等）。
 * 4、切入点（PointCut）
 * 对 JoinPoint 进行拦截的定义。PointCut 的目的就是提供一种方法使得开发者能够选择自己感兴趣的 JoinPoint。
 * 5、通知（Advice）
 * 切入点仅用于捕捉连接点集合，但是，除了捕捉连接点集合以外什么事情都没有做。
 * 事实上实现横切行为我们要使用通知。
 * 它一般指拦截到 JoinPoint 后要执行的代码，分为 前置、后置、环绕 三种类型。
 * 这里，我们需要 注意 Advice Precedence（优先权） 的情况，
 * 比如我们对同一个切面方法同时使用了 @Before 和 @Around 时就会报错，
 * 此时会提示需要设置 Advice 的优先级。
 *
 * execution 就是处理 Join Point 的类型，通常有如下两种类型：
 * 1）、call：代表调用方法的位置，插入在函数体外面。
 * 2）、execution：代表方法执行的位置，插入在函数体内部。
 */
@Aspect
public class AspectJActivity {

    @After("execution(* android.app.Activity.on**(..))")
    public void onResumeMethod(JoinPoint joinPoint) throws Throwable {
        Log.i("AspectJActivity", "aspect:::" + joinPoint.getSignature());
    }


}
