package com.cloudpioneer.demo.base.aop;


import com.cloudpioneer.demo.base.entity.FailureResult;
import com.cloudpioneer.demo.base.entity.Result;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletResponse;

/**
 * 全局结果处理，从切面中的方法获取返回数据，进行结果的统一包装
 *
 * @author jiangyunjun
 */
@Aspect
@Component
public class ResultDataAop {

    private final String userPoint = " execution(* com.cloudpioneer.demo.controller.**.*(..))";

    private final String basePoint = " execution(* com.cloudpioneer.demo.base.entity.BaseController.*(..))";

    private final String point = basePoint + " || " + userPoint;

    @Value("${com.cloudpioneer.changeWebCode}")
    private boolean changeWebCode;

    /**
     * 环绕通知中执行切面中的方法
     * 此处不进行异常处理，异常交由GlobalExceptionHandler统一处理
     *
     * @param proceedingJoinPoint 切面
     * @return 返回方法最后的结果
     * @throws Throwable 切面代理执行方法时可能会抛出异常，全局异常处理会将异常包装成一个Result返回
     */
    @Around(point)
    public Object doAroundAdvice(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {
        //调用执行目标方法
        Object result = proceedingJoinPoint.proceed();
        return processResult(result);
    }

    /**
     * 处理返回对象
     */
    private Result processResult(Object result) {
        Result finalResult;
        if (result instanceof FailureResult) {
            FailureResult failureResult = (FailureResult) result;
            finalResult = new Result(failureResult.getCode(), failureResult.getMessage(), null);
            if (changeWebCode) {

                ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
                if (requestAttributes != null) {
                    HttpServletResponse response = requestAttributes.getResponse();
                    if (response != null) {
                        response.setStatus(finalResult.getCode());
                    }
                }
            }
        } else {
            finalResult = new Result(0, "SUCCESS", result);
        }
        return finalResult;
    }
}