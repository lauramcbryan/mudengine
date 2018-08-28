'use strict';

exports.handler = (event, context, callback) => {

	event.messages = []

    if (event.actor.place.exits[event.targetCode] == undefined) {
        event.messages.push({targetCode: event.actorCode, messageKey:"action.walk.no.exit"})
        event.curState = "REFUSED"
    } else {

	    if (event.actor.place.exits[event.targetCode].opened==false) {
	        event.messages.push({targetCode: event.actorCode, messageKey: "action.walk.exit.closed"})
	        event.curState = "REFUSED"
	    }
    }

    return callback(null, event)
};
