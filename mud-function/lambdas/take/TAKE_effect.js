'use strict';

exports.handler = (event, context, callback) => {

	event.target.item.curOwner=event.actor.being.code
	
	event.target.item.curPlaceCode=null
	event.target.item.curWorld=null

    return callback(null, event)
};
