自动定位显示Android NDK中c/c++代码的异常错误bug

先下载源码，在“根.找错误.二.自动从日志里面获取错误信息自动输出错误信息”里面配置sdk路径
分别是“sdk目录加命令”、 “adb位置”这两个变量。配置完毕后打包运行jar文件，放到本项目的模块目录下，假设”app“模块就放到app目录下，然后直接运行jar就行，可写个bat文件方便运行
等运行ndk报错闪退时自动输出c/c++代码出错的位置。
如果有多个模拟器或者多个手机连接电脑运行时可能没看到报错位置，重新关闭打开模拟器也需要重新运行jar

源码

gitee：https://gitee.com/qitaoguilin/NDKcodErrorUtlis.git
