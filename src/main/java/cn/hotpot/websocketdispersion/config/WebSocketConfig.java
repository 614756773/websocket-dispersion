package cn.hotpot.websocketdispersion.config;

import cn.hotpot.websocketdispersion.constants.BaseConstant;
import cn.hotpot.websocketdispersion.constants.WebsocketConstant;
import cn.hotpot.websocketdispersion.model.UserDto;
import cn.hotpot.websocketdispersion.tool.JsonUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketTransportRegistration;
import org.springframework.web.socket.server.HandshakeHandler;
import org.springframework.web.socket.server.support.DefaultHandshakeHandler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.security.Principal;
import java.util.Map;
import java.util.UUID;

/**
 * @author qinzhu
 * @since 2020/12/18
 */
@Configuration
@EnableWebSocketMessageBroker
@RequiredArgsConstructor
@Slf4j
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {
    private final WebSocketDecoratorFactory webSocketDecoratorFactory;

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/greeting")
                .setAllowedOriginPatterns("*")
                // .setAllowedOrigins("*") 不能设置该参数，否则会影响到MvcConfig中的配置，从而报错：
                // When allowCredentials is true, allowedOrigins cannot contain the special value "*"since that cannot be set on the "Access-Control-Allow-Origin" response header. To allow credentials to a set of origins, list them explicitly or consider using "allowedOriginPatterns" instead.
                // 所以在MvcConfig的配置和此配置中，跨越配置都应该使用setAllowedOriginPatterns
                .setHandshakeHandler(principalHandshakeHandler())
                .withSockJS();
    }

    /**
     * /queue 点对点
     * /topic 广播
     * /user 点对点前缀
     */
    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        registry.enableSimpleBroker(WebsocketConstant.BROADCAST_PREFIX, WebsocketConstant.P2P_PREFIX);
        registry.setUserDestinationPrefix("/user");
    }

    @Override
    public void configureWebSocketTransport(WebSocketTransportRegistration registration) {
        registration.addDecoratorFactory(webSocketDecoratorFactory);
    }

    /**
     * 握手期间识别session，包装成Principal
     */
    @Bean
    HandshakeHandler principalHandshakeHandler() {
        return new DefaultHandshakeHandler() {

            @Override
            protected Principal determineUser(ServerHttpRequest request, WebSocketHandler wsHandler, Map<String, Object> attributes) {
                if (request instanceof ServletServerHttpRequest) {
                    ServletServerHttpRequest servletServerHttpRequest = (ServletServerHttpRequest) request;
                    HttpServletRequest httpRequest = servletServerHttpRequest.getServletRequest();
                    HttpSession session = httpRequest.getSession();
                    if (session == null) {
                        return null;
                    }

                    // 下面这3行模拟用户登录，正常的操作其实应该是在其他地方又一个登录接口
                    String userCode = UUID.randomUUID().toString().substring(0, 4);
                    UserDto userDto = new UserDto().setUserCode(userCode);
                    session.setAttribute(BaseConstant.LOGIN_USER, JsonUtil.toJson(userDto));
                    log.info("用户{}登录", userCode);

                    Object loginUserJson = session.getAttribute(BaseConstant.LOGIN_USER);
                    UserDto user = JsonUtil.parse((String) loginUserJson, UserDto.class);
                    if (user == null) {
                        return null;
                    }

                    return user::getUserCode;
                }
                return null;
            }
        };
    }
}
