//package com.wcdOk.aspectj
//
//import com.android.build.gradle.AppExtension
//import org.gradle.api.Plugin
//import org.gradle.api.Project;
// /**
//  * @author: wcd
//  * @email : wcdwangyi@163.com
//  * @date : 7/26/21 10:20 AM
//  * @desc : 创建一个任务
//  */
//class AspectJXPlugin implements Plugin<Project>{
//
//    @Override
//    void apply(Project project) {
//        project.repositories {
//            mavenLocal()
//        }
//
//        project.dependencies {
//            //动态的引入两个库
//            if(project.gradle.gradleVersion > "4.0"){
//                project.logger.debug("gradlew version > 4.0")
//                implementation 'org.aspectj:aspectjrt:1.9.5'
//            }else{
//                project.logger.debug("gradlew version < 4.0")
//                compile 'org.aspectj:aspectjrt:1.9.5'
//            }
//        }
//
//        project.extensions.create("aspectjx", AJXExtension)
//
//        if(project.plugins.hasPlugin(AppPlugin)){
//            project.gradle.addListener(new TimeTrace())
//
//            AppExtension android = project.extensions.getByType(AppExtension)
//            android.registerTransform(new AJXTransform(project))
//
//        }
//
//    }
//}