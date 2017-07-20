delete from mud_place_exit;
delete from mud_place_attr;
delete from mud_place;
delete from mud_place_class_attr;
delete from mud_place_class;

insert into mud_place_class(place_class_code, name, movement_cost, description) values('PLAIN', 'Plain', 1, 'Plain');
insert into mud_place_class(place_class_code, name, movement_cost, description) values('FOREST', 'Forest', 4, 'Forest');
insert into mud_place_class(place_class_code, name, movement_cost, description) values('LAKE', 'Lake', 2, 'Lake');
insert into mud_place_class(place_class_code, name, movement_cost, description) values('RIVER', 'River', 3, 'Surface');
insert into mud_place_class(place_class_code, name, movement_cost, description) values('UWATER', 'Lake', 2, 'Under the surface');
insert into mud_place_class(place_class_code, name, movement_cost, description) values('SWAMP', 'Swamp', 8, 'Swamp');
insert into mud_place_class(place_class_code, name, movement_cost, description) values('SKY', 'Sky', 1, 'Sky');
insert into mud_place_class(place_class_code, name, movement_cost, description) values('SPACE', 'Space', 1, 'Space');
insert into mud_place_class(place_class_code, name, movement_cost, description, size_capacity) values('RUIN', 'Ruins', 100, 'Ruins', 0);
insert into mud_place_class(place_class_code, name, movement_cost, description, size_capacity, parent_class_code, demise_class_code) values('TUNNEL', 'Tunnel', 1, 'Tunnel', 10, null, 'RUIN');
insert into mud_place_class(place_class_code, name, movement_cost, description, size_capacity, parent_class_code, demise_class_code) values('BIGTUNNEL', 'Large Tunnel', 1, 'Passagem subterranea', 100, 'TUNNEL', 'TUNNEL');
insert into mud_place_class(place_class_code, name, movement_cost, description, size_capacity, parent_class_code, demise_class_code, build_cost, build_effort) values('WORKSHOP', 'Workshop', 1, 'Workshop', 90, 'BIGTUNNEL', 'BIGTUNNEL', 500, 100);
insert into mud_place_class(place_class_code, name, movement_cost, description, size_capacity, parent_class_code, demise_class_code, build_cost, build_effort) values('POD', 'Escape Pod', 1, 'Escape Pod', 90, 'PLAIN', 'PLAIN', null, null);
insert into mud_place_class(place_class_code, name, movement_cost, description, size_capacity, parent_class_code, demise_class_code, build_cost, build_effort) values('LNODE', 'Living Node', 1, 'Living Node', 90, 'BIGTUNNEL', 'BIGTUNNEL', 300, 100);
insert into mud_place_class(place_class_code, name, movement_cost, description, size_capacity, parent_class_code, demise_class_code, build_cost, build_effort) values('RSTATION', 'Radio Station', 1, 'Radio Station', 40, 'BIGTUNNEL', 'BIGTUNNEL', 100, 50);
insert into mud_place_class(place_class_code, name, movement_cost, description, size_capacity, parent_class_code, demise_class_code, build_cost, build_effort) values('LRTRANS', 'Long Range Transmitter', 1, 'Long Range Transmitter', 50, 'RSTATION', 'BIGTUNNEL', 100, 50);
insert into mud_place_class(place_class_code, name, movement_cost, description, size_capacity, parent_class_code, demise_class_code, build_cost, build_effort) values('NETWORK', 'Remote Network', 1, 'Remote Network', 50, 'LRTRANS', 'BIGTUNNEL', 1000, 500);

insert into mud_place_class(place_class_code, name, movement_cost, description, size_capacity, parent_class_code, demise_class_code, build_cost, build_effort) values('SCOLLECT', 'Scrap Collector', 1, 'Scrap Collector', 80, 'BIGTUNNEL', 'BIGTUNNEL', 100, 50);
insert into mud_place_class(place_class_code, name, movement_cost, description, size_capacity, parent_class_code, demise_class_code, build_cost, build_effort) values('RECYCLER', 'Recycler', 1, 'Recycler', 60, 'SCOLLECT', 'BIGTUNNEL', 300, 100);
insert into mud_place_class(place_class_code, name, movement_cost, description, size_capacity, parent_class_code, demise_class_code, build_cost, build_effort) values('FACTORY', 'Factory', 1, 'Factory', 80, 'BIGTUNNEL', 'BIGTUNNEL', 1000, 500);

insert into mud_place_class(place_class_code, name, movement_cost, description, size_capacity, parent_class_code, demise_class_code, build_cost, build_effort) values('FARM', 'Farm', 1, 'Farm', 200, 'BIGTUNNEL', 'BIGTUNNEL', 500, 100);
insert into mud_place_class(place_class_code, name, movement_cost, description, size_capacity, parent_class_code, demise_class_code, build_cost, build_effort) values('FPROCESS', 'Food Processor', 1, 'Food Processor', 100, 'BIGTUNNEL', 'BIGTUNNEL', 500, 100);
insert into mud_place_class(place_class_code, name, movement_cost, description, size_capacity, parent_class_code, demise_class_code, build_cost, build_effort) values('IFPROCESS', 'Improved Food Processor', 1, 'Improved Food Processor', 100, 'FPROCESS', 'BIGTUNNEL', 1000, 500);
insert into mud_place_class(place_class_code, name, movement_cost, description, size_capacity, parent_class_code, demise_class_code, build_cost, build_effort) values('TCENTER', 'Training Center', 1, 'Training Center', 80, 'BIGTUNNEL', 'BIGTUNNEL', 1000, 500);

insert into mud_place_class_attr(place_class_code, attr_code, attr_value) values('POD', 'HP', 100);
insert into mud_place_class_attr(place_class_code, attr_code, attr_value) values('POD', 'MAXHP', 100);


INSERT INTO mud_place(place_code, place_class_code) values(1, 'POD');
INSERT INTO mud_place(place_code, place_class_code) values(2, 'FOREST');
INSERT INTO mud_place(place_code, place_class_code) values(3, 'LAKE');
INSERT INTO mud_place(place_code, place_class_code) values(4, 'UNDERWATER');
select setval('MUD_PLACE_SEQ', 10);

INSERT INTO mud_place_exit(place_code, direction, name, opened, visible, target_place_code) values (1, 'OUT', 'Forest', true, true, 2);
INSERT INTO mud_place_exit(place_code, direction, name, opened, visible, target_place_code) values (2, 'NORTH', 'Lake', true, true, 3);
INSERT INTO mud_place_exit(place_code, direction, name, opened, visible, target_place_code) values (2, 'IN', 'Pod', true, true, 1);
INSERT INTO mud_place_exit(place_code, direction, name, opened, visible, target_place_code) values (3, 'DOWN', 'Underwater', true, true, 4);
INSERT INTO mud_place_exit(place_code, direction, name, opened, visible, target_place_code) values (4, 'UP', 'Surface', true, true, 3);

