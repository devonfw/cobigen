/*global config, isProd*/
'use strict';
var gulp = require('gulp');
var $ = require('gulp-load-plugins')({
    pattern: ['gulp-*', 'gulp.*', 'main-bower-files', 'uglify-save-license', 'del']
});
var gulpsync = require('gulp-sync')(gulp);

var ngTemplatesTasks = [];

gulp.task('ngTemplatesTasksGeneration', function () {
    config.ngTemplates.conf().forEach(function (ngTemplatesItemConf) {
        ngTemplatesTasks.push('ngTemplates[' + ngTemplatesItemConf.file + ']');
        gulp.task('ngTemplates[' + ngTemplatesItemConf.file + ']', [], function () {
            return gulp.src(ngTemplatesItemConf.src)
                .pipe($.processhtml({commentMarker: 'process',
                    recursive: true,
                    includeBase: config.app.src()}))
                .pipe($.minifyHtml({
                    empty: true,
                    spare: true,
                    quotes: true
                }))
                .pipe($.ngTemplates({
                    module: ngTemplatesItemConf.module,
                    path: function (path, base) {
                        return path.replace(base, ngTemplatesItemConf.moduleBasePath + '/').replace('/cached', '');
                    }
                }))
                .pipe($.concat(ngTemplatesItemConf.file))
                .pipe(gulp.dest(ngTemplatesItemConf.dest));
        });
    });
});

gulp.task('ngTemplatesTasksExecution', ngTemplatesTasks);

gulp.task('ngTemplates', gulpsync.sync(['ngTemplatesTasksGeneration', 'ngTemplatesTasksExecution']));

gulp.task('index', ['wiredep', 'ngTemplates', 'sprite', 'less'], function () {
    return gulp.src(config.index.src())
        .pipe($.inject(gulp.src(config.js.src(), {read: false}), {
            addRootSlash: false,
            ignorePath: [config.app.tmp(), config.app.src()]
        }))
        .pipe($.inject(gulp.src(config.css.inject(), {read: false}), {
            addRootSlash: false,
            ignorePath: [config.app.tmp(), config.app.src()]
        }))
        .pipe($.processhtml({commentMarker: 'process',
            recursive: true,
            includeBase: config.app.src()}))
        .pipe($.if(isProd(), $.usemin({
            css: [$.minifyCss(), 'concat', $.rev()],
            jsModernizr: [$.ngAnnotate(), $.uglify({preserveComments: $.uglifySaveLicense}), $.rev()],
            jsVendor: [$.ngAnnotate(), $.uglify({preserveComments: $.uglifySaveLicense}), $.rev()],
            jsApp: [$.ngAnnotate(), $.uglify({preserveComments: $.uglifySaveLicense}), $.rev()]
        })))
        .pipe($.if(isProd(), gulp.dest(config.app.dist())))
        .pipe($.if(!isProd(), gulp.dest(config.app.tmp())))
        .pipe($.size());
});

gulp.task('html', [], function () {
    return gulp.src(config.html.src(), { base: config.app.src() })
        .pipe($.newer(config.app.tmp()))
        .pipe($.processhtml({commentMarker: 'process',
            recursive: true,
            includeBase: config.app.src()}))
        .pipe($.if(isProd(), $.minifyHtml({
            empty: true,
            spare: true,
            quotes: true
        })))
        .pipe($.if(isProd(), gulp.dest(config.app.dist())))
        .pipe($.if(!isProd(), gulp.dest(config.app.tmp())))
        .pipe($.size());
});
gulp.task('copy-less', function () {
    return gulp.src(config.css.src(true), { base: config.app.src()})
        .pipe($.if(isProd(), gulp.dest(config.app.dist())))
        .pipe($.if(!isProd(), gulp.dest(config.app.tmp())))
        .pipe($.size());
});
gulp.task('less', function () {
    return gulp.src(config.css.src())
        .pipe($.concat(config.css.dest.file()))
        .pipe($.less({
            paths: config.css.includePaths()
        }))
        .pipe(gulp.dest(config.app.tmp()))
        .pipe($.size());
});

gulp.task('sprite', function () {
    return gulp.src(config.sprite.src())
        .pipe($.spritesmith({
            imgName: config.sprite.dest.img(),
            cssName: config.sprite.dest.css()
        }))
        .pipe(gulp.dest(config.app.tmp()))
        .pipe($.filter('**/*.png'))
        .pipe($.if(isProd(), gulp.dest(config.app.dist())));
});

gulp.task('img', function () {
    if (isProd()) {
        return gulp.src(config.img.src(), {base: config.app.src()})
            .pipe($.imagemin({
                optimizationLevel: 3,
                progressive: true,
                interlaced: true
            }))
            .pipe(gulp.dest(config.app.dist()));
    }
});
gulp.task('copy-img', function () {
    return gulp.src(config.img.src(true), {base: config.app.src()})
        .pipe($.imagemin({
            optimizationLevel: 3,
            progressive: true,
            interlaced: true
        }))
        .pipe($.if(isProd(), gulp.dest(config.app.dist())))
        .pipe($.if(!isProd(), gulp.dest(config.app.tmp())));
});
gulp.task('i18n', function () {
    if (isProd()) {
        return gulp.src(config.i18n.src(), {base: config.app.src()})
            .pipe(gulp.dest(config.app.dist()));
    }
});

gulp.task('fonts', function () {
    if (isProd()) {
        return gulp.src($.mainBowerFiles())
            .pipe($.filter('**/*.{eot,svg,ttf,woff}'))
            .pipe($.flatten())
            .pipe(gulp.dest(config.app.dist() + '/fonts/'));
    }
});

gulp.task('clean', function (done) {
    return $.del([config.app.tmp(), config.app.dist(), config.app.test()], done);
});

gulp.task('build', ['index', 'html', 'ngTemplates', 'less', 'sprite', 'img', 'i18n', 'fonts']);

gulp.task('build:develop', [], function () {
    process.env.NODE_ENV = 'dev';
    gulp.start('build');
});

gulp.task('build:ci', ['test'], function () {
    process.env.NODE_ENV = 'prod';
    gulp.start('build');
});

gulp.task('build:dist', [], function () {
    process.env.NODE_ENV = 'prod';
    gulp.start('build');
});
