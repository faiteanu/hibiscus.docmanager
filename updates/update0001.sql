-- ----------------------------------------------------------------------
-- Erweitert die Tabelle "document" um eine Spalte "RemoteID"
-- ----------------------------------------------------------------------

alter table DOCMANAGER_DOCUMENT add RemoteID varchar(255) NULL;
