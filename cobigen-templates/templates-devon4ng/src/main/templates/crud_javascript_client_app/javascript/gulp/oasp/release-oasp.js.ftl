/*global config*/
'use strict';
var gulp = require('gulp');
var git = require('gulp-git');
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
gulp.task('oasp:release', [], function () {
    if (!argv.version || argv.version === true) {
        throw  new Error('Please call oasp:release with --version parameter');
    } else {
        gulp.start('oasp:release:internal');
    }
});
gulp.task('oasp:release:internal', gulpsync.sync(['build:oasp:init', 'oasp:release:prepareRepo', 'build:oasp', 'oasp:release:publish']));

gulp.task('oasp:release:prepareRepo', ['clean'], function (done) {
    git.clone(config.app.externalConfig('releaseRepo'), {args: config.app.dist()}, function (err) {
        handleGitError(err);
        execGitChain(['rm -r -f *'], config.app.dist(), done);
    });
});

gulp.task('oasp:release:publish', [], function (done) {
    execGitChain(['config --global core.autocrlf false', 'add -A', 'commit -am "release ' + argv.version + '"', 'tag -a ' + argv.version + ' -f -m "' + argv.version + '"'], config.app.dist(), done);
});

gulp.task('oasp:deploy', [], function (done) {
    execGitChain(['push origin master','push origin --tags -f'], config.app.dist(), done);
});
