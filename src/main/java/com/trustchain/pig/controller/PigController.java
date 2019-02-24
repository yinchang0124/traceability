package com.trustchain.pig.controller;

import com.trustchain.pig.util.ContractUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

@Controller
public class PigController {
    @Autowired
    ContractUtil contractUtil;
}
