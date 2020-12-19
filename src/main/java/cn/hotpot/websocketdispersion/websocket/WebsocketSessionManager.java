package cn.hotpot.websocketdispersion.websocket;

import org.springframework.web.socket.WebSocketSession;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author qinzhu
 * @since 2020/12/18
 * ws会话管理
 */
public class WebsocketSessionManager {
    private static Map<String, WebSocketSession> holder = new ConcurrentHashMap<>();

    public static void put(String key, WebSocketSession webSocketSession) {
        holder.put(key, webSocketSession);
    }

    public static void remove(String key) {
        holder.remove(key);
    }

    public static WebSocketSession get(String key) {
        return holder.get(key);
    }

    public static Boolean contains(String key) {
        return holder.containsKey(key);
    }
}
