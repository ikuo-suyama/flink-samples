/**
 * @author v.lugovksy
 * created on 16.12.2015
 */
(function () {
  'use strict';

  angular.module('BlurAdmin.pages.dashboard')
      .controller('EonChartCtrl', EonChartCtrl);

  /** @ngInject */
  function EonChartCtrl($scope) {
    $scope.pubnub = new PubNub({
      publishKey: 'demo',
      subscribeKey: 'demo'
    });
    setInterval(function() {

      $scope.pubnub.publish({
        channel: 'eon-spline',
        message: {
          eon: {
            'Austin': Math.floor(Math.random() * 99),
            'New York': Math.floor(Math.random() * 99),
            'San Francisco': Math.floor(Math.random() * 99),
            'Portland': Math.floor(Math.random() * 99)
          }
        }
      });

    }, 1000);

    eon.chart({
      channels: ['eon-spline'],
      history: true,
      flow: true,
      pubnub: $scope.pubnub,
      generate: {
        bindto: '#eon-chart',
        data: {
          labels: false
        }
      }
    });
  }
})();