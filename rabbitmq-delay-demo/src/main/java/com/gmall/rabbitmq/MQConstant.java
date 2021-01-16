package com.gmall.rabbitmq;

/**
 * 类说明：
 */
public class MQConstant {

    public final static String QUEUE_HELLO = "sb.hello";
    public final static String QUEUE_USER = "sb.user";


    public final static String QUEUE_TOPIC_EMAIL = "sb.info.email";
    public final static String QUEUE_TOPIC_USER = "sb.info.user";
    public final static String RK_EMAIL = "sb.info.email";
    public final static String RK_USER = "sb.info.user";

    public final static String EXCHANGE_TOPIC = "sb.exchange";
    public final static String EXCHANGE_FANOUT = "sb.fanout.exchange";

    /**
     * 死信队列配置
     */
    public final static String DLX_QUEUE = "GMALL-DLX-QUEUE";
    public final static String DLX_EXCHANGE_TOPIC = "GMALL-DLX-EXCHANGE";
    public final static String DLX_EXCHANGE_KEY = "gmall.dlx";

    /**
     * 延迟队列配置
     */
    public final static String TTL_QUEUE = "GMALL-TTL-QUEUE";
    public final static String TTL_EXCHANGE_TOPIC = "GMALL-TTL-EXCHANGE";
    public final static String TTL_EXCHANGE_KEY = "gmall.ttl";

    /**
     * 延迟插件队列配置
     */
    public final static String DELAY_PLUGIN_QUEUE = "GMALL_PAYMENT_DELAY_QUEUE";
    public final static String DELAY_PLUGIN_EXCHANGE_TOPIC = "GMALL_PAYMENT_DELAY_EXCHANGE";
    public final static String DELAY_PLUGIN_EXCHANGE_KEY = "gmall.delay";

    /**
     * 分布式事务队列配置
     */
    public final static String TRANSACTION_QUEUE = "GMALL_DISTRIBUTE_TRANSACTION_QUEUE";
    public final static String TRANSACTION_EXCHANGE_TOPIC = "GMALL_DISTRIBUTE_TRANSACTION_EXCHANGE";
    public final static String TRANSACTION_EXCHANGE_KEY = "gmall.distribute.transaction";


}
