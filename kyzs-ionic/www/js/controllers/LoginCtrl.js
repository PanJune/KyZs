angular.module('kyzs')
.constant("SCHOOL_ID_LOGIN","http://localhost:8081/login/schoolIDLogin")
.constant("GET_SAFE_CODE_URL","http://localhost:8081/login/safecodeImage")
.constant('SECOND_LOGIN', "http://localhost:8081/login/secondLogin")
.controller('LoginCtrl', function($scope,$http,GET_SAFE_CODE_URL,
    SCHOOL_ID_LOGIN,SECOND_LOGIN,$ionicPopup,$ionicHistory){
    
    $scope.$on("$ionicView.beforeEnter", function () {
        $ionicHistory.clearCache();
        $ionicHistory.clearHistory();
        console.log("clear cache");
    });

    $scope.isSafecodeImageGet = false;

    $scope.user = {
        studentID : "",
        password : "",
        safecode : ""
    }

    $scope.getSafecodeImg = function(){
        var user = $scope.user;
        console.log(user.studentID);
        if (user.studentID === "") {
            showAlert("error","必须先填写学号");
        }
        else{
            var httpResponse = $http({
                method: 'POST',
                url: SCHOOL_ID_LOGIN,
                contentType: "application/json",
                data: {
                    studentID: user.studentID
                },
            });
            httpResponse.success(function (data) {
                console.log(data);
                if(data.status === 'success'){
                    $scope.safecodeImage = GET_SAFE_CODE_URL;
                    $scope.isSafecodeImageGet = true;
                }
            }).error(function (data) {
                showAlert("error",data.message);
            });
        }
    }

    $scope.login = function(){
        var user = $scope.user;
        if (user.studentID === "") {
            showAlert("error","必须先填写学号");
        }
        else if(user.password === ""){
            showAlert("error","密码不能为空");
        }
        else if(user.safecode === ""){
            showAlert("error","验证码不能为空");
        }
        else{
            //login
            var httpResponse = $http({
                method: 'POST',
                url: SECOND_LOGIN,
                contentType: "application/json",
                data: {
                    password: user.password,
                    safecode: user.safecode
                },
            });

            httpResponse.success(function (data) {
                console.log(data);
                if(data.status === 'success'){
                    showAlert("success","login succeed");
                }
            }).error(function (data) {
                showAlert("error",data.message);
            });
        }   
    }

     // An alert dialog
    var showAlert = function(title,message) {
        var alertPopup = $ionicPopup.alert({
            title: title,
            template: message
        });

        // alertPopup.then(function(res) {
        //     console.log('Thank you for not eating my delicious ice cream cone');
        // });
    };
});
