delete from mud_place_exit;
delete from mud_place_attr;
delete from mud_place;
delete from mud_place_class_attr;
delete from mud_place_class;

insert into mud_place_class(place_class_code, name, movement_cost, description) values('TEST', 'Test', 1, 'Test PlaceClass');
insert into mud_place_class(place_class_code, name, movement_cost, description, DEMISE_CLASS_CODE) values('TESTBLDG', 'Test Building', 1, 'Test Building PlaceClass', 'TEST');

insert into mud_place_class_attr(place_class_code, attr_code, attr_value) values('TEST', 'HP', 50);
insert into mud_place_class_attr(place_class_code, attr_code, attr_value) values('TEST', 'MAXHP', 500);

insert into mud_place_class_attr(place_class_code, attr_code, attr_value) values('TESTBLDG', 'HP2', 3);
insert into mud_place_class_attr(place_class_code, attr_code, attr_value) values('TESTBLDG', 'MAXH2', 8);


insert into mud_place_class(place_class_code, name, movement_cost, description) values('PLAIN', 'Plain', 1, 'Terra firme ao ar livre');
insert into mud_place_class(place_class_code, name, movement_cost, description) values('SWAMP', 'Swamp', 8, 'Pântano');
insert into mud_place_class(place_class_code, name, movement_cost, description) values('LAKE', 'Lake', 2, 'Superfície do lago');
insert into mud_place_class(place_class_code, name, movement_cost, description) values('UNDERWATER', 'Lake', 2, 'Lake Bottom');
insert into mud_place_class(place_class_code, name, movement_cost, description) values('RIVER', 'River', 3, 'Superf�cie do rio');
insert into mud_place_class(place_class_code, name, movement_cost, description) values('SKY', 'Sky', 1, 'Ceu acima');
insert into mud_place_class(place_class_code, name, movement_cost, description, size_capacity, weight_capacity) values('RUIN', 'Ruins', 100, 'Ru�nas', 0, 0);
insert into mud_place_class(place_class_code, name, movement_cost, description, size_capacity, weight_capacity, parent_class_code, demise_class_code) values('TUNNEL', 'Tunnel', 1, 'Passagem subterranea', 10, 0, null, 'RUIN');
insert into mud_place_class(place_class_code, name, movement_cost, description, size_capacity, weight_capacity, parent_class_code, demise_class_code, build_cost, build_effort) values('POD', 'Escape Pod', 1, 'C�psula de fuga', 50, 100, null, 'SCRAP', null, null);
insert into mud_place_class(place_class_code, name, movement_cost, description, size_capacity, weight_capacity, parent_class_code, demise_class_code, build_cost, build_effort) values('OUTPOST', 'Outpost', 1, 'Entreposto', 50, 100, 'TUNNEL', 'TUNNEL', 500, 100);


INSERT INTO mud_place(place_code, place_class_code) values(1, 'POD');
INSERT INTO mud_place(place_code, place_class_code) values(2, 'PLAIN');
INSERT INTO mud_place(place_code, place_class_code) values(3, 'LAKE');
INSERT INTO mud_place(place_code, place_class_code) values(4, 'UNDERWATER');
select setval('MUD_PLACE_SEQ', 10);

INSERT INTO mud_place_exit(place_code, direction, name, opened, visible, target_place_code) values (1, 'OUT', 'Plain', true, true, 2);
INSERT INTO mud_place_exit(place_code, direction, name, opened, visible, target_place_code) values (2, 'NORTH', 'Lake', true, true, 3);
INSERT INTO mud_place_exit(place_code, direction, name, opened, visible, target_place_code) values (2, 'IN', 'Pod', true, true, 1);
INSERT INTO mud_place_exit(place_code, direction, name, opened, visible, target_place_code) values (3, 'DOWN', 'Underwater', true, true, 4);
INSERT INTO mud_place_exit(place_code, direction, name, opened, visible, target_place_code) values (4, 'UP', 'Surface', true, true, 3);

