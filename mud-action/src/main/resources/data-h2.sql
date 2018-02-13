insert into mud_action_class(action_class_code, verb, action_type) values ('WALK', 'WALK', 0);

insert into mud_action_class_prereq(action_class_code, eval_order, expression, message_code) values ('WALK', 1, 'actor.place.exits[#root.targetCode]!=null', 10);
insert into mud_action_class_prereq(action_class_code, eval_order, expression, message_code) values ('WALK', 2, 'actor.place.exits[#root.targetCode].opened', 11);

insert into mud_action_class_effect(action_class_code, eval_order, expression, message_code) values ('WALK', 1, 'actor.being.curPlaceCode = actor.place.exits[#root.targetCode].targetPlaceCode', 12);
-- insert into mud_action_class_effect(action_class_code, eval_order, expression) values ('WALK', 2, 'actor.being.curWorld = actor.place.exits[#root.targetCode].targetWorld');
insert into mud_action_class_effect(action_class_code, eval_order, expression) values ('WALK', 2, 'actor.place = null');


insert into mud_action_class(action_class_code, verb, action_type) values ('LOOK', 'LOOK', 0);

insert into mud_action_class_effect(action_class_code, eval_order, expression) values ('LOOK', 1, '$action.sendMessageTo($action.being.beingCode, ''BEING'', ''YOUAREIN'', actor.place.name)');
insert into mud_action_class_effect(action_class_code, eval_order, expression) values ('LOOK', 2, '$action.sendMessageTo($action.being.beingCode, ''BEING'', ''YOUAREINDESC'', actor.place.description)');
insert into mud_action_class_effect(action_class_code, eval_order, expression) values ('LOOK', 3, '$action.sendMessageTo($action.being.beingCode, ''BEING'', ''EXITHEADER'')');
insert into mud_action_class_effect(action_class_code, eval_order, expression) values ('LOOK', 4, '$action.sendMessageTo($action.being.beingCode, ''BEING'', ''YOUAREIN'', actor.place.name)');


insert into mud_action(ACTION_UID, ISSUER_CODE, ACTOR_CODE, ACTION_CLASS_CODE, WORLD_NAME, MEDIATOR_CODE, PLACE_CODE, TARGET_CODE, TARGET_TYPE, START_TURN, CUR_STATE) 
	values(1, 1, 1, 'WALK', 'aforgotten', null, 1, 'NORTH', 3, 1, 0);

