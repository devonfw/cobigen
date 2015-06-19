/*global config*/
'use strict';

var gulp = require('gulp');
// inject bower components
gulp.task('wiredep', function () {
    var wiredep = require('wiredep').stream;

    return gulp.src(config.index.src())
        .pipe(wiredep({
            directory: 'app/bower_components',
            exclude: ['bootstrap.js']
        }))
        .pipe(gulp.dest(config.app.src()));
});
