package com.example.CommonException;

import com.example.CommonResponse.CommonResponse;
import com.example.CommonResponse.ResponseEnum;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

@ControllerAdvice
public class CommonExceptionHandler {

    @ResponseBody
    @ExceptionHandler(MyCustomException.class)
    public CommonResponse returnMyException(MyCustomException e){
        e.printStackTrace ();
        return CommonResponse.exception (e.getResponseEnum ());
    }

    @ExceptionHandler(MaxUploadSizeExceededException.class)
    @ResponseBody
    public CommonResponse returnMaxUploadSizeExceededException(MaxUploadSizeExceededException e) {
        return CommonResponse.errorCustom(ResponseEnum.FILE_MAX_SIZE_ERROR);
    }
}
