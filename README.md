# 工程简介
分布式websocket解决方案，使用redis订阅通知各个websocket节点


# 前提
- redis服务端 `无密码，若需要密码认证请修改application.yml`
- lombok插件

# 运行
为了模拟多节点下的websocket通信，所以需要启动至少两个进程。<br>
下文中描述的是启动两个节点

- 启动第一个节点
    - 使用mvn package将工程打包
    - 运行 java -jar websocket-dispersion-0.0.1-SNAPSHOT.jar启动
- 启动第二个节点
    - 修改application.yml中的port为8081
    - 修改ws.html中的 `var host = "http://127.0.0.1:8080";` 为 ``var host = "http://127.0.0.1:8081";``
    - 打包运行
- 使用两个不同的浏览器分别访问<br>
`http://localhost:8080/ws.html`<br>
`http://localhost:8081/ws.html`
- 两个浏览器中都点击`连接`按钮，页面如图：
[![rNcFwd.png](https://s3.ax1x.com/2020/12/19/rNcFwd.png)](https://imgchr.com/i/rNcFwd)
- 访问 `http://localhost:8080/sendTopic?message=hello%20everybody`
- 最后会发现两个浏览器中都显示文本`hello everybody`，如下图
[![rNcg1K.png](https://s3.ax1x.com/2020/12/19/rNcg1K.png)](https://imgchr.com/i/rNcg1K)
[![rNcH9P.png](https://s3.ax1x.com/2020/12/19/rNcH9P.png)](https://imgchr.com/i/rNcH9P)

