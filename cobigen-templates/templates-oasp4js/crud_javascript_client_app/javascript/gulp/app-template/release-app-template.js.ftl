/*global config*/
'use strict';
var gulp = require('gulp');
var git = require('gulp-git');
var del = require('del');
var argv = require('yargs').argv;
var gulpsync = require('gulp-sync')(gulp);

var handleGitError = function (err) {
    if (err) {
        throw err;
    }
}, execGitChain = function (commands, dir, done) {
    if (commands.length) {
        git.exec({args: commands[0], cwd: dir}, function (err) {
            handleGitError(err);
            if (commands.length > 1) {
                commands.shift();
                execGitChain(commands, dir, done);
            } else {
                done();
            }
        });
    }
};
gulp.task('apptemplate:release', [], function () {
    if (!argv.version || argv.version === true) {
        throw  new Error('Please call apptemplate:release with --version parameter');
    } else {
        gulp.start('apptemplate:release:internal');
    }
});
gulp.task('apptemplate:release:internal', gulpsync.sync(['apptemplate:release:prepareRepo', 'build:apptemplate', 'apptemplate:release:publish']));

gulp.task('apptemplate:release:prepareRepo', ['clean'], function (done) {
    git.clone('https://github.com/oasp/generator-oasp.git', {args: config.app.dist()}, function (err) {
        handleGitError(err);
        var conf = [
            config.builder.build('{dist}/app/templates/*'),
            config.builder.build('{dist}/app/templates/.*'),
            config.builder.build('!{dist}/app/templates/config.json'),
            config.builder.build('!**/{dist}/app/templates/{app}')
        ];
        del(conf, done);
    });
});

gulp.task('apptemplate:release:publish', [], function (done) {
    execGitChain(['config --global core.autocrlf false', 'add -A', 'commit -am "release ' + argv.version + '"', 'tag -a ' + argv.version + ' -f -m "' + argv.version + '"'], config.app.dist(), done);
});

gulp.task('apptemplate:deploy', [], function (done) {
    execGitChain(['push origin master', 'push origin --tags -f'], config.app.dist(), done);
});
