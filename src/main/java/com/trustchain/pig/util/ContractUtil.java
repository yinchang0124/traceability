package com.trustchain.pig.util;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.http.HttpService;
import org.web3j.tx.ClientTransactionManager;
import org.web3j.tx.TransactionManager;
import org.web3j.tx.gas.ContractGasProvider;
import org.web3j.tx.gas.DefaultGasProvider;

@Component
public class ContractUtil {

    @Value("${web3_url}")
    private String web3_url;
    @Value("${PigCore_address}")
    private  String PigCore_address;
    @Value("${account_address}")
    private String account_address;



    public Pig PigCoreLoad(){
        Web3j web3j=Web3j.build(new HttpService(web3_url));
        TransactionManager clientTransactionManager=new ClientTransactionManager(web3j,account_address) ;
        ContractGasProvider contractGasProvider=new DefaultGasProvider();
        return  Pig.load(PigCore_address,web3j,clientTransactionManager,contractGasProvider.getGasPrice(),contractGasProvider.getGasLimit());
    }

}
