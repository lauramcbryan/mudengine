INSERT INTO mud_item_class(item_class, size, weight, durability) VALUES ('WEAPON', 0.25, 0.25, 100);

INSERT INTO mud_item_class_attr(item_class, attr_code, attr_offset) VALUES ('WEAPON', 'STR', 1);

INSERT INTO mud_item_class_skill(item_class, skill_code, skill_offset) VALUES ('WEAPON', 'BRAWLER', 1);

INSERT INTO mud_item(item_code, item_class, usage_count, name, description) VALUES (1, 'WEAPON', 100, 'Sword', 'Blade made of steel');

INSERT INTO mud_item_attr(item_code, attr_code, attr_offset) VALUES (1, 'STR', 1);

INSERT INTO mud_item_skill(item_code, skill_code, skill_offset) VALUES (1, 'BRAWLER', 1);
