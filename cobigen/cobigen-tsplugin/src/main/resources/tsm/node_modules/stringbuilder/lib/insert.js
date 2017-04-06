/*!
 * StringBuilder.Insert
 * Copyright(c) 2013 Delmo Carrozzo <dcardev@gmail.com>
 * MIT Licensed
 */

 /**
 * Initialize a new Insert instruction
 * 
 *
 * @param {Object} options
 */
var Insert = module.exports = function Insert(value, position) {
	var self = this;

	self.value = value || '';
	self.position = position || 0;
	
	return self;
}

Insert.prototype.constructor = Insert;

/**
 * Build this Insert
 *
 * @param {Function} fn
 */
Insert.prototype.build = function(str, fn){
	var self = this;

	var result = [str.slice(0, self.position), self.value, str.slice(self.position)].join('')
	
	fn && fn(null, result);

	return self;
};