var module = angular.module( "net.traeumt.Angie", [] );

//module.directive('crud', function() {
//    return {
//        restrict: 'A',                  // restrict to elements
//        transclude: true,               // pass parent scope to elements within this directive
////        template: '<form><button data-ng-click="config.edit = !config.edit">Toggle</button><div data-ng-transclude></div></form>',  // just a wrapping <div>
//        scope: {                        // isolated scope
//        },
//        controller: function($scope) {
//            console.log("crud controller");
//            console.log($scope);
//            console.log(this);
//            this.data = {
//                id: 'myObject',
//                attributes: [
//                    {name:'firstname', value:'Dirk', type:'text'},
//                    {name:'lastname', value:'Schalge', type:'text'},
//                    {name:'address', value:'Vormholzer Ring 77', type:'text'},
//                    {name:'city', value:'Witten', type:'text'},
//                    {name:'dateOfBirth', value:'30.04.1987', type:'date'}
//                ]
//            };
//            this.config = {
//                edit: false
//            };
//
//            $scope.config = this.config;
//        },
//        link: function($scope, element, attrs) {
//            console.log("crud link");
//            console.log($scope);
//            console.log(this);
//            element.on('dblclick', function(event) {
//                event.preventDefault();
//                $scope.config.edit = !$scope.config.edit;
//            });
//        }
//    };
//});

module.run(['sessionService', function(sessionService) {
}]);

module.controller('loginController', ['$scope', 'sessionService', function($scope, sessionService) {
    this.login = function(username, password) {
        sessionService.login(username, password);
    }
}]);

module.service('sessionService', ['$http', function($http) {
    this.login = function(username, password) {
        $http.post('/couchdb/_session', {name: username, password: password}).then(function(result) {
            if ( result.ok == true ) {
                console.login("Login successful!")
            } else {
                this.loginError("Username or password wrong")
            }
        }, this.loginError);
    }

    this.loginError = function(error) {
        console.log(error);
    };
}]);

module.service('schemaService', function() {
    // service constructor function

});

module.directive('crudList', function() {
    return {
        restrict: 'A',
        link: {
            pre: function($scope, element, attrs) {
            }
        }
    };
});

module.directive('crudObject', function() {
    return {
        restrict: 'A',
        link: {
            pre: function($scope, element, attrs) {
                var id = attrs.crudObject;

                // CRUD INIT
                if ( _.isUndefined($scope.crud) ) {
                    $scope.crud = {};
                }

                // CONFIGURATION
                var config = {
                    edit: false
                };

                // SCHEMA INFORMATION
                var schema = {
                    id: {type:'text', hidden: true, required: true},
                    firstname: {type:'text'},
                    lastname: {type:'text', required: true},
                    address: {type:'text'},
                    city: {type:'text'},
                    dateOfBirth: {type:'date'},
                    gender: {type:'text', select: true, options:['male','female']},
                    hobby: {type:'text', select: true, multi: true, options:['Modellbau','Fußball','Brettspiele']},
                }

                // INSTANCE OF SCHEMA
                var data;
                if ( attrs.add == 'true' ) {
                    data = {};
                    _.each(schema.keys(), function(key) {
                        data[key] = undefined;
                    });
                } else if (id in $scope.crud && 'data' in $scope.crud[id]) {
                    data = $scope.crud[id].data;
                } else {
                    data = {
                        id: {value: id},
                        firstname: {value: 'Dirk'},
                        lastname: {value: 'Schalge'},
                        address: {value: 'Vormholzer Ring 77'},
                        city: {value: 'Witten'},
                        dateOfBirth: {value: '30.04.1987'},
                    }
                }

                // DEFINE THIS OBJECT
                var object = {
                    config: config,
                    schema: schema,
                    data: data,

                    // FUNCTIONS
                    startEditHooks: [],
                    startEdit: function() {
                        config.edit = true;

                        // run all start hooks
                        _.each(object.startEditHooks, function(hook) {
                            hook();
                        });
                    },

                    abortEditHooks: [],
                    abortEdit: function() {
                        config.edit = false;

                        // run all abort hooks
                        _.each(object.abortEditHooks, function(hook) {
                            hook();
                        });
                    },

                    saveEditHooks: [],
                    saveEdit: function() {
                        config.edit = false;

                        // run all save hooks
                        _.each(object.saveEditHooks, function(hook) {
                            hook();
                        });

                        // send updated object to backend
                    },

                    remove: function() {

                    },
                }

                $scope.crud[id] = object;
            }
        }
    };
});

module.directive('viewedit', function() {
    return {
        restrict: 'E',
//        require: '^crud',
        template: '<div><div ng-show="object.config.edit"><input type="text" data-ng-model="data.__editvalue" /></div><div ng-hide="object.config.edit">{{data.value}}</div></div>',
        scope: {
            object: '='
        },
        link: function($scope, element, attrs) {
            // map $scope.data to our part of the crud object
            $scope.data = $scope.object.data[attrs.name];

            // create shadow copy of actual value for editing
            var startEdit = function() {
                $scope.data.__editvalue = $scope.data.value;
            }
            // make the shadow copy the actual value
            var saveEdit = function() {
                $scope.data.value = $scope.data.__editvalue;
            }

            // register hooks
            $scope.object.startEditHooks.push(startEdit);
            $scope.object.saveEditHooks.push(saveEdit);
        }
    };
});
