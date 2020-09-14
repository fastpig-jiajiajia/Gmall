package com.gmall.redis.votedemo.basic;
/**
* 常量类
*/
public class Constants {
	public static final int ONE_WEEK_IN_SECONDS = 7 * 86400;  //文章发布7天后失效，不能投票
    public static final int VOTE_SCORE = 400;//获取一票后文章分值加400
    public static final int ARTICLES_PER_PAGE = 25; //分页查询每页显示25条
}
