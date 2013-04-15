/**
 * AngularJs - app.js
 */
angular.module('SharedServices', [])
    .config(function ($httpProvider) {
        $httpProvider.responseInterceptors.push('myHttpInterceptor');
        var spinnerFunction = function (data, headersGetter) {
            // todo start the spinner here
            $('#loading').show();
            return data;
        };
        $httpProvider.defaults.transformRequest.push(spinnerFunction);
    })
    // register the interceptor as a service, intercepts ALL angular ajax http calls
    .factory('myHttpInterceptor', function ($q, $window) {
        return function (promise) {
            return promise.then(function (response) {
                // do something on success
                // todo hide the spinner
                $('#loading').hide();
                $('#error').hide();
                return response;

            }, function (response) {
                // do something on error
                // todo hide the spinner
                $('#error').show();
                $('#loading').hide();
                return $q.reject(response);
            });
        };
    });

angular.module('AnyAsTumblr', ['SharedServices'])
    .config(['$routeProvider', function($routeProvider) {
        $routeProvider.
            when('/index', { templateUrl: "assets/partial/tumblr.html", controller: TumblrController}).
            when('/tweets', {templateUrl: 'tweets', controller: TweetsController}).
            otherwise({redirectTo: '/index'});
    }]);

