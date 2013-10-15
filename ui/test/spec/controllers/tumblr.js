'use strict';

describe('Controller: TumblrCtrl', function () {

  // load the controller's module
  beforeEach(module('uiApp'));

  var TumblrCtrl,
    scope;

  // Initialize the controller and a mock scope
  beforeEach(inject(function ($controller, $rootScope) {
    scope = $rootScope.$new();
    TumblrCtrl = $controller('TumblrCtrl', {
      $scope: scope
    });
  }));

  it('should attach a list of awesomeThings to the scope', function () {
    expect(scope.awesomeThings.length).toBe(3);
  });
});
