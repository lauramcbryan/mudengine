'use strict';

exports = function (router) {
    router.post('/', function (req, res) {
        handler(req.body, null, res);

        console.log(res);

        res.send(res);

    });
};

exports.handler = (event, context, callback) => {

	event.actor.being.curPlaceCode = event.actor.place.exits[event.targetCode].targetPlaceCode
	event.actor.being.curWorld = event.actor.place.exits[event.targetCode].targetWorld

    return callback(null, event)
};
