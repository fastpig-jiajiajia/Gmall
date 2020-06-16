package com.gmall.mongodbdemo.service;

import com.gmall.mongodbdemo.entity.User;
import com.gmall.mongodbdemo.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private MongoTemplate mongoTemplate;

    public List<User> selectAllUser() {
        return userMapper.selectAllUser();
    }

    public User selectUserOne(int id) {
        return userMapper.selectUserOne(id);
    }

    /**
     * 保存对象到 MongoDB
     * @param userList
     * @return
     */
    public void insertUser2Mongo(List<User> userList){
        for (User user : userList){
            mongoTemplate.save(user);
        }
    }

    /**
     * 更新
     * @param user
     * @return
     */
    public long updateUser2MongoDB(User user){
        Query query=new Query(Criteria.where("id").is(user.getId()));
        Update update= new Update().set("username", user.getUserName());
        //更新查询返回结果集的第一条
        return mongoTemplate.updateFirst(query, update, User.class).getModifiedCount();
        //更新查询返回结果集的所有
        // mongoTemplate.updateMulti(query, update, TestEntity.class);
    }


    /**
     * 查询单个
     * @param user
     * @return
     */
    public User selectUserFromMongoDB(User user){
        Query query=new Query(Criteria.where("id").is(user.getId()));
        User user1 =  mongoTemplate.findOne(query , User.class);
        return user1;
    }

    /**
     * 查询所有
     * @param id
     * @return
     */
    public List<User> selectAllUserFromMongoDB(int id){
        Query query=new Query(Criteria.where("id").gt(id));
        List<User> userList =  mongoTemplate.find(query, User.class);
        return userList;
    }

    /**
     * 删除
     */
    public long deleteUserFromMongoDB(User user){
        Query query=new Query(Criteria.where("id").is(user.getId()));
        return mongoTemplate.remove(query, User.class).getDeletedCount();
    }



}
