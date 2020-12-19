package cn.hotpot.websocketdispersion.constants;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * websocket消息推送类型
 */
@Getter
@AllArgsConstructor
public enum WebsocketMessageType {

    P2P("p2p", "点对点"),
    BROADCAST("broadcast", "广播"),;

    private String code;
    private String value;
}
