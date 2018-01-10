-- attributes
INSERT INTO mud_attribute(attr_code, name) VALUES ('AAA', 'Test AAA');
INSERT INTO mud_attribute(attr_code, name) VALUES ('AAB', 'Test AAB');
INSERT INTO mud_attribute(attr_code, name) VALUES ('BAA', 'Test BAA');
INSERT INTO mud_attribute(attr_code, name) VALUES ('BAB', 'Test BAB');


-- skill categories
INSERT INTO mud_skill_category(category_code, name, attr_code_based) VALUES ('SKLCA', 'Test AAA', 'AAA');
INSERT INTO mud_skill_category(category_code, name, attr_code_based) VALUES ('SKLCB', 'Test AAB', 'AAB');
INSERT INTO mud_skill_category(category_code, name, attr_code_based) VALUES ('SKLCC', 'Test BAA', 'BAA');
INSERT INTO mud_skill_category(category_code, name, attr_code_based) VALUES ('SKLCD', 'Test BAB', 'BAB');

-- skills
INSERT INTO mud_skill(skill_code, category_code, name) VALUES ('SKLA', 'SKLCA', 'Test AAA');
INSERT INTO mud_skill(skill_code, category_code, name) VALUES ('SKLB', 'SKLCB', 'Test AAB');
INSERT INTO mud_skill(skill_code, category_code, name) VALUES ('SKLC', 'SKLCC', 'Test AAC');
INSERT INTO mud_skill(skill_code, category_code, name) VALUES ('SKLD', 'SKLCD', 'Test AAD');


-- being classes

-- Test Class A
INSERT INTO mud_being_class(being_class_code, name, size, weight_capacity) VALUES ('CLASSA', 'Test A', 1, 10);
INSERT INTO mud_being_class_attr(being_class_code, attr_code, attr_value) VALUES ('CLASSA', 'AAA', 8);
INSERT INTO mud_being_class_attr(being_class_code, attr_code, attr_value) VALUES ('CLASSA', 'AAB', 16);
INSERT INTO mud_being_class_skill(being_class_code, skill_code, skill_value) VALUES ('CLASSA', 'SKLA', 10);
INSERT INTO mud_being_class_skill(being_class_code, skill_code, skill_value) VALUES ('CLASSA', 'SKLB', 50);

-- Test Class B
INSERT INTO mud_being_class(being_class_code, name, size, weight_capacity) VALUES ('CLASSB', 'Test B', 1, 10);
INSERT INTO mud_being_class_attr(being_class_code, attr_code, attr_value) VALUES ('CLASSB', 'BAA', 8);
INSERT INTO mud_being_class_attr(being_class_code, attr_code, attr_value) VALUES ('CLASSB', 'BAB', 16);
INSERT INTO mud_being_class_skill(being_class_code, skill_code, skill_value) VALUES ('CLASSB', 'SKLC', 10);
INSERT INTO mud_being_class_skill(being_class_code, skill_code, skill_value) VALUES ('CLASSB', 'SKLD', 50);

