set role mudengine_action;

insert into mud_action_class(action_class_code, verb) values (1, 'WALK');

insert into mud_action_class_prereq(action_class_code, eval_order, expression, message_code) values (1, 1, '$action.place.exit($target)', 10);
insert into mud_action_class_prereq(action_class_code, eval_order, expression, message_code) values (1, 2, '$action.place.exit($target).opened', 11);

insert into mud_action_class_cost(action_class_code, eval_order, expression) values (1, 1, '$action.time=$action.actor.place.placeClass.movementCost');

insert into mud_action_class_effect(action_class_code, eval_order, expression, message_code) values (1, 1, '$action.being.curLocation = $action.place.exit[$target].targetPlaceCode', 12);
insert into mud_action_class_effect(action_class_code, eval_order, expression) values (1, 2, '$action.being.curWorld = $action.place.exit[$target].targetWorld');
