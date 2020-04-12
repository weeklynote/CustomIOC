package com.mar.impl;

import com.mar.AccountService;
import com.mar.dao.AccountDao;
import com.mar.model.Account;

/**
 * @Author: 刘劲
 * @Date: 2020/4/7 22:44
 */
public class AccountServiceImpl implements AccountService {

    private AccountDao accountDao;

    public void setAccountDao(AccountDao accountDao) {
        this.accountDao = accountDao;
    }

    @Override
    public void transfer(String fromCardNo, String toCardNo, int money) throws Exception {
        Account from = accountDao.queryAccountByCardNo(fromCardNo);
        Account to = accountDao.queryAccountByCardNo(toCardNo);
        from.setMoney(from.getMoney()-money);
        to.setMoney(to.getMoney()+money);
        accountDao.updateAccountByCardNo(to);
        int i = 1 / 0;
        accountDao.updateAccountByCardNo(from);
    }
}
