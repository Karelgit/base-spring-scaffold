package com.cloudpioneer.demo.base.aop;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.TableFieldInfo;
import com.baomidou.mybatisplus.core.metadata.TableInfo;
import com.baomidou.mybatisplus.core.toolkit.TableInfoHelper;
import com.cloudpioneer.demo.base.annotation.UniqueCheck;
import com.cloudpioneer.demo.base.entity.FailureResult;
import com.cloudpioneer.demo.base.exception.GlobalException;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;

/**
 * 注解UniqueCheck相关的处理类
 *
 * @author jiangyunjun
 */
@Aspect
@Component
public class UniqueCheckAop {

    private final BaseMapper baseMapper;

    @Autowired
    @SuppressWarnings("SpringJavaAutowiringInspection")
    public UniqueCheckAop(BaseMapper baseMapper) {
        this.baseMapper = baseMapper;
    }

    @Pointcut("@annotation(com.cloudpioneer.demo.base.annotation.UniqueCheck)")
    public void pointCut() {
    }

    @Before("pointCut() && @annotation(uniqueCheck)")
    public void doBefore(JoinPoint joinPoint, UniqueCheck uniqueCheck) throws InstantiationException, IllegalAccessException {
        //该方法的所有参数
        Object[] args = joinPoint.getArgs();
        //校验的对象类型
        Class<?> clazz = uniqueCheck.type();
        //需要检验的id
        TableInfo tableInfo = TableInfoHelper.getTableInfo(clazz);
        String id = tableInfo.getKeyProperty();
        //所有需要检验的字段
        String[] fields = uniqueCheck.field();

        List<Map<String, Object>> params = new ArrayList<>();

        //从参数中获取需要校验的对象
        for (Object object : args) {
            if (object instanceof Collection) {
                Collection c = (Collection) object;
                for (Object obj : c) {
                    if (clazz.isInstance(obj)) {
                        getParam(params, obj, clazz, fields, id);
                    }
                }
            } else if (clazz.isInstance(object)) {
                getParam(params, object, clazz, fields, id);
            }
        }

        //判断是否需要校验
        boolean needCheck = false;
        for (Map param:params) {
            if(param.size()>1){
                needCheck = true;
                break;
            }
        }

        if(needCheck){
            //获取实体属性对应的数据库列名
            Map<String, String> correlationMap = new HashMap<>(fields.length + 1);
            correlationMap.put(tableInfo.getKeyProperty(), tableInfo.getKeyColumn());

            List<TableFieldInfo> fieldList = tableInfo.getFieldList();
            List<String> needFieldList = Arrays.asList(fields);
            for (TableFieldInfo tableFieldInfo : fieldList) {
                if (needFieldList.contains(tableFieldInfo.getProperty())) {
                    correlationMap.put(tableFieldInfo.getProperty(), tableFieldInfo.getColumn());
                }
            }

            //通过params去查询相应的结果
            List<Map<String, Object>> result = select(params, tableInfo, correlationMap);

            //校验结果
            List<Map<String, Object>> noUnique = checkUnique(result, params, tableInfo);
            if (noUnique != null && noUnique.size() > 0) {
                throw new GlobalException(FailureResult.valueOf("UN_UNIQUE"), noUnique.toString());
            }
        }
    }

    /**
     * 根据字段名字获取该字段的get方法
     */
    private String getMethodName(String field) {
        return "get" + field.substring(0, 1).toUpperCase() + field.substring(1);
    }

    private List<Map<String, Object>> select(List<Map<String, Object>> params, TableInfo tableInfo, Map<String, String> correlationMap) throws IllegalAccessException, InstantiationException {

        //构造查询条件
        QueryWrapper<Object> queryWrapper = new QueryWrapper<>();
        queryWrapper.setEntity(tableInfo.getClazz().newInstance());

        //设置查询结果
        queryWrapper.select(correlationMap.values().toArray(new String[0]));

        //设置条件
        queryWrapper.nested(i -> {
            for (Map<String, Object> param : params) {
                for (String property : param.keySet()) {
                    if (!property.equals(tableInfo.getKeyProperty())) {
                        i.or().eq(correlationMap.get(property), param.get(property));
                    }
                }
            }
            return i;
        });

        return baseMapper.selectMaps(queryWrapper);
    }

    /**
     * 校验查询结果，如果发现重复，则返回
     */
    private List<Map<String, Object>> checkUnique(List<Map<String, Object>> results, List<Map<String, Object>> params, TableInfo tableInfo) {
        if (results.size() <= 0) {
            return null;
        } else {
            List<Map<String, Object>> noUnique = new ArrayList<>();

            for (Map<String, Object> param : params) {
                Map<String, Object> map = new HashMap<>(params.get(0).size() + 1);
                for (String keyP : param.keySet()) {

                    if (!keyP.equals(tableInfo.getKeyProperty())) {

                        for (Map<String, Object> result : results) {
                            for (String keyR : result.keySet()) {
                                if (result.get(keyR).equals(param.get(keyP)) && !result.get(tableInfo.getKeyColumn()).equals(param.get(tableInfo.getKeyProperty()))) {
                                    map.put(keyP, param.get(keyP));
                                }
                            }
                        }
                    }

                }
                if (map.size() > 0) {
                    if (!StringUtils.isEmpty(param.get(tableInfo.getKeyProperty()))) {
                        map.put(tableInfo.getKeyProperty(), param.get(tableInfo.getKeyProperty()));
                    }
                    noUnique.add(map);
                }
            }


            return noUnique;
        }
    }

    /**
     * 获取参数
     */
    private void getParam(List<Map<String, Object>> params, Object obj, Class<?> clazz, String[] fields, String id) {

        try {

            Map<String, Object> param = new HashMap<>(fields.length);

            //获取每一个字段的值,并将其装入map中
            for (String field : fields) {
                Method method = clazz.getMethod(getMethodName(field));
                Object value = method.invoke(obj);
                if (!StringUtils.isEmpty(value)) {
                    param.put(field, value);
                }
            }

            String idValue = (String) clazz.getMethod(getMethodName(id)).invoke(obj);
            param.put(id, idValue);

            params.add(param);
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }
    }
}
