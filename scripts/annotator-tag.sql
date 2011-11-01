-- Simpelt skript der indsætter et par tags i databasen. Så er der altid noget at overvåge

REM INSERTING into TAG
Insert into TAG (ID,TAG_VALUE,CREATOR,TIMESTAMP,XLINK_TO) values ('persistent-1','Den Sorte Diamant','root',to_timestamp('11-03-11 11:36:22,663000000','RR-MM-DD HH24:MI:SS,FF'),'http://www.kb.dk');
Insert into TAG (ID,TAG_VALUE,CREATOR,TIMESTAMP,XLINK_TO) values ('persistent-2','Bibliotek','root',to_timestamp('11-03-11 11:36:49,853000000','RR-MM-DD HH24:MI:SS,FF'),'http://www.kb.dk');
