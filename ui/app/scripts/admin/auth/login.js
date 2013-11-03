'use strict';

angular.module('admin.auth', [])
    .config(['$routeProvider', function ($routeProvider) {
        $routeProvider
            .when('/admin/login', {
                templateUrl: 'scripts/admin/auth/login.tpl.html',
                controller: 'LoginCtrl',
                access: "admin",
                login: true
            })
    }])
    .factory('secureResource', function($resource, Auth) {
        return function(url, params, othersMethods) {
            var headers = { 'Authorization': Auth.getToken(), 'Content-Type': 'application/json' };

            var addHeadersToOthersMethods = function () {
                var keys = Object.keys(othersMethods || []);
                for (var i = 0; i < keys.length; i++) {
                    angular.extend(othersMethods[keys[i]], { params: params, headers: headers });
                }
            }
            addHeadersToOthersMethods();

            var defaultMethods = {
                get: { method: 'GET', params: params, headers: headers },
                save: { method: 'POST', params: params, headers: headers },
                query: { method: 'GET', isArray: true, params: params, headers: headers },
                update: { method: 'PUT', params: params, headers: headers },
                remove: { method: 'DELETE', data: null, params: params, headers: headers },
                delete: { method: 'DELETE',  data: null, params: params, headers: headers }
            };
            var allMethods = angular.extend(defaultMethods, othersMethods);

            return $resource(url, {}, allMethods);
        };
    })
    .service('Auth', function ($cookieStore, $http) {
        var token = $cookieStore.get("user") || "";

        var storeToken = function (_token) {
            token = _token;
            $cookieStore.put("user", _token);
        };

        var removeToken = function () {
            $cookieStore.remove("user");
            token = "";
        }

        return {
            getToken: function () {
                return token;
            },
            isLoggedIn: function () {
                return token.length > 0;
            },
            login: function (user, success, error) {
                $http.post('/tumblr/administration/login', user).success(function (data) {
                    storeToken(data);
                    success(data);
                }).error(error);
            },
            logout: function (success) {
                removeToken();
                success();
            }
        };

    })
    .controller('LoginCtrl', function ($scope, $location, Auth) {
        $scope.user = { name: "", password: "" };
        $scope.error = "";

        $scope.login = function () {

            Auth.login($scope.user, function (data) {
                    $location.path("/admin/sites");
                },
                function (error) {
                    $scope.error = "Invalid username or password.";
                })

        }
    })
;
