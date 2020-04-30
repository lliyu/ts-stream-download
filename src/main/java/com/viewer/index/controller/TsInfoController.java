package com.viewer.index.controller;

import com.viewer.index.entity.M3U8InfoDTO;
import com.viewer.index.entity.TsDto;
import com.viewer.index.response.BaseResponse;
import com.viewer.index.service.M3U8Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 收集m3u8信息
 */
@Controller
@RequestMapping("/")
public class TsInfoController {

    @Autowired
    private M3U8Service m3U8Service;

    @ResponseBody
    @RequestMapping(value = "/m3u8", method = RequestMethod.POST)
    public BaseResponse parse(String url, String name) throws IOException {
        M3U8InfoDTO m3U8InfoDTO = m3U8Service.parseM3U8(url, name);
        return new BaseResponse(200, "", m3U8InfoDTO);
    }

    @ResponseBody
    @RequestMapping(value = "/ts", method = RequestMethod.POST)
    public BaseResponse download(HttpServletResponse response, TsDto tsDto) {

        return new BaseResponse(200, "", null);
    }

}
