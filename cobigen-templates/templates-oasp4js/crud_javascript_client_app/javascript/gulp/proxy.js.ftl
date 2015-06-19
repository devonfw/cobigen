/*global config*/
'use strict';

var httpProxy = require('http-proxy');
var chalk = require('chalk');
//var config = require('./config.js');

var proxy = httpProxy.createProxyServer({
    target: config.proxy,
    ws: true
});
/**
 * Additional logging for 500
 */
proxy.on('error', function (error, req, res) {
    res.writeHead(500, {
        'Content-Type': 'text/plain'
    });

    console.error(chalk.red('[Proxy]'), error);
});
/**
 * Update cookie to allow app be served on /.
 */
proxy.on('proxyRes', function (proxyRes) {
    if (proxyRes.headers['set-cookie']) {
        proxyRes.headers['set-cookie'][0] = proxyRes.headers['set-cookie'][0].replace(config.context, '');
    }
});
/**
 * Support Websockets.
 */
proxy.on('upgrade', function (req, socket, head) {
    proxy.ws(req, socket, head);
});
/**
 * Create middleware and define routing
 */
function proxyMiddleware(req, res, next) {
    if (/\/services\//.test(req.url)) {
        proxy.web(req, res);
    } else if (/\/websocket\//.test(req.url)) {
        proxy.web(req, res);
    } else {
        next();
    }
}
module.exports = [proxyMiddleware];
