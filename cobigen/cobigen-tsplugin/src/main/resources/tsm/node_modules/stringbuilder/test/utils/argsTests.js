process.env.NODE_ENV = 'test';

var args = require('../../lib/utils/args')
  , assert = require('chai').assert;

describe('replace', function() {
	it('should replace placeholders with values', function(){
        assert.strictEqual(args.replace('{0}+{1}', [1, 2]), '1+2');
    });

    it('should replace placeholders with empty string for undefined values', function(){
        assert.strictEqual(args.replace('{0}+{1}', [1, undefined]), '1+');
    });

    it('should replace placeholders with empty string for null values', function(){
        assert.strictEqual(args.replace('{0}+{1}', [null, 2]), '+2');
    });

});
