CREATE TABLE FLW_EVENT_DEPLOYMENT (ID_ varchar(255) NOT NULL, NAME_ nvarchar(255), CATEGORY_ varchar(255), DEPLOY_TIME_ datetime, TENANT_ID_ varchar(255), PARENT_DEPLOYMENT_ID_ varchar(255), CONSTRAINT PK_FLW_EVENT_DEPLOYMENT PRIMARY KEY (ID_));

CREATE TABLE FLW_EVENT_RESOURCE (ID_ varchar(255) NOT NULL, NAME_ nvarchar(255), DEPLOYMENT_ID_ varchar(255), RESOURCE_BYTES_ varbinary(MAX), CONSTRAINT PK_FLW_EVENT_RESOURCE PRIMARY KEY (ID_));

CREATE TABLE FLW_EVENT_DEFINITION (ID_ varchar(255) NOT NULL, NAME_ nvarchar(255), VERSION_ int, KEY_ varchar(255), CATEGORY_ nvarchar(255), DEPLOYMENT_ID_ varchar(255), TENANT_ID_ varchar(255), RESOURCE_NAME_ nvarchar(255), DESCRIPTION_ nvarchar(255), CONSTRAINT PK_FLW_EVENT_DEFINITION PRIMARY KEY (ID_));

ALTER TABLE FLW_EVENT_RESOURCE
    ADD CONSTRAINT FLW_FK_EVENT_RSRC_DPL FOREIGN KEY (DEPLOYMENT_ID_) REFERENCES FLW_EVENT_DEPLOYMENT (ID_);

CREATE NONCLUSTERED INDEX FLW_IDX_EVENT_RSRC_DPL ON FLW_EVENT_RESOURCE(DEPLOYMENT_ID_);

CREATE UNIQUE NONCLUSTERED INDEX ACT_IDX_EVENT_DEF_UNIQ ON FLW_EVENT_DEFINITION(KEY_, VERSION_, TENANT_ID_);

CREATE TABLE FLW_CHANNEL_DEFINITION (ID_ varchar(255) NOT NULL, NAME_ nvarchar(255), VERSION_ int, KEY_ varchar(255), CATEGORY_ nvarchar(255), TYPE_ varchar(255), IMPLEMENTATION_ varchar(255), DEPLOYMENT_ID_ varchar(255), CREATE_TIME_ datetime, TENANT_ID_ varchar(255), RESOURCE_NAME_ nvarchar(255), DESCRIPTION_ nvarchar(255), CONSTRAINT PK_FLW_CHANNEL_DEFINITION PRIMARY KEY (ID_));

CREATE UNIQUE NONCLUSTERED INDEX ACT_IDX_CHANNEL_DEF_UNIQ ON FLW_CHANNEL_DEFINITION(KEY_, VERSION_, TENANT_ID_);

insert into ACT_GE_PROPERTY
values ('eventregistry.schema.version', '7.2.0.1', 1);
