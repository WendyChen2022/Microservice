package com.example.iam.base;

import com.example.common.entity.Result;
import com.example.common.entity.StatusCode;
import org.apache.http.auth.AuthenticationException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import javax.security.auth.message.AuthException;

@RestControllerAdvice
public class IamExceptionHandler {

//    @ExceptionHandler
//    public Result handleRuntimeException(RuntimeException exception){
//        return new Result<>(false, StatusCode.ERROR, "system business exception");
//    }

    @ExceptionHandler
    public Result handleAuthException(AuthenticationException exception){
        return new Result<>(false, StatusCode.ACCESSERROR, "system auth exception");
    }
}
