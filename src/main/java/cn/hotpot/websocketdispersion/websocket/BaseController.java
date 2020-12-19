package cn.hotpot.websocketdispersion.websocket;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.socket.WebSocketSession;

import java.util.Map;

@RestController
@Slf4j
@RequiredArgsConstructor
public class BaseController {

    private final SimpMessagingTemplate template;
    private final WebsocketBus websocketBus;

    /**
     * 服务器指定用户进行推送
     */
    @RequestMapping("/sendUser")
    public void sendUser(String userCode) {
        WebSocketSession webSocketSession = WebsocketSessionManager.get(userCode);
        // 主要防止broken pipe
        if (webSocketSession != null) {
            template.convertAndSendToUser(userCode, "/queue/sendUser", "您好");
        }

    }

    /**
     * 广播，服务器主动推给连接的客户端
     */
    @RequestMapping("/sendTopic")
    public void sendTopic(String message) {
        websocketBus.pushByBroadcast("sendTopic", message);
//        template.convertAndSend("/topic/sendTopic", message);
    }

    /**
     * 客户端发消息，服务端接收
     */
    @MessageMapping("/sendServer")
    public void sendServer(String message) {
        log.info("message:{}", message);
    }

    /**
     * 客户端发消息，然后服务端进行广播
     */
    @MessageMapping("/sendAllUser")
    @SendTo("/topic/sendTopic")
    public String sendAllUser(String message) {
        // 也可以采用template方式
        return message;
    }

    /**
     * 点对点用户聊天，这边需要注意，由于前端传过来json数据，所以使用@RequestBody
     */
    @MessageMapping("/sendMyUser")
    public void sendOneUser(@RequestBody Map<String, String> map) {
        log.info("map = {}", map);
        WebSocketSession webSocketSession = WebsocketSessionManager.get(map.get("name"));
        if (webSocketSession != null) {
            log.info("sessionId = {}", webSocketSession.getId());
            template.convertAndSendToUser(map.get("name"), "/queue/sendUser", map.get("message"));
        }
    }


}