'use strict';

exports.handler = (event, context, callback) => {

	event.messages = []

    if (event.actor.place.exits[event.targetCode] == undefined) {
    	event.curState = "REFUSED"
        event.messages.push(
        		{
        			targetCode: event.actorCode, 
        			messageKey:"action.walk.no.exit"
        		})
        
    } else {

	    if (event.actor.place.exits[event.targetCode].opened==false) {
	    	event.curState = "REFUSED"
	        event.messages.push(
	        	{
	        		targetCode: event.actorCode, 
	        		messageKey: "action.walk.exit.closed"
	        	})
	        
	    }
    }

    return callback(null, event)
};
