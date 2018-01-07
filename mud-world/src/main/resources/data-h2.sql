insert into mud_place_class(place_class_code, name, description) values('TEST', 'Test', 'Test PlaceClass');
insert into mud_place_class(place_class_code, name, description, DEMISE_CLASS_CODE) values('TESTBLDG', 'Test Building', 'Test Building PlaceClass', 'TEST');

insert into mud_place_class_attr(place_class_code, attr_code, attr_value) values('TEST', 'HP', 50);
insert into mud_place_class_attr(place_class_code, attr_code, attr_value) values('TEST', 'MAXHP', 500);

insert into mud_place_class_attr(place_class_code, attr_code, attr_value) values('TESTBLDG', 'HP2', 3);
insert into mud_place_class_attr(place_class_code, attr_code, attr_value) values('TESTBLDG', 'MAXH2', 8);


insert into mud_place_class(place_class_code, name, description) values('PLAIN', 'Plain', 'Terra firme ao ar livre');
insert into mud_place_class(place_class_code, name, description) values('SWAMP', 'Swamp', 'Pântano');
insert into mud_place_class(place_class_code, name, description) values('LAKE', 'Lake', 'Superfície do lago');
insert into mud_place_class(place_class_code, name, description) values('UNDERWATER', 'Lake', 'Lake Bottom');
insert into mud_place_class(place_class_code, name, description) values('RIVER', 'River', 'Superficie do rio');
insert into mud_place_class(place_class_code, name, description) values('SKY', 'Sky', 'Ceu acima');
insert into mud_place_class(place_class_code, name, description, size_capacity, weight_capacity) values('RUIN', 'Ruins', 'Ruinas', 0, 0);
insert into mud_place_class(place_class_code, name, description, size_capacity, weight_capacity, parent_class_code, demise_class_code) values('TUNNEL', 'Tunnel', 'Passagem subterranea', 10, 0, null, 'RUIN');
insert into mud_place_class(place_class_code, name, description, size_capacity, weight_capacity, parent_class_code, demise_class_code, build_cost, build_effort) values('POD', 'Escape Pod', 'Capsula de fuga', 50, 100, null, 'SCRAP', null, null);
insert into mud_place_class(place_class_code, name, description, size_capacity, weight_capacity, parent_class_code, demise_class_code, build_cost, build_effort) values('OUTPOST', 'Outpost', 'Entreposto', 50, 100, 'TUNNEL', 'TUNNEL', 500, 100);


INSERT INTO mud_place(place_code, place_class_code) values(1, 'POD');
INSERT INTO mud_place(place_code, place_class_code) values(2, 'PLAIN');
alter sequence MUD_PLACE_SEQ restart with 3;

INSERT INTO mud_place_exit(place_code, direction, name, opened, visible, target_place_code) values (1, 'OUT', 'Plain', true, true, 2);

