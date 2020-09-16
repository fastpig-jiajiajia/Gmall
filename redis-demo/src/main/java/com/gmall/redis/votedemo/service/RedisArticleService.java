package com.gmall.redis.votedemo.service;

import java.util.List;
import java.util.Map;

public interface RedisArticleService {
    public String postArticle(String title, String content, String link, String userId);

    public void articleVote(String userId, String articleId);

    public List<Map<String, String>> listArticles(int page, String order);

    Map<String, String> hgetAll(String key);

    String hget(String key, String field);
}
