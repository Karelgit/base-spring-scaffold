package com.cloudpioneer.demo.service.impl;

import com.cloudpioneer.demo.base.annotation.UniqueCheck;
import com.cloudpioneer.demo.entity.User;
import com.cloudpioneer.demo.mapper.UserMapper;
import com.cloudpioneer.demo.service.UserService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.util.Collection;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author jiangyunjun
 * @since 2018-12-27
 */
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    @Override
    @UniqueCheck(type = User.class, field = {"name", "phone", "idCard"})
    public boolean save(User entity) {
        return super.save(entity);
    }

    @Override
    @UniqueCheck(type = User.class, field = {"name", "phone", "idCard"})
    public boolean saveBatch(Collection<User> entityList) {
        return false;
    }

    @Override
    @UniqueCheck(type = User.class, field = {"name", "phone", "idCard"})
    public boolean updateById(User entity) {
        return super.updateById(entity);
    }

}
