-- drop targets of FKs after tables with the FKs
-- this version works only in SQLPlus to drop the DB regardless
-- of tables' existence, that is, keep going if one table isn't there.
-- use -f (for force) with mysql for this effect
whenever sqlerror continue;
drop table download;
drop table track;
drop table lineitem;
drop table product;
drop table invoice;
drop table site_user;
drop table userpass;
drop table userrole;
drop table music_sys_tab;
