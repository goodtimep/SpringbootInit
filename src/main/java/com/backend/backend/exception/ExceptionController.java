package com.backend.backend.exception;

import com.backend.backend.common.model.ResponseModel;
import com.backend.backend.jwt.JwtFilter;
import org.apache.shiro.ShiroException;
import org.apache.shiro.authz.UnauthorizedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @Author: goodtimp
 * @Date: 2019/10/23 15:45
 * @description :  异常拦截
 */
@ControllerAdvice
public class ExceptionController {

    private final Logger logger = LoggerFactory.getLogger(JwtFilter.class);

    // 捕捉shiro的异常
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    @ExceptionHandler(ShiroException.class)
    @ResponseBody
    public ResponseModel handle401(ShiroException e) {
        System.out.println(e);
        logger.error(e.getMessage());
        return ResponseModel.fail(401, e.getMessage());
    }

    // 捕捉shiro的异常
    @ExceptionHandler(TokenException.class)
    @ResponseBody
    public ResponseModel handle401(TokenException e) {
        System.out.println(e);
        logger.error(e.getMessage());
        return ResponseModel.fail(401, e.getMessage());
    }

    // 捕捉UnauthorizedException
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(UnauthorizedException.class)
    @ResponseBody
    public ResponseModel handle401() {
        System.out.println(401);
        logger.error("401，无权访问！");
        return ResponseModel.fail(401, "无权访问！");
    }

    @ExceptionHandler(Exception.class)
    @ResponseBody
    public ResponseModel globalException(HttpServletRequest request, Exception e) {
        System.out.println(e.getMessage());
        logger.error(e.getMessage());
        return ResponseModel.fail(getStatus(request).value(), e.getMessage());
    }

    private HttpStatus getStatus(HttpServletRequest request) {
        Integer statusCode = (Integer) request.getAttribute("javax.servlet.error.status_code");
        if (statusCode == null) {
            return HttpStatus.INTERNAL_SERVER_ERROR;
        }
        return HttpStatus.valueOf(statusCode);
    }
}