package com.cloudpioneer.demo.base.exception;

import com.cloudpioneer.demo.base.entity.FailureResult;
import com.cloudpioneer.demo.base.entity.Result;
import com.cloudpioneer.demo.base.entity.ValidationError;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;

/**
 * 全局异常处理
 *
 * @author jiangyunjun
 */
@ControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger LOG = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @Value("${com.cloudpioneer.changeWebCode}")
    private boolean changeWebCode;

    /**
     * 业务异常处理
     *
     * @param e exception
     * @return result
     */
    @ResponseBody
    @ExceptionHandler(GlobalException.class)
    public Result handle(GlobalException e) {
        String message = null;
        try {
            message = e.getCause().getMessage();
        } catch (Exception except) {
            //e.getCause()可能获得空对象
        }
        Result result = new Result(e.getCode(), e.getMessage(), message);
        changeWebCode(result.getCode());
        return result;
    }

    /**
     * 校验失败异常处理
     *
     * @param e exception
     * @return result
     */
    @ResponseBody
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Result handle(MethodArgumentNotValidException e) {
        BindingResult bindingResult = e.getBindingResult();
        List<FieldError> fieldErrors = bindingResult.getFieldErrors();
        List<ValidationError> validationErrorList = new ArrayList<>(fieldErrors.size());
        for (FieldError fieldError : fieldErrors) {
            ValidationError validationError = new ValidationError(fieldError.getField(), fieldError.getRejectedValue(), fieldError.getDefaultMessage());
            validationErrorList.add(validationError);
        }
        Result result = new Result(FailureResult.valueOf("VALIDATION_FAILED"), validationErrorList);
        changeWebCode(result.getCode());
        return result;
    }

    /**
     * 系统异常处理
     *
     * @param e exception
     * @return result
     */
    @ResponseBody
    @ExceptionHandler(Exception.class)
    public Result handle(Exception e) {
        //打印堆栈
        e.printStackTrace();

        LOG.error(e.toString());
        FailureResult systemException = FailureResult.valueOf("SYSTEM_EXCEPTION");
        changeWebCode(systemException.getCode());
        return new Result(systemException, e.getLocalizedMessage());
    }

    private void changeWebCode(int code) {
        if (changeWebCode) {
            ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            if (requestAttributes != null) {
                HttpServletResponse response = requestAttributes.getResponse();
                if (response != null) {
                    response.setStatus(code);
                }
            }
        }
    }

}
