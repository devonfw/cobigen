package com.capgemini.cobigen.pluginmanager.utils;

import java.io.File;

import com.capgemini.cobigen.extension.IMerger;

public class TestMerger implements IMerger {

    @Override
    public String getType() {

        return "MergerType";
    }

    @Override
    public String merge(File base, String patch, String targetCharset) throws Exception {

        return "";
    }

}
