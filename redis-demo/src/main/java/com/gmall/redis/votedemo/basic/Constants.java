package com.gmall.redis.votedemo.basic;

import com.sun.xml.internal.bind.v2.model.core.ID;

/**
 * 常量类
 */
public class Constants {
    /**
     * 文章发布7天后失效，不能投票
     */
    public static final int ONE_WEEK_IN_SECONDS = 7 * 86400;

    /**
     * 获取一票后文章分值加400
     */
    public static final int VOTE_SCORE = 400;

    /**
     * 分页查询每页显示25条
     */
    public static final int ARTICLES_PER_PAGE = 25;

    public static final String VOTE_KEY = "vote:";
    public static final String ARTICLE_KEY = "article:";
    public static final String SCORE_KEY = "score:info";
    public static final String PUBLISH_TIME_KEY = "publish:time";
    public static final String USER_SCORE_KEY = "score";
    public static final String USER_TIME_KEY = "time";


    public static final String TITLE = "title";
    public static final String LINK = "link";
    public static final String USER_ID = "userId";
    public static final String TIME = "time";
    public static final String VOTES = "votes";
    public static final String CONTENT = "content";

    public static final String ARTICLE_ID = "articleId";



}
