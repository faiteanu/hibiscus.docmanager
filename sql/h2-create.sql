CREATE TABLE DOCMANAGER_ACCOUNT (
  id IDENTITY(1),
  institute varchar(255) NOT NULL,
  username varchar(255) NOT NULL,
  name varchar(255) NULL,
  DocumentsPath varchar(255) NULL,
  comment varchar(1000) NULL,
  lastupdate date,
  UNIQUE (id),
  PRIMARY KEY (id)
);

CREATE TABLE DOCMANAGER_DOCUMENT (
  ID IDENTITY(1),
  AccountID int(4) NOT NULL,
  RemoteFolder varchar(255) NULL,
  Title varchar(255) NOT NULL,
  LocalFolder varchar(255) NULL,
  Filename varchar(255) NULL,
  Comment varchar(1000) NULL,
  CreatedOn timestamp,
  DownloadedOn timestamp,
  ReadOn timestamp,
  UNIQUE (id),
  PRIMARY KEY (id)
);

ALTER TABLE DOCMANAGER_DOCUMENT ADD CONSTRAINT fk_account FOREIGN KEY (accountid) REFERENCES DOCMANAGER_ACCOUNT (id) DEFERRABLE;

