package cn.hotpot.websocketdispersion.model;

import cn.hotpot.websocketdispersion.constants.WebsocketMessageType;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.lang.Nullable;

/**
 * @author qinzhu
 * @since 2020/12/18
 */
@Data
@NoArgsConstructor
public class WebsocketRedisDto {
    /**
     * 推送类型
     */
    private String type;

    /**
     * 推送地址
     */
    private String destination;

    /**
     * 点对点推送时，还必须指定userCode
     */
    private String userCode;

    /**
     * 内容
     */
    private String content;

    public WebsocketRedisDto(WebsocketMessageType type, String destination, String content, @Nullable String userCode) {
        if (type == WebsocketMessageType.P2P && userCode == null) {
            throw new RuntimeException("必须指定userCode");
        }
        this.type = type.getCode();
        this.destination = destination;
        this.content = content;
        this.userCode = userCode;
    }
}
