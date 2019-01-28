'use strict';

const prereq = require('./walk/WALK_prereq')
var callback = require('./callback')

module.exports = function (router) {

    router.post('/walk_prereq', function (req, res) {

        prereq.handler(req.body, {}, callback);

        res.send(callback.event);

    });


};