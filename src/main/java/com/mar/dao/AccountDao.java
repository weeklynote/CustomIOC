package com.mar.dao;

import com.mar.model.Account;

/**
 * @Author: 刘劲
 * @Date: 2020/4/7 23:06
 */
public interface AccountDao {
    Account queryAccountByCardNo(String cardNo) throws Exception;
    int updateAccountByCardNo(Account account) throws Exception;
}
