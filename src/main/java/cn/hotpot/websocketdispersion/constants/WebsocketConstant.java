package cn.hotpot.websocketdispersion.constants;

/**
 * @author qinzhu
 * @since 2020/12/18
 */
public interface WebsocketConstant {
    /**
     * 广播前缀
     */
    String BROADCAST_PREFIX = "/topic";

    /**
     * 点对点前缀
     */
    String P2P_PREFIX = "/queue";

    /**
     * 完整的点对点路径
     */
    String P2P_URL = P2P_PREFIX + "/sendUser";
}
