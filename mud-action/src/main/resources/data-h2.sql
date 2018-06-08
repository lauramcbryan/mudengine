insert into mud_action_class(action_class_code, action_type, NRO_TURNS_EXPRESSION) values (1, 0, '1');
insert into mud_action_class_prereq(action_class_code, eval_order, check_expression, fail_expression) values (1, 1, 'actor.place.exits[#root.targetCode]!=null', 'actor.addMessage(''${str:NOEXIT}'')');
insert into mud_action_class_prereq(action_class_code, eval_order, check_expression, fail_expression) values (1, 2, 'actor.place.exits[#root.targetCode].opened', 'actor.addMessage(''${str:EXITCLOSED}'')');

insert into mud_action_class_effect(action_class_code, eval_order, effect_expression, message_expression) values (1, 1, 'actor.being.curPlaceCode = actor.place.exits[#root.targetCode].targetPlaceCode', null);
-- insert into mud_action_class_effect(action_class_code, eval_order, expression) values ('WALK', 2, 'actor.being.curWorld = actor.place.exits[#root.targetCode].targetWorld');
insert into mud_action_class_effect(action_class_code, eval_order, effect_expression) values (1, 2, 'actor.place = null');

insert into mud_action_class_cmd(command_id, action_class_code, mediator_type, target_type, verb, description, usage, locale) 
	values(1, 1, null, 'DIRECTION', 'WALK', 'Move to another place', 'WALK <DIRECTION>', 'en-US');

-- ========================================================================================================


insert into mud_action_class(action_class_code, action_type) values (2, 0);

insert into mud_action_class_prereq(action_class_code, eval_order, check_expression, fail_expression) values (2, 1, 
	'actor.being.curPlaceCode==target.place.placeCode', 'actor.addMessage(''${str:NOPLACE}'', #root.targetCode)');

insert into mud_action_class_effect(action_class_code, eval_order, effect_expression) values (2, 1, 'target.describeIt(#root.actor)');

insert into mud_action_class_cmd(command_id, action_class_code, mediator_type, target_type, verb, description, usage, locale) 
	values(1, 1, null, 'PLACE', 'LOOK', 'Get details from a place', 'LOOK <PLACE>', 'en-US');



insert into mud_action_class(action_class_code, verb, mediator_type, target_type, action_type) values (3, 'LOOK', null, 'BEING', 0);

insert into mud_action_class_prereq(action_class_code, eval_order, check_expression, fail_expression) values (3, 1, 
	'actor.being.curPlaceCode==target.being.curPlaceCode', 'actor.addMessage(''${str:NOBEING}'', #root.targetCode)');

insert into mud_action_class_effect(action_class_code, eval_order, effect_expression) values (3, 1, 'target.describeIt(#root.actor)');



insert into mud_action_class(action_class_code, verb, mediator_type, target_type, action_type) values (4, 'LOOK', null, 'ITEM', 0);

insert into mud_action_class_prereq(action_class_code, eval_order, check_expression, fail_expression) values (4, 1, 
	'((actor.being.curPlaceCode==target.item.curPlaceCode) || (actor.being.curPlaceCode==target.item.owner))', 
	'actor.addMessage(''${str:NOTHAVE}'', #root.targetCode)');

insert into mud_action_class_effect(action_class_code, eval_order, effect_expression) values (4, 1, 'target.describeIt(#root.actor)');

-- ========================================================================================================


insert into mud_action_class(action_class_code, verb, mediator_type, target_type, action_type) values (5, 'TAKE', null, 'ITEM', 0);

insert into mud_action_class_prereq(action_class_code, eval_order, check_expression, fail_expression) values (5, 1, 'actor.being.curPlaceCode==target.item.curPlaceCode)', 'actor.addMessage(''${str:NOTITEM}'')');

insert into mud_action_class_effect(action_class_code, eval_order, effect_expression) values (5, 1, 'target.item.owner=actor.being.beingCode');
insert into mud_action_class_effect(action_class_code, eval_order, effect_expression, message_expression) values (5, 2, 'target.item.curPlaceCode=null', 'actor.addMessage(''${str:YOUTAKE}'', #root.target.item.itemClass.name)');


-- ========================================================================================================


insert into mud_action_class(action_class_code, verb, mediator_type, target_type, action_type) values (6, 'DROP', null, 'ITEM', 0);

insert into mud_action_class_prereq(action_class_code, eval_order, check_expression, fail_expression) values (6, 1, 'actor.being.beingCode==target.item.owner)', 'actor.addMessage(''${str:NOTHAVE}'')');

insert into mud_action_class_effect(action_class_code, eval_order, effect_expression) values (6, 1, 'target.item.owner=null');
insert into mud_action_class_effect(action_class_code, eval_order, effect_expression, message_expression) values (6, 2, 'target.item.curPlaceCode=actor.place.placeCode', 'actor.addMessage(''${str:YOUDROP}'', #root.target.item.itemClass.name)');


-- ========================================================================================================

insert into mud_action_class(action_class_code, verb, mediator_type, target_type, action_type) values (7, 'TALK', null, 'BEING', 0);

insert into mud_action_class_prereq(action_class_code, eval_order, check_expression, fail_expression) values (7, 1, 'actor.being.curPlaceCode==target.being.curPlaceCode)', 'actor.addMessage(''${str:NOBEING}'')');

insert into mud_action_class_effect(action_class_code, eval_order, effect_expression) values (7, 1, 'target.addMessage(#root.mediatorCode)');


-- ========================================================================================================

insert into mud_action_class(action_class_code, verb, mediator_type, target_type, action_type) values (8, 'SHOUT', null, 'PLACE', 0);

insert into mud_action_class_prereq(action_class_code, eval_order, check_expression, fail_expression) values (8, 1, 'actor.place.placeCode==target.place.placeCode)', 'actor.addMessage(''${str:NOTHERE_PLACE}'')');

insert into mud_action_class_effect(action_class_code, eval_order, effect_expression) values (8, 1, 'target.addMessage(#root.mediatorCode)');


-- ========================================================================================================


--insert into mud_action(ACTION_UID, ISSUER_CODE, ACTOR_CODE, ACTION_CLASS_CODE, MEDIATOR_CODE, MEDIATOR_TYPE, TARGET_CODE, TARGET_TYPE, START_TURN, CUR_STATE) 
--	values(1, 1, 1, 'WALK', null, null, 'NORTH', 'DIRECTION', 1, 0);
