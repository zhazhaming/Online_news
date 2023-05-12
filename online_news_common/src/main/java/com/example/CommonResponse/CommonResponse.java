package com.example.CommonResponse;

import java.util.Map;

public class CommonResponse {
    // 状态码
    private Integer status;

    //响应信息
    private String msg;

    //是否成功
    private Boolean success;

    //响应数据
    private Object data;

    //返回成功
    public static CommonResponse ok(){
        return new CommonResponse ( ResponseEnum.SUCCESS );
    }

    //返回成功 带参数
    public static CommonResponse ok(Object data){
        return new CommonResponse ( data );
    }

    //返回错误
    public static CommonResponse error(){
        return new CommonResponse ( ResponseEnum.FAILED );
    }

    //带多条错误返回
    public static CommonResponse errorMap(Map map) {
        return new CommonResponse(ResponseEnum.FAILED, map);
    }

    // 带错误返回
    public static CommonResponse errorMsg(String msg) {
        return new CommonResponse (ResponseEnum.FAILED, msg);
    }

    //token错误
    public static CommonResponse errorTicket(){
        return new CommonResponse ( ResponseEnum.TICKET_INVALID );
    }

    //自定义错误
    public static CommonResponse errorCustom(ResponseEnum responseEnum){
        return new CommonResponse(responseEnum);
    }

    //异常错误
    public static CommonResponse exception(ResponseEnum responseStatus) {
        return new CommonResponse (responseStatus);
    }

    //传入一个枚举
    public CommonResponse(ResponseEnum Enum){
        this.status = Enum.status ();
        this.msg = Enum.msg ();
        this.success = Enum.success ();
    }

    //传入data数据
    public CommonResponse(Object data){
        this.status = ResponseEnum.SUCCESS.status ( );
        this.msg = ResponseEnum.SUCCESS.msg ( );
        this.success = ResponseEnum.SUCCESS.success ( );
        this.data = data;
    }
    //传入一个枚举和数据
    public CommonResponse(ResponseEnum Enum,Object data){
        this.status = Enum.status ();
        this.msg = Enum.msg ();
        this.success = Enum.success ();
        this.data = data;
    }

    //传入一个枚举和自定义消息
    public CommonResponse(ResponseEnum Enum,String msg){
        this.status = Enum.status ();
        this.msg = msg;
        this.success = Enum.success ();
    }

    //无参构造
    public CommonResponse(){}


    //get和set方法
    public void setStatus(Integer status) {
        this.status = status;
    }

    public Integer getStatus() {
        return status;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public Boolean getSuccess() {
        return success;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

//    public Integer getStatus() {
//        return status;
//    }
//
//    public void setStatus(Integer status) {
//        this.status = status;
//    }
//
//    public String getMsg() {
//        return msg;
//    }
//
//    public void setMsg(String msg) {
//        this.msg = msg;
//    }
//
//    public Boolean getSuccess() {
//        return success;
//    }
//
//    public void setSuccess(Boolean success) {
//        this.success = success;
//    }
//
//    public Object getData() {
//        return data;
//    }
//
//    public void setData(Object data) {
//        this.data = data;
//    }
}
