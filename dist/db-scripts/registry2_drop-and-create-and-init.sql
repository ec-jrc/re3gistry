/* Drop Tables */

DROP TABLE IF EXISTS reg_action CASCADE;
DROP TABLE IF EXISTS reg_field CASCADE;
DROP TABLE IF EXISTS reg_fieldmapping CASCADE;
DROP TABLE IF EXISTS reg_fieldtype CASCADE;
DROP TABLE IF EXISTS reg_group CASCADE;
DROP TABLE IF EXISTS reg_item_reg_group_reg_role_mapping CASCADE;
DROP TABLE IF EXISTS reg_itemproposed_reg_group_reg_role_mapping CASCADE;
DROP TABLE IF EXISTS reg_itemhistory_reg_group_reg_role_mapping CASCADE;
DROP TABLE IF EXISTS reg_item CASCADE;
DROP TABLE IF EXISTS reg_itemclass CASCADE;
DROP TABLE IF EXISTS reg_itemclasstype CASCADE;
DROP TABLE IF EXISTS reg_itemhistory CASCADE;
DROP TABLE IF EXISTS reg_itemproposed CASCADE;
DROP TABLE IF EXISTS reg_languagecode CASCADE;
DROP TABLE IF EXISTS reg_localization CASCADE;
DROP TABLE IF EXISTS reg_localizationhistory CASCADE;
DROP TABLE IF EXISTS reg_localizationproposed CASCADE;
DROP TABLE IF EXISTS reg_relation CASCADE;
DROP TABLE IF EXISTS reg_relationhistory CASCADE;
DROP TABLE IF EXISTS reg_relationpredicate CASCADE;
DROP TABLE IF EXISTS reg_relationproposed CASCADE;
DROP TABLE IF EXISTS reg_role CASCADE;
DROP TABLE IF EXISTS reg_status CASCADE;
DROP TABLE IF EXISTS reg_statusgroup CASCADE;
DROP TABLE IF EXISTS reg_statuslocalization CASCADE;
DROP TABLE IF EXISTS reg_user CASCADE;
DROP TABLE IF EXISTS reg_user_reg_group_mapping CASCADE;


/* Create Tables */

CREATE TABLE reg_action
(
	uuid VARCHAR(50) NOT NULL,
	LABEL VARCHAR(512) NOT NULL,
	reg_user VARCHAR(50) NOT NULL,
	reg_item_registry VARCHAR(50),
	reg_item_register VARCHAR(50),
	reg_status VARCHAR(50) NOT NULL,
	submitted_by VARCHAR(50),
	approved_by VARCHAR(50),
	rejected_by VARCHAR(50),
	published_by VARCHAR(50),
	change_request TEXT,
	changelog TEXT,
	reject_message TEXT,
	changes_implemented BOOLEAN DEFAULT FALSE,
	issue_tracker_link VARCHAR(512),
	insertdate TIMESTAMP WITHOUT TIME ZONE NOT NULL   DEFAULT now(),
	editdate TIMESTAMP WITHOUT TIME ZONE
);

CREATE TABLE reg_field
(
	uuid VARCHAR(50) NOT NULL,
	localid VARCHAR(200) NOT NULL,
	reg_fieldtype VARCHAR(50) NOT NULL,
	istitle BOOLEAN DEFAULT FALSE,
	reg_itemclass_reference VARCHAR(50),
	reg_role_reference VARCHAR(50),
	reg_status VARCHAR(50) NOT NULL,
	insertdate TIMESTAMP WITHOUT TIME ZONE NOT NULL   DEFAULT now(),
	editdate TIMESTAMP WITHOUT TIME ZONE
);

CREATE TABLE reg_fieldmapping
(
	uuid VARCHAR(50) NOT NULL,
	reg_field VARCHAR(50) NOT NULL,
	reg_itemclass VARCHAR(50) NOT NULL,
	listorder INTEGER NOT NULL,
	tablevisible BOOLEAN NOT NULL DEFAULT FALSE,
	reg_status VARCHAR(50) NOT NULL,
	required BOOLEAN NOT NULL DEFAULT FALSE,
	hidden BOOLEAN NOT NULL DEFAULT FALSE,
	multivalue BOOLEAN NOT NULL DEFAULT FALSE,
	insertdate TIMESTAMP WITHOUT TIME ZONE NOT NULL   DEFAULT now(),
	editdate TIMESTAMP WITHOUT TIME ZONE,
	hashref BOOLEAN DEFAULT FALSE
);

CREATE TABLE reg_fieldtype
(
	uuid VARCHAR(50) NOT NULL,
	localid VARCHAR(200) NOT NULL,
	insertdate TIMESTAMP WITHOUT TIME ZONE NOT NULL   DEFAULT now(),
	editdate TIMESTAMP WITHOUT TIME ZONE
);

CREATE TABLE reg_group
(
	uuid VARCHAR(50) NOT NULL,
	localid VARCHAR(200) NOT NULL ,
	NAME VARCHAR(350) NOT NULL,
	email VARCHAR(350),
	website VARCHAR(350),
	insertdate TIMESTAMP WITHOUT TIME ZONE NOT NULL   DEFAULT now(),
	editdate TIMESTAMP WITHOUT TIME ZONE
);

CREATE TABLE reg_item
(
	uuid VARCHAR(50) NOT NULL,
	localid VARCHAR(300) NOT NULL,
	reg_itemclass VARCHAR(50) NOT NULL,
	reg_status VARCHAR(50) NOT NULL,
	reg_action VARCHAR(50),
	EXTERNAL BOOLEAN DEFAULT FALSE,
	currentversion INTEGER,
	insertdate TIMESTAMP WITHOUT TIME ZONE NOT NULL   DEFAULT now(),
	editdate TIMESTAMP WITHOUT TIME ZONE,
	reg_user VARCHAR(50) NOT NULL,
	ror_export BOOLEAN DEFAULT FALSE
);

CREATE TABLE reg_item_reg_group_reg_role_mapping
(
	uuid VARCHAR(50) NOT NULL,
	reg_item VARCHAR(50) NOT NULL,
	reg_group VARCHAR(50) NOT NULL,
	reg_role VARCHAR(50) NOT NULL,
	insertdate TIMESTAMP WITHOUT TIME ZONE NOT NULL   DEFAULT now(),
	editdate TIMESTAMP WITHOUT TIME ZONE
);

CREATE TABLE reg_itemhistory_reg_group_reg_role_mapping
(
	uuid VARCHAR(50) NOT NULL,
	reg_itemhistory VARCHAR(50) NOT NULL,
	reg_group VARCHAR(50) NOT NULL,
	reg_role VARCHAR(50) NOT NULL,
	insertdate TIMESTAMP WITHOUT TIME ZONE NOT NULL   DEFAULT now(),
	editdate TIMESTAMP WITHOUT TIME ZONE
);

CREATE TABLE reg_itemproposed_reg_group_reg_role_mapping
(
	uuid VARCHAR(50) NOT NULL,
	reg_itemproposed VARCHAR(50) NOT NULL,
	reg_group VARCHAR(50) NOT NULL,
	reg_role VARCHAR(50) NOT NULL,
	reg_item_reg_group_reg_role_mapping_reference VARCHAR(50),
	insertdate TIMESTAMP WITHOUT TIME ZONE NOT NULL   DEFAULT now(),
	editdate TIMESTAMP WITHOUT TIME ZONE
);

CREATE TABLE reg_itemclass
(
	uuid VARCHAR(50) NOT NULL,
	localid VARCHAR(200) NOT NULL,
	baseuri VARCHAR(500),
	systemitem BOOLEAN DEFAULT FALSE,
	active BOOLEAN DEFAULT TRUE,
	reg_itemclass_parent VARCHAR(50),
	reg_itemclasstype VARCHAR(50) NOT NULL,
	reg_status VARCHAR(50) NOT NULL,
	dataprocedureorder INTEGER NOT NULL,
	insertdate TIMESTAMP WITHOUT TIME ZONE NOT NULL   DEFAULT now(),
	editdate TIMESTAMP WITHOUT TIME ZONE
);

CREATE TABLE reg_itemclasstype
(
	uuid VARCHAR(50) NOT NULL,
	localid VARCHAR(200),
	insertdate TIMESTAMP WITHOUT TIME ZONE NOT NULL   DEFAULT now(),
	editdate TIMESTAMP WITHOUT TIME ZONE
);

CREATE TABLE reg_itemhistory
(
	uuid VARCHAR(50) NOT NULL,
	localid VARCHAR(300) NOT NULL,
	reg_itemclass VARCHAR(50) NOT NULL,
	reg_item_reference VARCHAR(50) NOT NULL,
	versionnumber INTEGER NOT NULL,
	EXTERNAL BOOLEAN DEFAULT FALSE,
	insertdate TIMESTAMP WITHOUT TIME ZONE NOT NULL   DEFAULT now(),
	editdate TIMESTAMP WITHOUT TIME ZONE,
	reg_user VARCHAR(50) NOT NULL,
	reg_action VARCHAR(50),
	ror_export BOOLEAN DEFAULT FALSE,
	reg_status VARCHAR(50) NOT NULL
);

CREATE TABLE reg_itemproposed
(
	uuid VARCHAR(50) NOT NULL,
	reg_item_reference VARCHAR(50),
	EXTERNAL BOOLEAN DEFAULT FALSE,
	localid VARCHAR(300) NOT NULL,
	reg_status VARCHAR(50) NOT NULL,
	reg_itemclass VARCHAR(50) NOT NULL,
	reg_action VARCHAR(50) NOT NULL,
	insertdate TIMESTAMP WITHOUT TIME ZONE NOT NULL   DEFAULT now(),
	editdate TIMESTAMP WITHOUT TIME ZONE,
	reg_user VARCHAR(50) NOT NULL,
	ror_export BOOLEAN DEFAULT FALSE
);

CREATE TABLE reg_languagecode
(
	uuid VARCHAR(50) NOT NULL,
	LABEL VARCHAR(50) NOT NULL,
	iso6391code VARCHAR(10) NOT NULL,
	iso6392code VARCHAR(10),
	masterlanguage BOOLEAN DEFAULT FALSE,
	active BOOLEAN DEFAULT TRUE,
	insertdate TIMESTAMP WITHOUT TIME ZONE NOT NULL   DEFAULT now(),
	editdate TIMESTAMP WITHOUT TIME ZONE
);

CREATE TABLE reg_localization
(
	uuid VARCHAR(50) NOT NULL,
	reg_languagecode VARCHAR(50) NOT NULL,
	reg_item VARCHAR(50),
	reg_field VARCHAR(50),
	field_value_index INTEGER NOT NULL,
	VALUE TEXT,
	href VARCHAR(1024),
    reg_action VARCHAR(50),
	reg_relation_reference VARCHAR(50),
	insertdate TIMESTAMP WITHOUT TIME ZONE NOT NULL   DEFAULT now(),
	editdate TIMESTAMP WITHOUT TIME ZONE
);

CREATE TABLE reg_localizationhistory
(
	uuid VARCHAR(50) NOT NULL,
	reg_languagecode VARCHAR(50) NOT NULL,
	reg_itemhistory VARCHAR(50),
	reg_field VARCHAR(50),
	field_value_index INTEGER NOT NULL,
	VALUE TEXT,
	href VARCHAR(1024),
    reg_action VARCHAR(50),
	reg_relationhistory_reference VARCHAR(50),
	insertdate TIMESTAMP WITHOUT TIME ZONE NOT NULL   DEFAULT now(),
	editdate TIMESTAMP WITHOUT TIME ZONE
);

CREATE TABLE reg_localizationproposed
(
	uuid VARCHAR(50) NOT NULL,
	reg_localization_reference VARCHAR(50),
	reg_languagecode VARCHAR(50) NOT NULL,
	reg_itemproposed VARCHAR(50) NOT NULL,
	reg_field VARCHAR(50) NOT NULL,
	field_value_index INTEGER NOT NULL,
	VALUE TEXT,
	href VARCHAR(1024),
    reg_action VARCHAR(50),
	reg_relationproposed_reference VARCHAR(50),
	insertdate TIMESTAMP WITHOUT TIME ZONE NOT NULL   DEFAULT now(),
	editdate TIMESTAMP WITHOUT TIME ZONE
);

CREATE TABLE reg_relation
(
	uuid VARCHAR(50) NOT NULL,
	reg_item_subject VARCHAR(50) NOT NULL,
	reg_relationpredicate VARCHAR(50) NOT NULL,
	reg_item_object VARCHAR(50) NOT NULL,
	insertdate TIMESTAMP WITHOUT TIME ZONE NOT NULL   DEFAULT now(),
	editdate TIMESTAMP WITHOUT TIME ZONE
);

CREATE TABLE reg_relationhistory
(
	uuid VARCHAR(50) NOT NULL,
	reg_item_subject VARCHAR(50),
	reg_itemhistory_subject VARCHAR(50),
	reg_item_object VARCHAR(50),
	reg_itemhistory_object VARCHAR(50),
	reg_relationpredicate VARCHAR(50),
	insertdate TIMESTAMP WITHOUT TIME ZONE NOT NULL DEFAULT now(),
	editdate TIMESTAMP WITHOUT TIME ZONE
);

CREATE TABLE reg_relationpredicate
(
	uuid VARCHAR(50) NOT NULL,
	localid VARCHAR(200) NOT NULL,
	insertdate TIMESTAMP WITHOUT TIME ZONE NOT NULL   DEFAULT now(),
	editdate TIMESTAMP WITHOUT TIME ZONE
);

CREATE TABLE reg_relationproposed
(
	uuid VARCHAR(50) NOT NULL,
	reg_relation_reference VARCHAR(50),
	reg_item_subject VARCHAR(50),
	reg_itemproposed_subject VARCHAR(50),
	reg_item_object VARCHAR(50),
	reg_itemproposed_object VARCHAR(50),
	reg_relationpredicate VARCHAR(50) NOT NULL,
	insertdate TIMESTAMP WITHOUT TIME ZONE NOT NULL   DEFAULT now(),
	editdate TIMESTAMP WITHOUT TIME ZONE
);

CREATE TABLE reg_role
(
	uuid VARCHAR(50) NOT NULL,
	localid VARCHAR(200) NOT NULL,
	NAME VARCHAR(350) NOT NULL,
	insertdate TIMESTAMP WITHOUT TIME ZONE NOT NULL   DEFAULT now(),
	editdate TIMESTAMP WITHOUT TIME ZONE
);

CREATE TABLE reg_status
(
	uuid VARCHAR(50) NOT NULL,
	localid VARCHAR(200) NOT NULL,
	ispublic BOOLEAN NOT NULL DEFAULT TRUE,
	insertdate TIMESTAMP WITHOUT TIME ZONE NOT NULL   DEFAULT now(),
	editdate TIMESTAMP WITHOUT TIME ZONE,
	reg_statusgroup VARCHAR(50) NOT NULL
);

CREATE TABLE reg_statusgroup
(
	uuid VARCHAR(50) NOT NULL,
	localid VARCHAR(200) NOT NULL,
	baseuri VARCHAR(500) NOT NULL,
	EXTERNAL BOOLEAN DEFAULT FALSE,
	insertdate TIMESTAMP WITHOUT TIME ZONE NOT NULL   DEFAULT now(),
	editdate TIMESTAMP WITHOUT TIME ZONE
);

CREATE TABLE reg_statuslocalization
(
	uuid VARCHAR(50) NOT NULL,
	reg_status VARCHAR(50),
	reg_statusgroup VARCHAR(50),
	LABEL VARCHAR(350),
	description TEXT,
	reg_languagecode VARCHAR(50) NOT NULL,
	insertdate TIMESTAMP WITHOUT TIME ZONE NOT NULL   DEFAULT now(),
	editdate TIMESTAMP WITHOUT TIME ZONE
);

CREATE TABLE reg_user
(
	uuid VARCHAR(50) NOT NULL,
	ssoreference VARCHAR(200),
	NAME VARCHAR(350) NOT NULL,
	shiropassword VARCHAR(350),
	shirosalt VARCHAR(350),
	email VARCHAR(350) NOT NULL,
	enabled BOOLEAN DEFAULT TRUE,
	insertdate TIMESTAMP WITHOUT TIME ZONE NOT NULL   DEFAULT now(),
	editdate TIMESTAMP WITHOUT TIME ZONE
);

CREATE TABLE reg_user_reg_group_mapping
(
	uuid VARCHAR(50) NOT NULL,
	reg_group VARCHAR(50) NOT NULL,
	reg_user VARCHAR(50) NOT NULL,
	is_groupadmin BOOLEAN DEFAULT FALSE,
	insertdate TIMESTAMP WITHOUT TIME ZONE NOT NULL   DEFAULT now(),
	editdate TIMESTAMP WITHOUT TIME ZONE
);

CREATE TABLE reg_user_codes 
(
	uuid VARCHAR(50) NOT NULL,
	reg_user VARCHAR(50) NOT NULL,
	code VARCHAR(80),
	action VARCHAR(50),
	insertdate TIMESTAMP WITHOUT TIME ZONE NOT NULL   DEFAULT now() 
);

/* Create Primary Keys */

ALTER TABLE reg_action ADD CONSTRAINT PK_reg_action
	PRIMARY KEY (uuid);

ALTER TABLE reg_field ADD CONSTRAINT PK_reg_field
	PRIMARY KEY (uuid);
	
ALTER TABLE reg_fieldmapping ADD CONSTRAINT PK_reg_fieldmapping
	PRIMARY KEY (uuid);

ALTER TABLE reg_fieldtype ADD CONSTRAINT PK_reg_fieldtype
	PRIMARY KEY (uuid);

ALTER TABLE reg_group ADD CONSTRAINT PK_reg_group
	PRIMARY KEY (uuid);

ALTER TABLE reg_item_reg_group_reg_role_mapping ADD CONSTRAINT PK_reg_item_reg_group_reg_role_mapping
	PRIMARY KEY (uuid);

ALTER TABLE reg_itemhistory_reg_group_reg_role_mapping ADD CONSTRAINT PK_reg_itemhistory_reg_group_reg_role_mapping
	PRIMARY KEY (uuid);
	
ALTER TABLE reg_itemproposed_reg_group_reg_role_mapping ADD CONSTRAINT PK_reg_itemproposed_reg_group_reg_role_mapping
	PRIMARY KEY (uuid);

ALTER TABLE reg_itemclass ADD CONSTRAINT PK_reg_itemclass
	PRIMARY KEY (uuid);
	
ALTER TABLE reg_item ADD CONSTRAINT PK_reg_item
	PRIMARY KEY (uuid);

ALTER TABLE reg_itemclasstype ADD CONSTRAINT PK_reg_itemclasstype
	PRIMARY KEY (uuid);
	
ALTER TABLE reg_itemhistory ADD CONSTRAINT PK_reg_itemhistory
	PRIMARY KEY (uuid);
	
ALTER TABLE reg_itemproposed ADD CONSTRAINT PK_reg_itemproposed
	PRIMARY KEY (uuid);
	
ALTER TABLE reg_languagecode ADD CONSTRAINT PK_reg_languagecode
	PRIMARY KEY (uuid);
	
ALTER TABLE reg_localization ADD CONSTRAINT PK_reg_localization
	PRIMARY KEY (uuid);
	
ALTER TABLE reg_localizationproposed ADD CONSTRAINT PK_reg_localizationproposed
	PRIMARY KEY (uuid);
	
ALTER TABLE reg_localizationhistory ADD CONSTRAINT PK_reg_localizationhistory
	PRIMARY KEY (uuid);
	
ALTER TABLE reg_relation ADD CONSTRAINT PK_reg_relation
	PRIMARY KEY (uuid);
	
ALTER TABLE reg_relationhistory ADD CONSTRAINT PK_reg_relationhistory
	PRIMARY KEY (uuid);
	
ALTER TABLE reg_relationpredicate ADD CONSTRAINT PK_reg_relationpredicete
	PRIMARY KEY (uuid);
	
ALTER TABLE reg_relationproposed ADD CONSTRAINT PK_reg_relationproposed
	PRIMARY KEY (uuid);
	
ALTER TABLE reg_role ADD CONSTRAINT PK_reg_role
	PRIMARY KEY (uuid);
	
ALTER TABLE reg_status ADD CONSTRAINT PK_reg_status
	PRIMARY KEY (uuid);
	
ALTER TABLE reg_statusgroup ADD CONSTRAINT PK_reg_statusgroup
	PRIMARY KEY (uuid);

ALTER TABLE reg_statuslocalization ADD CONSTRAINT PK_reg_statuslocalization
	PRIMARY KEY (uuid);
	
ALTER TABLE reg_user ADD CONSTRAINT PK_reg_user
	PRIMARY KEY (uuid);
	
ALTER TABLE reg_user_reg_group_mapping ADD CONSTRAINT PK_reg_user_reg_group_mapping
	PRIMARY KEY (uuid);
	
ALTER TABLE reg_user_codes ADD CONSTRAINT PK_reg_user_codes
	PRIMARY KEY (uuid); 
	
/* Create Uniques */

ALTER TABLE ONLY reg_field
    ADD CONSTRAINT localid_unique_reg_field UNIQUE (localid);
	
ALTER TABLE ONLY reg_fieldtype
    ADD CONSTRAINT localid_unique_reg_fieldtype UNIQUE (localid);
	
ALTER TABLE ONLY reg_group
    ADD CONSTRAINT localid_unique_reg_group UNIQUE (localid);
	
ALTER TABLE ONLY reg_itemclass
    ADD CONSTRAINT localid_unique_reg_itemclass UNIQUE (localid);
	
ALTER TABLE ONLY reg_itemclasstype
    ADD CONSTRAINT localid_unique_reg_itemclasstype UNIQUE (localid);
	
ALTER TABLE ONLY reg_languagecode
    ADD CONSTRAINT iso6391code_unique_reg_languagecode UNIQUE (iso6391code);
	
ALTER TABLE ONLY reg_relationpredicate
    ADD CONSTRAINT localid_unique_reg_relationpredicate UNIQUE (localid);
	
ALTER TABLE ONLY reg_role
    ADD CONSTRAINT name_unique_reg_role UNIQUE (localid);

ALTER TABLE ONLY reg_status
    ADD CONSTRAINT name_unique_reg_status UNIQUE (localid);
	
ALTER TABLE ONLY reg_statusgroup
    ADD CONSTRAINT name_unique_reg_statusgroup UNIQUE (localid);

ALTER TABLE ONLY reg_user
    ADD CONSTRAINT email_unique_reg_user UNIQUE (email);


/* Create Foreign Keys */

ALTER TABLE reg_action ADD CONSTRAINT FK_reg_action_reg_user
	FOREIGN KEY (reg_user) REFERENCES reg_user (uuid) ON DELETE NO ACTION ON UPDATE CASCADE;
	
ALTER TABLE reg_action ADD CONSTRAINT FK_reg_action_reg_status
	FOREIGN KEY (reg_status) REFERENCES reg_status (uuid) ON DELETE NO ACTION ON UPDATE CASCADE;
	
ALTER TABLE reg_action ADD CONSTRAINT FK_reg_action_reg_user_approved
	FOREIGN KEY (approved_by) REFERENCES reg_user (uuid) ON DELETE NO ACTION ON UPDATE CASCADE;
	
ALTER TABLE reg_action ADD CONSTRAINT FK_reg_action_reg_user_rejected
	FOREIGN KEY (rejected_by) REFERENCES reg_user (uuid) ON DELETE NO ACTION ON UPDATE CASCADE;
	
ALTER TABLE reg_action ADD CONSTRAINT FK_reg_action_reg_user_published
	FOREIGN KEY (published_by) REFERENCES reg_user (uuid) ON DELETE NO ACTION ON UPDATE CASCADE;
	
ALTER TABLE reg_action ADD CONSTRAINT FK_reg_action_reg_user_submitted
	FOREIGN KEY (submitted_by) REFERENCES reg_user (uuid) ON DELETE NO ACTION ON UPDATE CASCADE;

ALTER TABLE reg_action ADD CONSTRAINT FK_reg_action_reg_item_register
	FOREIGN KEY (reg_item_register) REFERENCES reg_item (uuid) ON DELETE NO ACTION ON UPDATE CASCADE;

ALTER TABLE reg_action ADD CONSTRAINT FK_reg_action_reg_item_regisry
	FOREIGN KEY (reg_item_registry) REFERENCES reg_item (uuid) ON DELETE NO ACTION ON UPDATE CASCADE;	

ALTER TABLE reg_field ADD CONSTRAINT FK_reg_field_reg_fieldtype
	FOREIGN KEY (reg_fieldtype) REFERENCES reg_fieldtype (uuid) ON DELETE NO ACTION ON UPDATE CASCADE;

ALTER TABLE reg_field ADD CONSTRAINT FK_reg_field_reg_itemclass
	FOREIGN KEY (reg_itemclass_reference) REFERENCES reg_itemclass (uuid) ON DELETE NO ACTION ON UPDATE CASCADE;
	
ALTER TABLE reg_field ADD CONSTRAINT FK_reg_field_reg_role
	FOREIGN KEY (reg_role_reference) REFERENCES reg_role (uuid) ON DELETE NO ACTION ON UPDATE CASCADE;

ALTER TABLE reg_field ADD CONSTRAINT FK_reg_field_reg_status
	FOREIGN KEY (reg_status) REFERENCES reg_status (uuid) ON DELETE NO ACTION ON UPDATE CASCADE;

ALTER TABLE reg_fieldmapping ADD CONSTRAINT FK_reg_fieldmapping_reg_field
	FOREIGN KEY (reg_field) REFERENCES reg_field (uuid) ON DELETE NO ACTION ON UPDATE CASCADE;

ALTER TABLE reg_fieldmapping ADD CONSTRAINT FK_reg_fieldmapping_reg_itemclass
	FOREIGN KEY (reg_itemclass) REFERENCES reg_itemclass (uuid) ON DELETE NO ACTION ON UPDATE CASCADE;

ALTER TABLE reg_fieldmapping ADD CONSTRAINT FK_reg_fieldmapping_reg_status
	FOREIGN KEY (reg_status) REFERENCES reg_status (uuid) ON DELETE NO ACTION ON UPDATE CASCADE;

ALTER TABLE reg_item_reg_group_reg_role_mapping ADD CONSTRAINT FK_reg_item_reg_group_reg_role_mapping_reg_group
	FOREIGN KEY (reg_group) REFERENCES reg_group (uuid) ON DELETE NO ACTION ON UPDATE CASCADE;
	
ALTER TABLE reg_item_reg_group_reg_role_mapping ADD CONSTRAINT FK_reg_item_reg_group_reg_role_mapping_reg_item
	FOREIGN KEY (reg_item) REFERENCES reg_item (uuid) ON DELETE NO ACTION ON UPDATE CASCADE;
	
ALTER TABLE reg_item_reg_group_reg_role_mapping ADD CONSTRAINT FK_reg_item_reg_group_reg_role_mapping_reg_role
	FOREIGN KEY (reg_role) REFERENCES reg_role (uuid) ON DELETE NO ACTION ON UPDATE CASCADE;
	
ALTER TABLE reg_itemhistory_reg_group_reg_role_mapping ADD CONSTRAINT FK_reg_itemhistory_reg_group_reg_role_mapping_reg_group
	FOREIGN KEY (reg_group) REFERENCES reg_group (uuid) ON DELETE NO ACTION ON UPDATE CASCADE;
	
ALTER TABLE reg_itemhistory_reg_group_reg_role_mapping ADD CONSTRAINT FK_reg_itemhistory_reg_group_reg_role_mapping_reg_itemhistory
	FOREIGN KEY (reg_itemhistory) REFERENCES reg_itemhistory (uuid) ON DELETE NO ACTION ON UPDATE CASCADE;
	
ALTER TABLE reg_itemhistory_reg_group_reg_role_mapping ADD CONSTRAINT FK_reg_itemhistory_reg_group_reg_role_mapping_reg_role
	FOREIGN KEY (reg_role) REFERENCES reg_role (uuid) ON DELETE NO ACTION ON UPDATE CASCADE;

ALTER TABLE reg_itemproposed_reg_group_reg_role_mapping ADD CONSTRAINT FK_reg_itemproposed_reg_group_reg_role_mapping_reg_group
	FOREIGN KEY (reg_group) REFERENCES reg_group (uuid) ON DELETE NO ACTION ON UPDATE CASCADE;
	
ALTER TABLE reg_itemproposed_reg_group_reg_role_mapping ADD CONSTRAINT FK_reg_itemproposed_reg_group_reg_role_mapping_reg_itemproposed
	FOREIGN KEY (reg_itemproposed) REFERENCES reg_itemproposed (uuid) ON DELETE NO ACTION ON UPDATE CASCADE;
	
ALTER TABLE reg_itemproposed_reg_group_reg_role_mapping ADD CONSTRAINT FK_reg_itemproposed_reg_group_reg_role_mapping_reg_role
	FOREIGN KEY (reg_role) REFERENCES reg_role (uuid) ON DELETE NO ACTION ON UPDATE CASCADE;

ALTER TABLE reg_itemproposed_reg_group_reg_role_mapping ADD CONSTRAINT FK_reg_itemproposed_reg_group_reg_role_mapping_reference
	FOREIGN KEY (reg_item_reg_group_reg_role_mapping_reference) REFERENCES reg_item_reg_group_reg_role_mapping (uuid) ON DELETE NO ACTION ON UPDATE CASCADE;	

ALTER TABLE reg_item ADD CONSTRAINT FK_reg_item_reg_itemclass
	FOREIGN KEY (reg_itemclass) REFERENCES reg_itemclass (uuid) ON DELETE NO ACTION ON UPDATE CASCADE;

ALTER TABLE reg_item ADD CONSTRAINT FK_reg_item_reg_status
	FOREIGN KEY (reg_status) REFERENCES reg_status (uuid) ON DELETE NO ACTION ON UPDATE CASCADE;

ALTER TABLE reg_item ADD CONSTRAINT FK_reg_item_reg_user
	FOREIGN KEY (reg_user) REFERENCES reg_user (uuid) ON DELETE NO ACTION ON UPDATE CASCADE;
	
ALTER TABLE reg_item ADD CONSTRAINT FK_reg_item_reg_action
	FOREIGN KEY (reg_action) REFERENCES reg_action (uuid) ON DELETE NO ACTION ON UPDATE CASCADE;

ALTER TABLE reg_itemclass ADD CONSTRAINT FK_reg_itemclass_reg_itemclass
	FOREIGN KEY (reg_itemclass_parent) REFERENCES reg_itemclass (uuid) ON DELETE NO ACTION ON UPDATE CASCADE;

ALTER TABLE reg_itemclass ADD CONSTRAINT FK_reg_itemclass_reg_itemclasstype
	FOREIGN KEY (reg_itemclasstype) REFERENCES reg_itemclasstype (uuid) ON DELETE NO ACTION ON UPDATE CASCADE;

ALTER TABLE reg_itemclass ADD CONSTRAINT FK_reg_itemclass_reg_status
	FOREIGN KEY (reg_status) REFERENCES reg_status (uuid) ON DELETE NO ACTION ON UPDATE CASCADE;

ALTER TABLE reg_itemhistory ADD CONSTRAINT FK_reg_itemhistory_reg_item
	FOREIGN KEY (reg_item_reference) REFERENCES reg_item (uuid) ON DELETE NO ACTION ON UPDATE CASCADE;

ALTER TABLE reg_itemhistory ADD CONSTRAINT FK_reg_itemhistory_reg_itemclass
	FOREIGN KEY (reg_itemclass) REFERENCES reg_itemclass (uuid) ON DELETE NO ACTION ON UPDATE CASCADE;

ALTER TABLE reg_itemhistory ADD CONSTRAINT FK_reg_itemhistory_reg_user
	FOREIGN KEY (reg_user) REFERENCES reg_user (uuid) ON DELETE NO ACTION ON UPDATE CASCADE;
	
ALTER TABLE reg_itemhistory ADD CONSTRAINT FK_reg_itemhistory_reg_action
	FOREIGN KEY (reg_action) REFERENCES reg_action (uuid) ON DELETE NO ACTION ON UPDATE CASCADE;
	
ALTER TABLE reg_itemhistory ADD CONSTRAINT FK_reg_itemhistory_reg_status
	FOREIGN KEY (reg_status) REFERENCES reg_status (uuid) ON DELETE NO ACTION ON UPDATE CASCADE;

ALTER TABLE reg_itemproposed ADD CONSTRAINT FK_reg_itemproposed_reg_item
	FOREIGN KEY (reg_item_reference) REFERENCES reg_item (uuid) ON DELETE NO ACTION ON UPDATE CASCADE;

ALTER TABLE reg_itemproposed ADD CONSTRAINT FK_reg_itemproposed_reg_itemclass
	FOREIGN KEY (reg_itemclass) REFERENCES reg_itemclass (uuid) ON DELETE NO ACTION ON UPDATE CASCADE;

ALTER TABLE reg_itemproposed ADD CONSTRAINT FK_reg_itemproposed_reg_status
	FOREIGN KEY (reg_status) REFERENCES reg_status (uuid) ON DELETE NO ACTION ON UPDATE CASCADE;

ALTER TABLE reg_itemproposed ADD CONSTRAINT FK_reg_itemproposed_reg_user
	FOREIGN KEY (reg_user) REFERENCES reg_user (uuid) ON DELETE NO ACTION ON UPDATE CASCADE;

ALTER TABLE reg_itemproposed ADD CONSTRAINT FK_reg_itemproposed_reg_action
	FOREIGN KEY (reg_action) REFERENCES reg_action (uuid) ON DELETE NO ACTION ON UPDATE CASCADE;

ALTER TABLE reg_localization ADD CONSTRAINT FK_reg_localization_reg_field
	FOREIGN KEY (reg_field) REFERENCES reg_field (uuid) ON DELETE NO ACTION ON UPDATE CASCADE;

ALTER TABLE reg_localization ADD CONSTRAINT FK_reg_localization_reg_item
	FOREIGN KEY (reg_item) REFERENCES reg_item (uuid) ON DELETE NO ACTION ON UPDATE CASCADE;

ALTER TABLE reg_localization ADD CONSTRAINT FK_reg_localization_reg_languagecode
	FOREIGN KEY (reg_languagecode) REFERENCES reg_languagecode (uuid) ON DELETE NO ACTION ON UPDATE CASCADE;

ALTER TABLE reg_localization ADD CONSTRAINT FK_reg_localization_reg_relation
	FOREIGN KEY (reg_relation_reference) REFERENCES reg_relation (uuid) ON DELETE NO ACTION ON UPDATE CASCADE;

ALTER TABLE reg_localization ADD CONSTRAINT FK_reg_localization_reg_action
	FOREIGN KEY (reg_action) REFERENCES reg_action (uuid) ON DELETE NO ACTION ON UPDATE CASCADE;
    
ALTER TABLE reg_localizationhistory ADD CONSTRAINT FK_reg_localizationhistory_reg_field
	FOREIGN KEY (reg_field) REFERENCES reg_field (uuid) ON DELETE NO ACTION ON UPDATE CASCADE;
    
ALTER TABLE reg_localizationhistory ADD CONSTRAINT FK_reg_localizationhistory_reg_itemhistory
	FOREIGN KEY (reg_itemhistory) REFERENCES reg_itemhistory (uuid) ON DELETE NO ACTION ON UPDATE CASCADE;

ALTER TABLE reg_localizationhistory ADD CONSTRAINT FK_reg_localizationhistory_reg_languagecode
	FOREIGN KEY (reg_languagecode) REFERENCES reg_languagecode (uuid) ON DELETE NO ACTION ON UPDATE CASCADE;

ALTER TABLE reg_localizationhistory ADD CONSTRAINT FK_reg_localizationhistory_reg_relationhistory
	FOREIGN KEY (reg_relationhistory_reference) REFERENCES reg_relationhistory (uuid) ON DELETE NO ACTION ON UPDATE CASCADE;
    
ALTER TABLE reg_localizationhistory ADD CONSTRAINT FK_reg_localizationhistory_reg_action
	FOREIGN KEY (reg_action) REFERENCES reg_action (uuid) ON DELETE NO ACTION ON UPDATE CASCADE;

ALTER TABLE reg_localizationproposed ADD CONSTRAINT FK_reg_localizationproposed_reg_field
	FOREIGN KEY (reg_field) REFERENCES reg_field (uuid) ON DELETE NO ACTION ON UPDATE CASCADE;

ALTER TABLE reg_localizationproposed ADD CONSTRAINT FK_reg_localizationproposed_reg_itemproposed
	FOREIGN KEY (reg_itemproposed) REFERENCES reg_itemproposed (uuid) ON DELETE NO ACTION ON UPDATE CASCADE;

ALTER TABLE reg_localizationproposed ADD CONSTRAINT FK_reg_localizationproposed_reg_languagecode
	FOREIGN KEY (reg_languagecode) REFERENCES reg_languagecode (uuid) ON DELETE NO ACTION ON UPDATE CASCADE;

ALTER TABLE reg_localizationproposed ADD CONSTRAINT FK_reg_localizationproposed_reg_relationproposed
	FOREIGN KEY (reg_relationproposed_reference) REFERENCES reg_relationproposed (uuid) ON DELETE NO ACTION ON UPDATE CASCADE;

ALTER TABLE reg_localizationproposed ADD CONSTRAINT FK_reg_localizationproposed_reg_localization
	FOREIGN KEY (reg_localization_reference) REFERENCES reg_localization (uuid) ON DELETE NO ACTION ON UPDATE CASCADE;

ALTER TABLE reg_localizationproposed ADD CONSTRAINT FK_reg_localizationproposed_reg_action
	FOREIGN KEY (reg_action) REFERENCES reg_action (uuid) ON DELETE NO ACTION ON UPDATE CASCADE;

ALTER TABLE reg_relation ADD CONSTRAINT FK_reg_relation_reg_item_object
	FOREIGN KEY (reg_item_object) REFERENCES reg_item (uuid) ON DELETE NO ACTION ON UPDATE CASCADE;

ALTER TABLE reg_relation ADD CONSTRAINT FK_reg_relation_reg_item_subject
	FOREIGN KEY (reg_item_subject) REFERENCES reg_item (uuid) ON DELETE NO ACTION ON UPDATE CASCADE;

ALTER TABLE reg_relation ADD CONSTRAINT FK_reg_relation_reg_relationpredicate
	FOREIGN KEY (reg_relationpredicate) REFERENCES reg_relationpredicate (uuid) ON DELETE NO ACTION ON UPDATE CASCADE;

ALTER TABLE reg_relationhistory ADD CONSTRAINT FK_reg_relationhistory_reg_item_object
	FOREIGN KEY (reg_item_object) REFERENCES reg_item (uuid) ON DELETE NO ACTION ON UPDATE CASCADE;

ALTER TABLE reg_relationhistory ADD CONSTRAINT FK_reg_relationhistory_reg_item_subject
	FOREIGN KEY (reg_item_subject) REFERENCES reg_item (uuid) ON DELETE NO ACTION ON UPDATE CASCADE;

ALTER TABLE reg_relationhistory ADD CONSTRAINT FK_reg_relationhistory_reg_itemhistory_object
	FOREIGN KEY (reg_itemhistory_object) REFERENCES reg_itemhistory (uuid) ON DELETE NO ACTION ON UPDATE CASCADE;

ALTER TABLE reg_relationhistory ADD CONSTRAINT FK_reg_relationhistory_reg_itemhistory_subject
	FOREIGN KEY (reg_itemhistory_subject) REFERENCES reg_itemhistory (uuid) ON DELETE NO ACTION ON UPDATE CASCADE;

ALTER TABLE reg_relationhistory ADD CONSTRAINT FK_reg_relationhistory_reg_relationpredicate
	FOREIGN KEY (reg_relationpredicate) REFERENCES reg_relationpredicate (uuid) ON DELETE NO ACTION ON UPDATE CASCADE;

ALTER TABLE reg_relationproposed ADD CONSTRAINT FK_reg_relationproposed_reg_item_object
	FOREIGN KEY (reg_item_object) REFERENCES reg_item (uuid) ON DELETE NO ACTION ON UPDATE CASCADE;

ALTER TABLE reg_relationproposed ADD CONSTRAINT FK_reg_relationproposed_reg_item_subject
	FOREIGN KEY (reg_item_subject) REFERENCES reg_item (uuid) ON DELETE NO ACTION ON UPDATE CASCADE;

ALTER TABLE reg_relationproposed ADD CONSTRAINT FK_reg_relationproposed_reg_itemproposed_object
	FOREIGN KEY (reg_itemproposed_object) REFERENCES reg_itemproposed (uuid) ON DELETE NO ACTION ON UPDATE CASCADE;

ALTER TABLE reg_relationproposed ADD CONSTRAINT FK_reg_relationproposed_reg_itemproposed_subject
	FOREIGN KEY (reg_itemproposed_subject) REFERENCES reg_itemproposed (uuid) ON DELETE NO ACTION ON UPDATE CASCADE;

ALTER TABLE reg_relationproposed ADD CONSTRAINT FK_reg_relationproposed_reg_relationpredicate
	FOREIGN KEY (reg_relationpredicate) REFERENCES reg_relationpredicate (uuid) ON DELETE NO ACTION ON UPDATE CASCADE;
	
ALTER TABLE reg_relationproposed ADD CONSTRAINT FK_reg_relationproposed_reg_relation
	FOREIGN KEY (reg_relation_reference) REFERENCES reg_relation (uuid) ON DELETE NO ACTION ON UPDATE CASCADE;

ALTER TABLE reg_status ADD CONSTRAINT FK_reg_status_reg_statusgroup
	FOREIGN KEY (reg_statusgroup) REFERENCES reg_statusgroup (uuid) ON DELETE NO ACTION ON UPDATE CASCADE;

ALTER TABLE reg_statuslocalization ADD CONSTRAINT FK_reg_statuslocalization_reg_languagecode
	FOREIGN KEY (reg_languagecode) REFERENCES reg_languagecode (uuid) ON DELETE NO ACTION ON UPDATE CASCADE;

ALTER TABLE reg_statuslocalization ADD CONSTRAINT FK_reg_statuslocalization_reg_status
	FOREIGN KEY (reg_status) REFERENCES reg_status (uuid) ON DELETE NO ACTION ON UPDATE CASCADE;

ALTER TABLE reg_statuslocalization ADD CONSTRAINT FK_reg_statuslocalization_reg_statusgroup
	FOREIGN KEY (reg_statusgroup) REFERENCES reg_statusgroup (uuid) ON DELETE NO ACTION ON UPDATE CASCADE;

ALTER TABLE reg_user_reg_group_mapping ADD CONSTRAINT FK_reg_user_reg_group_mapping_reg_group
	FOREIGN KEY (reg_group) REFERENCES reg_group (uuid) ON DELETE NO ACTION ON UPDATE CASCADE;

ALTER TABLE reg_user_reg_group_mapping ADD CONSTRAINT FK_reg_user_reg_group_mapping_reg_user
	FOREIGN KEY (reg_user) REFERENCES reg_user (uuid) ON DELETE NO ACTION ON UPDATE CASCADE;

ALTER TABLE reg_user_codes ADD CONSTRAINT FK_reg_user_code_reg_user 
	FOREIGN KEY (reg_user) REFERENCES reg_user (uuid) ON DELETE NO ACTION ON UPDATE CASCADE;
	
/* Create functions and triggers */	
	
CREATE OR REPLACE FUNCTION update_date()	
RETURNS TRIGGER AS $$
BEGIN
    NEW.editdate = now();
    RETURN NEW;	
END;
$$ LANGUAGE 'plpgsql';

CREATE TRIGGER TRG_reg_action_editdate
BEFORE UPDATE ON reg_action
FOR EACH ROW EXECUTE PROCEDURE update_date();;


CREATE OR REPLACE FUNCTION update_date()	
RETURNS TRIGGER AS $$
BEGIN
    NEW.editdate = now();
    RETURN NEW;	
END;
$$ LANGUAGE 'plpgsql';

CREATE TRIGGER TRG_reg_field_editdate
BEFORE UPDATE ON reg_field
FOR EACH ROW EXECUTE PROCEDURE update_date();;


CREATE OR REPLACE FUNCTION update_date()	
RETURNS TRIGGER AS $$
BEGIN
    NEW.editdate = now();
    RETURN NEW;	
END;
$$ LANGUAGE 'plpgsql';

CREATE TRIGGER TRG_reg_fieldmapping_editdate
BEFORE UPDATE ON reg_fieldmapping
FOR EACH ROW EXECUTE PROCEDURE update_date();;


CREATE OR REPLACE FUNCTION update_date()	
RETURNS TRIGGER AS $$
BEGIN
    NEW.editdate = now();
    RETURN NEW;	
END;
$$ LANGUAGE 'plpgsql';

CREATE TRIGGER TRG_reg_fieldtype_editdate
BEFORE UPDATE ON reg_fieldtype
FOR EACH ROW EXECUTE PROCEDURE update_date();;


CREATE OR REPLACE FUNCTION update_date()	
RETURNS TRIGGER AS $$
BEGIN
    NEW.editdate = now();
    RETURN NEW;	
END;
$$ LANGUAGE 'plpgsql';

CREATE TRIGGER TRG_reg_group_editdate
BEFORE UPDATE ON reg_group
FOR EACH ROW EXECUTE PROCEDURE update_date();;


CREATE OR REPLACE FUNCTION update_date()	
RETURNS TRIGGER AS $$
BEGIN
    NEW.editdate = now();
    RETURN NEW;	
END;
$$ LANGUAGE 'plpgsql';

CREATE TRIGGER TRG_reg_item_reg_group_reg_role_mapping_editdate
BEFORE UPDATE ON reg_item_reg_group_reg_role_mapping
FOR EACH ROW EXECUTE PROCEDURE update_date();;

CREATE OR REPLACE FUNCTION update_date()	
RETURNS TRIGGER AS $$
BEGIN
    NEW.editdate = now();
    RETURN NEW;	
END;
$$ LANGUAGE 'plpgsql';

CREATE TRIGGER TRG_reg_itemproposed_reg_group_reg_role_mapping_editdate
BEFORE UPDATE ON reg_itemproposed_reg_group_reg_role_mapping
FOR EACH ROW EXECUTE PROCEDURE update_date();;

CREATE OR REPLACE FUNCTION update_date()	
RETURNS TRIGGER AS $$
BEGIN
    NEW.editdate = now();
    RETURN NEW;	
END;
$$ LANGUAGE 'plpgsql';

CREATE TRIGGER TRG_reg_item_editdate
BEFORE UPDATE ON reg_item
FOR EACH ROW EXECUTE PROCEDURE update_date();;


CREATE OR REPLACE FUNCTION update_date()	
RETURNS TRIGGER AS $$
BEGIN
    NEW.editdate = now();
    RETURN NEW;	
END;
$$ LANGUAGE 'plpgsql';

CREATE TRIGGER TRG_reg_itemclass_editdate
BEFORE UPDATE ON reg_itemclass
FOR EACH ROW EXECUTE PROCEDURE update_date();;


CREATE OR REPLACE FUNCTION update_date()	
RETURNS TRIGGER AS $$
BEGIN
    NEW.editdate = now();
    RETURN NEW;	
END;
$$ LANGUAGE 'plpgsql';

CREATE TRIGGER TRG_reg_itemclasstype_editdate
BEFORE UPDATE ON reg_itemclasstype
FOR EACH ROW EXECUTE PROCEDURE update_date();;


CREATE OR REPLACE FUNCTION update_date()	
RETURNS TRIGGER AS $$
BEGIN
    NEW.editdate = now();
    RETURN NEW;	
END;
$$ LANGUAGE 'plpgsql';

CREATE TRIGGER TRG_reg_itemhistory_editdate
BEFORE UPDATE ON reg_itemhistory
FOR EACH ROW EXECUTE PROCEDURE update_date();;


CREATE OR REPLACE FUNCTION update_date()	
RETURNS TRIGGER AS $$
BEGIN
    NEW.editdate = now();
    RETURN NEW;	
END;
$$ LANGUAGE 'plpgsql';

CREATE TRIGGER TRG_reg_itemproposed_editdate
BEFORE UPDATE ON reg_itemproposed
FOR EACH ROW EXECUTE PROCEDURE update_date();;


CREATE OR REPLACE FUNCTION update_date()	
RETURNS TRIGGER AS $$
BEGIN
    NEW.editdate = now();
    RETURN NEW;	
END;
$$ LANGUAGE 'plpgsql';

CREATE TRIGGER TRG_reg_languagecode_editdate
BEFORE UPDATE ON reg_languagecode
FOR EACH ROW EXECUTE PROCEDURE update_date();;


CREATE OR REPLACE FUNCTION update_date()	
RETURNS TRIGGER AS $$
BEGIN
    NEW.editdate = now();
    RETURN NEW;	
END;
$$ LANGUAGE 'plpgsql';

CREATE TRIGGER TRG_reg_localization_editdate
BEFORE UPDATE ON reg_localization
FOR EACH ROW EXECUTE PROCEDURE update_date();;


CREATE OR REPLACE FUNCTION update_date()	
RETURNS TRIGGER AS $$
BEGIN
    NEW.editdate = now();
    RETURN NEW;	
END;
$$ LANGUAGE 'plpgsql';

CREATE TRIGGER TRG_reg_localizationhistory_editdate
BEFORE UPDATE ON reg_localizationhistory
FOR EACH ROW EXECUTE PROCEDURE update_date();;


CREATE OR REPLACE FUNCTION update_date()	
RETURNS TRIGGER AS $$
BEGIN
    NEW.editdate = now();
    RETURN NEW;	
END;
$$ LANGUAGE 'plpgsql';

CREATE TRIGGER TRG_reg_localizationproposed_editdate
BEFORE UPDATE ON reg_localizationproposed
FOR EACH ROW EXECUTE PROCEDURE update_date();;


CREATE OR REPLACE FUNCTION update_date()	
RETURNS TRIGGER AS $$
BEGIN
    NEW.editdate = now();
    RETURN NEW;	
END;
$$ LANGUAGE 'plpgsql';

CREATE TRIGGER TRG_reg_relation_editdate
BEFORE UPDATE ON reg_relation
FOR EACH ROW EXECUTE PROCEDURE update_date();;


CREATE OR REPLACE FUNCTION update_date()	
RETURNS TRIGGER AS $$
BEGIN
    NEW.editdate = now();
    RETURN NEW;	
END;
$$ LANGUAGE 'plpgsql';

CREATE TRIGGER TRG_reg_relationhistory_editdate
BEFORE UPDATE ON reg_relationhistory
FOR EACH ROW EXECUTE PROCEDURE update_date();;


CREATE OR REPLACE FUNCTION update_date()	
RETURNS TRIGGER AS $$
BEGIN
    NEW.editdate = now();
    RETURN NEW;	
END;
$$ LANGUAGE 'plpgsql';

CREATE TRIGGER TRG_reg_relationpredicate_editdate
BEFORE UPDATE ON reg_relationpredicate
FOR EACH ROW EXECUTE PROCEDURE update_date();;


CREATE OR REPLACE FUNCTION update_date()	
RETURNS TRIGGER AS $$
BEGIN
    NEW.editdate = now();
    RETURN NEW;	
END;
$$ LANGUAGE 'plpgsql';

CREATE TRIGGER TRG_reg_relationproposed_editdate
BEFORE UPDATE ON reg_relationproposed
FOR EACH ROW EXECUTE PROCEDURE update_date();;


CREATE OR REPLACE FUNCTION update_date()	
RETURNS TRIGGER AS $$
BEGIN
    NEW.editdate = now();
    RETURN NEW;	
END;
$$ LANGUAGE 'plpgsql';

CREATE TRIGGER TRG_reg_role_editdate
BEFORE UPDATE ON reg_role
FOR EACH ROW EXECUTE PROCEDURE update_date();;


CREATE OR REPLACE FUNCTION update_date()	
RETURNS TRIGGER AS $$
BEGIN
    NEW.editdate = now();
    RETURN NEW;	
END;
$$ LANGUAGE 'plpgsql';

CREATE TRIGGER TRG_reg_status_editdate
BEFORE UPDATE ON reg_status
FOR EACH ROW EXECUTE PROCEDURE update_date();;


CREATE OR REPLACE FUNCTION update_date()	
RETURNS TRIGGER AS $$
BEGIN
    NEW.editdate = now();
    RETURN NEW;	
END;
$$ LANGUAGE 'plpgsql';

CREATE TRIGGER TRG_reg_statusgroup_editdate
BEFORE UPDATE ON reg_statusgroup
FOR EACH ROW EXECUTE PROCEDURE update_date();;


CREATE OR REPLACE FUNCTION update_date()	
RETURNS TRIGGER AS $$
BEGIN
    NEW.editdate = now();
    RETURN NEW;	
END;
$$ LANGUAGE 'plpgsql';

CREATE TRIGGER TRG_reg_statuslocalization_editdate
BEFORE UPDATE ON reg_statuslocalization
FOR EACH ROW EXECUTE PROCEDURE update_date();;


CREATE OR REPLACE FUNCTION update_date()	
RETURNS TRIGGER AS $$
BEGIN
    NEW.editdate = now();
    RETURN NEW;	
END;
$$ LANGUAGE 'plpgsql';

CREATE TRIGGER TRG_reg_user_editdate
BEFORE UPDATE ON reg_user
FOR EACH ROW EXECUTE PROCEDURE update_date();;


CREATE OR REPLACE FUNCTION update_date()	
RETURNS TRIGGER AS $$
BEGIN
    NEW.editdate = now();
    RETURN NEW;	
END;
$$ LANGUAGE 'plpgsql';

CREATE TRIGGER TRG_reg_user_reg_group_mapping_editdate
BEFORE UPDATE ON reg_user_reg_group_mapping
FOR EACH ROW EXECUTE PROCEDURE update_date();


--reg_languagecode
INSERT INTO reg_languagecode(uuid, label, iso6391code, iso6392code, masterlanguage, active, insertdate, editdate) VALUES ('en', 'english', 'en', 'eng', TRUE, TRUE, '2017-05-11 14:14:05.33835', NULL);
INSERT INTO reg_languagecode(uuid, label, iso6391code, iso6392code, masterlanguage, active, insertdate, editdate) VALUES ('it', 'italiano', 'it', 'ita', FALSE, TRUE, '2017-05-11 14:14:05.33835', NULL);
INSERT INTO reg_languagecode(uuid, label, iso6391code, iso6392code, masterlanguage, active, insertdate, editdate) VALUES ('cs', 'czech', 'cs', 'cze', FALSE, TRUE, '2017-05-11 14:14:05.33835', NULL);
INSERT INTO reg_languagecode(uuid, label, iso6391code, iso6392code, masterlanguage, active, insertdate, editdate) VALUES ('bg', 'bulgarian', 'bg', 'bul', FALSE, TRUE, '2017-05-11 14:14:05.33835', NULL);
INSERT INTO reg_languagecode(uuid, label, iso6391code, iso6392code, masterlanguage, active, insertdate, editdate) VALUES ('da', 'danish', 'da', 'dan', FALSE, TRUE, '2017-05-11 14:14:05.33835', NULL);
INSERT INTO reg_languagecode(uuid, label, iso6391code, iso6392code, masterlanguage, active, insertdate, editdate) VALUES ('de', 'german', 'de', 'ger', FALSE, TRUE, '2017-05-11 14:14:05.33835', NULL);
INSERT INTO reg_languagecode(uuid, label, iso6391code, iso6392code, masterlanguage, active, insertdate, editdate) VALUES ('el', 'greek', 'el', 'gre', FALSE, TRUE, '2017-05-11 14:14:05.33835', NULL);
INSERT INTO reg_languagecode(uuid, label, iso6391code, iso6392code, masterlanguage, active, insertdate, editdate) VALUES ('es', 'spanish', 'es', 'spa', FALSE, TRUE, '2017-05-11 14:14:05.33835', NULL);
INSERT INTO reg_languagecode(uuid, label, iso6391code, iso6392code, masterlanguage, active, insertdate, editdate) VALUES ('et', 'estonian', 'et', 'est', FALSE, TRUE, '2017-05-11 14:14:05.33835', NULL);
INSERT INTO reg_languagecode(uuid, label, iso6391code, iso6392code, masterlanguage, active, insertdate, editdate) VALUES ('fi', 'finnish', 'fi', 'fin', FALSE, TRUE, '2017-05-11 14:14:05.33835', NULL);
INSERT INTO reg_languagecode(uuid, label, iso6391code, iso6392code, masterlanguage, active, insertdate, editdate) VALUES ('fr', 'french', 'fr', 'fra', FALSE, TRUE, '2017-05-11 14:14:05.33835', NULL);
INSERT INTO reg_languagecode(uuid, label, iso6391code, iso6392code, masterlanguage, active, insertdate, editdate) VALUES ('hu', 'hungarian', 'hu', 'hun', FALSE, TRUE, '2017-05-11 14:14:05.33835', NULL);
INSERT INTO reg_languagecode(uuid, label, iso6391code, iso6392code, masterlanguage, active, insertdate, editdate) VALUES ('lt', 'lithuanian', 'lt', 'lit', FALSE, TRUE, '2017-05-11 14:14:05.33835', NULL);
INSERT INTO reg_languagecode(uuid, label, iso6391code, iso6392code, masterlanguage, active, insertdate, editdate) VALUES ('lv', 'latvian', 'lv', 'lav', FALSE, TRUE, '2017-05-11 14:14:05.33835', NULL);
INSERT INTO reg_languagecode(uuid, label, iso6391code, iso6392code, masterlanguage, active, insertdate, editdate) VALUES ('mt', 'maltese', 'mt', 'mlt', FALSE, TRUE, '2017-05-11 14:14:05.33835', NULL);
INSERT INTO reg_languagecode(uuid, label, iso6391code, iso6392code, masterlanguage, active, insertdate, editdate) VALUES ('pl', 'polish', 'pl', 'pol', FALSE, TRUE, '2017-05-11 14:14:05.33835', NULL);
INSERT INTO reg_languagecode(uuid, label, iso6391code, iso6392code, masterlanguage, active, insertdate, editdate) VALUES ('nl', 'dutch', 'nl', 'dut', FALSE, TRUE, '2017-05-11 14:14:05.33835', NULL);
INSERT INTO reg_languagecode(uuid, label, iso6391code, iso6392code, masterlanguage, active, insertdate, editdate) VALUES ('pt', 'portuguese', 'pt', 'por', FALSE, TRUE, '2017-05-11 14:14:05.33835', NULL);
INSERT INTO reg_languagecode(uuid, label, iso6391code, iso6392code, masterlanguage, active, insertdate, editdate) VALUES ('ro', 'romanian', 'ro', 'rum', FALSE, TRUE, '2017-05-11 14:14:05.33835', NULL);
INSERT INTO reg_languagecode(uuid, label, iso6391code, iso6392code, masterlanguage, active, insertdate, editdate) VALUES ('sk', 'slovak', 'sk', 'slo', FALSE, TRUE, '2017-05-11 14:14:05.33835', NULL);
INSERT INTO reg_languagecode(uuid, label, iso6391code, iso6392code, masterlanguage, active, insertdate, editdate) VALUES ('sl', 'slovenian', 'sl', 'slv', FALSE, TRUE, '2017-05-11 14:14:05.33835', NULL);
INSERT INTO reg_languagecode(uuid, label, iso6391code, iso6392code, masterlanguage, active, insertdate, editdate) VALUES ('sv', 'swedish', 'sv', 'swe', FALSE, TRUE, '2017-05-11 14:14:05.33835', NULL);
INSERT INTO reg_languagecode(uuid, label, iso6391code, iso6392code, masterlanguage, active, insertdate, editdate) VALUES ('hr', 'croatian', 'hr', 'hrv', FALSE, TRUE, '2017-05-11 14:14:05.33835', NULL);

--reg_itemclasstype
INSERT INTO reg_itemclasstype(uuid, localid, insertdate, editdate) VALUES ('1', 'registry', '2017-05-11 14:14:05.33835', NULL);
INSERT INTO reg_itemclasstype(uuid, localid, insertdate, editdate) VALUES ('2', 'register', '2017-05-11 14:14:05.33835', NULL);
INSERT INTO reg_itemclasstype(uuid, localid, insertdate, editdate) VALUES ('3', 'item', '2017-05-11 14:14:05.33835', NULL);

--reg_statusgroup
INSERT INTO reg_statusgroup(uuid, localid, baseuri, EXTERNAL, insertdate, editdate) VALUES ('1', 'status', 'http://127.0.0.1:8082/', FALSE, '2017-05-11 14:14:05.33835', NULL );

--reg_status
INSERT INTO reg_status(uuid, localid, ispublic, reg_statusgroup, insertdate, editdate) VALUES ('1', 'valid', TRUE, '1', '2017-05-11 14:14:05.33835', NULL);
INSERT INTO reg_status(uuid, localid, ispublic, reg_statusgroup, insertdate, editdate) VALUES ('2', 'invalid', TRUE, '1', '2017-05-11 14:14:05.33835', NULL);
INSERT INTO reg_status(uuid, localid, ispublic, reg_statusgroup, insertdate, editdate) VALUES ('3', 'superseded', TRUE, '1', '2017-05-11 14:14:05.33835', NULL);
INSERT INTO reg_status(uuid, localid, ispublic, reg_statusgroup, insertdate, editdate) VALUES ('4', 'retired', TRUE, '1', '2017-05-11 14:14:05.33835', NULL);
INSERT INTO reg_status(uuid, localid, ispublic, reg_statusgroup, insertdate, editdate) VALUES ('5', 'draft', FALSE, '1', '2017-05-11 14:14:05.33835', NULL);
INSERT INTO reg_status(uuid, localid, ispublic, reg_statusgroup, insertdate, editdate) VALUES ('6', 'submitted', TRUE, '1', '2017-05-11 14:14:05.33835', NULL);
INSERT INTO reg_status(uuid, localid, ispublic, reg_statusgroup, insertdate, editdate) VALUES ('7', 'needreview', FALSE, '1', '2017-05-11 14:14:05.33835', NULL);
INSERT INTO reg_status(uuid, localid, ispublic, reg_statusgroup, insertdate, editdate) VALUES ('8', 'proposed', FALSE, '1', '2017-05-11 14:14:05.33835', NULL);
INSERT INTO reg_status(uuid, localid, ispublic, reg_statusgroup, insertdate, editdate) VALUES ('9', 'notAccepted', FALSE, '1', '2017-05-11 14:14:05.33835', NULL);
INSERT INTO reg_status(uuid, localid, ispublic, reg_statusgroup, insertdate, editdate) VALUES ('10', 'acceptedWithChanges', FALSE, '1', '2017-05-11 14:14:05.33835', NULL);
INSERT INTO reg_status(uuid, localid, ispublic, reg_statusgroup, insertdate, editdate) VALUES ('11', 'changesCompleted', FALSE, '1', '2017-05-11 14:14:05.33835', NULL);
INSERT INTO reg_status(uuid, localid, ispublic, reg_statusgroup, insertdate, editdate) VALUES ('12', 'accepted', FALSE, '1', '2017-05-11 14:14:05.33835', NULL);
INSERT INTO reg_status(uuid, localid, ispublic, reg_statusgroup, insertdate, editdate) VALUES ('13', 'withdrawn', FALSE, '1', '2017-05-11 14:14:05.33835', NULL);
INSERT INTO reg_status(uuid, localid, ispublic, reg_statusgroup, insertdate, editdate) VALUES ('14', 'published', FALSE, '1', '2017-05-11 14:14:05.33835', NULL);

-- reg_statuslocalization
INSERT INTO public.reg_statuslocalization(uuid, reg_status, reg_statusgroup, "label", description, reg_languagecode, insertdate, editdate)
VALUES('status-ml', NULL, '1', 'Status', '', 'en', '2017-05-11 14:14:05.33835', NULL);

INSERT INTO public.reg_statuslocalization(uuid, reg_status, reg_statusgroup, "label", description, reg_languagecode, insertdate, editdate)
VALUES('valid-ml', '1', NULL, 'Valid', '', 'en', '2017-05-11 14:14:05.33835', NULL);

INSERT INTO public.reg_statuslocalization(uuid, reg_status, reg_statusgroup, "label", description, reg_languagecode, insertdate, editdate)
VALUES('invalid-ml', '2', NULL, 'invalid', '', 'en', '2017-05-11 14:14:05.33835', NULL);

INSERT INTO public.reg_statuslocalization(uuid, reg_status, reg_statusgroup, "label", description, reg_languagecode, insertdate, editdate)
VALUES('superseded-ml', '3', NULL, 'Superseded', '', 'en', '2017-05-11 14:14:05.33835', NULL);

INSERT INTO public.reg_statuslocalization(uuid, reg_status, reg_statusgroup, "label", description, reg_languagecode, insertdate, editdate)
VALUES('retired-ml', '4', NULL, 'Retired', '', 'en', '2017-05-11 14:14:05.33835', NULL);

INSERT INTO public.reg_statuslocalization(uuid, reg_status, reg_statusgroup, "label", description, reg_languagecode, insertdate, editdate)
VALUES('draft-ml', '5', NULL, 'Draft', '', 'en', '2017-05-11 14:14:05.33835', NULL);

INSERT INTO public.reg_statuslocalization(uuid, reg_status, reg_statusgroup, "label", description, reg_languagecode, insertdate, editdate)
VALUES('submitted-ml', '6', NULL, 'Submitted', '', 'en', '2017-05-11 14:14:05.33835', NULL);

INSERT INTO public.reg_statuslocalization(uuid, reg_status, reg_statusgroup, "label", description, reg_languagecode, insertdate, editdate)
VALUES('needreview-ml', '7', NULL, 'Need review', '', 'en', '2017-05-11 14:14:05.33835', NULL);

INSERT INTO public.reg_statuslocalization(uuid, reg_status, reg_statusgroup, "label", description, reg_languagecode, insertdate, editdate)
VALUES('proposed-ml', '8', NULL, 'proposed', '', 'en', '2017-05-11 14:14:05.33835', NULL);

INSERT INTO public.reg_statuslocalization(uuid, reg_status, reg_statusgroup, "label", description, reg_languagecode, insertdate, editdate)
VALUES('notaccepted-ml', '9', NULL, 'Not accepted', '', 'en', '2017-05-11 14:14:05.33835', NULL);

INSERT INTO public.reg_statuslocalization(uuid, reg_status, reg_statusgroup, "label", description, reg_languagecode, insertdate, editdate)
VALUES('acceptedWithChanges-ml', '10', NULL, 'Accepted With Changes', '', 'en', '2017-05-11 14:14:05.33835', NULL);

INSERT INTO public.reg_statuslocalization(uuid, reg_status, reg_statusgroup, "label", description, reg_languagecode, insertdate, editdate)
VALUES('changesCompleted-ml', '11', NULL, 'Changes Completed', '', 'en', '2017-05-11 14:14:05.33835', NULL);

INSERT INTO public.reg_statuslocalization(uuid, reg_status, reg_statusgroup, "label", description, reg_languagecode, insertdate, editdate)
VALUES('accepted-ml', '12', NULL, 'Accepted', '', 'en', '2017-05-11 14:14:05.33835', NULL);

INSERT INTO public.reg_statuslocalization(uuid, reg_status, reg_statusgroup, "label", description, reg_languagecode, insertdate, editdate)
VALUES('withdrawn-ml', '13', NULL, 'Withdrawn', '', 'en', '2017-05-11 14:14:05.33835', NULL);

INSERT INTO public.reg_statuslocalization(uuid, reg_status, reg_statusgroup, "label", description, reg_languagecode, insertdate, editdate)
VALUES('published-ml', '14', NULL, 'Published', '', 'en', '2017-05-11 14:14:05.33835', NULL);



--reg_fieldtype
INSERT INTO reg_fieldtype(uuid, localid, insertdate, editdate) VALUES ('1', 'text', '2017-05-11 14:14:05.33835', NULL);
INSERT INTO reg_fieldtype(uuid, localid, insertdate, editdate) VALUES ('2', 'date', '2017-12-15 10:00:00.33835', NULL);
INSERT INTO reg_fieldtype(uuid, localid, insertdate, editdate) VALUES ('3', 'number', '2017-05-11 14:14:05.33835', NULL);
INSERT INTO reg_fieldtype(uuid, localid, insertdate, editdate) VALUES ('4', 'registry', '2017-05-11 14:14:05.33835', NULL);
INSERT INTO reg_fieldtype(uuid, localid, insertdate, editdate) VALUES ('5', 'register', '2017-05-11 14:14:05.33835', NULL);
INSERT INTO reg_fieldtype(uuid, localid, insertdate, editdate) VALUES ('6', 'collection', '2017-05-11 14:14:05.33835', NULL);
INSERT INTO reg_fieldtype(uuid, localid, insertdate, editdate) VALUES ('7', 'parent', '2017-05-11 14:14:05.33835', NULL);
INSERT INTO reg_fieldtype(uuid, localid, insertdate, editdate) VALUES ('8', 'successor', '2017-05-11 14:14:05.33835', NULL);
INSERT INTO reg_fieldtype(uuid, localid, insertdate, editdate) VALUES ('9', 'predecessor', '2017-05-11 14:14:05.33835', NULL);
INSERT INTO reg_fieldtype(uuid, localid, insertdate, editdate) VALUES ('10', 'relationReference', '2017-05-11 14:14:05.33835', NULL);
INSERT INTO reg_fieldtype(uuid, localid, insertdate, editdate) VALUES ('11', 'longtext', '2017-05-11 14:14:05.33835', NULL);
INSERT INTO reg_fieldtype(uuid, localid, insertdate, editdate) VALUES ('12', 'groupReference', '2017-05-11 14:14:05.33835', NULL);
INSERT INTO reg_fieldtype(uuid, localid, insertdate, editdate) VALUES ('13', 'status', '2017-05-11 14:14:05.33835', NULL);
INSERT INTO reg_fieldtype(uuid, localid, insertdate, editdate) VALUES ('14', 'dateCreation', '2017-05-11 14:14:05.33835', NULL);
INSERT INTO reg_fieldtype(uuid, localid, insertdate, editdate) VALUES ('15', 'dateEdit', '2017-05-11 14:14:05.33835', NULL);

--reg_relationpredicate
INSERT INTO reg_relationpredicate(uuid, localid, insertdate, editdate) VALUES ('1', 'hasRegistry', '2017-05-11 14:14:05.33835', NULL);
INSERT INTO reg_relationpredicate(uuid, localid, insertdate, editdate) VALUES ('2', 'hasRegister', '2017-05-11 14:14:05.33835', NULL);
INSERT INTO reg_relationpredicate(uuid, localid, insertdate, editdate) VALUES ('3', 'hasCollection', '2017-05-11 14:14:05.33835', NULL);
INSERT INTO reg_relationpredicate(uuid, localid, insertdate, editdate) VALUES ('4', 'hasParent', '2017-05-11 14:14:05.33835', NULL);
INSERT INTO reg_relationpredicate(uuid, localid, insertdate, editdate) VALUES ('5', 'hasSuccessor', '2017-05-11 14:14:05.33835', NULL);
INSERT INTO reg_relationpredicate(uuid, localid, insertdate, editdate) VALUES ('6', 'hasPredecessor', '2017-05-11 14:14:05.33835', NULL);
INSERT INTO reg_relationpredicate(uuid, localid, insertdate, editdate) VALUES ('7', 'itemReference', '2017-05-11 14:14:05.33835', NULL);


--reg_role
INSERT INTO reg_role(uuid, localid, name, insertdate, editdate) VALUES ('1', 'registryManager', 'Registry Manager', '2017-05-11 14:14:05.33835', NULL);
INSERT INTO reg_role(uuid, localid, name, insertdate, editdate) VALUES ('2', 'registerManager', 'Register Manager', '2017-05-11 14:14:05.33835', NULL);
INSERT INTO reg_role(uuid, localid, name, insertdate, editdate) VALUES ('3', 'registerOwner', 'Register Owner', '2017-05-11 14:14:05.33835', NULL);
INSERT INTO reg_role(uuid, localid, name, insertdate, editdate) VALUES ('4', 'controlBody', 'Control Body', '2017-05-11 14:14:05.33835', NULL);
INSERT INTO reg_role(uuid, localid, name, insertdate, editdate) VALUES ('5', 'submittingOrganization', 'Submitting Organization', '2017-05-11 14:14:05.33835', NULL);

