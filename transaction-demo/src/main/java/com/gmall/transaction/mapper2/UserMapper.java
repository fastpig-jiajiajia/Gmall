package com.gmall.transaction.mapper2;


import com.gmall.transaction.entity.User;

/**
 * 用户Mapper
 *
 * @author Zhou Huanghua
 * @date 2019/10/25 23:09
 */
public interface UserMapper {

    /**
     * 插入一条用户记录
     *
     * @param user 用户信息
     * @return 插入数量
     */
    int insert(User user);

    /**
     * 根据id删除用户记录
     *
     * @param id 用户id
     * @return 删除数量
     */
    int deleteById(Long id);
}
