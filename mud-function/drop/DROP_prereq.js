'use strict';

exports.handler = (event, context, callback) => {

	event.messages = []

    if (actor.being.beingCode!=event.target.item.owner) {
        event.messages.push({targetCode: event.actorCode, messageKey:"action.drop.no.item"})
        event.curState = "REFUSED"
    }

    return callback(null, event)
};
