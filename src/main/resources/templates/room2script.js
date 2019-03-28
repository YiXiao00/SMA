var sessionArg;
var cache_username;
var glancePageTimer;

$(document).ready(function() {
    $(".device_page").hide();

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
                "<div class='glance_device_status'>" + deviceItem["poweredOn"] + "</div>" + "</div>";
            tmpHtml = tmpHtml + itemHtml;
        }
        $(".glance_device_content").html(tmpHtml);

        }
    );
}
function enterDevicePage(){
    clearInterval(glancePageTimer);
    $(".glance_page").hide();
    $(".device_page").fadeIn("fast");
}
