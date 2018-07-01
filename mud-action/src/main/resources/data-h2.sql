insert into mud_action_class(action_class_code, action_type, mediator_type, target_type, NRO_TURNS_EXPRESSION) values (1, 0, null, 'DIRECTION', '1');
insert into mud_action_class_prereq(action_class_code, eval_order, check_expression, fail_expression) values (1, 1, 'actor.place.exits[#root.targetCode]!=null', 'actor.addMessage(''${str:NOEXIT}'')');
insert into mud_action_class_prereq(action_class_code, eval_order, check_expression, fail_expression) values (1, 2, 'actor.place.exits[#root.targetCode].opened', 'actor.addMessage(''${str:EXITCLOSED}'')');

insert into mud_action_class_effect(action_class_code, eval_order, effect_expression, message_expression) values (1, 1, 'actor.being.curPlaceCode = actor.place.exits[#root.targetCode].targetPlaceCode', null);
-- insert into mud_action_class_effect(action_class_code, eval_order, expression) values ('WALK', 2, 'actor.being.curWorld = actor.place.exits[#root.targetCode].targetWorld');
insert into mud_action_class_effect(action_class_code, eval_order, effect_expression) values (1, 2, 'actor.place = null');

insert into mud_action_class_cmd(command_id, action_class_code, verb, description, usage, locale) 
	values(1, 1, 'WALK', 'Move to another place', 'WALK <DIRECTION>', 'en-US');

insert into mud_action_class_cmd_parameter(command_id, name, input_message, type, required, domain_values, default_value) 
	values (1, 'targetCode', 'Please input the direction', 'anyString', 1, null, null);

insert into mud_action_class_cmd(command_id, action_class_code, verb, description, usage, locale) 
	values(101, 1, 'VÁ PARA', 'Ir para outro lugar', 'VÁ PARA <DIREÇÃO>', 'pt-BR');

insert into mud_action_class_cmd_parameter(command_id, name, input_message, type, required, domain_values, default_value) 
	values (101, 'targetCode', 'Por favor indique a direção', 'anyString', 1, null, null);


-- ========================================================================================================


insert into mud_action_class(action_class_code, action_type, mediator_type, target_type) values (2, 0, null, 'PLACE');

insert into mud_action_class_prereq(action_class_code, eval_order, check_expression, fail_expression) values (2, 1, 
	'actor.being.curPlaceCode==target.place.placeCode', 'actor.addMessage(''${str:NOPLACE}'', #root.targetCode)');

insert into mud_action_class_effect(action_class_code, eval_order, effect_expression) values (2, 1, 'target.describeIt(#root.actor)');

insert into mud_action_class_cmd(command_id, action_class_code, verb, description, usage, locale) 
	values(2, 2, 'LOOK', 'Get details from a place', 'LOOK <PLACE>', 'en-US');

insert into mud_action_class_cmd_parameter(command_id, name, input_message, type, required, domain_values, default_value) 
	values (2, 'targetCode', 'Please input the place to look at', 'anyString', 1, null, 'HERE');

insert into mud_action_class_cmd(command_id, action_class_code, verb, description, usage, locale) 
	values(102, 2, 'EXAMINE', 'Obtém detalhes do local', 'EXAMINE <LOCAL>', 'pt-BR');

insert into mud_action_class_cmd_parameter(command_id, name, input_message, type, required, domain_values, default_value) 
	values (102, 'targetCode', 'Por favor indique o local', 'anyString', 1, null, 'AQUI');



insert into mud_action_class(action_class_code, action_type, mediator_type, target_type) values (3, 0, null, 'BEING');

insert into mud_action_class_prereq(action_class_code, eval_order, check_expression, fail_expression) values (3, 1, 
	'actor.being.curPlaceCode==target.being.curPlaceCode', 'actor.addMessage(''${str:NOBEING}'', #root.targetCode)');

insert into mud_action_class_effect(action_class_code, eval_order, effect_expression) values (3, 1, 'target.describeIt(#root.actor)');

insert into mud_action_class_cmd(command_id, action_class_code, verb, description, usage, locale) 
	values(3, 3, 'LOOK', 'Get details from a being', 'LOOK <BEING>', 'en-US');

insert into mud_action_class_cmd_parameter(command_id, name, input_message, type, required, domain_values, default_value) 
	values (3, 'targetCode', 'Please input the being to look at', 'anyString', 1, null, null);


insert into mud_action_class_cmd(command_id, action_class_code, verb, description, usage, locale) 
	values(103, 3, 'EXAMINE', 'Obtém detalhes de um ser.', 'EXAMINE <SER>', 'pt-BR');

insert into mud_action_class_cmd_parameter(command_id, name, input_message, type, required, domain_values, default_value) 
	values (103, 'targetCode', 'Por favor, indique o ser a examinar', 'anyString', 1, null, null);


insert into mud_action_class(action_class_code, action_type, mediator_type, target_type) values (4, 0, null, 'ITEM');

insert into mud_action_class_prereq(action_class_code, eval_order, check_expression, fail_expression) values (4, 1, 
	'((actor.being.curPlaceCode==target.item.curPlaceCode) || (actor.being.curPlaceCode==target.item.owner))', 
	'actor.addMessage(''${str:NOTHAVE}'', #root.targetCode)');

insert into mud_action_class_effect(action_class_code, eval_order, effect_expression) values (4, 1, 'target.describeIt(#root.actor)');

insert into mud_action_class_cmd(command_id, action_class_code, verb, description, usage, locale) 
	values(4, 4, 'LOOK', 'Get details from an item', 'LOOK <ITEM>', 'en-US');

insert into mud_action_class_cmd_parameter(command_id, name, input_message, type, required, domain_values, default_value) 
	values (4, 'targetCode', 'Please input the item to look at', 'anyString', 1, null, null);

insert into mud_action_class_cmd(command_id, action_class_code, verb, description, usage, locale) 
	values(104, 4, 'EXAMINE', 'Obtém detalhes de um item', 'EXAMINE <ITEM>', 'pt-BR');

insert into mud_action_class_cmd_parameter(command_id, name, input_message, type, required, domain_values, default_value) 
	values (104, 'targetCode', 'Por favor, indique o item a examinar', 'anyString', 1, null, null);


-- ========================================================================================================


insert into mud_action_class(action_class_code, action_type, mediator_type, target_type) values (5, 0, null, 'ITEM');

insert into mud_action_class_prereq(action_class_code, eval_order, check_expression, fail_expression) values (5, 1, 'actor.being.curPlaceCode==target.item.curPlaceCode)', 'actor.addMessage(''${str:NOTITEM}'')');

insert into mud_action_class_effect(action_class_code, eval_order, effect_expression) values (5, 1, 'target.item.owner=actor.being.beingCode');
insert into mud_action_class_effect(action_class_code, eval_order, effect_expression, message_expression) values (5, 2, 'target.item.curPlaceCode=null', 'actor.addMessage(''${str:YOUTAKE}'', #root.target.item.itemClass.name)');

insert into mud_action_class_cmd(command_id, action_class_code, verb, description, usage, locale) 
	values(5, 5, 'TAKE', 'Take an item', 'TAKE <ITEM>', 'en-US');

insert into mud_action_class_cmd_parameter(command_id, name, input_message, type, required, domain_values, default_value) 
	values (5, 'targetCode', 'Please input the item', 'anyString', 1, null, null);

insert into mud_action_class_cmd(command_id, action_class_code, verb, description, usage, locale) 
	values(105, 5, 'PEGUE', 'Pegar um item', 'PEGUE <ITEM>', 'pt-BR');

insert into mud_action_class_cmd_parameter(command_id, name, input_message, type, required, domain_values, default_value) 
	values (105, 'targetCode', 'Por favor, indique o item', 'anyString', 1, null, null);


-- ========================================================================================================


insert into mud_action_class(action_class_code, action_type, mediator_type, target_type) values (6, 0, null, 'ITEM');

insert into mud_action_class_prereq(action_class_code, eval_order, check_expression, fail_expression) values (6, 1, 'actor.being.beingCode==target.item.owner)', 'actor.addMessage(''${str:NOTHAVE}'')');

insert into mud_action_class_effect(action_class_code, eval_order, effect_expression) values (6, 1, 'target.item.owner=null');
insert into mud_action_class_effect(action_class_code, eval_order, effect_expression, message_expression) values (6, 2, 'target.item.curPlaceCode=actor.place.placeCode', 'actor.addMessage(''${str:YOUDROP}'', #root.target.item.itemClass.name)');

insert into mud_action_class_cmd(command_id, action_class_code, verb, description, usage, locale) 
	values(6, 6, 'DROP', 'Drop an item', 'DROP <ITEM>', 'en-US');

insert into mud_action_class_cmd_parameter(command_id, name, input_message, type, required, domain_values, default_value) 
	values (6, 'targetCode', 'Please input the item', 'anyString', 1, null, null);


insert into mud_action_class_cmd(command_id, action_class_code, verb, description, usage, locale) 
	values(106, 6, 'SOLTE', 'Soltar um item', 'PEGUE <ITEM>', 'pt-BR');

insert into mud_action_class_cmd_parameter(command_id, name, input_message, type, required, domain_values, default_value) 
	values (106, 'targetCode', 'Por favor, indique o item', 'anyString', 1, null, null);


-- ========================================================================================================

insert into mud_action_class(action_class_code, action_type, mediator_type, target_type) values (7, 0, null, 'BEING');

insert into mud_action_class_prereq(action_class_code, eval_order, check_expression, fail_expression) values (7, 1, 'actor.being.curPlaceCode==target.being.curPlaceCode)', 'actor.addMessage(''${str:NOBEING}'')');

insert into mud_action_class_effect(action_class_code, eval_order, effect_expression) values (7, 1, 'target.addMessage(#root.mediatorCode)');

insert into mud_action_class_cmd(command_id, action_class_code, verb, description, usage, locale) 
	values(7, 7, 'TALK TO', 'Talk to another being', 'TALK TO <BEING> "<MESSAGE>"', 'en-US');

insert into mud_action_class_cmd_parameter(command_id, name, input_message, type, required, domain_values, default_value) 
	values (7, 'targetCode', 'Please input the being', 'anyString', 1, null, null);


insert into mud_action_class_cmd(command_id, action_class_code, verb, description, usage, locale) 
	values(107, 7, 'FALE COM', 'Falar com outro ser', 'FALE COM <SER> "<MENSAGEM>"', 'pt-BR');

insert into mud_action_class_cmd_parameter(command_id, name, input_message, type, required, domain_values, default_value) 
	values (107, 'targetCode', 'Por favor, indique o ser', 'anyString', 1, null, null);

-- ========================================================================================================

insert into mud_action_class(action_class_code, action_type, mediator_type, target_type) values (8, 0, null, 'PLACE');

insert into mud_action_class_prereq(action_class_code, eval_order, check_expression, fail_expression) values (8, 1, 'actor.place.placeCode==target.place.placeCode)', 'actor.addMessage(''${str:NOTHERE_PLACE}'')');

insert into mud_action_class_effect(action_class_code, eval_order, effect_expression) values (8, 1, 'target.addMessage(#root.mediatorCode)');

insert into mud_action_class_cmd(command_id, action_class_code, verb, description, usage, locale) 
	values(8, 8, 'SHOUT AT', 'Shout at a place', 'SHOUT AT <PLACE>', 'en-US');

insert into mud_action_class_cmd_parameter(command_id, name, input_message, type, required, domain_values, default_value) 
	values (8, 'targetCode', 'Please input the place', 'anyString', 1, null, null);

insert into mud_action_class_cmd(command_id, action_class_code, verb, description, usage, locale) 
	values(108, 8, 'GRITE', 'Gritar para um local', 'GRITE EM <LOCAL> "<MENSAGEM>"', 'pt-BR');

insert into mud_action_class_cmd_parameter(command_id, name, input_message, type, required, domain_values, default_value) 
	values (108, 'targetCode', 'Por favor, indique o local', 'anyString', 1, null, null);

-- ========================================================================================================


--insert into mud_action(ACTION_UID, ISSUER_CODE, ACTOR_CODE, ACTION_CLASS_CODE, MEDIATOR_CODE, MEDIATOR_TYPE, TARGET_CODE, TARGET_TYPE, START_TURN, CUR_STATE) 
--	values(1, 1, 1, 'WALK', null, null, 'NORTH', 'DIRECTION', 1, 0);
