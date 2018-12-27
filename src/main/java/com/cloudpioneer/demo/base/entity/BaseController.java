package com.cloudpioneer.demo.base.entity;

import com.baomidou.mybatisplus.extension.service.IService;
import com.cloudpioneer.demo.base.exception.GlobalException;
import com.cloudpioneer.demo.base.validator.Insert;
import com.cloudpioneer.demo.base.validator.Update;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

/**
 * controller基类
 *
 * @author jiangyunjun
 */
public class BaseController<T> {

    @Autowired
    private IService<T> iService;

    @ResponseBody
    @PostMapping("/save")
    public Object save(@RequestBody @Validated(Insert.class) T entity){
        boolean result = iService.save(entity);
        return returnResult(result);
    }

    @ResponseBody
    @PostMapping("/saveBatch")
    public Object saveBatch(@RequestBody @Validated(Insert.class) ValidList<T> entitys){
        boolean result = iService.saveBatch(entitys);
        return returnResult(result);
    }

    @ResponseBody
    @PostMapping("/updateById")
    public Object updateById(@RequestBody @Validated(Update.class) T entity){
        boolean result = iService.updateById(entity);
        return returnResult(result);
    }

    @ResponseBody
    @PostMapping("/removeById")
    public Object removeById(@RequestBody String id){
        validEmpty(id);
        boolean result = iService.removeById(id);
        return returnResult(result);
    }

    @ResponseBody
    @PostMapping("/removeByIds")
    public Object removeByIds(@RequestBody List<String> ids){
        validEmpty(ids);
        boolean result = iService.removeByIds(ids);
        return returnResult(result);
    }

    @ResponseBody
    @GetMapping("/getById")
    public Object getById(String id){
        validEmpty(id);
        return iService.getById(id);
    }

    private Object returnResult(boolean success){
        if(success){
            return null;
        }else{
            return FailureResult.valueOf("FAILURE");
        }
    }

    private boolean stringIsEmpty(String str){
        return str == null || "".equals(str);
    }

    public void validEmpty(String str){
        if(stringIsEmpty(str)){
            throw new GlobalException(FailureResult.valueOf("PARAM_NULL"));
        }
    }

    public void validEmpty(List<String> stringList){
        if (stringList == null || stringList.size() < 1){
            throw new GlobalException(FailureResult.valueOf("PARAM_NULL"));
        }else{
            for (String str:stringList) {
                validEmpty(str);
            }
        }
    }

}
