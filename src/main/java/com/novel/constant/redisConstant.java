package com.novel.constant;

public class redisConstant {
    /** ZSet：用户会话索引，member 是会话 ID，score 是创建时间戳。 *
     *
     */
    public static final String AI_USER_SESSIONS_KEY_PREFIX = "ai:user:sessions:";
    /** Hash：会话信息，当前保存 title 字段。 */
    public static final String AI_SESSION_INFO_KEY_PREFIX = "ai:session:info:";
    /** List：会话消息，每个元素是一条消息的 JSON。 */
    public static final String AI_SESSION_MESSAGES_KEY_PREFIX = "ai:session:messages:";
    public static final String AI_SESSION_TITLE_FIELD = "title";
}
