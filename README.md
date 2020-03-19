# thrift-rpc-trace
Thrift 跨端调用链跟踪



- #### Thrift的通信流程

![thrift-java](https://raw.githubusercontent.com/Janson-Leung/thrift-rpc-trace/master/thrift-java.jpg)

所有RPC的流程都差不多，绿色部分表示可以扩展的点。

而对于实现调用链埋点，也就是trace信息传递这一简单的功能来说，可以通过扩展TProtocol和TProcessor，在通信的时候多带一个trace上下文信息进去。



- #### 协议改造

  #### Thrift消息格式
  
  主要分为四种：
  
  1. Message，读写消息头，消息头包含了版本号(version)，方法名(name)，序列号(seqid)等信息
  
  2. Struct，将RPC方法的参数/返回值封装成结构体，读写结构体即表示要读写RPC方法参数了
  
  3. Field，每一个参数都被抽象成Field，Field主要包含了字段的索引信息，类型信息等
  
  4. Type，即读写各种具体的数据
  
     

因为要扩展TProtocol，而目前业务在使用的是TBinaryProtocol，这种protocol的struct是空的，也就是message后面直接是field。生成的消息大致如下：

![TBinaryProtocol](https://raw.githubusercontent.com/Janson-Leung/thrift-rpc-trace/master/TBinaryProtocol.jpg)

考虑到thrift读取field的一些特性：

1. Thrift生成的代码在读写字节流时，都是按照生成的TField的索引号去判断，然后读取的
2. Thrift提供了skip和stop解析Filed的机制

那么其实可以对field做一个扩展：

![TBinaryProtocol2](https://raw.githubusercontent.com/Janson-Leung/thrift-rpc-trace/master/TBinaryProtocol2.jpg)

我们自定义一个attachment，然后把trace上下文信息传进去。

然后我们去实现读写filed0的方法，以及将字节流复位的reset方法就好了(复位后不影响thrift的正常执行流程)。



- #### 最终链路

  ![jaeger](https://raw.githubusercontent.com/Janson-Leung/thrift-rpc-trace/master/jaeger.png)



