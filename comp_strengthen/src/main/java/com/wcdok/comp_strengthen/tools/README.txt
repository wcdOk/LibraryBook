为什么这里不直接使用api project(":lib_strengthen")

因为我们只需要这个module的aar，而打包aar的时候并不会引入第三发的jar包，所有这里要自己定义一份