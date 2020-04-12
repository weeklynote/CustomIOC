package com.mar.impl;

import com.mar.annotation.MyAutoWired;
import com.mar.annotation.MyService;
import com.mar.annotation.MyTransactional;
import com.mar.dao.AccountDao;
import com.mar.model.Account;

/**
 * @Author: 刘劲
 * @Date: 2020/4/12 16:04
 */
@MyService
@MyTransactional
public class AnnotationServiceImpl {

    @MyAutoWired
    private AccountDao accountDao;

    public void updateAccountByCardNo(String fromNo, String toNo, int money) throws Exception {
        final Account from = accountDao.queryAccountByCardNo(fromNo);
        from.minusMoney(money);
        final Account to = accountDao.queryAccountByCardNo(toNo);
        to.addMoney(money);
        accountDao.updateAccountByCardNo(from);
        int i = 1 / 0;
        accountDao.updateAccountByCardNo(to);
    }
}
