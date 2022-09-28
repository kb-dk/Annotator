Dokumentation for servicen "Annotator"
======================================

Generel Beskrivelse
-------------------

Annotator er en REST baseret webservice med 3 hovedformål:

1.  læse og skrive kommentarer tilknyttet en resource identificeret ved en URI.
2.  læse og skrive tags tilknyttet en resource identificeret ved en URI.
3.  læse og skrive relationer mellem resourcer. Relationer er implementeret som simple [XLinks v. 11](http://www.w3.org/TR/xlink11/)

Annotator skal i første omgang bruges af luftfotoprojektet samt evt. brevprojektet.  
Annotators relations del, skal implementere objekters komplekse struktur i COP.

1\. line support
----------------

Servicen er en backend service. Der er ingen brugere der taler direkte med annotator.  

*   Services er i produktion på serveren http://{server}.kb.dk
*   Servicen kan testes på følgende URL http://{server}/annotation/http:%2F%2Fwww.kb.dk/tag. Her skal enten svares med en liste af tags, eller "ingen tags"
*   Eksempel på tags kan findes via, http://localhost:8080/annotation/tag\_aerial/?uri=/images/luftfo/2011/maj/luftfoto/object62356

Tekniske Noter
--------------

### Forudsætninger

*   Java minimum version 1.6 (testet med openjdk 1.6.0\_20) - bemærk at her skal bruges et fuldt JDK og ikke kun JRE
*   Apache ant version 1.8.0
*   Oracle 10.g (tests kører mod udvikling2.kb.dk:1521:udvl2, prod kører mod oracledb.kb.dk:1521:prod)

### Udrulning

Hent nyeste version af koden  
svn checkout svn+ssh://code-01.kb.dk/home/svn/Annotator/trunk  

Producer en war fil via kommandoen: mvn install

Hudson Continous integration serveren bygger annotation engine, kan findes her

Opret tabller i databasen  
Sker via Cop2-Backendens oprettelses script.  
Indsæt testdatasæt (2 rækker i tags tabellen)  
Køre sql script: scripts/annotator-tag.sql  

### Konfiguration

#### Logning

Konfiguration af logning via log4j. konfiguration foretages i filen log4j_prod.xml
Backup
------

Der tages ikke backup af services selv, men af de data der gemmes i Oracle. Disse indgår i alm Oracle backup.

Overvågning
-----------

Vigtigste overvågning er på Oracle.  
Der kan etableres Nagios overvågning på http://{server}/annotation/http:%2F%2Fwww.kb.dk/tag
