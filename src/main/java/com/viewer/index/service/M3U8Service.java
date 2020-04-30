package com.viewer.index.service;

import com.viewer.index.entity.M3U8InfoDTO;
import com.viewer.index.entity.TsDto;

import java.io.IOException;

public interface M3U8Service {

    public M3U8InfoDTO parseM3U8(String url, String name) throws IOException;

    public void download(TsDto tsDto);
}
