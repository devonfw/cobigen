'use strict';

var gulp = require('gulp');
var util = require('gulp-util');
var argv = require('yargs').argv;
var $ = require('gulp-load-plugins')({
    pattern: ['gulp-*']
});
gulp.task('sonar', function () {
    var options = {
        debug: false,
        separator: '\n',
        dryRun: false,
        sonar: {
            login: argv.login,
            password: argv.password,
            host: {
                url: 'http://localhost:9000/sonarqube'
            },
            jdbc: {
                url: 'jdbc:mysql://localhost:3306/sonar',
                username: 'sonar',
                password: 'sonar'
            },

            projectKey: 'io.oasp:oasp4js:0.0.1',
            projectName: 'oasp4js',
            projectVersion: '0.0.1',
            sources: ['app'].join(','),
            exclusions: ['app/bower_components/**/*', '**/*.spec.js', '**/*.mock.js'].join(','),
            language: 'js',
            sourceEncoding: 'UTF-8',
            'javascript.lcov.reportPath': 'test/coverage/lcov.info'
        }
    };

    // gulp source doesn't matter, all files are referenced in options object above
    return gulp.src('dummy.dummy', { read: false })
        .pipe($.sonar(options))
        .on('error', util.log);
});
