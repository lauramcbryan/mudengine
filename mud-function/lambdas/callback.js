'use strict';

var status = new String()
var event = new String()

exports.callback = function(_status, _event) {
	status = _status
	event = _event
};
