package com.longlian.live.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Created by lh on 2016/6/7.
 */
@Controller
@RequestMapping("goto")
public class GoToPageController {
    @RequestMapping(value = "{func}/{page}" )
    public String gotoPage(HttpServletRequest request , HttpServletResponse response ,@PathVariable("func") String func , @PathVariable("page") String page) throws Exception {
        return "func/"+func+"/" + page;
    }
}
