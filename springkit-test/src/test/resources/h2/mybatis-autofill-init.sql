create table auto_fill_test(id int auto_increment primary key not null, col1 varchar(50) null, col2 varchar(50) null, creator varchar(50) null, updater varchar(50) null );
insert into auto_fill_test(col1, col2, creator) values ('test', 'test', 'testholder');
