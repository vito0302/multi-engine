package com.baidu.unbiz.multiengine.dto;

/**
 * Created by wangchongjie on 16/4/7.
 */
public class RpcResult {

    private Object result;

    public static RpcResult newInstance() {
        return new RpcResult();
    }

    public Object getResult() {
        return result;
    }

    public RpcResult setResult(Object result) {
        this.result = result;
        return this;
    }
}