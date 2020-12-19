package cn.hotpot.websocketdispersion.websocket;

import cn.hotpot.websocketdispersion.constants.WebsocketConstant;
import cn.hotpot.websocketdispersion.constants.WebsocketMessageType;
import cn.hotpot.websocketdispersion.model.WebsocketRedisDto;
import cn.hotpot.websocketdispersion.tool.JsonUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import static cn.hotpot.websocketdispersion.constants.BaseConstant.CHANNEL_TOPIC_WEBSOCKET;

/**
 * @author qinzhu
 * @since 2020/12/17
 */
@Service
@RequiredArgsConstructor
public class WebsocketBus {
    private final StringRedisTemplate stringRedisTemplate;

    /**
     * 推送消息，点对点模式
     *
     * @param userCode 接受消息的用户号
     * @param message  消息内容
     */
    public void pushByP2P(String userCode, String message) {
        // 通过redis让所有websocket节点尝试发送消息，因为当前节点不一定持有与userCode对应的连接
        WebsocketRedisDto dto = new WebsocketRedisDto(WebsocketMessageType.P2P, WebsocketConstant.P2P_URL, message, userCode);
        stringRedisTemplate.convertAndSend(CHANNEL_TOPIC_WEBSOCKET, JsonUtil.toJson(dto));
    }

    /**
     * 推送消息，广播模式
     *
     * @param topic   主题
     * @param message 消息内容
     */
    public void pushByBroadcast(String topic, String message) {
        // 通过redis让所有websocket节点尝试发送消息，因为当前节点不一定持有与userCode对应的连接
        WebsocketRedisDto dto = new WebsocketRedisDto(WebsocketMessageType.BROADCAST, WebsocketConstant.BROADCAST_PREFIX + "/" + topic, message, null);
        stringRedisTemplate.convertAndSend(CHANNEL_TOPIC_WEBSOCKET, JsonUtil.toJson(dto));
    }
}
