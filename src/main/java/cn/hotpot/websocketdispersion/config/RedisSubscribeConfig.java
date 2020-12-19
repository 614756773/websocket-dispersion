package cn.hotpot.websocketdispersion.config;

import cn.hotpot.websocketdispersion.websocket.WebsocketWorker;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;

import static cn.hotpot.websocketdispersion.constants.BaseConstant.CHANNEL_TOPIC_WEBSOCKET;

/**
 * @author qinzhu
 * @since 2020/12/18
 * redis订阅配置
 * 使用redis订阅解决websocket多节点问题
 */
@Configuration
public class RedisSubscribeConfig {
    @Bean
    RedisMessageListenerContainer redisContainer(RedisConnectionFactory factory,
                                                 WebsocketWorker listener) {
        final RedisMessageListenerContainer container = new RedisMessageListenerContainer();
        container.setConnectionFactory(factory);
        container.addMessageListener(listener, new ChannelTopic(CHANNEL_TOPIC_WEBSOCKET));
        return container;
    }
}
