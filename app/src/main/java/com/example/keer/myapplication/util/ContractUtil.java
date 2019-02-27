package com.example.keer.myapplication.util;


import android.app.Application;

import com.example.keer.myapplication.R;
import com.example.keer.myapplication.Solidity.Pig;

import org.web3j.protocol.Web3j;
import org.web3j.protocol.http.HttpService;
import org.web3j.tx.ClientTransactionManager;
import org.web3j.tx.TransactionManager;
import org.web3j.tx.gas.ContractGasProvider;
import org.web3j.tx.gas.DefaultGasProvider;


public class ContractUtil {
    //TODO 如何在java文件中使用string.xml中的值，解决之后更改下面方法

    /**
     * 引入java合约
     * @param web3j_url web3 的 IP：host
     * @param contract_address 合约地址
     * @param account_address 账户地址
     * @return 返回java合约
     */

    public Pig PigLoad(String web3j_url, String contract_address, String account_address){
        Web3j web3j=Web3j.build(new HttpService(web3j_url));
        TransactionManager clientTransactionManager=new ClientTransactionManager(web3j,account_address) ;
        ContractGasProvider contractGasProvider=new DefaultGasProvider();
        return  Pig.load(contract_address,web3j,clientTransactionManager,contractGasProvider.getGasPrice(),contractGasProvider.getGasLimit());
    }
}
