new function() {
    var app = angular.module('tscApp', [])

    app.controller('mainCtrl', function($scope) {
        
    })

    app.controller('staticUpdateCtrl', function($scope, $http, $timeout) {
        $scope.update = function(url) {
            $scope.clear()
            $scope.isLoading = true


            // $timeout(function() {
            //     data = {
            //         code: 0,
            //         message: 'lala<br>lala'
            //     }
            $http.post(url).success(function(data) {
                if (data.code <= 0) {
                    $scope.isError = true
                    $scope.updateResultText = '更新失败！'
                }

                if (data.code > 0) {
                    $scope.isSuccess = true
                    $scope.updateResultText = '更新成功！'
                }

                $scope.message = data.message

            }, 2000)['finally'](function() {
                $scope.isLoading = false
            })
        }

        $scope.clear = function() {
            $scope.isError = false
            $scope.isSuccess = false
            $scope.isLoading = false
            $scope.message = ''
            $scope.updateResultText = ''
        }

        $scope.clear()
    })

    app.directive('ngBindHtmlUnsafe', function() {
        return function($scope, $element, $attrs) {
            $scope.$watch($attrs.ngBindHtmlUnsafe, function(nval) {
                $element.html(nval)
            })
        }
    })
}