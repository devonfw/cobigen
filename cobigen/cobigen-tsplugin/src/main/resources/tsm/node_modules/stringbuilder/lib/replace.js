/*!
 * StringBuilder.Replace
 * Copyright(c) 2013 Delmo Carrozzo <dcardev@gmail.com>
 * MIT Licensed
 */

var XRegExp = require('xregexp').XRegExp;

/**
 * Initialize a new Replace instruction
 * 
 * @param {Object} searchvalue
 * @param {Object} newvalue
 */
var Replace = module.exports = function Replace(searchvalue, newvalue) {
	var self = this;

	self.searchvalue = searchvalue || '';
	self.args = newvalue || '';
	
	return self;
}

Replace.prototype.constructor = Replace;

/**
 * Build this Replace
 *
 * @param {Function} fn
 */
Replace.prototype.build = function(str, fn){
	var self = this;

	var result = str.replace(self.searchvalue, self.args);
	
	fn && fn(null, result);

	return self;
};