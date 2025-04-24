# 功能
接收windows端的音频,windows端不发出声音,曲线救国实现一个蓝牙耳机连接两个设备
# 介绍
这是AudioTransmitor的安卓端
AudioTransmitor的windows端:https://github.com/aurxsiu/AudioTransmitor-java-windows
基本功能可以使用了
# 基本使用
## 安卓
添加新项:内容(连接的Ip),备注
选择即可连接
#attention:只支持接收windows端的音频,同样,不支持windows之间连接
## windows
确保安装了ffmpeg并且配置了环境变量,可在命令行调用ffmpeg
确保安装了虚拟声卡(核心,虚拟声卡不发声,用来检测音频):https://vb-audio.com/Cable/index.html
直接运行jar文件
第一次运行会出现选择声卡的步骤,输入对应的字符串即可,如CABLE Output (VB-Audio Virtual Cable)
等待安卓端连接
# 不会实现的功能
## 广播实现设备的侦测
网络环境复杂,广播功能可能被扳,反正我是被封了,没有测试环境
## 其它设备的互联
没有测试环境