package io.codelirium.crawler.spider;

import java.util.List;

import static com.google.common.collect.Lists.newArrayList;


public class BaseTest {

    protected static final String DUMMY_INPUT = "FOO";

    protected static final String LINK_1 = "https://scalable.capital";
    protected static final String LINK_2 = "https://www.github.com";
    protected static final String LINK_3 = "https://www.bitbucket.com";

    protected static final String DUMMY_BASE_URL = LINK_1;

    protected static final String JS_LIB_1 = "jquery.js";
    protected static final String JS_LIB_2 = "vue.js";
    protected static final String JS_LIB_3 = "bootstrap.js";
    protected static final String JS_LIB_4 = "analytics.js";
    protected static final String JS_LIB_5 = "leetskinz.js";
    protected static final String JS_LIB_6 = "dummy1.js";
    protected static final String JS_LIB_7 = "dummy2.js";
    protected static final String JS_LIB_8 = "dummy3.js";


    protected List<String> getDummyGoogleSearchResults() {

        return newArrayList(LINK_1, LINK_2, LINK_3);

    }

    protected List<String> getLink1JSLibrariesWithPaths() {

        return newArrayList(JS_LIB_1, JS_LIB_2, JS_LIB_3, JS_LIB_4, JS_LIB_5, JS_LIB_6, JS_LIB_7, JS_LIB_8);

    }

    protected List<String> getLink2JSLibrariesWithPaths() {

        return newArrayList(JS_LIB_1, JS_LIB_2, JS_LIB_3, JS_LIB_4, JS_LIB_5);

    }

    protected List<String> getLink3JSLibrariesWithPaths() {

        return newArrayList(JS_LIB_1, JS_LIB_2, JS_LIB_3, JS_LIB_4, JS_LIB_5);

    }
}
