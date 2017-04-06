/*!
 * StringBuilder.utils.formatter
 * Copyright(c) 2013 Delmo Carrozzo <dcardev@gmail.com>
 * MIT Licensed
 */

/**
 * Module dependencies.
 */
 var numeral = require('numeral')
   , moment = require('moment');


/**
 * Expose functionality
 */
exports.format = format;

/**
 * Format the `fmat` with `value`
 */
function format(fmat, value) {

	if (typeof value === 'number') {
		return numeral(value).format(fmat);
	} else if (true === (value instanceof Date )) {
		return moment(value).format(fmat);
	}

	return value;
}