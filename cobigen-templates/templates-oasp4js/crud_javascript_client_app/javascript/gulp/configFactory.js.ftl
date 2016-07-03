var configFactory = function (externalConfig) {
    'use strict';
    var $s = require('string'), _ = require('lodash'),
        paths = externalConfig.paths,
        modules = externalConfig.modules,
        builder = (function () {
            return {
                expandModule: function (module) {
                    var modules;
                    if (module && module.indexOf('/') >= 0) {
                        modules = module.split('/');
                        return {
                            module: modules[0],
                            submodule: modules[1]
                        };
                    } else {
                        return {
                            module: module
                        };
                    }
                },
                build: function (path, module, submodule) {
                    return $s(path)
                        .replaceAll('{module}', module)
                        .replaceAll('{submodule}', submodule)
                        .replaceAll('{app}', paths.app)
                        .replaceAll('{tmp}', paths.tmp)
                        .replaceAll('{test}', paths.test)
                        .replaceAll('{dist}', paths.dist).s;
                },
                buildForModules: function (patterns) {
                    var i, j, result = [];
                    for (i = 0; i < modules.length; i += 1) {
                        for (j = 0; j < arguments.length; j += 1) {
                            result.push(builder.build(arguments[j], modules[i]));
                        }
                    }
                    return result;
                },
                visitModules: function (factoryFn) {
                    var i, result = [], item;
                    for (i = 0; i < modules.length; i += 1) {
                        item = factoryFn(modules[i]);
                        if (item) {
                            result.push(item);
                        }
                    }
                    return result;
                }
            };
        }());
    return {
        builder: builder,
        context: externalConfig.proxyContext,
        proxy: externalConfig.proxy + externalConfig.proxyContext,
        app: {
            src: function () {
                return builder.build('{app}');
            },
            tmp: function () {
                return builder.build('{tmp}');
            },
            test: function () {
                return builder.build('{test}');
            },
            dist: function () {
                return builder.build('{dist}');
            },
            externalConfig: function (rawKey) {
                return externalConfig[rawKey];
            }
        },
        js: {
            src: function () {
                var appModule = externalConfig.appBuild === true ?
                    [
                        builder.build('{app}/*.module.js')
                    ] :
                    [

                    ];
                return _.flatten([
                    appModule,
                    builder.buildForModules(
                        '{app}/{module}/js/**/**.module.js',
                        '{app}/{module}/js/**/!(*spec|*mock).js',
                        '{tmp}/{module}/js/**/*.js'
                    )
                ]);
            },
            testSrc: function () {
                return _.flatten([
                    builder.buildForModules(
                        '{app}/{module}/js/**/*.mock.js'
                    ),
                    builder.buildForModules(
                        '{app}/{module}/js/**/*.spec.js'
                    ),
                    builder.buildForModules(
                        '{app}/*.spec.js'
                    )
                ]);
            },
            lintSrc: function () {
                return _.flatten([
                    [
                        builder.build('{app}/*.module.js')
                    ],
                    builder.buildForModules(
                        '{app}/{module}/js/**/**.js'
                    )
                ]);
            }
        },
        index: {
            src: function () {
                return builder.build('{app}/index.html');
            }
        },
        css: {
            src: function (all) {
                return builder.visitModules(function (module) {
                    var subModuleDef = builder.expandModule(module), basePattern, suffixPattern;
                    if (subModuleDef.submodule) {
                        basePattern = '{app}/{module}/{submodule}/css';
                        suffixPattern = '/{submodule}.less';
                    } else {
                        basePattern = '{app}/{module}/css';
                        suffixPattern = '/{module}.less';
                    }

                    if (all) {
                        suffixPattern = '/**/*.less';
                    }
                    return builder.build(basePattern + suffixPattern, subModuleDef.module, subModuleDef.submodule);
                });
            },
            includePaths: function () {
                return builder.build('{app}');
            },
            dest: {
                file: function () {
                    return builder.build('css/oasp.css');
                },
                path: function () {
                    return builder.build('{tmp}/css/oasp.css');
                }
            },
            inject: function () {
                return builder.build('{tmp}/css/*.css');
            }
        },
        html: {
            src: function () {
                return builder.buildForModules(
                    '{app}/{module}/html/**/*.html',
                    '!{app}/{module}/html/cached/**/*.html'
                );
            }
        },
        img: {
            src: function (all) {
                if (all) {
                    return builder.buildForModules(
                        '{app}/{module}/img/**/*.*'
                    );
                }
                else {
                    return builder.buildForModules(
                        '{app}/{module}/img/**/*.*',
                        '!{app}/{module}/img/sprite/**'
                    );
                }
            }
        },
        i18n: {
            src: function () {
                return builder.buildForModules(
                    '{app}/{module}/i18n/**/*.*'
                );
            }
        },
        sprite: {
            src: function () {
                return builder.buildForModules(
                    '{app}/{module}/img/sprite/**/*.png'
                );
            },
            dest: {
                css: function () {
                    return builder.build('css/sprite.css');
                },
                img: function () {
                    return builder.build('img/sprite.png');
                }
            }
        },
        ngTemplates: {
            conf: function () {
                return builder.visitModules(function (module) {
                    var subModuleDef = builder.expandModule(module);
                    if (subModuleDef.submodule) {
                        return {
                            module: $s(subModuleDef.module).camelize().s + '.' + $s(subModuleDef.submodule).camelize().s + '.templates',
                            file: builder.build('{submodule}.templates.js', subModuleDef.module, subModuleDef.submodule),
                            moduleBasePath: builder.build('{module}/{submodule}/html', subModuleDef.module, subModuleDef.submodule),
                            dest: builder.build('{tmp}/{module}/{submodule}/js', subModuleDef.module, subModuleDef.submodule),
                            src: builder.build('{app}/{module}/{submodule}/html/cached/**/*.html', subModuleDef.module, subModuleDef.submodule)
                        };
                    } else {
                        return {
                            module: 'app.' + $s(module).camelize().s + '.templates',
                            file: builder.build('{module}.templates.js', module),
                            moduleBasePath: builder.build('{module}/html', module),
                            dest: builder.build('{tmp}/{module}/js', module),
                            src: builder.build('{app}/{module}/html/cached/**/*.html', module)
                        };

                    }
                });
            }
        }
    };
};


module.exports = configFactory;
