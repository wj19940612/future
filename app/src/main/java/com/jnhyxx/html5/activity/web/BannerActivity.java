package com.jnhyxx.html5.activity.web;

import android.os.Bundle;
import android.support.annotation.Nullable;

import com.jnhyxx.html5.activity.WebViewActivity;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

/**
 * Created by ${wangJie} on 2016/11/1.
 */

public class BannerActivity extends WebViewActivity {
    public static final String INFO_HTML_META = "<meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0, maximum-scale=1.0, minimum-scale=1.0, user-scalable=no\">";


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        String content = INFO_HTML_META + "<body>" + mPureHtml + "</body>";

        getWebView().loadDataWithBaseURL(null, getHtmlContent(content), "text/html", "utf-8", null);
    }

    @Override
    protected boolean isNotNeedNetTitle() {
        return true;
    }


    public static String getHtmlContent(String html) {
//
        Document doc_Dis = Jsoup.parse(html);
        Elements ele_Img = doc_Dis.getElementsByTag("img");
        if (ele_Img.size() != 0) {
            for (Element e_Img : ele_Img) {
                e_Img.attr("style", "max-width:100%;height:auto");
            }
        }
        return doc_Dis.toString();
    }
}
