create table web_query_test(id int auto_increment primary key not null, col1 varchar(50) null, col2 varchar(50) null, creator varchar(50) null, updater varchar(50) null );
insert into web_query_test(col1, col2, creator) values ('中国', 'test1', 'testholder1');
insert into web_query_test(col1, col2, creator) values ('test2', 'test2', 'testholder2');
