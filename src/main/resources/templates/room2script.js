var sessionArg;
var cache_username;
var glancePageTimer;

$(document).ready(function() {
    $(".device_page").hide();
    $(".single_device_page").hide();

    var cookiesString = document.cookie;
    var sessionCookie = cookiesString.split(";");
    sessionArg = sessionCookie[0].split("=")[1];
    var getNameUri = "/user/getname";
    $.post(getNameUri, function(data){
            if (data === ""){
                window.location.replace("homepage");
            }
            cache_username = data;
            $(".top_username").text(cache_username);
        }
    );
    getGlanceDeviceList();
    glancePageTimer = setInterval(getGlanceDeviceList,5000);

    $(".glance_device_title").click(function(){
        enterDevicePage();
    });
    $(".left_device_list_button").click(function(){
        enterDevicePage();
    });
    $('.glance_device_content').on('click','.glance_device_item',function(){
        var deviceId = $(this).children(".glance_device_hide_id").text();
        enterSingleDevicePage(deviceId);
    });
    $(".device_item").click(function(){
        var deviceId = $(this).children(".device_item_id").text();
        enterSingleDevicePage(deviceId);
    });
});
function getGlanceDeviceList(){
    var getDeviceUri = "/device/user/all";
    $.post(getDeviceUri,
        {
            token: sessionArg
        }
        ,function(data){
        var tmpHtml = "";
        for (var index in data){
            var deviceItem = data[index];
            var itemHtml = "<div class='glance_device_item'>" + deviceItem["type"] + "<div class='glance_device_color'></div>" +
                "<div class='glance_device_status'>" + deviceItem["poweredOn"] + "</div>" +
                "<div class='glance_device_hide_id'>" + deviceItem["deviceId"] + "</div></div>";
            tmpHtml = tmpHtml + itemHtml;
        }
        $(".glance_device_content").html(tmpHtml);
        }
    );
}
function loadSingleDevicePage(deviceId){
    var uri = "/device/get";
    $.post(uri,
        {
            token: sessionArg,
            device: deviceId
        }
        ,function(data){

        }
    );
}
function enterDevicePage(){
    clearInterval(glancePageTimer);
    $(".glance_page").hide();
    $(".single_device_page").hide();
    $(".device_page").fadeIn("fast");
}
function enterSingleDevicePage(deviceId){
    // clearInterval(glancePageTimer);
    $(".glance_page").hide();
    $(".device_page").hide();
    $(".single_device_page").fadeIn("fast");
    loadSingleDevicePage(deviceId);
}
