package com.capgemini.cobigen.unittest.pluginmanager.utils;

import java.io.File;

import com.capgemini.cobigen.api.extension.Merger;

public class TestMerger implements Merger {

    @Override
    public String getType() {

        return "MergerType";
    }

    @Override
    public String merge(File base, String patch, String targetCharset) throws Exception {

        return "";
    }

}
