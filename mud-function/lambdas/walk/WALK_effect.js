'use strict';

exports = function (router) {
    router.post('/', function (req, res) {

        var callback = function(status, event) {

        }

        handler(req.body, {}, callback);

        console.log(callback.event);

        res.send(callback.event);

    });
};

exports.handler = (event, context, callback) => {

	event.actor.being.curPlaceCode = event.actor.place.exits[event.targetCode].targetPlaceCode
	event.actor.being.curWorld = event.actor.place.exits[event.targetCode].targetWorld

    return callback(null, event)
};
