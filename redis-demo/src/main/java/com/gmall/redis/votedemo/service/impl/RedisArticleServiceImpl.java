package com.gmall.redis.votedemo.service.impl;

import com.gmall.redis.votedemo.basic.Constants;
import com.gmall.redis.votedemo.service.RedisArticleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.TimeUnit;


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

    @Autowired
    private RedisTemplate redisTemplate;

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
        // 文章发表的 ID
        String incrId = String.valueOf(redisTemplate.opsForValue().increment(Constants.ARTICLE_ID, 1));
        long now = System.currentTimeMillis() / 1000;

        // 设置文章内容缓存
        String articleId = Constants.ARTICLE_KEY + incrId;  // 文章内容ID
        Map<String, String> articleMap = new HashMap<>();
        articleMap.put(Constants.TITLE, title);
        articleMap.put(Constants.LINK, link);
        articleMap.put(Constants.CONTENT, content);
        articleMap.put(Constants.VOTES, String.valueOf(1));
        articleMap.put(Constants.USER_ID, userId);
        articleMap.put(Constants.TIME, String.valueOf(now));
        redisTemplate.opsForHash().putAll(articleId, articleMap);

        // 存储已对该文章投过票的人，投票时间仅限一周
        String voteId = Constants.VOTE_KEY + articleId;  // 文章的投票ID
        redisTemplate.opsForSet().add(voteId, userId);
        redisTemplate.expire(voteId, Constants.ONE_WEEK_IN_SECONDS, TimeUnit.SECONDS);

        // 总的文章的得分排行
        redisTemplate.opsForZSet().add(Constants.SCORE_KEY, articleId, Constants.VOTE_SCORE);
        // 总的文章的发布时间排序
        redisTemplate.opsForZSet().add(Constants.PUBLISH_TIME_KEY, articleId, now);

        // 自己文章的得分排行
        redisTemplate.opsForZSet().add(userId + ":" + Constants.USER_SCORE_KEY, articleId, Constants.VOTE_SCORE);
        // 自己文章的发布时间排序
        redisTemplate.opsForZSet().add(userId + ":" + Constants.USER_SCORE_KEY, articleId, now);

        return articleId;
    }


    /**
     * 投票
     *
     * @param userId
     * @param articleId
     */
    @Override
    public void articleVote(String userId, String articleId) {
        //计算投票截止时间
        long cutoff = (System.currentTimeMillis() / 1000) - Constants.ONE_WEEK_IN_SECONDS;
        //检查是否还可以对文章进行投票,如果该文章的发布时间比截止时间小，则已过期，不能进行投票
        if (redisTemplate.opsForZSet().score(userId + ":" + Constants.USER_SCORE_KEY, articleId) < cutoff) {
            return;
        }
        // 每个人只允许投一次票
        if (redisTemplate.opsForSet().add(Constants.VOTE_KEY + articleId, userId) == 1) {
            redisTemplate.opsForZSet().incrementScore(Constants.SCORE_KEY, articleId, Constants.VOTE_SCORE); //分值加400
            redisTemplate.opsForHash().increment(articleId, Constants.VOTES, 1); //投票数加1
        }
    }


    /**
     * 文章列表查询（分页）
     * 可以根据投票数、发布时间进行查询
     *
     * @param page key
     * @return redis查询结果
     */
    @Override
    public List<Map<String, String>> listArticles(int page, String key) {
        int start = (page - 1) * Constants.ARTICLES_PER_PAGE;
        int end = start + Constants.ARTICLES_PER_PAGE - 1;
        //倒序查询出投票数最高的文章，zset有序集合，分值递减
        Set<String> ids = redisTemplate.opsForZSet().reverseRange(key, start, end);
        List<Map<String, String>> articles = new ArrayList<Map<String, String>>();
        for (String id : ids) {
            Map<String, String> articleData = redisTemplate.opsForHash().entries(id);
            articleData.put(Constants.ARTICLE_ID, id);
            articles.add(articleData);
        }

        return articles;
    }

    @Override
    public Map<String, String> hgetAll(String key) {
        return redisTemplate.opsForHash().entries(key);
    }

    @Override
    public String hget(String key, String field) {
        return String.valueOf(redisTemplate.opsForHash().get(key, field));
    }

}
