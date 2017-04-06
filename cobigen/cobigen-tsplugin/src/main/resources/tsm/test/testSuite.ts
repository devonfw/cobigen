import merge from '../src/index';
import { expect } from 'chai';
// if you used the '@types/mocha' method to install mocha type definitions, uncomment the following line
import 'mocha';

describe('Hello function', () => {
  it('should return hello world', () => {
    const result = merge(false, './test/importsBase.ts', './test/importsPatch.ts');
    expect(result).to.equal('Hello World!');
  });
});