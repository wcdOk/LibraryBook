//package com.wcdOk.aspectj
//
//import com.android.build.api.transform.QualifiedContent
//import com.android.build.api.transform.Transform
//import com.android.build.api.transform.TransformException
//import com.android.build.api.transform.TransformInvocation
//import org.gradle.api.Project
//
//
//class AspectJXTransform extends Transform {
//
//    AJXProcedure ajxProcedure
//
//    AspectJXTransform(Project project){
//        ajxProcedure = new AJXProcedure(proj)
//    }
//    /**
//     * 设置我们自定义的 Transform 对应的 Task 名称。Gradle 在编译的时候，会将这个名称显示在控制台上
//     * */
//    @Override
//    String getName() {
//        return "AspectJX"
//    }
///**
// * 在项目中会有各种各样格式的文件，通过 getInputType 可以设置 LifeCycleTransform 接收的文件类型，
// * 此方法返回的类型是 Set<QualifiedContent.ContentType> 集合
// * */
//    @Override
//    Set<QualifiedContent.ContentType> getInputTypes() {
//        return ImmutableSet.<QualifiedContent.ContentType>of(QualifiedContent.DefaultContentType.CLASSES)
//    }
//    /**
//     * 这个方法规定自定义 Transform 检索的范围，具体有以下几种取值:
//     *
//     * */
//    @Override
//    Set<? super QualifiedContent.Scope> getScopes() {
//        return TransformManager.SCOPE_FULL_PROJECT
//    }
//
//    @Override
//    boolean isIncremental() {
//        //是否支持增量编译
//        return true
//    }
//
//    @Override
//    void transform(TransformInvocation transformInvocation) throws TransformException, InterruptedException, IOException {
//
//
//
//        super.transform(transformInvocation)
//    }
//}