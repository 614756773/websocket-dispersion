package cn.hotpot.websocketdispersion.config;

import cn.hotpot.websocketdispersion.websocket.WebsocketSessionManager;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.WebSocketHandlerDecorator;
import org.springframework.web.socket.handler.WebSocketHandlerDecoratorFactory;

import java.security.Principal;

/**
 * @author qinzhu
 * @since 2020/12/18
 * websocket握手监听
 */
@Component
@Slf4j
public class WebSocketDecoratorFactory implements WebSocketHandlerDecoratorFactory {
    @Override
    public WebSocketHandler decorate(WebSocketHandler handler) {
        return new WebSocketHandlerDecorator(handler) {
            @Override
            public void afterConnectionEstablished(WebSocketSession session) throws Exception {
                Principal principal = session.getPrincipal();
                if (principal == null) {
                    log.info("关闭未经认证的连接");
                    session.close();
                    return;
                }

                log.info("新的连接，sessionId：{}，userCode：{}", session.getId(), principal.getName());
                WebsocketSessionManager.put(principal.getName(), session);
                super.afterConnectionEstablished(session);
            }

            @Override
            public void afterConnectionClosed(WebSocketSession session, CloseStatus closeStatus) throws Exception {
                Principal principal = session.getPrincipal();
                if (principal == null) {
                    log.info("连接关闭，sessionId：{}", session.getId());
                    return;
                }

                log.info("连接关闭，sessionId：{}，userCode：{}", session.getId(), principal.getName());
                WebsocketSessionManager.remove(principal.getName());
                super.afterConnectionClosed(session, closeStatus);
            }
        };
    }
}
