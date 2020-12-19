package cn.hotpot.websocketdispersion.tool;

import cn.hotpot.websocketdispersion.model.UserDto;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpSession;
import java.util.Objects;

import static cn.hotpot.websocketdispersion.constants.BaseConstant.LOGIN_USER;

/**
 * @author qinzhu
 * @since 2020/12/18
 */
public class SessionUtil {
    private SessionUtil() {
    }

    public static UserDto getLoginUserInfo() {
        HttpSession session = getSession();
        assert session != null;
        Object loginUserJson = session.getAttribute(LOGIN_USER);
        return JsonUtil.parse((String) loginUserJson, UserDto.class);
    }

    public static HttpSession getSession() {
        ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        return requestAttributes == null ? null : requestAttributes.getRequest().getSession(false);
    }
}
