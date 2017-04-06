/*!
 * StringBuilder.utils.convert
 * Copyright(c) 2013 Delmo Carrozzo <dcardev@gmail.com>
 * MIT Licensed
 */

/**
 * Expose functionality
 */
exports.toTitleCase = toTitleCase;
exports.toCamelCase = toTitleCase;
exports.toJsonCase = toJsonCase;


/**
 * Return the `str` to title case
 * Example:
 *  'foo bar' => FooBar
 *  'foo_bar' => FooBar
 *  
 * @param  {String} str
 * @param  {Boolean} clean remove spaces or _ default as true
 * @return {String}    
 */
function toTitleCase(str, clean) {
	clean = clean || true;

	var res = str.toLowerCase().replace(/(?:^|\s|_)\w/g, function(match) {
        return match.toUpperCase();
    });

	if (true === clean){
		return res.replace(/(\s|_)+/g, '');
	}

	return res;
}

/**
 * Return the `str` to json case
 * Example:
 *  'foo bar' => fooBar
 *  'foo_bar' => fooBar
 * 
 * @param  {String} str
 * @param  {Boolean} clean remove spaces or _ default as true
 * @return {String}    
 */
function toJsonCase(str, clean) {
	var titleCase = toTitleCase(str, clean);

	return titleCase.substring(0, 1).toLowerCase() + titleCase.substring(1);
}
