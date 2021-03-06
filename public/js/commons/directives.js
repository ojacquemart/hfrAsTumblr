'use strict';

var directives = angular.module('commonDirectives', []);

directives
    .directive("loader", function () {
        return {
            restrict: "A",
            link: function ($scope, element) {
                $scope.$on("loader_show", function () {
                    return $(element).show();
                });
                $scope.$on("loader_hide", function () {
                    return $(element).hide();
                });
            }
        }
    });
