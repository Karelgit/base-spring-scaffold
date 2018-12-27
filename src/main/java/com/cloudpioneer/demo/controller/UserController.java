package com.cloudpioneer.demo.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cloudpioneer.demo.base.entity.BaseController;
import com.cloudpioneer.demo.entity.User;
import com.cloudpioneer.demo.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;



/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author jiangyunjun
 * @since 2018-12-27
 */
@RestController
@RequestMapping("/user")
public class UserController extends BaseController<User> {

    @Autowired
    private UserService userService;

    @GetMapping("list")
    public Object list(Integer page,Integer pageSize,String phone){
        IPage<User> userPage = new Page<>(page, pageSize);
        QueryWrapper<User> userQueryWrapper = new QueryWrapper<>();
        if(!StringUtils.isEmpty(phone)){
            userQueryWrapper.like("phone",phone);
        }
        userService.page(userPage,userQueryWrapper);
        return userPage;
    }

}
