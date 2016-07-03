<!DOCTYPE html>
<!--[if lt IE 7]>
<html class="no-js lt-ie9 lt-ie8 lt-ie7"> <![endif]-->
<!--[if IE 7]>
<html class="no-js lt-ie9 lt-ie8"> <![endif]-->
<!--[if IE 8]>
<html class="no-js lt-ie9"> <![endif]-->
<!--[if gt IE 8]><!-->
<html class="no-js"> <!--<![endif]-->
<head>
    <meta charset="utf-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge,chrome=1">
    <title></title>
    <meta name="description" content="">
    <meta name="viewport" content="width=device-width, initial-scale=1">
	<!-- build:css({.tmp,app}) css/oasp.css -->
    <!-- inject:css -->
    <!-- css files will be automaticaly insert here -->
    <!-- endinject -->
    <!-- endbuild -->
    <!-- build:jsModernizr({.tmp,app}) js/modernizr.js -->
    <script src="bower_components/modernizr/modernizr.js"></script>
    <script src="bower_components/respond/respond.min.js"></script>
    <!-- endbuild -->
</head>
<body data-ng-app="app" data-ng-controller="AppCntl"> <!-- data-ng-controller="AppCntl" to be removed? -->
<!-- process:include main/html/layout/browsehappy.html --><!-- /process -->
<!-- process:include main/html/layout/navbar.html --><!-- /process -->
<!-- process:include main/html/layout/container.html --><!-- /process -->

<!-- build:jsVendor({.tmp,app}) js/vendor.js -->
<!-- bower:js -->
<script src="bower_components/jquery/dist/jquery.js"></script>
<script src="bower_components/angular/angular.js"></script>
<script src="bower_components/angular-route/angular-route.js"></script>
<script src="bower_components/angular-ui-select/dist/select.js"></script>
<script src="bower_components/autofill-event/src/autofill-event.js"></script>
<script src="bower_components/angular-ui-bootstrap-bower/ui-bootstrap-tpls.js"></script>
<script src="bower_components/angular-translate/angular-translate.js"></script>
<script src="bower_components/angular-translate-loader-partial/angular-translate-loader-partial.js"></script>
<script src="bower_components/spin.js/spin.js"></script>
<script src="bower_components/angular-spinner/angular-spinner.js"></script>
<script src="bower_components/trNgGrid/release/trNgGrid.min.js"></script>
<script src="bower_components/sockjs-client/dist/sockjs.js"></script>
<script src="bower_components/stomp-websocket/lib/stomp.min.js"></script>
<!-- endbower -->
<!-- endbuild -->

<!-- build:jsApp({.tmp,app}) js/app.js -->
<!-- inject:js -->
<!-- js files will be inserted here automatically -->
<!-- endinject -->
<!-- endbuild -->
<div data-spinner="globalSpinner"></div>
</body>
</html>
