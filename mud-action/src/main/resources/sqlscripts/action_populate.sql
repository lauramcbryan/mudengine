set role mudengine_action;

insert into mud_action_class(action_class_code, verb, action_type) values ('WALK', 'WALK', 0);

insert into mud_action_class_prereq(action_class_code, eval_order, expression, message_code) values ('WALK', 1, '$action.place.exit($target)', 10);
insert into mud_action_class_prereq(action_class_code, eval_order, expression, message_code) values ('WALK', 2, '$action.place.exit($target).opened', 11);

insert into mud_action_class_cost(action_class_code, eval_order, expression) values ('WALK', 1, '$action.time=$action.actor.place.placeClass.movementCost');

insert into mud_action_class_effect(action_class_code, eval_order, expression, message_code) values ('WALK', 1, '$action.being.curLocation = $action.place.exit[$target].targetPlaceCode', 12);
insert into mud_action_class_effect(action_class_code, eval_order, expression) values ('WALK', 2, '$action.being.curWorld = $action.place.exit[$target].targetWorld');


insert into mud_action_class(action_class_code, verb, action_type) values ('LOOK', 'LOOK', 0);

insert into mud_action_class_effect(action_class_code, eval_order, expression) values ('LOOK', 1, '$action.sendMessageTo($action.being.beingCode, 'BEING', 'YOUAREIN', $action.place.name)');
insert into mud_action_class_effect(action_class_code, eval_order, expression) values ('LOOK', 2, '$action.sendMessageTo($action.being.beingCode, 'BEING', 'YOUAREINDESC', $action.place.description)');
insert into mud_action_class_effect(action_class_code, eval_order, expression) values ('LOOK', 3, '$action.sendMessageTo($action.being.beingCode, 'BEING', 'EXITHEADER')');
insert into mud_action_class_effect(action_class_code, eval_order, expression) values ('LOOK', 4, '$action.sendMessageTo($action.being.beingCode, 'BEING', 'YOUAREIN', $action.place.name)');
