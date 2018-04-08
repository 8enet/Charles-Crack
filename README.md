# Charles 破解工具web版

[![Build Status](https://travis-ci.org/8enet/Charles-Crack.svg?branch=master)](https://travis-ci.org/8enet/Charles-Crack)

支持charles 4.1.3-4.2.5版本的破解，自定义注册名称

[立即使用](https://www.zzzmode.com/mytools/charles/)


## 运行
请先配置[charles-crack.json](src/main/resources/charles-crack.json)文件，修改`origJar`字段为本地`charles.jar`的路径

```
gradle bootJar
java -jar build/libs/*.jar --ckConfig=/path/charles-crack.json

```

## 使用
启动成功后，打开 http://127.0.0.1:8090 输入名称选择版本然后下载生成的charles.jar文件进行手动替换。

## 参数

```
--server.port 端口,默认8090

--ckConfig 破解配置的文件位置,内容参考charles-crack.json,
``` 



## License
GPL v3.0
