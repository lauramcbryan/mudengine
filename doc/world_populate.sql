insert into mud_place_class(place_class_code, name, movement_cost, description, size_capacity, weight_capacity, parent_class_code, build_cost, build_effort, MATERIAL_CODE) values('PLAIN', 'Plain', 1, 'Terra firme ao ar livre', 900, 900, null, null, null, null);
insert into mud_place_class(place_class_code, name, movement_cost, description, size_capacity, weight_capacity, parent_class_code, build_cost, build_effort, MATERIAL_CODE) values('SWAMP', 'Swamp', 8, 'Pântano', 100, 0, null, null, null, null);
insert into mud_place_class(place_class_code, name, movement_cost, description, size_capacity, weight_capacity, parent_class_code, build_cost, build_effort, MATERIAL_CODE) values('LAKE', 'Lake', 2, 'Superfície do lago', 50, 0, null, null, null, null);
insert into mud_place_class(place_class_code, name, movement_cost, description, size_capacity, weight_capacity, parent_class_code, build_cost, build_effort, MATERIAL_CODE) values('UNDERWATER', 'Lake', 2, 'Lake Bottom', 50, 100, null, null, null, null);
insert into mud_place_class(place_class_code, name, movement_cost, description, size_capacity, weight_capacity, parent_class_code, build_cost, build_effort, MATERIAL_CODE) values('RIVER', 'River', 3, 'Superfície do rio', 50, 0, null, null, null, null);
insert into mud_place_class(place_class_code, name, movement_cost, description, size_capacity, weight_capacity, parent_class_code, build_cost, build_effort, MATERIAL_CODE) values('TUNNEL', 'Tunnel', 1, 'Passagem subterranea', 10, 0, null, null, null, null);
insert into mud_place_class(place_class_code, name, movement_cost, description, size_capacity, weight_capacity, parent_class_code, build_cost, build_effort, MATERIAL_CODE) values('SKY', 'Sky', 1, 'Céu acima', 10, 0, null, null, null, null);
insert into mud_place_class(place_class_code, name, movement_cost, description, size_capacity, weight_capacity, parent_class_code, build_cost, build_effort, MATERIAL_CODE) values('POD', 'Escape Pod', 1, 'Cápsula de fuga', 50, 100, null, null, null, null);
insert into mud_place_class(place_class_code, name, movement_cost, description, size_capacity, weight_capacity, parent_class_code, build_cost, build_effort, MATERIAL_CODE) values('OUTPOST', 'Entreposto', 1, 'Entreposto', 50, 100, 'TUNNEL', 500, 100, 'BRICK');

-- being classes

-- Human
INSERT INTO mud_being_class(being_class, name, description, size, weight_capacity) VALUES ('HUMAN', 'Human', 'Average Human being', 1, 10);
INSERT INTO mud_being_class_attr(being_class, attr_code, attr_value) VALUES ('HUMAN', 'STR', 8);
INSERT INTO mud_being_class_attr(being_class, attr_code, attr_value) VALUES ('HUMAN', 'DEX', 8);
INSERT INTO mud_being_class_attr(being_class, attr_code, attr_value) VALUES ('HUMAN', 'INT', 8);
INSERT INTO mud_being_class_attr(being_class, attr_code, attr_value) VALUES ('HUMAN', 'CHR', 8);
INSERT INTO mud_being_class_attr(being_class, attr_code, attr_value) VALUES ('HUMAN', 'HP', 10);
INSERT INTO mud_being_class_skill(being_class, skill_code, skill_value) VALUES ('HUMAN', 'FARMER', 10);
INSERT INTO mud_being_class_skill(being_class, skill_code, skill_value) VALUES ('HUMAN', 'BRAWLER', 50);


-- Crinos
INSERT INTO mud_being_class(being_class, name, description, size, weight_capacity) VALUES ('CRINOS', 'Werewolf', 'Werewolf in Crinos form', 2, 25);
INSERT INTO mud_being_class_attr(being_class, attr_code, attr_value) VALUES ('CRINOS', 'STR', 12);
INSERT INTO mud_being_class_attr(being_class, attr_code, attr_value) VALUES ('CRINOS', 'DEX', 10);
INSERT INTO mud_being_class_attr(being_class, attr_code, attr_value) VALUES ('CRINOS', 'INT', 6);
INSERT INTO mud_being_class_attr(being_class, attr_code, attr_value) VALUES ('CRINOS', 'CHR', 6);
INSERT INTO mud_being_class_attr(being_class, attr_code, attr_value) VALUES ('CRINOS', 'HP', 15);
INSERT INTO mud_being_class_skill(being_class, skill_code, skill_value) VALUES ('CRINOS', 'BRAWLER', 80);

-- Ox
INSERT INTO mud_being_class(being_class, name, size, weight_capacity) VALUES ('OX', 'Ox', 4, 50);
INSERT INTO mud_being_class_attr(being_class, attr_code, attr_value) VALUES ('OX', 'STR', 15);
INSERT INTO mud_being_class_attr(being_class, attr_code, attr_value) VALUES ('OX', 'DEX', 6);
INSERT INTO mud_being_class_attr(being_class, attr_code, attr_value) VALUES ('OX', 'INT', 6);
INSERT INTO mud_being_class_attr(being_class, attr_code, attr_value) VALUES ('OX', 'CHR', 4);
INSERT INTO mud_being_class_attr(being_class, attr_code, attr_value) VALUES ('OX', 'HP', 20);



INSERT INTO mud_place(place_code, place_class_code) values(1, 'POD');
INSERT INTO mud_place(place_code, place_class_code) values(2, 'PLAIN');
INSERT INTO mud_place(place_code, place_class_code) values(3, 'LAKE');
INSERT INTO mud_place(place_code, place_class_code) values(4, 'UNDERWATER');

INSERT INTO mud_place_exits(place_code, direction, name, opened, visible, target_place_code) values (1, 'OUT', 'Outside the pod', true, true, 2);
INSERT INTO mud_place_exits(place_code, direction, name, opened, visible, target_place_code) values (2, 'NORTH', 'A Lake', true, true, 3);
INSERT INTO mud_place_exits(place_code, direction, name, opened, visible, target_place_code) values (2, 'IN', 'The Escape Pod', true, true, 1);
INSERT INTO mud_place_exits(place_code, direction, name, opened, visible, target_place_code) values (3, 'DOWN', 'Lake bottom', true, true, 4);
INSERT INTO mud_place_exits(place_code, direction, name, opened, visible, target_place_code) values (4, 'UP', 'Surface', true, true, 3);

