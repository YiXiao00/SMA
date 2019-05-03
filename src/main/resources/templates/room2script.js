var sessionArg;
var cache_username;
var glancePageTimer;
var singleDeviceId;
var previous;

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
            var home_name = "Location: ".concat(cache_username).concat('\'s home');
            $(".single_device_block_title_home").text(home_name);


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
    $(".left_glance_button").click(function(){
        enterGlance();
    });
    $(".left_home_button").click(function(){
        enterHomes();
    });
    $(".left_navigation_icon").click(function(){
        toggleLeftMenu();
    });
    $(".top_navigation_icon").click(function(){
        toggleLeftMenu();
    });
    $(".log_out_btn").click(function(){
        logOutUser();
    });
    $(".single_device_back_btn").click(function(){
        returnFromSingle();
    });





    $('.glance_device_content').on('click','.glance_device_item',function(){
        var deviceId = $(this).children(".glance_device_hide_id").text();
        enterSingleDevicePage(deviceId);
    });
    $(".device_item").click(function(){
        var deviceId = $(this).children(".device_item_id").text();
        enterSingleDevicePage(deviceId);
    });

    $(".device_panel_btn_add").click(function(){
        openForm();
    });

    $(".deviceDelete").click(function(){
        deleteDevice();
    });

    $(".deviceToggle").click(function(){
        toggleDevice();
    });

    $(".addTask").click(function(){
        openTaskForm();
    });


    $(".addTask2").click(function(){
        openTask2Form();
    });

    $(".submitTask1").click(function(){
        submitTask1();
    });
    $(".submitTask2").click(function(){
        submitTask2();
    });




    detailedDeviceList();
});


function deleteDevice(){

    if (confirm("Are you sure you want to delete device " + $(".single_device_info_type").text()  + "?" )){
        var uri = "http://localhost:8090/device/delete"
        $.post(uri, {
            token:sessionArg,
            device:singleDeviceId
        }, function (data) {
            location.reload(true);


        });

    }

}

function toggleDevice(){
  var uri = "http://localhost:8090/device/toggle"
  $.post(uri, {
      token:sessionArg,
      device:singleDeviceId
  }, function (data) {
      location.reload(true);


  });
}
function openForm(){
    var unitWidth = $(".popupForm").css("width").replace(/px$/, "");
    if(unitWidth>50) {
        if($("#newDevice").val()!="Enter Device Name"){
            var uri = "http://localhost:8090/device/add"

            $.post(uri,
                {
                    token:sessionArg,
                    type:$("#newDevice").val()
                }, function(data) {
                    location.reload(true);
                });

        }
    }

    $('.transform').toggleClass('transform-active');
}
function detailedDeviceList(){
    var uri = "http://localhost:8090/device/user/all";
    $.post(uri,
        {
            token:sessionArg
        }, function (data) {

          var p = document.getElementsByClassName("device_item")[0];
            if(data==""){
                $(".device_item").hide();
                return;

            }
            var pChild = p.childNodes;
            pChild[1].textContent = data[0]["type"];
            pChild[3].textContent = data[0]["deviceId"];
            pChild[5].textContent = data[0]["poweredOn"];
            pChild[7].textContent = data[0]["FiS"];
            pChild[9].textContent = data[0]["FiSP"];
            pChild[11].textContent = data[0]["sID"];
            for (var i =1; i<data.length;i++){
                var y = p.cloneNode(true);
                var yChild = y.childNodes;
                yChild[1].textContent = data[i]["type"];
                yChild[3].textContent = data[i]["deviceId"];
                yChild[5].textContent = data[i]["poweredOn"]
                yChild[7].textContent = data[i]["FiS"];
                yChild[9].textContent = data[i]["FiSP"];
                yChild[11].textContent = data[i]["sID"];
                document.getElementsByClassName("device_content")[0].appendChild(y);


            }
        })


}
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
            if(deviceItem["poweredOn"]==true)
              deviceItem["poweredOn"] = "On"
            if(deviceItem["poweredOn"]==false)
              deviceItem["poweredOn"]= "Off"

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

      var taskCount = 0
      var uri = "http://localhost:8090/task/device/view";
      $.post(uri,
          {
              token:sessionArg,
              deviceId:deviceId
          }, function (data) {
              var p = document.getElementsByClassName("taskBlock")[0];
              taskCount = data.length
              var pChild = p.childNodes;

              if(data.length==0){
                  $(".taskBlock").hide();
              }else {
                  pChild[1].textContent = "Id: ".concat(data[0]["taskId"]);
                  pChild[3].textContent = "Type: ".concat(data[0]["type"]);
                  pChild[5].textContent = "Repeat: ".concat(data[0]["duration"]);
                  pChild[7].textContent = "When: ".concat(data[0]["calendar"]);

                  for (var i = 1; i < data.length; i++) {
                      var y = p.cloneNode(true);
                      var yChild = y.childNodes;
                      yChild[1].textContent = "Id: ".concat(data[i]["taskId"]);
                      yChild[3].textContent = "Type: ".concat(data[i]["type"]);
                      yChild[5].textContent = "Repeat: ".concat(data[i]["duration"]);
                      yChild[7].textContent = "When: ".concat(data[i]["calendar"]);
                      document.getElementsByClassName("taskBlock")[0].appendChild(y);


                  }
              }
          })

    var uri = "/device/get";
    setTimeout(
    $.post(uri,
        {
            token: sessionArg,
            device: deviceId
        }
        ,function(data){
           singleDeviceId = data["deviceId"];
           $(".single_device_title").text(data["type"]);
           $(".single_device_info_type").text(data["type"]);
           $(".single_device_info_id").text(data["deviceId"]);
           $(".single_device_info_status").text(data["poweredOn"]);
          $(".single_device_info_task_count").text(taskCount);
           if(data["FiS"]!=""){
            $(".single_device_info_fi_s").text(data["FiS"]);
          }
           if(data["FiSP"]!=""){
            $(".single_device_info_fi_sp").text(data["FiSP"]);
          }
           if(data["sID"]!=""){
            $(".single_device_info_samsung").text(data["sID"]);
          }


        }
    ), 500);


}
function enterDevicePage(){
    clearInterval(glancePageTimer);
    $(".glance_page").hide();
    $(".single_device_page").hide();
    $(".device_page").fadeIn("fast");
}

function enterGlance(){

    $(".device_page").hide();
    $(".single_device_page").hide();
    $(".glance_page").fadeIn("fast");


}

function logOutUser(){
    var uri = "/user/signout";
    $.post(uri,
        {
            token:sessionArg
        }, function (data) {
            if (data === "succeeded"){
                window.location.replace("http://localhost:8090/homepage");
            }
        })
}

function enterHomes(){

}
function enterSingleDevicePage(deviceId){
    // clearInterval(glancePageTimer);
    if($(".glance_page").is(":visible"))
        previous=1;
    if($(".device_page").is(":visible"))
        previous=2;
    $(".glance_page").hide();
    $(".device_page").hide();
    $(".single_device_page").fadeIn("fast");
    loadSingleDevicePage(deviceId);
}

function returnFromSingle(){
    if(previous==1){
        enterGlance();
    }
    if(previous==2){
        enterDevicePage();
    }
}

function toggleLeftMenu(){
    if($(".left_bar").is(":visible")) {
        $(".left_bar").hide();
        $(".default_panel").css({left: 0});
    }else{
        $(".left_bar").show();
        $(".default_panel").css({left: 250});

    }

}

function openTaskForm(){
    $(".single_device_binding_taskSlot").show();
    $(".addTask2Line").hide();
    $(".addTaskLine").fadeIn("fast");
}


function openTask2Form(){
    $(".single_device_binding_taskSlot").show();
    $(".addTaskLine").hide();
    $(".addTask2Line").fadeIn("fast");
}

function submitTask1(){

    var uri = "http://localhost:8090/task/add"
    $.post(uri,
        {
            type:$("#taskType").val(),
            in:$("#newTaskWhen").val(),
            duration:$("#newTaskRepeat").val(),
            device:singleDeviceId,
            token:sessionArg
        }, function(data){
            location.reload();


        }
    )

}

function submitTask2(){

    var uri = "http://localhost:8090/fiware/task/add"
    $.post(uri,
        {
            type:$("#task2type").val(),
            condition:$("#newTask2Syntax").val(),
            device:singleDeviceId,
            token:sessionArg
        }, function(data){
            location.reload();
        }
    )

}
