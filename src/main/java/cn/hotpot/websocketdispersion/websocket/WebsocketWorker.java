package cn.hotpot.websocketdispersion.websocket;

import cn.hotpot.websocketdispersion.constants.WebsocketMessageType;
import cn.hotpot.websocketdispersion.model.WebsocketRedisDto;
import cn.hotpot.websocketdispersion.tool.JsonUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.connection.DefaultMessage;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;

/**
 * @author qinzhu
 * @since 2020/12/18
 * 接受到redis的订阅消息后，进行websocket推送
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class WebsocketWorker implements MessageListener {
    private final SimpMessagingTemplate template;

    @Override
    public void onMessage(Message message, byte[] pattern) {
        WebsocketRedisDto dto = JsonUtil.parse(((DefaultMessage) message).toString(), WebsocketRedisDto.class);
        if (log.isDebugEnabled()) {
            log.debug("收到订阅消息：{}", JsonUtil.toJson(dto));
        }

        String type = dto.getType();
        if (WebsocketMessageType.P2P.getCode().equals(type)
                && WebsocketSessionManager.contains(dto.getUserCode())) {
            template.convertAndSendToUser(dto.getUserCode(), dto.getDestination(), dto.getContent());
            return;
        }

        if (WebsocketMessageType.BROADCAST.getCode().equals(type)) {
            template.convertAndSend(dto.getDestination(), dto.getContent());
            return;
        }

        throw new RuntimeException("错误的WebsocketMessageType");
    }
}
