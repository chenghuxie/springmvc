package com.xch.controller;

import com.xch.annonation.ExtController;
import com.xch.annonation.ExtRequestMapping;

/**
 * @author xiech
 * @create 2020-01-20 10:19
 */
@ExtController
@ExtRequestMapping("/ext")
public class IndexController {

    @ExtRequestMapping("/index")
    public String index(){
        return "index";
    }
}
