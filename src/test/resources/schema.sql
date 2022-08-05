create table address (id bigint not null, line varchar(255), primary key (id))
create table item (id bigint not null, name varchar(255), primary key (id))
create table item_attributes (item_id bigint not null, attributes varchar(255), attributes_key varchar(255) not null, primary key (item_id, attributes_key))
create table p_order (id bigint not null, amount double, purchased_on timestamp, shipping double, person_id bigint, primary key (id))
create table p_order_items (orders_id bigint not null, items_id bigint not null)
create table person (id bigint not null, name varchar(255) not null, address_id bigint, primary key (id))
create table person_nick_names (person_id bigint not null, nick_names varchar(255))
create table tire (make varchar(255) not null, size integer not null, primary key (make, size))
create table vehicle (make varchar(255) not null, model varchar(255) not null, tire_make varchar(255), tire_size integer, primary key (make, model))
create sequence hibernate_sequence start with 1 increment by 1
alter table item_attributes add constraint FKbuhog8m60g9nd9vhjpwr3mkmm foreign key (item_id) references item
alter table p_order add constraint FKlod31a0kav5txffb5dpiuwwuo foreign key (person_id) references person
alter table p_order_items add constraint FKgl0c8arw267781ivxikpyntu6 foreign key (items_id) references item
alter table p_order_items add constraint FK5rp1f5vcbc36p7usu620bc50k foreign key (orders_id) references p_order
alter table person add constraint FKk7rgn6djxsv2j2bv1mvuxd4m9 foreign key (address_id) references address
alter table person_nick_names add constraint FKf4o499j5uto5n9fhjj8qb5fvd foreign key (person_id) references person
alter table vehicle add constraint FKj1cq1b3y6v10wjdot3isrw4io foreign key (tire_make, tire_size) references tire