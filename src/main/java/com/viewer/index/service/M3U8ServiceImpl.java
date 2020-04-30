package com.viewer.index.service;

import com.viewer.index.entity.M3U8InfoDTO;
import com.viewer.index.entity.TsDto;
import com.viewer.index.parse.TimesCalculate;
import com.viewer.index.utils.DownLoadUtils;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
public class M3U8ServiceImpl implements M3U8Service {

    @Override
    public M3U8InfoDTO parseM3U8(String url, String name) throws IOException {
        DownLoadUtils.getM3U8File(url, name);
        M3U8InfoDTO infoDTO = TimesCalculate.calAllTimes(name);
        return infoDTO;
    }

    @Override
    public void download(TsDto tsDto) {
        
    }


}
