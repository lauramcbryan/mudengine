'use strict';

exports.handler = (event, context, callback) => {

	event.target.item.curOwner=null
	event.target.item.curPlaceCode=event.actor.being.curPlaceCode
	event.target.item.curWorld=event.actor.being.curWorld

    return callback(null, event)
};
