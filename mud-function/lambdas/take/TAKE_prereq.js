'use strict';

exports.handler = (event, context, callback) => {

	event.messages = []

    if (event.actor.being.curPlaceCode!=event.target.item.curPlaceCode) {
        event.messages.push({targetCode: event.actorCode, messageKey:"action.take.no.item"})
        event.curState = "REFUSED"
    }

    return callback(null, event)
};
