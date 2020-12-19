package cn.hotpot.websocketdispersion.model;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @author qinzhu
 * @since 2020/12/18
 */
@Data
@Accessors(chain = true)
public class UserDto {
    private String userCode;
}
