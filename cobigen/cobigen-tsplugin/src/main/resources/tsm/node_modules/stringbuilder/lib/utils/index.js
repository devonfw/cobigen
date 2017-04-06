/*!
 * StringBuilder.utils
 * Copyright(c) 2013 Delmo Carrozzo <dcardev@gmail.com>
 * MIT Licensed
 */

/**
 * Module dependencies.
 */
 var formatter = require('./formatter')
   , args = require('./args')
   , convert = require('./convert');


/**
 * Expose functionality
 */
exports.format = formatter.format;

exports.replaceArgs = args.replace;
exports.hasArgs = args.hasArgs;

exports.toTitleCase = convert.toTitleCase;
exports.toCamelCase = convert.toCamelCase;
exports.toJsonCase = convert.toJsonCase;