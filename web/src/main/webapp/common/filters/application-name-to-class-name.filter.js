(function() {
	'use strict';
	
	angular.module('pinpointApp').filter('applicationNameToClassName', function () {
	    return function (input) {
	        return input.replace(/\./gi,'_').replace(/\^/gi,'_').replace(/:/gi, '_');
	    };
	});
})();