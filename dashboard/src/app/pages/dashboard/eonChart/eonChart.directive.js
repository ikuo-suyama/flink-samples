/**
 * @author v.lugovksy
 * created on 16.12.2015
 */
(function () {
  'use strict';

  angular.module('BlurAdmin.pages.dashboard')
      .directive('eonChart', eonChart);

  /** @ngInject */
  function eonChart() {
    return {
      restrict: 'E',
      controller: 'EonChartCtrl',
      templateUrl: 'app/pages/dashboard/eonChart/eonChart.html'
    };
  }
})();