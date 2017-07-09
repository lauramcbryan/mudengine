insert into mud_place_class(place_class_code, name, movement_cost, description) values('PLAIN', 'Plain', 1, 'Terra firme ao ar livre');
insert into mud_place_class(place_class_code, name, movement_cost, description) values('SWAMP', 'Swamp', 8, 'Pântano');
insert into mud_place_class(place_class_code, name, movement_cost, description) values('LAKE', 'Lake', 2, 'Superfície do lago');
insert into mud_place_class(place_class_code, name, movement_cost, description) values('UNDERWATER', 'Lake', 2, 'Lake Bottom');
insert into mud_place_class(place_class_code, name, movement_cost, description) values('RIVER', 'River', 3, 'Superfície do rio');
insert into mud_place_class(place_class_code, name, movement_cost, description) values('SKY', 'Sky', 1, 'Céu acima');
insert into mud_place_class(place_class_code, name, movement_cost, description, size_capacity, weight_capacity) values('RUIN', 'Ruins', 100, 'Ruínas', 0, 0);
insert into mud_place_class(place_class_code, name, movement_cost, description, size_capacity, weight_capacity, parent_class_code, demise_class_code) values('TUNNEL', 'Tunnel', 1, 'Passagem subterranea', 10, 0, null, 'RUIN');
insert into mud_place_class(place_class_code, name, movement_cost, description, size_capacity, weight_capacity, parent_class_code, demise_class_code, build_cost, build_effort) values('POD', 'Escape Pod', 1, 'Cápsula de fuga', 50, 100, null, 'SCRAP', null, null);
insert into mud_place_class(place_class_code, name, movement_cost, description, size_capacity, weight_capacity, parent_class_code, demise_class_code, build_cost, build_effort) values('OUTPOST', 'Outpost', 1, 'Entreposto', 50, 100, 'TUNNEL', 'TUNNEL', 500, 100);

INSERT INTO mud_place(place_code, place_class_code) values(1, 'POD');
INSERT INTO mud_place(place_code, place_class_code) values(2, 'PLAIN');
INSERT INTO mud_place(place_code, place_class_code) values(3, 'LAKE');
INSERT INTO mud_place(place_code, place_class_code) values(4, 'UNDERWATER');

INSERT INTO mud_place_exit(place_code, direction, name, opened, visible, target_place_code) values (1, 'OUT', 'Plain', true, true, 2);
INSERT INTO mud_place_exit(place_code, direction, name, opened, visible, target_place_code) values (2, 'NORTH', 'Lake', true, true, 3);
INSERT INTO mud_place_exit(place_code, direction, name, opened, visible, target_place_code) values (2, 'IN', 'Pod', true, true, 1);
INSERT INTO mud_place_exit(place_code, direction, name, opened, visible, target_place_code) values (3, 'DOWN', 'Underwater', true, true, 4);
INSERT INTO mud_place_exit(place_code, direction, name, opened, visible, target_place_code) values (4, 'UP', 'Surface', true, true, 3);

