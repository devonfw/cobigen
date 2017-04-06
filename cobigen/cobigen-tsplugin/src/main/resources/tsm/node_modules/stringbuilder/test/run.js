process.env.NODE_ENV = 'test';

var StringBuilder = require('../lib/stringbuilder')
  , assert = require('assert');

var lorem = "Lorem ipsum dolor sit amet, consectetur adipisicing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum.";

describe('Making `Lorem ipsum` with StringBuilder', function() {
	var sb = new StringBuilder()
	  , res = '';

	before(function(done){
		sb
		  .append('Lorem {0} sit {1},', 'ipsum dolor', 'amet')
		  .append(' consectetur {0} elit,', 'adipisicing')
		  .append(' sed do {0} dolore magna aliqua.', 'eiusmod tempor incididunt ut labore et')
		  .append(' Ut {0} veniam,', 'enim ad minim')
		  .append(' quis {0} laboris', 'nostrud exercitation ullamco')
		  .append(' nisi {0} ex ea commodo consequat.', 'ut aliquip')
		  .append(' Duis {0} reprehenderit in voluptate', 'aute irure dolor in')
		  .append(' velit esse cillum dolore eu fugiat nulla pariatur.')
		  .append(' Excepteur sint occaecat cupidatat non proident,')
		  .append(' sunt in culpa qui officia deserunt')
		  .append(' mollit anim id est laborum.');

	    sb.build(function(err, result){
	    	res = result;
	    	done();
	    });

		
	});

	it('the `Lorem ipsum` was created', function(){
        assert.ok(lorem === res);
    });

});


