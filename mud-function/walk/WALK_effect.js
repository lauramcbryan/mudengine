'use strict';

exports.handler = (event, context, callback) => {

	event.actor.being.curPlaceCode = event.actor.place.exits[event.targetCode].targetPlaceCode
	event.actor.being.curWorld = event.actor.place.exits[event.targetCode].targetWorld

    return callback(null, event)
};
