package com.gmall.redis.votedemo.service;

import java.util.List;
import java.util.Map;

public interface RedisArticleService {
    public String postArticle(String title, String content, String link, String userId);

    public Map<String, String> hgetAll(String key);

    public void articleVote(String userId, String articleId);

    public String hget(String key, String votes);

    public List<Map<String, String>> getArticles(int page, String order);
}
