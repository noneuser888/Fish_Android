package com.wantime.wbangapp.utils

object JsScriptUtils {
    const val  exeJsScript="\"\"javascript:(function() {function getOffsetTop(el){\n" +
            "                     return el.offsetParent\n" +
            "                      ? el.offsetTop + getOffsetTop(el.offsetParent)\n" +
            "                      : el.offsetTop\n" +
            "                    };function getOffsetLeft(el){\n" +
            "                     return el.offsetParent\n" +
            "                      ? el.offsetLeft + getOffsetLeft(el.offsetParent)\n" +
            "                      : el.offsetLeft\n" +
            "                    };\n" +
            "                    var authItem=document.getElementsByClassName(\"auth_qrcode\")[0];  var imgSrc=authItem.getAttribute('src');authItem.setAttribute('src',imgSrc);\n" +
            "                    authItem.addEventListener('error',function(e){window.handler.imageLoadFailed();});var oLeft=getOffsetLeft(authItem);\n" +
            "                    var oTop=getOffsetTop(authItem);var iHeight=authItem.height;var iWidth=authItem.width;\n" +
            "                    window.handler.getContent(document.body.innerHTML);\n" +
            "                    window.handler.imageLayoutPosition(oLeft,oTop,iWidth,iHeight);})()\"\""

    const val showBodyJs="javascript:window.handler.parse(document.body.innerHTML);"
}