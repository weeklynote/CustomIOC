package com.mar;

/**
 * @Author: 刘劲
 * @Date: 2020/4/7 22:44
 */
public interface AccountService {
    void transfer(String fromCardNo, String toCardNo, int money) throws Exception;
}
