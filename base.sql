Drop all objects;

Create user If not exists SA SALT 'f6e293b0d7d6a619' HASH '50007d9c9583418d29b0d79144556ca7ce646060aacc1af32ffbe2dbf9a4d55e' ADMIN;

Create table Owners(
    owners_id BigInt not null,
    firstName Varchar(100),
    lastName Varchar(100),
    address Varchar(100),
    city Varchar(100),
    telephone Varchar(100),
    email Varchar(100)
);
Alter Table Owners add constraint ownersPk primary key(owners_id);

Insert into Owners(owners_id, firstName, lastName, address, city, telephone, email) values
(142841788496711, 'Ivan', 'Ivanov', 'Ivanovskaya st.', 'Ivanovo', '+79011111111', 'sample@example.com'),
(142841834950629, 'Petr', 'Petrov', 'Petrovskaya', 'Saint Petersburg', '+79022222222', 'test@test.ru');
Create table pets(
    pets_id BigInt not null,
    owner_id BigInt not null,
    type_id BigInt not null,
    name Varchar(100),
    birthDate Timestamp
);
Alter table Pets add constraint pets_pk primary key(pets_id);

Insert into Pets(pets_id, owner_id, type_id, name, birthDate) Values
(142841880961396, 142841788496711, 142841300122653, 'Druzhok', null),
(142841883974964, 142841834950629, 142841300155478, 'Vasya', Timestamp '2015-04-29 00:00:00.0'),
(143059430815594, 142841788496711, 142850046716850, 'Pick', null);

Create table PetTypes(
    petTypes_id BigInt not null,
    name Varchar(100)
);
Alter table PetTypes add constraint petTypes_pk primary key(petTypes_id);

Insert into PetTypes(petTypes_id, name) values
(142841300122653, 'Dog'),
(142841300155478, 'Cat'),
(142850046716850, 'Mouse');

Create table Visit(
    visit_id BigInt not null,
    pet_id BigInt not null,
    fromDate Timestamp,
    toDate Timestamp,
    description Varchar(100),
    isPaid boolean
);
Alter table Visit add constraint visit_pk primary Key(visit_id);
Insert into Visit(visit_id, pet_id, fromDate, toDate, description, isPaid) Values
(143023673259940, 142841883974964, Timestamp '2015-04-28 18:58:52.604', Timestamp '2015-04-29 00:00:00.0', stringDecode('\u044b\u0430\u0432\u043a\u043f'), null),
(143031982989403, 142841880961396, Timestamp '2015-04-29 18:03:49.898', null, null, null),
(143029901200462, 142841883974964, Timestamp '2015-04-29 12:16:52.008', Timestamp '2015-04-30 00:00:00.0', '1234', null);
Alter table pets add constraint fk_143039780889568 foreign key(type_id) references petTypes(petTypes_id) on delete cascade on update cascade NoCheck;
Alter table pets add constraint fk_137568650945995 foreign key(owner_id) references owners(owners_id) on delete cascade on update cascade NoCheck;
Alter table visit add constraint fk_137568671360207 foreign key(pet_id) references pets(pets_id) on delete cascade on update cascade NoCheck;