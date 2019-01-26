'use strict';

module.exports = function (router) {
    router.post('/', function (req, res) {

        var callback = function(status, event) {

        }


        handler(req.body, {}, callback);

        console.log(callback.event);

        res.send(callback.event);

    });
};

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
