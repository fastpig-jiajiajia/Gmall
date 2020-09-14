package com.gmall.redis.votedemo.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.Resource;

import com.gmall.redis.votedemo.basic.Constants;
import com.gmall.redis.votedemo.service.RedisArticleService;
import com.gmall.redis.votedemo.utils.JedisUtils;
import org.springframework.stereotype.Service;


/**
 * 文章发布使用redis技术
 * <p>
 * 用户可以发表文章(Hash),发表时默认给自己的文章投了一票（set/String + String）
 * 用户在查看网站时可以按评分进行排列查看（zset）
 * 用户也可以按照文章发布时间进行排序(zset)
 * 为节约内存，一篇文章发表后，7天内可以投票,7天过后就不能再投票了(String)
 * 为防止同一用户多次投票，用户只能给一篇文章投一次票(set)
 */
@Service
public class RedisArticleServiceImpl implements RedisArticleService {

    @Resource
    private JedisUtils jedis;

    /**
     * 文章发布
     *
     * @param title
     * @param content
     * @param link
     * @param userId
     * @return
     */
    @Override
    public String postArticle(String title, String content, String link, String userId) {

        //article:001
        String articleId = String.valueOf(jedis.incr("article:")); // articleId=1


        //投票键： voted:
        String voted = "voted:" + articleId;

        jedis.sadd(voted, userId);
        jedis.expire(voted, Constants.ONE_WEEK_IN_SECONDS);

        long now = System.currentTimeMillis() / 1000;

        String article = "article:" + articleId;

        HashMap<String, String> articleData = new HashMap<String, String>();
        articleData.put("title", title);
        articleData.put("link", link);
        articleData.put("user", userId);
        articleData.put("now", String.valueOf(now));
        articleData.put("votes", "1");

        jedis.hmset(article, articleData);
        jedis.zadd("score:info", now + Constants.VOTE_SCORE, article);
        jedis.zadd("time:", now, article);

        return articleId;
    }


    /**
     * 文章投票
     *
     * @param 用户ID 文章ID（article:001）  //001
     */
    @Override
    public void articleVote(String userId, String article) {


        //计算投票截止时间
        long cutoff = (System.currentTimeMillis() / 1000) - Constants.ONE_WEEK_IN_SECONDS;
        //检查是否还可以对文章进行投票,如果该文章的发布时间比截止时间小，则已过期，不能进行投票
        if (jedis.zscore("time:", article) < cutoff) {
            return;
        }
        //获取文章主键id
        String articleId = article.substring(article.indexOf(':') + 1); ////article:1    1

        if (jedis.sadd("voted:" + articleId, userId) == 1) {
            jedis.zincrby("score:info", Constants.VOTE_SCORE, article);//分值加400
            jedis.hincrBy(article, "votes", 1l);//投票数加1
        }
    }


    /**
     * 文章列表查询（分页）
     *
     * @param page key
     * @return redis查询结果
     */
    @Override
    public List<Map<String, String>> getArticles(int page, String key) {
        int start = (page - 1) * Constants.ARTICLES_PER_PAGE;
        int end = start + Constants.ARTICLES_PER_PAGE - 1;
        //倒序查询出投票数最高的文章，zset有序集合，分值递减
        Set<String> ids = jedis.zrevrange(key, start, end);
        List<Map<String, String>> articles = new ArrayList<Map<String, String>>();
        for (String id : ids) {
            Map<String, String> articleData = jedis.hgetAll(id);
            articleData.put("id", id);
            articles.add(articleData);
        }

        return articles;
    }


    @Override
    public String hget(String key, String feild) {
        return jedis.hget(key, feild);
    }

    @Override
    public Map<String, String> hgetAll(String key) {
        return jedis.hgetAll(key);
    }

} 
